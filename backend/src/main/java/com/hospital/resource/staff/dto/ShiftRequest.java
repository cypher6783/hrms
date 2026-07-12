package com.hospital.resource.staff.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ShiftRequest(
        @NotNull String shiftName,
        @NotNull LocalDate shiftDate,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        @NotNull UUID wardId,
        Integer minRequiredStaff,
        Integer maxStaff
) {}
