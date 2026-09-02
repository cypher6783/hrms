package com.hospital.resource.bedcleaning.domain;

import com.hospital.resource.bedcleaning.entity.BedCleaning;
import com.hospital.resource.common.exception.BusinessException;
import com.hospital.resource.common.exception.ConflictException;
import com.hospital.resource.common.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class BedCleaningDomainService {

    private static final Set<String> VALID_STATUS_TRANSITIONS = Set.of(
            "PENDING→ASSIGNED",
            "ASSIGNED→IN_PROGRESS",
            "IN_PROGRESS→COMPLETED",
            "COMPLETED→VERIFIED"
    );

    public void validateStatusTransition(String currentStatus, String newStatus) {
        String transition = currentStatus + "→" + newStatus;
        if (!VALID_STATUS_TRANSITIONS.contains(transition)) {
            throw new BusinessException(
                    String.format("Invalid cleaning status transition from %s to %s", currentStatus, newStatus));
        }
    }

    public void validateAssignment(BedCleaning cleaning) {
        if (!"PENDING".equals(cleaning.getStatus())) {
            throw new ValidationException("Only PENDING tasks can be assigned");
        }
    }

    public void validateStart(BedCleaning cleaning) {
        if (!"ASSIGNED".equals(cleaning.getStatus())) {
            throw new ValidationException("Only ASSIGNED tasks can be started");
        }
        if (cleaning.getAssignedTo() == null) {
            throw new ValidationException("Task must be assigned before starting");
        }
    }

    public void validateComplete(BedCleaning cleaning) {
        if (!"IN_PROGRESS".equals(cleaning.getStatus())) {
            throw new ValidationException("Only IN_PROGRESS tasks can be completed");
        }
    }

    public void validateVerification(BedCleaning cleaning) {
        if (!"COMPLETED".equals(cleaning.getStatus())) {
            throw new ValidationException("Only COMPLETED tasks can be verified");
        }
    }
}
