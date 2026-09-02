package com.hospital.resource.admission.mapper;

import com.hospital.resource.admission.dto.AdmissionRequest;
import com.hospital.resource.admission.dto.AdmissionResponse;
import com.hospital.resource.admission.entity.Admission;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdmissionMapperImpl implements AdmissionMapper {

    @Override
    public Admission toEntity(AdmissionRequest request) {
        if (request == null) return null;
        return Admission.builder()
                .patientId(request.patientId())
                .wardId(request.wardId())
                .bedId(request.bedId())
                .admissionNotes(request.admissionNotes())
                .build();
    }

    @Override
    public AdmissionResponse toResponse(Admission admission) {
        if (admission == null) return null;
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

    @Override
    public List<AdmissionResponse> toResponseList(List<Admission> admissions) {
        if (admissions == null) return List.of();
        return admissions.stream().map(this::toResponse).toList();
    }

    @Override
    public void updateEntity(AdmissionRequest request, Admission admission) {
        if (request == null || admission == null) return;
        if (request.patientId() != null) admission.setPatientId(request.patientId());
        if (request.wardId() != null) admission.setWardId(request.wardId());
        if (request.bedId() != null) admission.setBedId(request.bedId());
        if (request.admissionNotes() != null) admission.setAdmissionNotes(request.admissionNotes());
    }
}
