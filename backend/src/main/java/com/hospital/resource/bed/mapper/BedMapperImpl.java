package com.hospital.resource.bed.mapper;

import com.hospital.resource.bed.dto.BedAvailabilityResponse;
import com.hospital.resource.bed.dto.BedRequest;
import com.hospital.resource.bed.dto.BedResponse;
import com.hospital.resource.bed.entity.Bed;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class BedMapperImpl implements BedMapper {

    @Override
    public Bed toEntity(BedRequest request) {
        if (request == null) return null;
        return Bed.builder()
                .bedNumber(request.bedNumber())
                .wardId(request.wardId())
                .bedType(request.bedType())
                .isIsolation(request.isIsolationCapable() != null ? request.isIsolationCapable() : false)
                .build();
    }

    @Override
    public BedResponse toResponse(Bed bed) {
        if (bed == null) return null;
        return new BedResponse(
                bed.getId(),
                bed.getBedNumber(),
                bed.getWardId(),
                bed.getBedType(),
                bed.getIsIsolation(),
                bed.getStatus(),
                bed.getCurrentAdmissionId(),
                bed.getLastMaintenanceAt(),
                bed.getCreatedAt(),
                bed.getUpdatedAt()
        );
    }

    @Override
    public BedAvailabilityResponse toAvailabilityResponse(UUID wardId, long totalBeds, long availableBeds, long occupiedBeds) {
        long reserved = 0;
        return new BedAvailabilityResponse(wardId, null, (int) totalBeds, (int) availableBeds, (int) reserved, (int) occupiedBeds);
    }

    @Override
    public List<BedResponse> toResponseList(List<Bed> beds) {
        if (beds == null) return List.of();
        return beds.stream().map(this::toResponse).toList();
    }

    @Override
    public void updateEntity(BedRequest request, Bed bed) {
        if (request == null || bed == null) return;
        if (request.bedNumber() != null) bed.setBedNumber(request.bedNumber());
        if (request.bedType() != null) bed.setBedType(request.bedType());
        if (request.isIsolationCapable() != null) bed.setIsIsolation(request.isIsolationCapable());
    }
}
