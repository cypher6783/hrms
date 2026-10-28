# 08 — Repository Design

## 1. Repository Strategy

- All repositories extend `JpaRepository<Entity, UUID>`.
- Custom queries use `@Query` annotations with JPQL.
- Pagination via `Pageable` parameter.
- Soft-deleted records filtered by default via `@Where(clause = "is_active = true")` or query methods.
- Append-only entities have repositories with only `save()` and query methods (no `delete()`).

---

## 2. UserRepository

**Extends**: `JpaRepository<User, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByUsername(String) | Derived | Login lookup |
| findByEmail(String) | Derived | Email lookup |
| existsByUsername(String) | Derived | Duplicate check |
| existsByEmail(String) | Derived | Duplicate check |
| findByStatus(String, Pageable) | Derived | Active user listing |
| incrementFailedLoginAttempts(UUID) | `@Modifying @Query UPDATE users SET failed_login_attempts = failed_login_attempts + 1 WHERE id = ?1` | Failed login tracking |
| resetFailedLoginAttempts(UUID) | `@Modifying @Query UPDATE users SET failed_login_attempts = 0, locked_until = NULL WHERE id = ?1` | Successful login |
| lockAccount(UUID, LocalDateTime) | `@Modifying @Query UPDATE users SET locked_until = ?2 WHERE id = ?1` | Account lockout |
| findByRole(String, Pageable) | Derived | Role-based listing |

---

## 3. RefreshTokenRepository

**Extends**: `JpaRepository<RefreshToken, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByTokenHash(String) | Derived | Token validation |
| findByUserIdAndRevokedFalse(UUID) | Derived | Active token lookup |
| revokeAllByUserId(UUID) | `@Modifying @Query UPDATE refresh_tokens SET revoked = true WHERE user_id = ?1 AND revoked = false` | Token rotation |
| deleteByExpiresAtBefore(LocalDateTime) | `@Modifying @Query DELETE FROM refresh_tokens WHERE expires_at < ?1` | Cleanup |

---

## 4. PasswordHistoryRepository

**Extends**: `JpaRepository<PasswordHistory, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findTop5ByUserIdOrderByCreatedAtDesc(UUID) | Derived | Last 5 passwords |
| countByUserId(UUID) | Derived | History count |
| deleteByUserIdAndIdNotIn(UUID, List) | `@Modifying @Query DELETE FROM password_history WHERE user_id = ?1 AND id NOT IN ?2` | Trim to 5 |

---

## 5. LoginAuditLogRepository

**Extends**: `JpaRepository<LoginAuditLog, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| countByUserIdAndSuccessFalseAndCreatedAtAfter(UUID, LocalDateTime) | Derived | Recent failures |
| findByUsernameAttemptedAndCreatedAtBetween(String, LocalDateTime, LocalDateTime, Pageable) | Derived | Brute-force detection |
| deleteByCreatedAtBefore(LocalDateTime) | `@Modifying @Query DELETE FROM login_audit_logs WHERE created_at < ?1` | Retention cleanup |

---

## 6. PatientRepository

**Extends**: `JpaRepository<Patient, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByPatientNumber(String) | Derived | Exact lookup |
| findByIsActiveTrue(Pageable) | Derived | Active patients |
| findByFullNameContainingIgnoreCase(String, Pageable) | Derived | Name search |
| findByPhoneNumber(String) | Derived | Phone search |
| countByIsActiveTrue() | Derived | Active patient count |
| existsByPatientNumber(String) | Derived | Duplicate check |

---

## 7. ClinicalAssessmentRepository

**Extends**: `JpaRepository<ClinicalAssessment, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByPatientIdOrderByAssessmentTimestampDesc(UUID, Pageable) | Derived | Patient timeline |
| findTop1ByAdmissionIdOrderByAssessmentTimestampDesc(UUID) | Derived | Latest assessment |
| findByAdmissionIdOrderByAssessmentTimestampDesc(UUID) | Derived | Admission assessments |
| findByPatientIdAndAssessmentTimestampBetween(UUID, LocalDateTime, LocalDateTime) | Derived | Date range query |
| existsByAdmissionIdAndCreatedAtAfter(UUID, LocalDateTime) | Derived | Reassessment check |

