package com.hospital.resource.staff.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record StaffResponse(
        UUID id,
        String staffNumber,
        String fullName,
        String role,
        String specialization,
        String certificationStatus,
        LocalDate certificationExpiry,
        UUID wardId,
        BigDecimal maxWorkloadThreshold,
        String availabilityStatus,
        Instant createdAt,
        Instant updatedAt
) {}
