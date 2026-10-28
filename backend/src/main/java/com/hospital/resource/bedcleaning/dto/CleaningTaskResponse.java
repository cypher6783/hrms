package com.hospital.resource.bedcleaning.dto;

import java.time.Instant;
import java.util.UUID;

public record CleaningTaskResponse(
        UUID id,
        UUID bedId,
        UUID admissionId,
        String status,
        UUID assignedTo,
        Instant assignedAt,
        Instant startedAt,
        Instant completedAt,
        UUID verifiedBy,
        Instant verifiedAt,
        String cleaningNotes,
        Instant createdAt
) {}
