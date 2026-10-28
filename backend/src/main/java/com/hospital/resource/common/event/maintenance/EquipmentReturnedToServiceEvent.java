package com.hospital.resource.common.event.maintenance;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class EquipmentReturnedToServiceEvent extends DomainEvent {

    private final UUID maintenanceId;
    private final UUID equipmentId;
    private final UUID verifiedBy;

    public EquipmentReturnedToServiceEvent(Object source, UUID maintenanceId, UUID equipmentId, UUID verifiedBy) {
        super(source, "EQUIPMENT_RETURNED_TO_SERVICE");
        this.maintenanceId = maintenanceId;
        this.equipmentId = equipmentId;
        this.verifiedBy = verifiedBy;
    }
}
