package com.hospital.resource.staff.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ShiftResponse(
        UUID id,
        String shiftName,
        LocalDate shiftDate,
        LocalTime startTime,
        LocalTime endTime,
        UUID wardId,
        Integer minRequiredStaff,
        Integer maxStaff,
        String status,
        Instant createdAt,
        Instant updatedAt
) {}
