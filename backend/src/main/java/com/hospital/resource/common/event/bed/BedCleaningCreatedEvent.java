package com.hospital.resource.common.event.bed;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BedCleaningCreatedEvent extends DomainEvent {

    private final UUID cleaningTaskId;
    private final UUID bedId;
    private final UUID admissionId;

    public BedCleaningCreatedEvent(Object source, UUID cleaningTaskId, UUID bedId, UUID admissionId) {
        super(source, "BED_CLEANING_CREATED");
        this.cleaningTaskId = cleaningTaskId;
        this.bedId = bedId;
        this.admissionId = admissionId;
    }
}
