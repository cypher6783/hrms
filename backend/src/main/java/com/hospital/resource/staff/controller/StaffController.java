package com.hospital.resource.staff.controller;

import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.common.dto.PagedResponse;
import com.hospital.resource.staff.dto.*;
import com.hospital.resource.staff.service.StaffApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffApplicationService staffService;

    @PostMapping
    public ApiResponse<StaffResponse> createStaff(@Valid @RequestBody StaffRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(staffService.createStaff(request, userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<StaffResponse> getStaff(@PathVariable UUID id) {
        return ApiResponse.success(staffService.getStaff(id));
    }

    @GetMapping("/{id}/summary")
    public ApiResponse<StaffSummaryResponse> getStaffSummary(@PathVariable UUID id) {
        return ApiResponse.success(staffService.getStaffSummary(id));
    }

    @GetMapping
    public ApiResponse<PagedResponse<StaffResponse>> searchStaff(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) UUID wardId,
            @RequestParam(required = false) String availabilityStatus,
            @RequestParam(required = false) String certificationStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        StaffSearchRequest request = new StaffSearchRequest(
                name, role, specialization, wardId, availabilityStatus, certificationStatus, page, size);
        return ApiResponse.success(staffService.searchStaff(request));
    }

    @GetMapping("/ward/{wardId}")
    public ApiResponse<List<StaffSummaryResponse>> getStaffByWard(@PathVariable UUID wardId) {
        return ApiResponse.success(staffService.getStaffByWard(wardId));
    }

    @PutMapping("/{id}")
    public ApiResponse<StaffResponse> updateStaff(@PathVariable UUID id, @Valid @RequestBody StaffRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(staffService.updateStaff(id, request, userId));
    }

    @GetMapping("/{id}/workload")
    public ApiResponse<StaffWorkloadResponse> getStaffWorkload(@PathVariable UUID id) {
        return ApiResponse.success(staffService.getStaffWorkload(id));
    }

    @GetMapping("/stats")
    public ApiResponse<StaffApplicationService.StaffStatsResponse> getStaffStats() {
        return ApiResponse.success(staffService.getStaffStats());
    }
}
