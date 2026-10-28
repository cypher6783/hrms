package com.hospital.resource.equipment.mapper;

import com.hospital.resource.equipment.dto.EquipmentAllocationResponse;
import com.hospital.resource.equipment.dto.EquipmentRequest;
import com.hospital.resource.equipment.dto.EquipmentResponse;
import com.hospital.resource.equipment.entity.Equipment;
import com.hospital.resource.equipment.entity.EquipmentAllocation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EquipmentMapperImpl implements EquipmentMapper {

    @Override
    public Equipment toEntity(EquipmentRequest request) {
        if (request == null) return null;
        return Equipment.builder()
                .name(request.name())
                .equipmentType(request.equipmentType())
                .serialNumber(request.serialNumber())
                .location(request.location())
                .assignedWardId(request.assignedWardId())
                .build();
    }

    @Override
    public EquipmentResponse toResponse(Equipment equipment) {
        if (equipment == null) return null;
        return new EquipmentResponse(
                equipment.getId(),
                equipment.getName(),
                equipment.getEquipmentType(),
                equipment.getSerialNumber(),
                equipment.getLocation(),
                equipment.getStatus(),
                equipment.getAssignedAdmissionId(),
                equipment.getAssignedWardId(),
                equipment.getCreatedAt(),
                equipment.getUpdatedAt()
        );
    }

    @Override
    public List<EquipmentResponse> toResponseList(List<Equipment> equipmentList) {
        if (equipmentList == null) return List.of();
        return equipmentList.stream().map(this::toResponse).toList();
    }

    @Override
    public void updateEntity(EquipmentRequest request, Equipment equipment) {
        if (request == null || equipment == null) return;
        if (request.name() != null) equipment.setName(request.name());
        if (request.equipmentType() != null) equipment.setEquipmentType(request.equipmentType());
        if (request.serialNumber() != null) equipment.setSerialNumber(request.serialNumber());
        if (request.location() != null) equipment.setLocation(request.location());
        if (request.assignedWardId() != null) equipment.setAssignedWardId(request.assignedWardId());
    }

    @Override
    public EquipmentAllocationResponse toAllocationResponse(EquipmentAllocation allocation) {
        if (allocation == null) return null;
        return new EquipmentAllocationResponse(
                allocation.getId(),
                allocation.getEquipmentId(),
                allocation.getAdmissionId(),
                allocation.getAllocatedAt(),
                allocation.getReleasedAt(),
                allocation.getAllocatedBy()
        );
    }

    @Override
    public List<EquipmentAllocationResponse> toAllocationResponseList(List<EquipmentAllocation> allocations) {
        if (allocations == null) return List.of();
        return allocations.stream().map(this::toAllocationResponse).toList();
    }
}
