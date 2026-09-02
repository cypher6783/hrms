package com.hospital.resource.equipment.mapper;

import com.hospital.resource.equipment.dto.MaintenanceRequest;
import com.hospital.resource.equipment.dto.MaintenanceResponse;
import com.hospital.resource.equipment.entity.EquipmentMaintenance;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MaintenanceMapperImpl implements MaintenanceMapper {

    @Override
    public EquipmentMaintenance toEntity(MaintenanceRequest request) {
        if (request == null) return null;
        return EquipmentMaintenance.builder()
                .maintenanceType(request.maintenanceType())
                .scheduledDate(request.scheduledDate())
                .performedBy(request.performedBy())
                .maintenanceNotes(request.maintenanceNotes())
                .cost(request.cost())
                .nextMaintenanceDate(request.nextMaintenanceDate())
                .build();
    }

    @Override
    public MaintenanceResponse toResponse(EquipmentMaintenance maintenance) {
        if (maintenance == null) return null;
        return new MaintenanceResponse(
                maintenance.getId(),
                maintenance.getEquipmentId(),
                maintenance.getMaintenanceType(),
                maintenance.getStatus(),
                maintenance.getScheduledDate(),
                maintenance.getCompletedDate(),
                maintenance.getPerformedBy(),
                maintenance.getMaintenanceNotes(),
                maintenance.getCost(),
                maintenance.getNextMaintenanceDate(),
                maintenance.getCreatedAt(),
                maintenance.getUpdatedAt()
        );
    }

    @Override
    public List<MaintenanceResponse> toResponseList(List<EquipmentMaintenance> maintenanceList) {
        if (maintenanceList == null) return List.of();
        return maintenanceList.stream().map(this::toResponse).toList();
    }
}
