package com.hospital.resource.admission.dto;

import java.time.Instant;
import java.util.UUID;

public record AdmissionSearchRequest(
        UUID patientId,
        UUID wardId,
        String status,
        Instant dateFrom,
        Instant dateTo,
        int page,
        int size
) {
    public AdmissionSearchRequest {
        if (page < 0) page = 0;
        if (size < 1 || size > 100) size = 20;
    }
}
