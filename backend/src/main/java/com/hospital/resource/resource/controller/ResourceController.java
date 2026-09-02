package com.hospital.resource.resource.controller;

import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.resource.dto.*;
import com.hospital.resource.resource.service.ResourceApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceApplicationService resourceService;

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
