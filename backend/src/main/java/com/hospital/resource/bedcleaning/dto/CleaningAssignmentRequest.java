package com.hospital.resource.bedcleaning.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CleaningAssignmentRequest(
        @NotNull UUID staffId
) {}
