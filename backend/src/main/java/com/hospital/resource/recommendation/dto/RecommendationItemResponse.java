package com.hospital.resource.recommendation.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record RecommendationItemResponse(
        UUID id,
        String itemType,
        String recommendedEntityType,
        UUID recommendedEntityId,
        Integer rank,
        BigDecimal confidenceScore,
        String rationale,
        String status
) {}
