package com.supermarket.controller;

import com.supermarket.common.Result;
import com.supermarket.dto.AiChatRequest;
import com.supermarket.dto.AiChatResponse;
import com.supermarket.repository.ChatHistoryRepository;
import com.supermarket.service.AiChatService;
import com.supermarket.service.impl.TongyiAiServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

/**
 * AI测试控制器
 * 用于测试AI服务配置是否正确
 */
@Slf4j
@RestController
@RequestMapping("/ai-test")
@RequiredArgsConstructor
@Tag(name = "AI测试接口", description = "用于测试AI服务配置和功能")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AiTestController {

    private final ChatClient chatClient;
    
    @PostMapping(value = "/service", produces = "text/plain;charset=utf-8")
    public Flux<String> service(@RequestParam String prompt, @RequestParam String chatId){
        return chatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY,chatId))
                .stream()
                .content();
    }
}