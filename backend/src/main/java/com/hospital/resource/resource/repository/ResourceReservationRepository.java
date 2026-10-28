package com.hospital.resource.resource.repository;

import com.hospital.resource.resource.entity.ResourceReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ResourceReservationRepository extends JpaRepository<ResourceReservation, UUID>, JpaSpecificationExecutor<ResourceReservation> {
    List<ResourceReservation> findByResourceId(UUID resourceId);
    List<ResourceReservation> findByAdmissionId(UUID admissionId);
    List<ResourceReservation> findByResourceIdAndStatus(UUID resourceId, String status);

    @Query("SELECT COALESCE(SUM(rr.quantity), 0) FROM ResourceReservation rr WHERE rr.resourceId = :resourceId AND rr.status = 'RESERVED' AND (rr.expiresAt IS NULL OR rr.expiresAt > :now)")
    Integer sumActiveReservedQuantityByResourceId(@Param("resourceId") UUID resourceId, @Param("now") Instant now);
}
