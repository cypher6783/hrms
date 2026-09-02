package com.hospital.resource.staff.mapper;

import com.hospital.resource.staff.dto.StaffRequest;
import com.hospital.resource.staff.dto.StaffResponse;
import com.hospital.resource.staff.dto.StaffSummaryResponse;
import com.hospital.resource.staff.entity.Staff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

public interface StaffMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "staffNumber", ignore = true)
    @Mapping(target = "certificationStatus", ignore = true)
    @Mapping(target = "maxWorkloadThreshold", ignore = true)
    @Mapping(target = "availabilityStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Staff toEntity(StaffRequest request);

    StaffResponse toResponse(Staff staff);

    StaffSummaryResponse toSummary(Staff staff);

    List<StaffResponse> toResponseList(List<Staff> staffList);

    List<StaffSummaryResponse> toSummaryList(List<Staff> staffList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "staffNumber", ignore = true)
    @Mapping(target = "certificationStatus", ignore = true)
    @Mapping(target = "maxWorkloadThreshold", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(StaffRequest request, @MappingTarget Staff staff);
}
