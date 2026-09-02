package com.hospital.resource.resource.dto;

import java.time.Instant;
import java.util.UUID;

public record ResourceReservationResponse(
        UUID id,
        UUID resourceId,
        UUID admissionId,
        Integer quantity,
        String status,
        Instant reservedAt,
        Instant expiresAt,
        UUID reservedBy
) {}
