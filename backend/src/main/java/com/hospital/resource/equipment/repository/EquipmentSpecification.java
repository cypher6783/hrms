package com.hospital.resource.equipment.repository;

import com.hospital.resource.equipment.entity.Equipment;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class EquipmentSpecification {

    public static Specification<Equipment> hasStatus(String status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Equipment> hasEquipmentType(String equipmentType) {
        return (root, query, cb) -> equipmentType == null ? null : cb.equal(root.get("equipmentType"), equipmentType);
    }

    public static Specification<Equipment> hasAssignedWardId(UUID wardId) {
        return (root, query, cb) -> wardId == null ? null : cb.equal(root.get("assignedWardId"), wardId);
    }
}
