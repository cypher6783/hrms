package com.hospital.resource.recommendation.dto;

import java.time.Instant;
import java.util.UUID;

public record RecommendationDecisionResponse(
        UUID id,
        UUID recommendationItemId,
        String decisionType,
        UUID decidedBy,
        Instant decidedAt
) {}
