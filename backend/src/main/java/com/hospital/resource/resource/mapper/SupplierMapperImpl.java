package com.hospital.resource.resource.mapper;

import com.hospital.resource.resource.dto.SupplierRequest;
import com.hospital.resource.resource.dto.SupplierResponse;
import com.hospital.resource.resource.entity.ResourceSupplier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SupplierMapperImpl implements SupplierMapper {

    @Override
    public ResourceSupplier toEntity(SupplierRequest request) {
        if (request == null) return null;
        return ResourceSupplier.builder()
                .name(request.name())
                .contactPerson(request.contactPerson())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .address(request.address())
                .build();
    }

    @Override
    public SupplierResponse toResponse(ResourceSupplier supplier) {
        if (supplier == null) return null;
        return new SupplierResponse(
                supplier.getId(),
                supplier.getName(),
                supplier.getContactPerson(),
                supplier.getEmail(),
                supplier.getPhoneNumber(),
                supplier.getAddress(),
                supplier.getIsActive(),
                supplier.getCreatedAt(),
                supplier.getUpdatedAt()
        );
    }

    @Override
    public List<SupplierResponse> toResponseList(List<ResourceSupplier> suppliers) {
        if (suppliers == null) return List.of();
        return suppliers.stream().map(this::toResponse).toList();
    }

    @Override
    public void updateEntity(SupplierRequest request, ResourceSupplier supplier) {
        if (request == null || supplier == null) return;
        if (request.name() != null) supplier.setName(request.name());
        if (request.contactPerson() != null) supplier.setContactPerson(request.contactPerson());
        if (request.email() != null) supplier.setEmail(request.email());
        if (request.phoneNumber() != null) supplier.setPhoneNumber(request.phoneNumber());
        if (request.address() != null) supplier.setAddress(request.address());
    }
}
