package com.hospital.resource.staff.mapper;

import com.hospital.resource.staff.dto.ShiftAssignmentResponse;
import com.hospital.resource.staff.entity.ShiftAssignment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShiftAssignmentMapperImpl implements ShiftAssignmentMapper {

    @Override
    public ShiftAssignmentResponse toResponse(ShiftAssignment assignment) {
        if (assignment == null) return null;
        return new ShiftAssignmentResponse(
                assignment.getId(),
                assignment.getStaffId(),
                assignment.getShiftId(),
                assignment.getStatus(),
                assignment.getAssignedBy(),
                assignment.getCreatedAt()
        );
    }

    @Override
    public List<ShiftAssignmentResponse> toResponseList(List<ShiftAssignment> assignments) {
        if (assignments == null) return List.of();
        return assignments.stream().map(this::toResponse).toList();
    }
}
