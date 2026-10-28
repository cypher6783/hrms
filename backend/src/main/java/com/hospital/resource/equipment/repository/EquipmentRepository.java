package com.hospital.resource.equipment.repository;

import com.hospital.resource.equipment.entity.Equipment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, UUID>, JpaSpecificationExecutor<Equipment> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Equipment e WHERE e.id = :id")
    Optional<Equipment> findByIdWithLock(@Param("id") UUID id);

    List<Equipment> findByStatus(String status);
    List<Equipment> findByEquipmentType(String equipmentType);
    List<Equipment> findByAssignedWardId(UUID wardId);
    List<Equipment> findByAssignedAdmissionId(UUID admissionId);
    Optional<Equipment> findBySerialNumber(String serialNumber);
    boolean existsBySerialNumber(String serialNumber);
}
