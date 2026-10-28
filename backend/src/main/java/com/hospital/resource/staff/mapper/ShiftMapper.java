package com.hospital.resource.staff.mapper;

import com.hospital.resource.staff.dto.ShiftRequest;
import com.hospital.resource.staff.dto.ShiftResponse;
import com.hospital.resource.staff.dto.ShiftSummaryResponse;
import com.hospital.resource.staff.entity.StaffShift;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

public interface ShiftMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    StaffShift toEntity(ShiftRequest request);

    ShiftResponse toResponse(StaffShift shift);

    ShiftSummaryResponse toSummary(StaffShift shift);

    List<ShiftResponse> toResponseList(List<StaffShift> shifts);

    List<ShiftSummaryResponse> toSummaryList(List<StaffShift> shifts);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(ShiftRequest request, @MappingTarget StaffShift shift);
}
