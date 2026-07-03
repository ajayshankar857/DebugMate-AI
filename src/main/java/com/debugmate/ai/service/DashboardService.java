package com.debugmate.ai.service;

import com.debugmate.ai.entity.User;
import com.debugmate.ai.repository.DebugSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DebugSessionRepository sessionRepo;
    private final UserService userService;
    private final AchievementService achievementService;

    public Map<String, Object> getDashboardStats(String username) {
        User user = userService.findByUsername(username);
        Long userId = user.getId();

        Map<String, Object> stats = new LinkedHashMap<>();

        // Total sessions
        stats.put("totalSessions", sessionRepo.countByUserId(userId));

        // Language distribution
        Map<String, Long> langDist = new LinkedHashMap<>();
        for (Object[] row : sessionRepo.countByLanguageForUser(userId)) {
            langDist.put(String.valueOf(row[0]), (Long) row[1]);
        }
        stats.put("languageDistribution", langDist);

        // Severity distribution
        Map<String, Long> sevDist = new LinkedHashMap<>();
        for (Object[] row : sessionRepo.countBySeverityForUser(userId)) {
            sevDist.put(String.valueOf(row[0]), (Long) row[1]);
        }
        stats.put("severityDistribution", sevDist);

        // Achievements
        stats.put("achievements", achievementService.getUserAchievements(userId));

        // Check for new achievements
        List<String> newBadges = achievementService.checkAndAwardAchievements(user);
        stats.put("newAchievements", newBadges);

        return stats;
    }
}
