package com.hospital.resource.admission.mapper;

import com.hospital.resource.admission.dto.AdmissionRequest;
import com.hospital.resource.admission.dto.AdmissionResponse;
import com.hospital.resource.admission.dto.AdmissionSummaryResponse;
import com.hospital.resource.admission.entity.Admission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

public interface AdmissionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "admissionNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dischargeOutcome", ignore = true)
    @Mapping(target = "dischargeNotes", ignore = true)
    @Mapping(target = "admittedAt", ignore = true)
    @Mapping(target = "dischargedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Admission toEntity(AdmissionRequest request);

    AdmissionResponse toResponse(Admission admission);

    AdmissionSummaryResponse toSummary(Admission admission);

    List<AdmissionResponse> toResponseList(List<Admission> admissions);

    List<AdmissionSummaryResponse> toSummaryList(List<Admission> admissions);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "admissionNumber", ignore = true)
    @Mapping(target = "patientId", ignore = true)
    @Mapping(target = "wardId", ignore = true)
    @Mapping(target = "bedId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "admissionNotes", ignore = true)
    @Mapping(target = "dischargeOutcome", ignore = true)
    @Mapping(target = "dischargeNotes", ignore = true)
    @Mapping(target = "admittedAt", ignore = true)
    @Mapping(target = "dischargedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(AdmissionRequest request, @MappingTarget Admission admission);
}
