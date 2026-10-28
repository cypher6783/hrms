package com.hospital.resource.equipment.mapper;

import com.hospital.resource.equipment.dto.EquipmentAllocationResponse;
import com.hospital.resource.equipment.dto.EquipmentRequest;
import com.hospital.resource.equipment.dto.EquipmentResponse;
import com.hospital.resource.equipment.entity.Equipment;
import com.hospital.resource.equipment.entity.EquipmentAllocation;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

public interface EquipmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignedAdmissionId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Equipment toEntity(EquipmentRequest request);

    EquipmentResponse toResponse(Equipment equipment);

    List<EquipmentResponse> toResponseList(List<Equipment> equipmentList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignedAdmissionId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(EquipmentRequest request, @MappingTarget Equipment equipment);

    EquipmentAllocationResponse toAllocationResponse(EquipmentAllocation allocation);

    List<EquipmentAllocationResponse> toAllocationResponseList(List<EquipmentAllocation> allocations);
}
