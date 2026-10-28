package com.hospital.resource.resource.dto;

import jakarta.validation.constraints.NotBlank;

public record SupplierRequest(
        @NotBlank String name,
        String contactPerson,
        String phoneNumber,
        String email,
        String address,
        Integer leadTimeDays
) {}
