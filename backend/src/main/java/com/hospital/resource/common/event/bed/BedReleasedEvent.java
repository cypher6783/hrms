package com.hospital.resource.common.event.bed;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BedReleasedEvent extends DomainEvent {

    private final UUID bedId;
    private final UUID wardId;
    private final UUID admissionId;

    public BedReleasedEvent(Object source, UUID bedId, UUID wardId, UUID admissionId) {
        super(source, "BED_RELEASED");
        this.bedId = bedId;
        this.wardId = wardId;
        this.admissionId = admissionId;
    }
}
