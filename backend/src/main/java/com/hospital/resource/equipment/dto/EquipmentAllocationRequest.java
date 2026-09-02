package com.hospital.resource.equipment.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EquipmentAllocationRequest(
        @NotNull(message = "Equipment ID is required")
        UUID equipmentId,

        @NotNull(message = "Admission ID is required")
        UUID admissionId
) {}
