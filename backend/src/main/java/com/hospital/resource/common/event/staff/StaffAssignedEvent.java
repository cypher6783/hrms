package com.hospital.resource.common.event.staff;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class StaffAssignedEvent extends DomainEvent {

    private final UUID staffId;
    private final UUID wardId;

    public StaffAssignedEvent(Object source, UUID staffId, UUID wardId) {
        super(source, "STAFF_ASSIGNED");
        this.staffId = staffId;
        this.wardId = wardId;
    }
}
