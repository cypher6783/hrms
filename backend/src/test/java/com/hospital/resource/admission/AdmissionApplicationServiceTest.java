package com.hospital.resource.admission;

import com.hospital.resource.admission.domain.AdmissionDomainService;
import com.hospital.resource.admission.dto.*;
import com.hospital.resource.admission.entity.Admission;
import com.hospital.resource.admission.mapper.AdmissionMapper;
import com.hospital.resource.admission.repository.AdmissionRepository;
import com.hospital.resource.admission.service.AdmissionApplicationService;
import com.hospital.resource.bed.service.BedApplicationService;
import com.hospital.resource.bedcleaning.entity.BedCleaning;
import com.hospital.resource.bedcleaning.repository.BedCleaningRepository;
import com.hospital.resource.common.event.DomainEventPublisher;
import com.hospital.resource.common.exception.ConflictException;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdmissionApplicationServiceTest {

    @Mock
    private AdmissionRepository admissionRepository;
    @Mock
    private BedApplicationService bedService;
    @Mock
    private BedCleaningRepository cleaningRepository;
    @Mock
    private AdmissionDomainService admissionDomainService;
    @Mock
    private AdmissionMapper admissionMapper;
    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private AdmissionApplicationService admissionService;

    private Admission testAdmission;
    private AdmissionRequest testRequest;
    private AdmissionResponse testResponse;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        testAdmission = Admission.builder()
                .id(UUID.randomUUID())
                .admissionNumber("ADM-20260712-0001")
                .patientId(UUID.randomUUID())
                .wardId(UUID.randomUUID())
                .bedId(UUID.randomUUID())
                .status("ADMITTED")
                .admittedAt(Instant.now())
                .isActive(true)
                .createdBy(userId)
                .build();

        testRequest = new AdmissionRequest(
                testAdmission.getPatientId(),
                testAdmission.getWardId(),
                testAdmission.getBedId(),
                "Test admission"
        );

        testResponse = new AdmissionResponse(
                testAdmission.getId(),
                testAdmission.getAdmissionNumber(),
                testAdmission.getPatientId(),
                testAdmission.getWardId(),
                testAdmission.getBedId(),
                testAdmission.getStatus(),
                testAdmission.getAdmissionNotes(),
                null, null, testAdmission.getAdmittedAt(),
                null, true, Instant.now(), Instant.now()
        );
    }

    @Test
    void createAdmission_Success() {
        when(admissionMapper.toEntity(any(AdmissionRequest.class))).thenReturn(testAdmission);
        when(admissionRepository.save(any(Admission.class))).thenReturn(testAdmission);
        when(admissionMapper.toResponse(any(Admission.class))).thenReturn(testResponse);
        when(bedService.getBed(any(UUID.class))).thenReturn(
                new com.hospital.resource.bed.dto.BedResponse(
                        testAdmission.getBedId(), "B-001", testAdmission.getWardId(),
                        "STANDARD", false, "AVAILABLE", null, null, Instant.now(), Instant.now()
                ));

        AdmissionResponse result = admissionService.createAdmission(testRequest, userId);

        assertThat(result).isNotNull();
        assertThat(result.patientId()).isEqualTo(testAdmission.getPatientId());
        verify(admissionDomainService).validateNewAdmission(testRequest.patientId());
        verify(bedService).updateBedStatus(testRequest.bedId(), "OCCUPIED");
        verify(admissionRepository).save(any(Admission.class));
        verify(eventPublisher, atLeastOnce()).publish(any());
    }

    @Test
    void getAdmission_Success() {
        when(admissionRepository.findById(testAdmission.getId())).thenReturn(Optional.of(testAdmission));
        when(admissionMapper.toResponse(testAdmission)).thenReturn(testResponse);

        AdmissionResponse result = admissionService.getAdmission(testAdmission.getId());

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(testAdmission.getId());
    }

    @Test
    void getAdmission_NotFound() {
        when(admissionRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> admissionService.getAdmission(UUID.randomUUID()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void dischargeAdmission_Success() {
        BedCleaning cleaning = BedCleaning.builder().id(UUID.randomUUID()).bedId(testAdmission.getBedId()).admissionId(testAdmission.getId()).build();
        when(admissionRepository.findById(testAdmission.getId())).thenReturn(Optional.of(testAdmission));
        when(admissionRepository.save(any(Admission.class))).thenReturn(testAdmission);
        when(cleaningRepository.save(any())).thenReturn(cleaning);
        when(admissionMapper.toResponse(any(Admission.class))).thenReturn(testResponse);

        DischargeRequest dischargeRequest = new DischargeRequest("RECOVERED", "Discharged successfully");

        AdmissionResponse result = admissionService.dischargeAdmission(testAdmission.getId(), dischargeRequest, userId);

        assertThat(result).isNotNull();
        verify(admissionDomainService).validateDischarge(testAdmission);
        verify(bedService).updateBedStatus(testAdmission.getBedId(), "CLEANING_REQUIRED");
        verify(cleaningRepository).save(any());
        verify(eventPublisher, atLeastOnce()).publish(any());
    }

    @Test
    void dischargeAdmission_NotFound() {
        when(admissionRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        DischargeRequest dischargeRequest = new DischargeRequest("RECOVERED", "Notes");

        assertThatThrownBy(() -> admissionService.dischargeAdmission(UUID.randomUUID(), dischargeRequest, userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
