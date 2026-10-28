package com.hospital.resource.equipment;

import com.hospital.resource.common.event.DomainEventPublisher;
import com.hospital.resource.equipment.dto.*;
import com.hospital.resource.equipment.entity.Equipment;
import com.hospital.resource.equipment.entity.EquipmentAllocation;
import com.hospital.resource.equipment.mapper.EquipmentMapper;
import com.hospital.resource.equipment.mapper.MaintenanceMapper;
import com.hospital.resource.equipment.repository.EquipmentAllocationRepository;
import com.hospital.resource.equipment.repository.EquipmentMaintenanceRepository;
import com.hospital.resource.equipment.repository.EquipmentRepository;
import com.hospital.resource.equipment.service.EquipmentApplicationService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentApplicationServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private EquipmentAllocationRepository allocationRepository;

    @Mock
    private EquipmentMaintenanceRepository maintenanceRepository;

    @Mock
    private EquipmentMapper equipmentMapper;

    @Mock
    private MaintenanceMapper maintenanceMapper;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private EquipmentApplicationService equipmentService;

    private UUID equipmentId;
    private UUID admissionId;
    private UUID userId;
    private Equipment equipment;

    @BeforeEach
    void setUp() {
        equipmentId = UUID.randomUUID();
        admissionId = UUID.randomUUID();
        userId = UUID.randomUUID();

        equipment = Equipment.builder()
                .id(equipmentId)
                .name("ECG Machine")
                .equipmentType("DIAGNOSTIC")
                .serialNumber("SN-ECG-01")
                .status("AVAILABLE")
                .build();
    }

    @Test
    void assignEquipment_Success() {
        EquipmentAllocation allocation = EquipmentAllocation.builder().id(UUID.randomUUID()).equipmentId(equipmentId).admissionId(admissionId).build();
        EquipmentAllocationResponse response = new EquipmentAllocationResponse(allocation.getId(), equipmentId, admissionId, Instant.now(), null, userId);

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));
        when(allocationRepository.save(any())).thenReturn(allocation);
        when(equipmentMapper.toAllocationResponse(allocation)).thenReturn(response);

        EquipmentAllocationResponse result = equipmentService.assignEquipment(equipmentId, admissionId, userId);

        assertThat(result).isNotNull();
        assertThat(equipment.getStatus()).isEqualTo("IN_USE");
        assertThat(equipment.getAssignedAdmissionId()).isEqualTo(admissionId);
        verify(eventPublisher).publish(any());
    }
}
