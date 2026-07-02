package com.debugmate.ai.service;

import com.debugmate.ai.dto.DebugRequestDto;
import com.debugmate.ai.dto.DebugResponseDto;
import com.debugmate.ai.entity.DebugSession;
import com.debugmate.ai.entity.User;
import com.debugmate.ai.repository.DebugSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DebugService {

    private final GeminiService geminiService;
    private final DebugSessionRepository sessionRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Transactional
    public DebugResponseDto analyzeCode(DebugRequestDto request, String username) {
        User user = userService.findByUsername(username);

        // Call AI service
        DebugResponseDto response = geminiService.analyzeCode(
            request.getCode(),
            request.getLanguage(),
            request.getLearningMode() != null ? request.getLearningMode() : "INTERMEDIATE",
            request.getSessionType() != null ? request.getSessionType() : "DEBUG"
        );

        // Persist the session
        DebugSession.Severity severityEnum;
        try {
            severityEnum = DebugSession.Severity.valueOf(
                response.getSeverity() != null ? response.getSeverity().toUpperCase() : "MEDIUM"
            );
        } catch (IllegalArgumentException e) {
            severityEnum = DebugSession.Severity.MEDIUM;
        }

        String aiResponseJson;
        try {
            aiResponseJson = objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            aiResponseJson = response.getExplanation();
        }

        DebugSession session = DebugSession.builder()
            .user(user)
            .programmingLanguage(request.getLanguage())
            .inputCode(request.getCode())
            .aiResponse(aiResponseJson)
            .errorCount(response.getDetectedErrors() != null ? response.getDetectedErrors().size() : 0)
            .confidenceScore(response.getConfidenceScore())
            .severity(severityEnum)
            .sessionType(request.getSessionType() != null ? request.getSessionType() : "DEBUG")
            .build();

        DebugSession saved = sessionRepository.save(session);
        response.setSessionId(saved.getId());

        // Award XP for each analysis
        int xpGained = 10 + (response.getDetectedErrors() != null ? response.getDetectedErrors().size() * 5 : 0);
        userService.updateProfileStats(user.getId(), xpGained, !response.getDetectedErrors().isEmpty());

        return response;
    }

    public List<DebugSession> getHistory(String username) {
        User user = userService.findByUsername(username);
        return sessionRepository.findTop10ByUserIdOrderByCreatedAtDesc(user.getId());
    }
}
