package com.hospital.resource.staff.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record StaffWorkloadResponse(
        UUID staffId,
        String staffNumber,
        BigDecimal currentWorkload,
        BigDecimal maxThreshold,
        BigDecimal workloadPercentage,
        long activeAdmissions,
        boolean isOverloaded
) {}
