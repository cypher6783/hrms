package com.hospital.resource.equipment.service;

import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.equipment.dto.MaintenanceRequest;
import com.hospital.resource.equipment.dto.MaintenanceResponse;
import com.hospital.resource.equipment.entity.Equipment;
import com.hospital.resource.equipment.entity.EquipmentMaintenance;
import com.hospital.resource.equipment.repository.EquipmentMaintenanceRepository;
import com.hospital.resource.equipment.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentMaintenanceApplicationService {

    private final EquipmentMaintenanceRepository maintenanceRepository;
    private final EquipmentRepository equipmentRepository;

    @Transactional
    public MaintenanceResponse scheduleMaintenance(UUID equipmentId, MaintenanceRequest request, UUID userId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", equipmentId.toString()));

        EquipmentMaintenance maintenance = EquipmentMaintenance.builder()
                .equipmentId(equipmentId)
                .maintenanceType(request.maintenanceType())
                .status("SCHEDULED")
                .scheduledDate(request.scheduledDate())
                .performedBy(request.performedBy())
                .maintenanceNotes(request.maintenanceNotes())
                .cost(request.cost())
                .nextMaintenanceDate(request.nextMaintenanceDate())
                .createdBy(userId)
                .updatedBy(userId)
                .build();

        maintenance = maintenanceRepository.save(maintenance);
        log.info("Maintenance scheduled: equipmentId={}, maintenanceId={}", equipmentId, maintenance.getId());
        return toResponse(maintenance);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceResponse> getMaintenanceByEquipment(UUID equipmentId) {
        return maintenanceRepository.findByEquipmentId(equipmentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MaintenanceResponse> getOverdueMaintenance() {
        return maintenanceRepository.findByStatus("SCHEDULED").stream()
                .filter(m -> m.getScheduledDate().isBefore(LocalDate.now()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MaintenanceResponse completeMaintenance(UUID maintenanceId, UUID userId) {
        EquipmentMaintenance maintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", maintenanceId.toString()));

        maintenance.setStatus("COMPLETED");
        maintenance.setCompletedDate(LocalDate.now());
        maintenance.setUpdatedBy(userId);

        maintenance = maintenanceRepository.save(maintenance);
        log.info("Maintenance completed: maintenanceId={}", maintenanceId);
        return toResponse(maintenance);
    }

    private MaintenanceResponse toResponse(EquipmentMaintenance maintenance) {
        return new MaintenanceResponse(
                maintenance.getId(), maintenance.getEquipmentId(),
                maintenance.getMaintenanceType(), maintenance.getStatus(),
                maintenance.getScheduledDate(), maintenance.getCompletedDate(),
                maintenance.getPerformedBy(), maintenance.getMaintenanceNotes(),
                maintenance.getCost(), maintenance.getNextMaintenanceDate(),
                maintenance.getCreatedAt(), maintenance.getUpdatedAt()
        );
    }
}
