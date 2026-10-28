package com.hospital.resource.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record UserManagementResponse(
        UUID id,
        String username,
        String email,
        String fullName,
        String role,
        String status,
        Instant lastLoginAt,
        Instant createdAt
) {}
