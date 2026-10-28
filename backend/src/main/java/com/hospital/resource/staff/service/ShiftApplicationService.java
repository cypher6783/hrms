package com.hospital.resource.staff.service;

import com.hospital.resource.common.exception.ConflictException;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.staff.dto.*;
import com.hospital.resource.staff.entity.ShiftAssignment;
import com.hospital.resource.staff.entity.StaffShift;
import com.hospital.resource.staff.repository.ShiftAssignmentRepository;
import com.hospital.resource.staff.repository.StaffShiftRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShiftApplicationService {

    private final StaffShiftRepository shiftRepository;
    private final ShiftAssignmentRepository assignmentRepository;

    @Transactional
    public ShiftResponse createShift(ShiftRequest request, UUID userId) {
        StaffShift shift = StaffShift.builder()
                .shiftName(request.shiftName())
                .shiftDate(request.shiftDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .wardId(request.wardId())
                .minRequiredStaff(request.minRequiredStaff() != null ? request.minRequiredStaff() : 1)
                .maxStaff(request.maxStaff() != null ? request.maxStaff() : 10)
                .status("SCHEDULED")
                .createdBy(userId)
                .updatedBy(userId)
                .build();

        shift = shiftRepository.save(shift);
        log.info("Shift created: shiftId={}, shiftName={}", shift.getId(), shift.getShiftName());
        return toResponse(shift);
    }

    @Transactional(readOnly = true)
    public ShiftResponse getShift(UUID id) {
        StaffShift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift", id.toString()));
        return toResponse(shift);
    }

    @Transactional(readOnly = true)
    public List<ShiftResponse> getShiftsByWardAndDate(UUID wardId, LocalDate date) {
        return shiftRepository.findByWardIdAndShiftDate(wardId, date).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ShiftAssignmentResponse assignStaff(ShiftAssignmentRequest request, UUID userId) {
        StaffShift shift = shiftRepository.findById(request.shiftId())
                .orElseThrow(() -> new ResourceNotFoundException("Shift", request.shiftId().toString()));

        if (assignmentRepository.existsByStaffIdAndShiftId(request.staffId(), request.shiftId())) {
            throw new ConflictException("Staff already assigned to this shift");
        }

        long currentCount = assignmentRepository.countByShiftId(request.shiftId());
        if (currentCount >= shift.getMaxStaff()) {
            throw new ConflictException("Shift is at maximum capacity");
        }

        ShiftAssignment assignment = ShiftAssignment.builder()
                .staffId(request.staffId())
                .shiftId(request.shiftId())
                .assignedBy(userId)
                .status("CONFIRMED")
                .build();

        assignment = assignmentRepository.save(assignment);
        log.info("Staff assigned to shift: shiftId={}, staffId={}", request.shiftId(), request.staffId());
        return toAssignmentResponse(assignment);
    }

    @Transactional(readOnly = true)
    public List<ShiftAssignmentResponse> getShiftAssignments(UUID shiftId) {
        return assignmentRepository.findByShiftId(shiftId).stream()
                .map(this::toAssignmentResponse)
                .toList();
    }

    private ShiftResponse toResponse(StaffShift shift) {
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

    private ShiftAssignmentResponse toAssignmentResponse(ShiftAssignment assignment) {
        return new ShiftAssignmentResponse(
                assignment.getId(),
                assignment.getStaffId(),
                assignment.getShiftId(),
                assignment.getStatus(),
                assignment.getAssignedBy(),
                assignment.getCreatedAt()
        );
    }
}
