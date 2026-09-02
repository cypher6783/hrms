package com.hospital.resource.assessment.mapper;

import com.hospital.resource.assessment.dto.ClinicalAssessmentRequest;
import com.hospital.resource.assessment.dto.ClinicalAssessmentResponse;
import com.hospital.resource.assessment.dto.ClinicalAssessmentSummaryResponse;
import com.hospital.resource.assessment.entity.ClinicalAssessment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

public interface ClinicalAssessmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assessedBy", ignore = true)
    @Mapping(target = "isReassessment", ignore = true)
    @Mapping(target = "assessmentTimestamp", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ClinicalAssessment toEntity(ClinicalAssessmentRequest request);

    ClinicalAssessmentResponse toResponse(ClinicalAssessment assessment);

    ClinicalAssessmentSummaryResponse toSummary(ClinicalAssessment assessment);

    List<ClinicalAssessmentResponse> toResponseList(List<ClinicalAssessment> assessments);
}
