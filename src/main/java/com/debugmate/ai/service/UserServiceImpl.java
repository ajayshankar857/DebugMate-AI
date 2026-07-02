package com.debugmate.ai.service;

import com.debugmate.ai.dto.UserProfileDto;
import com.debugmate.ai.entity.User;
import com.debugmate.ai.entity.UserProfile;
import com.debugmate.ai.exception.ResourceNotFoundException;
import com.debugmate.ai.repository.UserProfileRepository;
import com.debugmate.ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(String username) {
        User user = findByUsername(username);
        UserProfile profile = user.getProfile();
        if (profile == null) {
            throw new ResourceNotFoundException("Profile not found for user: " + username);
        }
        return UserProfileDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .xpPoints(profile.getXpPoints())
                .dailyStreak(profile.getDailyStreak())
                .totalErrorsSolved(profile.getTotalErrorsSolved())
                .accuracyScore(profile.getAccuracyScore())
                .build();
    }

    @Override
    @Transactional
    public void updateProfileStats(Long userId, int xpGained, boolean errorSolved) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user id: " + userId));
        profile.setXpPoints(profile.getXpPoints() + xpGained);
        if (errorSolved) {
            profile.setTotalErrorsSolved(profile.getTotalErrorsSolved() + 1);
        }
        userProfileRepository.save(profile);
    }
}
