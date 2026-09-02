package com.hospital.resource.common.event.shift;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ShiftAssignedEvent extends DomainEvent {

    private final UUID shiftId;
    private final UUID staffId;
    private final UUID assignedBy;

    public ShiftAssignedEvent(Object source, UUID shiftId, UUID staffId, UUID assignedBy) {
        super(source, "SHIFT_ASSIGNED");
        this.shiftId = shiftId;
        this.staffId = staffId;
        this.assignedBy = assignedBy;
    }
}
