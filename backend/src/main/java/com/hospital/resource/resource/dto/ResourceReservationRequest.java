package com.hospital.resource.resource.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ResourceReservationRequest(
        @NotNull(message = "Resource ID is required")
        UUID resourceId,

        UUID admissionId,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        Integer expirationMinutes
) {}
