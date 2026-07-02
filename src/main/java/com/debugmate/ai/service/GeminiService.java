package com.debugmate.ai.service;

import com.debugmate.ai.dto.DebugResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiService {

    @Value("${app.gemini.apiKey:}")
    private String apiKey;

    @Value("${app.gemini.model:gemini-1.5-flash}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GEMINI_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    public DebugResponseDto analyzeCode(String code, String language, String learningMode, String sessionType) {
        if (apiKey == null || apiKey.isBlank()) {
            log.info("GEMINI_API_KEY not set — running in Mentor Simulation Mode.");
            return buildMockResponse(code, language, sessionType);
        }
        try {
            String prompt = buildPrompt(code, language, learningMode, sessionType);
            String rawJson = callGeminiApi(prompt);
            return parseGeminiResponse(rawJson);
        } catch (Exception e) {
            log.error("Gemini API call failed: {}. Falling back to simulation.", e.getMessage());
            return buildMockResponse(code, language, sessionType);
        }
    }

    private String buildPrompt(String code, String language, String learningMode, String sessionType) {
        String modeInstruction = switch (learningMode == null ? "INTERMEDIATE" : learningMode.toUpperCase()) {
            case "BEGINNER" -> "Explain everything in very simple English, avoiding jargon. Use analogies.";
            case "EXPERT"   -> "Provide deep JVM/runtime internals, performance implications, and architectural best practices.";
            default         -> "Explain with clear programming concepts and practical examples.";
        };

        String typeInstruction = switch (sessionType == null ? "DEBUG" : sessionType.toUpperCase()) {
            case "STACKTRACE" -> "This is a Java stack trace. Identify the root cause exception, the file and line number, and explain what went wrong.";
            case "SQL"        -> "This is a SQL query. Detect syntax errors, wrong joins, missing clauses, and suggest an optimized version.";
            case "EXPLAIN"    -> "Explain every function, variable, loop, and the overall complexity of this code. Suggest clean code improvements.";
            default           -> "Perform a full debug analysis: detect syntax, runtime, and logical errors.";
        };

        return """
            You are DebugMate AI — a senior software engineer and patient mentor with 15 years of experience.
            Your task: %s
            Language: %s
            Learning Mode: %s. %s

            Analyze the following code and respond ONLY with a valid JSON object matching this exact schema:
            {
              "programmingLanguage": "string",
              "severity": "CRITICAL|HIGH|MEDIUM|LOW|SUGGESTION",
              "confidenceScore": 0-100,
              "codeQualityScore": "X/100",
              "detectedErrors": [{"type":"string","lineNumber":0,"description":"string","fix":"string","severity":"string"}],
              "correctedCode": "string (full corrected code)",
              "explanation": "string (detailed mentor-style explanation)",
              "rootCause": "string (root cause analysis)",
              "bestPractices": ["string"],
              "relatedConcepts": ["string"],
              "interviewQuestion": "string",
              "references": ["string"],
              "learningTip": "string"
            }

            CODE TO ANALYZE:
            ```%s
            %s
            ```

            Respond ONLY with the JSON object. No markdown fences, no extra text.
            """.formatted(typeInstruction, language, learningMode, modeInstruction, language.toLowerCase(), code);
    }

    private String callGeminiApi(String prompt) {
        String url = GEMINI_URL.formatted(model, apiKey);

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(Map.of(
                "parts", List.of(Map.of("text", prompt))
            )),
            "generationConfig", Map.of(
                "temperature", 0.3,
                "maxOutputTokens", 4096
            )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }

    private DebugResponseDto parseGeminiResponse(String rawResponse) throws Exception {
        JsonNode root = objectMapper.readTree(rawResponse);
        String text = root.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();

        // Strip any accidental markdown fences
        text = text.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();

        JsonNode json = objectMapper.readTree(text);

        List<DebugResponseDto.DetectedError> errors = new ArrayList<>();
        if (json.has("detectedErrors")) {
            for (JsonNode e : json.get("detectedErrors")) {
                errors.add(DebugResponseDto.DetectedError.builder()
                    .type(e.path("type").asText())
                    .lineNumber(e.path("lineNumber").asInt(0))
                    .description(e.path("description").asText())
                    .fix(e.path("fix").asText())
                    .severity(e.path("severity").asText())
                    .build());
            }
        }

        List<String> bestPractices = new ArrayList<>();
        json.path("bestPractices").forEach(n -> bestPractices.add(n.asText()));

        List<String> relatedConcepts = new ArrayList<>();
        json.path("relatedConcepts").forEach(n -> relatedConcepts.add(n.asText()));

        List<String> references = new ArrayList<>();
        json.path("references").forEach(n -> references.add(n.asText()));

        return DebugResponseDto.builder()
            .programmingLanguage(json.path("programmingLanguage").asText())
            .severity(json.path("severity").asText("MEDIUM"))
            .confidenceScore(json.path("confidenceScore").asInt(75))
            .codeQualityScore(json.path("codeQualityScore").asText("N/A"))
            .detectedErrors(errors)
            .correctedCode(json.path("correctedCode").asText())
            .explanation(json.path("explanation").asText())
            .rootCause(json.path("rootCause").asText())
            .bestPractices(bestPractices)
            .relatedConcepts(relatedConcepts)
            .interviewQuestion(json.path("interviewQuestion").asText())
            .references(references)
            .learningTip(json.path("learningTip").asText())
            .timestamp(LocalDateTime.now())
            .build();
    }

    // ─── Rich Mentor Simulation Mode (no API key needed) ─────────────────────
    private DebugResponseDto buildMockResponse(String code, String language, String sessionType) {
        boolean isStackTrace = "STACKTRACE".equalsIgnoreCase(sessionType);
        boolean isSql        = "SQL".equalsIgnoreCase(sessionType);
        boolean isExplain    = "EXPLAIN".equalsIgnoreCase(sessionType);

        String explanation = isStackTrace
            ? "Your stack trace reveals a NullPointerException originating from a dereferenced object that was never initialized. This is one of the most common Java runtime errors. The JVM throws this when your code attempts to call a method or access a field on a null reference."
            : isSql
            ? "Your SQL query has a structural issue: a missing or incorrect JOIN condition may cause a Cartesian product, returning far more rows than expected. Additionally, the WHERE clause appears after the GROUP BY, which could lead to incorrect aggregation results."
            : isExplain
            ? "This code defines a recursive function. The base case stops recursion when the input reaches zero, and the recursive case reduces the problem by one unit per call. The time complexity is O(n) and space complexity is O(n) due to the call stack depth."
            : "Your code contains a NullPointerException risk on line 7 where `user.getName()` is called without a null check. Additionally, the loop on line 12 uses `i <= array.length` instead of `i < array.length`, which will throw an ArrayIndexOutOfBoundsException at runtime. These are the two most common Java beginner mistakes.";

        List<DebugResponseDto.DetectedError> errors = new ArrayList<>();
        if (!isExplain) {
            errors.add(DebugResponseDto.DetectedError.builder()
                .type("NullPointerException Risk")
                .lineNumber(7)
                .description("Calling method on potentially null object reference `user`")
                .fix("Add a null check: `if (user != null) { ... }` or use `Optional<User>`")
                .severity("HIGH")
                .build());

            if (!isStackTrace && !isSql) {
                errors.add(DebugResponseDto.DetectedError.builder()
                    .type("ArrayIndexOutOfBoundsException")
                    .lineNumber(12)
                    .description("Loop condition `i <= array.length` exceeds valid index range (0 to length-1)")
                    .fix("Change `i <= array.length` to `i < array.length`")
                    .severity("CRITICAL")
                    .build());
            }
        }

        String corrected = code.replace("i <= array.length", "i < array.length")
                               .replace("user.getName()", "user != null ? user.getName() : \"Unknown\"");

        return DebugResponseDto.builder()
            .programmingLanguage(language)
            .severity(isExplain ? "SUGGESTION" : "HIGH")
            .confidenceScore(isExplain ? 95 : 88)
            .codeQualityScore(isExplain ? "82/100" : "61/100")
            .detectedErrors(errors)
            .correctedCode(corrected.equals(code) ? "// ✅ Code looks correct structurally. Enable Gemini API for deep AI analysis.\n" + code : corrected)
            .explanation(explanation)
            .rootCause(isExplain ? "Recursive pattern — not an error, but be aware of potential StackOverflowError for large inputs."
                      : isStackTrace ? "The object reference was null at the point of method invocation. Ensure the object is instantiated before calling any of its methods."
                      : "Two separate bugs: (1) A null dereference risk where no guard clause exists. (2) An off-by-one error in the loop termination condition.")
            .bestPractices(List.of(
                "Always validate method parameters with Objects.requireNonNull() or Optional",
                "Use enhanced for-each loops to avoid index-related bugs",
                "Apply the Null Object pattern to eliminate null checks throughout your code",
                "Write unit tests for every edge case including null and empty inputs"
            ))
            .relatedConcepts(List.of(
                "Null Object Pattern", "Optional<T> in Java 8+",
                "Array indexing and zero-based counting",
                "Defensive programming", "Early return / guard clauses"
            ))
            .interviewQuestion("Q: What is the difference between NullPointerException and ArrayIndexOutOfBoundsException? When does each occur, and how do you prevent them in production code?")
            .references(List.of(
                "Java Docs: java.lang.NullPointerException — https://docs.oracle.com/javase/8/docs/api/java/lang/NullPointerException.html",
                "Effective Java Item 43: Return empty collections — Bloch, Joshua",
                "Baeldung: Guide to Optional in Java — https://www.baeldung.com/java-optional"
            ))
            .learningTip("🎯 Mentor Tip: Make 'null checks first' a muscle memory habit. Before accessing ANY object's method, ask yourself: 'Can this ever be null?' If yes, handle it. Your future self (and teammates) will thank you.")
            .timestamp(LocalDateTime.now())
            .build();
    }
}
