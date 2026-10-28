package com.hospital.resource.bed.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record BedRequest(
        @NotBlank String bedNumber,
        UUID wardId,
        @NotBlank String bedType,
        Boolean isIsolationCapable
) {}
