package com.hospital.resource.staff.domain;

import com.hospital.resource.common.exception.ConflictException;
import com.hospital.resource.common.exception.ValidationException;
import com.hospital.resource.staff.dto.StaffingLevelResponse;
import com.hospital.resource.staff.entity.Staff;
import com.hospital.resource.staff.entity.StaffShift;
import com.hospital.resource.staff.repository.ShiftAssignmentRepository;
import com.hospital.resource.staff.repository.StaffRepository;
import com.hospital.resource.staff.repository.StaffShiftRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShiftDomainService {

    private final StaffShiftRepository shiftRepository;
    private final ShiftAssignmentRepository assignmentRepository;
    private final StaffRepository staffRepository;

    @Transactional(readOnly = true)
    public void validateNoOverlap(UUID staffId, UUID newShiftId) {
        StaffShift newShift = shiftRepository.findById(newShiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift", newShiftId.toString()));

        List<StaffShift> existingShifts = findOverlappingShifts(staffId, newShift.getShiftDate(),
                newShift.getStartTime(), newShift.getEndTime(), newShiftId);

        if (!existingShifts.isEmpty()) {
            throw new ConflictException("Staff member has an overlapping shift on " + newShift.getShiftDate());
        }
    }

    @Transactional(readOnly = true)
    public void validateStaffAvailability(UUID staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", staffId.toString()));

        if (!staff.isAvailable()) {
            throw new ValidationException("Staff member is not available. Status: " + staff.getAvailabilityStatus());
        }

        if (!staff.isCertificationCurrent()) {
            throw new ValidationException("Staff certification has expired");
        }
    }

    @Transactional(readOnly = true)
    public void validateShiftCapacity(StaffShift shift) {
        long currentCount = assignmentRepository.countByShiftId(shift.getId());
        if (currentCount >= shift.getMaxStaff()) {
            throw new ConflictException("Shift is at maximum capacity");
        }
    }

    @Transactional(readOnly = true)
    public StaffingLevelResponse calculateStaffingLevel(UUID shiftId) {
        StaffShift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift", shiftId.toString()));

        long assignedCount = assignmentRepository.countByShiftId(shiftId);
        int requiredCount = shift.getMinRequiredStaff();
        boolean isFullyStaffed = assignedCount >= requiredCount;
        int deficit = isFullyStaffed ? 0 : (int) (requiredCount - assignedCount);

        return new StaffingLevelResponse(
                shiftId,
                requiredCount,
                (int) assignedCount,
                shift.getMaxStaff(),
                isFullyStaffed,
                deficit
        );
    }

    @Transactional(readOnly = true)
    public List<StaffShift> findOverlappingShifts(UUID staffId, java.time.LocalDate shiftDate,
                                                   LocalTime startTime, LocalTime endTime, UUID excludeShiftId) {
        List<StaffShift> dayShifts = shiftRepository.findByShiftDate(shiftDate);

        return dayShifts.stream()
                .filter(s -> !s.getId().equals(excludeShiftId))
                .filter(s -> assignmentRepository.existsByStaffIdAndShiftId(staffId, s.getId()))
                .filter(s -> s.getStartTime().isBefore(endTime) && s.getEndTime().isAfter(startTime))
                .toList();
    }

    @Transactional(readOnly = true)
    public void validateShiftCreation(StaffShift shift) {
        if (shift.getEndTime().isBefore(shift.getStartTime()) || shift.getEndTime().equals(shift.getStartTime())) {
            throw new ValidationException("Shift end time must be after start time");
        }
        if (shift.getMinRequiredStaff() > shift.getMaxStaff()) {
            throw new ValidationException("Minimum required staff cannot exceed maximum staff");
        }
    }

    private static class ResourceNotFoundException extends com.hospital.resource.common.exception.BusinessException {
        ResourceNotFoundException(String entity, String id) {
            super(entity + " not found with id: " + id,
                    org.springframework.http.HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
        }
    }
}
