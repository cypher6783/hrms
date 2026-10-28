package com.hospital.resource.common.event.inventory;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class InventoryUpdatedEvent extends DomainEvent {

    private final UUID inventoryId;
    private final UUID resourceId;
    private final String transactionType;
    private final Integer quantity;
    private final Integer newCurrentStock;

    public InventoryUpdatedEvent(Object source, UUID inventoryId, UUID resourceId, String transactionType, Integer quantity, Integer newCurrentStock) {
        super(source, "INVENTORY_UPDATED");
        this.inventoryId = inventoryId;
        this.resourceId = resourceId;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.newCurrentStock = newCurrentStock;
    }
}
