package com.hospital.resource.common.event.equipment;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class EquipmentAssignedEvent extends DomainEvent {

    private final UUID equipmentId;
    private final UUID admissionId;
    private final UUID wardId;
    private final UUID assignedBy;

    public EquipmentAssignedEvent(Object source, UUID equipmentId, UUID admissionId, UUID wardId, UUID assignedBy) {
        super(source, "EQUIPMENT_ASSIGNED");
        this.equipmentId = equipmentId;
        this.admissionId = admissionId;
        this.wardId = wardId;
        this.assignedBy = assignedBy;
    }
}
