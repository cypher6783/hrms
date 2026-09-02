package com.hospital.resource.patient.dto;

import java.util.UUID;

public record PatientSummaryResponse(
        UUID id,
        String patientNumber,
        String fullName,
        Boolean isActive
) {}
