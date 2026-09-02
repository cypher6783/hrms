package com.hospital.resource.bedcleaning;

import com.hospital.resource.bed.service.BedApplicationService;
import com.hospital.resource.bedcleaning.domain.BedCleaningDomainService;
import com.hospital.resource.bedcleaning.dto.*;
import com.hospital.resource.bedcleaning.entity.BedCleaning;
import com.hospital.resource.bedcleaning.mapper.BedCleaningMapper;
import com.hospital.resource.bedcleaning.repository.BedCleaningRepository;
import com.hospital.resource.bedcleaning.service.BedCleaningApplicationService;
import com.hospital.resource.common.event.DomainEventPublisher;
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
class BedCleaningApplicationServiceTest {

    @Mock
    private BedCleaningRepository cleaningRepository;
    @Mock
    private BedApplicationService bedService;
    @Mock
    private BedCleaningDomainService cleaningDomainService;
    @Mock
    private BedCleaningMapper cleaningMapper;
    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private BedCleaningApplicationService cleaningService;

    private BedCleaning testCleaning;
    private CleaningTaskResponse testResponse;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        testCleaning = BedCleaning.builder()
                .id(UUID.randomUUID())
                .bedId(UUID.randomUUID())
                .admissionId(UUID.randomUUID())
                .status("PENDING")
                .build();

        testResponse = new CleaningTaskResponse(
                testCleaning.getId(),
                testCleaning.getBedId(),
                testCleaning.getAdmissionId(),
                "PENDING",
                null, null, null, null, null, null, null, Instant.now()
        );
    }

    @Test
    void assignTask_Success() {
        when(cleaningRepository.findById(testCleaning.getId())).thenReturn(Optional.of(testCleaning));
        when(cleaningRepository.save(any(BedCleaning.class))).thenReturn(testCleaning);
        when(cleaningMapper.toResponse(any(BedCleaning.class))).thenReturn(testResponse);

        CleaningAssignmentRequest request = new CleaningAssignmentRequest(UUID.randomUUID());
        CleaningTaskResponse result = cleaningService.assignTask(testCleaning.getId(), request, userId);

        assertThat(result).isNotNull();
        verify(cleaningDomainService).validateAssignment(testCleaning);
        verify(cleaningRepository).save(any(BedCleaning.class));
    }

    @Test
    void assignTask_NotFound() {
        when(cleaningRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        CleaningAssignmentRequest request = new CleaningAssignmentRequest(UUID.randomUUID());

        assertThatThrownBy(() -> cleaningService.assignTask(UUID.randomUUID(), request, userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void startCleaning_Success() {
        when(cleaningRepository.findById(testCleaning.getId())).thenReturn(Optional.of(testCleaning));
        when(cleaningRepository.save(any(BedCleaning.class))).thenReturn(testCleaning);
        when(cleaningMapper.toResponse(any(BedCleaning.class))).thenReturn(testResponse);

        CleaningTaskResponse result = cleaningService.startCleaning(testCleaning.getId(), userId);

        assertThat(result).isNotNull();
        verify(cleaningDomainService).validateStart(testCleaning);
        verify(eventPublisher).publish(any());
    }

    @Test
    void completeCleaning_Success() {
        when(cleaningRepository.findById(testCleaning.getId())).thenReturn(Optional.of(testCleaning));
        when(cleaningRepository.save(any(BedCleaning.class))).thenReturn(testCleaning);
        when(cleaningMapper.toResponse(any(BedCleaning.class))).thenReturn(testResponse);

        CleaningCompletionRequest request = new CleaningCompletionRequest("Cleaning completed");
        CleaningTaskResponse result = cleaningService.completeCleaning(testCleaning.getId(), request, userId);

        assertThat(result).isNotNull();
        verify(cleaningDomainService).validateComplete(testCleaning);
        verify(eventPublisher).publish(any());
    }

    @Test
    void verifyCleaning_Success() {
        when(cleaningRepository.findById(testCleaning.getId())).thenReturn(Optional.of(testCleaning));
        when(cleaningRepository.save(any(BedCleaning.class))).thenReturn(testCleaning);
        when(cleaningMapper.toResponse(any(BedCleaning.class))).thenReturn(testResponse);

        CleaningTaskResponse result = cleaningService.verifyCleaning(testCleaning.getId(), userId);

        assertThat(result).isNotNull();
        verify(cleaningDomainService).validateVerification(testCleaning);
        verify(bedService).updateBedStatus(testCleaning.getBedId(), "AVAILABLE");
        verify(eventPublisher).publish(any());
    }
}
