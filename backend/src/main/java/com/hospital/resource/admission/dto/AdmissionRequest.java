package com.hospital.resource.admission.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AdmissionRequest(
        @NotNull UUID patientId,
        @NotNull UUID wardId,
        UUID bedId,
        String admissionNotes
) {}
