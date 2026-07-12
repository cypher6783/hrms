package com.hospital.resource.auth.repository;

import com.hospital.resource.auth.entity.PasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, UUID> {

    List<PasswordHistory> findTop5ByUserIdOrderByCreatedAtDesc(UUID userId);

    @Modifying
    @Query("DELETE FROM PasswordHistory ph WHERE ph.user.id = :userId AND ph.id NOT IN (" +
           "SELECT p.id FROM PasswordHistory p WHERE p.user.id = :userId ORDER BY p.createdAt DESC LIMIT 5)")
    void deleteOldestIfExceedsFive(UUID userId);
}
