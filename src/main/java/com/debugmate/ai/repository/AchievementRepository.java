package com.debugmate.ai.repository;

import com.debugmate.ai.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    List<Achievement> findByUserIdOrderByEarnedAtDesc(Long userId);
    boolean existsByUserIdAndName(Long userId, String name);
}
