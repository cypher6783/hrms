package com.hospital.resource.common.event.inventory;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class InventoryExpiredEvent extends DomainEvent {

    private final UUID inventoryId;
    private final UUID resourceId;
    private final String batchNumber;
    private final Integer quantity;
    private final LocalDate expirationDate;

    public InventoryExpiredEvent(Object source, UUID inventoryId, UUID resourceId, String batchNumber, Integer quantity, LocalDate expirationDate) {
        super(source, "INVENTORY_EXPIRED");
        this.inventoryId = inventoryId;
        this.resourceId = resourceId;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
    }
}
