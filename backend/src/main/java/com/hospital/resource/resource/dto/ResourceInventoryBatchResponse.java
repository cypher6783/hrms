package com.hospital.resource.resource.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ResourceInventoryBatchResponse(
        UUID id,
        UUID resourceId,
        String resourceName,
        String location,
        Integer currentStock,
        LocalDate expirationDate,
        String batchNumber
) {}
