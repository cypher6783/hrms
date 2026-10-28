package com.hospital.resource.patient.dto;

public record PatientSearchRequest(
        String search,
        String gender,
        Boolean isActive
) {}
