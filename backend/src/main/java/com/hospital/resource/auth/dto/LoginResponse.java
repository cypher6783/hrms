package com.hospital.resource.auth.dto;

import java.util.UUID;

public record LoginResponse(
        UUID userId,
        String username,
        String fullName,
        String role,
        String accessToken,
        String refreshToken,
        long expiresIn
) {}
