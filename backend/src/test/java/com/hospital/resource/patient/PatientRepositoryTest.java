package com.hospital.resource.patient;

import com.hospital.resource.patient.entity.Patient;
import com.hospital.resource.patient.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    private Patient testPatient;

    @BeforeEach
    void setUp() {
        testPatient = Patient.builder()
                .patientNumber("PT-20260712-0001")
                .fullName("John Doe")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .gender("MALE")
                .phoneNumber("08012345678")
                .address("123 Test Street")
                .nextOfKinName("Jane Doe")
                .nextOfKinPhone("08087654321")
                .isActive(true)
                .createdBy(UUID.randomUUID())
                .build();
        patientRepository.save(testPatient);
    }

    @Test
    void findByPatientNumber_Success() {
        Optional<Patient> found = patientRepository.findByPatientNumber("PT-20260712-0001");
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("John Doe");
    }

    @Test
    void findByPatientNumber_NotFound() {
        Optional<Patient> found = patientRepository.findByPatientNumber("PT-00000000-0000");
        assertThat(found).isEmpty();
    }

    @Test
    void existsByPatientNumber_True() {
        assertThat(patientRepository.existsByPatientNumber("PT-20260712-0001")).isTrue();
    }

    @Test
    void existsByPatientNumber_False() {
        assertThat(patientRepository.existsByPatientNumber("PT-00000000-0000")).isFalse();
    }

    @Test
    void searchPatients_ByName() {
        Page<Patient> result = patientRepository.searchPatients("John", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFullName()).isEqualTo("John Doe");
    }

    @Test
    void searchPatients_ByPhoneNumber() {
        Page<Patient> result = patientRepository.searchPatients("080123", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void searchPatients_NoMatch() {
        Page<Patient> result = patientRepository.searchPatients("Nonexistent", PageRequest.of(0, 10));
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void searchPatients_ExcludesInactive() {
        testPatient.setIsActive(false);
        patientRepository.save(testPatient);

        Page<Patient> result = patientRepository.searchPatients("John", PageRequest.of(0, 10));
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void countByIsActiveTrue_Success() {
        long count = patientRepository.countByIsActiveTrue();
        assertThat(count).isEqualTo(1);
    }

    @Test
    void countByIsActiveTrue_ExcludesInactive() {
        testPatient.setIsActive(false);
        patientRepository.save(testPatient);

        long count = patientRepository.countByIsActiveTrue();
        assertThat(count).isEqualTo(0);
    }

    @Test
    void savePatient_SetsId() {
        Patient newPatient = Patient.builder()
                .patientNumber("PT-20260712-0002")
                .fullName("Jane Smith")
                .dateOfBirth(LocalDate.of(1985, 3, 20))
                .gender("FEMALE")
                .isActive(true)
                .build();
        Patient saved = patientRepository.save(newPatient);
        assertThat(saved.getId()).isNotNull();
    }
}
