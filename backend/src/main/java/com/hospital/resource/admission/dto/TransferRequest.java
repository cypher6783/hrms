package com.hospital.resource.admission.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TransferRequest(
        @NotNull UUID newWardId,
        UUID newBedId,
        String transferNotes
) {}
