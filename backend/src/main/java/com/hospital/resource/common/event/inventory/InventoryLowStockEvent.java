package com.hospital.resource.common.event.inventory;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class InventoryLowStockEvent extends DomainEvent {

    private final UUID resourceId;
    private final String resourceName;
    private final Integer currentStock;
    private final Integer minimumThreshold;

    public InventoryLowStockEvent(Object source, UUID resourceId, String resourceName, Integer currentStock, Integer minimumThreshold) {
        super(source, "INVENTORY_LOW_STOCK");
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.currentStock = currentStock;
        this.minimumThreshold = minimumThreshold;
    }
}
