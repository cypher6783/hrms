package com.hospital.resource.staff.mapper;

import com.hospital.resource.staff.dto.ShiftRequest;
import com.hospital.resource.staff.dto.ShiftResponse;
import com.hospital.resource.staff.dto.ShiftSummaryResponse;
import com.hospital.resource.staff.entity.StaffShift;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShiftMapperImpl implements ShiftMapper {

    @Override
    public StaffShift toEntity(ShiftRequest request) {
        if (request == null) return null;
        return StaffShift.builder()
                .shiftName(request.shiftName())
                .shiftDate(request.shiftDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .wardId(request.wardId())
                .minRequiredStaff(request.minRequiredStaff())
                .maxStaff(request.maxStaff())
                .build();
    }

    @Override
    public ShiftResponse toResponse(StaffShift shift) {
        if (shift == null) return null;
        return new ShiftResponse(
                shift.getId(),
                shift.getShiftName(),
                shift.getShiftDate(),
                shift.getStartTime(),
                shift.getEndTime(),
                shift.getWardId(),
                shift.getMinRequiredStaff(),
                shift.getMaxStaff(),
                shift.getStatus(),
                shift.getCreatedAt(),
                shift.getUpdatedAt()
        );
    }

    @Override
    public ShiftSummaryResponse toSummary(StaffShift shift) {
        if (shift == null) return null;
        return new ShiftSummaryResponse(
                shift.getId(),
                shift.getShiftName(),
                shift.getShiftDate(),
                shift.getStartTime(),
                shift.getEndTime(),
                shift.getStatus()
        );
    }

    @Override
    public List<ShiftResponse> toResponseList(List<StaffShift> shifts) {
        if (shifts == null) return List.of();
        return shifts.stream().map(this::toResponse).toList();
    }

    @Override
    public List<ShiftSummaryResponse> toSummaryList(List<StaffShift> shifts) {
        if (shifts == null) return List.of();
        return shifts.stream().map(this::toSummary).toList();
    }

    @Override
    public void updateEntity(ShiftRequest request, StaffShift shift) {
        if (request == null || shift == null) return;
        if (request.shiftName() != null) shift.setShiftName(request.shiftName());
        if (request.shiftDate() != null) shift.setShiftDate(request.shiftDate());
        if (request.startTime() != null) shift.setStartTime(request.startTime());
        if (request.endTime() != null) shift.setEndTime(request.endTime());
        if (request.wardId() != null) shift.setWardId(request.wardId());
        if (request.minRequiredStaff() != null) shift.setMinRequiredStaff(request.minRequiredStaff());
        if (request.maxStaff() != null) shift.setMaxStaff(request.maxStaff());
    }
}
