package com.hospital.resource.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.resource.admission.entity.Admission;
import com.hospital.resource.admission.repository.AdmissionRepository;
import com.hospital.resource.assessment.entity.ClinicalAssessment;
import com.hospital.resource.assessment.repository.ClinicalAssessmentRepository;
import com.hospital.resource.audit.service.AuditApplicationService;
import com.hospital.resource.bed.entity.Bed;
import com.hospital.resource.bed.repository.BedRepository;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.common.exception.ValidationException;
import com.hospital.resource.equipment.entity.Equipment;
import com.hospital.resource.equipment.repository.EquipmentRepository;
import com.hospital.resource.notification.service.NotificationApplicationService;
import com.hospital.resource.patient.entity.Patient;
import com.hospital.resource.patient.repository.PatientRepository;
import com.hospital.resource.recommendation.dto.RecommendationDecisionRequest;
import com.hospital.resource.recommendation.dto.RecommendationDecisionResponse;
import com.hospital.resource.recommendation.dto.RecommendationResponse;
import com.hospital.resource.recommendation.entity.AllocationRecommendation;
import com.hospital.resource.recommendation.entity.RecommendationDecision;
import com.hospital.resource.recommendation.entity.RecommendationItem;
import com.hospital.resource.recommendation.repository.AllocationRecommendationRepository;
import com.hospital.resource.recommendation.repository.RecommendationDecisionRepository;
import com.hospital.resource.recommendation.repository.RecommendationItemRepository;
import com.hospital.resource.recommendation.service.RecommendationApplicationService;
import com.hospital.resource.ward.entity.Ward;
import com.hospital.resource.ward.repository.WardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationApplicationServiceTest {

    @Mock
    private AllocationRecommendationRepository recommendationRepository;
    @Mock
    private RecommendationItemRepository itemRepository;
    @Mock
    private RecommendationDecisionRepository decisionRepository;
    @Mock
    private AdmissionRepository admissionRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private ClinicalAssessmentRepository clinicalAssessmentRepository;
    @Mock
    private BedRepository bedRepository;
    @Mock
    private WardRepository wardRepository;
    @Mock
    private EquipmentRepository equipmentRepository;
    @Mock
    private NotificationApplicationService notificationService;
    @Mock
    private AuditApplicationService auditService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private RecommendationApplicationService service;

    @BeforeEach
    void setUp() {
        service = new RecommendationApplicationService(
                recommendationRepository,
                itemRepository,
                decisionRepository,
                admissionRepository,
                patientRepository,
                clinicalAssessmentRepository,
                bedRepository,
                wardRepository,
                equipmentRepository,
                notificationService,
                objectMapper,
                auditService
        );
    }

    @Test
    @DisplayName("generateRecommendation evaluates real beds, applies isolation constraint for confirmed Lassa, and ranks top bed")
    void generateRecommendation_confirmedPatient_prioritizesIsolationBed() {
        UUID admissionId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        UUID wardId = UUID.randomUUID();

        Admission admission = Admission.builder()
                .id(admissionId)
                .admissionNumber("ADM-2026-001")
                .patientId(patientId)
                .wardId(wardId)
                .status("ADMITTED")
                .build();

        Patient patient = Patient.builder()
                .id(patientId)
                .patientNumber("PAT-001")
                .fullName("John Doe")
                .build();

        ClinicalAssessment assessment = ClinicalAssessment.builder()
                .id(UUID.randomUUID())
                .admissionId(admissionId)
                .patientId(patientId)
                .infectionStatus("CONFIRMED")
                .severityLevel("CRITICAL")
                .triageClassification("RED")
                .assessmentTimestamp(Instant.now())
                .build();

        Ward ward = Ward.builder()
                .id(wardId)
                .name("Lassa Isolation Ward A")
                .status("ACTIVE")
                .maxBedCapacity(10)
                .equipmentZone("ZONE_A")
                .build();

        Bed nonIsolationBed = Bed.builder()
                .id(UUID.randomUUID())
                .bedNumber("GEN-01")
                .wardId(wardId)
                .bedType("STANDARD")
                .isIsolationCapable(false)
                .status("AVAILABLE")
                .build();

        Bed isolationBed = Bed.builder()
                .id(UUID.randomUUID())
                .bedNumber("ISO-01")
                .wardId(wardId)
                .bedType("ICU")
                .isIsolationCapable(true)
                .status("AVAILABLE")
                .build();

        when(recommendationRepository.findByAdmissionId(admissionId)).thenReturn(List.of());
        when(admissionRepository.findById(admissionId)).thenReturn(Optional.of(admission));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(clinicalAssessmentRepository.findTopByAdmissionIdOrderByAssessmentTimestampDesc(admissionId)).thenReturn(assessment);
        when(wardRepository.findByIsActiveTrue()).thenReturn(List.of(ward));
        when(bedRepository.findAll()).thenReturn(List.of(nonIsolationBed, isolationBed));
        when(bedRepository.countOccupiedByWardId(wardId)).thenReturn(2L);

        when(recommendationRepository.save(any(AllocationRecommendation.class))).thenAnswer(invocation -> {
            AllocationRecommendation rec = invocation.getArgument(0);
            return AllocationRecommendation.builder()
                    .id(UUID.randomUUID())
                    .admissionId(rec.getAdmissionId())
                    .batchType(rec.getBatchType())
                    .status(rec.getStatus())
                    .generatedAt(rec.getGeneratedAt())
                    .expiresAt(rec.getExpiresAt())
                    .createdAt(Instant.now())
                    .build();
        });

        RecommendationResponse response = service.generateRecommendation(admissionId);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo("PENDING");
        assertThat(response.items()).isNotEmpty();

        var bedItem = response.items().stream()
                .filter(i -> "BED".equals(i.itemType()))
                .findFirst()
                .orElse(null);

        assertThat(bedItem).isNotNull();
        assertThat(bedItem.recommendedEntityId()).isEqualTo(isolationBed.getId());
        assertThat(bedItem.confidenceScore()).isGreaterThan(BigDecimal.valueOf(0.80));
        assertThat(bedItem.rationale()).contains("ISO-01");
        assertThat(bedItem.rationale()).contains("Lassa Isolation Ward A");
    }

    @Test
    @DisplayName("generateRecommendation when no eligible beds returns NO_FEASIBLE_ALLOCATION and does not fabricate bed ID")
    void generateRecommendation_whenNoEligibleBeds_returnsNoFeasibleAllocation() {
        UUID admissionId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        UUID wardId = UUID.randomUUID();

        Admission admission = Admission.builder()
                .id(admissionId)
                .admissionNumber("ADM-2026-002")
                .patientId(patientId)
                .status("ADMITTED")
                .build();

        ClinicalAssessment assessment = ClinicalAssessment.builder()
                .id(UUID.randomUUID())
                .admissionId(admissionId)
                .patientId(patientId)
                .infectionStatus("CONFIRMED")
                .assessmentTimestamp(Instant.now())
                .build();

        Ward ward = Ward.builder()
                .id(wardId)
                .name("General Ward")
                .status("ACTIVE")
                .maxBedCapacity(10)
                .build();

        // Only a non-isolation bed exists, but patient is CONFIRMED Lassa
        Bed nonIsolationBed = Bed.builder()
                .id(UUID.randomUUID())
                .bedNumber("GEN-02")
                .wardId(wardId)
                .isIsolationCapable(false)
                .status("AVAILABLE")
                .build();

        when(recommendationRepository.findByAdmissionId(admissionId)).thenReturn(List.of());
        when(admissionRepository.findById(admissionId)).thenReturn(Optional.of(admission));
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());
        when(clinicalAssessmentRepository.findTopByAdmissionIdOrderByAssessmentTimestampDesc(admissionId)).thenReturn(assessment);
        when(wardRepository.findByIsActiveTrue()).thenReturn(List.of(ward));
        when(bedRepository.findAll()).thenReturn(List.of(nonIsolationBed));

        when(recommendationRepository.save(any(AllocationRecommendation.class))).thenAnswer(invocation -> {
            AllocationRecommendation rec = invocation.getArgument(0);
            return AllocationRecommendation.builder()
                    .id(UUID.randomUUID())
                    .admissionId(rec.getAdmissionId())
                    .batchType(rec.getBatchType())
                    .status(rec.getStatus())
                    .generatedAt(rec.getGeneratedAt())
                    .expiresAt(rec.getExpiresAt())
                    .createdAt(Instant.now())
                    .build();
        });

        RecommendationResponse response = service.generateRecommendation(admissionId);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo("NO_FEASIBLE_ALLOCATION");
        // Verify NO BED item was fabricated!
        boolean hasBedItem = response.items().stream().anyMatch(i -> "BED".equals(i.itemType()));
        assertThat(hasBedItem).isFalse();

        // Verify alert notification was dispatched
        verify(notificationService).createNotification(
                contains("CAPACITY ALERT"),
                contains("could not find an available isolation bed"),
                eq("ALERT"),
                eq("RECOMMENDATION"),
                any(),
                eq(admissionId),
                any()
        );
    }

    @Test
    @DisplayName("generateRecommendation when bed table is empty does not fabricate UUID")
    void generateRecommendation_whenBedTableEmpty_doesNotFabricateUuid() {
        UUID admissionId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Admission admission = Admission.builder()
                .id(admissionId)
                .admissionNumber("ADM-2026-003")
                .patientId(patientId)
                .status("ADMITTED")
                .build();

        when(recommendationRepository.findByAdmissionId(admissionId)).thenReturn(List.of());
        when(admissionRepository.findById(admissionId)).thenReturn(Optional.of(admission));
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());
        when(clinicalAssessmentRepository.findTopByAdmissionIdOrderByAssessmentTimestampDesc(admissionId)).thenReturn(null);
        when(wardRepository.findByIsActiveTrue()).thenReturn(List.of());
        when(bedRepository.findAll()).thenReturn(List.of());

        when(recommendationRepository.save(any(AllocationRecommendation.class))).thenAnswer(invocation -> {
            AllocationRecommendation rec = invocation.getArgument(0);
            return AllocationRecommendation.builder()
                    .id(UUID.randomUUID())
                    .admissionId(rec.getAdmissionId())
                    .batchType(rec.getBatchType())
                    .status(rec.getStatus())
                    .generatedAt(rec.getGeneratedAt())
                    .expiresAt(rec.getExpiresAt())
                    .createdAt(Instant.now())
                    .build();
        });

        RecommendationResponse response = service.generateRecommendation(admissionId);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo("NO_FEASIBLE_ALLOCATION");
        assertThat(response.items()).isEmpty();
    }

    @Test
    @DisplayName("generateRecommendation excludes occupied bed and cleaning bed")
    void generateRecommendation_excludesOccupiedAndCleaningBeds() {
        UUID admissionId = UUID.randomUUID();
        UUID wardId = UUID.randomUUID();

        Admission admission = Admission.builder()
                .id(admissionId)
                .admissionNumber("ADM-2026-004")
                .patientId(UUID.randomUUID())
                .status("ADMITTED")
                .build();

        Ward ward = Ward.builder()
                .id(wardId)
                .name("Isolation Ward")
                .status("ACTIVE")
                .maxBedCapacity(10)
                .build();

        Bed occupiedBed = Bed.builder()
                .id(UUID.randomUUID())
                .bedNumber("ISO-OCC")
                .wardId(wardId)
                .status("OCCUPIED")
                .isIsolationCapable(true)
                .build();

        Bed cleaningBed = Bed.builder()
                .id(UUID.randomUUID())
                .bedNumber("ISO-CLN")
                .wardId(wardId)
                .status("CLEANING")
                .isIsolationCapable(true)
                .build();

        when(recommendationRepository.findByAdmissionId(admissionId)).thenReturn(List.of());
        when(admissionRepository.findById(admissionId)).thenReturn(Optional.of(admission));
        when(wardRepository.findByIsActiveTrue()).thenReturn(List.of(ward));
        when(bedRepository.findAll()).thenReturn(List.of(occupiedBed, cleaningBed));

        when(recommendationRepository.save(any(AllocationRecommendation.class))).thenAnswer(invocation -> {
            AllocationRecommendation rec = invocation.getArgument(0);
            return AllocationRecommendation.builder()
                    .id(UUID.randomUUID())
                    .status(rec.getStatus())
                    .admissionId(rec.getAdmissionId())
                    .batchType(rec.getBatchType())
                    .generatedAt(rec.getGeneratedAt())
                    .expiresAt(rec.getExpiresAt())
                    .createdAt(Instant.now())
                    .build();
        });

        RecommendationResponse response = service.generateRecommendation(admissionId);
        assertThat(response.status()).isEqualTo("NO_FEASIBLE_ALLOCATION");
        assertThat(response.items()).isEmpty();
    }

    @Test
    @DisplayName("recordDecision when accepted assigns real bed with pessimistic lock and logs audit trail")
    void recordDecision_whenAccepted_updatesAdmissionAndBedWithLockAndAudit() {
        UUID itemId = UUID.randomUUID();
        UUID recId = UUID.randomUUID();
        UUID admissionId = UUID.randomUUID();
        UUID bedId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        RecommendationItem item = RecommendationItem.builder()
                .id(itemId)
                .recommendationId(recId)
                .itemType("BED")
                .recommendedEntityType("BED")
                .recommendedEntityId(bedId)
                .status("PENDING")
                .build();

        AllocationRecommendation rec = AllocationRecommendation.builder()
                .id(recId)
                .admissionId(admissionId)
                .build();

        Admission admission = Admission.builder()
                .id(admissionId)
                .admissionNumber("ADM-2026-001")
                .status("ADMITTED")
                .build();

        Bed bed = Bed.builder()
                .id(bedId)
                .bedNumber("ISO-02")
                .status("AVAILABLE")
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(recommendationRepository.findById(recId)).thenReturn(Optional.of(rec));
        when(admissionRepository.findById(admissionId)).thenReturn(Optional.of(admission));
        when(bedRepository.findByIdWithLock(bedId)).thenReturn(Optional.of(bed));

        when(decisionRepository.save(any(RecommendationDecision.class))).thenAnswer(invocation -> {
            RecommendationDecision d = invocation.getArgument(0);
            return RecommendationDecision.builder()
                    .id(UUID.randomUUID())
                    .recommendationItemId(d.getRecommendationItemId())
                    .decisionType(d.getDecisionType())
                    .decidedBy(d.getDecidedBy())
                    .decidedAt(Instant.now())
                    .build();
        });

        RecommendationDecisionRequest request = new RecommendationDecisionRequest("ACCEPT", null, null);
        RecommendationDecisionResponse response = service.recordDecision(itemId, request, userId);

        assertThat(response).isNotNull();
        assertThat(response.decisionType()).isEqualTo("ACCEPT");
        assertThat(item.getStatus()).isEqualTo("ACCEPTED");

        // Verify row lock queried and bed allocated
        verify(bedRepository).findByIdWithLock(bedId);
        verify(admissionRepository).save(argThat(adm -> bedId.equals(adm.getBedId())));
        verify(bedRepository).save(argThat(b -> "OCCUPIED".equals(b.getStatus()) && admissionId.equals(b.getCurrentAdmissionId())));

        // Verify audit log recorded
        verify(auditService).recordAudit(
                eq(userId),
                eq("RECOMMENDATION_ACCEPT"),
                eq("BED"),
                eq(bedId),
                eq("AVAILABLE"),
                eq("OCCUPIED"),
                any(),
                contains("Bed ISO-02")
        );
    }

    @Test
    @DisplayName("recordDecision when bed has become occupied fails safely with ValidationException")
    void recordDecision_whenBedBecomeOccupied_failsSafelyWithoutModifyingState() {
        UUID itemId = UUID.randomUUID();
        UUID recId = UUID.randomUUID();
        UUID admissionId = UUID.randomUUID();
        UUID bedId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        RecommendationItem item = RecommendationItem.builder()
                .id(itemId)
                .recommendationId(recId)
                .itemType("BED")
                .recommendedEntityId(bedId)
                .status("PENDING")
                .build();

        AllocationRecommendation rec = AllocationRecommendation.builder()
                .id(recId)
                .admissionId(admissionId)
                .build();

        Admission admission = Admission.builder()
                .id(admissionId)
                .status("ADMITTED")
                .build();

        // Bed changed status to OCCUPIED between recommendation generation and decision!
        Bed occupiedBed = Bed.builder()
                .id(bedId)
                .bedNumber("ISO-03")
                .status("OCCUPIED")
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(recommendationRepository.findById(recId)).thenReturn(Optional.of(rec));
        when(admissionRepository.findById(admissionId)).thenReturn(Optional.of(admission));
        when(bedRepository.findByIdWithLock(bedId)).thenReturn(Optional.of(occupiedBed));

        RecommendationDecisionRequest request = new RecommendationDecisionRequest("ACCEPT", null, null);

        assertThatThrownBy(() -> service.recordDecision(itemId, request, userId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("no longer available");

        // Verify neither admission nor bed was updated
        verify(admissionRepository, never()).save(any());
        verify(bedRepository, never()).save(any());
        verify(decisionRepository, never()).save(any());
    }

    @Test
    @DisplayName("recordDecision when bed does not exist throws ResourceNotFoundException")
    void recordDecision_whenBedDoesNotExist_throwsResourceNotFoundException() {
        UUID itemId = UUID.randomUUID();
        UUID recId = UUID.randomUUID();
        UUID admissionId = UUID.randomUUID();
        UUID bedId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        RecommendationItem item = RecommendationItem.builder()
                .id(itemId)
                .recommendationId(recId)
                .itemType("BED")
                .recommendedEntityId(bedId)
                .status("PENDING")
                .build();

        AllocationRecommendation rec = AllocationRecommendation.builder()
                .id(recId)
                .admissionId(admissionId)
                .build();

        Admission admission = Admission.builder()
                .id(admissionId)
                .status("ADMITTED")
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(recommendationRepository.findById(recId)).thenReturn(Optional.of(rec));
        when(admissionRepository.findById(admissionId)).thenReturn(Optional.of(admission));
        when(bedRepository.findByIdWithLock(bedId)).thenReturn(Optional.empty());

        RecommendationDecisionRequest request = new RecommendationDecisionRequest("ACCEPT", null, null);

        assertThatThrownBy(() -> service.recordDecision(itemId, request, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Bed");

        verify(admissionRepository, never()).save(any());
        verify(bedRepository, never()).save(any());
    }
}
