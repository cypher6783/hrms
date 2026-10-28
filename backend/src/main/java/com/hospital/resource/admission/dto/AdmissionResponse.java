package com.hospital.resource.admission.dto;

import java.time.Instant;
import java.util.UUID;

public record AdmissionResponse(
        UUID id,
        String admissionNumber,
        UUID patientId,
        UUID wardId,
        UUID bedId,
        String status,
        String admissionNotes,
        String dischargeOutcome,
        String dischargeNotes,
        Instant admittedAt,
        Instant dischargedAt,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {}
