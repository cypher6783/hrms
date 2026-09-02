package com.hospital.resource.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record SystemConfigResponse(
        UUID id,
        String configKey,
        String configValue,
        String valueType,
        String description,
        String category,
        Boolean requiresRestart,
        Instant updatedAt
) {}
