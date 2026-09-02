package com.hospital.resource.resource.dto;

import java.util.UUID;

public record ResourceUtilizationResponse(
        UUID resourceId,
        String resourceName,
        String category,
        Integer totalStock,
        Integer allocatedQuantity,
        Integer reservedQuantity,
        Integer availableStock,
        Double utilizationRatePercentage
) {}
