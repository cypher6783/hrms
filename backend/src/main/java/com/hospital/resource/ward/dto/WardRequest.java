package com.hospital.resource.ward.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record WardRequest(
        @NotBlank String name,
        @NotBlank String wardType,
        @Min(1) Integer maxBedCapacity,
        String isolationLevel,
        String equipmentZone
) {}
