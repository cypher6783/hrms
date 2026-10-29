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
        Instant updatedAt,
        Integer totalQuantity,
        Integer availableQuantity,
        Integer reservedQuantity,
        Integer allocatedQuantity,
        String status
) {
    public ResourceResponse(
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
    ) {
        this(id, name, category, unitOfMeasure, minimumThreshold, reorderPoint, criticalityLevel,
                defaultSupplierId, createdAt, updatedAt, 0, 0, 0, 0, "AVAILABLE");
    }
}
