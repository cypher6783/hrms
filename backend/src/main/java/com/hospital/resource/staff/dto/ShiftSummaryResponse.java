package com.hospital.resource.staff.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ShiftSummaryResponse(
        UUID id,
        String shiftName,
        LocalDate shiftDate,
        LocalTime startTime,
        LocalTime endTime,
        UUID wardId,
        String status,
        Instant createdAt
) {}
