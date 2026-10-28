package com.hospital.resource.assessment;

import com.hospital.resource.assessment.entity.ClinicalAssessment;
import com.hospital.resource.assessment.repository.ClinicalAssessmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ClinicalAssessmentRepositoryTest {

    @Autowired
    private ClinicalAssessmentRepository assessmentRepository;

    private ClinicalAssessment testAssessment;
    private UUID patientId;
    private UUID admissionId;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        admissionId = UUID.randomUUID();

        testAssessment = ClinicalAssessment.builder()
                .patientId(patientId)
                .admissionId(admissionId)
                .assessedBy(UUID.randomUUID())
                .severityLevel("HIGH")
                .triageClassification("EMERGENCY")
                .infectionStatus("NEGATIVE")
                .clinicalNotes("Test assessment notes")
                .isReassessment(false)
                .assessmentTimestamp(Instant.now())
                .build();
        assessmentRepository.save(testAssessment);
    }

    @Test
    void findByPatientIdOrderByAssessmentTimestampDesc_Success() {
        List<ClinicalAssessment> result = assessmentRepository.findByPatientIdOrderByAssessmentTimestampDesc(patientId);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPatientId()).isEqualTo(patientId);
    }

    @Test
    void findByPatientIdOrderByAssessmentTimestampDesc_Empty() {
        List<ClinicalAssessment> result = assessmentRepository.findByPatientIdOrderByAssessmentTimestampDesc(UUID.randomUUID());
        assertThat(result).isEmpty();
    }

    @Test
    void findByAdmissionIdOrderByAssessmentTimestampDesc_Success() {
        List<ClinicalAssessment> result = assessmentRepository.findByAdmissionIdOrderByAssessmentTimestampDesc(admissionId);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAdmissionId()).isEqualTo(admissionId);
    }

    @Test
    void findTopByAdmissionIdOrderByAssessmentTimestampDesc_Success() {
        ClinicalAssessment result = assessmentRepository.findTopByAdmissionIdOrderByAssessmentTimestampDesc(admissionId);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testAssessment.getId());
    }

    @Test
    void findTopByAdmissionIdOrderByAssessmentTimestampDesc_NotFound() {
        ClinicalAssessment result = assessmentRepository.findTopByAdmissionIdOrderByAssessmentTimestampDesc(UUID.randomUUID());
        assertThat(result).isNull();
    }

    @Test
    void saveAssessment_SetsId() {
        ClinicalAssessment newAssessment = ClinicalAssessment.builder()
                .patientId(UUID.randomUUID())
                .assessedBy(UUID.randomUUID())
                .severityLevel("LOW")
                .triageClassification("NON_URGENT")
                .infectionStatus("NEGATIVE")
                .assessmentTimestamp(Instant.now())
                .build();
        ClinicalAssessment saved = assessmentRepository.save(newAssessment);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void saveAssessment_DefaultsReassessmentToFalse() {
        ClinicalAssessment newAssessment = ClinicalAssessment.builder()
                .patientId(UUID.randomUUID())
                .assessedBy(UUID.randomUUID())
                .severityLevel("MODERATE")
                .triageClassification("URGENT")
                .infectionStatus("NEGATIVE")
                .assessmentTimestamp(Instant.now())
                .build();
        ClinicalAssessment saved = assessmentRepository.save(newAssessment);
        assertThat(saved.getIsReassessment()).isFalse();
    }
}
