package com.hospital.resource.staff.dto;

import java.time.Instant;
import java.util.UUID;

public record StaffSummaryResponse(
        UUID id,
        String staffNumber,
        String fullName,
        String role,
        String specialization,
        UUID wardId,
        String availabilityStatus,
        Instant createdAt
) {}
