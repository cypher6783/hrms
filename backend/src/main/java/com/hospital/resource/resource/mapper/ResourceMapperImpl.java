package com.hospital.resource.resource.mapper;

import com.hospital.resource.resource.dto.*;
import com.hospital.resource.resource.entity.Resource;
import com.hospital.resource.resource.entity.ResourceAllocation;
import com.hospital.resource.resource.entity.ResourceReservation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResourceMapperImpl implements ResourceMapper {

    @Override
    public Resource toEntity(ResourceRequest request) {
        if (request == null) return null;
        return Resource.builder()
                .name(request.name())
                .category(request.category())
                .unitOfMeasure(request.unitOfMeasure())
                .minimumThreshold(request.minimumThreshold())
                .reorderPoint(request.reorderPoint())
                .criticalityLevel(request.criticalityLevel() != null ? request.criticalityLevel() : "NORMAL")
                .specificationDetails(request.specificationDetails())
                .build();
    }

    @Override
    public ResourceResponse toResponse(Resource resource) {
        if (resource == null) return null;
        return new ResourceResponse(
                resource.getId(),
                resource.getName(),
                resource.getCategory(),
                resource.getUnitOfMeasure(),
                resource.getMinimumThreshold(),
                resource.getReorderPoint(),
                resource.getCriticalityLevel(),
                resource.getSpecificationDetails(),
                resource.getCreatedAt(),
                resource.getUpdatedAt()
        );
    }

    @Override
    public List<ResourceResponse> toResponseList(List<Resource> resources) {
        if (resources == null) return List.of();
        return resources.stream().map(this::toResponse).toList();
    }

    @Override
    public void updateEntity(ResourceRequest request, Resource resource) {
        if (request == null || resource == null) return;
        if (request.name() != null) resource.setName(request.name());
        if (request.category() != null) resource.setCategory(request.category());
        if (request.unitOfMeasure() != null) resource.setUnitOfMeasure(request.unitOfMeasure());
        if (request.minimumThreshold() != null) resource.setMinimumThreshold(request.minimumThreshold());
        if (request.reorderPoint() != null) resource.setReorderPoint(request.reorderPoint());
        if (request.criticalityLevel() != null) resource.setCriticalityLevel(request.criticalityLevel());
        if (request.specificationDetails() != null) resource.setSpecificationDetails(request.specificationDetails());
    }

    @Override
    public ResourceAllocationResponse toAllocationResponse(ResourceAllocation allocation) {
        if (allocation == null) return null;
        return new ResourceAllocationResponse(
                allocation.getId(),
                allocation.getResourceId(),
                allocation.getAdmissionId(),
                allocation.getQuantity(),
                allocation.getAllocatedAt(),
                allocation.getReleasedAt(),
                allocation.getAllocatedBy()
        );
    }

    @Override
    public List<ResourceAllocationResponse> toAllocationResponseList(List<ResourceAllocation> allocations) {
        if (allocations == null) return List.of();
        return allocations.stream().map(this::toAllocationResponse).toList();
    }

    @Override
    public ResourceReservationResponse toReservationResponse(ResourceReservation reservation) {
        if (reservation == null) return null;
        return new ResourceReservationResponse(
                reservation.getId(),
                reservation.getResourceId(),
                reservation.getAdmissionId(),
                reservation.getQuantity(),
                reservation.getStatus(),
                reservation.getReservedAt(),
                reservation.getExpiresAt(),
                reservation.getReservedBy()
        );
    }

    @Override
    public List<ResourceReservationResponse> toReservationResponseList(List<ResourceReservation> reservations) {
        if (reservations == null) return List.of();
        return reservations.stream().map(this::toReservationResponse).toList();
    }
}
