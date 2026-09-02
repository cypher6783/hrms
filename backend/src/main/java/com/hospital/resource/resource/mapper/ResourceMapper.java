package com.hospital.resource.resource.mapper;

import com.hospital.resource.resource.dto.*;
import com.hospital.resource.resource.entity.Resource;
import com.hospital.resource.resource.entity.ResourceAllocation;
import com.hospital.resource.resource.entity.ResourceReservation;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

public interface ResourceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Resource toEntity(ResourceRequest request);

    ResourceResponse toResponse(Resource resource);

    List<ResourceResponse> toResponseList(List<Resource> resources);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(ResourceRequest request, @MappingTarget Resource resource);

    ResourceAllocationResponse toAllocationResponse(ResourceAllocation allocation);

    List<ResourceAllocationResponse> toAllocationResponseList(List<ResourceAllocation> allocations);

    ResourceReservationResponse toReservationResponse(ResourceReservation reservation);

    List<ResourceReservationResponse> toReservationResponseList(List<ResourceReservation> reservations);
}
