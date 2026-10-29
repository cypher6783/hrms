package com.hospital.resource.bed.repository;

import com.hospital.resource.bed.entity.Bed;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BedRepository extends JpaRepository<Bed, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Bed b WHERE b.id = :id")
    Optional<Bed> findByIdWithLock(@Param("id") UUID id);

    List<Bed> findByWardIdAndStatus(UUID wardId, String status);

    List<Bed> findByWardId(UUID wardId);

    long countByWardIdAndStatus(UUID wardId, String status);

    @Query("SELECT COUNT(b) FROM Bed b WHERE b.wardId = :wardId AND b.status = 'AVAILABLE'")
    long countAvailableByWardId(@Param("wardId") UUID wardId);

    @Query("SELECT COUNT(b) FROM Bed b WHERE b.wardId = :wardId AND b.status = 'OCCUPIED'")
    long countOccupiedByWardId(@Param("wardId") UUID wardId);

    List<Bed> findByStatusAndIsIsolationCapable(String status, Boolean isIsolationCapable);

    List<Bed> findByBedType(String bedType);

    @Query("SELECT b FROM Bed b WHERE (cast(:wardId as string) IS NULL OR b.wardId = :wardId) AND (:bedType IS NULL OR b.bedType = :bedType) AND (:status IS NULL OR b.status = :status) AND (:isIsolationCapable IS NULL OR b.isIsolationCapable = :isIsolationCapable)")
    List<Bed> findBedsWithFilters(
            @Param("wardId") UUID wardId,
            @Param("bedType") String bedType,
            @Param("status") String status,
            @Param("isIsolationCapable") Boolean isIsolationCapable
    );
}
