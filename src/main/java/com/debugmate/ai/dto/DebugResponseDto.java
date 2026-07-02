package com.debugmate.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebugResponseDto {

    private Long sessionId;
    private String programmingLanguage;
    private String severity;
    private Integer confidenceScore;
    private String codeQualityScore;      // e.g. "72/100"
    private List<DetectedError> detectedErrors;
    private String correctedCode;
    private String explanation;
    private String rootCause;
    private List<String> bestPractices;
    private List<String> relatedConcepts;
    private String interviewQuestion;
    private List<String> references;
    private String learningTip;
    private LocalDateTime timestamp;

    @Getter @Setter @Builder
    public static class DetectedError {
        private String type;
        private Integer lineNumber;
        private String description;
        private String fix;
        private String severity;
    }
}
