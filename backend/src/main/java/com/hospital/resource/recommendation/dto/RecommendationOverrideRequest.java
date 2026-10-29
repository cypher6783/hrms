package com.hospital.resource.recommendation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RecommendationOverrideRequest(
        @NotNull(message = "Overridden Bed ID is required")
        UUID overriddenBedId,
        UUID overriddenEquipmentId,
        @NotBlank(message = "Clinical override justification is mandatory")
        String overrideJustification
) {}
