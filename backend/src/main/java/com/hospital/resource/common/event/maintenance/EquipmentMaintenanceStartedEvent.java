package com.hospital.resource.common.event.maintenance;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class EquipmentMaintenanceStartedEvent extends DomainEvent {

    private final UUID maintenanceId;
    private final UUID equipmentId;
    private final String maintenanceType;

    public EquipmentMaintenanceStartedEvent(Object source, UUID maintenanceId, UUID equipmentId, String maintenanceType) {
        super(source, "EQUIPMENT_MAINTENANCE_STARTED");
        this.maintenanceId = maintenanceId;
        this.equipmentId = equipmentId;
        this.maintenanceType = maintenanceType;
    }
}
