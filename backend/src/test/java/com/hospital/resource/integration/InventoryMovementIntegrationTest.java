package com.hospital.resource.integration;

import com.hospital.resource.resource.dto.InventoryStockResponse;
import com.hospital.resource.resource.dto.InventoryTransactionRequest;
import com.hospital.resource.resource.dto.InventoryTransactionResponse;
import com.hospital.resource.resource.entity.Resource;
import com.hospital.resource.resource.entity.ResourceInventory;
import com.hospital.resource.resource.repository.ResourceInventoryRepository;
import com.hospital.resource.resource.repository.ResourceRepository;
import com.hospital.resource.resource.service.InventoryApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class InventoryMovementIntegrationTest {

    @Autowired
    private InventoryApplicationService inventoryService;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceInventoryRepository inventoryRepository;

    private Resource testResource;
    private ResourceInventory inventory;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        testResource = Resource.builder()
                .name("Antibiotic Injection " + UUID.randomUUID().toString().substring(0, 5))
                .category("MEDICATION")
                .unitOfMeasure("VIAL")
                .minimumThreshold(50)
                .reorderPoint(100)
                .criticalityLevel("CRITICAL")
                .createdBy(userId)
                .build();
        testResource = resourceRepository.save(testResource);

        inventory = ResourceInventory.builder()
                .resourceId(testResource.getId())
                .location("Central Pharmacy")
                .currentStock(100)
                .batchNumber("BATCH-INI-001")
                .expirationDate(LocalDate.now().plusMonths(12))
                .build();
        inventory = inventoryRepository.save(inventory);
    }

    @Test
    void stockMovementAndLowStockDetectionWorkflow() {
        // 1. Stock OUT transaction
        InventoryTransactionRequest outRequest = new InventoryTransactionRequest(
                inventory.getId(), "OUT", 60, null, "DISPENSE-001", "Dispensed to ward"
        );
        InventoryTransactionResponse outTx = inventoryService.recordTransaction(outRequest, userId);

        assertThat(outTx).isNotNull();
        assertThat(outTx.quantity()).isEqualTo(60);

        // 2. Verify low stock alert status
        InventoryStockResponse stock = inventoryService.getStock(testResource.getId());
        assertThat(stock.totalStock()).isEqualTo(40);
        assertThat(stock.isBelowThreshold()).isTrue();

        // 3. Stock IN transaction
        InventoryTransactionRequest inRequest = new InventoryTransactionRequest(
                inventory.getId(), "IN", 100, null, "PO-2002", "Restock"
        );
        inventoryService.recordTransaction(inRequest, userId);

        InventoryStockResponse postRestock = inventoryService.getStock(testResource.getId());
        assertThat(postRestock.totalStock()).isEqualTo(140);
        assertThat(postRestock.isBelowThreshold()).isFalse();
    }

    @Test
    void processExpiredBatchesWorkflow() {
        // Create an expired inventory batch
        ResourceInventory expiredBatch = ResourceInventory.builder()
                .resourceId(testResource.getId())
                .location("Side Store")
                .currentStock(30)
                .batchNumber("BATCH-EXP-999")
                .expirationDate(LocalDate.now().minusDays(5))
                .build();
        inventoryRepository.save(expiredBatch);

        // Process expired batches
        List<InventoryTransactionResponse> writeOffs = inventoryService.processExpiredBatches(userId);

        assertThat(writeOffs).isNotEmpty();
        ResourceInventory updatedExpiredBatch = inventoryRepository.findById(expiredBatch.getId()).orElseThrow();
        assertThat(updatedExpiredBatch.getCurrentStock()).isEqualTo(0);
    }
}
