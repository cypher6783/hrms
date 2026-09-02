package com.hospital.resource.ward.dto;

import java.time.Instant;
import java.util.UUID;

public record WardResponse(
        UUID id,
        String name,
        String wardType,
        Integer maxBedCapacity,
        String isolationLevel,
        String equipmentZone,
        String status,
        Instant createdAt,
        Instant updatedAt
) {}
