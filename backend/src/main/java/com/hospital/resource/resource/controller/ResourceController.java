package com.hospital.resource.resource.controller;

import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.resource.dto.*;
import com.hospital.resource.resource.service.InventoryApplicationService;
import com.hospital.resource.resource.service.ResourceApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/resources")
public class ResourceController {

    private final ResourceApplicationService resourceService;
    private final InventoryApplicationService inventoryService;

    @Autowired
    public ResourceController(ResourceApplicationService resourceService,
                              @Autowired(required = false) InventoryApplicationService inventoryService) {
        this.resourceService = resourceService;
        this.inventoryService = inventoryService;
    }

    public ResourceController(ResourceApplicationService resourceService) {
        this(resourceService, null);
    }

    @PostMapping
    public ApiResponse<ResourceResponse> createResource(@Valid @RequestBody ResourceRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(resourceService.createResource(request, userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<ResourceResponse> getResource(@PathVariable UUID id) {
        return ApiResponse.success(resourceService.getResource(id));
    }

    @GetMapping
    public ApiResponse<List<ResourceResponse>> getAllResources() {
        return ApiResponse.success(resourceService.getAllResources());
    }

    @GetMapping("/category/{category}")
    public ApiResponse<List<ResourceResponse>> getResourcesByCategory(@PathVariable String category) {
        return ApiResponse.success(resourceService.getResourcesByCategory(category));
    }

    @PutMapping("/{id}")
    public ApiResponse<ResourceResponse> updateResource(@PathVariable UUID id, @Valid @RequestBody ResourceRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(resourceService.updateResource(id, request, userId));
    }

    @PostMapping("/{id}/inventory/add")
    public ApiResponse<InventoryTransactionResponse> restockResource(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> body) {
        UUID userId = SecurityUtils.getCurrentUserId();
        int qty = 1;
        if (body.get("quantity") != null) {
            if (body.get("quantity") instanceof Number num) {
                qty = num.intValue();
            } else {
                qty = Integer.parseInt(body.get("quantity").toString());
            }
        }
        String notes = (String) body.getOrDefault("notes", "Restocked via resource inventory management");
        return ApiResponse.success(inventoryService.restockResource(id, qty, notes, userId));
    }

    @PostMapping("/reservations")
    public ApiResponse<ResourceReservationResponse> reserveResource(@Valid @RequestBody ResourceReservationRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(resourceService.reserveResource(request, userId));
    }

    @PostMapping("/allocations")
    public ApiResponse<ResourceAllocationResponse> allocateResource(@Valid @RequestBody ResourceAllocationRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(resourceService.allocateResource(request, userId));
    }

    @PutMapping("/allocations/{allocationId}/release")
    public ApiResponse<ResourceAllocationResponse> releaseResource(@PathVariable UUID allocationId) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(resourceService.releaseResource(allocationId, userId));
    }

    @GetMapping("/{id}/utilization")
    public ApiResponse<ResourceUtilizationResponse> getUtilizationMetrics(@PathVariable UUID id) {
        return ApiResponse.success(resourceService.getUtilizationMetrics(id));
    }
}
