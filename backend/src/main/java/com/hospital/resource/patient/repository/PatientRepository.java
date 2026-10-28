package com.hospital.resource.patient.repository;

import com.hospital.resource.patient.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findByPatientNumber(String patientNumber);

    boolean existsByPatientNumber(String patientNumber);

    @Query("SELECT p FROM Patient p WHERE p.isActive = true AND " +
           "(LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.patientNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "p.phoneNumber LIKE CONCAT('%', :search, '%'))")
    Page<Patient> searchPatients(@Param("search") String search, Pageable pageable);

    long countByIsActiveTrue();
}
