package com.debugmate.ai.repository;

import com.debugmate.ai.entity.DebugSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebugSessionRepository extends JpaRepository<DebugSession, Long> {

    List<DebugSession> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<DebugSession> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserId(Long userId);

    @Query("SELECT d.programmingLanguage, COUNT(d) FROM DebugSession d WHERE d.user.id = :userId GROUP BY d.programmingLanguage")
    List<Object[]> countByLanguageForUser(@Param("userId") Long userId);

    @Query("SELECT d.severity, COUNT(d) FROM DebugSession d WHERE d.user.id = :userId GROUP BY d.severity")
    List<Object[]> countBySeverityForUser(@Param("userId") Long userId);
}
