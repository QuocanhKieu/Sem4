package com.devteria.notification.controller;

import com.devteria.notification.configuration.SecurityUtils;
import com.devteria.notification.dto.request.ScheduledNotificationRequest;
import com.devteria.notification.service.PushNotificationService;
import com.devteria.notification.service.RedisService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/push-notification")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PushNotificationController {
    private final RedisService redisService;
    private final PushNotificationService pushNotificationService;
    private final SecurityUtils securityUtils;

    /**
     * Schedules a notification for a booking.
     */
    @PostMapping("/internal/schedule")
    public ResponseEntity<String> scheduleNotification(@RequestBody ScheduledNotificationRequest scheduledNotificationRequest ) {
        String bookingId = scheduledNotificationRequest.getBookingId();

        // Get startDatetime from request
        Instant startDatetime = scheduledNotificationRequest.getStartDateTime();

        // Store notification in Redis
        redisService.scheduleNotification(bookingId, startDatetime);

        return ResponseEntity.ok("✅ Notification scheduled for Booking ID: " + bookingId + " at " + startDatetime);
    }

    /**
     * Cancels a scheduled notification for a booking.
     */
    @DeleteMapping("/internal/cancel/{bookingId}")
    public ResponseEntity<String> cancelNotification(@PathVariable String bookingId) {
        redisService.cancelNotification(bookingId);
        return ResponseEntity.ok("❌ Notification canceled for Booking ID: " + bookingId);
    }

    @GetMapping("/fcm")
    public ResponseEntity<String> getFcmToken() {
        String token = pushNotificationService.getFcmToken(securityUtils.getCurrentUserId());
        if (token == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(token);
    }


    @PostMapping("/internal/send")
    public String sendNotification(@RequestParam String title, @RequestParam String message) throws FirebaseMessagingException {
        return pushNotificationService.sendPushNotification(securityUtils.getCurrentUserId(), title, message);
    }
        @GetMapping("/internal/list")
    public ResponseEntity<Set<ZSetOperations.TypedTuple<String>>> listAllScheduledNotifications() {
            Set<ZSetOperations.TypedTuple<String>> notifications = redisService.getAllScheduledNotifications();
        return ResponseEntity.ok(notifications);
    }
}
