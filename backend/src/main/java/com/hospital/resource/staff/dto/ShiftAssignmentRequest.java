package com.hospital.resource.staff.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ShiftAssignmentRequest(
        @NotNull UUID staffId,
        @NotNull UUID shiftId
) {}
