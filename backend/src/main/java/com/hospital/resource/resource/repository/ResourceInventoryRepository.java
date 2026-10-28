package com.hospital.resource.resource.repository;

import com.hospital.resource.resource.entity.ResourceInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceInventoryRepository extends JpaRepository<ResourceInventory, UUID>, JpaSpecificationExecutor<ResourceInventory> {
    List<ResourceInventory> findByResourceId(UUID resourceId);
    List<ResourceInventory> findByResourceIdAndLocation(UUID resourceId, String location);
    Optional<ResourceInventory> findByResourceIdAndLocationAndBatchNumber(UUID resourceId, String location, String batchNumber);

    @Query("SELECT SUM(ri.currentStock) FROM ResourceInventory ri WHERE ri.resourceId = :resourceId")
    Integer sumStockByResourceId(@Param("resourceId") UUID resourceId);

    @Query("SELECT ri FROM ResourceInventory ri WHERE ri.expirationDate IS NOT NULL AND ri.expirationDate <= :cutoffDate AND ri.currentStock > 0")
    List<ResourceInventory> findExpiredBatches(@Param("cutoffDate") LocalDate cutoffDate);
}
