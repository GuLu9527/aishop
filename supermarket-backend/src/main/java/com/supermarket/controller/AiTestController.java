package com.supermarket.controller;

import com.supermarket.common.Result;
import com.supermarket.service.impl.TongyiAiServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * AI测试控制器
 * 用于测试AI服务配置是否正确
 */
@Slf4j
@RestController
@RequestMapping("/ai-test")
@RequiredArgsConstructor
@Tag(name = "AI测试接口", description = "用于测试AI服务配置和功能")
public class AiTestController {
    
    private final TongyiAiServiceImpl tongyiAiService;
    
    @GetMapping("/health")
    @Operation(summary = "AI服务健康检查", description = "检查AI服务配置是否正常")
    public Result<String> healthCheck() {
        try {
            if (!tongyiAiService.isConfigured()) {
                return Result.error("AI服务尚未配置，请设置DASHSCOPE_API_KEY环境变量");
            }
            
            // 简单的健康检查
            String response = tongyiAiService.chat("请回复'AI服务正常'");
            
            log.info("AI健康检查响应: {}", response);
            return Result.success("AI服务配置正常，响应: " + response);
            
        } catch (Exception e) {
            log.error("AI服务健康检查失败", e);
            return Result.error("AI服务配置异常: " + e.getMessage());
        }
    }
    
    @PostMapping("/simple-chat")
    @Operation(summary = "简单AI对话测试", description = "测试基本的AI对话功能")
    public Result<String> simpleChat(@RequestParam String message) {
        try {
            if (message == null || message.trim().isEmpty()) {
                return Result.error("消息内容不能为空");
            }
            
            if (!tongyiAiService.isConfigured()) {
                return Result.error("AI服务尚未配置，请设置DASHSCOPE_API_KEY环境变量");
            }
            
            String response = tongyiAiService.chat(message);
            
            log.info("AI对话测试 - 用户: {}, AI: {}", message, response);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("AI对话测试失败", e);
            return Result.error("AI对话失败: " + e.getMessage());
        }
    }
}