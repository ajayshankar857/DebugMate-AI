package com.debugmate.ai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "xp_points")
    @Builder.Default
    private Integer xpPoints = 0;

    @Column(name = "daily_streak")
    @Builder.Default
    private Integer dailyStreak = 0;

    @Column(name = "last_login_date")
    private LocalDate lastLoginDate;

    @Column(name = "total_errors_solved")
    @Builder.Default
    private Integer totalErrorsSolved = 0;

    @Column(name = "accuracy_score")
    @Builder.Default
    private Integer accuracyScore = 100; // start with 100%
}
