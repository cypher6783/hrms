package com.hospital.resource.resource.dto;

import java.time.Instant;
import java.util.UUID;

public record SupplierResponse(
        UUID id,
        String name,
        String contactPerson,
        String phoneNumber,
        String email,
        String address,
        Integer leadTimeDays,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {}
