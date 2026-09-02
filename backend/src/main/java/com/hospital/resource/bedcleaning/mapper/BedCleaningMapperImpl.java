package com.hospital.resource.bedcleaning.mapper;

import com.hospital.resource.bedcleaning.dto.CleaningTaskResponse;
import com.hospital.resource.bedcleaning.entity.BedCleaning;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BedCleaningMapperImpl implements BedCleaningMapper {

    @Override
    public CleaningTaskResponse toResponse(BedCleaning cleaning) {
        if (cleaning == null) return null;
        return new CleaningTaskResponse(
                cleaning.getId(),
                cleaning.getBedId(),
                cleaning.getAdmissionId(),
                cleaning.getStatus(),
                cleaning.getAssignedTo(),
                cleaning.getAssignedAt(),
                cleaning.getStartedAt(),
                cleaning.getCompletedAt(),
                cleaning.getVerifiedBy(),
                cleaning.getVerifiedAt(),
                cleaning.getCleaningNotes(),
                cleaning.getCreatedAt()
        );
    }

    @Override
    public List<CleaningTaskResponse> toResponseList(List<BedCleaning> cleaningTasks) {
        if (cleaningTasks == null) return List.of();
        return cleaningTasks.stream().map(this::toResponse).toList();
    }
}
