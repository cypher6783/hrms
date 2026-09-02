package com.hospital.resource.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UUID userId) {
                return userId;
            }
            if (principal instanceof String str) {
                try {
                    return UUID.fromString(str);
                } catch (IllegalArgumentException e) {
                    return UUID.nameUUIDFromBytes(str.getBytes());
                }
            }
            if (authentication.getName() != null) {
                try {
                    return UUID.fromString(authentication.getName());
                } catch (IllegalArgumentException e) {
                    return UUID.nameUUIDFromBytes(authentication.getName().getBytes());
                }
            }
        }
        throw new IllegalStateException("No authenticated user found");
    }

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.getName() != null) {
                return authentication.getName();
            }
            if (authentication.getCredentials() instanceof String username) {
                return username;
            }
        }
        throw new IllegalStateException("No authenticated user found");
    }
}
