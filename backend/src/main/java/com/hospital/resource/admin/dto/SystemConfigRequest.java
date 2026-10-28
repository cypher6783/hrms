package com.hospital.resource.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record SystemConfigRequest(
        @NotBlank String configValue,
        String description
) {}
