package com.hospital.resource.admission.dto;

import jakarta.validation.constraints.NotBlank;

public record DischargeRequest(
        @NotBlank String dischargeOutcome,
        String dischargeNotes
) {}
