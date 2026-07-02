package com.debugmate.ai.controller;

import com.debugmate.ai.dto.DebugRequestDto;
import com.debugmate.ai.dto.DebugResponseDto;
import com.debugmate.ai.entity.DebugSession;
import com.debugmate.ai.service.DebugService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class DebugController {

    private final DebugService debugService;

    @PostMapping("/analyze")
    public ResponseEntity<DebugResponseDto> analyzeCode(@Valid @RequestBody DebugRequestDto request) {
        String username = getCurrentUsername();
        DebugResponseDto response = debugService.analyzeCode(request, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<DebugSession>> getHistory() {
        String username = getCurrentUsername();
        List<DebugSession> history = debugService.getHistory(username);
        return ResponseEntity.ok(history);
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
