package com.hospital.resource.patient.mapper;

import com.hospital.resource.patient.dto.PatientRequest;
import com.hospital.resource.patient.dto.PatientResponse;
import com.hospital.resource.patient.dto.PatientSummaryResponse;
import com.hospital.resource.patient.entity.Patient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatientMapperImpl implements PatientMapper {

    @Override
    public Patient toEntity(PatientRequest request) {
        if (request == null) return null;
        return Patient.builder()
                .fullName(request.fullName())
                .dateOfBirth(request.dateOfBirth())
                .gender(request.gender())
                .phoneNumber(request.phoneNumber())
                .address(request.address())
                .nextOfKinName(request.nextOfKinName())
                .nextOfKinPhone(request.nextOfKinPhone())
                .build();
    }

    @Override
    public PatientResponse toResponse(Patient patient) {
        if (patient == null) return null;
        return new PatientResponse(
                patient.getId(),
                patient.getPatientNumber(),
                patient.getFullName(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getPhoneNumber(),
                patient.getAddress(),
                patient.getNextOfKinName(),
                patient.getNextOfKinPhone(),
                patient.getIsActive(),
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }

    @Override
    public PatientSummaryResponse toSummary(Patient patient) {
        if (patient == null) return null;
        return new PatientSummaryResponse(
                patient.getId(),
                patient.getPatientNumber(),
                patient.getFullName(),
                patient.getGender(),
                patient.getDateOfBirth()
        );
    }

    @Override
    public List<PatientResponse> toResponseList(List<Patient> patients) {
        if (patients == null) return List.of();
        return patients.stream().map(this::toResponse).toList();
    }

    @Override
    public void updateEntity(PatientRequest request, Patient patient) {
        if (request == null || patient == null) return;
        if (request.fullName() != null) patient.setFullName(request.fullName());
        if (request.dateOfBirth() != null) patient.setDateOfBirth(request.dateOfBirth());
        if (request.gender() != null) patient.setGender(request.gender());
        if (request.phoneNumber() != null) patient.setPhoneNumber(request.phoneNumber());
        if (request.address() != null) patient.setAddress(request.address());
        if (request.nextOfKinName() != null) patient.setNextOfKinName(request.nextOfKinName());
        if (request.nextOfKinPhone() != null) patient.setNextOfKinPhone(request.nextOfKinPhone());
    }
}
