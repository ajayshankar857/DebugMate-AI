package com.debugmate.ai.service;

import com.debugmate.ai.dto.UserProfileDto;
import com.debugmate.ai.entity.User;

public interface UserService {
    User findByUsername(String username);
    UserProfileDto getUserProfile(String username);
    void updateProfileStats(Long userId, int xpGained, boolean errorSolved);
}