---

## 8. AdmissionRepository

**Extends**: `JpaRepository<Admission, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByAdmissionNumber(String) | Derived | Exact lookup |
| findByPatientIdAndIsActiveTrue(UUID) | Derived | Active admission check |
| findByIsActiveTrue(Pageable) | Derived | Active admissions |
| findByStatus(String, Pageable) | Derived | Status filter |
| findByWardIdAndIsActiveTrue(UUID) | Derived | Ward occupancy |
| countByWardIdAndIsActiveTrue(UUID) | Derived | Ward bed count |
| findByStatusAndAdmittedAtBefore(String, LocalDateTime) | Derived | Stale admissions |
| existsByPatientIdAndIsActiveTrue(UUID) | Derived | Active admission check |

---

## 9. BedRepository

**Extends**: `JpaRepository<Bed, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByWardIdAndStatus(UUID, String, Pageable) | Derived | Available beds per ward |
| findByStatus(String, Pageable) | Derived | Status filter |
| findByBedNumberAndWardId(String, UUID) | Derived | Unique lookup |
| countByWardIdAndStatus(UUID, String) | Derived | Status count |
| findByWardIdAndBedTypeAndStatus(UUID, String, String) | Derived | Typed bed lookup |
| findByIsIsolationCapableTrueAndStatus(String) | Derived | Isolation beds |
| findByCurrentAdmissionId(UUID) | Derived | Bed by admission |

---

## 10. BedCleaningRepository

**Extends**: `JpaRepository<BedCleaning, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByStatus(String, Pageable) | Derived | Status filter |
| findByBedIdAndStatusNot(UUID, String) | Derived | Active cleaning for bed |
| findByAssignedToAndStatus(UUID, String) | Derived | Cleaner's tasks |
| findByWardIdAndStatus(UUID, String) | Derived | Ward cleaning tasks |

**Note**: Ward filtering requires a `@Query` joining with beds table:

```sql
SELECT bc FROM BedCleaning bc JOIN bc.bed b WHERE b.ward.id = ?1 AND bc.status = ?2
```

---

## 11. WardRepository

**Extends**: `JpaRepository<Ward, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByName(String) | Derived | Name lookup |
| findByStatus(String, Pageable) | Derived | Active wards |
| existsByName(String) | Derived | Duplicate check |

---

## 12. StaffRepository

**Extends**: `JpaRepository<Staff, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByStaffNumber(String) | Derived | Exact lookup |
| findByWardIdAndAvailabilityStatus(UUID, String, Pageable) | Derived | Available staff in ward |
| findByAvailabilityStatus(String, Pageable) | Derived | Status filter |
| findByRole(String, Pageable) | Derived | Role filter |
| countByWardIdAndAvailabilityStatus(UUID, String) | Derived | Ward staff count |
| findByWardId(UUID, Pageable) | Derived | Ward staff listing |
| existsByStaffNumber(String) | Derived | Duplicate check |

---

## 13. StaffShiftRepository

**Extends**: `JpaRepository<StaffShift, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByWardIdAndShiftDate(UUID, LocalDate) | Derived | Ward shifts for date |
| findByShiftDateBetween(LocalDate, LocalDate) | Derived | Date range |
| findByWardIdAndShiftDateBetween(UUID, LocalDate, LocalDate) | Derived | Ward date range |
| findByStatus(String, Pageable) | Derived | Status filter |

---

## 14. ShiftAssignmentRepository

