package com.hospital.resource.assessment.repository;

import com.hospital.resource.assessment.entity.ClinicalAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClinicalAssessmentRepository extends JpaRepository<ClinicalAssessment, UUID> {

    List<ClinicalAssessment> findByPatientIdOrderByAssessmentTimestampDesc(UUID patientId);

    List<ClinicalAssessment> findByAdmissionIdOrderByAssessmentTimestampDesc(UUID admissionId);

    ClinicalAssessment findTopByAdmissionIdOrderByAssessmentTimestampDesc(UUID admissionId);
}
