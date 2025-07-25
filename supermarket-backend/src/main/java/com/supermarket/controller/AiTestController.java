package com.supermarket.controller;

import com.supermarket.common.Result;
import com.supermarket.dto.AiChatRequest;
import com.supermarket.dto.AiChatResponse;
import com.supermarket.service.AiChatService;
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
    private final AiChatService aiChatService;
    
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
    
    @PostMapping("/full-chat")
    @Operation(summary = "完整AI聊天测试", description = "测试完整的AI聊天服务功能")
    public Result<AiChatResponse> fullChatTest(@RequestBody AiChatRequest request) {
        try {
            if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                return Result.error("消息内容不能为空");
            }
            
            // 设置默认值
            if (request.getUserId() == null) {
                request.setUserId(1L); // 测试用户ID
            }
            if (request.getUserName() == null) {
                request.setUserName("测试用户");
            }
            
            AiChatResponse response = aiChatService.chat(request);
            
            log.info("完整AI聊天测试 - 用户: {}, AI: {}", request.getMessage(), response.getMessage());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("完整AI聊天测试失败", e);
            return Result.error("AI聊天失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/config-info")
    @Operation(summary = "AI配置信息", description = "获取当前AI服务配置信息")
    public Result<String> getConfigInfo() {
        try {
            StringBuilder info = new StringBuilder();
            info.append("AI服务配置信息:\n");
            info.append("- 配置状态: ").append(tongyiAiService.isConfigured() ? "已配置" : "未配置").append("\n");
            
            // 不显示完整的API Key，只显示前几位和后几位
            String apiKeyInfo = "未设置";
            try {
                java.lang.reflect.Field field = tongyiAiService.getClass().getDeclaredField("apiKey");
                field.setAccessible(true);
                String apiKey = (String) field.get(tongyiAiService);
                if (apiKey != null && !apiKey.trim().isEmpty()) {
                    if (apiKey.length() > 10) {
                        apiKeyInfo = apiKey.substring(0, 6) + "****" + apiKey.substring(apiKey.length() - 4);
                    } else {
                        apiKeyInfo = "****";
                    }
                }
            } catch (Exception e) {
                apiKeyInfo = "无法获取";
            }
            info.append("- API Key: ").append(apiKeyInfo).append("\n");
            
            return Result.success(info.toString());
            
        } catch (Exception e) {
            log.error("获取AI配置信息失败", e);
            return Result.error("获取配置信息失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/test-sales-query")
    @Operation(summary = "测试销售查询", description = "测试AI处理销售相关查询")
    public Result<AiChatResponse> testSalesQuery() {
        try {
            AiChatRequest request = new AiChatRequest();
            request.setUserId(1L);
            request.setUserName("测试用户");
            request.setMessage("今天的销售情况怎么样？");
            
            AiChatResponse response = aiChatService.chat(request);
            
            log.info("销售查询测试 - AI响应: {}", response.getMessage());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("销售查询测试失败", e);
            return Result.error("销售查询测试失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/test-inventory-query")
    @Operation(summary = "测试库存查询", description = "测试AI处理库存相关查询")
    public Result<AiChatResponse> testInventoryQuery() {
        try {
            AiChatRequest request = new AiChatRequest();
            request.setUserId(1L);
            request.setUserName("测试用户");
            request.setMessage("帮我查看一下库存情况");
            
            AiChatResponse response = aiChatService.chat(request);
            
            log.info("库存查询测试 - AI响应: {}", response.getMessage());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("库存查询测试失败", e);
            return Result.error("库存查询测试失败: " + e.getMessage());
        }
    }
}