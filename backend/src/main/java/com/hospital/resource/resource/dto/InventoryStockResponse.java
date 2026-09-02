package com.hospital.resource.resource.dto;

import java.util.UUID;

public record InventoryStockResponse(
        UUID resourceId,
        String resourceName,
        Integer totalStock,
        Integer minimumThreshold,
        boolean isBelowThreshold
) {}
