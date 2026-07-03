package com.debugmate.ai.controller;

import com.debugmate.ai.entity.Achievement;
import com.debugmate.ai.service.AchievementService;
import com.debugmate.ai.entity.User;
import com.debugmate.ai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AchievementController {

    private final AchievementService achievementService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Achievement>> getAchievements() {
        String username = getCurrentUsername();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(achievementService.getUserAchievements(user.getId()));
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
