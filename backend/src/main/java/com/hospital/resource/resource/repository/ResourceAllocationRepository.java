package com.hospital.resource.resource.repository;

import com.hospital.resource.resource.entity.ResourceAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResourceAllocationRepository extends JpaRepository<ResourceAllocation, UUID>, JpaSpecificationExecutor<ResourceAllocation> {
    List<ResourceAllocation> findByResourceId(UUID resourceId);
    List<ResourceAllocation> findByAdmissionId(UUID admissionId);
    List<ResourceAllocation> findByResourceIdAndReleasedAtIsNull(UUID resourceId);

    @Query("SELECT COALESCE(SUM(ra.quantity), 0) FROM ResourceAllocation ra WHERE ra.resourceId = :resourceId AND ra.releasedAt IS NULL")
    Integer sumActiveAllocatedQuantityByResourceId(@Param("resourceId") UUID resourceId);
}
