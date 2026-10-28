package com.hospital.resource.resource.repository;

import com.hospital.resource.resource.entity.ResourceSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResourceSupplierRepository extends JpaRepository<ResourceSupplier, UUID> {

    List<ResourceSupplier> findByIsActiveTrue();
}
