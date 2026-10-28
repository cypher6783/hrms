package com.hospital.resource.bed.controller;

import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.bed.dto.BedAvailabilityResponse;
import com.hospital.resource.bed.dto.BedRequest;
import com.hospital.resource.bed.dto.BedResponse;
import com.hospital.resource.bed.service.BedApplicationService;
import com.hospital.resource.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/beds")
@RequiredArgsConstructor
public class BedController {

    private final BedApplicationService bedService;

    @PostMapping
    public ApiResponse<BedResponse> createBed(@Valid @RequestBody BedRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(bedService.createBed(request, userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<BedResponse> getBed(@PathVariable UUID id) {
        return ApiResponse.success(bedService.getBed(id));
    }

    @GetMapping("/ward/{wardId}")
    public ApiResponse<List<BedResponse>> getBedsByWard(@PathVariable UUID wardId) {
        return ApiResponse.success(bedService.getBedsByWard(wardId));
    }

    @GetMapping("/available/isolation")
    public ApiResponse<List<BedResponse>> getAvailableIsolationBeds() {
        return ApiResponse.success(bedService.getAvailableIsolationBeds());
    }

    @GetMapping("/availability/{wardId}")
    public ApiResponse<BedAvailabilityResponse> getBedAvailability(@PathVariable UUID wardId) {
        return ApiResponse.success(bedService.getBedAvailability(wardId));
    }

    @GetMapping("/filter")
    public ApiResponse<List<BedResponse>> filterBeds(
            @RequestParam(required = false) UUID wardId,
            @RequestParam(required = false) String bedType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isIsolationCapable) {
        return ApiResponse.success(bedService.filterBeds(wardId, bedType, status, isIsolationCapable));
    }

    @PutMapping("/{id}")
    public ApiResponse<BedResponse> updateBed(@PathVariable UUID id, @Valid @RequestBody BedRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(bedService.updateBed(id, request, userId));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<BedResponse> updateBedStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        return ApiResponse.success(bedService.updateBedStatus(id, status));
    }
}
