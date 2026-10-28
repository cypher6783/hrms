package com.hospital.resource.bedcleaning.repository;

import com.hospital.resource.bedcleaning.entity.BedCleaning;
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
public interface BedCleaningRepository extends JpaRepository<BedCleaning, UUID> {

    List<BedCleaning> findByBedIdAndStatus(UUID bedId, String status);

    List<BedCleaning> findByStatus(String status);

    Optional<BedCleaning> findByAdmissionIdAndStatus(UUID admissionId, String status);

    List<BedCleaning> findByAssignedTo(UUID assignedTo);

    Page<BedCleaning> findByStatus(String status, Pageable pageable);

    @Query("SELECT bc FROM BedCleaning bc WHERE (:bedId IS NULL OR bc.bedId = :bedId) " +
            "AND (:status IS NULL OR bc.status = :status) " +
            "AND (:assignedTo IS NULL OR bc.assignedTo = :assignedTo) " +
            "AND (:dateFrom IS NULL OR bc.createdAt >= :dateFrom) " +
            "AND (:dateTo IS NULL OR bc.createdAt <= :dateTo)")
    Page<BedCleaning> searchCleaningTasks(
            @Param("bedId") UUID bedId,
            @Param("status") String status,
            @Param("assignedTo") UUID assignedTo,
            @Param("dateFrom") Instant dateFrom,
            @Param("dateTo") Instant dateTo,
            Pageable pageable
    );

    @Query("SELECT COUNT(bc) FROM BedCleaning bc WHERE bc.status = :status")
    long countByStatus(@Param("status") String status);

    @Query("SELECT COUNT(bc) FROM BedCleaning bc WHERE bc.assignedTo = :staffId AND bc.status IN ('ASSIGNED', 'IN_PROGRESS')")
    long countActiveTasksByStaff(@Param("staffId") UUID staffId);

    @Query("SELECT bc.status, COUNT(bc) FROM BedCleaning bc GROUP BY bc.status")
    List<Object[]> countByStatusGrouped();

    @Query("SELECT COUNT(bc) FROM BedCleaning bc WHERE bc.completedAt >= :since")
    long countCompletedSince(@Param("since") Instant since);
}
