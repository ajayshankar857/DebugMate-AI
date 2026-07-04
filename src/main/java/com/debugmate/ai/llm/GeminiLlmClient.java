package com.debugmate.ai.llm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class GeminiLlmClient {

    @Value("${app.groq.apiKey:}")
    private String apiKey;

    @Value("${app.groq.model:llama-3.3-70b-versatile}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String generate(String prompt) {
        if (!isConfigured()) {
            throw new IllegalStateException("LLM is not configured (missing API key).");
        }

        Map<String, Object> requestBody = Map.of(
            "model", model,
            "messages", List.of(Map.of(
                "role", "user",
                "content", prompt
            ))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        log.info("Executing LLM Request to Groq [{}]", model);
        ResponseEntity<String> response = restTemplate.postForEntity(GROQ_URL, request, String.class);
        return response.getBody();
    }
}
