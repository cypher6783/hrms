package com.hospital.resource.admission.service;

import com.hospital.resource.admission.domain.AdmissionDomainService;
import com.hospital.resource.admission.dto.*;
import com.hospital.resource.admission.entity.Admission;
import com.hospital.resource.admission.mapper.AdmissionMapper;
import com.hospital.resource.admission.repository.AdmissionRepository;
import com.hospital.resource.bed.service.BedApplicationService;
import com.hospital.resource.bedcleaning.entity.BedCleaning;
import com.hospital.resource.bedcleaning.repository.BedCleaningRepository;
import com.hospital.resource.common.dto.PagedResponse;
import com.hospital.resource.common.event.DomainEventPublisher;
import com.hospital.resource.common.event.admission.AdmissionCreatedEvent;
import com.hospital.resource.common.event.admission.AdmissionDischargedEvent;
import com.hospital.resource.common.event.admission.AdmissionTransferredEvent;
import com.hospital.resource.common.event.bed.BedAssignedEvent;
import com.hospital.resource.common.event.bed.BedCleaningCreatedEvent;
import com.hospital.resource.common.event.bed.BedReleasedEvent;
import com.hospital.resource.common.exception.ConflictException;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.common.util.NumberGenerator;
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
public class AdmissionApplicationService {

    private final AdmissionRepository admissionRepository;
    private final BedApplicationService bedService;
    private final BedCleaningRepository cleaningRepository;
    private final AdmissionDomainService admissionDomainService;
    private final AdmissionMapper admissionMapper;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public AdmissionResponse createAdmission(AdmissionRequest request, UUID userId) {
        admissionDomainService.validateNewAdmission(request.patientId());

        Admission admission = admissionMapper.toEntity(request);
        admission.setAdmissionNumber(NumberGenerator.generateAdmissionNumber());
        admission.setStatus("ADMITTED");
        admission.setAdmittedAt(Instant.now());
        admission.setIsActive(true);
        admission.setCreatedBy(userId);
        admission.setUpdatedBy(userId);

        if (request.bedId() != null) {
            var bed = bedService.getBed(request.bedId());
            admissionDomainService.validateBedAssignment(request.bedId(), bed.status());
            bedService.updateBedStatus(request.bedId(), "OCCUPIED");
        }

        admission = admissionRepository.save(admission);

        eventPublisher.publish(new AdmissionCreatedEvent(
                this, admission.getId(), request.patientId(),
                request.wardId(), request.bedId(), userId));

        if (request.bedId() != null) {
            eventPublisher.publish(new BedAssignedEvent(
                    this, request.bedId(), request.wardId(), admission.getId()));
        }

        log.info("Admission created: admissionId={}, patientId={}", admission.getId(), request.patientId());
        return admissionMapper.toResponse(admission);
    }

    @Transactional(readOnly = true)
    public AdmissionResponse getAdmission(UUID id) {
        Admission admission = admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission", id.toString()));
        return admissionMapper.toResponse(admission);
    }

    @Transactional(readOnly = true)
    public AdmissionSummaryResponse getAdmissionSummary(UUID id) {
        Admission admission = admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission", id.toString()));
        return admissionMapper.toSummary(admission);
    }

