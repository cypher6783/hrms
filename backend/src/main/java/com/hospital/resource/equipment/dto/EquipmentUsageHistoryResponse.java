package com.hospital.resource.equipment.dto;

import java.util.List;
import java.util.UUID;

public record EquipmentUsageHistoryResponse(
        UUID equipmentId,
        String serialNumber,
        String name,
        String status,
        List<EquipmentAllocationResponse> allocationHistory,
        List<MaintenanceResponse> maintenanceHistory
) {}
