package com.hospital.resource.bed;

import com.hospital.resource.bed.domain.BedDomainService;
import com.hospital.resource.bed.dto.BedAvailabilityResponse;
import com.hospital.resource.bed.dto.BedRequest;
import com.hospital.resource.bed.dto.BedResponse;
import com.hospital.resource.bed.entity.Bed;
import com.hospital.resource.bed.mapper.BedMapper;
import com.hospital.resource.bed.repository.BedRepository;
import com.hospital.resource.bed.service.BedApplicationService;
import com.hospital.resource.common.exception.BusinessException;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BedApplicationServiceTest {

    @Mock
    private BedRepository bedRepository;

    @Mock
    private BedMapper bedMapper;

    @Mock
    private BedDomainService bedDomainService;

    @InjectMocks
    private BedApplicationService bedService;

    private Bed testBed;
    private BedRequest testRequest;
    private BedResponse testResponse;
    private UUID wardId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        wardId = UUID.randomUUID();

        testBed = Bed.builder()
                .id(UUID.randomUUID())
                .bedNumber("B-001")
                .wardId(wardId)
                .bedType("STANDARD")
                .isIsolationCapable(false)
                .status("AVAILABLE")
                .createdBy(userId)
                .build();

        testRequest = new BedRequest("B-001", wardId, "STANDARD", false);

        testResponse = new BedResponse(
                testBed.getId(), "B-001", wardId, "STANDARD",
                false, "AVAILABLE", null, null, Instant.now(), Instant.now()
        );
    }

    @Test
    void createBed_Success() {
        when(bedRepository.findByWardId(wardId)).thenReturn(List.of());
        when(bedMapper.toEntity(any(BedRequest.class))).thenReturn(testBed);
        when(bedRepository.save(any(Bed.class))).thenReturn(testBed);
        when(bedMapper.toResponse(any(Bed.class))).thenReturn(testResponse);

        BedResponse result = bedService.createBed(testRequest, userId);

        assertThat(result).isNotNull();
        assertThat(result.bedNumber()).isEqualTo("B-001");
        verify(bedRepository).save(any(Bed.class));
    }

    @Test
    void createBed_DuplicateBedNumberInWard_Throws() {
        Bed existingBed = Bed.builder()
                .id(UUID.randomUUID())
                .bedNumber("B-001")
                .wardId(wardId)
                .build();
        when(bedRepository.findByWardId(wardId)).thenReturn(List.of(existingBed));

        assertThatThrownBy(() -> bedService.createBed(testRequest, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void getBed_Success() {
        when(bedRepository.findById(testBed.getId())).thenReturn(Optional.of(testBed));
        when(bedMapper.toResponse(testBed)).thenReturn(testResponse);

        BedResponse result = bedService.getBed(testBed.getId());

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(testBed.getId());
    }

    @Test
    void getBed_NotFound() {
        when(bedRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bedService.getBed(UUID.randomUUID()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getBedsByWard_Success() {
        when(bedRepository.findByWardId(wardId)).thenReturn(List.of(testBed));
        when(bedMapper.toResponseList(any())).thenReturn(List.of(testResponse));

        List<BedResponse> result = bedService.getBedsByWard(wardId);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAvailableIsolationBeds_Success() {
        when(bedRepository.findByStatusAndIsIsolationCapable("AVAILABLE", true)).thenReturn(List.of());
        when(bedMapper.toResponseList(any())).thenReturn(List.of());

        List<BedResponse> result = bedService.getAvailableIsolationBeds();

        assertThat(result).isEmpty();
    }

    @Test
    void updateBed_Success() {
        when(bedRepository.findById(testBed.getId())).thenReturn(Optional.of(testBed));
        when(bedRepository.save(any(Bed.class))).thenReturn(testBed);
        when(bedMapper.toResponse(any(Bed.class))).thenReturn(testResponse);

        BedResponse result = bedService.updateBed(testBed.getId(), testRequest, userId);

        assertThat(result).isNotNull();
        verify(bedMapper).updateEntity(testRequest, testBed);
        verify(bedRepository).save(any(Bed.class));
    }

    @Test
    void updateBed_NotFound() {
        when(bedRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bedService.updateBed(UUID.randomUUID(), testRequest, userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateBedStatus_Success() {
        when(bedRepository.findById(testBed.getId())).thenReturn(Optional.of(testBed));
        when(bedRepository.save(any(Bed.class))).thenReturn(testBed);
        when(bedMapper.toResponse(any(Bed.class))).thenReturn(testResponse);

        BedResponse result = bedService.updateBedStatus(testBed.getId(), "OCCUPIED");

        assertThat(result).isNotNull();
        verify(bedDomainService).validateStatusTransition("AVAILABLE", "OCCUPIED");
        verify(bedRepository).save(any(Bed.class));
    }

    @Test
    void updateBedStatus_InvalidTransition_Throws() {
        when(bedRepository.findById(testBed.getId())).thenReturn(Optional.of(testBed));
        doThrow(new BusinessException("Invalid transition"))
                .when(bedDomainService).validateStatusTransition("AVAILABLE", "CLEANING_REQUIRED");

        assertThatThrownBy(() -> bedService.updateBedStatus(testBed.getId(), "CLEANING_REQUIRED"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void filterBeds_Success() {
        when(bedRepository.findBedsWithFilters(wardId, "STANDARD", "AVAILABLE", false))
                .thenReturn(List.of(testBed));
        when(bedMapper.toResponseList(any())).thenReturn(List.of(testResponse));

        List<BedResponse> result = bedService.filterBeds(wardId, "STANDARD", "AVAILABLE", false);

        assertThat(result).hasSize(1);
    }

    @Test
    void filterBeds_NoMatch() {
        when(bedRepository.findBedsWithFilters(any(), any(), any(), any())).thenReturn(List.of());
        when(bedMapper.toResponseList(any())).thenReturn(List.of());

        List<BedResponse> result = bedService.filterBeds(null, null, null, null);

        assertThat(result).isEmpty();
    }
}
