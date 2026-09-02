package com.hospital.resource.admission.service;

import com.hospital.resource.admission.dto.*;
import com.hospital.resource.admission.entity.Admission;
import com.hospital.resource.admission.repository.AdmissionRepository;
import com.hospital.resource.bed.service.BedApplicationService;
import com.hospital.resource.bedcleaning.entity.BedCleaning;
import com.hospital.resource.bedcleaning.repository.BedCleaningRepository;
import com.hospital.resource.common.exception.ConflictException;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.common.exception.ValidationException;
import com.hospital.resource.common.util.NumberGenerator;
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
public class AdmissionApplicationService {

    private final AdmissionRepository admissionRepository;
    private final BedApplicationService bedService;
    private final BedCleaningRepository cleaningRepository;

    @Transactional
    public AdmissionResponse createAdmission(AdmissionRequest request, UUID userId) {
        var existingAdmission = admissionRepository.findByPatientIdAndIsActiveTrue(request.patientId());
        if (existingAdmission.isPresent()) {
            throw new ConflictException("Patient already has an active admission");
        }

        Admission admission = Admission.builder()
                .admissionNumber(NumberGenerator.generateAdmissionNumber())
                .patientId(request.patientId())
                .wardId(request.wardId())
                .bedId(request.bedId())
                .status("ADMITTED")
                .admissionNotes(request.admissionNotes())
                .admittedAt(Instant.now())
                .isActive(true)
                .createdBy(userId)
                .updatedBy(userId)
                .build();

        if (request.bedId() != null) {
            bedService.updateBedStatus(request.bedId(), "OCCUPIED");
            var bed = bedService.getBed(request.bedId());
        }

        admission = admissionRepository.save(admission);
        log.info("Admission created: admissionId={}, patientId={}", admission.getId(), request.patientId());
        return toResponse(admission);
    }

    @Transactional(readOnly = true)
    public AdmissionResponse getAdmission(UUID id) {
        Admission admission = admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission", id.toString()));
        return toResponse(admission);
    }

    @Transactional(readOnly = true)
    public AdmissionResponse getActiveAdmissionByPatient(UUID patientId) {
        Admission admission = admissionRepository.findByPatientIdAndIsActiveTrue(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Active admission for patient: " + patientId));
        return toResponse(admission);
    }

    @Transactional
    public AdmissionResponse transferAdmission(UUID id, UUID newWardId, UUID newBedId, UUID userId) {
        Admission admission = admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission", id.toString()));

        // Release old bed
        if (admission.getBedId() != null) {
            bedService.updateBedStatus(admission.getBedId(), "CLEANING_REQUIRED");
            createCleaningTask(admission.getBedId(), id);
        }

        // Assign new bed
        admission.setWardId(newWardId);
        admission.setBedId(newBedId);
        admission.setUpdatedBy(userId);

        if (newBedId != null) {
            bedService.updateBedStatus(newBedId, "OCCUPIED");
        }

        admission = admissionRepository.save(admission);
        log.info("Admission transferred: admissionId={}, newWardId={}", id, newWardId);
        return toResponse(admission);
    }

    @Transactional
    public AdmissionResponse dischargeAdmission(UUID id, DischargeRequest request, UUID userId) {
        Admission admission = admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission", id.toString()));

        admission.setStatus("DISCHARGED");
        admission.setDischargeOutcome(request.dischargeOutcome());
        admission.setDischargeNotes(request.dischargeNotes());
        admission.setDischargedAt(Instant.now());
        admission.setIsActive(false);
        admission.setUpdatedBy(userId);

        // Release bed and create cleaning task
        if (admission.getBedId() != null) {
            bedService.updateBedStatus(admission.getBedId(), "CLEANING_REQUIRED");
            createCleaningTask(admission.getBedId(), id);
        }

        admission = admissionRepository.save(admission);
        log.info("Admission discharged: admissionId={}", id);
        return toResponse(admission);
    }

    @Transactional(readOnly = true)
    public long getActiveAdmissionCount() {
        return admissionRepository.countActiveAdmissions();
    }

    private void createCleaningTask(UUID bedId, UUID admissionId) {
        BedCleaning cleaning = BedCleaning.builder()
                .bedId(bedId)
                .admissionId(admissionId)
                .status("PENDING")
                .build();
        cleaningRepository.save(cleaning);
    }

    private AdmissionResponse toResponse(Admission admission) {
        return new AdmissionResponse(
                admission.getId(),
                admission.getAdmissionNumber(),
                admission.getPatientId(),
                admission.getWardId(),
                admission.getBedId(),
                admission.getStatus(),
                admission.getAdmissionNotes(),
                admission.getDischargeOutcome(),
                admission.getDischargeNotes(),
                admission.getAdmittedAt(),
                admission.getDischargedAt(),
                admission.getIsActive(),
                admission.getCreatedAt(),
                admission.getUpdatedAt()
        );
    }
}
