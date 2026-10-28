package com.hospital.resource.integration;

import com.hospital.resource.equipment.dto.EquipmentAllocationResponse;
import com.hospital.resource.equipment.dto.EquipmentRequest;
import com.hospital.resource.equipment.dto.EquipmentResponse;
import com.hospital.resource.equipment.dto.EquipmentUsageHistoryResponse;
import com.hospital.resource.equipment.entity.Equipment;
import com.hospital.resource.equipment.repository.EquipmentRepository;
import com.hospital.resource.equipment.service.EquipmentApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EquipmentAssignmentIntegrationTest {

    @Autowired
    private EquipmentApplicationService equipmentService;

    @Autowired
    private EquipmentRepository equipmentRepository;

    private UUID userId;
    private UUID admissionId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        admissionId = UUID.randomUUID();
    }

    @Test
    void fullEquipmentAssignmentAndReturnLifecycle() {
        // 1. Create equipment
        EquipmentRequest createReq = new EquipmentRequest(
                "Patient Monitor Alpha " + UUID.randomUUID().toString().substring(0, 4),
                "MONITOR",
                "SN-MON-" + UUID.randomUUID().toString().substring(0, 6),
                "ICU Ward",
                null
        );
        EquipmentResponse equipment = equipmentService.createEquipment(createReq, userId);

        assertThat(equipment).isNotNull();
        assertThat(equipment.status()).isEqualTo("AVAILABLE");

        // 2. Assign equipment to admission
        EquipmentAllocationResponse allocation = equipmentService.assignEquipment(equipment.id(), admissionId, userId);
        assertThat(allocation).isNotNull();

        Equipment assignedEquipment = equipmentRepository.findById(equipment.id()).orElseThrow();
        assertThat(assignedEquipment.getStatus()).isEqualTo("IN_USE");
        assertThat(assignedEquipment.getAssignedAdmissionId()).isEqualTo(admissionId);

        // 3. Check usage history
        EquipmentUsageHistoryResponse history = equipmentService.getUsageHistory(equipment.id());
        assertThat(history.allocationHistory()).hasSize(1);

        // 4. Release equipment
        EquipmentAllocationResponse released = equipmentService.releaseEquipment(equipment.id(), userId);
        assertThat(released.releasedAt()).isNotNull();

        Equipment releasedEquipment = equipmentRepository.findById(equipment.id()).orElseThrow();
        assertThat(releasedEquipment.getStatus()).isEqualTo("AVAILABLE");
        assertThat(releasedEquipment.getAssignedAdmissionId()).isNull();
    }
}
