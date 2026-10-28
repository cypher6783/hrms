package com.hospital.resource.bedcleaning.mapper;

import com.hospital.resource.bedcleaning.dto.CleaningTaskResponse;
import com.hospital.resource.bedcleaning.dto.CleaningTaskSummaryResponse;
import com.hospital.resource.bedcleaning.entity.BedCleaning;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

public interface BedCleaningMapper {

    CleaningTaskResponse toResponse(BedCleaning cleaning);

    CleaningTaskSummaryResponse toSummary(BedCleaning cleaning);

    List<CleaningTaskResponse> toResponseList(List<BedCleaning> cleanings);

    List<CleaningTaskSummaryResponse> toSummaryList(List<BedCleaning> cleanings);
}
