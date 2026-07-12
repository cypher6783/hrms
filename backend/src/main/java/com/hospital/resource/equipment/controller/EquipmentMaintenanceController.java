package com.hospital.resource.equipment.controller;

import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.equipment.dto.MaintenanceRequest;
import com.hospital.resource.equipment.dto.MaintenanceResponse;
import com.hospital.resource.equipment.service.EquipmentMaintenanceApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/equipment/{equipmentId}/maintenance")
@RequiredArgsConstructor
public class EquipmentMaintenanceController {

    private final EquipmentMaintenanceApplicationService maintenanceService;

    @PostMapping
    public ApiResponse<MaintenanceResponse> scheduleMaintenance(
            @PathVariable UUID equipmentId,
            @Valid @RequestBody MaintenanceRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(maintenanceService.scheduleMaintenance(equipmentId, request, userId));
    }

    @GetMapping
    public ApiResponse<List<MaintenanceResponse>> getMaintenanceByEquipment(@PathVariable UUID equipmentId) {
        return ApiResponse.success(maintenanceService.getMaintenanceByEquipment(equipmentId));
    }

    @GetMapping("/overdue")
    public ApiResponse<List<MaintenanceResponse>> getOverdueMaintenance() {
        return ApiResponse.success(maintenanceService.getOverdueMaintenance());
    }

    @PostMapping("/{maintenanceId}/complete")
    public ApiResponse<MaintenanceResponse> completeMaintenance(@PathVariable UUID equipmentId, @PathVariable UUID maintenanceId) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(maintenanceService.completeMaintenance(maintenanceId, userId));
    }
}
