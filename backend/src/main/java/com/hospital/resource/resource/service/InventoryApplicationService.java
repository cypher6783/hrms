package com.hospital.resource.resource.service;

import com.hospital.resource.common.event.DomainEventPublisher;
import com.hospital.resource.common.event.inventory.InventoryExpiredEvent;
import com.hospital.resource.common.event.inventory.InventoryLowStockEvent;
import com.hospital.resource.common.event.inventory.InventoryUpdatedEvent;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.resource.domain.InventoryDomainService;
import com.hospital.resource.resource.dto.*;
import com.hospital.resource.resource.entity.InventoryTransaction;
import com.hospital.resource.resource.entity.Resource;
import com.hospital.resource.resource.entity.ResourceInventory;
import com.hospital.resource.resource.entity.ResourceSupplier;
import com.hospital.resource.resource.mapper.InventoryMapper;
import com.hospital.resource.resource.mapper.SupplierMapper;
import com.hospital.resource.resource.repository.InventoryTransactionRepository;
import com.hospital.resource.resource.repository.ResourceInventoryRepository;
import com.hospital.resource.resource.repository.ResourceRepository;
import com.hospital.resource.resource.repository.ResourceSupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class InventoryApplicationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(InventoryApplicationService.class);

    private final ResourceInventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceSupplierRepository supplierRepository;
    private final InventoryMapper inventoryMapper;
    private final SupplierMapper supplierMapper;
    private final DomainEventPublisher eventPublisher;

    private final InventoryDomainService inventoryDomainService = new InventoryDomainService();

    public InventoryApplicationService(
            ResourceInventoryRepository inventoryRepository,
            InventoryTransactionRepository transactionRepository,
            ResourceRepository resourceRepository,
            ResourceSupplierRepository supplierRepository,
            InventoryMapper inventoryMapper,
            SupplierMapper supplierMapper,
            DomainEventPublisher eventPublisher) {
        this.inventoryRepository = inventoryRepository;
        this.transactionRepository = transactionRepository;
        this.resourceRepository = resourceRepository;
        this.supplierRepository = supplierRepository;
        this.inventoryMapper = inventoryMapper;
        this.supplierMapper = supplierMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public InventoryTransactionResponse recordTransaction(InventoryTransactionRequest request, UUID performedBy) {
        ResourceInventory inventory = inventoryRepository.findById(request.resourceInventoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", request.resourceInventoryId().toString()));

        Resource resource = resourceRepository.findById(inventory.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource", inventory.getResourceId().toString()));

        String type = request.transactionType().toUpperCase();

        if ("OUT".equals(type) || "EXPIRED_DISCARD".equals(type)) {
            inventoryDomainService.validateNonNegativeStock(inventory.getCurrentStock(), request.quantity());
            if ("OUT".equals(type)) {
                inventoryDomainService.validateNotExpired(inventory);
            }
            inventory.setCurrentStock(inventory.getCurrentStock() - request.quantity());
        } else if ("IN".equals(type) || "RETURN".equals(type)) {
            inventory.setCurrentStock(inventory.getCurrentStock() + request.quantity());
        } else if ("ADJUSTMENT".equals(type)) {
            if (request.quantity() < 0) {
                inventoryDomainService.validateNonNegativeStock(inventory.getCurrentStock(), Math.abs(request.quantity()));
            }
            inventory.setCurrentStock(inventory.getCurrentStock() + request.quantity());
        }

        inventoryRepository.save(inventory);

        InventoryTransaction transaction = InventoryTransaction.builder()
                .resourceInventoryId(request.resourceInventoryId())
                .transactionType(type)
                .quantity(request.quantity())
                .admissionId(request.admissionId())
                .referenceDocument(request.referenceDocument())
                .notes(request.notes())
                .performedBy(performedBy)
                .transactionTimestamp(Instant.now())
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Inventory transaction recorded: type={}, quantity={}, inventoryId={}", type, request.quantity(), inventory.getId());

        eventPublisher.publish(new InventoryUpdatedEvent(this, inventory.getId(), resource.getId(), type, request.quantity(), inventory.getCurrentStock()));

        // Low stock detection check
        Integer totalStock = inventoryRepository.sumStockByResourceId(resource.getId());
        if (inventoryDomainService.isLowStock(totalStock, resource.getMinimumThreshold())) {
            log.warn("Low stock detected for resource {}: current total={}, minimum threshold={}", resource.getName(), totalStock, resource.getMinimumThreshold());
            eventPublisher.publish(new InventoryLowStockEvent(this, resource.getId(), resource.getName(), totalStock, resource.getMinimumThreshold()));
        }

        return inventoryMapper.toTransactionResponse(transaction);
    }

    @Transactional(readOnly = true)
    public InventoryStockResponse getStock(UUID resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", resourceId.toString()));
        Integer totalStock = inventoryRepository.sumStockByResourceId(resourceId);
        int currentTotal = totalStock != null ? totalStock : 0;

        return new InventoryStockResponse(
                resourceId,
                resource.getName(),
                currentTotal,
                resource.getMinimumThreshold(),
                currentTotal <= resource.getMinimumThreshold()
        );
    }

    @Transactional(readOnly = true)
    public List<InventoryTransactionResponse> getTransactionHistory(UUID inventoryId) {
        return inventoryMapper.toTransactionResponseList(
                transactionRepository.findByResourceInventoryIdOrderByTransactionTimestampDesc(inventoryId)
        );
    }

    @Transactional
    public List<InventoryTransactionResponse> processExpiredBatches(UUID performedBy) {
        List<ResourceInventory> expiredBatches = inventoryRepository.findExpiredBatches(LocalDate.now());
        return expiredBatches.stream().map(batch -> {
            int stockToDiscard = batch.getCurrentStock();
            batch.setCurrentStock(0);
            inventoryRepository.save(batch);

            InventoryTransaction transaction = InventoryTransaction.builder()
                    .resourceInventoryId(batch.getId())
                    .transactionType("EXPIRED_DISCARD")
                    .quantity(stockToDiscard)
                    .notes("Automated expiry write-off")
                    .performedBy(performedBy)
                    .transactionTimestamp(Instant.now())
                    .build();

            transaction = transactionRepository.save(transaction);
            eventPublisher.publish(new InventoryExpiredEvent(this, batch.getId(), batch.getResourceId(), batch.getBatchNumber(), stockToDiscard, batch.getExpirationDate()));
            return inventoryMapper.toTransactionResponse(transaction);
        }).toList();
    }

    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request, UUID userId) {
        ResourceSupplier supplier = supplierMapper.toEntity(request);
        supplier.setIsActive(true);
        supplier.setCreatedBy(userId);
        supplier.setUpdatedBy(userId);
        supplier = supplierRepository.save(supplier);
        log.info("Supplier created: supplierId={}, name={}", supplier.getId(), supplier.getName());
        return supplierMapper.toResponse(supplier);
    }

    @Transactional(readOnly = true)
    public List<SupplierResponse> getAllSuppliers() {
        return supplierMapper.toResponseList(supplierRepository.findByIsActiveTrue());
    }

    @Transactional
    public SupplierResponse updateSupplier(UUID id, SupplierRequest request, UUID userId) {
        ResourceSupplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id.toString()));

        supplierMapper.updateEntity(request, supplier);
        supplier.setUpdatedBy(userId);
        supplier = supplierRepository.save(supplier);
        log.info("Supplier updated: supplierId={}", supplier.getId());
        return supplierMapper.toResponse(supplier);
    }
}
