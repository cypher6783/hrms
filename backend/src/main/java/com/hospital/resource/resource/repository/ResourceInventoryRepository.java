package com.hospital.resource.resource.repository;

import com.hospital.resource.resource.entity.ResourceInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResourceInventoryRepository extends JpaRepository<ResourceInventory, UUID> {

    List<ResourceInventory> findByResourceId(UUID resourceId);

    @Query("SELECT SUM(ri.currentStock) FROM ResourceInventory ri WHERE ri.resourceId = :resourceId")
    Integer sumStockByResourceId(UUID resourceId);

    List<ResourceInventory> findByResourceIdAndLocation(UUID resourceId, String location);
}
