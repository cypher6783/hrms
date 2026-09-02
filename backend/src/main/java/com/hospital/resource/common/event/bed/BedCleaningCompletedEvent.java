package com.hospital.resource.common.event.bed;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BedCleaningCompletedEvent extends DomainEvent {

    private final UUID cleaningTaskId;
    private final UUID bedId;
    private final UUID assignedTo;

    public BedCleaningCompletedEvent(Object source, UUID cleaningTaskId, UUID bedId, UUID assignedTo) {
        super(source, "BED_CLEANING_COMPLETED");
        this.cleaningTaskId = cleaningTaskId;
        this.bedId = bedId;
        this.assignedTo = assignedTo;
    }
}
