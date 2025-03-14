package com.devteria.identity.configuration;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public String getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("User not authenticated");
        }
        return jwt.getClaim("userId"); // Extract userId from JWT claims
    }

    public String getCurrentEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("User not authenticated");
        }
        return jwt.getClaim("email"); // Extract userId from JWT claims
    }
}
