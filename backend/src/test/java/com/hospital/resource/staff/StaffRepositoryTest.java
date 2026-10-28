package com.hospital.resource.staff;

import com.hospital.resource.staff.entity.Staff;
import com.hospital.resource.staff.repository.StaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class StaffRepositoryTest {

    @Autowired
    private StaffRepository staffRepository;

    private Staff testStaff;

    @BeforeEach
    void setUp() {
        testStaff = Staff.builder()
                .staffNumber("STF-TEST0001")
                .fullName("Dr. John Smith")
                .role("DOCTOR")
                .specialization("CARDIOLOGY")
                .certificationStatus("CURRENT")
                .wardId(UUID.randomUUID())
                .maxWorkloadThreshold(BigDecimal.valueOf(10))
                .availabilityStatus("ACTIVE")
                .createdBy(UUID.randomUUID())
                .build();
        staffRepository.save(testStaff);
    }

    @Test
    void findByWardIdAndAvailabilityStatus_Success() {
        List<Staff> found = staffRepository.findByWardIdAndAvailabilityStatus(
                testStaff.getWardId(), "ACTIVE");
        assertThat(found).hasSize(1);
    }

    @Test
    void findByRole_Success() {
        List<Staff> found = staffRepository.findByRole("DOCTOR");
        assertThat(found).hasSize(1);
    }

    @Test
    void countByAvailabilityStatus_Success() {
        long count = staffRepository.countByAvailabilityStatus("ACTIVE");
        assertThat(count).isEqualTo(1);
    }

    @Test
    void searchStaff_WithFilters() {
        Page<Staff> page = staffRepository.searchStaff(
                "Smith", "DOCTOR", null, null, null, null,
                PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    void searchStaff_NoFilters() {
        Page<Staff> page = staffRepository.searchStaff(
                null, null, null, null, null, null,
                PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    void countByWardId_Success() {
        long count = staffRepository.countByWardId(testStaff.getWardId());
        assertThat(count).isEqualTo(1);
    }

    @Test
    void countWithExpiredCertification_Success() {
        long count = staffRepository.countWithExpiredCertification();
        assertThat(count).isEqualTo(0);
    }
}
