package com.hospital.resource.equipment.repository;

import com.hospital.resource.equipment.entity.EquipmentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EquipmentAllocationRepository extends JpaRepository<EquipmentAllocation, UUID>, JpaSpecificationExecutor<EquipmentAllocation> {
    List<EquipmentAllocation> findByEquipmentId(UUID equipmentId);
    List<EquipmentAllocation> findByAdmissionId(UUID admissionId);
    Optional<EquipmentAllocation> findByEquipmentIdAndReleasedAtIsNull(UUID equipmentId);
}
