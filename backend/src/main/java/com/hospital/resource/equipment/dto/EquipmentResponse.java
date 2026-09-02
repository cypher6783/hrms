package com.hospital.resource.equipment.dto;

import java.time.Instant;
import java.util.UUID;

public record EquipmentResponse(
        UUID id,
        String name,
        String equipmentType,
        String serialNumber,
        String location,
        String status,
        UUID assignedAdmissionId,
        UUID assignedWardId,
        Instant createdAt,
        Instant updatedAt
) {}
