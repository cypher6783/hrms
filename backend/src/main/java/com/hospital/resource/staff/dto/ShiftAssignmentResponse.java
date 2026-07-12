package com.hospital.resource.staff.dto;

import java.time.Instant;
import java.util.UUID;

public record ShiftAssignmentResponse(
        UUID id,
        UUID staffId,
        UUID shiftId,
        String status,
        UUID assignedBy,
        Instant createdAt
) {}
