package com.hospital.resource.bedcleaning.service;

import com.hospital.resource.bed.service.BedApplicationService;
import com.hospital.resource.bedcleaning.domain.BedCleaningDomainService;
import com.hospital.resource.bedcleaning.dto.*;
import com.hospital.resource.bedcleaning.entity.BedCleaning;
import com.hospital.resource.bedcleaning.mapper.BedCleaningMapper;
import com.hospital.resource.bedcleaning.repository.BedCleaningRepository;
import com.hospital.resource.common.dto.PagedResponse;
import com.hospital.resource.common.event.DomainEventPublisher;
import com.hospital.resource.common.event.bed.BedCleaningCompletedEvent;
import com.hospital.resource.common.event.bed.BedCleaningStartedEvent;
import com.hospital.resource.common.event.bed.BedCleaningVerifiedEvent;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BedCleaningApplicationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BedCleaningApplicationService.class);

    private final BedCleaningRepository cleaningRepository;
    private final BedApplicationService bedService;
    private final BedCleaningDomainService cleaningDomainService;
    private final BedCleaningMapper cleaningMapper;
    private final DomainEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<CleaningTaskResponse> getPendingTasks() {
        return cleaningRepository.findByStatus("PENDING").stream()
                .map(cleaningMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CleaningTaskResponse> getTasksByBed(UUID bedId) {
        return cleaningRepository.findByBedIdAndStatus(bedId, "PENDING").stream()
                .map(cleaningMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PagedResponse<CleaningTaskResponse> searchTasks(CleaningSearchRequest request) {
        PageRequest pageRequest = PageRequest.of(request.page(), request.size(),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<BedCleaning> page = cleaningRepository.searchCleaningTasks(
                request.bedId(), request.status(), request.assignedTo(),
                request.dateFrom(), request.dateTo(), pageRequest);

        List<CleaningTaskResponse> content = page.getContent().stream()
                .map(cleaningMapper::toResponse)
                .toList();

        return PagedResponse.of(content, request.page(), request.size(), page.getTotalElements());
    }

    @Transactional
    public CleaningTaskResponse assignTask(UUID cleaningId, CleaningAssignmentRequest request, UUID userId) {
        BedCleaning cleaning = cleaningRepository.findById(cleaningId)
                .orElseThrow(() -> new ResourceNotFoundException("Cleaning task", cleaningId.toString()));

        cleaningDomainService.validateAssignment(cleaning);

        cleaning.setAssignedTo(request.staffId());
        cleaning.setAssignedAt(Instant.now());
        cleaning.setStatus("ASSIGNED");

        cleaning = cleaningRepository.save(cleaning);
        log.info("Cleaning task assigned: cleaningId={}, staffId={}", cleaningId, request.staffId());
        return cleaningMapper.toResponse(cleaning);
    }

    @Transactional
    public CleaningTaskResponse startCleaning(UUID cleaningId, UUID userId) {
        BedCleaning cleaning = cleaningRepository.findById(cleaningId)
                .orElseThrow(() -> new ResourceNotFoundException("Cleaning task", cleaningId.toString()));

        cleaningDomainService.validateStart(cleaning);

        cleaning.setStatus("IN_PROGRESS");
        cleaning.setStartedAt(Instant.now());

        cleaning = cleaningRepository.save(cleaning);

        eventPublisher.publish(new BedCleaningStartedEvent(
                this, cleaningId, cleaning.getBedId(), cleaning.getAssignedTo()));

        log.info("Cleaning started: cleaningId={}", cleaningId);
        return cleaningMapper.toResponse(cleaning);
    }

    @Transactional
    public CleaningTaskResponse completeCleaning(UUID cleaningId, CleaningCompletionRequest request, UUID userId) {
        BedCleaning cleaning = cleaningRepository.findById(cleaningId)
                .orElseThrow(() -> new ResourceNotFoundException("Cleaning task", cleaningId.toString()));

        cleaningDomainService.validateComplete(cleaning);

        cleaning.setStatus("COMPLETED");
        cleaning.setCompletedAt(Instant.now());
        cleaning.setCleaningNotes(request.cleaningNotes());

        cleaning = cleaningRepository.save(cleaning);

        eventPublisher.publish(new BedCleaningCompletedEvent(
                this, cleaningId, cleaning.getBedId(), cleaning.getAssignedTo()));

        log.info("Cleaning completed: cleaningId={}", cleaningId);
        return cleaningMapper.toResponse(cleaning);
    }

    @Transactional
    public CleaningTaskResponse verifyCleaning(UUID cleaningId, UUID userId) {
        BedCleaning cleaning = cleaningRepository.findById(cleaningId)
                .orElseThrow(() -> new ResourceNotFoundException("Cleaning task", cleaningId.toString()));

        cleaningDomainService.validateVerification(cleaning);

        cleaning.setStatus("VERIFIED");
        cleaning.setVerifiedBy(userId);
        cleaning.setVerifiedAt(Instant.now());

        bedService.updateBedStatus(cleaning.getBedId(), "AVAILABLE");

        cleaning = cleaningRepository.save(cleaning);

        eventPublisher.publish(new BedCleaningVerifiedEvent(
                this, cleaningId, cleaning.getBedId(), userId));

        log.info("Cleaning verified: cleaningId={}, bedId={}", cleaningId, cleaning.getBedId());
        return cleaningMapper.toResponse(cleaning);
    }

    @Transactional(readOnly = true)
    public long getPendingCleaningCount() {
        return cleaningRepository.countByStatus("PENDING");
    }

    @Transactional(readOnly = true)
    public CleaningStatsResponse getCleaningStats() {
        long pendingCount = cleaningRepository.countByStatus("PENDING");
        long inProgressCount = cleaningRepository.countByStatus("IN_PROGRESS");
        long completedToday = cleaningRepository.countCompletedSince(
                Instant.now().truncatedTo(java.time.temporal.ChronoUnit.DAYS));
        return new CleaningStatsResponse(pendingCount, inProgressCount, completedToday);
    }

    public record CleaningStatsResponse(
            long pendingTasks,
            long inProgressTasks,
            long completedToday
    ) {}
}
