package com.hospital.resource.resource.dto;

import java.time.Instant;
import java.util.UUID;

public record ResourceAllocationResponse(
        UUID id,
        UUID resourceId,
        UUID admissionId,
        Integer quantity,
        Instant allocatedAt,
        Instant releasedAt,
        UUID allocatedBy
) {}
