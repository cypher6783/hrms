package com.hospital.resource.staff.service;

import com.hospital.resource.common.dto.PagedResponse;
import com.hospital.resource.common.event.DomainEventPublisher;
import com.hospital.resource.common.event.shift.ShiftAssignedEvent;
import com.hospital.resource.common.exception.ConflictException;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.staff.domain.ShiftDomainService;
import com.hospital.resource.staff.dto.*;
import com.hospital.resource.staff.entity.ShiftAssignment;
import com.hospital.resource.staff.entity.StaffShift;
import com.hospital.resource.staff.mapper.ShiftAssignmentMapper;
import com.hospital.resource.staff.mapper.ShiftMapper;
import com.hospital.resource.staff.repository.ShiftAssignmentRepository;
import com.hospital.resource.staff.repository.StaffShiftRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final ShiftDomainService shiftDomainService;
    private final ShiftMapper shiftMapper;
    private final ShiftAssignmentMapper assignmentMapper;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public ShiftResponse createShift(ShiftRequest request, UUID userId) {
        StaffShift shift = shiftMapper.toEntity(request);
        shift.setMinRequiredStaff(request.minRequiredStaff() != null ? request.minRequiredStaff() : 1);
        shift.setMaxStaff(request.maxStaff() != null ? request.maxStaff() : 10);
        shift.setStatus("SCHEDULED");
        shift.setCreatedBy(userId);
        shift.setUpdatedBy(userId);

        shiftDomainService.validateShiftCreation(shift);

        shift = shiftRepository.save(shift);
        log.info("Shift created: shiftId={}, shiftName={}", shift.getId(), shift.getShiftName());
        return shiftMapper.toResponse(shift);
    }

    @Transactional(readOnly = true)
    public ShiftResponse getShift(UUID id) {
        StaffShift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift", id.toString()));
        return shiftMapper.toResponse(shift);
    }

    @Transactional(readOnly = true)
    public ShiftSummaryResponse getShiftSummary(UUID id) {
        StaffShift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift", id.toString()));
        return shiftMapper.toSummary(shift);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ShiftResponse> searchShifts(ShiftSearchRequest request) {
        PageRequest pageRequest = PageRequest.of(request.page(), request.size(),
                Sort.by("shiftDate").descending().and(Sort.by("startTime")));

        Page<StaffShift> page = shiftRepository.searchShifts(
                request.wardId(), request.shiftDateFrom(), request.shiftDateTo(),
                request.shiftName(), request.status(), pageRequest);

        List<ShiftResponse> content = page.getContent().stream()
                .map(shiftMapper::toResponse)
                .toList();

        return PagedResponse.of(content, request.page(), request.size(), page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<ShiftSummaryResponse> getShiftsByWardAndDate(UUID wardId, LocalDate date) {
        return shiftRepository.findByWardIdAndShiftDate(wardId, date).stream()
                .map(shiftMapper::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShiftResponse> getShiftCalendar(LocalDate startDate, LocalDate endDate, UUID wardId) {
        List<StaffShift> shifts;
        if (wardId != null) {
            shifts = shiftRepository.findShiftsByWardAndDateRange(wardId, startDate, endDate);
        } else {
            shifts = shiftRepository.findShiftsInDateRange(startDate, endDate);
        }
        return shiftMapper.toResponseList(shifts);
    }

    @Transactional
    public ShiftAssignmentResponse assignStaff(ShiftAssignmentRequest request, UUID userId) {
        StaffShift shift = shiftRepository.findById(request.shiftId())
                .orElseThrow(() -> new ResourceNotFoundException("Shift", request.shiftId().toString()));

        shiftDomainService.validateStaffAvailability(request.staffId());
        shiftDomainService.validateNoOverlap(request.staffId(), request.shiftId());

        if (assignmentRepository.existsByStaffIdAndShiftId(request.staffId(), request.shiftId())) {
            throw new ConflictException("Staff already assigned to this shift");
        }

        shiftDomainService.validateShiftCapacity(shift);

        ShiftAssignment assignment = ShiftAssignment.builder()
                .staffId(request.staffId())
                .shiftId(request.shiftId())
                .assignedBy(userId)
                .status("CONFIRMED")
                .build();

        assignment = assignmentRepository.save(assignment);

        eventPublisher.publish(new ShiftAssignedEvent(
                this, request.shiftId(), request.staffId(), userId));

        log.info("Staff assigned to shift: shiftId={}, staffId={}", request.shiftId(), request.staffId());
        return assignmentMapper.toResponse(assignment);
    }

    @Transactional
    public void removeAssignment(UUID assignmentId, UUID userId) {
        ShiftAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift assignment", assignmentId.toString()));

        assignment.setStatus("CANCELLED");
        assignmentRepository.save(assignment);

        log.info("Shift assignment removed: assignmentId={}", assignmentId);
    }

    @Transactional(readOnly = true)
    public List<ShiftAssignmentResponse> getShiftAssignments(UUID shiftId) {
        return assignmentRepository.findByShiftId(shiftId).stream()
                .map(assignmentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StaffingLevelResponse getStaffingLevel(UUID shiftId) {
        return shiftDomainService.calculateStaffingLevel(shiftId);
    }

    @Transactional(readOnly = true)
    public ShiftStatsResponse getShiftStats() {
        long scheduledCount = shiftRepository.countByStatusGrouped().stream()
                .filter(row -> "SCHEDULED".equals(row[0]))
                .map(row -> (Long) row[1])
                .findFirst().orElse(0L);

        long todayCount = shiftRepository.countByShiftDate(LocalDate.now());
        return new ShiftStatsResponse(scheduledCount, todayCount);
    }

    public record ShiftStatsResponse(
            long scheduledShifts,
            long todayShifts
    ) {}
}
