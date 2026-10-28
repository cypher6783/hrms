package com.hospital.resource.equipment.domain;

import com.hospital.resource.common.exception.ConflictException;
import com.hospital.resource.common.exception.ValidationException;
import com.hospital.resource.equipment.entity.Equipment;

public class EquipmentDomainService {

    public void validateAssignable(Equipment equipment) {
        if ("IN_USE".equals(equipment.getStatus())) {
            throw new ConflictException(String.format("Equipment %s (Serial: %s) is already assigned and in use",
                    equipment.getName(), equipment.getSerialNumber()));
        }
        if ("UNDER_MAINTENANCE".equals(equipment.getStatus())) {
            throw new ValidationException(String.format("Equipment %s (Serial: %s) is currently under maintenance and cannot be assigned",
                    equipment.getName(), equipment.getSerialNumber()));
        }
        if ("OUT_OF_SERVICE".equals(equipment.getStatus())) {
            throw new ValidationException(String.format("Equipment %s (Serial: %s) is out of service",
                    equipment.getName(), equipment.getSerialNumber()));
        }
        if ("DECONTAMINATION_REQUIRED".equals(equipment.getStatus())) {
            throw new ValidationException(String.format("Equipment %s (Serial: %s) requires decontamination before reuse",
                    equipment.getName(), equipment.getSerialNumber()));
        }
    }

    public void validateSerialUniqueness(boolean exists) {
        if (exists) {
            throw new ConflictException("Equipment serial number already exists in the system");
        }
    }
}
