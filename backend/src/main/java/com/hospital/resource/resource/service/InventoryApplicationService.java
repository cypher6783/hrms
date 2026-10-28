package com.hospital.resource.resource.service;

import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.common.exception.ValidationException;
import com.hospital.resource.resource.dto.*;
import com.hospital.resource.resource.entity.InventoryTransaction;
import com.hospital.resource.resource.entity.Resource;
import com.hospital.resource.resource.entity.ResourceInventory;
import com.hospital.resource.resource.repository.InventoryTransactionRepository;
import com.hospital.resource.resource.repository.ResourceInventoryRepository;
import com.hospital.resource.resource.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryApplicationService {

    private final ResourceInventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final ResourceRepository resourceRepository;

    @Transactional
    public InventoryTransactionResponse recordTransaction(InventoryTransactionRequest request, UUID performedBy) {
        ResourceInventory inventory = inventoryRepository.findById(request.resourceInventoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", request.resourceInventoryId().toString()));

        if ("OUT".equals(request.transactionType()) && inventory.getCurrentStock() < request.quantity()) {
            throw new ValidationException("Insufficient stock. Available: " + inventory.getCurrentStock());
        }

        // Update stock
        if ("IN".equals(request.transactionType())) {
            inventory.setCurrentStock(inventory.getCurrentStock() + request.quantity());
        } else {
            inventory.setCurrentStock(inventory.getCurrentStock() - request.quantity());
        }
        inventoryRepository.save(inventory);

        // Record transaction
        InventoryTransaction transaction = InventoryTransaction.builder()
                .resourceInventoryId(request.resourceInventoryId())
                .transactionType(request.transactionType())
                .quantity(request.quantity())
                .admissionId(request.admissionId())
                .referenceDocument(request.referenceDocument())
                .notes(request.notes())
                .performedBy(performedBy)
                .transactionTimestamp(Instant.now())
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Inventory transaction: type={}, quantity={}, inventoryId={}", request.transactionType(), request.quantity(), request.resourceInventoryId());
        return toTransactionResponse(transaction);
    }

    @Transactional(readOnly = true)
    public InventoryStockResponse getStock(UUID resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", resourceId.toString()));
        Integer totalStock = inventoryRepository.sumStockByResourceId(resourceId);

        return new InventoryStockResponse(
                resourceId,
                resource.getName(),
                totalStock != null ? totalStock : 0,
                resource.getMinimumThreshold(),
                (totalStock != null ? totalStock : 0) < resource.getMinimumThreshold()
        );
    }

    @Transactional(readOnly = true)
    public List<InventoryTransactionResponse> getTransactionHistory(UUID inventoryId) {
        return transactionRepository.findByResourceInventoryIdOrderByTransactionTimestampDesc(inventoryId).stream()
                .map(this::toTransactionResponse)
                .toList();
    }

    private InventoryTransactionResponse toTransactionResponse(InventoryTransaction transaction) {
        return new InventoryTransactionResponse(
                transaction.getId(), transaction.getResourceInventoryId(),
                transaction.getTransactionType(), transaction.getQuantity(),
                transaction.getAdmissionId(), transaction.getReferenceDocument(),
                transaction.getNotes(), transaction.getPerformedBy(),
                transaction.getTransactionTimestamp(), transaction.getCreatedAt()
        );
    }
}
