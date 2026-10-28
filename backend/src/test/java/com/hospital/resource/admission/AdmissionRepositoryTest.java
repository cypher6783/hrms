package com.hospital.resource.admission;

import com.hospital.resource.admission.entity.Admission;
import com.hospital.resource.admission.repository.AdmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AdmissionRepositoryTest {

    @Autowired
    private AdmissionRepository admissionRepository;

    private Admission testAdmission;

    @BeforeEach
    void setUp() {
        testAdmission = Admission.builder()
                .admissionNumber("ADM-20260712-0001")
                .patientId(UUID.randomUUID())
                .wardId(UUID.randomUUID())
                .bedId(UUID.randomUUID())
                .status("ADMITTED")
                .admittedAt(Instant.now())
                .isActive(true)
                .createdBy(UUID.randomUUID())
                .build();
        admissionRepository.save(testAdmission);
    }

    @Test
    void findByAdmissionNumber_Success() {
        Optional<Admission> found = admissionRepository.findByAdmissionNumber("ADM-20260712-0001");
        assertThat(found).isPresent();
        assertThat(found.get().getAdmissionNumber()).isEqualTo("ADM-20260712-0001");
    }

    @Test
    void findByPatientIdAndIsActiveTrue_Success() {
        Optional<Admission> found = admissionRepository.findByPatientIdAndIsActiveTrue(testAdmission.getPatientId());
        assertThat(found).isPresent();
        assertThat(found.get().getPatientId()).isEqualTo(testAdmission.getPatientId());
    }

    @Test
    void findByWardIdAndIsActiveTrue_Success() {
        List<Admission> found = admissionRepository.findByWardIdAndIsActiveTrue(testAdmission.getWardId());
        assertThat(found).hasSize(1);
    }

    @Test
    void countActiveAdmissions_Success() {
        long count = admissionRepository.countActiveAdmissions();
        assertThat(count).isEqualTo(1);
    }

    @Test
    void searchAdmissions_WithFilters() {
        Page<Admission> page = admissionRepository.searchAdmissions(
                testAdmission.getPatientId(), null, "ADMITTED", null, null,
                PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    void searchAdmissions_NoFilters() {
        Page<Admission> page = admissionRepository.searchAdmissions(
                null, null, null, null, null, PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    void countActiveAdmissionsByWard_Success() {
        long count = admissionRepository.countActiveAdmissionsByWard(testAdmission.getWardId());
        assertThat(count).isEqualTo(1);
    }

    @Test
    void countAdmittedSince_Success() {
        long count = admissionRepository.countAdmittedSince(Instant.now().minusSeconds(3600));
        assertThat(count).isEqualTo(1);
    }
}
