package com.hospital.resource.admission.repository;

import com.hospital.resource.admission.entity.Admission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
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

    Page<Admission> findByPatientId(UUID patientId, Pageable pageable);

    Page<Admission> findByWardId(UUID wardId, Pageable pageable);

    Page<Admission> findByStatus(String status, Pageable pageable);

    @Query("SELECT a FROM Admission a WHERE (:patientId IS NULL OR a.patientId = :patientId) " +
            "AND (:wardId IS NULL OR a.wardId = :wardId) " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND (:dateFrom IS NULL OR a.admittedAt >= :dateFrom) " +
            "AND (:dateTo IS NULL OR a.admittedAt <= :dateTo)")
    Page<Admission> searchAdmissions(
            @Param("patientId") UUID patientId,
            @Param("wardId") UUID wardId,
            @Param("status") String status,
            @Param("dateFrom") Instant dateFrom,
            @Param("dateTo") Instant dateTo,
            Pageable pageable
    );

    @Query("SELECT a FROM Admission a WHERE a.patientId = :patientId ORDER BY a.admittedAt DESC")
    List<Admission> findRecentAdmissionsByPatient(@Param("patientId") UUID patientId, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Admission a WHERE a.wardId = :wardId AND a.isActive = true")
    long countActiveAdmissionsByWard(@Param("wardId") UUID wardId);

    @Query("SELECT a.status, COUNT(a) FROM Admission a WHERE a.wardId = :wardId GROUP BY a.status")
    List<Object[]> countByStatusForWard(@Param("wardId") UUID wardId);

    @Query("SELECT COUNT(a) FROM Admission a WHERE a.dischargedAt >= :since")
    long countDischargedSince(@Param("since") Instant since);

    @Query("SELECT COUNT(a) FROM Admission a WHERE a.createdAt >= :since")
    long countAdmittedSince(@Param("since") Instant since);
}
