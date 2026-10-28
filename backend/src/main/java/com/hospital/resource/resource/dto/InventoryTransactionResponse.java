package com.hospital.resource.resource.dto;

import java.time.Instant;
import java.util.UUID;

public record InventoryTransactionResponse(
        UUID id,
        UUID resourceInventoryId,
        String transactionType,
        Integer quantity,
        UUID admissionId,
        String referenceDocument,
        String notes,
        UUID performedBy,
        Instant transactionTimestamp,
        Instant createdAt
) {}
