package com.hospital.resource.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record ClinicalAssessmentRequest(
        UUID patientId,
        UUID admissionId,
        @NotBlank String severityLevel,
        @NotBlank String triageClassification,
        @NotBlank String infectionStatus,
        String clinicalNotes
) {}
