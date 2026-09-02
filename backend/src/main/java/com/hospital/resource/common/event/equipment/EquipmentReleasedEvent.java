package com.hospital.resource.common.event.equipment;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class EquipmentReleasedEvent extends DomainEvent {

    private final UUID equipmentId;
    private final UUID admissionId;
    private final UUID releasedBy;

    public EquipmentReleasedEvent(Object source, UUID equipmentId, UUID admissionId, UUID releasedBy) {
        super(source, "EQUIPMENT_RELEASED");
        this.equipmentId = equipmentId;
        this.admissionId = admissionId;
        this.releasedBy = releasedBy;
    }
}
