package com.hospital.resource.equipment.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record EquipmentRequest(
        @NotBlank String name,
        @NotBlank String equipmentType,
        @NotBlank String serialNumber,
        String location,
        UUID assignedWardId
) {}
