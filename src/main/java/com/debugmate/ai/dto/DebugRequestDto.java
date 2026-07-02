package com.debugmate.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebugRequestDto {

    @NotBlank(message = "Code cannot be empty")
    private String code;

    @NotBlank(message = "Language is required")
    private String language;

    private String learningMode; // BEGINNER, INTERMEDIATE, EXPERT
    private String sessionType;  // DEBUG, STACKTRACE, SQL, EXPLAIN
}
