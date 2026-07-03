package com.debugmate.ai.service;

import com.debugmate.ai.entity.BugKnowledge;
import com.debugmate.ai.repository.BugKnowledgeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {

    private final BugKnowledgeRepository bugKnowledgeRepository;

    /**
     * Retrieves knowledge base articles (RAG Context) relevant to the current language
     * and potential error type to augment the LLM prompt.
     */
    public String retrieveContext(String language, String sessionType) {
        log.info("Retrieving RAG Context for language: {}, sessionType: {}", language, sessionType);
        
        List<BugKnowledge> knowledgeList;
        if (sessionType != null && sessionType.equalsIgnoreCase("STACKTRACE")) {
            knowledgeList = bugKnowledgeRepository.findByLanguageIgnoreCaseAndCategoryIgnoreCase(language, "Runtime Error");
        } else {
            knowledgeList = bugKnowledgeRepository.findByLanguageIgnoreCase(language);
        }

        if (knowledgeList.isEmpty()) {
            return "No specific internal knowledge base context available for this request.";
        }

        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("--- INTERNAL RAG KNOWLEDGE BASE CONTEXT ---\n");
        for (BugKnowledge bk : knowledgeList) {
            contextBuilder.append("Title: ").append(bk.getTitle()).append("\n")
                          .append("Description: ").append(bk.getDescription()).append("\n")
                          .append("Best Practice: ").append(bk.getBestPractice()).append("\n\n");
        }
        contextBuilder.append("--- END RAG CONTEXT ---\n");
        
        return contextBuilder.toString();
    }
}
