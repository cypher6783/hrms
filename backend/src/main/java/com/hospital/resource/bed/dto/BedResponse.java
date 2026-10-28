package com.hospital.resource.bed.dto;

import java.time.Instant;
import java.util.UUID;

public record BedResponse(
        UUID id,
        String bedNumber,
        UUID wardId,
        String bedType,
        Boolean isIsolationCapable,
        String status,
        UUID currentAdmissionId,
        Instant lastMaintenanceAt,
        Instant createdAt,
        Instant updatedAt
) {}
