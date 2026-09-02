package com.hospital.resource.resource;

import com.hospital.resource.common.exception.ValidationException;
import com.hospital.resource.resource.domain.InventoryDomainService;
import com.hospital.resource.resource.entity.ResourceInventory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryDomainServiceTest {

    private final InventoryDomainService domainService = new InventoryDomainService();

    @Test
    void validateNonNegativeStock_Throws_WhenDeductExceedsStock() {
        assertThatThrownBy(() -> domainService.validateNonNegativeStock(50, 60))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("cannot become negative");
    }

    @Test
    void validateNotExpired_Throws_WhenBatchExpired() {
        ResourceInventory inventory = ResourceInventory.builder()
                .batchNumber("EXP-123")
                .expirationDate(LocalDate.now().minusDays(2))
                .build();

        assertThatThrownBy(() -> domainService.validateNotExpired(inventory))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void isLowStock_ReturnsTrue_WhenStockBelowThreshold() {
        boolean low = domainService.isLowStock(15, 20);
        assertThat(low).isTrue();
    }
}
