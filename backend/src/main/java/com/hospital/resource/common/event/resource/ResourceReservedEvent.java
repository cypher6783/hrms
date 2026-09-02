package com.hospital.resource.common.event.resource;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ResourceReservedEvent extends DomainEvent {

    private final UUID reservationId;
    private final UUID resourceId;
    private final UUID admissionId;
    private final Integer quantity;
    private final UUID reservedBy;

    public ResourceReservedEvent(Object source, UUID reservationId, UUID resourceId, UUID admissionId, Integer quantity, UUID reservedBy) {
        super(source, "RESOURCE_RESERVED");
        this.reservationId = reservationId;
        this.resourceId = resourceId;
        this.admissionId = admissionId;
        this.quantity = quantity;
        this.reservedBy = reservedBy;
    }
}
