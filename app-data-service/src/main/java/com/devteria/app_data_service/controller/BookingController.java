package com.devteria.app_data_service.controller;

import com.devteria.app_data_service.dto.ApiResponse;
import com.devteria.app_data_service.dto.request.BookingRequest;
import com.devteria.app_data_service.dto.request.ConfirmedBookingRequest;
import com.devteria.app_data_service.entity.Booking;
import com.devteria.app_data_service.enums.BookingStatusEnums;
import com.devteria.app_data_service.enums.ServiceProvidedEnums;
import com.devteria.app_data_service.repository.BookingRepository;
import com.devteria.app_data_service.service.BookingService;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
//    private final BookingRepository bookingRepository;


    // Create a booking
    @PostMapping("/create")
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest request) {

        Booking createdBooking = bookingService.createBooking(request);
        return ResponseEntity.ok(createdBooking);
    }

    @PutMapping("/internal/confirm")
    public ApiResponse<String> confirmBooking(@RequestBody ConfirmedBookingRequest confirmedBookingRequest) throws IOException, WriterException {
            Booking confirmedBooking = bookingService.confirmBooking(confirmedBookingRequest);
            return ApiResponse.<String>builder()
                    .message("Booking confirmed successfully")
                    .result("Booking confirmed successfully with ID:" + confirmedBooking.getId() )
                    .build();
    }

    @GetMapping("/invalid-slots")
    public ResponseEntity<Set<String>> getInvalidSlots(
            @RequestParam String locationId,
            @RequestParam ServiceProvidedEnums type,
            @RequestParam String startDateTime, // Change from Instant to String
            @RequestParam String endDateTime) { // Change from Instant to String

        // Convert manually from offset date-time to UTC Instant
        Instant startInstant = OffsetDateTime.parse(startDateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();
        Instant endInstant = OffsetDateTime.parse(endDateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();

        Set<String> invalidSlots = bookingService.getInvalidSlots(locationId, type, startInstant, endInstant);
        return ResponseEntity.ok(invalidSlots);
    }

    @GetMapping("/valid-slots")
    public ResponseEntity<Set<String>> getValidSlots(
            @RequestParam String locationId,
            @RequestParam ServiceProvidedEnums type,
            @RequestParam String startDateTime, // Change from Instant to String
            @RequestParam String endDateTime) { // Change from Instant to String

        // Convert manually from offset date-time to UTC Instant
        Instant startInstant = OffsetDateTime.parse(startDateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();
        Instant endInstant = OffsetDateTime.parse(endDateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();

        Set<String> validSlots = bookingService.getValidSlots(locationId, type, startInstant, endInstant);
        return ResponseEntity.ok(validSlots);
    }

    // Endpoint to find a booking by ID
    @GetMapping("/find/{bookingId}")
    public ResponseEntity<Booking> getBookingById(@PathVariable String bookingId) {
        Booking booking = bookingService.findBookingById(bookingId);
        return ResponseEntity.ok(booking);
    }

    // Endpoint to cancel a booking
    @PostMapping("/cancel/{bookingId}")
    public ResponseEntity<Booking> cancelBooking(@PathVariable String bookingId) {
        Booking cancelledBooking = bookingService.cancelAndReturnBooking(bookingId);
        return ResponseEntity.ok(cancelledBooking);
    }

//    @GetMapping("/valid-slots")
//    public ResponseEntity<Set<String>> getValidSlots(
//            @RequestParam String locationId,
//            @RequestParam ServiceProvidedEnums type,
//            @RequestParam Instant startDateTime,
//            @RequestParam Instant endDateTime) {
//
//        Set<String> invalidSlots = bookingService.getValidSlots(locationId, type, startDateTime, endDateTime);
//        return ResponseEntity.ok(invalidSlots);
//    }
}