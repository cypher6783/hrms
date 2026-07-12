package com.hospital.resource.notification.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String title,
        String message,
        String notificationType,
        String sourceModule,
        Boolean isRead,
        Instant readAt,
        Instant createdAt
) {}
