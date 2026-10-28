package com.hospital.resource.equipment;

import com.hospital.resource.common.exception.ConflictException;
import com.hospital.resource.common.exception.ValidationException;
import com.hospital.resource.equipment.domain.EquipmentDomainService;
import com.hospital.resource.equipment.entity.Equipment;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EquipmentDomainServiceTest {

    private final EquipmentDomainService domainService = new EquipmentDomainService();

    @Test
    void validateAssignable_ThrowsConflict_WhenInUse() {
        Equipment eq = Equipment.builder().name("Pump").serialNumber("SN-1").status("IN_USE").build();
        assertThatThrownBy(() -> domainService.validateAssignable(eq))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already assigned");
    }

    @Test
    void validateAssignable_ThrowsValidation_WhenUnderMaintenance() {
        Equipment eq = Equipment.builder().name("Pump").serialNumber("SN-1").status("UNDER_MAINTENANCE").build();
        assertThatThrownBy(() -> domainService.validateAssignable(eq))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("under maintenance");
    }
}
