package com.hospital.resource.resource.service;

import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.resource.dto.*;
import com.hospital.resource.resource.entity.Resource;
import com.hospital.resource.resource.entity.ResourceSupplier;
import com.hospital.resource.resource.repository.ResourceRepository;
import com.hospital.resource.resource.repository.ResourceSupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceApplicationService {

    private final ResourceRepository resourceRepository;
    private final ResourceSupplierRepository supplierRepository;

    @Transactional
    public ResourceResponse createResource(ResourceRequest request, UUID userId) {
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
        log.info("Resource created: resourceId={}, name={}", resource.getId(), resource.getName());
        return toResponse(resource);
    }

    @Transactional(readOnly = true)
    public ResourceResponse getResource(UUID id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", id.toString()));
        return toResponse(resource);
    }

    @Transactional(readOnly = true)
    public List<ResourceResponse> getAllResources() {
        return resourceRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ResourceResponse> getResourcesByCategory(String category) {
        return resourceRepository.findByCategory(category).stream().map(this::toResponse).toList();
    }

    @Transactional
    public ResourceResponse updateResource(UUID id, ResourceRequest request, UUID userId) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", id.toString()));

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
        return toResponse(resource);
    }

    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request, UUID userId) {
        ResourceSupplier supplier = ResourceSupplier.builder()
                .name(request.name())
                .contactPerson(request.contactPerson())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .address(request.address())
                .leadTimeDays(request.leadTimeDays() != null ? request.leadTimeDays() : 0)
                .isActive(true)
                .createdBy(userId)
                .updatedBy(userId)
                .build();

        supplier = supplierRepository.save(supplier);
        log.info("Supplier created: supplierId={}, name={}", supplier.getId(), supplier.getName());
        return toSupplierResponse(supplier);
    }

    @Transactional(readOnly = true)
    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findByIsActiveTrue().stream().map(this::toSupplierResponse).toList();
    }

    private ResourceResponse toResponse(Resource resource) {
        return new ResourceResponse(
                resource.getId(), resource.getName(), resource.getCategory(),
                resource.getUnitOfMeasure(), resource.getMinimumThreshold(),
                resource.getReorderPoint(), resource.getCriticalityLevel(),
                resource.getDefaultSupplierId(), resource.getCreatedAt(), resource.getUpdatedAt()
        );
    }

    private SupplierResponse toSupplierResponse(ResourceSupplier supplier) {
        return new SupplierResponse(
                supplier.getId(), supplier.getName(), supplier.getContactPerson(),
                supplier.getPhoneNumber(), supplier.getEmail(), supplier.getAddress(),
                supplier.getLeadTimeDays(), supplier.getIsActive(),
                supplier.getCreatedAt(), supplier.getUpdatedAt()
        );
    }
}
