package com.devteria.identity.controller;

import com.devteria.identity.configuration.SecurityUtils;
import com.devteria.identity.service.FcmTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;
    private final SecurityUtils securityUtils;

    /**
     * Endpoint to update or store the FCM token.
     */
    @PostMapping("")
    public ResponseEntity<String> updateFcmToken(@RequestParam String token) {
        fcmTokenService.updateFcmToken(securityUtils.getCurrentUserId(), token);
        return ResponseEntity.ok("✅ FCM token updated for user: " + securityUtils.getCurrentUserId());
    }

    /**
     * Endpoint to remove the FCM token when the user logs out.
     */
//    @DeleteMapping("/remove")
//    public ResponseEntity<String> removeFcmToken(@RequestParam Long userId) {
//        fcmTokenService.removeFcmToken(securityUtils.getCurrentUserId());
//        return ResponseEntity.ok("❌ FCM token removed for user: " + userId);
//    }

    /**
     * Endpoint to get the FCM token for a user.
     */
    @GetMapping("")
    public ResponseEntity<String> getFcmToken() {
        String token = fcmTokenService.getFcmToken(securityUtils.getCurrentUserId());
        if (token == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(token);
    }
}
