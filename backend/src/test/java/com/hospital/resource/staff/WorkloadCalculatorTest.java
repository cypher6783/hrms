package com.hospital.resource.staff;

import com.hospital.resource.admission.repository.AdmissionRepository;
import com.hospital.resource.staff.domain.WorkloadCalculator;
import com.hospital.resource.staff.dto.StaffWorkloadResponse;
import com.hospital.resource.staff.entity.Staff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkloadCalculatorTest {

    @Mock
    private AdmissionRepository admissionRepository;

    @InjectMocks
    private WorkloadCalculator workloadCalculator;

    private Staff testStaff;

    @BeforeEach
    void setUp() {
        testStaff = Staff.builder()
                .id(UUID.randomUUID())
                .staffNumber("STF-TEST0001")
                .wardId(UUID.randomUUID())
                .maxWorkloadThreshold(BigDecimal.valueOf(10))
                .build();
    }

    @Test
    void calculateWorkload_NoAdmissions() {
        when(admissionRepository.countActiveAdmissionsByWard(any(UUID.class))).thenReturn(0L);
        when(admissionRepository.countByStatusForWard(any(UUID.class))).thenReturn(List.of());

        StaffWorkloadResponse result = workloadCalculator.calculateWorkload(testStaff);

        assertThat(result).isNotNull();
        assertThat(result.currentWorkload()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.isOverloaded()).isFalse();
    }

    @Test
    void calculateWorkload_WithAdmissions() {
        when(admissionRepository.countActiveAdmissionsByWard(any(UUID.class))).thenReturn(5L);
        when(admissionRepository.countByStatusForWard(any(UUID.class))).thenReturn(List.of());

        StaffWorkloadResponse result = workloadCalculator.calculateWorkload(testStaff);

        assertThat(result).isNotNull();
        assertThat(result.activeAdmissions()).isEqualTo(5);
        assertThat(result.isOverloaded()).isFalse();
    }

    @Test
    void calculateWorkload_Overloaded() {
        testStaff.setMaxWorkloadThreshold(BigDecimal.valueOf(3));
        when(admissionRepository.countActiveAdmissionsByWard(any(UUID.class))).thenReturn(5L);
        when(admissionRepository.countByStatusForWard(any(UUID.class))).thenReturn(List.of());

        StaffWorkloadResponse result = workloadCalculator.calculateWorkload(testStaff);

        assertThat(result).isNotNull();
        assertThat(result.isOverloaded()).isTrue();
    }
}
