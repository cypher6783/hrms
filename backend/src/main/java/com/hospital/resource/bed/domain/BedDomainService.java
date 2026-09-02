package com.hospital.resource.bed.domain;

import com.hospital.resource.bed.entity.Bed;
import com.hospital.resource.bed.repository.BedRepository;
import com.hospital.resource.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BedDomainService {

    private static final Set<String> VALID_STATUS_TRANSITIONS = Set.of(
            "AVAILABLE→OCCUPIED",
            "AVAILABLE→MAINTENANCE",
            "OCCUPIED→AVAILABLE",
            "OCCUPIED→CLEANING_REQUIRED",
            "CLEANING_REQUIRED→AVAILABLE",
            "MAINTENANCE→AVAILABLE"
    );

    private final BedRepository bedRepository;

    @Transactional(readOnly = true)
    public void validateStatusTransition(String currentStatus, String newStatus) {
        String transition = currentStatus + "→" + newStatus;
        if (!VALID_STATUS_TRANSITIONS.contains(transition)) {
            throw new BusinessException(
                    String.format("Invalid status transition from %s to %s", currentStatus, newStatus)
            );
        }
    }

    @Transactional(readOnly = true)
    public BedAvailabilitySummary getAvailabilitySummary(UUID wardId) {
        long totalBeds = bedRepository.countByWardIdAndStatus(wardId, "AVAILABLE") +
                        bedRepository.countByWardIdAndStatus(wardId, "OCCUPIED") +
                        bedRepository.countByWardIdAndStatus(wardId, "CLEANING_REQUIRED") +
                        bedRepository.countByWardIdAndStatus(wardId, "MAINTENANCE");

        long availableBeds = bedRepository.countAvailableByWardId(wardId);
        long occupiedBeds = bedRepository.countOccupiedByWardId(wardId);
        long cleaningBeds = bedRepository.countByWardIdAndStatus(wardId, "CLEANING_REQUIRED");
        long maintenanceBeds = bedRepository.countByWardIdAndStatus(wardId, "MAINTENANCE");

        return new BedAvailabilitySummary(totalBeds, availableBeds, occupiedBeds, cleaningBeds, maintenanceBeds);
    }

    public record BedAvailabilitySummary(
            long totalBeds,
            long availableBeds,
            long occupiedBeds,
            long cleaningBeds,
            long maintenanceBeds
    ) {}
}
