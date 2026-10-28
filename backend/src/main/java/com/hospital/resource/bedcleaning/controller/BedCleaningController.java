package com.hospital.resource.bedcleaning.controller;

import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.bedcleaning.dto.*;
import com.hospital.resource.bedcleaning.service.BedCleaningApplicationService;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.common.dto.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bed-cleaning")
@RequiredArgsConstructor
public class BedCleaningController {

    private final BedCleaningApplicationService cleaningService;

    @GetMapping("/pending")
    public ApiResponse<List<CleaningTaskResponse>> getPendingTasks() {
        return ApiResponse.success(cleaningService.getPendingTasks());
    }

    @GetMapping("/bed/{bedId}")
    public ApiResponse<List<CleaningTaskResponse>> getTasksByBed(@PathVariable UUID bedId) {
        return ApiResponse.success(cleaningService.getTasksByBed(bedId));
    }

    @GetMapping
    public ApiResponse<PagedResponse<CleaningTaskResponse>> searchTasks(
            @RequestParam(required = false) UUID bedId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID assignedTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        CleaningSearchRequest request = new CleaningSearchRequest(
                bedId, status, assignedTo, null, null, page, size);
        return ApiResponse.success(cleaningService.searchTasks(request));
    }

    @PostMapping("/{id}/assign")
    public ApiResponse<CleaningTaskResponse> assignTask(
            @PathVariable UUID id,
            @Valid @RequestBody CleaningAssignmentRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(cleaningService.assignTask(id, request, userId));
    }

    @PostMapping("/{id}/start")
    public ApiResponse<CleaningTaskResponse> startCleaning(@PathVariable UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(cleaningService.startCleaning(id, userId));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<CleaningTaskResponse> completeCleaning(
            @PathVariable UUID id,
            @RequestBody CleaningCompletionRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(cleaningService.completeCleaning(id, request, userId));
    }

    @PostMapping("/{id}/verify")
    public ApiResponse<CleaningTaskResponse> verifyCleaning(@PathVariable UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(cleaningService.verifyCleaning(id, userId));
    }

    @GetMapping("/stats")
    public ApiResponse<BedCleaningApplicationService.CleaningStatsResponse> getCleaningStats() {
        return ApiResponse.success(cleaningService.getCleaningStats());
    }
}
