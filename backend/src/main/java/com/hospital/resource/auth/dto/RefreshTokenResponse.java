package com.hospital.resource.auth.dto;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn
) {}
