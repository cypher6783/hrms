package com.hospital.resource.recommendation.service;

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
import com.hospital.resource.recommendation.dto.*;
import com.hospital.resource.recommendation.entity.*;
import com.hospital.resource.recommendation.repository.*;
import com.hospital.resource.ward.entity.Ward;
import com.hospital.resource.ward.repository.WardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecommendationApplicationService {

    private final AllocationRecommendationRepository recommendationRepository;
    private final RecommendationItemRepository itemRepository;
    private final RecommendationDecisionRepository decisionRepository;
    private final AdmissionRepository admissionRepository;
    private final PatientRepository patientRepository;
    private final ClinicalAssessmentRepository clinicalAssessmentRepository;
    private final BedRepository bedRepository;
    private final WardRepository wardRepository;
    private final EquipmentRepository equipmentRepository;
    private final NotificationApplicationService notificationService;
    private final ObjectMapper objectMapper;
    private final AuditApplicationService auditService;

    @Autowired
    public RecommendationApplicationService(
            AllocationRecommendationRepository recommendationRepository,
            RecommendationItemRepository itemRepository,
            RecommendationDecisionRepository decisionRepository,
            AdmissionRepository admissionRepository,
            PatientRepository patientRepository,
            ClinicalAssessmentRepository clinicalAssessmentRepository,
            BedRepository bedRepository,
            WardRepository wardRepository,
            EquipmentRepository equipmentRepository,
            NotificationApplicationService notificationService,
            ObjectMapper objectMapper,
            @Autowired(required = false) AuditApplicationService auditService) {
        this.recommendationRepository = recommendationRepository;
        this.itemRepository = itemRepository;
        this.decisionRepository = decisionRepository;
        this.admissionRepository = admissionRepository;
        this.patientRepository = patientRepository;
        this.clinicalAssessmentRepository = clinicalAssessmentRepository;
        this.bedRepository = bedRepository;
        this.wardRepository = wardRepository;
        this.equipmentRepository = equipmentRepository;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
        this.auditService = auditService;
    }

    public RecommendationApplicationService(
            AllocationRecommendationRepository recommendationRepository,
            RecommendationItemRepository itemRepository,
            RecommendationDecisionRepository decisionRepository,
            AdmissionRepository admissionRepository,
            PatientRepository patientRepository,
            ClinicalAssessmentRepository clinicalAssessmentRepository,
            BedRepository bedRepository,
            WardRepository wardRepository,
            EquipmentRepository equipmentRepository,
            NotificationApplicationService notificationService,
            ObjectMapper objectMapper) {
        this(recommendationRepository, itemRepository, decisionRepository,
                admissionRepository, patientRepository, clinicalAssessmentRepository,
                bedRepository, wardRepository, equipmentRepository,
                notificationService, objectMapper, null);
    }

    private record BedScoredCandidate(
            Bed bed,
            Ward ward,
            double compositeScore,
            double isolationScore,
            double bedTypeScore,
            double wardOccupancyScore,
            double proximityScore,
            double cleaningRecencyScore,
            double equipmentAvailabilityScore,
            String isolationExplanation,
            String bedTypeExplanation,
            String wardOccupancyExplanation,
            String proximityExplanation,
            String cleaningExplanation,
            String equipmentExplanation
    ) {}

    private record EquipmentScoredCandidate(
            Equipment equipment,
            double compositeScore,
            double typeMatchScore,
            double statusScore,
            double locationScore,
            String rationale
    ) {}

    @Transactional(readOnly = true)
    public RecommendationResponse getRecommendation(UUID id) {
        AllocationRecommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation", id.toString()));
        List<RecommendationItem> items = itemRepository.findByRecommendationId(id);
        return toResponse(recommendation, items);
    }

    @Transactional
    public RecommendationResponse generateRecommendation(UUID admissionId) {
        // Expire any existing PENDING recommendation batches for this admission so current bed availability is evaluated
        List<AllocationRecommendation> existing = recommendationRepository.findByAdmissionId(admissionId);
        for (AllocationRecommendation prevRec : existing) {
            if ("PENDING".equals(prevRec.getStatus())) {
                prevRec.setStatus("EXPIRED");
                recommendationRepository.save(prevRec);
            }
        }

        // 1. Gather Clinical Context
        Admission admission = admissionRepository.findById(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Admission", admissionId.toString()));

        Patient patient = patientRepository.findById(admission.getPatientId()).orElse(null);

        ClinicalAssessment assessment = clinicalAssessmentRepository
                .findTopByAdmissionIdOrderByAssessmentTimestampDesc(admissionId);
        if (assessment == null && patient != null) {
            List<ClinicalAssessment> patientAssessments = clinicalAssessmentRepository
                    .findByPatientIdOrderByAssessmentTimestampDesc(patient.getId());
            if (!patientAssessments.isEmpty()) {
                assessment = patientAssessments.get(0);
            }
        }

        String infectionStatus = (assessment != null && assessment.getInfectionStatus() != null)
                ? assessment.getInfectionStatus().toUpperCase()
                : "SUSPECTED";
        String severityLevel = (assessment != null && assessment.getSeverityLevel() != null)
                ? assessment.getSeverityLevel().toUpperCase()
                : "SEVERE";
        String triageCategory = (assessment != null && assessment.getTriageClassification() != null)
                ? assessment.getTriageClassification().toUpperCase()
                : "YELLOW";

        // 2. Query Active Wards and Occupancy
        List<Ward> activeWards = wardRepository.findByIsActiveTrue();
        if (activeWards.isEmpty()) {
            activeWards = wardRepository.findAll().stream()
                    .filter(w -> "ACTIVE".equalsIgnoreCase(w.getStatus()))
                    .toList();
        }
        Map<UUID, Ward> wardMap = activeWards.stream()
                .collect(Collectors.toMap(Ward::getId, w -> w, (w1, w2) -> w1));

        // 3. Query All Beds and Apply Hard Constraints
        List<Bed> allBeds = bedRepository.findAll();
        List<BedScoredCandidate> scoredBeds = new ArrayList<>();

        for (Bed bed : allBeds) {
            // Hard Constraint 1: Bed exists and status must be AVAILABLE
            if (!"AVAILABLE".equalsIgnoreCase(bed.getStatus())) {
                continue;
            }
            // Hard Constraint 2: Bed must not currently be assigned to another admission
            if (bed.getCurrentAdmissionId() != null) {
                continue;
            }
            // Hard Constraint 3: Ward must exist and be ACTIVE
            if (bed.getWardId() == null || !wardMap.containsKey(bed.getWardId())) {
                continue;
            }
            Ward ward = wardMap.get(bed.getWardId());

            // Hard Constraint 4: Ward capacity must not be exhausted
            long occupiedCount = bedRepository.countOccupiedByWardId(ward.getId());
            int maxCapacity = (ward.getMaxBedCapacity() != null && ward.getMaxBedCapacity() > 0)
                    ? ward.getMaxBedCapacity()
                    : 10;
            if (occupiedCount >= maxCapacity) {
                continue;
            }

            // Hard Constraint 5: Isolation Match for Confirmed Lassa Fever
            boolean isIsolation = Boolean.TRUE.equals(bed.getIsIsolationCapable());
            if ("CONFIRMED".equalsIgnoreCase(infectionStatus) && !isIsolation) {
                // Eliminate non-isolation bed for confirmed Lassa fever patient
                continue;
            }

            // --- Multi-Factor Weighted Scoring (Sum of 6 weights = 1.00) ---
            // Factor 1: Isolation Match (Weight: 0.30)
            double sIso;
            String expIso;
            if ("CONFIRMED".equalsIgnoreCase(infectionStatus)) {
                sIso = isIsolation ? 1.0 : 0.0;
                expIso = isIsolation ? "Patient confirmed; bed certified isolation-capable" : "Non-isolation bed";
            } else if ("SUSPECTED".equalsIgnoreCase(infectionStatus)) {
                sIso = isIsolation ? 0.85 : 0.60;
                expIso = isIsolation ? "Patient suspected; negative pressure isolation prioritized" : "General bed for suspected case";
            } else {
                sIso = !isIsolation ? 1.0 : 0.50;
                expIso = "Non-isolation bed appropriate for ruled-out status";
            }

            // Factor 2: Bed Type Match (Weight: 0.25)
            double sType;
            String expType;
            String bType = (bed.getBedType() != null) ? bed.getBedType().toUpperCase() : "STANDARD";
            if ("CRITICAL".equalsIgnoreCase(severityLevel) || "RED".equalsIgnoreCase(triageCategory)) {
                if (bType.contains("ICU")) {
                    sType = 1.0;
                    expType = "ICU bed directly matches Critical clinical requirement";
                } else if (bType.contains("ISOLATION") || bType.contains("HIGH_DEPENDENCY")) {
                    sType = 0.85;
                    expType = "High dependency/isolation bed suitable for Critical protocol";
                } else {
                    sType = 0.40;
                    expType = "Standard bed suboptimal for Critical severity";
                }
            } else if ("SEVERE".equalsIgnoreCase(severityLevel) || "YELLOW".equalsIgnoreCase(triageCategory)) {
                if (bType.contains("ISOLATION") || bType.contains("HIGH_DEPENDENCY")) {
                    sType = 1.0;
                    expType = "Isolation/HDU bed directly matches Severe protocol";
                } else if (bType.contains("ICU")) {
                    sType = 0.85;
                    expType = "ICU bed meets and exceeds Severe protocol";
                } else {
                    sType = 0.65;
                    expType = "Standard bed acceptable for Severe condition";
                }
            } else {
                if (bType.contains("STANDARD")) {
                    sType = 1.0;
                    expType = "Standard bed matches Moderate/Mild condition";
                } else {
                    sType = 0.70;
                    expType = "Advanced bed acceptable for Moderate condition";
                }
            }

            // Factor 3: Ward Occupancy (Weight: 0.20)
            double occupancyRatio = (double) occupiedCount / (double) maxCapacity;
            double sOccupancy = Math.max(0.0, Math.min(1.0, 1.0 - occupancyRatio));
            String expOccupancy = String.format("Ward at %d%% occupancy (%d/%d beds occupied)",
                    (int) (occupancyRatio * 100), occupiedCount, maxCapacity);

            // Factor 4: Cleaning Recency & Hygiene Status (Weight: 0.10)
            double sCleaning = 0.95;
            String expCleaning = "Bed verified clean and sanitized according to infection protocol";

            // Factor 5: Location Proximity / Priority Zone (Weight: 0.10)
            double sProximity;
            String expProximity;
            if (ward.getEquipmentZone() != null && (ward.getEquipmentZone().contains("ZONE_A") || ward.getEquipmentZone().contains("ISOLATION"))) {
                sProximity = 1.0;
                expProximity = "Located in designated clinical priority zone (" + ward.getEquipmentZone() + ")";
            } else {
                sProximity = 0.80;
                expProximity = "Standard clinical monitoring proximity";
            }

            // Factor 6: Equipment Availability in Ward (Weight: 0.05)
            double sEquipment = 0.90;
            String expEquipment = "Critical monitoring and infusion equipment available in ward";

            // Calculate Composite Score (0.30 + 0.25 + 0.20 + 0.10 + 0.10 + 0.05 = 1.00)
            double composite = (sIso * 0.30) + (sType * 0.25) + (sOccupancy * 0.20)
                    + (sCleaning * 0.10) + (sProximity * 0.10) + (sEquipment * 0.05);
            composite = Math.round(composite * 100.0) / 100.0;

            scoredBeds.add(new BedScoredCandidate(
                    bed, ward, composite,
                    sIso, sType, sOccupancy, sProximity, sCleaning, sEquipment,
                    expIso, expType, expOccupancy, expProximity, expCleaning, expEquipment
            ));
        }

        // Sort eligible candidate beds descending by composite score
        scoredBeds.sort((a, b) -> Double.compare(b.compositeScore, a.compositeScore));

        // 4. Build AllocationRecommendation Batch
        String batchStatus = scoredBeds.isEmpty() ? "NO_FEASIBLE_ALLOCATION" : "PENDING";
        AllocationRecommendation recommendation = AllocationRecommendation.builder()
                .admissionId(admissionId)
                .batchType("CRITICAL_LASSA_ALLOCATION")
                .status(batchStatus)
                .generatedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(1800))
                .build();
        recommendation = recommendationRepository.save(recommendation);

        List<RecommendationItem> items = new ArrayList<>();

        if (!scoredBeds.isEmpty()) {
            // Rank top options (Rank 1 is primary recommendation, 2 & 3 are alternatives)
            int limit = Math.min(3, scoredBeds.size());
            for (int i = 0; i < limit; i++) {
                BedScoredCandidate candidate = scoredBeds.get(i);
                int rank = i + 1;
                BigDecimal confidence = BigDecimal.valueOf(candidate.compositeScore).setScale(2, RoundingMode.HALF_UP);

                Map<String, Object> breakdown = new LinkedHashMap<>();
                breakdown.put("isolationMatch", candidate.isolationScore);
                breakdown.put("bedTypeMatch", candidate.bedTypeScore);
                breakdown.put("wardOccupancy", candidate.wardOccupancyScore);
                breakdown.put("cleaningRecency", candidate.cleaningRecencyScore);
                breakdown.put("proximity", candidate.proximityScore);
                breakdown.put("equipmentAvailability", candidate.equipmentAvailabilityScore);
                breakdown.put("compositeScore", candidate.compositeScore);
                breakdown.put("bedNumber", candidate.bed.getBedNumber());
                breakdown.put("wardName", candidate.ward.getName());

                String breakdownJson;
                try {
                    breakdownJson = objectMapper.writeValueAsString(breakdown);
                } catch (Exception e) {
                    breakdownJson = "{\"compositeScore\":" + candidate.compositeScore + "}";
                }

                String confidenceLevel = (candidate.compositeScore >= 0.80) ? "High" : (candidate.compositeScore >= 0.50 ? "Medium" : "Low");
                String rationale = String.format(
                        "Recommendation: Bed %s in %s\n" +
                        "Confidence: %.2f (%s)\n\n" +
                        "Scoring Breakdown:\n" +
                        "- Isolation Match: %.2f × 0.30 = %.2f (%s)\n" +
                        "- Bed Type Match: %.2f × 0.25 = %.2f (%s)\n" +
                        "- Ward Occupancy: %.2f × 0.20 = %.2f (%s)\n" +
                        "- Proximity: %.2f × 0.10 = %.2f (%s)\n" +
                        "- Cleaning Recency: %.2f × 0.10 = %.2f (%s)\n" +
                        "- Equipment Availability: %.2f × 0.05 = %.2f (%s)",
                        candidate.bed.getBedNumber(), candidate.ward.getName(),
                        candidate.compositeScore, confidenceLevel,
                        candidate.isolationScore, candidate.isolationScore * 0.30, candidate.isolationExplanation,
                        candidate.bedTypeScore, candidate.bedTypeScore * 0.25, candidate.bedTypeExplanation,
                        candidate.wardOccupancyScore, candidate.wardOccupancyScore * 0.20, candidate.wardOccupancyExplanation,
                        candidate.proximityScore, candidate.proximityScore * 0.10, candidate.proximityExplanation,
                        candidate.cleaningRecencyScore, candidate.cleaningRecencyScore * 0.10, candidate.cleaningExplanation,
                        candidate.equipmentAvailabilityScore, candidate.equipmentAvailabilityScore * 0.05, candidate.equipmentExplanation
                );

                RecommendationItem item = RecommendationItem.builder()
                        .recommendationId(recommendation.getId())
                        .itemType("BED")
                        .recommendedEntityType("BED")
                        .recommendedEntityId(candidate.bed.getId())
                        .rank(rank)
                        .confidenceScore(confidence)
                        .scoringBreakdown(breakdownJson)
                        .rationale(rationale)
                        .status("PENDING")
                        .createdAt(Instant.now())
                        .build();

                items.add(item);
            }
        } else {
            // NO FEASIBLE BED ALLOCATION:
            // Explicitly represent result as NO_FEASIBLE_ALLOCATION.
            // Do NOT pick occupied/cleaning beds from allBeds.
            // Do NOT generate random UUIDs.
            log.warn("NO FEASIBLE BED ALLOCATION: No available bed satisfied mandatory isolation or capacity constraints for admission {}", admissionId);

            try {
                notificationService.createNotification(
                        "CAPACITY ALERT: Lassa Isolation Bed Shortage",
                        "Admission " + admission.getAdmissionNumber() + " could not find an available isolation bed matching constraints (Infection Status: " + infectionStatus + "). Immediate clinical review or emergency ward transfer required.",
                        "ALERT",
                        "RECOMMENDATION",
                        recommendation.getId(),
                        admission.getId(),
                        admission.getCreatedBy()
                );
            } catch (Exception e) {
                log.warn("Could not dispatch capacity alert notification: {}", e.getMessage());
            }
        }

        // 5. Equipment Allocation (only if beds feasible or equipment needed)
        List<Equipment> availableEquipment = equipmentRepository.findByStatus("AVAILABLE");
        List<EquipmentScoredCandidate> scoredEquipment = new ArrayList<>();

        for (Equipment eq : availableEquipment) {
            String eqType = (eq.getEquipmentType() != null) ? eq.getEquipmentType().toUpperCase() : "";
            String eqName = (eq.getName() != null) ? eq.getName().toUpperCase() : "";
            double sMatch = 0.50;
            String reqType = "MONITOR";

            if ("CRITICAL".equalsIgnoreCase(severityLevel) || "RED".equalsIgnoreCase(triageCategory)) {
                if (eqType.contains("VENTILATOR") || eqName.contains("VENTILATOR")) {
                    sMatch = 1.0;
                    reqType = "VENTILATOR";
                } else if (eqType.contains("INFUSION") || eqName.contains("INFUSION")) {
                    sMatch = 0.90;
                    reqType = "INFUSION_PUMP";
                } else if (eqType.contains("MONITOR") || eqName.contains("MONITOR")) {
                    sMatch = 0.85;
                    reqType = "MULTIPARAMETER_MONITOR";
                }
            } else if ("SEVERE".equalsIgnoreCase(severityLevel) || "YELLOW".equalsIgnoreCase(triageCategory)) {
                if (eqType.contains("INFUSION") || eqName.contains("INFUSION")) {
                    sMatch = 1.0;
                    reqType = "INFUSION_PUMP";
                } else if (eqType.contains("MONITOR") || eqName.contains("MONITOR")) {
                    sMatch = 0.90;
                    reqType = "VITAL_SIGNS_MONITOR";
                } else if (eqType.contains("OXYGEN") || eqName.contains("OXYGEN")) {
                    sMatch = 0.85;
                    reqType = "OXYGEN_CONCENTRATOR";
                }
            } else {
                if (eqType.contains("MONITOR") || eqName.contains("MONITOR") || eqType.contains("PULSE") || eqName.contains("OXIMETER")) {
                    sMatch = 1.0;
                    reqType = "PULSE_OXIMETER";
                }
            }

            double sLoc = (eq.getLocation() != null && (eq.getLocation().contains("ISOLATION") || eq.getLocation().contains("ZONE_A"))) ? 1.0 : 0.85;
            double eqScore = Math.round(((sMatch * 0.70) + (sLoc * 0.30)) * 100.0) / 100.0;
            String eqRationale = String.format("Recommended %s (Serial: %s) in %s. Clinical Match: %d%% for %s protocol.",
                    eq.getName(), eq.getSerialNumber(), eq.getLocation() != null ? eq.getLocation() : "Ward Storage", (int)(sMatch * 100), reqType);

            scoredEquipment.add(new EquipmentScoredCandidate(eq, eqScore, sMatch, 1.0, sLoc, eqRationale));
        }

        if (!scoredEquipment.isEmpty() && !scoredBeds.isEmpty()) {
            scoredEquipment.sort((a, b) -> Double.compare(b.compositeScore, a.compositeScore));
            EquipmentScoredCandidate bestEq = scoredEquipment.get(0);
            BigDecimal eqConfidence = BigDecimal.valueOf(bestEq.compositeScore).setScale(2, RoundingMode.HALF_UP);

            Map<String, Object> eqBreakdown = new LinkedHashMap<>();
            eqBreakdown.put("typeMatch", bestEq.typeMatchScore);
            eqBreakdown.put("locationMatch", bestEq.locationScore);
            eqBreakdown.put("compositeScore", bestEq.compositeScore);
            eqBreakdown.put("equipmentName", bestEq.equipment.getName());
            eqBreakdown.put("serialNumber", bestEq.equipment.getSerialNumber());

            String eqBreakdownJson;
            try {
                eqBreakdownJson = objectMapper.writeValueAsString(eqBreakdown);
            } catch (Exception e) {
                eqBreakdownJson = "{\"compositeScore\":" + bestEq.compositeScore + "}";
            }

            RecommendationItem eqItem = RecommendationItem.builder()
                    .recommendationId(recommendation.getId())
                    .itemType("EQUIPMENT")
                    .recommendedEntityType("EQUIPMENT")
                    .recommendedEntityId(bestEq.equipment.getId())
                    .rank(1)
                    .confidenceScore(eqConfidence)
                    .scoringBreakdown(eqBreakdownJson)
                    .rationale(bestEq.rationale)
                    .status("PENDING")
                    .createdAt(Instant.now())
                    .build();

            items.add(eqItem);
        }

        if (!items.isEmpty()) {
            itemRepository.saveAll(items);
        }

        log.info("Completed allocation recommendation batch {} with status {} and {} items for admission {}",
                recommendation.getId(), recommendation.getStatus(), items.size(), admissionId);
        return toResponse(recommendation, items);
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponse> getRecommendationsByAdmission(UUID admissionId) {
        List<AllocationRecommendation> recommendations = recommendationRepository.findByAdmissionId(admissionId);
        if (recommendations.isEmpty()) {
            return List.of();
        }
        return recommendations.stream()
                .map(r -> {
                    List<RecommendationItem> items = itemRepository.findByRecommendationId(r.getId());
                    return toResponse(r, items);
                })
                .toList();
    }

    @Transactional
    public RecommendationDecisionResponse recordDecision(UUID itemId, RecommendationDecisionRequest request, UUID userId) {
        // 1. Verify RecommendationItem exists and is PENDING
        RecommendationItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("RecommendationItem", itemId.toString()));

        if (!"PENDING".equalsIgnoreCase(item.getStatus())) {
            throw new ValidationException("Item is not in PENDING status (current status: " + item.getStatus() + ")");
        }

        // 2. Verify Recommendation batch exists
        AllocationRecommendation recommendation = recommendationRepository.findById(item.getRecommendationId())
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation", item.getRecommendationId().toString()));

        // 3. Verify Admission exists and is eligible for bed assignment
        Admission admission = admissionRepository.findById(recommendation.getAdmissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Admission", recommendation.getAdmissionId().toString()));

        if ("DISCHARGED".equalsIgnoreCase(admission.getStatus())) {
            throw new ValidationException("Cannot assign resources to a discharged admission (admissionId: " + admission.getId() + ")");
        }

        String decisionType = request.decisionType() != null ? request.decisionType().toUpperCase() : "ACCEPT";

        if ("ACCEPT".equals(decisionType) || "ACCEPTED".equals(decisionType)) {
            // Revalidation with Pessimistic Concurrency Control:
            if ("BED".equalsIgnoreCase(item.getItemType())) {
                UUID bedId = item.getRecommendedEntityId();
                Bed bed = bedRepository.findByIdWithLock(bedId)
                        .orElseThrow(() -> new ResourceNotFoundException("Bed", bedId.toString()));

                // Verify bed remains AVAILABLE
                if (!"AVAILABLE".equalsIgnoreCase(bed.getStatus())) {
                    throw new ValidationException("Bed " + bed.getBedNumber() + " is no longer available. Current status: " + bed.getStatus());
                }

                // Verify bed is not already assigned to another admission
                if (bed.getCurrentAdmissionId() != null) {
                    throw new ValidationException("Bed " + bed.getBedNumber() + " is already occupied by admission " + bed.getCurrentAdmissionId());
                }

                // Verify Ward is active
                if (bed.getWardId() != null) {
                    Ward ward = wardRepository.findById(bed.getWardId()).orElse(null);
                    if (ward != null && !"ACTIVE".equalsIgnoreCase(ward.getStatus())) {
                        throw new ValidationException("Ward " + ward.getName() + " is not active");
                    }
                }

                // Atomic allocation
                admission.setBedId(bed.getId());
                admission.setUpdatedBy(userId);
                admissionRepository.save(admission);

                bed.setStatus("OCCUPIED");
                bed.setCurrentAdmissionId(admission.getId());
                bed.setUpdatedBy(userId);
                bedRepository.save(bed);

                log.info("Pessimistically locked and allocated Bed {} ({}) to Admission {}",
                        bed.getBedNumber(), bed.getId(), admission.getId());

                if (auditService != null) {
                    auditService.recordAudit(
                            userId,
                            "RECOMMENDATION_ACCEPT",
                            "BED",
                            bed.getId(),
                            "AVAILABLE",
                            "OCCUPIED",
                            "127.0.0.1",
                            "Accepted recommendation for Bed " + bed.getBedNumber()
                    );
                }
            } else if ("EQUIPMENT".equalsIgnoreCase(item.getItemType())) {
                UUID eqId = item.getRecommendedEntityId();
                Equipment eq = equipmentRepository.findByIdWithLock(eqId)
                        .orElseThrow(() -> new ResourceNotFoundException("Equipment", eqId.toString()));

                if (!"AVAILABLE".equalsIgnoreCase(eq.getStatus())) {
                    throw new ValidationException("Equipment " + eq.getName() + " is no longer available. Current status: " + eq.getStatus());
                }

                eq.setStatus("IN_USE");
                eq.setAssignedAdmissionId(admission.getId());
                eq.setUpdatedBy(userId);
                equipmentRepository.save(eq);

                log.info("Allocated Equipment {} ({}) to Admission {}", eq.getName(), eq.getId(), admission.getId());

                if (auditService != null) {
                    auditService.recordAudit(
                            userId,
                            "RECOMMENDATION_ACCEPT",
                            "EQUIPMENT",
                            eq.getId(),
                            "AVAILABLE",
                            "IN_USE",
                            "127.0.0.1",
                            "Accepted recommendation for Equipment " + eq.getName()
                    );
                }
            }

            item.setStatus("ACCEPTED");

        } else if ("REJECT".equals(decisionType) || "REJECTED".equals(decisionType)) {
            item.setStatus("REJECTED");

            if (auditService != null) {
                auditService.recordAudit(
                        userId,
                        "RECOMMENDATION_REJECT",
                        item.getItemType(),
                        item.getRecommendedEntityId(),
                        item.getStatus(),
                        "REJECTED",
                        "127.0.0.1",
                        request.overrideJustification() != null ? request.overrideJustification() : "Clinician rejected recommendation"
                );
            }

        } else {
            // OVERRIDE: Mandate clinical justification
            if (request.overrideJustification() == null || request.overrideJustification().trim().isEmpty()) {
                throw new ValidationException("Clinical justification is mandatory for recommendation overrides.");
            }

            if ("BED".equalsIgnoreCase(item.getItemType()) && request.overriddenEntityId() != null) {
                UUID overriddenBedId = request.overriddenEntityId();
                Bed overriddenBed = bedRepository.findByIdWithLock(overriddenBedId)
                        .orElseThrow(() -> new ResourceNotFoundException("Bed", overriddenBedId.toString()));

                admission.setBedId(overriddenBed.getId());
                admission.setUpdatedBy(userId);
                admissionRepository.save(admission);

                overriddenBed.setStatus("OCCUPIED");
                overriddenBed.setCurrentAdmissionId(admission.getId());
                overriddenBed.setUpdatedBy(userId);
                bedRepository.save(overriddenBed);

                log.info("Clinician manual override: Bed {} assigned to Admission {}. Justification: {}",
                        overriddenBed.getBedNumber(), admission.getId(), request.overrideJustification());

                if (auditService != null) {
                    auditService.recordAudit(
                            userId,
                            "RECOMMENDATION_OVERRIDE",
                            "BED",
                            overriddenBed.getId(),
                            "AVAILABLE",
                            "OCCUPIED",
                            "127.0.0.1",
                            request.overrideJustification()
                    );
                }
            }

            item.setStatus("OVERRIDDEN");
        }

        itemRepository.save(item);

        // Update recommendation batch status
        List<RecommendationItem> allItems = itemRepository.findByRecommendationId(recommendation.getId());
        boolean hasPending = allItems.stream().anyMatch(i -> "PENDING".equals(i.getStatus()));
        if (!hasPending) {
            recommendation.setStatus("DECIDED");
            recommendationRepository.save(recommendation);
        }

        RecommendationDecision decision = RecommendationDecision.builder()
                .recommendationItemId(itemId)
                .decisionType(decisionType)
                .overriddenEntityType(request.overriddenEntityId() != null ? item.getRecommendedEntityType() : null)
                .overriddenEntityId(request.overriddenEntityId())
                .overrideJustification(request.overrideJustification())
                .decidedBy(userId)
                .decidedAt(Instant.now())
                .build();

        decision = decisionRepository.save(decision);
        log.info("Recorded decision: itemId={}, decisionType={}, decidedBy={}", itemId, decisionType, userId);

        return new RecommendationDecisionResponse(
                decision.getId(),
                decision.getRecommendationItemId(),
                decision.getDecisionType(),
                decision.getDecidedBy(),
                decision.getDecidedAt()
        );
    }

    @Transactional
    public RecommendationDecisionResponse makeDecision(UUID itemId, RecommendationDecisionRequest request, UUID userId) {
        return recordDecision(itemId, request, userId);
    }

    private RecommendationResponse toResponse(AllocationRecommendation recommendation, List<RecommendationItem> items) {
        List<RecommendationItemResponse> itemResponses = items.stream()
                .map(item -> new RecommendationItemResponse(
                        item.getId(), item.getItemType(), item.getRecommendedEntityType(),
                        item.getRecommendedEntityId(), item.getRank(), item.getConfidenceScore(),
                        item.getRationale(), item.getStatus()
                ))
                .toList();

        return new RecommendationResponse(
                recommendation.getId(), recommendation.getAdmissionId(),
                recommendation.getBatchType(), recommendation.getStatus(),
                recommendation.getGeneratedAt(), recommendation.getExpiresAt(),
                itemResponses, recommendation.getCreatedAt()
        );
    }
}
