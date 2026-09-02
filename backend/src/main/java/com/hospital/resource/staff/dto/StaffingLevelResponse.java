package com.hospital.resource.staff.dto;

import java.util.UUID;

public record StaffingLevelResponse(
        UUID shiftId,
        int requiredStaff,
        int assignedStaff,
        int maxStaff,
        boolean isFullyStaffed,
        int deficit
) {}
