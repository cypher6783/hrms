package com.hospital.resource.admission.repository;

import com.hospital.resource.admission.entity.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdmissionRepository extends JpaRepository<Admission, UUID> {

    Optional<Admission> findByAdmissionNumber(String admissionNumber);

    Optional<Admission> findByPatientIdAndIsActiveTrue(UUID patientId);

    List<Admission> findByWardIdAndIsActiveTrue(UUID wardId);

    List<Admission> findByStatus(String status);

    long countByWardIdAndStatus(UUID wardId, String status);

    @Query("SELECT COUNT(a) FROM Admission a WHERE a.isActive = true")
    long countActiveAdmissions();
}
