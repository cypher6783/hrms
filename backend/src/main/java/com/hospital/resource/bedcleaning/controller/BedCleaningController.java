package com.hospital.resource.bedcleaning.controller;

import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.bedcleaning.dto.*;
import com.hospital.resource.bedcleaning.service.BedCleaningApplicationService;
import com.hospital.resource.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cleaning")
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
}
