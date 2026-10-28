package com.hospital.resource.assessment.dto;

import java.time.Instant;
import java.util.UUID;

public record ClinicalAssessmentResponse(
        UUID id,
        UUID patientId,
        UUID admissionId,
        UUID assessedBy,
        String severityLevel,
        String triageClassification,
        String infectionStatus,
        String clinicalNotes,
        Boolean isReassessment,
        Instant assessmentTimestamp,
        Instant createdAt
) {}
