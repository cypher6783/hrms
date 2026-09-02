package com.hospital.resource.bedcleaning.dto;

import java.time.Instant;
import java.util.UUID;

public record CleaningTaskSummaryResponse(
        UUID id,
        UUID bedId,
        UUID admissionId,
        String status,
        UUID assignedTo,
        Instant createdAt
) {}
