package com.hospital.resource.resource;

import com.hospital.resource.common.event.DomainEventPublisher;
import com.hospital.resource.resource.dto.*;
import com.hospital.resource.resource.entity.Resource;
import com.hospital.resource.resource.entity.ResourceAllocation;
import com.hospital.resource.resource.entity.ResourceReservation;
import com.hospital.resource.resource.mapper.ResourceMapper;
import com.hospital.resource.resource.repository.ResourceAllocationRepository;
import com.hospital.resource.resource.repository.ResourceInventoryRepository;
import com.hospital.resource.resource.repository.ResourceRepository;
import com.hospital.resource.resource.repository.ResourceReservationRepository;
import com.hospital.resource.resource.repository.ResourceSupplierRepository;
import com.hospital.resource.resource.service.ResourceApplicationService;
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
class ResourceApplicationServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ResourceSupplierRepository supplierRepository;

    @Mock
    private ResourceAllocationRepository allocationRepository;

    @Mock
    private ResourceReservationRepository reservationRepository;

    @Mock
    private ResourceInventoryRepository inventoryRepository;

    @Mock
    private ResourceMapper resourceMapper;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private ResourceApplicationService resourceService;

    private UUID resourceId;
    private UUID userId;
    private Resource resource;

    @BeforeEach
    void setUp() {
        resourceId = UUID.randomUUID();
        userId = UUID.randomUUID();

        resource = Resource.builder()
                .id(resourceId)
                .name("Surgical Gloves")
                .category("PPE")
                .unitOfMeasure("PAIR")
                .minimumThreshold(50)
                .reorderPoint(100)
                .criticalityLevel("HIGH")
                .build();
    }

    @Test
    void createResource_Success() {
        ResourceRequest request = new ResourceRequest("Surgical Gloves", "PPE", "PAIR", 50, 100, "HIGH", null);
        ResourceResponse response = new ResourceResponse(resourceId, "Surgical Gloves", "PPE", "PAIR", 50, 100, "HIGH", null, Instant.now(), Instant.now());

        when(resourceRepository.existsByNameAndCategory("Surgical Gloves", "PPE")).thenReturn(false);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);
        when(resourceMapper.toResponse(resource)).thenReturn(response);

        ResourceResponse result = resourceService.createResource(request, userId);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Surgical Gloves");
        verify(resourceRepository).save(any(Resource.class));
    }

    @Test
    void reserveResource_Success() {
        ResourceReservationRequest request = new ResourceReservationRequest(resourceId, UUID.randomUUID(), 10, 30);
        ResourceReservation reservation = ResourceReservation.builder()
                .id(UUID.randomUUID())
                .resourceId(resourceId)
                .quantity(10)
                .status("RESERVED")
                .build();
        ResourceReservationResponse response = new ResourceReservationResponse(reservation.getId(), resourceId, request.admissionId(), 10, "RESERVED", Instant.now(), Instant.now().plusSeconds(1800), userId);

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(inventoryRepository.sumStockByResourceId(resourceId)).thenReturn(100);
        when(allocationRepository.sumActiveAllocatedQuantityByResourceId(resourceId)).thenReturn(20);
        when(reservationRepository.sumActiveReservedQuantityByResourceId(eq(resourceId), any())).thenReturn(10);
        when(reservationRepository.save(any(ResourceReservation.class))).thenReturn(reservation);
        when(resourceMapper.toReservationResponse(reservation)).thenReturn(response);

        ResourceReservationResponse result = resourceService.reserveResource(request, userId);

        assertThat(result).isNotNull();
        assertThat(result.quantity()).isEqualTo(10);
        verify(eventPublisher).publish(any());
    }
}
