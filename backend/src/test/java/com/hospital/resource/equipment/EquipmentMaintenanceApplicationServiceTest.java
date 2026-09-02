package com.hospital.resource.equipment;

import com.hospital.resource.common.event.DomainEventPublisher;
import com.hospital.resource.equipment.dto.MaintenanceRequest;
import com.hospital.resource.equipment.dto.MaintenanceResponse;
import com.hospital.resource.equipment.entity.Equipment;
import com.hospital.resource.equipment.entity.EquipmentMaintenance;
import com.hospital.resource.equipment.mapper.MaintenanceMapper;
import com.hospital.resource.equipment.repository.EquipmentMaintenanceRepository;
import com.hospital.resource.equipment.repository.EquipmentRepository;
import com.hospital.resource.equipment.service.EquipmentMaintenanceApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentMaintenanceApplicationServiceTest {

    @Mock
    private EquipmentMaintenanceRepository maintenanceRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private MaintenanceMapper maintenanceMapper;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private EquipmentMaintenanceApplicationService maintenanceService;

    private UUID equipmentId;
    private UUID maintenanceId;
    private UUID userId;
    private Equipment equipment;
    private EquipmentMaintenance maintenance;

    @BeforeEach
    void setUp() {
        equipmentId = UUID.randomUUID();
        maintenanceId = UUID.randomUUID();
        userId = UUID.randomUUID();

        equipment = Equipment.builder().id(equipmentId).name("Defibrillator").status("AVAILABLE").build();
        maintenance = EquipmentMaintenance.builder().id(maintenanceId).equipmentId(equipmentId).status("COMPLETED").build();
    }

    @Test
    void scheduleMaintenance_Success() {
        MaintenanceRequest request = new MaintenanceRequest("PREVENTIVE", LocalDate.now(), "Tech B", "Routine check", BigDecimal.valueOf(150), LocalDate.now().plusMonths(6));
        EquipmentMaintenance scheduled = EquipmentMaintenance.builder().id(maintenanceId).equipmentId(equipmentId).status("SCHEDULED").build();
        MaintenanceResponse response = new MaintenanceResponse(maintenanceId, equipmentId, "PREVENTIVE", "SCHEDULED", LocalDate.now(), null, "Tech B", "Routine check", BigDecimal.valueOf(150), LocalDate.now().plusMonths(6), Instant.now(), Instant.now());

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));
        when(maintenanceRepository.save(any())).thenReturn(scheduled);
        when(maintenanceMapper.toResponse(scheduled)).thenReturn(response);

        MaintenanceResponse result = maintenanceService.scheduleMaintenance(equipmentId, request, userId);

        assertThat(result).isNotNull();
        assertThat(equipment.getStatus()).isEqualTo("UNDER_MAINTENANCE");
        verify(eventPublisher).publish(any());
    }

    @Test
    void verifyAndReturnToService_Success() {
        MaintenanceResponse response = new MaintenanceResponse(maintenanceId, equipmentId, "PREVENTIVE", "VERIFIED", LocalDate.now(), LocalDate.now(), "Tech B", "Routine check", BigDecimal.valueOf(150), LocalDate.now().plusMonths(6), Instant.now(), Instant.now());

        when(maintenanceRepository.findById(maintenanceId)).thenReturn(Optional.of(maintenance));
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));
        when(maintenanceRepository.save(any())).thenReturn(maintenance);
        when(maintenanceMapper.toResponse(maintenance)).thenReturn(response);

        MaintenanceResponse result = maintenanceService.verifyAndReturnToService(maintenanceId, userId);

        assertThat(result).isNotNull();
        assertThat(equipment.getStatus()).isEqualTo("AVAILABLE");
        verify(eventPublisher).publish(any());
    }
}
