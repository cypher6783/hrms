package com.hospital.resource.ward.dto;

import java.util.UUID;

public record WardStatusResponse(
        UUID wardId,
        String wardName,
        Integer totalBeds,
        Integer availableBeds,
        Integer occupiedBeds,
        Integer cleaningBeds,
        Double occupancyRate
) {}
