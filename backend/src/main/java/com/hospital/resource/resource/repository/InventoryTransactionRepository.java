package com.hospital.resource.resource.repository;

import com.hospital.resource.resource.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, UUID> {

    List<InventoryTransaction> findByResourceInventoryIdOrderByTransactionTimestampDesc(UUID resourceInventoryId);

    List<InventoryTransaction> findByAdmissionId(UUID admissionId);
}
