package com.hospital.resource.equipment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record MaintenanceResponse(
        UUID id,
        UUID equipmentId,
        String maintenanceType,
        String status,
        LocalDate scheduledDate,
        LocalDate completedDate,
        String performedBy,
        String maintenanceNotes,
        BigDecimal cost,
        LocalDate nextMaintenanceDate,
        Instant createdAt,
        Instant updatedAt
) {}
