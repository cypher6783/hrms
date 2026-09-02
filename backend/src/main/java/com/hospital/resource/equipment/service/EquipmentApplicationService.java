package com.hospital.resource.equipment.service;

import com.hospital.resource.common.event.DomainEventPublisher;
import com.hospital.resource.common.event.equipment.EquipmentAssignedEvent;
import com.hospital.resource.common.event.equipment.EquipmentReleasedEvent;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.equipment.domain.EquipmentDomainService;
import com.hospital.resource.equipment.dto.EquipmentAllocationResponse;
import com.hospital.resource.equipment.dto.EquipmentRequest;
import com.hospital.resource.equipment.dto.EquipmentResponse;
import com.hospital.resource.equipment.dto.EquipmentUsageHistoryResponse;
import com.hospital.resource.equipment.dto.MaintenanceResponse;
import com.hospital.resource.equipment.entity.Equipment;
import com.hospital.resource.equipment.entity.EquipmentAllocation;
import com.hospital.resource.equipment.mapper.EquipmentMapper;
import com.hospital.resource.equipment.mapper.MaintenanceMapper;
import com.hospital.resource.equipment.repository.EquipmentAllocationRepository;
import com.hospital.resource.equipment.repository.EquipmentMaintenanceRepository;
import com.hospital.resource.equipment.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class EquipmentApplicationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EquipmentApplicationService.class);

    private final EquipmentRepository equipmentRepository;
    private final EquipmentAllocationRepository allocationRepository;
    private final EquipmentMaintenanceRepository maintenanceRepository;
    private final EquipmentMapper equipmentMapper;
    private final MaintenanceMapper maintenanceMapper;
    private final DomainEventPublisher eventPublisher;

    private final EquipmentDomainService equipmentDomainService = new EquipmentDomainService();

    public EquipmentApplicationService(
            EquipmentRepository equipmentRepository,
            EquipmentAllocationRepository allocationRepository,
            EquipmentMaintenanceRepository maintenanceRepository,
            EquipmentMapper equipmentMapper,
            MaintenanceMapper maintenanceMapper,
            DomainEventPublisher eventPublisher) {
        this.equipmentRepository = equipmentRepository;
        this.allocationRepository = allocationRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.equipmentMapper = equipmentMapper;
        this.maintenanceMapper = maintenanceMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public EquipmentResponse createEquipment(EquipmentRequest request, UUID userId) {
        boolean exists = equipmentRepository.existsBySerialNumber(request.serialNumber());
        equipmentDomainService.validateSerialUniqueness(exists);

        Equipment equipment = Equipment.builder()
                .name(request.name())
                .equipmentType(request.equipmentType())
                .serialNumber(request.serialNumber())
                .location(request.location())
                .assignedWardId(request.assignedWardId())
                .status("AVAILABLE")
                .createdBy(userId)
                .updatedBy(userId)
                .build();

        equipment = equipmentRepository.save(equipment);
        log.info("Equipment registered: equipmentId={}, serialNumber={}", equipment.getId(), equipment.getSerialNumber());
        return equipmentMapper.toResponse(equipment);
    }

    @Transactional(readOnly = true)
    public EquipmentResponse getEquipment(UUID id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", id.toString()));
        return equipmentMapper.toResponse(equipment);
    }

    @Transactional(readOnly = true)
    public List<EquipmentResponse> getAllEquipment() {
        return equipmentMapper.toResponseList(equipmentRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<EquipmentResponse> getAvailableEquipment() {
        return equipmentMapper.toResponseList(equipmentRepository.findByStatus("AVAILABLE"));
    }

    @Transactional(readOnly = true)
    public List<EquipmentResponse> getEquipmentByWard(UUID wardId) {
        return equipmentMapper.toResponseList(equipmentRepository.findByAssignedWardId(wardId));
    }

    @Transactional
    public EquipmentResponse updateEquipment(UUID id, EquipmentRequest request, UUID userId) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", id.toString()));

        if (!equipment.getSerialNumber().equals(request.serialNumber())) {
            boolean exists = equipmentRepository.existsBySerialNumber(request.serialNumber());
            equipmentDomainService.validateSerialUniqueness(exists);
        }

        equipment.setName(request.name());
        equipment.setEquipmentType(request.equipmentType());
        equipment.setSerialNumber(request.serialNumber());
        equipment.setLocation(request.location());
        equipment.setAssignedWardId(request.assignedWardId());
        equipment.setUpdatedBy(userId);

        equipment = equipmentRepository.save(equipment);
        log.info("Equipment updated: equipmentId={}", equipment.getId());
        return equipmentMapper.toResponse(equipment);
    }

    @Transactional
    public EquipmentAllocationResponse assignEquipment(UUID equipmentId, UUID admissionId, UUID userId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", equipmentId.toString()));

        equipmentDomainService.validateAssignable(equipment);

        equipment.setAssignedAdmissionId(admissionId);
        equipment.setStatus("IN_USE");
        equipment.setUpdatedBy(userId);
        equipmentRepository.save(equipment);

        EquipmentAllocation allocation = EquipmentAllocation.builder()
                .equipmentId(equipmentId)
                .admissionId(admissionId)
                .allocatedAt(Instant.now())
                .allocatedBy(userId)
                .build();

        allocation = allocationRepository.save(allocation);
        log.info("Equipment assigned: equipmentId={}, admissionId={}", equipmentId, admissionId);

        eventPublisher.publish(new EquipmentAssignedEvent(this, equipmentId, admissionId, equipment.getAssignedWardId(), userId));

        return equipmentMapper.toAllocationResponse(allocation);
    }

    @Transactional
    public EquipmentAllocationResponse releaseEquipment(UUID equipmentId, UUID userId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", equipmentId.toString()));

        UUID previousAdmissionId = equipment.getAssignedAdmissionId();

        equipment.setAssignedAdmissionId(null);
        equipment.setStatus("AVAILABLE");
        equipment.setUpdatedBy(userId);
        equipmentRepository.save(equipment);

        Optional<EquipmentAllocation> activeAllocOpt = allocationRepository.findByEquipmentIdAndReleasedAtIsNull(equipmentId);
        EquipmentAllocation allocation = null;
        if (activeAllocOpt.isPresent()) {
            allocation = activeAllocOpt.get();
            allocation.setReleasedAt(Instant.now());
            allocation = allocationRepository.save(allocation);
        }

        log.info("Equipment released: equipmentId={}", equipmentId);

        eventPublisher.publish(new EquipmentReleasedEvent(this, equipmentId, previousAdmissionId, userId));

        return allocation != null ? equipmentMapper.toAllocationResponse(allocation) : null;
    }

    @Transactional(readOnly = true)
    public EquipmentUsageHistoryResponse getUsageHistory(UUID equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", equipmentId.toString()));

        List<EquipmentAllocationResponse> allocations = equipmentMapper.toAllocationResponseList(
                allocationRepository.findByEquipmentId(equipmentId)
        );

        List<MaintenanceResponse> maintenanceLogs = maintenanceMapper.toResponseList(
                maintenanceRepository.findByEquipmentId(equipmentId)
        );

        return new EquipmentUsageHistoryResponse(
                equipment.getId(),
                equipment.getSerialNumber(),
                equipment.getName(),
                equipment.getStatus(),
                allocations,
                maintenanceLogs
        );
    }
}