**Extends**: `JpaRepository<ShiftAssignment, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByStaffIdAndShiftId(UUID, UUID) | Derived | Check existing |
| findByStaffIdAndShiftShiftDateBetween(UUID, LocalDate, LocalDate) | Derived | Overlap check |
| findByShiftId(UUID) | Derived | Shift assignments |
| countByShiftId(UUID) | Derived | Assignment count |

---

## 15. EquipmentRepository

**Extends**: `JpaRepository<Equipment, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findBySerialNumber(String) | Derived | Serial lookup |
| findByStatus(String, Pageable) | Derived | Status filter |
| findByEquipmentTypeAndStatus(String, String) | Derived | Type + status |
| findByAssignedAdmissionId(UUID) | Derived | Equipment by admission |
| findByAssignedWardId(UUID) | Derived | Ward equipment |
| existsBySerialNumber(String) | Derived | Duplicate check |

---

## 16. EquipmentMaintenanceRepository

**Extends**: `JpaRepository<EquipmentMaintenance, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByEquipmentId(UUID, Pageable) | Derived | Equipment history |
| findByEquipmentIdAndStatus(UUID, String) | Derived | Active maintenance |
| findByScheduledDateBeforeAndStatus(LocalDate, String) | Derived | Overdue maintenance |
| findByStatus(String, Pageable) | Derived | Status filter |

---

## 17. ResourceRepository

**Extends**: `JpaRepository<Resource, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByCategory(String, Pageable) | Derived | Category filter |
| findByCriticalityLevel(String, Pageable) | Derived | Criticality filter |
| findByNameContainingIgnoreCase(String) | Derived | Name search |

---

## 18. ResourceInventoryRepository

**Extends**: `JpaRepository<ResourceInventory, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByResourceId(UUID) | Derived | Resource inventory |
| findByResourceIdAndLocation(UUID, String) | Derived | Location lookup |
| findByResourceIdAndBatchNumber(UUID, String) | Derived | Batch lookup |
| findByCurrentStockLessThan(Integer) | Derived | Low stock |
| findByExpirationDateBefore(LocalDate) | Derived | Expiring items |
| findByResourceIdAndLocationAndExpirationDateBefore(UUID, String, LocalDate) | Derived | FEFO query |

---

## 19. InventoryTransactionRepository

**Extends**: `JpaRepository<InventoryTransaction, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByResourceInventoryIdOrderByTransactionTimestampDesc(UUID, Pageable) | Derived | Inventory history |
| findByAdmissionId(UUID) | Derived | Admission transactions |
| findByTransactionTypeAndTransactionTimestampBetween(String, LocalDateTime, LocalDateTime) | Derived | Type + date range |
| sumQuantityByResourceInventoryId(UUID) | `@Query SELECT COALESCE(SUM(t.quantity), 0) FROM InventoryTransaction t WHERE t.resourceInventory.id = ?1` | Stock calculation |

**Note**: Repository has no `delete()` method (append-only).

---

## 20. ResourceSupplierRepository

**Extends**: `JpaRepository<ResourceSupplier, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByIsActiveTrue() | Derived | Active suppliers |
| findByNameContainingIgnoreCase(String) | Derived | Name search |

---

## 21. EquipmentAllocationRepository

**Extends**: `JpaRepository<EquipmentAllocation, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByEquipmentIdAndReleasedAtIsNull(UUID) | Derived | Current assignment |
| findByAdmissionId(UUID) | Derived | Admission equipment |
| findByReleasedAtIsNull(Pageable) | Derived | Active allocations |

---

## 22. ResourceAllocationRepository

**Extends**: `JpaRepository<ResourceAllocation, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByAdmissionId(UUID) | Derived | Admission resources |
| findByResourceId(UUID, Pageable) | Derived | Resource usage |
| findByAllocatedAtBetween(LocalDateTime, LocalDateTime) | Derived | Date range |

---

## 23. StaffAdmissionRepository

**Extends**: `JpaRepository<StaffAdmission, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByStaffIdAndReleasedAtIsNull(UUID) | Derived | Current assignments |
| findByAdmissionIdAndReleasedAtIsNull(UUID) | Derived | Admission staff |
| existsByStaffIdAndAdmissionIdAndReleasedAtIsNull(UUID, UUID) | Derived | Active assignment check |

---

## 24. AllocationRecommendationRepository

