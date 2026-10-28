package com.hospital.resource.ward.repository;

import com.hospital.resource.ward.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WardRepository extends JpaRepository<Ward, UUID> {

    List<Ward> findByStatus(String status);

    List<Ward> findByIsActiveTrue();

    Optional<Ward> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT w FROM Ward w WHERE LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(w.wardType) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Ward> searchWards(@Param("search") String search);
}
