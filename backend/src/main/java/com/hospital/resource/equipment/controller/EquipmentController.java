package com.hospital.resource.equipment.controller;

import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.equipment.dto.EquipmentAllocationResponse;
import com.hospital.resource.equipment.dto.EquipmentRequest;
import com.hospital.resource.equipment.dto.EquipmentResponse;
import com.hospital.resource.equipment.dto.EquipmentUsageHistoryResponse;
import com.hospital.resource.equipment.service.EquipmentApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentApplicationService equipmentService;

    @PostMapping
    public ApiResponse<EquipmentResponse> createEquipment(@Valid @RequestBody EquipmentRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(equipmentService.createEquipment(request, userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<EquipmentResponse> getEquipment(@PathVariable UUID id) {
        return ApiResponse.success(equipmentService.getEquipment(id));
    }

    @GetMapping
    public ApiResponse<List<EquipmentResponse>> getAllEquipment() {
        return ApiResponse.success(equipmentService.getAllEquipment());
    }

    @GetMapping("/available")
    public ApiResponse<List<EquipmentResponse>> getAvailableEquipment() {
        return ApiResponse.success(equipmentService.getAvailableEquipment());
    }

    @GetMapping("/ward/{wardId}")
    public ApiResponse<List<EquipmentResponse>> getEquipmentByWard(@PathVariable UUID wardId) {
        return ApiResponse.success(equipmentService.getEquipmentByWard(wardId));
    }

    @PutMapping("/{id}")
    public ApiResponse<EquipmentResponse> updateEquipment(@PathVariable UUID id, @Valid @RequestBody EquipmentRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(equipmentService.updateEquipment(id, request, userId));
    }

    @PostMapping("/{id}/assign")
    public ApiResponse<EquipmentAllocationResponse> assignEquipment(@PathVariable UUID id, @RequestParam UUID admissionId) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(equipmentService.assignEquipment(id, admissionId, userId));
    }

    @PostMapping("/{id}/release")
    public ApiResponse<EquipmentAllocationResponse> releaseEquipment(@PathVariable UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(equipmentService.releaseEquipment(id, userId));
    }

    @GetMapping("/{id}/history")
    public ApiResponse<EquipmentUsageHistoryResponse> getUsageHistory(@PathVariable UUID id) {
        return ApiResponse.success(equipmentService.getUsageHistory(id));
    }
}