    @Transactional(readOnly = true)
    public AdmissionResponse getActiveAdmissionByPatient(UUID patientId) {
        Admission admission = admissionRepository.findByPatientIdAndIsActiveTrue(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Active admission for patient: " + patientId));
        return admissionMapper.toResponse(admission);
    }

    @Transactional(readOnly = true)
    public PagedResponse<AdmissionResponse> searchAdmissions(AdmissionSearchRequest request) {
        PageRequest pageRequest = PageRequest.of(request.page(), request.size(),
                Sort.by(Sort.Direction.DESC, "admittedAt"));

        Page<Admission> page = admissionRepository.searchAdmissions(
                request.patientId(), request.wardId(), request.status(),
                request.dateFrom(), request.dateTo(), pageRequest);

        List<AdmissionResponse> content = page.getContent().stream()
                .map(admissionMapper::toResponse)
                .toList();

        return PagedResponse.of(content, request.page(), request.size(), page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<AdmissionSummaryResponse> getAdmissionsByWard(UUID wardId) {
        List<Admission> admissions = admissionRepository.findByWardIdAndIsActiveTrue(wardId);
        return admissionMapper.toSummaryList(admissions);
    }

    @Transactional(readOnly = true)
    public AdmissionStatsResponse getAdmissionStats() {
        long activeCount = admissionRepository.countActiveAdmissions();
        long totalToday = admissionRepository.countAdmittedSince(
                Instant.now().truncatedTo(java.time.temporal.ChronoUnit.DAYS));
        long dischargedToday = admissionRepository.countDischargedSince(
                Instant.now().truncatedTo(java.time.temporal.ChronoUnit.DAYS));
        return new AdmissionStatsResponse(activeCount, totalToday, dischargedToday);
    }

    @Transactional
    public AdmissionResponse transferAdmission(UUID id, TransferRequest request, UUID userId) {
        Admission admission = admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission", id.toString()));

        admissionDomainService.validateTransfer(admission);

        UUID oldWardId = admission.getWardId();
        UUID oldBedId = admission.getBedId();

        if (admission.getBedId() != null) {
            bedService.updateBedStatus(admission.getBedId(), "CLEANING_REQUIRED");
            BedCleaning cleaning = createCleaningTask(admission.getBedId(), id);
            eventPublisher.publish(new BedReleasedEvent(this, admission.getBedId(), oldWardId, id));
            eventPublisher.publish(new BedCleaningCreatedEvent(this, cleaning.getId(), admission.getBedId(), id));
        }

        admission.setWardId(request.newWardId());
        admission.setBedId(request.newBedId());
        admission.setUpdatedBy(userId);

        if (request.newBedId() != null) {
            var bed = bedService.getBed(request.newBedId());
            admissionDomainService.validateBedAssignment(request.newBedId(), bed.status());
            bedService.updateBedStatus(request.newBedId(), "OCCUPIED");
            eventPublisher.publish(new BedAssignedEvent(this, request.newBedId(), request.newWardId(), id));
        }

        admission = admissionRepository.save(admission);

        eventPublisher.publish(new AdmissionTransferredEvent(
                this, id, admission.getPatientId(),
                oldWardId, request.newWardId(),
                oldBedId, request.newBedId(), userId));

        log.info("Admission transferred: admissionId={}, newWardId={}", id, request.newWardId());
        return admissionMapper.toResponse(admission);
    }

    @Transactional
    public AdmissionResponse dischargeAdmission(UUID id, DischargeRequest request, UUID userId) {
        Admission admission = admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission", id.toString()));

        admissionDomainService.validateDischarge(admission);

        admission.setStatus("DISCHARGED");
        admission.setDischargeOutcome(request.dischargeOutcome());
        admission.setDischargeNotes(request.dischargeNotes());
        admission.setDischargedAt(Instant.now());
        admission.setIsActive(false);
        admission.setUpdatedBy(userId);

        if (admission.getBedId() != null) {
            bedService.updateBedStatus(admission.getBedId(), "CLEANING_REQUIRED");
            BedCleaning cleaning = createCleaningTask(admission.getBedId(), id);
            eventPublisher.publish(new BedReleasedEvent(this, admission.getBedId(), admission.getWardId(), id));
            eventPublisher.publish(new BedCleaningCreatedEvent(this, cleaning.getId(), admission.getBedId(), id));
        }

        admission = admissionRepository.save(admission);

        eventPublisher.publish(new AdmissionDischargedEvent(
                this, id, admission.getPatientId(),
                admission.getWardId(), admission.getBedId(),
                request.dischargeOutcome(), userId));

        log.info("Admission discharged: admissionId={}", id);
        return admissionMapper.toResponse(admission);
    }

    @Transactional(readOnly = true)
    public long getActiveAdmissionCount() {
        return admissionRepository.countActiveAdmissions();
    }

    private BedCleaning createCleaningTask(UUID bedId, UUID admissionId) {
        BedCleaning cleaning = BedCleaning.builder()
                .bedId(bedId)
                .admissionId(admissionId)
                .status("PENDING")
                .build();
        return cleaningRepository.save(cleaning);
    }

    public record AdmissionStatsResponse(
            long activeAdmissions,
            long admittedToday,
            long dischargedToday
    ) {}
}
