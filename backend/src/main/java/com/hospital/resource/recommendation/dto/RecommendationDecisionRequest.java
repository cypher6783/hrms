package com.hospital.resource.recommendation.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record RecommendationDecisionRequest(
        @NotBlank String decisionType,
        UUID overriddenEntityId,
        String overrideJustification
) {}
