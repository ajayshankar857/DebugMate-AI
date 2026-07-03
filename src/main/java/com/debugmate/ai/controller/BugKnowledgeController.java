package com.debugmate.ai.controller;

import com.debugmate.ai.entity.BugKnowledge;
import com.debugmate.ai.repository.BugKnowledgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class BugKnowledgeController {

    private final BugKnowledgeRepository bugKnowledgeRepository;

    @GetMapping
    public ResponseEntity<List<BugKnowledge>> getAllKnowledge() {
        return ResponseEntity.ok(bugKnowledgeRepository.findAll());
    }

    @GetMapping("/language/{lang}")
    public ResponseEntity<List<BugKnowledge>> getByLanguage(@PathVariable String lang) {
        return ResponseEntity.ok(bugKnowledgeRepository.findByLanguageIgnoreCase(lang));
    }
}
