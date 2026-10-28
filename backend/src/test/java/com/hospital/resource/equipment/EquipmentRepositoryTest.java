package com.hospital.resource.equipment;

import com.hospital.resource.equipment.entity.Equipment;
import com.hospital.resource.equipment.repository.EquipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EquipmentRepositoryTest {

    @Autowired
    private EquipmentRepository equipmentRepository;

    private Equipment testEquipment;

    @BeforeEach
    void setUp() {
        testEquipment = Equipment.builder()
                .name("Ventilator Model X")
                .equipmentType("VENTILATOR")
                .serialNumber("SN-VENT-999")
                .location("ICU Room 1")
                .status("AVAILABLE")
                .createdBy(UUID.randomUUID())
                .build();
        equipmentRepository.save(testEquipment);
    }

    @Test
    void findBySerialNumber_Success() {
        var opt = equipmentRepository.findBySerialNumber("SN-VENT-999");
        assertThat(opt).isPresent();
        assertThat(opt.get().getName()).isEqualTo("Ventilator Model X");
    }

    @Test
    void findByStatus_Success() {
        List<Equipment> available = equipmentRepository.findByStatus("AVAILABLE");
        assertThat(available).hasSize(1);
    }

    @Test
    void existsBySerialNumber_Success() {
        boolean exists = equipmentRepository.existsBySerialNumber("SN-VENT-999");
        assertThat(exists).isTrue();
    }
}
