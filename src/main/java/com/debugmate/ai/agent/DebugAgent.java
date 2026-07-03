package com.debugmate.ai.agent;

import com.debugmate.ai.dto.DebugResponseDto;
import com.debugmate.ai.llm.GeminiLlmClient;
import com.debugmate.ai.service.RagService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DebugAgent {

    private final GeminiLlmClient llmClient;
    private final RagService ragService;
    private final ObjectMapper objectMapper;

    /**
     * Agentic Pipeline:
     * 1. Plan: Determine what type of analysis is required.
     * 2. Retrieve: Fetch RAG context from the Bug Knowledge Base.
     * 3. Act: Construct the prompt and execute via the LLM.
     * 4. Parse: Structure the output into DTOs.
     */
    public DebugResponseDto analyze(String code, String language, String learningMode, String sessionType) {
        log.info("Starting Agentic Pipeline for {} code", language);

        // 1. Retrieve Phase (RAG)
        String ragContext = ragService.retrieveContext(language, sessionType);

        if (!llmClient.isConfigured()) {
            log.warn("LLM API Key not found. Falling back to Simulation Mode.");
            return buildMockResponse(code, language, sessionType);
        }

        try {
            // 2. Plan Phase (Prompt Engineering)
            String prompt = buildPrompt(code, language, learningMode, sessionType, ragContext);

            // 3. Act Phase (LLM Execution)
            String rawJsonResponse = llmClient.generate(prompt);

            // 4. Parse Phase
            return parseResponse(rawJsonResponse);

        } catch (Exception e) {
            log.error("Agent pipeline failed: {}", e.getMessage());
            return buildMockResponse(code, language, sessionType);
        }
    }

    public String chat(String userMessage) {
        if (!llmClient.isConfigured()) {
            return "Simulation Mode: I see you are asking about '" + userMessage + "'. As your mentor, I'd suggest reviewing the core principles of software engineering. (Enable Gemini API for live responses).";
        }
        try {
            String prompt = "You are DebugMate AI, a senior software engineer mentor. Respond briefly and concisely to this developer query: " + userMessage;
            String rawJson = llmClient.generate(prompt);
            JsonNode root = objectMapper.readTree(rawJson);
            return root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();
        } catch (Exception e) {
            log.error("Chat Agent failed: {}", e.getMessage());
            return "I'm having trouble connecting to my knowledge base right now.";
        }
    }

    private String buildPrompt(String code, String language, String learningMode, String sessionType, String ragContext) {
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

            %s

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
            """.formatted(typeInstruction, language, learningMode, modeInstruction, ragContext, language.toLowerCase(), code);
    }

    private DebugResponseDto parseResponse(String rawResponse) throws Exception {
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

    private DebugResponseDto buildMockResponse(String code, String language, String sessionType) {
        boolean isStackTrace = "STACKTRACE".equalsIgnoreCase(sessionType);
        boolean isSql        = "SQL".equalsIgnoreCase(sessionType);
        boolean isExplain    = "EXPLAIN".equalsIgnoreCase(sessionType);

        String explanation = isStackTrace
            ? "Your stack trace reveals a NullPointerException originating from a dereferenced object that was never initialized. This is one of the most common Java runtime errors."
            : isSql
            ? "Your SQL query has a structural issue: a missing or incorrect JOIN condition may cause a Cartesian product."
            : isExplain
            ? "This code defines a recursive function. The base case stops recursion when the input reaches zero."
            : "Your code contains a NullPointerException risk. Additionally, the loop uses an off-by-one error in the termination condition.";

        List<DebugResponseDto.DetectedError> errors = new ArrayList<>();
        if (!isExplain) {
            errors.add(DebugResponseDto.DetectedError.builder()
                .type("NullPointerException Risk")
                .lineNumber(7)
                .description("Calling method on potentially null object reference")
                .fix("Add a null check")
                .severity("HIGH")
                .build());
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
            .rootCause("Analyzed structurally by Mentor Mode. API not configured.")
            .bestPractices(List.of("Always validate method parameters", "Use enhanced for-each loops"))
            .relatedConcepts(List.of("Null Object Pattern", "Defensive programming"))
            .interviewQuestion("Q: How do you prevent NullPointerException in production code?")
            .references(List.of("Java Docs: java.lang.NullPointerException"))
            .learningTip("🎯 Mentor Tip: Make 'null checks first' a muscle memory habit.")
            .timestamp(LocalDateTime.now())
            .build();
    }
}
