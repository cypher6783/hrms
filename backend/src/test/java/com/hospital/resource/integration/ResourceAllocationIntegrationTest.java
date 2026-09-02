package com.hospital.resource.integration;

import com.hospital.resource.resource.dto.*;
import com.hospital.resource.resource.entity.Resource;
import com.hospital.resource.resource.entity.ResourceInventory;
import com.hospital.resource.resource.repository.ResourceAllocationRepository;
import com.hospital.resource.resource.repository.ResourceInventoryRepository;
import com.hospital.resource.resource.repository.ResourceRepository;
import com.hospital.resource.resource.service.ResourceApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ResourceAllocationIntegrationTest {

    @Autowired
    private ResourceApplicationService resourceService;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceInventoryRepository inventoryRepository;

    @Autowired
    private ResourceAllocationRepository allocationRepository;

    private Resource testResource;
    private UUID admissionId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        admissionId = UUID.randomUUID();

        testResource = Resource.builder()
                .name("Oxygen Mask " + UUID.randomUUID().toString().substring(0, 5))
                .category("CONSUMABLE")
                .unitOfMeasure("PIECE")
                .minimumThreshold(10)
                .reorderPoint(20)
                .criticalityLevel("HIGH")
                .createdBy(userId)
                .build();
        testResource = resourceRepository.save(testResource);

        ResourceInventory inventory = ResourceInventory.builder()
                .resourceId(testResource.getId())
                .location("Central Store")
                .currentStock(100)
                .build();
        inventoryRepository.save(inventory);
    }

    @Test
    void fullResourceReservationAndAllocationWorkflow() {
        // 1. Reserve resource
        ResourceReservationRequest reservationRequest = new ResourceReservationRequest(
                testResource.getId(), admissionId, 15, 60
        );
        ResourceReservationResponse reservation = resourceService.reserveResource(reservationRequest, userId);

        assertThat(reservation).isNotNull();
        assertThat(reservation.status()).isEqualTo("RESERVED");

        // 2. Allocate resource using reservation
        ResourceAllocationRequest allocationRequest = new ResourceAllocationRequest(
                testResource.getId(), admissionId, 15, reservation.id()
        );
        ResourceAllocationResponse allocation = resourceService.allocateResource(allocationRequest, userId);

        assertThat(allocation).isNotNull();
        assertThat(allocation.quantity()).isEqualTo(15);
        assertThat(allocation.releasedAt()).isNull();

        // 3. Verify utilization metric
        ResourceUtilizationResponse utilization = resourceService.getUtilizationMetrics(testResource.getId());
        assertThat(utilization.allocatedQuantity()).isEqualTo(15);

        // 4. Release resource
        ResourceAllocationResponse released = resourceService.releaseResource(allocation.id(), userId);
        assertThat(released.releasedAt()).isNotNull();

        ResourceUtilizationResponse postReleaseUtilization = resourceService.getUtilizationMetrics(testResource.getId());
        assertThat(postReleaseUtilization.allocatedQuantity()).isEqualTo(0);
    }
}
