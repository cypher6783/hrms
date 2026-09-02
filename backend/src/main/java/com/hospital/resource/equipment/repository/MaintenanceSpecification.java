package com.hospital.resource.equipment.repository;

import com.hospital.resource.equipment.entity.EquipmentMaintenance;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class MaintenanceSpecification {

    public static Specification<EquipmentMaintenance> hasEquipmentId(UUID equipmentId) {
        return (root, query, cb) -> equipmentId == null ? null : cb.equal(root.get("equipmentId"), equipmentId);
    }

    public static Specification<EquipmentMaintenance> hasStatus(String status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<EquipmentMaintenance> hasMaintenanceType(String maintenanceType) {
        return (root, query, cb) -> maintenanceType == null ? null : cb.equal(root.get("maintenanceType"), maintenanceType);
    }
}
