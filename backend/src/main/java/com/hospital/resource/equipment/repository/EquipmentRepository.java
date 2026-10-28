package com.hospital.resource.equipment.repository;

import com.hospital.resource.equipment.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, UUID>, JpaSpecificationExecutor<Equipment> {
    List<Equipment> findByStatus(String status);
    List<Equipment> findByEquipmentType(String equipmentType);
    List<Equipment> findByAssignedWardId(UUID wardId);
    List<Equipment> findByAssignedAdmissionId(UUID admissionId);
    Optional<Equipment> findBySerialNumber(String serialNumber);
    boolean existsBySerialNumber(String serialNumber);
}
