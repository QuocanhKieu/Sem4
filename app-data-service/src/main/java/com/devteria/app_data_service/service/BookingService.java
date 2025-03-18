package com.devteria.app_data_service.service;
import com.devteria.app_data_service.configuration.SecurityUtils;
import com.devteria.app_data_service.dto.SlotBasicInfo;
import com.devteria.app_data_service.dto.request.BookingConfirmedEmailRequest;
import com.devteria.app_data_service.dto.request.BookingRequest;
import com.devteria.app_data_service.dto.request.ConfirmedBookingRequest;
import com.devteria.app_data_service.dto.request.ScheduledNotificationRequest;
import com.devteria.app_data_service.entity.ChargingSlot;
import com.devteria.app_data_service.entity.Location;
import com.devteria.app_data_service.entity.ParkingSlot;
import com.devteria.app_data_service.enums.ServiceProvidedEnums;
import com.devteria.app_data_service.exception.AppException;
import com.devteria.app_data_service.exception.ErrorCode;
import com.devteria.app_data_service.mapper.BookingMapper;
import com.devteria.app_data_service.repository.ChargingSlotRepository;
import com.devteria.app_data_service.repository.LocationRepository;
import com.devteria.app_data_service.repository.ParkingSlotRepository;
import com.devteria.app_data_service.repository.httpclient.NotificationClient;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.time.Duration;

import com.devteria.app_data_service.entity.Booking;
import com.devteria.app_data_service.enums.BookingStatusEnums;
import com.devteria.app_data_service.repository.BookingRepository;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;


import javax.imageio.ImageIO;
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final SecurityUtils securityUtils;
    private final CostCalculatorService costCalculatorService;
    private final NotificationClient notificationClient;
    private final LocationRepository locationRepository;
    private final ChargingSlotRepository chargingSlotRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final MongoTemplate mongoTemplate;


    // Create a new booking // next button
    public Booking createBooking(BookingRequest bookingRequest) {
        Duration duration = Duration.between(bookingRequest.getStartDateTime(), bookingRequest.getEndDateTime());

// Convert minutes to hours with precise division
        BigDecimal hours = BigDecimal.valueOf(duration.toMinutes())
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        if (hours.compareTo(BigDecimal.valueOf(10)) > 0) {
            throw new IllegalArgumentException("Booking cannot exceed 10 hours");
        }
        Set<String> invalidSlots = getInvalidSlots(
                bookingRequest.getLocationId(),
                bookingRequest.getType(),
                bookingRequest.getStartDateTime(),
                bookingRequest.getEndDateTime()
        );
        Instant now = Instant.now();

        // Validate start and end times
        if (bookingRequest.getStartDateTime().isBefore(now)) {
            throw new IllegalArgumentException("Start time cannot be in the past.");
        }
        if (bookingRequest.getEndDateTime().isBefore(now)) {
            throw new IllegalArgumentException("End time cannot be in the past.");
        }
        if (bookingRequest.getEndDateTime().isBefore(bookingRequest.getStartDateTime())) {
            throw new IllegalArgumentException("End time cannot be before start time.");
        }
// Extract slot IDs from SlotBasicInfos
        Set<String> requestedSlotIds = bookingRequest.getSlotBasicInfos().stream()
                .map(SlotBasicInfo::getSlotId)  // Extract slotId
                .collect(Collectors.toSet());

        // Check if any selected slot is already booked
        if (requestedSlotIds.stream().anyMatch(invalidSlots::contains)) {
            throw new IllegalStateException("One or more selected slots are already booked.");
        }

        // Save the booking since slots are still available
        Booking booking = bookingMapper.toBooking(bookingRequest);
        booking.setId(UUID.randomUUID().toString()); // Manually setting ID
        booking.setCreatedAt(Instant.now()); // Manually setting createdAt
        booking.setUserId(securityUtils.getCurrentUserId()); // Set user ID from authentication
        booking.setStatus(BookingStatusEnums.PENDING);
        booking.setSlotBasicInfos(bookingRequest.getSlotBasicInfos());
        log.info("current user Id : {}", securityUtils.getCurrentUserId());
        booking.setDuration(hours);

        // Calculate total price based on slot count
        int numberOfSlots = requestedSlotIds.size();
        BigDecimal price = BigDecimal.ZERO; // Initialize properly

        if (bookingRequest.getType() == ServiceProvidedEnums.CHARGING) {
            price = costCalculatorService.calculateChargingCost(hours, bookingRequest.getWattHours())
                    .multiply(BigDecimal.valueOf(numberOfSlots));
        } else if (bookingRequest.getType() == ServiceProvidedEnums.PARKING) {
            price = costCalculatorService.calculateParkingCost(hours)
                    .multiply(BigDecimal.valueOf(numberOfSlots));
        }

        booking.setPrice(price);
        log.info("send push notification thank you, tell the user to complete the payment 2 minutes");

        // send push notification thank you, tell the user to complete the payment 2 minutes
        return bookingRepository.save(booking);

    }

    public Booking confirmBooking(ConfirmedBookingRequest confirmedBookingRequest) throws WriterException, IOException {
        Booking booking = bookingRepository.findById(confirmedBookingRequest.getBookingId()).orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() == BookingStatusEnums.CONFIRMED) {
            throw new RuntimeException("Booking already confirmed");
        }

        if (booking.getStatus() == BookingStatusEnums.CANCELLED) {
            throw new RuntimeException("Booking already cancelled");
        }


        String gateServiceUrl = "https://gate-control.example.com/open?bookingId={bookingId}&slotId={eachSlotId}&slotName={eachSlotNumber}";

