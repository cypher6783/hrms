package com.hospital.resource.integration;

import com.hospital.resource.common.exception.ConflictException;
import com.hospital.resource.staff.dto.*;
import com.hospital.resource.staff.entity.Staff;
import com.hospital.resource.staff.entity.StaffShift;
import com.hospital.resource.staff.repository.StaffRepository;
import com.hospital.resource.staff.repository.StaffShiftRepository;
import com.hospital.resource.staff.service.ShiftApplicationService;
import com.hospital.resource.ward.entity.Ward;
import com.hospital.resource.ward.repository.WardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ShiftWorkflowIntegrationTest {

    @Autowired
    private ShiftApplicationService shiftService;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StaffShiftRepository shiftRepository;

    @Autowired
    private WardRepository wardRepository;

    private Ward testWard;
    private Staff testStaff;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        testWard = Ward.builder()
                .name("Test Ward " + UUID.randomUUID().toString().substring(0, 8))
                .wardType("GENERAL")
                .maxBedCapacity(10)
                .isolationLevel("NONE")
                .status("ACTIVE")
                .build();
        testWard = wardRepository.save(testWard);

        testStaff = Staff.builder()
                .staffNumber("STF-" + UUID.randomUUID().toString().substring(0, 8))
                .fullName("Dr. Test")
                .role("DOCTOR")
                .specialization("GENERAL")
                .certificationStatus("CURRENT")
                .wardId(testWard.getId())
                .maxWorkloadThreshold(BigDecimal.valueOf(10))
                .availabilityStatus("ACTIVE")
                .createdBy(userId)
                .build();
        testStaff = staffRepository.save(testStaff);
    }

    @Test
    void createAndAssignShift() {
        // 1. Create shift
        ShiftRequest shiftRequest = new ShiftRequest(
                "Morning Shift", LocalDate.now(),
                LocalTime.of(8, 0), LocalTime.of(16, 0),
                testWard.getId(), 3, 10
        );
        ShiftResponse shift = shiftService.createShift(shiftRequest, userId);

        assertThat(shift).isNotNull();
        assertThat(shift.shiftName()).isEqualTo("Morning Shift");
        assertThat(shift.status()).isEqualTo("SCHEDULED");

        // 2. Assign staff to shift
        ShiftAssignmentRequest assignmentRequest = new ShiftAssignmentRequest(
                testStaff.getId(), shift.id()
        );
        ShiftAssignmentResponse assignment = shiftService.assignStaff(assignmentRequest, userId);

        assertThat(assignment).isNotNull();
        assertThat(assignment.staffId()).isEqualTo(testStaff.getId());
        assertThat(assignment.shiftId()).isEqualTo(shift.id());
        assertThat(assignment.status()).isEqualTo("CONFIRMED");

        // 3. Check staffing level
        StaffingLevelResponse staffingLevel = shiftService.getStaffingLevel(shift.id());
        assertThat(staffingLevel.isFullyStaffed()).isFalse(); // 1 assigned, 3 required
        assertThat(staffingLevel.deficit()).isEqualTo(2);
    }

    @Test
    void shiftOverlapDetection() {
        // Create first shift
        ShiftRequest shift1Request = new ShiftRequest(
                "Morning Shift", LocalDate.now(),
                LocalTime.of(8, 0), LocalTime.of(16, 0),
                testWard.getId(), 3, 10
        );
        ShiftResponse shift1 = shiftService.createShift(shift1Request, userId);

        // Assign staff to first shift
        ShiftAssignmentRequest assignment1 = new ShiftAssignmentRequest(
                testStaff.getId(), shift1.id()
        );
        shiftService.assignStaff(assignment1, userId);

        // Create overlapping shift
        ShiftRequest shift2Request = new ShiftRequest(
                "Afternoon Shift", LocalDate.now(),
                LocalTime.of(12, 0), LocalTime.of(20, 0),
                testWard.getId(), 3, 10
        );
        ShiftResponse shift2 = shiftService.createShift(shift2Request, userId);

        // Try to assign same staff to overlapping shift
        ShiftAssignmentRequest assignment2 = new ShiftAssignmentRequest(
                testStaff.getId(), shift2.id()
        );

        assertThatThrownBy(() -> shiftService.assignStaff(assignment2, userId))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("overlapping");
    }

    @Test
    void shiftCapacityValidation() {
        // Create shift with max 2 staff
        ShiftRequest shiftRequest = new ShiftRequest(
                "Limited Shift", LocalDate.now(),
                LocalTime.of(8, 0), LocalTime.of(16, 0),
                testWard.getId(), 1, 2
        );
        ShiftResponse shift = shiftService.createShift(shiftRequest, userId);

        // Create additional staff
        Staff staff2 = Staff.builder()
                .staffNumber("STF-" + UUID.randomUUID().toString().substring(0, 8))
                .fullName("Nurse Test")
                .role("NURSE")
                .wardId(testWard.getId())
                .maxWorkloadThreshold(BigDecimal.valueOf(10))
                .availabilityStatus("ACTIVE")
                .createdBy(userId)
                .build();
        staff2 = staffRepository.save(staff2);

        final Staff savedStaff3 = staffRepository.save(Staff.builder()
                .staffNumber("STF-" + UUID.randomUUID().toString().substring(0, 8))
                .fullName("Nurse Test 2")
                .role("NURSE")
                .wardId(testWard.getId())
                .maxWorkloadThreshold(BigDecimal.valueOf(10))
                .availabilityStatus("ACTIVE")
                .createdBy(userId)
                .build());

        // Assign two staff (should succeed)
        shiftService.assignStaff(new ShiftAssignmentRequest(testStaff.getId(), shift.id()), userId);
        shiftService.assignStaff(new ShiftAssignmentRequest(staff2.getId(), shift.id()), userId);

        // Try to assign third staff (should fail - at capacity)
        assertThatThrownBy(() -> shiftService.assignStaff(new ShiftAssignmentRequest(savedStaff3.getId(), shift.id()), userId))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("capacity");
    }
}
