package com.debugmate.ai.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bug_knowledge_base")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BugKnowledge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String language;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 100)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "example_code", columnDefinition = "TEXT")
    private String exampleCode;

    @Column(columnDefinition = "TEXT")
    private String solution;

    @Column(name = "best_practice", columnDefinition = "TEXT")
    private String bestPractice;

    @Column(length = 20)
    private String severity;
}
