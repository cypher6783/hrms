package com.hospital.resource.resource.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record InventoryTransactionRequest(
        @NotNull UUID resourceInventoryId,
        @NotNull String transactionType,
        @NotNull Integer quantity,
        UUID admissionId,
        String referenceDocument,
        String notes
) {}
