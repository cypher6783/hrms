package com.hospital.resource.bed.mapper;

import com.hospital.resource.bed.dto.BedAvailabilityResponse;
import com.hospital.resource.bed.dto.BedRequest;
import com.hospital.resource.bed.dto.BedResponse;
import com.hospital.resource.bed.entity.Bed;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BedMapper {

    BedMapper INSTANCE = Mappers.getMapper(BedMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "currentAdmissionId", ignore = true)
    @Mapping(target = "lastMaintenanceAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Bed toEntity(BedRequest request);

    BedResponse toResponse(Bed bed);

    @Mapping(target = "wardId", source = "wardId")
    @Mapping(target = "wardName", ignore = true)
    @Mapping(target = "totalBeds", source = "totalBeds")
    @Mapping(target = "availableBeds", source = "availableBeds")
    @Mapping(target = "reservedBeds", ignore = true)
    @Mapping(target = "occupiedBeds", source = "occupiedBeds")
    BedAvailabilityResponse toAvailabilityResponse(java.util.UUID wardId, long totalBeds, long availableBeds, long occupiedBeds);

    List<BedResponse> toResponseList(List<Bed> beds);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "wardId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "currentAdmissionId", ignore = true)
    @Mapping(target = "lastMaintenanceAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(BedRequest request, @MappingTarget Bed bed);
}
