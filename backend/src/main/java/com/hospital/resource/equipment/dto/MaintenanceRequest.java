package com.hospital.resource.equipment.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MaintenanceRequest(
        @NotNull String maintenanceType,
        @NotNull LocalDate scheduledDate,
        String performedBy,
        String maintenanceNotes,
        BigDecimal cost,
        LocalDate nextMaintenanceDate
) {}
