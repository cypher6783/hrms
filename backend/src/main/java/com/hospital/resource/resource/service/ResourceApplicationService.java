package com.hospital.resource.resource.service;

import com.hospital.resource.common.event.DomainEventPublisher;
import com.hospital.resource.common.event.resource.ResourceAllocatedEvent;
import com.hospital.resource.common.event.resource.ResourceReleasedEvent;
import com.hospital.resource.common.event.resource.ResourceReservedEvent;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.resource.domain.ResourceDomainService;
import com.hospital.resource.resource.dto.*;
import com.hospital.resource.resource.entity.Resource;
import com.hospital.resource.resource.entity.ResourceAllocation;
import com.hospital.resource.resource.entity.ResourceReservation;
import com.hospital.resource.resource.entity.ResourceSupplier;
import com.hospital.resource.resource.mapper.ResourceMapper;
import com.hospital.resource.resource.repository.ResourceAllocationRepository;
import com.hospital.resource.resource.repository.ResourceInventoryRepository;
import com.hospital.resource.resource.repository.ResourceRepository;
import com.hospital.resource.resource.repository.ResourceReservationRepository;
import com.hospital.resource.resource.repository.ResourceSupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ResourceApplicationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ResourceApplicationService.class);

    private final ResourceRepository resourceRepository;
    private final ResourceSupplierRepository supplierRepository;
    private final ResourceAllocationRepository allocationRepository;
    private final ResourceReservationRepository reservationRepository;
    private final ResourceInventoryRepository inventoryRepository;
    private final ResourceMapper resourceMapper;
    private final DomainEventPublisher eventPublisher;

    private final ResourceDomainService resourceDomainService = new ResourceDomainService();

    public ResourceApplicationService(
            ResourceRepository resourceRepository,
            ResourceSupplierRepository supplierRepository,
            ResourceAllocationRepository allocationRepository,
            ResourceReservationRepository reservationRepository,
            ResourceInventoryRepository inventoryRepository,
            ResourceMapper resourceMapper,
            DomainEventPublisher eventPublisher) {
        this.resourceRepository = resourceRepository;
        this.supplierRepository = supplierRepository;
        this.allocationRepository = allocationRepository;
        this.reservationRepository = reservationRepository;
        this.inventoryRepository = inventoryRepository;
        this.resourceMapper = resourceMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ResourceResponse createResource(ResourceRequest request, UUID userId) {
        boolean exists = resourceRepository.existsByNameAndCategory(request.name(), request.category());
        resourceDomainService.validateResourceUniqueness(exists);

        Resource resource = Resource.builder()
                .name(request.name())
                .category(request.category())
                .unitOfMeasure(request.unitOfMeasure())
                .minimumThreshold(request.minimumThreshold() != null ? request.minimumThreshold() : 0)
                .reorderPoint(request.reorderPoint() != null ? request.reorderPoint() : 0)
                .criticalityLevel(request.criticalityLevel() != null ? request.criticalityLevel() : "NORMAL")
                .defaultSupplierId(request.defaultSupplierId())
                .createdBy(userId)
                .updatedBy(userId)
                .build();

        resource = resourceRepository.save(resource);
        log.info("Resource created: resourceId={}, name={}, category={}", resource.getId(), resource.getName(), resource.getCategory());
        return resourceMapper.toResponse(resource);
    }

    @Transactional(readOnly = true)
    public ResourceResponse getResource(UUID id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", id.toString()));
        return resourceMapper.toResponse(resource);
    }

    @Transactional(readOnly = true)
    public List<ResourceResponse> getAllResources() {
        return resourceMapper.toResponseList(resourceRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<ResourceResponse> getResourcesByCategory(String category) {
        return resourceMapper.toResponseList(resourceRepository.findByCategory(category));
    }

    @Transactional
    public ResourceResponse updateResource(UUID id, ResourceRequest request, UUID userId) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", id.toString()));

        if (!resource.getName().equalsIgnoreCase(request.name()) || !resource.getCategory().equalsIgnoreCase(request.category())) {
            boolean exists = resourceRepository.existsByNameAndCategory(request.name(), request.category());
            resourceDomainService.validateResourceUniqueness(exists);
        }

        resource.setName(request.name());
        resource.setCategory(request.category());
        resource.setUnitOfMeasure(request.unitOfMeasure());
        resource.setMinimumThreshold(request.minimumThreshold());
        resource.setReorderPoint(request.reorderPoint());
        resource.setCriticalityLevel(request.criticalityLevel());
        resource.setDefaultSupplierId(request.defaultSupplierId());
        resource.setUpdatedBy(userId);

        resource = resourceRepository.save(resource);
        log.info("Resource updated: resourceId={}", resource.getId());
        return resourceMapper.toResponse(resource);
    }

    @Transactional
    public ResourceReservationResponse reserveResource(ResourceReservationRequest request, UUID userId) {
        Resource resource = resourceRepository.findById(request.resourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource", request.resourceId().toString()));

        Integer currentStock = inventoryRepository.sumStockByResourceId(request.resourceId());
        if (currentStock == null) currentStock = 0;

        Integer activeAllocations = allocationRepository.sumActiveAllocatedQuantityByResourceId(request.resourceId());
        Integer activeReservations = reservationRepository.sumActiveReservedQuantityByResourceId(request.resourceId(), Instant.now());

        resourceDomainService.validateReservationAvailability(currentStock, activeAllocations, activeReservations, request.quantity());

        Instant expiresAt = null;
        if (request.expirationMinutes() != null && request.expirationMinutes() > 0) {
            expiresAt = Instant.now().plus(request.expirationMinutes(), ChronoUnit.MINUTES);
        }

        ResourceReservation reservation = ResourceReservation.builder()
                .resourceId(request.resourceId())
                .admissionId(request.admissionId())
                .quantity(request.quantity())
                .status("RESERVED")
                .reservedAt(Instant.now())
                .expiresAt(expiresAt)
                .reservedBy(userId)
                .createdBy(userId)
                .updatedBy(userId)
                .build();

        reservation = reservationRepository.save(reservation);
        log.info("Resource reserved: reservationId={}, resourceId={}, quantity={}", reservation.getId(), request.resourceId(), request.quantity());

        eventPublisher.publish(new ResourceReservedEvent(this, reservation.getId(), request.resourceId(), request.admissionId(), request.quantity(), userId));

        return resourceMapper.toReservationResponse(reservation);
    }

    @Transactional
    public ResourceAllocationResponse allocateResource(ResourceAllocationRequest request, UUID userId) {
        Resource resource = resourceRepository.findById(request.resourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource", request.resourceId().toString()));

        if (request.reservationId() != null) {
            ResourceReservation reservation = reservationRepository.findById(request.reservationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation", request.reservationId().toString()));
            resourceDomainService.validateReservationActive(reservation);
            reservation.setStatus("ALLOCATED");
            reservationRepository.save(reservation);
        } else {
            Integer currentStock = inventoryRepository.sumStockByResourceId(request.resourceId());
            if (currentStock == null) currentStock = 0;
            Integer activeAllocations = allocationRepository.sumActiveAllocatedQuantityByResourceId(request.resourceId());

            resourceDomainService.validateAllocationAvailability(currentStock, activeAllocations, request.quantity());
        }

        ResourceAllocation allocation = ResourceAllocation.builder()
                .resourceId(request.resourceId())
                .admissionId(request.admissionId())
                .quantity(request.quantity())
                .allocatedAt(Instant.now())
                .allocatedBy(userId)
                .build();

        allocation = allocationRepository.save(allocation);
        log.info("Resource allocated: allocationId={}, resourceId={}, admissionId={}, quantity={}", allocation.getId(), request.resourceId(), request.admissionId(), request.quantity());

        eventPublisher.publish(new ResourceAllocatedEvent(this, allocation.getId(), request.resourceId(), request.admissionId(), request.quantity(), userId));

        return resourceMapper.toAllocationResponse(allocation);
    }

    @Transactional
    public ResourceAllocationResponse releaseResource(UUID allocationId, UUID userId) {
        ResourceAllocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation", allocationId.toString()));

        resourceDomainService.validateAllocationActive(allocation);

        allocation.setReleasedAt(Instant.now());
        allocation = allocationRepository.save(allocation);
        log.info("Resource released: allocationId={}, resourceId={}", allocation.getId(), allocation.getResourceId());

        eventPublisher.publish(new ResourceReleasedEvent(this, allocation.getId(), allocation.getResourceId(), allocation.getAdmissionId(), allocation.getQuantity(), userId));

        return resourceMapper.toAllocationResponse(allocation);
    }

    @Transactional(readOnly = true)
    public ResourceUtilizationResponse getUtilizationMetrics(UUID resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", resourceId.toString()));

        Integer totalStock = inventoryRepository.sumStockByResourceId(resourceId);
        if (totalStock == null) totalStock = 0;

        Integer activeAllocated = allocationRepository.sumActiveAllocatedQuantityByResourceId(resourceId);
        Integer activeReserved = reservationRepository.sumActiveReservedQuantityByResourceId(resourceId, Instant.now());

        int available = Math.max(0, totalStock - activeAllocated - activeReserved);
        double utilizationRate = totalStock > 0 ? ((double) (activeAllocated + activeReserved) / totalStock) * 100.0 : 0.0;

        return new ResourceUtilizationResponse(
                resource.getId(),
                resource.getName(),
                resource.getCategory(),
                totalStock,
                activeAllocated,
                activeReserved,
                available,
                Math.round(utilizationRate * 100.0) / 100.0
        );
    }
}
