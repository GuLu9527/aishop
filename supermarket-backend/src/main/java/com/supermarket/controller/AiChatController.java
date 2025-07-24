package com.supermarket.controller;

import com.supermarket.dto.AiChatRequest;
import com.supermarket.dto.AiChatResponse;
import com.supermarket.entity.AiConversation;
import com.supermarket.entity.AiMessage;
import com.supermarket.service.AiChatService;
import com.supermarket.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI聊天控制器
 */
@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Tag(name = "AI聊天接口", description = "AI对话交互及历史记录管理")
public class AiChatController {
    
    private final AiChatService aiChatService;
    
    @PostMapping("/chat")
    @Operation(summary = "发送聊天消息", description = "用户发送消息并获取AI回复，需传入userId和消息内容")
    public Result<AiChatResponse> chat(@RequestBody AiChatRequest request) {
        try {
            log.info("收到AI聊天请求: {}", request);
            
            // 参数验证
            if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                return Result.error("消息内容不能为空");
            }
            
            if (request.getUserId() == null) {
                return Result.error("用户ID不能为空");
            }
            
            // 处理聊天消息
            AiChatResponse response = aiChatService.chat(request);
            
            log.info("AI聊天响应: {}", response);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("处理AI聊天请求失败", e);
            return Result.error("AI服务暂时不可用，请稍后重试");
        }
    }
    
    @PostMapping("/conversation")
    @Operation(summary = "创建新会话", description = "为用户创建新的AI对话会话")
    public Result<AiConversation> createConversation(@RequestParam Long userId, 
                                                   @RequestParam(required = false) String title) {
        try {
            AiConversation conversation = aiChatService.createConversation(userId, title);
            return Result.success(conversation);
        } catch (Exception e) {
            log.error("创建会话失败", e);
            return Result.error("创建会话失败");
        }
    }
    
    @GetMapping("/conversation/{conversationId}/history")
    @Operation(summary = "获取聊天历史", description = "获取指定会话的聊天历史记录")
    public Result<List<AiMessage>> getChatHistory(@PathVariable String conversationId,
                                                 @RequestParam Long userId,
                                                 @RequestParam(defaultValue = "50") Integer limit) {
        try {
            List<AiMessage> history = aiChatService.getChatHistory(userId, conversationId, limit);
            return Result.success(history);
        } catch (Exception e) {
            log.error("获取聊天历史失败", e);
            return Result.error("获取聊天历史失败");
        }
    }
    
    @GetMapping("/conversations")
    @Operation(summary = "获取用户会话列表", description = "获取指定用户的所有AI对话会话")
    public Result<List<AiConversation>> getUserConversations(@RequestParam Long userId) {
        try {
            List<AiConversation> conversations = aiChatService.getUserConversations(userId);
            return Result.success(conversations);
        } catch (Exception e) {
            log.error("获取用户会话列表失败", e);
            return Result.error("获取会话列表失败");
        }
    }
    
    @PutMapping("/conversation/{conversationId}/end")
    @Operation(summary = "结束会话", description = "结束指定的AI对话会话")
    public Result<Void> endConversation(@PathVariable String conversationId) {
        try {
            aiChatService.endConversation(conversationId);
            return Result.success();
        } catch (Exception e) {
            log.error("结束会话失败", e);
            return Result.error("结束会话失败");
        }
    }
    
    @DeleteMapping("/conversation/{conversationId}")
    @Operation(summary = "删除会话", description = "删除指定的AI对话会话及其历史记录")
    public Result<Void> deleteConversation(@PathVariable String conversationId,
                                          @RequestParam Long userId) {
        try {
            aiChatService.deleteConversation(conversationId, userId);
            return Result.success();
        } catch (Exception e) {
            log.error("删除会话失败", e);
            return Result.error("删除会话失败");
        }
    }
    
    @GetMapping("/suggestions")
    @Operation(summary = "获取智能建议", description = "获取AI智能推荐的常用问题建议")
    public Result<List<String>> getSmartSuggestions(@RequestParam Long userId) {
        try {
            // 返回一些常用的智能建议
            List<String> suggestions = List.of(
                "今天的销售情况怎么样？",
                "哪些商品库存不足？",
                "本月的财务报表",
                "添加新商品",
                "查看热销商品排行",
                "设置库存预警"
            );
            return Result.success(suggestions);
        } catch (Exception e) {
            log.error("获取智能建议失败", e);
            return Result.error("获取建议失败");
        }
    }
    
    @GetMapping("/quick-actions")
    @Operation(summary = "获取快捷操作", description = "获取AI助手的快捷操作菜单")
    public Result<List<Map<String, Object>>> getQuickActions() {
        try {
            List<Map<String, Object>> actions = List.of(
                Map.of("title", "今日销售", "action", "query_today_sales", "icon", "📊"),
                Map.of("title", "库存预警", "action", "check_inventory", "icon", "⚠️"),
                Map.of("title", "添加商品", "action", "add_product", "icon", "➕"),
                Map.of("title", "财务概况", "action", "financial_overview", "icon", "💰"),
                Map.of("title", "销售排行", "action", "sales_ranking", "icon", "🏆"),
                Map.of("title", "系统设置", "action", "system_settings", "icon", "⚙️")
            );
            return Result.success(actions);
        } catch (Exception e) {
            log.error("获取快捷操作失败", e);
            return Result.error("获取快捷操作失败");
        }
    }
}