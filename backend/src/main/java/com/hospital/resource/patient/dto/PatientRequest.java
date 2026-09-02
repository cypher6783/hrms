package com.hospital.resource.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public record PatientRequest(
        @NotBlank String fullName,
        @Past LocalDate dateOfBirth,
        @NotBlank String gender,
        String phoneNumber,
        String address,
        String nextOfKinName,
        String nextOfKinPhone
) {}
