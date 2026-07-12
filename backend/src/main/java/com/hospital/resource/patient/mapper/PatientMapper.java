package com.hospital.resource.patient.mapper;

import com.hospital.resource.patient.dto.PatientRequest;
import com.hospital.resource.patient.dto.PatientResponse;
import com.hospital.resource.patient.dto.PatientSummaryResponse;
import com.hospital.resource.patient.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patientNumber", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Patient toEntity(PatientRequest request);

    PatientResponse toResponse(Patient patient);

    PatientSummaryResponse toSummary(Patient patient);

    List<PatientResponse> toResponseList(List<Patient> patients);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patientNumber", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(PatientRequest request, @MappingTarget Patient patient);
}
