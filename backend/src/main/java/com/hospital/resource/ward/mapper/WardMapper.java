package com.hospital.resource.ward.mapper;

import com.hospital.resource.ward.dto.WardRequest;
import com.hospital.resource.ward.dto.WardResponse;
import com.hospital.resource.ward.dto.WardStatusResponse;
import com.hospital.resource.ward.entity.Ward;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WardMapper {

    WardMapper INSTANCE = Mappers.getMapper(WardMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Ward toEntity(WardRequest request);

    WardResponse toResponse(Ward ward);

    @Mapping(target = "wardId", source = "ward.id")
    @Mapping(target = "wardName", source = "ward.name")
    @Mapping(target = "totalBeds", source = "totalBeds")
    @Mapping(target = "availableBeds", source = "availableBeds")
    @Mapping(target = "occupiedBeds", source = "occupiedBeds")
    @Mapping(target = "cleaningBeds", source = "cleaningBeds")
    @Mapping(target = "occupancyRate", source = "occupancyRate")
    WardStatusResponse toStatusResponse(Ward ward, long totalBeds, long availableBeds, long occupiedBeds, long cleaningBeds, double occupancyRate);

    List<WardResponse> toResponseList(List<Ward> wards);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(WardRequest request, @MappingTarget Ward ward);
}
