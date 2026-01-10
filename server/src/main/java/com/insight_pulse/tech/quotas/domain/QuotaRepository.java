package com.insight_pulse.tech.quotas.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

public interface QuotaRepository extends JpaRepository<Quota, Integer>{
    Optional<Quota> findByUserId(int userId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Quota q SET q.usedCount = q.usedCount + 1 WHERE q.id = :userId")
    int incrementUsedCount(@Param("userId") int userId);
}
