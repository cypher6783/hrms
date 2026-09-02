package com.hospital.resource.resource.domain;

import com.hospital.resource.common.exception.ValidationException;
import com.hospital.resource.resource.entity.ResourceInventory;

import java.time.LocalDate;

public class InventoryDomainService {

    public void validateNonNegativeStock(Integer currentStock, Integer deductQuantity) {
        if (deductQuantity > currentStock) {
            throw new ValidationException(String.format("Stock quantity cannot become negative. Current stock: %d, Deduct quantity: %d", currentStock, deductQuantity));
        }
    }

    public void validateNotExpired(ResourceInventory inventory) {
        if (inventory.getExpirationDate() != null && inventory.getExpirationDate().isBefore(LocalDate.now())) {
            throw new ValidationException(String.format("Batch %s expired on %s and cannot be allocated",
                    inventory.getBatchNumber() != null ? inventory.getBatchNumber() : "N/A",
                    inventory.getExpirationDate()));
        }
    }

    public boolean isLowStock(Integer totalStock, Integer threshold) {
        return totalStock != null && threshold != null && totalStock <= threshold;
    }
}
