package com.hospital.resource.patient.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PatientResponse(
        UUID id,
        String patientNumber,
        String fullName,
        LocalDate dateOfBirth,
        String gender,
        String phoneNumber,
        String address,
        String nextOfKinName,
        String nextOfKinPhone,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {}
