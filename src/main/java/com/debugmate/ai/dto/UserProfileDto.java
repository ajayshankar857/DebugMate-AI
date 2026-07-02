package com.debugmate.ai.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserProfileDto {
    private Long userId;
    private String username;
    private String email;
    private Integer xpPoints;
    private Integer dailyStreak;
    private Integer totalErrorsSolved;
    private Integer accuracyScore;
}
