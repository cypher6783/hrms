package com.hospital.resource.audit.dto;

import java.time.Instant;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        Instant timestamp,
        UUID userId,
        String actionType,
        String entityType,
        UUID entityId,
        String ipAddress,
        Instant createdAt
) {}
