package com.hospital.resource.assessment.mapper;

import com.hospital.resource.assessment.dto.ClinicalAssessmentRequest;
import com.hospital.resource.assessment.dto.ClinicalAssessmentResponse;
import com.hospital.resource.assessment.dto.ClinicalAssessmentSummaryResponse;
import com.hospital.resource.assessment.entity.ClinicalAssessment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClinicalAssessmentMapperImpl implements ClinicalAssessmentMapper {

    @Override
    public ClinicalAssessment toEntity(ClinicalAssessmentRequest request) {
        if (request == null) return null;
        return ClinicalAssessment.builder()
                .patientId(request.patientId())
                .admissionId(request.admissionId())
                .severityLevel(request.severityLevel())
                .triageClassification(request.triageClassification())
                .infectionStatus(request.infectionStatus())
                .clinicalNotes(request.clinicalNotes())
                .build();
    }

    @Override
    public ClinicalAssessmentResponse toResponse(ClinicalAssessment assessment) {
        if (assessment == null) return null;
        return new ClinicalAssessmentResponse(
                assessment.getId(),
                assessment.getPatientId(),
                assessment.getAdmissionId(),
                assessment.getAssessedBy(),
                assessment.getSeverityLevel(),
                assessment.getTriageClassification(),
                assessment.getInfectionStatus(),
                assessment.getClinicalNotes(),
                assessment.getIsReassessment(),
                assessment.getAssessmentTimestamp(),
                assessment.getCreatedAt()
        );
    }

    @Override
    public ClinicalAssessmentSummaryResponse toSummary(ClinicalAssessment assessment) {
        if (assessment == null) return null;
        return new ClinicalAssessmentSummaryResponse(
                assessment.getId(),
                assessment.getPatientId(),
                assessment.getSeverityLevel(),
                assessment.getTriageClassification(),
                assessment.getAssessmentTimestamp()
        );
    }

    @Override
    public List<ClinicalAssessmentResponse> toResponseList(List<ClinicalAssessment> assessments) {
        if (assessments == null) return List.of();
        return assessments.stream().map(this::toResponse).toList();
    }
}
