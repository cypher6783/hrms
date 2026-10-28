package com.hospital.resource.ward.mapper;

import com.hospital.resource.ward.dto.WardRequest;
import com.hospital.resource.ward.dto.WardResponse;
import com.hospital.resource.ward.dto.WardStatusResponse;
import com.hospital.resource.ward.entity.Ward;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WardMapperImpl implements WardMapper {

    @Override
    public Ward toEntity(WardRequest request) {
        if (request == null) return null;
        return Ward.builder()
                .name(request.name())
                .wardType(request.wardType())
                .maxBedCapacity(request.maxBedCapacity())
                .isolationLevel(request.isolationLevel() != null ? request.isolationLevel() : "NONE")
                .equipmentZone(request.equipmentZone())
                .build();
    }

    @Override
    public WardResponse toResponse(Ward ward) {
        if (ward == null) return null;
        return new WardResponse(
                ward.getId(),
                ward.getName(),
                ward.getWardType(),
                ward.getMaxBedCapacity(),
                ward.getIsolationLevel(),
                ward.getEquipmentZone(),
                ward.getStatus(),
                ward.getCreatedAt(),
                ward.getUpdatedAt()
        );
    }

    @Override
    public WardStatusResponse toStatusResponse(Ward ward, long totalBeds, long availableBeds, long occupiedBeds, long cleaningBeds, double occupancyRate) {
        if (ward == null) return null;
        return new WardStatusResponse(
                ward.getId(),
                ward.getName(),
                (int) totalBeds,
                (int) availableBeds,
                (int) occupiedBeds,
                (int) cleaningBeds,
                occupancyRate
        );
    }

    @Override
    public List<WardResponse> toResponseList(List<Ward> wards) {
        if (wards == null) return List.of();
        return wards.stream().map(this::toResponse).toList();
    }

    @Override
    public void updateEntity(WardRequest request, Ward ward) {
        if (request == null || ward == null) return;
        if (request.name() != null) ward.setName(request.name());
        if (request.wardType() != null) ward.setWardType(request.wardType());
        if (request.maxBedCapacity() != null) ward.setMaxBedCapacity(request.maxBedCapacity());
        if (request.isolationLevel() != null) ward.setIsolationLevel(request.isolationLevel());
        if (request.equipmentZone() != null) ward.setEquipmentZone(request.equipmentZone());
    }
}
