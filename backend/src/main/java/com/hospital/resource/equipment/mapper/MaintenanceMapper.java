package com.hospital.resource.equipment.mapper;

import com.hospital.resource.equipment.dto.MaintenanceRequest;
import com.hospital.resource.equipment.dto.MaintenanceResponse;
import com.hospital.resource.equipment.entity.EquipmentMaintenance;
import org.mapstruct.Mapping;

import java.util.List;

public interface MaintenanceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "equipmentId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "completedDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    EquipmentMaintenance toEntity(MaintenanceRequest request);

    MaintenanceResponse toResponse(EquipmentMaintenance maintenance);

    List<MaintenanceResponse> toResponseList(List<EquipmentMaintenance> maintenanceList);
}
