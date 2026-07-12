package com.hospital.resource.patient.service;

import com.hospital.resource.common.dto.PagedResponse;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.common.util.NumberGenerator;
import com.hospital.resource.patient.dto.PatientRequest;
import com.hospital.resource.patient.dto.PatientResponse;
import com.hospital.resource.patient.entity.Patient;
import com.hospital.resource.patient.mapper.PatientMapper;
import com.hospital.resource.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientApplicationService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Transactional
    public PatientResponse createPatient(PatientRequest request, UUID userId) {
        Patient patient = patientMapper.toEntity(request);
        patient.setPatientNumber(NumberGenerator.generatePatientNumber());
        patient.setIsActive(true);
        patient.setCreatedBy(userId);
        patient.setUpdatedBy(userId);

        patient = patientRepository.save(patient);
        log.info("Patient created: patientId={}, patientNumber={}", patient.getId(), patient.getPatientNumber());
        return patientMapper.toResponse(patient);
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatient(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id.toString()));
        return patientMapper.toResponse(patient);
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientByNumber(String patientNumber) {
        Patient patient = patientRepository.findByPatientNumber(patientNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Patient with number: " + patientNumber));
        return patientMapper.toResponse(patient);
    }

    @Transactional(readOnly = true)
    public PagedResponse<PatientResponse> searchPatients(String search, int page, int size) {
        Page<Patient> patients = patientRepository.searchPatients(
                search != null ? search : "",
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        return PagedResponse.of(
                patientMapper.toResponseList(patients.getContent()),
                page,
                size,
                patients.getTotalElements()
        );
    }

    @Transactional
    public PatientResponse updatePatient(UUID id, PatientRequest request, UUID userId) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id.toString()));

        patientMapper.updateEntity(request, patient);
        patient.setUpdatedBy(userId);

        patient = patientRepository.save(patient);
        log.info("Patient updated: patientId={}", patient.getId());
        return patientMapper.toResponse(patient);
    }

    @Transactional
    public void deactivatePatient(UUID id, UUID userId) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id.toString()));
        patient.setIsActive(false);
        patient.setUpdatedBy(userId);
        patientRepository.save(patient);
        log.info("Patient deactivated: patientId={}", id);
    }

    @Transactional(readOnly = true)
    public long getActivePatientCount() {
        return patientRepository.countByIsActiveTrue();
    }
}
