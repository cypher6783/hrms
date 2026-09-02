package com.hospital.resource.staff;

import com.hospital.resource.common.event.DomainEventPublisher;
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
import com.hospital.resource.staff.service.ShiftApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShiftApplicationServiceTest {

    @Mock
    private StaffShiftRepository shiftRepository;
    @Mock
    private ShiftAssignmentRepository assignmentRepository;
    @Mock
    private ShiftDomainService shiftDomainService;
    @Mock
    private ShiftMapper shiftMapper;
    @Mock
    private ShiftAssignmentMapper assignmentMapper;
    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private ShiftApplicationService shiftService;

    private StaffShift testShift;
    private ShiftRequest testRequest;
    private ShiftResponse testResponse;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

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
                .createdBy(userId)
                .build();

        testRequest = new ShiftRequest(
                "Morning Shift", LocalDate.now(),
                LocalTime.of(8, 0), LocalTime.of(16, 0),
                testShift.getWardId(), 3, 10
        );

        testResponse = new ShiftResponse(
                testShift.getId(),
                testShift.getShiftName(),
                testShift.getShiftDate(),
                testShift.getStartTime(),
                testShift.getEndTime(),
                testShift.getWardId(),
                testShift.getMinRequiredStaff(),
                testShift.getMaxStaff(),
                testShift.getStatus(),
                Instant.now(), Instant.now()
        );
    }

    @Test
    void createShift_Success() {
        when(shiftMapper.toEntity(any(ShiftRequest.class))).thenReturn(testShift);
        when(shiftRepository.save(any(StaffShift.class))).thenReturn(testShift);
        when(shiftMapper.toResponse(any(StaffShift.class))).thenReturn(testResponse);

        ShiftResponse result = shiftService.createShift(testRequest, userId);

        assertThat(result).isNotNull();
        assertThat(result.shiftName()).isEqualTo("Morning Shift");
        verify(shiftDomainService).validateShiftCreation(testShift);
        verify(shiftRepository).save(any(StaffShift.class));
    }

    @Test
    void getShift_Success() {
        when(shiftRepository.findById(testShift.getId())).thenReturn(Optional.of(testShift));
        when(shiftMapper.toResponse(testShift)).thenReturn(testResponse);

        ShiftResponse result = shiftService.getShift(testShift.getId());

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(testShift.getId());
    }

    @Test
    void getShift_NotFound() {
        when(shiftRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shiftService.getShift(UUID.randomUUID()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void assignStaff_Success() {
        when(shiftRepository.findById(testShift.getId())).thenReturn(Optional.of(testShift));
        when(assignmentRepository.existsByStaffIdAndShiftId(any(UUID.class), any(UUID.class))).thenReturn(false);

        ShiftAssignment assignment = ShiftAssignment.builder()
                .id(UUID.randomUUID())
                .staffId(UUID.randomUUID())
                .shiftId(testShift.getId())
                .status("CONFIRMED")
                .assignedBy(userId)
                .build();
        when(assignmentRepository.save(any(ShiftAssignment.class))).thenReturn(assignment);

        ShiftAssignmentResponse assignmentResponse = new ShiftAssignmentResponse(
                assignment.getId(), assignment.getStaffId(), assignment.getShiftId(),
                "CONFIRMED", assignment.getAssignedBy(), Instant.now()
        );
        when(assignmentMapper.toResponse(any(ShiftAssignment.class))).thenReturn(assignmentResponse);

        ShiftAssignmentRequest request = new ShiftAssignmentRequest(assignment.getStaffId(), testShift.getId());
        ShiftAssignmentResponse result = shiftService.assignStaff(request, userId);

        assertThat(result).isNotNull();
        verify(shiftDomainService).validateStaffAvailability(request.staffId());
        verify(shiftDomainService).validateNoOverlap(request.staffId(), request.shiftId());
        verify(shiftDomainService).validateShiftCapacity(testShift);
        verify(eventPublisher).publish(any());
    }

    @Test
    void assignStaff_DuplicateAssignment() {
        when(shiftRepository.findById(testShift.getId())).thenReturn(Optional.of(testShift));
        when(assignmentRepository.existsByStaffIdAndShiftId(any(UUID.class), any(UUID.class))).thenReturn(true);

        ShiftAssignmentRequest request = new ShiftAssignmentRequest(UUID.randomUUID(), testShift.getId());

        assertThatThrownBy(() -> shiftService.assignStaff(request, userId))
                .isInstanceOf(ConflictException.class);
    }
}
