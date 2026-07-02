package com.debugmate.ai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "debug_sessions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DebugSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "programming_language", length = 50)
    private String programmingLanguage;

    @Column(name = "input_code", columnDefinition = "TEXT")
    private String inputCode;

    @Column(name = "ai_response", columnDefinition = "LONGTEXT")
    private String aiResponse;

    @Column(name = "error_count")
    private Integer errorCount;

    @Column(name = "confidence_score")
    private Integer confidenceScore;

    @Column(name = "severity")
    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Column(name = "session_type", length = 30)
    private String sessionType; // DEBUG, STACKTRACE, SQL, EXPLAIN

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum Severity {
        CRITICAL, HIGH, MEDIUM, LOW, SUGGESTION
    }
}
