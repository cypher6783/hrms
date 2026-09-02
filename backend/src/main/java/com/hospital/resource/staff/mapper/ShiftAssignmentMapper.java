package com.hospital.resource.staff.mapper;

import com.hospital.resource.staff.dto.ShiftAssignmentResponse;
import com.hospital.resource.staff.entity.ShiftAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

public interface ShiftAssignmentMapper {

    ShiftAssignmentResponse toResponse(ShiftAssignment assignment);

    List<ShiftAssignmentResponse> toResponseList(List<ShiftAssignment> assignments);
}
