package com.hospital.resource.staff.controller;

import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.common.dto.PagedResponse;
import com.hospital.resource.staff.dto.StaffRequest;
import com.hospital.resource.staff.dto.StaffResponse;
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

    @GetMapping
    public ApiResponse<PagedResponse<StaffResponse>> getAllStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(staffService.getAllStaff(page, size));
    }

    @GetMapping("/ward/{wardId}")
    public ApiResponse<List<StaffResponse>> getStaffByWard(@PathVariable UUID wardId) {
        return ApiResponse.success(staffService.getStaffByWard(wardId));
    }

    @PutMapping("/{id}")
    public ApiResponse<StaffResponse> updateStaff(@PathVariable UUID id, @Valid @RequestBody StaffRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(staffService.updateStaff(id, request, userId));
    }
}