// Loop through the slot list and update `slotBookedQr`
        booking.getSlotBasicInfos().forEach(slotBasicInfo -> {
            try {
                // Replace placeholders dynamically
                String qrUrl = gateServiceUrl
                        .replace("{bookingId}", booking.getId())
                        .replace("{eachSlotId}", slotBasicInfo.getSlotId().trim())
                        .replace("{eachSlotNumber}", slotBasicInfo.getSlotNumber().trim());

                // Generate QR Code and set it in the object
                slotBasicInfo.setSlotBookedQr(generateQRCodeBase64(qrUrl));
            } catch (WriterException | IOException e) {
                throw new RuntimeException("Error generating QR code", e);
            }
        });

        // Update booking status and add QR codes
        booking.setStatus(BookingStatusEnums.CONFIRMED);
        booking.setFinalPrice(confirmedBookingRequest.getFinalPrice());
        booking.setVoucherId(confirmedBookingRequest.getVoucherId());
        booking.setVoucherAmount(confirmedBookingRequest.getVoucherAmount());

        Location location = locationRepository.findById(booking.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found with ID: " + booking.getLocationId()));


        var bookingConfirmedEmailRequest = BookingConfirmedEmailRequest.builder()
                .duration(booking.getDuration())
                .finalPrice(booking.getFinalPrice())
                .price(booking.getFinalPrice())
                .voucherAmount(booking.getVoucherAmount())
                .wattHours(booking.getWattHours())
                .type(booking.getType())
                .slotBasicInfos(booking.getSlotBasicInfos())
                .bookingId(booking.getId())
                .locationName(location.getName())
                .locationAddress(location.getAddress())
                .userId(securityUtils.getCurrentUserId())
                .startDateTime(booking.getStartDateTime())
                .endDateTime(booking.getEndDateTime())

                .build();
        try {
            notificationClient.sendEmailBookingConfirmed(bookingConfirmedEmailRequest);
            notificationClient.scheduleNotification(ScheduledNotificationRequest.builder().bookingId(booking.getId()).startDateTime(booking.getStartDateTime()).build());
        } catch (FeignException e) { // Handle Feign client errors
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }


        return bookingRepository.save(booking);
    }
    public String generateQRCodeBase64(String data) throws WriterException, IOException {
        int width = 300;
        int height = 300;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ImageIO.write(image, "png", baos);

        return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
    }

//    public Set<String> getInvalidSlots(String locationId, ServiceProvidedEnums type, Instant startDateTime, Instant endDateTime) {
//        Instant now = Instant.now();
//
//        // Validate start and end times
//        if (startDateTime.isBefore(now)) {
//            throw new IllegalArgumentException("Start time cannot be in the past.");
//        }
//        if (endDateTime.isBefore(now)) {
//            throw new IllegalArgumentException("End time cannot be in the past.");
//        }
//        if (endDateTime.isBefore(startDateTime)) {
//            throw new IllegalArgumentException("End time cannot be before start time.");
//        }
//        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(locationId, startDateTime, endDateTime, type.name());
//
//        // Extract all slot IDs from overlapping bookings
////        return overlappingBookings.stream()
////                .flatMap(booking -> booking.getSlotIds().stream())
////                .collect(Collectors.toSet());
//
//        return overlappingBookings.stream()
//                .flatMap(booking -> booking.getSlotBasicInfos().stream()
//                        .map(SlotBasicInfo::getSlotId)) // Extract slotId from SlotBasicInfo
//                .collect(Collectors.toSet());
//    }
public Set<String> getInvalidSlots(String locationId, ServiceProvidedEnums type, Instant startDateTime, Instant endDateTime) {
    Instant now = Instant.now();

    // Validate start and end times
    if (startDateTime.isBefore(now)) {
        throw new IllegalArgumentException("Start time cannot be in the past.");
    }
    if (endDateTime.isBefore(now)) {
        throw new IllegalArgumentException("End time cannot be in the past.");
    }
    if (endDateTime.isBefore(startDateTime)) {
        throw new IllegalArgumentException("End time cannot be before start time.");
    }

    // Fetch overlapping bookings that are NOT CANCELLED
    List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(locationId, startDateTime, endDateTime, type.name())
            .stream()
            .filter(booking -> booking.getStatus() != BookingStatusEnums.CANCELLED) // Exclude cancelled bookings
            .toList();

    // Extract slot IDs from valid bookings
    return overlappingBookings.stream()
            .flatMap(booking -> booking.getSlotBasicInfos().stream().map(SlotBasicInfo::getSlotId))
            .collect(Collectors.toSet());
}


    public Set<String> getValidSlots(String locationId, ServiceProvidedEnums type, Instant startDateTime, Instant endDateTime) {
        Set<String> invalidSlots = getInvalidSlots(locationId, type, startDateTime, endDateTime);

        List<String> allSlotIds;

        if (type == ServiceProvidedEnums.CHARGING) {
            // Fetch all charging slots for the location
            allSlotIds = chargingSlotRepository.findByLocationId(locationId)
                    .stream()
                    .map(ChargingSlot::getId) // Extract IDs
                    .collect(Collectors.toList());
        } else if (type == ServiceProvidedEnums.PARKING) {
            // Fetch all parking slots for the location
            allSlotIds = parkingSlotRepository.findByLocationId(locationId)
                    .stream()
                    .map(ParkingSlot::getId) // Extract IDs
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Unsupported service type: " + type);
        }

        // Filter out invalid slots
        return allSlotIds.stream()
                .filter(slotId -> !invalidSlots.contains(slotId))
                .collect(Collectors.toSet());
    }

