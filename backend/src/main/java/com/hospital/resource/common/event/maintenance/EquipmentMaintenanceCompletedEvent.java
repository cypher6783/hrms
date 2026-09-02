package com.hospital.resource.common.event.maintenance;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class EquipmentMaintenanceCompletedEvent extends DomainEvent {

    private final UUID maintenanceId;
    private final UUID equipmentId;
    private final String performedBy;

    public EquipmentMaintenanceCompletedEvent(Object source, UUID maintenanceId, UUID equipmentId, String performedBy) {
        super(source, "EQUIPMENT_MAINTENANCE_COMPLETED");
        this.maintenanceId = maintenanceId;
        this.equipmentId = equipmentId;
        this.performedBy = performedBy;
    }
}
