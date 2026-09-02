package com.hospital.resource.resource.mapper;

import com.hospital.resource.resource.dto.SupplierRequest;
import com.hospital.resource.resource.dto.SupplierResponse;
import com.hospital.resource.resource.entity.ResourceSupplier;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

public interface SupplierMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    ResourceSupplier toEntity(SupplierRequest request);

    SupplierResponse toResponse(ResourceSupplier supplier);

    List<SupplierResponse> toResponseList(List<ResourceSupplier> suppliers);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(SupplierRequest request, @MappingTarget ResourceSupplier supplier);
}
