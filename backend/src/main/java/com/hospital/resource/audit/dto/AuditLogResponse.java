package com.hospital.resource.audit.dto;

import java.time.Instant;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        Instant timestamp,
        UUID userId,
        String username,
        String action,
        String actionType,
        String entityType,
        UUID entityId,
        String previousState,
        String newState,
        String justification,
        String ipAddress,
        Instant createdAt
) {
    public AuditLogResponse(
            UUID id,
            Instant timestamp,
            UUID userId,
            String actionType,
            String entityType,
            UUID entityId,
            String ipAddress,
            Instant createdAt
    ) {
        this(id, timestamp, userId, "Clinical User", actionType, actionType, entityType, entityId, null, null, null, ipAddress, createdAt);
    }
}
