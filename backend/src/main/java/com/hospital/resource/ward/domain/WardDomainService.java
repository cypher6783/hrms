package com.hospital.resource.ward.domain;

import com.hospital.resource.bed.repository.BedRepository;
import com.hospital.resource.ward.entity.Ward;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WardDomainService {

    private final BedRepository bedRepository;

    @Transactional(readOnly = true)
    public long calculateTotalBeds(UUID wardId) {
        return bedRepository.countByWardIdAndStatus(wardId, "AVAILABLE") +
               bedRepository.countByWardIdAndStatus(wardId, "OCCUPIED") +
               bedRepository.countByWardIdAndStatus(wardId, "CLEANING_REQUIRED") +
               bedRepository.countByWardIdAndStatus(wardId, "MAINTENANCE");
    }

    @Transactional(readOnly = true)
    public long calculateAvailableBeds(UUID wardId) {
        return bedRepository.countAvailableByWardId(wardId);
    }

    @Transactional(readOnly = true)
    public long calculateOccupiedBeds(UUID wardId) {
        return bedRepository.countOccupiedByWardId(wardId);
    }

    @Transactional(readOnly = true)
    public long calculateCleaningBeds(UUID wardId) {
        return bedRepository.countByWardIdAndStatus(wardId, "CLEANING_REQUIRED");
    }

    @Transactional(readOnly = true)
    public long calculateActiveBeds(UUID wardId) {
        return bedRepository.countByWardIdAndStatus(wardId, "AVAILABLE") +
               bedRepository.countByWardIdAndStatus(wardId, "OCCUPIED") +
               bedRepository.countByWardIdAndStatus(wardId, "CLEANING_REQUIRED");
    }

    @Transactional(readOnly = true)
    public double calculateOccupancyRate(Ward ward) {
        long totalBeds = calculateTotalBeds(ward.getId());
        if (totalBeds == 0) {
            return 0.0;
        }
        long occupiedBeds = calculateOccupiedBeds(ward.getId());
        return ((double) occupiedBeds / ward.getMaxBedCapacity()) * 100.0;
    }
}
