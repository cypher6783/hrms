package com.hospital.resource.resource;

import com.hospital.resource.common.event.DomainEventPublisher;
import com.hospital.resource.resource.dto.InventoryTransactionRequest;
import com.hospital.resource.resource.dto.InventoryTransactionResponse;
import com.hospital.resource.resource.entity.InventoryTransaction;
import com.hospital.resource.resource.entity.Resource;
import com.hospital.resource.resource.entity.ResourceInventory;
import com.hospital.resource.resource.mapper.InventoryMapper;
import com.hospital.resource.resource.mapper.SupplierMapper;
import com.hospital.resource.resource.repository.InventoryTransactionRepository;
import com.hospital.resource.resource.repository.ResourceInventoryRepository;
import com.hospital.resource.resource.repository.ResourceRepository;
import com.hospital.resource.resource.repository.ResourceSupplierRepository;
import com.hospital.resource.resource.service.InventoryApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryApplicationServiceTest {

    @Mock
    private ResourceInventoryRepository inventoryRepository;

    @Mock
    private InventoryTransactionRepository transactionRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ResourceSupplierRepository supplierRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    @Mock
    private SupplierMapper supplierMapper;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private InventoryApplicationService inventoryService;

    private UUID inventoryId;
    private UUID resourceId;
    private UUID userId;
    private ResourceInventory inventory;
    private Resource resource;

    @BeforeEach
    void setUp() {
        inventoryId = UUID.randomUUID();
        resourceId = UUID.randomUUID();
        userId = UUID.randomUUID();

        inventory = ResourceInventory.builder()
                .id(inventoryId)
                .resourceId(resourceId)
                .location("Central Store")
                .currentStock(100)
                .build();

        resource = Resource.builder()
                .id(resourceId)
                .name("Syringes")
                .minimumThreshold(20)
                .build();
    }

    @Test
    void recordTransaction_StockIn_Success() {
        InventoryTransactionRequest request = new InventoryTransactionRequest(inventoryId, "IN", 50, null, "PO-001", "Restock");
        InventoryTransaction transaction = InventoryTransaction.builder().id(UUID.randomUUID()).resourceInventoryId(inventoryId).quantity(50).transactionType("IN").build();
        InventoryTransactionResponse response = new InventoryTransactionResponse(transaction.getId(), inventoryId, "IN", 50, null, "PO-001", "Restock", userId, Instant.now(), Instant.now());

        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(transactionRepository.save(any())).thenReturn(transaction);
        when(inventoryMapper.toTransactionResponse(transaction)).thenReturn(response);

        InventoryTransactionResponse result = inventoryService.recordTransaction(request, userId);

        assertThat(result).isNotNull();
        assertThat(inventory.getCurrentStock()).isEqualTo(150);
        verify(eventPublisher, atLeastOnce()).publish(any());
    }
}
