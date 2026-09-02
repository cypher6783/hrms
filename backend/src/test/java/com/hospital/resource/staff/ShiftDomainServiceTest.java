package com.hospital.resource.staff;

import com.hospital.resource.common.exception.ConflictException;
import com.hospital.resource.common.exception.ValidationException;
import com.hospital.resource.staff.domain.ShiftDomainService;
import com.hospital.resource.staff.dto.StaffingLevelResponse;
import com.hospital.resource.staff.entity.Staff;
import com.hospital.resource.staff.entity.StaffShift;
import com.hospital.resource.staff.repository.ShiftAssignmentRepository;
import com.hospital.resource.staff.repository.StaffRepository;
import com.hospital.resource.staff.repository.StaffShiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiftDomainServiceTest {

    @Mock
    private StaffShiftRepository shiftRepository;
    @Mock
    private ShiftAssignmentRepository assignmentRepository;
    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private ShiftDomainService shiftDomainService;

    private Staff testStaff;
    private StaffShift testShift;

    @BeforeEach
    void setUp() {
        testStaff = Staff.builder()
                .id(UUID.randomUUID())
                .availabilityStatus("ACTIVE")
                .certificationExpiry(null)
                .build();

        testShift = StaffShift.builder()
                .id(UUID.randomUUID())
                .shiftName("Morning Shift")
                .shiftDate(LocalDate.now())
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .wardId(UUID.randomUUID())
                .minRequiredStaff(3)
                .maxStaff(10)
                .status("SCHEDULED")
                .build();
    }

    @Test
    void validateStaffAvailability_Success() {
        when(staffRepository.findById(testStaff.getId())).thenReturn(Optional.of(testStaff));

        shiftDomainService.validateStaffAvailability(testStaff.getId());
    }

    @Test
    void validateStaffAvailability_Inactive() {
        testStaff.setAvailabilityStatus("INACTIVE");
        when(staffRepository.findById(testStaff.getId())).thenReturn(Optional.of(testStaff));

        assertThatThrownBy(() -> shiftDomainService.validateStaffAvailability(testStaff.getId()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void validateShiftCapacity_Success() {
        when(assignmentRepository.countByShiftId(testShift.getId())).thenReturn(5L);

        shiftDomainService.validateShiftCapacity(testShift);
    }

    @Test
    void validateShiftCapacity_AtCapacity() {
        when(assignmentRepository.countByShiftId(testShift.getId())).thenReturn(10L);

        assertThatThrownBy(() -> shiftDomainService.validateShiftCapacity(testShift))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void calculateStaffingLevel_FullyStaffed() {
        when(shiftRepository.findById(testShift.getId())).thenReturn(Optional.of(testShift));
        when(assignmentRepository.countByShiftId(testShift.getId())).thenReturn(5L);

        StaffingLevelResponse result = shiftDomainService.calculateStaffingLevel(testShift.getId());

        assertThat(result).isNotNull();
        assertThat(result.isFullyStaffed()).isTrue();
        assertThat(result.deficit()).isEqualTo(0);
    }

    @Test
    void calculateStaffingLevel_Understaffed() {
        when(shiftRepository.findById(testShift.getId())).thenReturn(Optional.of(testShift));
        when(assignmentRepository.countByShiftId(testShift.getId())).thenReturn(1L);

        StaffingLevelResponse result = shiftDomainService.calculateStaffingLevel(testShift.getId());

        assertThat(result).isNotNull();
        assertThat(result.isFullyStaffed()).isFalse();
        assertThat(result.deficit()).isEqualTo(2);
    }

    @Test
    void validateShiftCreation_InvalidTimeRange() {
        StaffShift invalidShift = StaffShift.builder()
                .startTime(LocalTime.of(16, 0))
                .endTime(LocalTime.of(8, 0))
                .minRequiredStaff(3)
                .maxStaff(10)
                .build();

        assertThatThrownBy(() -> shiftDomainService.validateShiftCreation(invalidShift))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void validateShiftCreation_MinExceedsMax() {
        StaffShift invalidShift = StaffShift.builder()
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .minRequiredStaff(15)
                .maxStaff(10)
                .build();

        assertThatThrownBy(() -> shiftDomainService.validateShiftCreation(invalidShift))
                .isInstanceOf(ValidationException.class);
    }
}
