package com.debugmate.ai.service;

import com.debugmate.ai.entity.ChatMessage;
import com.debugmate.ai.entity.User;
import com.debugmate.ai.repository.ChatMessageRepository;
import com.debugmate.ai.agent.DebugAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final DebugAgent debugAgent;
    private final ChatMessageRepository chatRepo;
    private final UserService userService;

    @Transactional
    public Map<String, String> chat(String message, String username) {
        User user = userService.findByUsername(username);

        String aiResponse = debugAgent.chat(message);

        ChatMessage msg = ChatMessage.builder()
                .user(user)
                .userMessage(message)
                .aiResponse(aiResponse)
                .build();
        chatRepo.save(msg);

        // Award small XP for engagement
        userService.updateProfileStats(user.getId(), 3, false);

        return Map.of("userMessage", message, "aiResponse", aiResponse);
    }

    public List<ChatMessage> getHistory(String username) {
        User user = userService.findByUsername(username);
        return chatRepo.findTop20ByUserIdOrderByCreatedAtDesc(user.getId());
    }
}