**Extends**: `JpaRepository<AllocationRecommendation, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByAdmissionIdAndStatus(UUID, String) | Derived | Pending for admission |
| findByStatusAndExpiresAtBefore(String, LocalDateTime) | Derived | Expired recommendations |
| findByAdmissionIdOrderByGeneratedAtDesc(UUID, Pageable) | Derived | Admission history |

---

## 25. RecommendationItemRepository

**Extends**: `JpaRepository<RecommendationItem, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByRecommendationIdAndItemTypeOrderByRank(UUID, String) | Derived | Items by type |
| findByRecommendationIdOrderByRank(UUID) | Derived | All items ranked |
| countByStatus(String) | Derived | Status count |
| countByItemTypeAndStatus(String, String) | Derived | Type + status count |

---

## 26. RecommendationDecisionRepository

**Extends**: `JpaRepository<RecommendationDecision, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByRecommendationItemId(UUID) | Derived | Decision by item |
| countByDecisionType(String) | Derived | Type count |
| countByDecisionTypeAndDecidedAtBetween(String, LocalDateTime, LocalDateTime) | Derived | Period count |

---

## 27. AuditLogRepository

**Extends**: `JpaRepository<AuditLog, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByEntityTypeAndEntityId(String, UUID, Pageable) | Derived | Entity audit trail |
| findByUserIdAndTimestampBetween(UUID, LocalDateTime, LocalDateTime, Pageable) | Derived | User activity |
| findByActionTypeAndTimestampBetween(String, LocalDateTime, LocalDateTime, Pageable) | Derived | Action type filter |
| findByTimestampBetween(LocalDateTime, LocalDateTime, Pageable) | Derived | Date range |

**Note**: Repository has no `save()` override or `delete()` method (append-only).

---

## 28. NotificationRepository

**Extends**: `JpaRepository<Notification, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByRecipientUserIdAndIsFalse(UUID, Pageable) | Derived | Unread notifications |
| countByRecipientUserIdAndIsFalse(UUID) | Derived | Unread count |
| findByRecipientUserIdOrderByCreatedAtDesc(UUID, Pageable) | Derived | User notifications |
| markAllAsReadByRecipientUserId(UUID) | `@Modifying @Query UPDATE notifications SET is_read = true, read_at = NOW() WHERE recipient_user_id = ?1 AND is_read = false` | Mark all read |

---

## 29. ForecastSnapshotRepository

**Extends**: `JpaRepository<ForecastSnapshot, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByForecastTypeOrderByGeneratedAtDesc(String, Pageable) | Derived | Type history |
| findTop1ByForecastTypeAndForecastHorizonOrderByGeneratedAtDesc(String, String) | Derived | Latest forecast |
| findByTargetPeriodStartBetweenAndTargetPeriodEndBetween(LocalDate, LocalDate, LocalDate, LocalDate) | Derived | Overlapping forecasts |

---

## 30. SystemConfigurationRepository

**Extends**: `JpaRepository<SystemConfiguration, UUID>`

| Method | Query | Purpose |
|--------|-------|---------|
| findByConfigKey(String) | Derived | Key lookup |
| findByCategory(String) | Derived | Category filter |
| findByRequiresRestartTrue() | Derived | Restart configs |

---

## 31. Caching Strategy

| Repository | Cacheable | Cache Key | TTL |
|-----------|-----------|-----------|-----|
| WardRepository | Yes | ward:id, wards:all | 10 min |
| BedRepository | Yes | bed:id, beds:ward:{id} | 5 min |
| StaffRepository | Yes | staff:id, staff:ward:{id} | 5 min |
| ResourceRepository | Yes | resource:id, resources:all | 10 min |
| SystemConfigurationRepository | Yes | config:{key} | 15 min |

Cache evicted on updates.

---

## 32. Document References

| Document | Reference |
|----------|-----------|
| Database Design | `docs/design/02-database-design.md` |
| Entity Design | `docs/design/06-entity-design.md` |
| Package Structure | `docs/design/04-package-structure.md` |
