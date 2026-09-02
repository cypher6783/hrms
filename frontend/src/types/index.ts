// Common API Envelope Types matching Spring Boot DTOs
export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: string;
}

export interface FieldError {
  field: string;
  message: string;
  rejectedValue?: any;
}

export interface ErrorResponse {
  success: boolean;
  message: string;
  code: string;
  fieldErrors?: FieldError[];
  timestamp: string;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

// Authentication Models
export interface User {
  userId: string;
  username: string;
  fullName: string;
  role: string;
}

export interface LoginResponse extends User {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

// Patient Models
export interface Patient {
  id: string;
  patientNumber: string;
  firstName: string;
  lastName: string;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
  dateOfBirth: string;
  contactNumber?: string;
  emergencyContact?: string;
  infectionStatus: 'CONFIRMED' | 'SUSPECTED' | 'NEGATIVE' | 'CLEARED';
  isolationRequired: boolean;
  careLevel: 'ICU' | 'HIGH_DEPENDENCY' | 'GENERAL' | 'ISOLATION';
  status: 'ACTIVE' | 'DISCHARGED' | 'DECEASED' | 'TRANSFERRED';
  createdAt: string;
  updatedAt: string;
}

export interface PatientRequest {
  firstName: string;
  lastName: string;
  gender: string;
  dateOfBirth: string;
  contactNumber?: string;
  emergencyContact?: string;
  infectionStatus: string;
  isolationRequired: boolean;
  careLevel: string;
}

// Admission Models
export interface Admission {
  id: string;
  admissionNumber: string;
  patientId: string;
  patientName?: string;
  wardId?: string;
  wardName?: string;
  bedId?: string;
  bedNumber?: string;
  status: 'PENDING_ALLOCATION' | 'ALLOCATED' | 'ADMITTED' | 'TRANSFERRED' | 'DISCHARGED';
  admittedAt?: string;
  dischargedAt?: string;
  attendingDoctor?: string;
  admittingDiagnosis?: string;
  priority: 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW';
  createdAt: string;
}

export interface AdmissionSummary {
  id: string;
  patientNumber: string;
  patientName: string;
  wardName?: string;
  bedNumber?: string;
  status: string;
  priority: string;
  admittedAt?: string;
}

export interface AdmissionStats {
  totalAdmissions: number;
  pendingAllocations: number;
  activeAdmissions: number;
  todayDischarges: number;
  icuOccupancyRate: number;
  isolationOccupancyRate: number;
}

// Ward & Bed Models
export interface Ward {
  id: string;
  name: string;
  code: string;
  wardType: 'ICU' | 'ISOLATION' | 'GENERAL' | 'PEDIATRIC' | 'MATERNITY';
  isolationLevel: 'HIGH' | 'MEDIUM' | 'STANDARD';
  equipmentZone: string;
  maxBedCapacity: number;
  status: 'ACTIVE' | 'MAINTENANCE' | 'CLOSED';
}

export interface WardStatus {
  wardId: string;
  wardName: string;
  totalBeds: number;
  occupiedBeds: number;
  availableBeds: number;
  cleaningBeds: number;
  maintenanceBeds: number;
  occupancyPercentage: number;
}

export interface Bed {
  id: string;
  bedNumber: string;
  wardId: string;
  wardName: string;
  bedType: 'STANDARD' | 'ICU' | 'ISOLATION_NEGATIVE_PRESSURE' | 'HIGH_DEPENDENCY';
  isIsolationCapable: boolean;
  status: 'AVAILABLE' | 'OCCUPIED' | 'CLEANING_REQUIRED' | 'UNDER_CLEANING' | 'MAINTENANCE' | 'UNAVAILABLE';
  currentPatientId?: string;
  currentPatientName?: string;
}

export interface BedAvailability {
  wardId: string;
  totalBeds: number;
  availableBeds: number;
  isolationBedsAvailable: number;
  icuBedsAvailable: number;
}

// Resource & Inventory Models
export interface Resource {
  id: string;
  name: string;
  category: 'MEDICATION' | 'PPE' | 'OXYGEN' | 'CONSUMABLE' | 'TEST_KIT';
  unitOfMeasure: string;
  totalQuantity: number;
  availableQuantity: number;
  reservedQuantity: number;
  allocatedQuantity: number;
  reorderLevel: number;
  status: 'AVAILABLE' | 'LOW_STOCK' | 'CRITICAL_SHORTAGE' | 'OUT_OF_STOCK';
}

export interface InventoryTransaction {
  id: string;
  resourceId: string;
  resourceName: string;
  transactionType: 'INFLOW' | 'OUTFLOW' | 'ADJUSTMENT' | 'ALLOCATION';
  quantity: number;
  reason?: string;
  performedBy: string;
  createdAt: string;
}

// Equipment & Maintenance Models
export interface Equipment {
  id: string;
  serialNumber: string;
  name: string;
  category: 'VENTILATOR' | 'PATIENT_MONITOR' | 'INFUSION_PUMP' | 'DEFIBRILLATOR' | 'DIALYSIS';
  status: 'AVAILABLE' | 'IN_USE' | 'RESERVED' | 'UNDER_MAINTENANCE' | 'DECOMMISSIONED';
  locationWardId?: string;
  locationWardName?: string;
  lastMaintenanceDate?: string;
  nextMaintenanceDate?: string;
}

export interface MaintenanceRecord {
  id: string;
  equipmentId: string;
  equipmentName: string;
  maintenanceType: 'PREVENTIVE' | 'CORRECTIVE' | 'INSPECTION';
  status: 'SCHEDULED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  description: string;
  technicianName?: string;
  startDate: string;
  expectedCompletionDate?: string;
}

// Staff & Shift Models
export interface Staff {
  id: string;
  staffNumber: string;
  fullName: string;
  role: 'DOCTOR' | 'NURSE' | 'CLEANER' | 'ADMIN' | 'LAB_TECH';
  unit: string;
  contactNumber: string;
  status: 'ACTIVE' | 'ON_LEAVE' | 'INACTIVE';
}

export interface Shift {
  id: string;
  shiftName: 'MORNING' | 'AFTERNOON' | 'NIGHT';
  startTime: string;
  endTime: string;
  date: string;
  requiredStaffCount: number;
  assignedStaffCount: number;
  status: 'OPEN' | 'FULLY_STAFFED' | 'UNDERSTAFFED';
}

// Recommendation Engine Models
export interface RecommendationItem {
  id: string;
  itemType: 'BED' | 'EQUIPMENT' | 'RESOURCE' | 'STAFF';
  recommendedEntityType: string;
  recommendedEntityId: string;
  recommendedEntityName?: string;
  rank: number;
  confidenceScore: number; // 0.00 - 1.00 score
  rationale: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'OVERRIDDEN';
}

export interface FeasibilityCheck {
  criterion: string;
  passed: boolean;
  type: 'MANDATORY' | 'PREFERENCE';
  details?: string;
}

export interface Recommendation {
  id: string;
  admissionId: string;
  patientId?: string;
  patientName?: string;
  batchType: string;
  status: 'ACTIVE' | 'EXPIRED' | 'COMPLETED' | 'NO_FEASIBLE_ALLOCATION' | 'INSUFFICIENT_DATA';
  generatedAt: string;
  expiresAt: string;
  items: RecommendationItem[];
  feasibilityChecks?: FeasibilityCheck[];
  missingDataReason?: string;
  createdAt: string;
}

export interface RecommendationDecisionRequest {
  decisionType: 'ACCEPT' | 'REJECT' | 'OVERRIDE';
  overriddenEntityId?: string;
  overrideJustification?: string;
}

export interface RecommendationDecisionResponse {
  id: string;
  recommendationItemId: string;
  decisionType: string;
  decidedBy: string;
  decidedAt: string;
  status: string;
}

// Audit Log Model
export interface AuditLog {
  id: string;
  userId: string;
  username: string;
  action: string;
  entityType: string;
  entityId: string;
  previousState?: string;
  newState?: string;
  justification?: string;
  ipAddress?: string;
  timestamp: string;
}
