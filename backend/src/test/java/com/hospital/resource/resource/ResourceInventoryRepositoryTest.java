package com.hospital.resource.resource;

import com.hospital.resource.resource.entity.ResourceInventory;
import com.hospital.resource.resource.repository.ResourceInventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ResourceInventoryRepositoryTest {

    @Autowired
    private ResourceInventoryRepository inventoryRepository;

    private UUID resourceId;

    @BeforeEach
    void setUp() {
        resourceId = UUID.randomUUID();

        ResourceInventory inventory1 = ResourceInventory.builder()
                .resourceId(resourceId)
                .location("Central Pharmacy")
                .currentStock(500)
                .batchNumber("BATCH-1001")
                .expirationDate(LocalDate.now().plusMonths(6))
                .build();

        ResourceInventory inventory2 = ResourceInventory.builder()
                .resourceId(resourceId)
                .location("Ward A Pharmacy")
                .currentStock(150)
                .batchNumber("BATCH-1002")
                .expirationDate(LocalDate.now().minusDays(1))
                .build();

        inventoryRepository.saveAll(List.of(inventory1, inventory2));
    }

    @Test
    void sumStockByResourceId_ReturnsCorrectSum() {
        Integer total = inventoryRepository.sumStockByResourceId(resourceId);
        assertThat(total).isEqualTo(650);
    }

    @Test
    void findExpiredBatches_ReturnsExpiredItems() {
        List<ResourceInventory> expired = inventoryRepository.findExpiredBatches(LocalDate.now());
        assertThat(expired).hasSize(1);
        assertThat(expired.get(0).getBatchNumber()).isEqualTo("BATCH-1002");
    }
}
