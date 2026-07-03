package com.debugmate.ai.service;

import com.debugmate.ai.entity.Achievement;
import com.debugmate.ai.entity.User;
import com.debugmate.ai.entity.UserProfile;
import com.debugmate.ai.repository.AchievementRepository;
import com.debugmate.ai.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AchievementService {

    private final AchievementRepository achievementRepo;
    private final UserProfileRepository profileRepo;

    private static final Map<String, Map<String, String>> ACHIEVEMENT_DEFS = Map.of(
        "First Blood",    Map.of("desc", "Solved your first bug!", "icon", "bi-star-fill"),
        "Bug Hunter",     Map.of("desc", "Solved 10 bugs", "icon", "bi-bug-fill"),
        "Code Warrior",   Map.of("desc", "Solved 25 bugs", "icon", "bi-shield-fill-check"),
        "Debug Master",   Map.of("desc", "Solved 50 bugs", "icon", "bi-trophy-fill"),
        "Streak Starter", Map.of("desc", "Maintained a 3-day streak", "icon", "bi-fire"),
        "On Fire",        Map.of("desc", "Maintained a 7-day streak", "icon", "bi-lightning-charge-fill"),
        "XP Collector",   Map.of("desc", "Earned 100 XP", "icon", "bi-gem"),
        "XP Legend",      Map.of("desc", "Earned 500 XP", "icon", "bi-award-fill")
    );

    @Transactional
    public List<String> checkAndAwardAchievements(User user) {
        UserProfile profile = profileRepo.findByUserId(user.getId()).orElse(null);
        if (profile == null) return List.of();

        List<String> newlyEarned = new ArrayList<>();
        int solved = profile.getTotalErrorsSolved();
        int streak = profile.getDailyStreak();
        int xp = profile.getXpPoints();

        if (solved >= 1)  tryAward(user, "First Blood", newlyEarned);
        if (solved >= 10) tryAward(user, "Bug Hunter", newlyEarned);
        if (solved >= 25) tryAward(user, "Code Warrior", newlyEarned);
        if (solved >= 50) tryAward(user, "Debug Master", newlyEarned);
        if (streak >= 3)  tryAward(user, "Streak Starter", newlyEarned);
        if (streak >= 7)  tryAward(user, "On Fire", newlyEarned);
        if (xp >= 100)    tryAward(user, "XP Collector", newlyEarned);
        if (xp >= 500)    tryAward(user, "XP Legend", newlyEarned);

        return newlyEarned;
    }

    private void tryAward(User user, String name, List<String> list) {
        if (!achievementRepo.existsByUserIdAndName(user.getId(), name)) {
            Map<String, String> def = ACHIEVEMENT_DEFS.get(name);
            Achievement a = Achievement.builder()
                    .user(user)
                    .name(name)
                    .description(def.get("desc"))
                    .iconClass(def.get("icon"))
                    .build();
            achievementRepo.save(a);
            list.add(name);
            log.info("🏆 Achievement unlocked for {}: {}", user.getUsername(), name);
        }
    }

    public List<Achievement> getUserAchievements(Long userId) {
        return achievementRepo.findByUserIdOrderByEarnedAtDesc(userId);
    }
}
