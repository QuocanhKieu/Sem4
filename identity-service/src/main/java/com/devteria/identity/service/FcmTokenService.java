package com.devteria.identity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FcmTokenService {

    private final StringRedisTemplate redisTemplate;
    private static final String REDIS_FCM_PREFIX = "fcm:user:";

    /**
     * Store or update the FCM token for a user.
     */
    public void updateFcmToken(String userId, String token) {
        redisTemplate.opsForValue().set(REDIS_FCM_PREFIX + userId, token);
    }

    /**
     * Remove the FCM token when the user logs out.
     */
    public void removeFcmToken(String userId) {
        redisTemplate.delete(REDIS_FCM_PREFIX + userId);
    }

    /**
     * Retrieve the FCM token for a user.
     */
    public String getFcmToken(String userId) {
        return redisTemplate.opsForValue().get(REDIS_FCM_PREFIX + userId);
    }

    public void testRedis() {
        redisTemplate.opsForValue().set("test-key", "Hello, Redis From Identity Service!");
        String value = redisTemplate.opsForValue().get("test-key");
        System.out.println("🔴 Redis Value: " + value);
    }
}
