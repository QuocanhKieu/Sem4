package com.devteria.notification.service;

import com.devteria.notification.configuration.SecurityUtils;
import com.devteria.notification.dto.request.SendEmailRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private final PushNotificationService pushNotificationService;
    private final EmailService emailService;
        private final SecurityUtils securityUtils;
        private static final String REDIS_KEY = "booking:notifications";
        private static final ObjectMapper objectMapper = new ObjectMapper();

        /**
         * Store a scheduled notification in Redis.
         */
        public void scheduleNotification(String bookingId, Instant notifyTime) {
            try {
                // Create JSON object
                String value = objectMapper.writeValueAsString(Map.of(
                        "bookingId", bookingId,
                        "userId", securityUtils.getCurrentUserId(),
                        "email", securityUtils.getCurrentUserEmail()

                ));
                cancelNotification(bookingId);
                // Store in Redis sorted set
                redisTemplate.opsForZSet().add(REDIS_KEY, value, notifyTime.getEpochSecond());
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize data", e);
            }
        }

        /**
         * Remove a scheduled notification from Redis.
         */
        public void cancelNotification(String bookingId) {
            Set<String> notifications = redisTemplate.opsForZSet().rangeByScore(REDIS_KEY, 0, Double.MAX_VALUE);

            if (notifications != null) {
                notifications.stream()
                        .filter(notification -> {
                            try {
                                Map<String, String> data = objectMapper.readValue(notification, new TypeReference<>() {});
                                return bookingId.equals(data.get("bookingId"));
                            } catch (JsonProcessingException e) {
                                return false;
                            }
                        })
                        .forEach(notification -> redisTemplate.opsForZSet().remove(REDIS_KEY, notification)); // Remove all matching notifications
            }
        }



        /**
         * Process and send notifications when the time is due.
         */
//        @Scheduled(fixedRate = 60000) // Runs every 60 seconds
//        public void processNotifications() {
//            Instant now = Instant.now();
//            Set<String> notifications = redisTemplate.opsForZSet().rangeByScore(REDIS_KEY, 0, now.getEpochSecond());
//
//            if (notifications != null) {
//                for (String notification : notifications) {
//                    try {
//                        Map<String, String> data = objectMapper.readValue(notification, new TypeReference<>() {});
//                        String bookingId = data.get("bookingId");
//                        String userId = data.get("userId");
//
//                        // Fetch FCM token and send notification
////                        String fcmToken = pushNotificationService.getFcmToken(userId);
////                        if (fcmToken != null) {
////                            sendNotification(userId, bookingId);
////                        }
//                        sendNotification(userId, bookingId);
//                        // Remove processed notification
//                        redisTemplate.opsForZSet().remove(REDIS_KEY, notification);
//                    } catch (JsonProcessingException | FirebaseMessagingException e) {
//                        throw new RuntimeException("Failed to parse data", e);
//                    }
//                }
//            }
//        }

@Scheduled(fixedRate = 5000) // Runs every 5 seconds
public void checkAndSendNotifications() {
    System.out.println("🕒 Current system UTC time: " + Instant.now()+"5seconds");
    Instant now = Instant.now();
    long currentEpoch = now.getEpochSecond();
    long notifyTime = currentEpoch + 120 ;

    // ✅ Fetch all notifications that are due now or earlier
    Set<String> bookings = redisTemplate.opsForZSet().rangeByScore(REDIS_KEY, 0, notifyTime);

    for (String bookingJson : bookings) {
        try {
            // Deserialize JSON
            Map<String, String> bookingData = objectMapper.readValue(bookingJson, new TypeReference<Map<String, String>>() {});
            String bookingId = bookingData.get("bookingId");
            String userId = bookingData.get("userId");
            String userEmail = bookingData.get("email");

            // Send notification
            sendNotification(userId, bookingId, userEmail);

            // ✅ Remove from Redis after sending notification
            redisTemplate.opsForZSet().remove(REDIS_KEY, bookingJson);

        } catch (JsonProcessingException | FirebaseMessagingException e) {
            System.err.println("❌ Failed to deserialize booking data: " + e.getMessage());
        }
    }
}


        private void sendNotification(String userID, String bookingId, String userEmail) throws FirebaseMessagingException {
//            pushNotificationService.sendPushNotification(userID,"Hurry up, Booking about to happen!",  "Your Booking with the id:" + bookingId +"is about to happen, quickly get into the slot to begin your session!");
            emailService.sendBookingAboutToHappenEmail(userEmail, bookingId);
            System.out.println("📢 Sending push notification for Booking ID: " + bookingId + "userId: " + userID);


        }

    public void testRedis() {
        redisTemplate.opsForValue().set("test-key", "Hello, Redis From Notification Service!");
        String value = redisTemplate.opsForValue().get("test-key");
        System.out.println("🔴 Redis Value: " + value);
        System.out.println("🕒 Current system UTC time: " + Instant.now());

    }
    public  Set<ZSetOperations.TypedTuple<String>>  getAllScheduledNotifications() {
        return redisTemplate.opsForZSet().rangeWithScores(REDIS_KEY, 0, -1);
    }

}