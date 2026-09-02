package com.hospital.resource.bedcleaning.service;

import com.hospital.resource.bed.service.BedApplicationService;
import com.hospital.resource.bedcleaning.dto.*;
import com.hospital.resource.bedcleaning.entity.BedCleaning;
import com.hospital.resource.bedcleaning.repository.BedCleaningRepository;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BedCleaningApplicationService {

    private final BedCleaningRepository cleaningRepository;
    private final BedApplicationService bedService;

    @Transactional(readOnly = true)
    public List<CleaningTaskResponse> getPendingTasks() {
        return cleaningRepository.findByStatus("PENDING").stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CleaningTaskResponse> getTasksByBed(UUID bedId) {
        return cleaningRepository.findByBedIdAndStatus(bedId, "PENDING").stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CleaningTaskResponse assignTask(UUID cleaningId, CleaningAssignmentRequest request, UUID userId) {
        BedCleaning cleaning = cleaningRepository.findById(cleaningId)
                .orElseThrow(() -> new ResourceNotFoundException("Cleaning task", cleaningId.toString()));

        cleaning.setAssignedTo(request.staffId());
        cleaning.setAssignedAt(Instant.now());
        cleaning.setStatus("ASSIGNED");

        cleaning = cleaningRepository.save(cleaning);
        log.info("Cleaning task assigned: cleaningId={}, staffId={}", cleaningId, request.staffId());
        return toResponse(cleaning);
    }

    @Transactional
    public CleaningTaskResponse startCleaning(UUID cleaningId, UUID userId) {
        BedCleaning cleaning = cleaningRepository.findById(cleaningId)
                .orElseThrow(() -> new ResourceNotFoundException("Cleaning task", cleaningId.toString()));

        cleaning.setStatus("IN_PROGRESS");
        cleaning.setStartedAt(Instant.now());

        cleaning = cleaningRepository.save(cleaning);
        log.info("Cleaning started: cleaningId={}", cleaningId);
        return toResponse(cleaning);
    }

    @Transactional
    public CleaningTaskResponse completeCleaning(UUID cleaningId, CleaningCompletionRequest request, UUID userId) {
        BedCleaning cleaning = cleaningRepository.findById(cleaningId)
                .orElseThrow(() -> new ResourceNotFoundException("Cleaning task", cleaningId.toString()));

        cleaning.setStatus("COMPLETED");
        cleaning.setCompletedAt(Instant.now());
        cleaning.setCleaningNotes(request.cleaningNotes());

        cleaning = cleaningRepository.save(cleaning);
        log.info("Cleaning completed: cleaningId={}", cleaningId);
        return toResponse(cleaning);
    }

    @Transactional
    public CleaningTaskResponse verifyCleaning(UUID cleaningId, UUID userId) {
        BedCleaning cleaning = cleaningRepository.findById(cleaningId)
                .orElseThrow(() -> new ResourceNotFoundException("Cleaning task", cleaningId.toString()));

        cleaning.setStatus("VERIFIED");
        cleaning.setVerifiedBy(userId);
        cleaning.setVerifiedAt(Instant.now());

        // Make bed available
        bedService.updateBedStatus(cleaning.getBedId(), "AVAILABLE");

        cleaning = cleaningRepository.save(cleaning);
        log.info("Cleaning verified: cleaningId={}, bedId={}", cleaningId, cleaning.getBedId());
        return toResponse(cleaning);
    }

    @Transactional(readOnly = true)
    public long getPendingCleaningCount() {
        return cleaningRepository.findByStatus("PENDING").size();
    }

    private CleaningTaskResponse toResponse(BedCleaning cleaning) {
        return new CleaningTaskResponse(
                cleaning.getId(),
                cleaning.getBedId(),
                cleaning.getAdmissionId(),
                cleaning.getStatus(),
                cleaning.getAssignedTo(),
                cleaning.getAssignedAt(),
                cleaning.getStartedAt(),
                cleaning.getCompletedAt(),
                cleaning.getVerifiedBy(),
                cleaning.getVerifiedAt(),
                cleaning.getCleaningNotes(),
                cleaning.getCreatedAt()
        );
    }
}
