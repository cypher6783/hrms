package com.hospital.resource.integration;

import com.hospital.resource.equipment.dto.EquipmentRequest;
import com.hospital.resource.equipment.dto.EquipmentResponse;
import com.hospital.resource.equipment.dto.MaintenanceRequest;
import com.hospital.resource.equipment.dto.MaintenanceResponse;
import com.hospital.resource.equipment.entity.Equipment;
import com.hospital.resource.equipment.repository.EquipmentRepository;
import com.hospital.resource.equipment.service.EquipmentApplicationService;
import com.hospital.resource.equipment.service.EquipmentMaintenanceApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MaintenanceWorkflowIntegrationTest {

    @Autowired
    private EquipmentApplicationService equipmentService;

    @Autowired
    private EquipmentMaintenanceApplicationService maintenanceService;

    @Autowired
    private EquipmentRepository equipmentRepository;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void maintenanceLifecycleAndReturnToServiceWorkflow() {
        // 1. Register equipment
        EquipmentRequest createReq = new EquipmentRequest(
                "Portable Ultrasound " + UUID.randomUUID().toString().substring(0, 4),
                "ULTRASOUND",
                "SN-ULTRA-" + UUID.randomUUID().toString().substring(0, 6),
                "Radiology",
                null
        );
        EquipmentResponse equipment = equipmentService.createEquipment(createReq, userId);

        // 2. Schedule maintenance
        MaintenanceRequest maintenanceReq = new MaintenanceRequest(
                "PREVENTIVE",
                LocalDate.now(),
                "Tech Service Inc",
                "Transducer probe calibration",
                BigDecimal.valueOf(350.00),
                LocalDate.now().plusMonths(6)
        );
        MaintenanceResponse scheduled = maintenanceService.scheduleMaintenance(equipment.id(), maintenanceReq, userId);

        assertThat(scheduled).isNotNull();
        assertThat(scheduled.status()).isEqualTo("SCHEDULED");

        // Verify equipment status changed to UNDER_MAINTENANCE
        Equipment underMaintEquipment = equipmentRepository.findById(equipment.id()).orElseThrow();
        assertThat(underMaintEquipment.getStatus()).isEqualTo("UNDER_MAINTENANCE");

        // Verify equipment assignment is locked during maintenance
        assertThatThrownBy(() -> equipmentService.assignEquipment(equipment.id(), UUID.randomUUID(), userId))
                .hasMessageContaining("under maintenance");

        // 3. Technician completes maintenance
        MaintenanceResponse completed = maintenanceService.completeMaintenance(scheduled.id(), userId);
        assertThat(completed.status()).isEqualTo("COMPLETED");
        assertThat(completed.completedDate()).isEqualTo(LocalDate.now());

        // 4. Supervisor verifies and returns equipment to service
        MaintenanceResponse verified = maintenanceService.verifyAndReturnToService(scheduled.id(), userId);
        assertThat(verified.status()).isEqualTo("VERIFIED");

        // Verify equipment status changed back to AVAILABLE
        Equipment availableEquipment = equipmentRepository.findById(equipment.id()).orElseThrow();
        assertThat(availableEquipment.getStatus()).isEqualTo("AVAILABLE");
    }
}
