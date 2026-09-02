package com.hospital.resource.bedcleaning.dto;

import java.time.Instant;
import java.util.UUID;

public record CleaningSearchRequest(
        UUID bedId,
        String status,
        UUID assignedTo,
        Instant dateFrom,
        Instant dateTo,
        int page,
        int size
) {
    public CleaningSearchRequest {
        if (page < 0) page = 0;
        if (size < 1 || size > 100) size = 20;
    }
}
