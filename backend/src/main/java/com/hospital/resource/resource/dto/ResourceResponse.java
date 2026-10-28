package com.hospital.resource.resource.dto;

import java.time.Instant;
import java.util.UUID;

public record ResourceResponse(
        UUID id,
        String name,
        String category,
        String unitOfMeasure,
        Integer minimumThreshold,
        Integer reorderPoint,
        String criticalityLevel,
        UUID defaultSupplierId,
        Instant createdAt,
        Instant updatedAt
) {}
