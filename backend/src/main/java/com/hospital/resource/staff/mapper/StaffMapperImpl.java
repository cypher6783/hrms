package com.hospital.resource.staff.mapper;

import com.hospital.resource.staff.dto.StaffRequest;
import com.hospital.resource.staff.dto.StaffResponse;
import com.hospital.resource.staff.dto.StaffSummaryResponse;
import com.hospital.resource.staff.entity.Staff;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StaffMapperImpl implements StaffMapper {

    @Override
    public Staff toEntity(StaffRequest request) {
        if (request == null) return null;
        return Staff.builder()
                .fullName(request.fullName())
                .role(request.role())
                .specialization(request.specialization())
                .certificationStatus(request.certificationStatus())
                .shiftPreference(request.shiftPreference())
                .wardId(request.wardId())
                .maxWorkloadThreshold(request.maxWorkloadThreshold())
                .availabilityStatus(request.availabilityStatus() != null ? request.availabilityStatus() : "ACTIVE")
                .build();
    }

    @Override
    public StaffResponse toResponse(Staff staff) {
        if (staff == null) return null;
        return new StaffResponse(
                staff.getId(),
                staff.getStaffNumber(),
                staff.getFullName(),
                staff.getRole(),
                staff.getSpecialization(),
                staff.getCertificationStatus(),
                staff.getShiftPreference(),
                staff.getWardId(),
                staff.getMaxWorkloadThreshold(),
                staff.getAvailabilityStatus(),
                staff.getCreatedAt(),
                staff.getUpdatedAt()
        );
    }

    @Override
    public StaffSummaryResponse toSummary(Staff staff) {
        if (staff == null) return null;
        return new StaffSummaryResponse(
                staff.getId(),
                staff.getStaffNumber(),
                staff.getFullName(),
                staff.getRole(),
                staff.getSpecialization(),
                staff.getAvailabilityStatus()
        );
    }

    @Override
    public List<StaffResponse> toResponseList(List<Staff> staffList) {
        if (staffList == null) return List.of();
        return staffList.stream().map(this::toResponse).toList();
    }

    @Override
    public List<StaffSummaryResponse> toSummaryList(List<Staff> staffList) {
        if (staffList == null) return List.of();
        return staffList.stream().map(this::toSummary).toList();
    }

    @Override
    public void updateEntity(StaffRequest request, Staff staff) {
        if (request == null || staff == null) return;
        if (request.fullName() != null) staff.setFullName(request.fullName());
        if (request.role() != null) staff.setRole(request.role());
        if (request.specialization() != null) staff.setSpecialization(request.specialization());
        if (request.certificationStatus() != null) staff.setCertificationStatus(request.certificationStatus());
        if (request.shiftPreference() != null) staff.setShiftPreference(request.shiftPreference());
        if (request.wardId() != null) staff.setWardId(request.wardId());
        if (request.maxWorkloadThreshold() != null) staff.setMaxWorkloadThreshold(request.maxWorkloadThreshold());
        if (request.availabilityStatus() != null) staff.setAvailabilityStatus(request.availabilityStatus());
    }
}
