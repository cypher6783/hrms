package com.hospital.resource.bed.dto;

import java.util.UUID;

public record BedAvailabilityResponse(
        UUID wardId,
        String wardName,
        int totalBeds,
        int availableBeds,
        int availableIsolationBeds,
        int occupiedBeds
) {}
