package com.hospital.resource.equipment.repository;

import com.hospital.resource.equipment.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {

    List<Equipment> findByStatus(String status);

    List<Equipment> findByEquipmentType(String equipmentType);

    List<Equipment> findByAssignedWardId(UUID wardId);

    List<Equipment> findByStatusAndEquipmentType(String status, String equipmentType);
}
