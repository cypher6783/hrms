package com.hospital.resource.resource;

import com.hospital.resource.common.exception.ConflictException;
import com.hospital.resource.common.exception.ValidationException;
import com.hospital.resource.resource.domain.ResourceDomainService;
import com.hospital.resource.resource.entity.ResourceAllocation;
import com.hospital.resource.resource.entity.ResourceReservation;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResourceDomainServiceTest {

    private final ResourceDomainService domainService = new ResourceDomainService();

    @Test
    void validateResourceUniqueness_ThrowsConflict_WhenExists() {
        assertThatThrownBy(() -> domainService.validateResourceUniqueness(true))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void validateResourceUniqueness_Success_WhenNotExists() {
        assertThatCode(() -> domainService.validateResourceUniqueness(false))
                .doesNotThrowAnyException();
    }

    @Test
    void validateReservationAvailability_ThrowsValidation_WhenStockInsufficient() {
        assertThatThrownBy(() -> domainService.validateReservationAvailability(100, 50, 40, 20))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Insufficient unreserved stock");
    }

    @Test
    void validateReservationAvailability_Success_WhenStockSufficient() {
        assertThatCode(() -> domainService.validateReservationAvailability(100, 40, 30, 20))
                .doesNotThrowAnyException();
    }

    @Test
    void validateAllocationActive_ThrowsValidation_WhenReleased() {
        ResourceAllocation allocation = ResourceAllocation.builder()
                .releasedAt(Instant.now())
                .build();

        assertThatThrownBy(() -> domainService.validateAllocationActive(allocation))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("already been released");
    }
}
