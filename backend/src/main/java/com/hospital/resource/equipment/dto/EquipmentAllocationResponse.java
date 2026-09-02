package com.hospital.resource.equipment.dto;

import java.time.Instant;
import java.util.UUID;

public record EquipmentAllocationResponse(
        UUID id,
        UUID equipmentId,
        UUID admissionId,
        Instant allocatedAt,
        Instant releasedAt,
        UUID allocatedBy
) {}
