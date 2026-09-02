package com.hospital.resource.equipment.domain;

import com.hospital.resource.common.exception.ValidationException;
import com.hospital.resource.equipment.entity.EquipmentMaintenance;

public class MaintenanceDomainService {

    public void validateCompletable(EquipmentMaintenance maintenance) {
        if ("COMPLETED".equals(maintenance.getStatus()) || "VERIFIED".equals(maintenance.getStatus())) {
            throw new ValidationException("Maintenance task is already completed");
        }
    }

    public void validateVerifiable(EquipmentMaintenance maintenance) {
        if (!"COMPLETED".equals(maintenance.getStatus())) {
            throw new ValidationException("Maintenance task must be marked as COMPLETED by technician before return-to-service verification");
        }
    }
}
