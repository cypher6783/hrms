package com.hospital.resource.ward;

import com.hospital.resource.common.exception.BusinessException;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.ward.domain.WardDomainService;
import com.hospital.resource.ward.dto.WardRequest;
import com.hospital.resource.ward.dto.WardResponse;
import com.hospital.resource.ward.entity.Ward;
import com.hospital.resource.ward.mapper.WardMapper;
import com.hospital.resource.ward.repository.WardRepository;
import com.hospital.resource.ward.service.WardApplicationService;
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
class WardApplicationServiceTest {

    @Mock
    private WardRepository wardRepository;

    @Mock
    private WardMapper wardMapper;

    @Mock
    private WardDomainService wardDomainService;

    @InjectMocks
    private WardApplicationService wardService;

    private Ward testWard;
    private WardRequest testRequest;
    private WardResponse testResponse;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        testWard = Ward.builder()
                .id(UUID.randomUUID())
                .name("Cardiology Ward")
                .wardType("GENERAL")
                .maxBedCapacity(20)
                .isolationLevel("NONE")
                .equipmentZone("Zone-A")
                .status("ACTIVE")
                .createdBy(userId)
                .build();

        testRequest = new WardRequest("Cardiology Ward", "GENERAL", 20, "NONE", "Zone-A");

        testResponse = new WardResponse(
                testWard.getId(), "Cardiology Ward", "GENERAL", 20,
                "NONE", "Zone-A", "ACTIVE", Instant.now(), Instant.now()
        );
    }

    @Test
    void createWard_Success() {
        when(wardRepository.existsByName("Cardiology Ward")).thenReturn(false);
        when(wardMapper.toEntity(any(WardRequest.class))).thenReturn(testWard);
        when(wardRepository.save(any(Ward.class))).thenReturn(testWard);
        when(wardMapper.toResponse(any(Ward.class))).thenReturn(testResponse);

        WardResponse result = wardService.createWard(testRequest, userId);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Cardiology Ward");
        verify(wardRepository).save(any(Ward.class));
    }

    @Test
    void createWard_DuplicateName_Throws() {
        when(wardRepository.existsByName("Cardiology Ward")).thenReturn(true);

        assertThatThrownBy(() -> wardService.createWard(testRequest, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void getWard_Success() {
        when(wardRepository.findById(testWard.getId())).thenReturn(Optional.of(testWard));
        when(wardMapper.toResponse(testWard)).thenReturn(testResponse);

        WardResponse result = wardService.getWard(testWard.getId());

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(testWard.getId());
    }

    @Test
    void getWard_NotFound() {
        when(wardRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wardService.getWard(UUID.randomUUID()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateWard_Success() {
        when(wardRepository.findById(testWard.getId())).thenReturn(Optional.of(testWard));
        when(wardRepository.save(any(Ward.class))).thenReturn(testWard);
        when(wardMapper.toResponse(any(Ward.class))).thenReturn(testResponse);

        WardResponse result = wardService.updateWard(testWard.getId(), testRequest, userId);

        assertThat(result).isNotNull();
        verify(wardMapper).updateEntity(testRequest, testWard);
        verify(wardRepository).save(any(Ward.class));
    }

    @Test
    void updateWard_NotFound() {
        when(wardRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wardService.updateWard(UUID.randomUUID(), testRequest, userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateWard_DuplicateName_Throws() {
        Ward existingWard = Ward.builder()
                .id(testWard.getId())
                .name("ICU Ward")
                .build();
        when(wardRepository.findById(testWard.getId())).thenReturn(Optional.of(existingWard));
        when(wardRepository.existsByName("Cardiology Ward")).thenReturn(true);

        WardRequest requestWithDifferentName = new WardRequest("Cardiology Ward", "GENERAL", 20, "NONE", null);

        assertThatThrownBy(() -> wardService.updateWard(testWard.getId(), requestWithDifferentName, userId))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void deactivateWard_Success() {
        when(wardRepository.findById(testWard.getId())).thenReturn(Optional.of(testWard));

        wardService.deactivateWard(testWard.getId(), userId);

        verify(wardRepository).save(testWard);
        assertThat(testWard.getStatus()).isEqualTo("INACTIVE");
    }

    @Test
    void deactivateWard_NotFound() {
        when(wardRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wardService.deactivateWard(UUID.randomUUID(), userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
