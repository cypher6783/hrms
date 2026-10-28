package com.hospital.resource.staff.controller;

import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.staff.dto.*;
import com.hospital.resource.staff.service.ShiftApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftApplicationService shiftService;

    @PostMapping
    public ApiResponse<ShiftResponse> createShift(@Valid @RequestBody ShiftRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(shiftService.createShift(request, userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<ShiftResponse> getShift(@PathVariable UUID id) {
        return ApiResponse.success(shiftService.getShift(id));
    }

    @GetMapping("/ward/{wardId}/date/{date}")
    public ApiResponse<List<ShiftResponse>> getShiftsByWardAndDate(
            @PathVariable UUID wardId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(shiftService.getShiftsByWardAndDate(wardId, date));
    }

    @PostMapping("/assign")
    public ApiResponse<ShiftAssignmentResponse> assignStaff(@Valid @RequestBody ShiftAssignmentRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(shiftService.assignStaff(request, userId));
    }

    @GetMapping("/{shiftId}/assignments")
    public ApiResponse<List<ShiftAssignmentResponse>> getShiftAssignments(@PathVariable UUID shiftId) {
        return ApiResponse.success(shiftService.getShiftAssignments(shiftId));
    }
}
