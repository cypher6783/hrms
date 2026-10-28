package com.hospital.resource.common.event.resource;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ResourceAllocatedEvent extends DomainEvent {

    private final UUID allocationId;
    private final UUID resourceId;
    private final UUID admissionId;
    private final Integer quantity;
    private final UUID allocatedBy;

    public ResourceAllocatedEvent(Object source, UUID allocationId, UUID resourceId, UUID admissionId, Integer quantity, UUID allocatedBy) {
        super(source, "RESOURCE_ALLOCATED");
        this.allocationId = allocationId;
        this.resourceId = resourceId;
        this.admissionId = admissionId;
        this.quantity = quantity;
        this.allocatedBy = allocatedBy;
    }
}
