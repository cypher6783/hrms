package com.hospital.resource.common.event.admission;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AdmissionTransferredEvent extends DomainEvent {

    private final UUID admissionId;
    private final UUID patientId;
    private final UUID oldWardId;
    private final UUID newWardId;
    private final UUID oldBedId;
    private final UUID newBedId;
    private final UUID updatedBy;

    public AdmissionTransferredEvent(Object source, UUID admissionId, UUID patientId,
                                     UUID oldWardId, UUID newWardId,
                                     UUID oldBedId, UUID newBedId, UUID updatedBy) {
        super(source, "ADMISSION_TRANSFERRED");
        this.admissionId = admissionId;
        this.patientId = patientId;
        this.oldWardId = oldWardId;
        this.newWardId = newWardId;
        this.oldBedId = oldBedId;
        this.newBedId = newBedId;
        this.updatedBy = updatedBy;
    }
}
