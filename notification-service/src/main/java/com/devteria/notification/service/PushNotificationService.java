package com.devteria.notification.service;

import com.devteria.notification.configuration.SecurityUtils;
import com.google.firebase.messaging.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PushNotificationService {
    private static final String REDIS_FCM_PREFIX = "fcm:user:";

    private final StringRedisTemplate redisTemplate;

    public String sendPushNotification(String userId, String title, String message) throws FirebaseMessagingException {
        if (userId == null) {
            log.warn("User is not authenticated. Cannot send push notification.");
            return null;
        }

        String deviceToken = getFcmToken(userId);
        if (deviceToken == null) {
            log.warn("No FCM token found for user: {}", userId);
            return null;
        }

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(message)
                .build();

        Message msg = Message.builder()
                .setToken(deviceToken)
                .setNotification(notification)
                .build();

        log.info("Sending push notification to user: {}", userId);
        return FirebaseMessaging.getInstance().send(msg);
    }

    public String getFcmToken(String userId) {
        return redisTemplate.opsForValue().get(REDIS_FCM_PREFIX + userId);
    }
}

