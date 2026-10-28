package com.hospital.resource.common.event.bed;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BedCleaningVerifiedEvent extends DomainEvent {

    private final UUID cleaningTaskId;
    private final UUID bedId;
    private final UUID verifiedBy;

    public BedCleaningVerifiedEvent(Object source, UUID cleaningTaskId, UUID bedId, UUID verifiedBy) {
        super(source, "BED_CLEANING_VERIFIED");
        this.cleaningTaskId = cleaningTaskId;
        this.bedId = bedId;
        this.verifiedBy = verifiedBy;
    }
}
