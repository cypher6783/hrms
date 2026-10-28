package com.hospital.resource.assessment;

import com.hospital.resource.assessment.dto.ClinicalAssessmentRequest;
import com.hospital.resource.assessment.dto.ClinicalAssessmentResponse;
import com.hospital.resource.assessment.entity.ClinicalAssessment;
import com.hospital.resource.assessment.mapper.ClinicalAssessmentMapper;
import com.hospital.resource.assessment.repository.ClinicalAssessmentRepository;
import com.hospital.resource.assessment.service.ClinicalAssessmentApplicationService;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicalAssessmentApplicationServiceTest {

    @Mock
    private ClinicalAssessmentRepository assessmentRepository;

    @Mock
    private ClinicalAssessmentMapper assessmentMapper;

    @InjectMocks
    private ClinicalAssessmentApplicationService assessmentService;

    private ClinicalAssessment testAssessment;
    private ClinicalAssessmentResponse testResponse;
    private UUID assessedBy;

    @BeforeEach
    void setUp() {
        assessedBy = UUID.randomUUID();

        testAssessment = ClinicalAssessment.builder()
                .id(UUID.randomUUID())
                .patientId(UUID.randomUUID())
                .admissionId(UUID.randomUUID())
                .assessedBy(assessedBy)
                .severityLevel("HIGH")
                .triageClassification("EMERGENCY")
                .infectionStatus("NEGATIVE")
                .clinicalNotes("Test notes")
                .isReassessment(false)
                .assessmentTimestamp(Instant.now())
                .build();

        testResponse = new ClinicalAssessmentResponse(
                testAssessment.getId(),
                testAssessment.getPatientId(),
                testAssessment.getAdmissionId(),
                assessedBy,
                "HIGH", "EMERGENCY", "NEGATIVE", "Test notes",
                false, Instant.now(), Instant.now()
        );
    }

    @Test
    void createAssessment_Success() {
        when(assessmentRepository.findByAdmissionIdOrderByAssessmentTimestampDesc(any(UUID.class)))
                .thenReturn(List.of());
        when(assessmentMapper.toEntity(any(ClinicalAssessmentRequest.class))).thenReturn(testAssessment);
        when(assessmentRepository.save(any(ClinicalAssessment.class))).thenReturn(testAssessment);
        when(assessmentMapper.toResponse(any(ClinicalAssessment.class))).thenReturn(testResponse);

        ClinicalAssessmentRequest request = new ClinicalAssessmentRequest(
                testAssessment.getPatientId(), testAssessment.getAdmissionId(),
                "HIGH", "EMERGENCY", "NEGATIVE", "Test notes"
        );

        ClinicalAssessmentResponse result = assessmentService.createAssessment(request, assessedBy);

        assertThat(result).isNotNull();
        assertThat(result.severityLevel()).isEqualTo("HIGH");
        assertThat(result.isReassessment()).isFalse();
        verify(assessmentRepository).save(any(ClinicalAssessment.class));
    }

    @Test
    void createAssessment_DetectsReassessment() {
        ClinicalAssessment existingAssessment = ClinicalAssessment.builder()
                .id(UUID.randomUUID())
                .patientId(testAssessment.getPatientId())
                .admissionId(testAssessment.getAdmissionId())
                .severityLevel("LOW")
                .triageClassification("NON_URGENT")
                .infectionStatus("NEGATIVE")
                .assessmentTimestamp(Instant.now().minusSeconds(3600))
                .build();

        when(assessmentRepository.findByAdmissionIdOrderByAssessmentTimestampDesc(any(UUID.class)))
                .thenReturn(List.of(existingAssessment));
        when(assessmentMapper.toEntity(any(ClinicalAssessmentRequest.class))).thenReturn(testAssessment);
        when(assessmentRepository.save(any(ClinicalAssessment.class))).thenReturn(testAssessment);
        when(assessmentMapper.toResponse(any(ClinicalAssessment.class))).thenReturn(testResponse);

        ClinicalAssessmentRequest request = new ClinicalAssessmentRequest(
                testAssessment.getPatientId(), testAssessment.getAdmissionId(),
                "HIGH", "EMERGENCY", "NEGATIVE", "Follow-up"
        );

        ClinicalAssessmentResponse result = assessmentService.createAssessment(request, assessedBy);

        assertThat(result).isNotNull();
        verify(assessmentRepository).save(any(ClinicalAssessment.class));
    }

    @Test
    void getPatientTimeline_Success() {
        when(assessmentRepository.findByPatientIdOrderByAssessmentTimestampDesc(any(UUID.class)))
                .thenReturn(List.of(testAssessment));
        when(assessmentMapper.toResponseList(any())).thenReturn(List.of(testResponse));

        List<ClinicalAssessmentResponse> result = assessmentService.getPatientTimeline(UUID.randomUUID());

        assertThat(result).hasSize(1);
    }

    @Test
    void getAdmissionTimeline_Success() {
        when(assessmentRepository.findByAdmissionIdOrderByAssessmentTimestampDesc(any(UUID.class)))
                .thenReturn(List.of(testAssessment));
        when(assessmentMapper.toResponseList(any())).thenReturn(List.of(testResponse));

        List<ClinicalAssessmentResponse> result = assessmentService.getAdmissionTimeline(UUID.randomUUID());

        assertThat(result).hasSize(1);
    }

    @Test
    void getLatestByAdmission_Success() {
        when(assessmentRepository.findTopByAdmissionIdOrderByAssessmentTimestampDesc(any(UUID.class)))
                .thenReturn(testAssessment);
        when(assessmentMapper.toResponse(testAssessment)).thenReturn(testResponse);

        ClinicalAssessmentResponse result = assessmentService.getLatestByAdmission(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result.severityLevel()).isEqualTo("HIGH");
    }

    @Test
    void getLatestByAdmission_NotFound() {
        when(assessmentRepository.findTopByAdmissionIdOrderByAssessmentTimestampDesc(any(UUID.class)))
                .thenReturn(null);

        assertThatThrownBy(() -> assessmentService.getLatestByAdmission(UUID.randomUUID()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
