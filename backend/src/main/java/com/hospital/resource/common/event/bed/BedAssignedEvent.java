package com.hospital.resource.common.event.bed;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BedAssignedEvent extends DomainEvent {

    private final UUID bedId;
    private final UUID wardId;
    private final UUID admissionId;

    public BedAssignedEvent(Object source, UUID bedId, UUID wardId, UUID admissionId) {
        super(source, "BED_ASSIGNED");
        this.bedId = bedId;
        this.wardId = wardId;
        this.admissionId = admissionId;
    }
}
