package com.hospital.resource.equipment.repository;

import com.hospital.resource.equipment.entity.EquipmentMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface EquipmentMaintenanceRepository extends JpaRepository<EquipmentMaintenance, UUID>, JpaSpecificationExecutor<EquipmentMaintenance> {
    List<EquipmentMaintenance> findByEquipmentId(UUID equipmentId);
    List<EquipmentMaintenance> findByStatus(String status);
    List<EquipmentMaintenance> findByEquipmentIdAndStatus(UUID equipmentId, String status);

    @Query("SELECT m FROM EquipmentMaintenance m WHERE m.status = 'SCHEDULED' AND m.scheduledDate < :currentDate")
    List<EquipmentMaintenance> findOverdueMaintenance(@Param("currentDate") LocalDate currentDate);
}
