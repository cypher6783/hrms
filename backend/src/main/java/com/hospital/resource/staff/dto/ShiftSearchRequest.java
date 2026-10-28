package com.hospital.resource.staff.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ShiftSearchRequest(
        UUID wardId,
        LocalDate shiftDateFrom,
        LocalDate shiftDateTo,
        String shiftName,
        String status,
        int page,
        int size
) {
    public ShiftSearchRequest {
        if (page < 0) page = 0;
        if (size < 1 || size > 100) size = 20;
    }
}
