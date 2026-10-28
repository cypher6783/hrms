package com.hospital.resource.patient;

import com.hospital.resource.common.dto.PagedResponse;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.patient.dto.PatientRequest;
import com.hospital.resource.patient.dto.PatientResponse;
import com.hospital.resource.patient.entity.Patient;
import com.hospital.resource.patient.mapper.PatientMapper;
import com.hospital.resource.patient.repository.PatientRepository;
import com.hospital.resource.patient.service.PatientApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientApplicationServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientApplicationService patientService;

    private Patient testPatient;
    private PatientRequest testRequest;
    private PatientResponse testResponse;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        testPatient = Patient.builder()
                .id(UUID.randomUUID())
                .patientNumber("PT-20260712-0001")
                .fullName("John Doe")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .gender("MALE")
                .phoneNumber("08012345678")
                .isActive(true)
                .createdBy(userId)
                .build();

        testRequest = new PatientRequest(
                "John Doe", LocalDate.of(1990, 5, 15), "MALE",
                "08012345678", "123 Test Street", "Jane Doe", "08087654321"
        );

        testResponse = new PatientResponse(
                testPatient.getId(), "PT-20260712-0001", "John Doe",
                LocalDate.of(1990, 5, 15), "MALE", "08012345678",
                "123 Test Street", "Jane Doe", "08087654321",
                true, Instant.now(), Instant.now()
        );
    }

    @Test
    void createPatient_Success() {
        when(patientMapper.toEntity(any(PatientRequest.class))).thenReturn(testPatient);
        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);
        when(patientMapper.toResponse(any(Patient.class))).thenReturn(testResponse);

        PatientResponse result = patientService.createPatient(testRequest, userId);

        assertThat(result).isNotNull();
        assertThat(result.fullName()).isEqualTo("John Doe");
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void getPatient_Success() {
        when(patientRepository.findById(testPatient.getId())).thenReturn(Optional.of(testPatient));
        when(patientMapper.toResponse(testPatient)).thenReturn(testResponse);

        PatientResponse result = patientService.getPatient(testPatient.getId());

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(testPatient.getId());
    }

    @Test
    void getPatient_NotFound() {
        when(patientRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getPatient(UUID.randomUUID()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getPatientByNumber_Success() {
        when(patientRepository.findByPatientNumber("PT-20260712-0001")).thenReturn(Optional.of(testPatient));
        when(patientMapper.toResponse(testPatient)).thenReturn(testResponse);

        PatientResponse result = patientService.getPatientByNumber("PT-20260712-0001");

        assertThat(result).isNotNull();
        assertThat(result.patientNumber()).isEqualTo("PT-20260712-0001");
    }

    @Test
    void getPatientByNumber_NotFound() {
        when(patientRepository.findByPatientNumber(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getPatientByNumber("PT-00000000-0000"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updatePatient_Success() {
        when(patientRepository.findById(testPatient.getId())).thenReturn(Optional.of(testPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);
        when(patientMapper.toResponse(any(Patient.class))).thenReturn(testResponse);

        PatientResponse result = patientService.updatePatient(testPatient.getId(), testRequest, userId);

        assertThat(result).isNotNull();
        verify(patientMapper).updateEntity(testRequest, testPatient);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void updatePatient_NotFound() {
        when(patientRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.updatePatient(UUID.randomUUID(), testRequest, userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deactivatePatient_Success() {
        when(patientRepository.findById(testPatient.getId())).thenReturn(Optional.of(testPatient));

        patientService.deactivatePatient(testPatient.getId(), userId);

        verify(patientRepository).save(testPatient);
        assertThat(testPatient.getIsActive()).isFalse();
    }

    @Test
    void deactivatePatient_NotFound() {
        when(patientRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.deactivatePatient(UUID.randomUUID(), userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getActivePatientCount_Success() {
        when(patientRepository.countByIsActiveTrue()).thenReturn(5L);

        long count = patientService.getActivePatientCount();

        assertThat(count).isEqualTo(5L);
    }
}