//    @Scheduled(fixedRate = 30000) // Run every 30 seconds
//    public void cancelExpiredBookings() {
//        Instant expirationTime = Instant.now().minusSeconds(120); // Bookings older than 120s
//        List<Booking> expiredBookings = bookingRepository.findByStatusAndCreatedAtBefore(BookingStatusEnums.PENDING, expirationTime);
//
//        for (Booking booking : expiredBookings) {
//            booking.setStatus(BookingStatusEnums.CANCELLED);
//            // can send notification here also
//        }
//        bookingRepository.saveAll(expiredBookings);
//    }

//    @Scheduled(fixedRate = 30000) // Run every 30 seconds
//    public void cancelExpiredBookings() {
//        Instant expirationTime = Instant.now().minusSeconds(120); // Expiration threshold
//
//        // Update all expired pending bookings directly in the database
//        bookingRepository.updateStatusForExpiredBookings(BookingStatusEnums.PENDING, expirationTime, BookingStatusEnums.CANCELLED);
//    }


//    public void cancelExpiredBookings() {
//        Instant expirationTime = Instant.now().minusSeconds(120); // Expiration threshold
//
//        Query query = new Query();
//        query.addCriteria(Criteria.where("status").is(BookingStatusEnums.PENDING)
//                .and("createdAt").lt(expirationTime));
//
//        Update update = new Update();
//        update.set("status", BookingStatusEnums.CANCELLED);
//
//        mongoTemplate.updateMulti(query, update, Booking.class); // Update all matching records
//    }
//
//
//    @Scheduled(fixedRate = 30000) // Runs every 30 seconds
//    public void scheduledTask() {
//        cancelExpiredBookings();
//    }

    @Scheduled(fixedRate = 30000) // Run every 30 seconds
    public void cancelExpiredBookings() {
        Instant expirationTime = Instant.now().minusSeconds(120); // Bookings older than 120s

        // Find expired pending bookings
        Query query = new Query();
        query.addCriteria(Criteria.where("status").is(BookingStatusEnums.PENDING)
                .and("createdAt").lt(expirationTime));

        List<Booking> expiredBookings = mongoTemplate.find(query, Booking.class);

        if (!expiredBookings.isEmpty()) {
            // Collect user IDs to send notifications
            List<String> userIds = expiredBookings.stream()
                    .map(Booking::getUserId)
                    .toList();

            // Update the bookings to CANCELLED
            Update update = new Update();
            update.set("status", BookingStatusEnums.CANCELLED);
            mongoTemplate.updateMulti(query, update, Booking.class);

            // Send push notifications
//            sendPushNotifications(userIds, "Your booking has been cancelled due to inactivity.");
            log.info("current users Id booking canceled : {}", userIds);

        }
        System.out.println("Current Instant Time: " + Instant.now());

    }


    public Booking cancelAndReturnBooking(String bookingId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(bookingId));

        Booking booking = mongoTemplate.findOne(query, Booking.class);

        if (booking == null) {
            throw new RuntimeException("Booking not found");
        }

        // Check if booking is CONFIRMED and in session (already started)
        if (booking.getStatus() == BookingStatusEnums.CONFIRMED) {
            Instant now = Instant.now();

            if (booking.getStartDateTime().isBefore(now)) {
                // Booking has already started → Apply partial refund or deny cancellation
                log.info("Booking has already started → Apply partial refund or deny cancellation");

//                handlePartialRefund(booking);
            } else {
                // Booking hasn't started → Apply full refund
                log.info("Booking hasn't started → Apply full refund");

//                handleFullRefund(booking);
            }
        }

        // Update status to CANCELLED
        Update update = new Update();
        update.set("status", BookingStatusEnums.CANCELLED);

        return mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), Booking.class);
    }
    public Booking findBookingById(String bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
    }


    public List<Booking> getBookingsByUserId(String userId, ServiceProvidedEnums type) {
        if (type == null) {
            return bookingRepository.findByUserId(userId); // Return all bookings if no type is provided
        }
        return bookingRepository.findByUserIdAndType(userId, type);
    }

}