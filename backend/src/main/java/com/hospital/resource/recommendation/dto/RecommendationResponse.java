package com.hospital.resource.recommendation.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RecommendationResponse(
        UUID id,
        UUID admissionId,
        String batchType,
        String status,
        Instant generatedAt,
        Instant expiresAt,
        List<RecommendationItemResponse> items,
        Instant createdAt
) {}
