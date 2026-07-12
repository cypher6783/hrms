package com.hospital.resource.equipment.repository;

import com.hospital.resource.equipment.entity.EquipmentMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EquipmentMaintenanceRepository extends JpaRepository<EquipmentMaintenance, UUID> {

    List<EquipmentMaintenance> findByEquipmentIdAndStatus(UUID equipmentId, String status);

    List<EquipmentMaintenance> findByStatus(String status);

    List<EquipmentMaintenance> findByEquipmentId(UUID equipmentId);
}
