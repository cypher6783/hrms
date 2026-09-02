package com.hospital.resource.resource.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record ResourceRequest(
        @NotBlank String name,
        @NotBlank String category,
        @NotBlank String unitOfMeasure,
        Integer minimumThreshold,
        Integer reorderPoint,
        String criticalityLevel,
        UUID defaultSupplierId
) {}
