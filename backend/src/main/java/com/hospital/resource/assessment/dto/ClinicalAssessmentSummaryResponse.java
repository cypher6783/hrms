package com.hospital.resource.assessment.dto;

import java.time.Instant;
import java.util.UUID;

public record ClinicalAssessmentSummaryResponse(
        UUID id,
        UUID patientId,
        String severityLevel,
        String triageClassification,
        Instant assessmentTimestamp
) {}
