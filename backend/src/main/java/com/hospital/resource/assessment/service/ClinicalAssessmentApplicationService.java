package com.hospital.resource.assessment.service;

import com.hospital.resource.assessment.dto.ClinicalAssessmentRequest;
import com.hospital.resource.assessment.dto.ClinicalAssessmentResponse;
import com.hospital.resource.assessment.entity.ClinicalAssessment;
import com.hospital.resource.assessment.mapper.ClinicalAssessmentMapper;
import com.hospital.resource.assessment.repository.ClinicalAssessmentRepository;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClinicalAssessmentApplicationService {

    private final ClinicalAssessmentRepository assessmentRepository;
    private final ClinicalAssessmentMapper assessmentMapper;

    @Transactional
    public ClinicalAssessmentResponse createAssessment(ClinicalAssessmentRequest request, UUID assessedBy) {
        boolean isReassessment = request.admissionId() != null &&
                assessmentRepository.findByAdmissionIdOrderByAssessmentTimestampDesc(request.admissionId()).size() > 0;

        ClinicalAssessment assessment = assessmentMapper.toEntity(request);
        assessment.setAssessedBy(assessedBy);
        assessment.setIsReassessment(isReassessment);
        assessment.setAssessmentTimestamp(Instant.now());

        assessment = assessmentRepository.save(assessment);
        log.info("Assessment created: id={}, patientId={}", assessment.getId(), request.patientId());
        return assessmentMapper.toResponse(assessment);
    }

    @Transactional(readOnly = true)
    public List<ClinicalAssessmentResponse> getPatientTimeline(UUID patientId) {
        List<ClinicalAssessment> assessments = assessmentRepository.findByPatientIdOrderByAssessmentTimestampDesc(patientId);
        return assessmentMapper.toResponseList(assessments);
    }

    @Transactional(readOnly = true)
    public List<ClinicalAssessmentResponse> getAdmissionTimeline(UUID admissionId) {
        List<ClinicalAssessment> assessments = assessmentRepository.findByAdmissionIdOrderByAssessmentTimestampDesc(admissionId);
        return assessmentMapper.toResponseList(assessments);
    }

    @Transactional(readOnly = true)
    public ClinicalAssessmentResponse getLatestByAdmission(UUID admissionId) {
        ClinicalAssessment assessment = assessmentRepository.findTopByAdmissionIdOrderByAssessmentTimestampDesc(admissionId);
        if (assessment == null) {
            throw new ResourceNotFoundException("Clinical assessment for admission: " + admissionId);
        }
        return assessmentMapper.toResponse(assessment);
    }
}
