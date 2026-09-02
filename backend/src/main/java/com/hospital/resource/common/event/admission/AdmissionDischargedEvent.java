package com.hospital.resource.common.event.admission;

import com.hospital.resource.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AdmissionDischargedEvent extends DomainEvent {

    private final UUID admissionId;
    private final UUID patientId;
    private final UUID wardId;
    private final UUID bedId;
    private final String dischargeOutcome;
    private final UUID updatedBy;

    public AdmissionDischargedEvent(Object source, UUID admissionId, UUID patientId,
                                    UUID wardId, UUID bedId, String dischargeOutcome, UUID updatedBy) {
        super(source, "ADMISSION_DISCHARGED");
        this.admissionId = admissionId;
        this.patientId = patientId;
        this.wardId = wardId;
        this.bedId = bedId;
        this.dischargeOutcome = dischargeOutcome;
        this.updatedBy = updatedBy;
    }
}
