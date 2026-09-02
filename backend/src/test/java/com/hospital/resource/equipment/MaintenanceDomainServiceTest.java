package com.hospital.resource.equipment;

import com.hospital.resource.common.exception.ValidationException;
import com.hospital.resource.equipment.domain.MaintenanceDomainService;
import com.hospital.resource.equipment.entity.EquipmentMaintenance;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MaintenanceDomainServiceTest {

    private final MaintenanceDomainService domainService = new MaintenanceDomainService();

    @Test
    void validateVerifiable_ThrowsValidation_WhenNotCompleted() {
        EquipmentMaintenance maintenance = EquipmentMaintenance.builder().status("SCHEDULED").build();
        assertThatThrownBy(() -> domainService.validateVerifiable(maintenance))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("must be marked as COMPLETED");
    }
}
