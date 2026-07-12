package com.hospital.resource.staff.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record StaffRequest(
        @NotBlank String fullName,
        @NotBlank String role,
        String specialization,
        String certificationStatus,
        LocalDate certificationExpiry,
        UUID wardId,
        BigDecimal maxWorkloadThreshold,
        String availabilityStatus
) {}
