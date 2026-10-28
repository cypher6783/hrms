package com.hospital.resource.equipment;

import com.hospital.resource.equipment.entity.EquipmentMaintenance;
import com.hospital.resource.equipment.repository.EquipmentMaintenanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EquipmentMaintenanceRepositoryTest {

    @Autowired
    private EquipmentMaintenanceRepository maintenanceRepository;

    private UUID equipmentId;

    @BeforeEach
    void setUp() {
        equipmentId = UUID.randomUUID();

        EquipmentMaintenance overdue = EquipmentMaintenance.builder()
                .equipmentId(equipmentId)
                .maintenanceType("PREVENTIVE")
                .status("SCHEDULED")
                .scheduledDate(LocalDate.now().minusDays(5))
                .performedBy("Tech A")
                .createdBy(UUID.randomUUID())
                .createdAt(java.time.Instant.now())
                .updatedAt(java.time.Instant.now())
                .build();

        maintenanceRepository.save(overdue);
    }

    @Test
    void findOverdueMaintenance_Success() {
        List<EquipmentMaintenance> overdueList = maintenanceRepository.findOverdueMaintenance(LocalDate.now());
        assertThat(overdueList).hasSize(1);
        assertThat(overdueList.get(0).getEquipmentId()).isEqualTo(equipmentId);
    }
}
