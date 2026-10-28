package com.hospital.resource.common.event.admission;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AdmissionCreatedEvent extends DomainEvent {

    private final UUID admissionId;
    private final UUID patientId;
    private final UUID wardId;
    private final UUID bedId;
    private final UUID createdBy;

    public AdmissionCreatedEvent(Object source, UUID admissionId, UUID patientId, UUID wardId, UUID bedId, UUID createdBy) {
        super(source, "ADMISSION_CREATED");
        this.admissionId = admissionId;
        this.patientId = patientId;
        this.wardId = wardId;
        this.bedId = bedId;
        this.createdBy = createdBy;
    }
}
