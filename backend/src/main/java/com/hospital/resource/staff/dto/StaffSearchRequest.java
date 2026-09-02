package com.hospital.resource.staff.dto;

import java.util.UUID;

public record StaffSearchRequest(
        String name,
        String role,
        String specialization,
        UUID wardId,
        String availabilityStatus,
        String certificationStatus,
        int page,
        int size
) {
    public StaffSearchRequest {
        if (page < 0) page = 0;
        if (size < 1 || size > 100) size = 20;
    }
}
