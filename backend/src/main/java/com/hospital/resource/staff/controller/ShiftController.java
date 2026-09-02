package com.hospital.resource.staff.controller;

import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.common.dto.PagedResponse;
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

    @GetMapping("/{id}/summary")
    public ApiResponse<ShiftSummaryResponse> getShiftSummary(@PathVariable UUID id) {
        return ApiResponse.success(shiftService.getShiftSummary(id));
    }

    @GetMapping
    public ApiResponse<PagedResponse<ShiftResponse>> searchShifts(
            @RequestParam(required = false) UUID wardId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate shiftDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate shiftDateTo,
            @RequestParam(required = false) String shiftName,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ShiftSearchRequest request = new ShiftSearchRequest(
                wardId, shiftDateFrom, shiftDateTo, shiftName, status, page, size);
        return ApiResponse.success(shiftService.searchShifts(request));
    }

    @GetMapping("/ward/{wardId}/date/{date}")
    public ApiResponse<List<ShiftSummaryResponse>> getShiftsByWardAndDate(
            @PathVariable UUID wardId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(shiftService.getShiftsByWardAndDate(wardId, date));
    }

    @GetMapping("/calendar")
    public ApiResponse<List<ShiftResponse>> getShiftCalendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) UUID wardId) {
        return ApiResponse.success(shiftService.getShiftCalendar(startDate, endDate, wardId));
    }

    @PostMapping("/assign")
    public ApiResponse<ShiftAssignmentResponse> assignStaff(@Valid @RequestBody ShiftAssignmentRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(shiftService.assignStaff(request, userId));
    }

    @DeleteMapping("/assignments/{id}")
    public ApiResponse<Void> removeAssignment(@PathVariable UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        shiftService.removeAssignment(id, userId);
        return ApiResponse.success(null);
    }

    @GetMapping("/{shiftId}/assignments")
    public ApiResponse<List<ShiftAssignmentResponse>> getShiftAssignments(@PathVariable UUID shiftId) {
        return ApiResponse.success(shiftService.getShiftAssignments(shiftId));
    }

    @GetMapping("/{shiftId}/staffing-level")
    public ApiResponse<StaffingLevelResponse> getStaffingLevel(@PathVariable UUID shiftId) {
        return ApiResponse.success(shiftService.getStaffingLevel(shiftId));
    }

    @GetMapping("/stats")
    public ApiResponse<ShiftApplicationService.ShiftStatsResponse> getShiftStats() {
        return ApiResponse.success(shiftService.getShiftStats());
    }
}
