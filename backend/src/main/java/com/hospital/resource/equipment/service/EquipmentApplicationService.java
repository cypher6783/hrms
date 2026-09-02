package com.hospital.resource.equipment.service;

import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.equipment.dto.EquipmentRequest;
import com.hospital.resource.equipment.dto.EquipmentResponse;
import com.hospital.resource.equipment.entity.Equipment;
import com.hospital.resource.equipment.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentApplicationService {

    private final EquipmentRepository equipmentRepository;

    @Transactional
    public EquipmentResponse createEquipment(EquipmentRequest request, UUID userId) {
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
        log.info("Equipment created: equipmentId={}, serialNumber={}", equipment.getId(), equipment.getSerialNumber());
        return toResponse(equipment);
    }

    @Transactional(readOnly = true)
    public EquipmentResponse getEquipment(UUID id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", id.toString()));
        return toResponse(equipment);
    }

    @Transactional(readOnly = true)
    public List<EquipmentResponse> getAllEquipment() {
        return equipmentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<EquipmentResponse> getAvailableEquipment() {
        return equipmentRepository.findByStatus("AVAILABLE").stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<EquipmentResponse> getEquipmentByWard(UUID wardId) {
        return equipmentRepository.findByAssignedWardId(wardId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public EquipmentResponse updateEquipment(UUID id, EquipmentRequest request, UUID userId) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", id.toString()));

        equipment.setName(request.name());
        equipment.setEquipmentType(request.equipmentType());
        equipment.setSerialNumber(request.serialNumber());
        equipment.setLocation(request.location());
        equipment.setAssignedWardId(request.assignedWardId());
        equipment.setUpdatedBy(userId);

        equipment = equipmentRepository.save(equipment);
        log.info("Equipment updated: equipmentId={}", equipment.getId());
        return toResponse(equipment);
    }

    @Transactional
    public void assignEquipment(UUID equipmentId, UUID admissionId, UUID userId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", equipmentId.toString()));
        equipment.setAssignedAdmissionId(admissionId);
        equipment.setStatus("IN_USE");
        equipment.setUpdatedBy(userId);
        equipmentRepository.save(equipment);
        log.info("Equipment assigned: equipmentId={}, admissionId={}", equipmentId, admissionId);
    }

    @Transactional
    public void releaseEquipment(UUID equipmentId, UUID userId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", equipmentId.toString()));
        equipment.setAssignedAdmissionId(null);
        equipment.setStatus("AVAILABLE");
        equipment.setUpdatedBy(userId);
        equipmentRepository.save(equipment);
        log.info("Equipment released: equipmentId={}", equipmentId);
    }

    private EquipmentResponse toResponse(Equipment equipment) {
        return new EquipmentResponse(
                equipment.getId(), equipment.getName(), equipment.getEquipmentType(),
                equipment.getSerialNumber(), equipment.getLocation(), equipment.getStatus(),
                equipment.getAssignedAdmissionId(), equipment.getAssignedWardId(),
                equipment.getCreatedAt(), equipment.getUpdatedAt()
        );
    }
}
