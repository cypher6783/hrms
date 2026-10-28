package com.hospital.resource.admission.dto;

import java.time.Instant;
import java.util.UUID;

public record AdmissionSummaryResponse(
        UUID id,
        String admissionNumber,
        UUID patientId,
        UUID wardId,
        UUID bedId,
        String status,
        Instant admittedAt,
        Instant dischargedAt,
        Boolean isActive
) {}
