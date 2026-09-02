package com.hospital.resource.admission.domain;

import com.hospital.resource.admission.entity.Admission;
import com.hospital.resource.admission.repository.AdmissionRepository;
import com.hospital.resource.common.exception.ConflictException;
import com.hospital.resource.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdmissionDomainService {

    private final AdmissionRepository admissionRepository;

    @Transactional(readOnly = true)
    public void validateNewAdmission(UUID patientId) {
        var existing = admissionRepository.findByPatientIdAndIsActiveTrue(patientId);
        if (existing.isPresent()) {
            throw new ConflictException("Patient already has an active admission: " + existing.get().getAdmissionNumber());
        }
    }

    @Transactional(readOnly = true)
    public void validateTransfer(Admission admission) {
        if (!isActive(admission)) {
            throw new ValidationException("Only active admissions can be transferred");
        }
    }

    @Transactional(readOnly = true)
    public void validateDischarge(Admission admission) {
        if (!isActive(admission)) {
            throw new ValidationException("Only active admissions can be discharged");
        }
    }

    @Transactional(readOnly = true)
    public void validateBedAssignment(UUID bedId, String bedStatus) {
        if (!"AVAILABLE".equals(bedStatus)) {
            throw new ConflictException("Bed is not available. Current status: " + bedStatus);
        }
    }

    @Transactional(readOnly = true)
    public void validateAdmissionTimestamps(Admission admission, boolean isTransfer) {
        if (isTransfer && admission.getAdmittedAt() != null) {
            log.debug("Admission timestamp preserved during transfer: admissionId={}", admission.getId());
        }
    }

    private boolean isActive(Admission admission) {
        return admission.getIsActive() && ("ACTIVE".equals(admission.getStatus()) || "ADMITTED".equals(admission.getStatus()));
    }
}
