package com.hospital.resource.equipment.service;

import com.hospital.resource.common.event.DomainEventPublisher;
import com.hospital.resource.common.event.maintenance.EquipmentMaintenanceCompletedEvent;
import com.hospital.resource.common.event.maintenance.EquipmentMaintenanceStartedEvent;
import com.hospital.resource.common.event.maintenance.EquipmentReturnedToServiceEvent;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.equipment.domain.MaintenanceDomainService;
import com.hospital.resource.equipment.dto.MaintenanceRequest;
import com.hospital.resource.equipment.dto.MaintenanceResponse;
import com.hospital.resource.equipment.entity.Equipment;
import com.hospital.resource.equipment.entity.EquipmentMaintenance;
import com.hospital.resource.equipment.mapper.MaintenanceMapper;
import com.hospital.resource.equipment.repository.EquipmentMaintenanceRepository;
import com.hospital.resource.equipment.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class EquipmentMaintenanceApplicationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EquipmentMaintenanceApplicationService.class);

    private final EquipmentMaintenanceRepository maintenanceRepository;
    private final EquipmentRepository equipmentRepository;
    private final MaintenanceMapper maintenanceMapper;
    private final DomainEventPublisher eventPublisher;

    private final MaintenanceDomainService maintenanceDomainService = new MaintenanceDomainService();

    public EquipmentMaintenanceApplicationService(
            EquipmentMaintenanceRepository maintenanceRepository,
            EquipmentRepository equipmentRepository,
            MaintenanceMapper maintenanceMapper,
            DomainEventPublisher eventPublisher) {
        this.maintenanceRepository = maintenanceRepository;
        this.equipmentRepository = equipmentRepository;
        this.maintenanceMapper = maintenanceMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public MaintenanceResponse scheduleMaintenance(UUID equipmentId, MaintenanceRequest request, UUID userId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", equipmentId.toString()));

        // Mark equipment as UNDER_MAINTENANCE
        equipment.setStatus("UNDER_MAINTENANCE");
        equipment.setUpdatedBy(userId);
        equipmentRepository.save(equipment);

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

        final EquipmentMaintenance savedMaintenance = maintenanceRepository.save(maintenance);
        log.info("Maintenance scheduled: equipmentId={}, maintenanceId={}", equipmentId, savedMaintenance.getId());

        eventPublisher.publish(new EquipmentMaintenanceStartedEvent(this, savedMaintenance.getId(), equipmentId, request.maintenanceType()));

        return maintenanceMapper.toResponse(savedMaintenance);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceResponse> getMaintenanceByEquipment(UUID equipmentId) {
        return maintenanceMapper.toResponseList(maintenanceRepository.findByEquipmentId(equipmentId));
    }

    @Transactional(readOnly = true)
    public List<MaintenanceResponse> getOverdueMaintenance() {
        return maintenanceMapper.toResponseList(maintenanceRepository.findOverdueMaintenance(LocalDate.now()));
    }

    @Transactional
    public MaintenanceResponse completeMaintenance(UUID maintenanceId, UUID userId) {
        EquipmentMaintenance maintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", maintenanceId.toString()));

        maintenanceDomainService.validateCompletable(maintenance);

        maintenance.setStatus("COMPLETED");
        maintenance.setCompletedDate(LocalDate.now());
        maintenance.setUpdatedBy(userId);

        final EquipmentMaintenance savedMaintenance = maintenanceRepository.save(maintenance);
        log.info("Maintenance completed by technician: maintenanceId={}", maintenanceId);

        eventPublisher.publish(new EquipmentMaintenanceCompletedEvent(this, maintenanceId, savedMaintenance.getEquipmentId(), savedMaintenance.getPerformedBy()));

        return maintenanceMapper.toResponse(savedMaintenance);
    }

    @Transactional
    public MaintenanceResponse verifyAndReturnToService(UUID maintenanceId, UUID userId) {
        EquipmentMaintenance maintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", maintenanceId.toString()));

        maintenanceDomainService.validateVerifiable(maintenance);

        maintenance.setStatus("VERIFIED");
        maintenance.setUpdatedBy(userId);
        final EquipmentMaintenance savedMaintenance = maintenanceRepository.save(maintenance);

        Equipment equipment = equipmentRepository.findById(savedMaintenance.getEquipmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", savedMaintenance.getEquipmentId().toString()));

        equipment.setStatus("AVAILABLE");
        equipment.setUpdatedBy(userId);
        equipmentRepository.save(equipment);

        log.info("Maintenance verified and equipment returned to service: equipmentId={}, maintenanceId={}", equipment.getId(), maintenanceId);

        eventPublisher.publish(new EquipmentReturnedToServiceEvent(this, maintenanceId, equipment.getId(), userId));

        return maintenanceMapper.toResponse(savedMaintenance);
    }
}
