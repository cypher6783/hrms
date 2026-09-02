package com.hospital.resource.staff;

import com.hospital.resource.common.event.DomainEventPublisher;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.staff.domain.WorkloadCalculator;
import com.hospital.resource.staff.dto.*;
import com.hospital.resource.staff.entity.Staff;
import com.hospital.resource.staff.mapper.StaffMapper;
import com.hospital.resource.staff.repository.StaffRepository;
import com.hospital.resource.staff.service.StaffApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffApplicationServiceTest {

    @Mock
    private StaffRepository staffRepository;
    @Mock
    private WorkloadCalculator workloadCalculator;
    @Mock
    private StaffMapper staffMapper;
    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private StaffApplicationService staffService;

    private Staff testStaff;
    private StaffRequest testRequest;
    private StaffResponse testResponse;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        testStaff = Staff.builder()
                .id(UUID.randomUUID())
                .staffNumber("STF-TEST0001")
                .fullName("Dr. John Smith")
                .role("DOCTOR")
                .specialization("CARDIOLOGY")
                .certificationStatus("CURRENT")
                .wardId(UUID.randomUUID())
                .maxWorkloadThreshold(BigDecimal.valueOf(10))
                .availabilityStatus("ACTIVE")
                .createdBy(userId)
                .build();

        testRequest = new StaffRequest(
                "Dr. John Smith", "DOCTOR", "CARDIOLOGY",
                "CURRENT", null, testStaff.getWardId(),
                BigDecimal.valueOf(10), "ACTIVE"
        );

        testResponse = new StaffResponse(
                testStaff.getId(),
                testStaff.getStaffNumber(),
                testStaff.getFullName(),
                testStaff.getRole(),
                testStaff.getSpecialization(),
                testStaff.getCertificationStatus(),
                testStaff.getCertificationExpiry(),
                testStaff.getWardId(),
                testStaff.getMaxWorkloadThreshold(),
                testStaff.getAvailabilityStatus(),
                Instant.now(), Instant.now()
        );
    }

    @Test
    void createStaff_Success() {
        when(staffMapper.toEntity(any(StaffRequest.class))).thenReturn(testStaff);
        when(staffRepository.save(any(Staff.class))).thenReturn(testStaff);
        when(staffMapper.toResponse(any(Staff.class))).thenReturn(testResponse);

        StaffResponse result = staffService.createStaff(testRequest, userId);

        assertThat(result).isNotNull();
        assertThat(result.fullName()).isEqualTo("Dr. John Smith");
        verify(staffRepository).save(any(Staff.class));
        verify(eventPublisher).publish(any());
    }

    @Test
    void getStaff_Success() {
        when(staffRepository.findById(testStaff.getId())).thenReturn(Optional.of(testStaff));
        when(staffMapper.toResponse(testStaff)).thenReturn(testResponse);

        StaffResponse result = staffService.getStaff(testStaff.getId());

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(testStaff.getId());
    }

    @Test
    void getStaff_NotFound() {
        when(staffRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> staffService.getStaff(UUID.randomUUID()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateStaff_Success() {
        when(staffRepository.findById(testStaff.getId())).thenReturn(Optional.of(testStaff));
        when(staffRepository.save(any(Staff.class))).thenReturn(testStaff);
        when(staffMapper.toResponse(any(Staff.class))).thenReturn(testResponse);

        StaffResponse result = staffService.updateStaff(testStaff.getId(), testRequest, userId);

        assertThat(result).isNotNull();
        verify(staffMapper).updateEntity(testRequest, testStaff);
        verify(staffRepository).save(any(Staff.class));
    }

    @Test
    void getStaffWorkload_Success() {
        when(staffRepository.findById(testStaff.getId())).thenReturn(Optional.of(testStaff));
        StaffWorkloadResponse workloadResponse = new StaffWorkloadResponse(
                testStaff.getId(), testStaff.getStaffNumber(),
                BigDecimal.valueOf(5), BigDecimal.valueOf(10),
                BigDecimal.valueOf(50), 3, false
        );
        when(workloadCalculator.calculateWorkload(testStaff)).thenReturn(workloadResponse);

        StaffWorkloadResponse result = staffService.getStaffWorkload(testStaff.getId());

        assertThat(result).isNotNull();
        assertThat(result.isOverloaded()).isFalse();
    }
}
