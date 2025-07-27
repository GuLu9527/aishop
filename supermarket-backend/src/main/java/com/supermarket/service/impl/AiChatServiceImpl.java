package com.supermarket.service.impl;

import com.supermarket.constants.AiChatConstants;
import com.supermarket.dto.AiChatRequest;
import com.supermarket.dto.AiChatResponse;
import com.supermarket.entity.AiConversation;
import com.supermarket.entity.AiMessage;
import com.supermarket.enums.ActionType;
import com.supermarket.enums.ConversationStatus;
import com.supermarket.enums.IntentType;
import com.supermarket.enums.MessageType;
import com.supermarket.mapper.AiConversationMapper;
import com.supermarket.mapper.AiMessageMapper;
import com.supermarket.service.AiChatService;
import com.supermarket.service.AiIntentService;
import com.supermarket.service.BusinessToolsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;
import reactor.core.publisher.Flux;

/**
 * AI聊天服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {
    
    private final TongyiAiServiceImpl tongyiAiService;
    private final AiConversationMapper conversationMapper;
    private final AiMessageMapper messageMapper;
    private final AiIntentService intentService;
    private final BusinessToolsService businessToolsService;
    
    @Override
    @Transactional
    public AiChatResponse chat(AiChatRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. 获取或创建会话
            AiConversation conversation = getOrCreateConversation(request);
            
            // 2. 保存用户消息
            AiMessage userMessage = saveUserMessage(conversation, request);
            
            // 3. 意图识别和实体提取
            Map<String, Object> intentResult = intentService.parseIntent(request.getMessage());
            String intent = (String) intentResult.get("intent");
            Map<String, Object> entities = null;
            Object entitiesObj = intentResult.get("entities");
            if (entitiesObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> entitiesMap = (Map<String, Object>) entitiesObj;
                entities = entitiesMap;
            } else {
                entities = new HashMap<>();
            }
            
            // 4. 执行业务操作
            Map<String, Object> actionResult = null;
            String action = null;
            if (intent != null && !IntentType.CHAT.getCode().equals(intent)) {
                // 将意图映射到具体的操作
                action = mapIntentToAction(intent);
                if (action != null) {
                    actionResult = businessToolsService.executeAction(action, entities, request.getUserId());
                }
            }
            
            // 5. 生成AI回复
            String aiResponse = generateAiResponse(request.getMessage(), intent, entities, actionResult);
            
            // 6. 保存AI回复
            AiMessage aiMessage = saveAiMessage(conversation, aiResponse, intent, entities, action, actionResult);
            
            // 7. 更新会话信息
            updateConversation(conversation);
            
            // 8. 生成建议问题
            List<String> suggestions = generateSuggestions(intent, actionResult);
            
            long processTime = System.currentTimeMillis() - startTime;
            
            // 9. 构建响应
            AiChatResponse response = new AiChatResponse();
            response.setSessionId(conversation.getSessionId());
            response.setMessage(aiResponse);
            response.setIntent(intent);
            response.setEntities(entities);
            response.setAction(action);
            response.setActionResult(actionResult);
            response.setSuggestions(suggestions);
            response.setProcessTime(processTime);
            response.setSuccess(true);
            
            return response;
            
        } catch (Exception e) {
            log.error("AI聊天处理失败", e);
            
            AiChatResponse errorResponse = new AiChatResponse();
            errorResponse.setSuccess(false);
            errorResponse.setErrorMessage("抱歉，我暂时无法处理您的请求，请稍后再试。");
            errorResponse.setMessage("抱歉，我暂时无法处理您的请求，请稍后再试。");
            errorResponse.setProcessTime(System.currentTimeMillis() - startTime);
            
            return errorResponse;
        }
    }
    
    private AiConversation getOrCreateConversation(AiChatRequest request) {
        // 如果前端传递了conversationId，优先使用conversationId查找对话
        if (request.getConversationId() != null && !request.getConversationId().isEmpty()) {
            try {
                Long conversationId = Long.parseLong(request.getConversationId());
                AiConversation conversation = conversationMapper.selectById(conversationId);
                if (conversation != null && conversation.getUserId().equals(request.getUserId())) {
                    return conversation;
                }
            } catch (NumberFormatException e) {
                log.warn("无效的conversationId格式: {}", request.getConversationId());
            }
        }
        
        // 如果需要创建新会话或sessionId为空，创建新对话
        if (request.getNewSession() || request.getCreateNewConversation() || request.getSessionId() == null) {
            return createConversation(request.getUserId(), request.getUserName());
        }
        
        // 使用sessionId查找对话
        AiConversation conversation = conversationMapper.selectBySessionId(request.getSessionId());
        if (conversation == null) {
            return createConversation(request.getUserId(), request.getUserName());
        }
        
        return conversation;
    }
    
    @Override
    @Transactional
    public AiConversation createConversation(Long userId, String userName) {
        AiConversation conversation = new AiConversation();
        conversation.setSessionId(UUID.randomUUID().toString().replace("-", ""));
        conversation.setUserId(userId);
        conversation.setUserName(userName);
        conversation.setTitle(AiChatConstants.DEFAULT_CONVERSATION_TITLE);
        conversation.setStatus(ConversationStatus.ACTIVE.getCode());
        conversation.setMessageCount(0);
        conversation.setCreateTime(LocalDateTime.now());
        conversation.setUpdateTime(LocalDateTime.now());
        conversation.setIsDeleted(0);
        
        conversationMapper.insert(conversation);
        log.info("创建新会话: sessionId={}, userId={}", conversation.getSessionId(), userId);
        
        return conversation;
    }
    
    private AiMessage saveUserMessage(AiConversation conversation, AiChatRequest request) {
        AiMessage message = new AiMessage();
        message.setConversationId(conversation.getId()); // Long类型，不需要转换
        message.setSessionId(conversation.getSessionId());
        message.setMessageType(MessageType.USER.getCode()); // 使用枚举
        message.setContent(request.getMessage());
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
        message.setIsDeleted(0);
        
        messageMapper.insert(message);
        return message;
    }
    
    private String generateAiResponse(String userMessage, String intent, Map<String, Object> entities, Map<String, Object> actionResult) {
        // 构建提示词
        String promptText = buildPrompt(userMessage, intent, entities, actionResult);
        
        // 使用通义千问服务
        try {
            if (!tongyiAiService.isAvailable()) {
                return AiChatConstants.AI_SERVICE_NOT_CONFIGURED;
            }
            
            return tongyiAiService.chat(promptText);
        } catch (Exception e) {
            log.error("调用AI模型失败", e);
            return AiChatConstants.DEFAULT_ERROR_RESPONSE;
        }
    }
    
    private String buildPrompt(String userMessage, String intent, Map<String, Object> entities, Map<String, Object> actionResult) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("你是一个超市管理系统的AI助手，请用友好、专业的语气回复用户。\n\n");
        prompt.append("用户消息：").append(userMessage).append("\n");
        
        if (intent != null) {
            prompt.append("识别意图：").append(intent).append("\n");
        }
        
        if (entities != null && !entities.isEmpty()) {
            prompt.append("提取实体：").append(entities).append("\n");
        }
        
        if (actionResult != null) {
            prompt.append("操作结果：").append(actionResult).append("\n");
        }
        
        prompt.append("\n请基于以上信息生成合适的回复，要求：\n");
        prompt.append("1. 语言通俗易懂，适合超市老板理解\n");
        prompt.append("2. 如果有操作结果，要清晰地说明结果\n");
        prompt.append("3. 如果操作失败，要给出建议\n");
        prompt.append("4. 保持友好和专业的语气\n");
        prompt.append("5. 回复长度控制在200字以内\n");
        
        return prompt.toString();
    }
    
    private AiMessage saveAiMessage(AiConversation conversation, String content, String intent, 
                                   Map<String, Object> entities, String action, Map<String, Object> actionResult) {
        AiMessage message = new AiMessage();
        message.setConversationId(conversation.getId()); // Long类型，不需要转换
        message.setSessionId(conversation.getSessionId());
        message.setMessageType(MessageType.AI.getCode()); // 使用枚举
        message.setContent(content);
        message.setIntent(intent);
        message.setEntities(entities);
        message.setAction(action);
        message.setActionResult(actionResult);
        message.setModelName(AiChatConstants.DEFAULT_MODEL_NAME);
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
        message.setIsDeleted(0);
        
        messageMapper.insert(message);
        log.info("保存AI消息: conversationId={}, content={}", conversation.getId(), content);
        
        return message;
    }
    
    private void updateConversation(AiConversation conversation) {
        conversation.setMessageCount(conversation.getMessageCount() + 2); // 用户消息 + AI回复
        conversation.setLastMessageTime(LocalDateTime.now());
        conversation.setUpdateTime(LocalDateTime.now());
        
        conversationMapper.updateById(conversation);
    }
    
    private List<String> generateSuggestions(String intent, Map<String, Object> actionResult) {
        List<String> suggestions = new ArrayList<>();
        
        IntentType intentType = IntentType.fromCode(intent);
        
        switch (intentType) {
            case QUERY_SALES:
                suggestions.addAll(Arrays.asList(AiChatConstants.SuggestionTemplates.SALES_SUGGESTIONS));
                break;
            case CHECK_INVENTORY:
                suggestions.addAll(Arrays.asList(AiChatConstants.SuggestionTemplates.INVENTORY_SUGGESTIONS));
                break;
            case ADD_PRODUCT:
            case UPDATE_PRICE:
            case REMOVE_PRODUCT:
                suggestions.addAll(Arrays.asList(AiChatConstants.SuggestionTemplates.PRODUCT_SUGGESTIONS));
                break;
            case QUERY_FINANCE:
            case GENERATE_REPORT:
                suggestions.addAll(Arrays.asList(AiChatConstants.SuggestionTemplates.FINANCE_SUGGESTIONS));
                break;
            default:
                suggestions.addAll(Arrays.asList(AiChatConstants.SuggestionTemplates.DEFAULT_SUGGESTIONS));
                break;
        }
        
        // 限制建议数量
        if (suggestions.size() > AiChatConstants.DEFAULT_SUGGESTION_COUNT) {
            suggestions = suggestions.subList(0, AiChatConstants.DEFAULT_SUGGESTION_COUNT);
        }
        
        return suggestions;
    }
    
    @Override
    public List<AiMessage> getChatHistory(Long userId, String sessionId, Integer limit) {
        if (limit == null) {
            limit = 50;
        }
        List<AiMessage> messages = messageMapper.selectBySessionId(sessionId, limit);
        // 反转列表以按时间正序返回（最早的在前）
        Collections.reverse(messages);
        return messages;
    }
    
    @Override
    public List<AiConversation> getUserConversations(Long userId) {
        // 使用不带分页参数的selectByUserId方法，或者传入默认分页参数
        return conversationMapper.selectAllByUserId(userId);
    }
    
    @Override
    @Transactional
    public void endConversation(String sessionId) {
        AiConversation conversation = conversationMapper.selectBySessionId(sessionId);
        if (conversation != null) {
            conversation.setStatus(ConversationStatus.ENDED.getCode());
            conversation.setUpdateTime(LocalDateTime.now());
            conversationMapper.updateById(conversation);
            log.info("结束会话: sessionId={}", sessionId);
        }
    }
    
    @Override
    @Transactional
    public void deleteConversation(String sessionId, Long userId) {
        AiConversation conversation = conversationMapper.selectBySessionId(sessionId);
        if (conversation != null && conversation.getUserId().equals(userId)) {
            conversation.setIsDeleted(1);
            conversation.setUpdateTime(LocalDateTime.now());
            conversationMapper.updateById(conversation);
            
            // 同时删除相关消息
            messageMapper.deleteBySessionId(sessionId);
        }
    }
    
    @Override
    @Transactional
    public Flux<String> chatStream(AiChatRequest request) {
        return Flux.create(sink -> {
            try {
                // 1. 获取或创建会话
                final AiConversation conversation = getOrCreateConversation(request);
                
                // 2. 保存用户消息
                final AiMessage userMessage = saveUserMessage(conversation, request);
                
                // 3. 意图识别和实体提取
                Map<String, Object> intentResult = intentService.parseIntent(request.getMessage());
                final String intent = (String) intentResult.get("intent");
                final Map<String, Object> entities;
                Object entitiesObj = intentResult.get("entities");
                if (entitiesObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> entitiesMap = (Map<String, Object>) entitiesObj;
                    entities = entitiesMap;
                } else {
                    entities = new HashMap<>();
                }
                
                // 4. 执行业务操作
                final Map<String, Object> actionResult;
                final String action;
                if (intent != null && !IntentType.CHAT.getCode().equals(intent)) {
                    // 将意图映射到具体的操作
                    action = mapIntentToAction(intent);
                    if (action != null) {
                        actionResult = businessToolsService.executeAction(action, entities, request.getUserId());
                    } else {
                        actionResult = null;
                    }
                } else {
                    action = null;
                    actionResult = null;
                }
                
                // 5. 构建提示词（包含上下文）
                String promptText = buildPromptWithContext(request.getMessage(), intent, entities, actionResult, conversation.getSessionId());
                
                // 6. 获取流式AI回复
                if (!tongyiAiService.isAvailable()) {
                    sink.next("抱歉，AI服务暂时不可用，请稍后重试。");
                    sink.complete();
                    return;
                }
                
                // 用于存储完整的AI回复
                final StringBuilder fullResponse = new StringBuilder();
                
                tongyiAiService.chatStream(promptText).subscribe(
                    chunk -> {
                        fullResponse.append(chunk);
                        sink.next(chunk);
                    },
                    error -> {
                        log.error("流式AI服务调用失败", error);
                        if (fullResponse.length() == 0) {
                            sink.next("抱歉，AI服务出现异常，请稍后重试。");
                        }
                        sink.error(error);
                    },
                    () -> {
                        try {
                            // 7. 保存AI回复到数据库
                            saveAiMessage(conversation, fullResponse.toString(), intent, entities, action, actionResult);
                            
                            // 8. 更新会话信息
                            updateConversation(conversation);
                            
                            sink.complete();
                        } catch (Exception e) {
                            log.error("保存AI消息失败", e);
                            sink.error(e);
                        }
                    }
                );
                
            } catch (Exception e) {
                log.error("流式聊天处理失败", e);
                sink.next("抱歉，处理您的请求时出现异常，请稍后重试。");
                sink.error(e);
            }
        });
    }
    
    private String buildPromptWithContext(String userMessage, String intent, Map<String, Object> entities, 
                                        Map<String, Object> actionResult, String sessionId) {
        StringBuilder prompt = new StringBuilder();
        
        // 获取历史对话上下文
        List<AiMessage> recentMessages = messageMapper.selectBySessionId(sessionId, 10);
        
        prompt.append("你是一个超市管理系统的AI助手，请用友好、专业的语气回复用户。\n\n");
        
        // 添加历史对话上下文
        if (!recentMessages.isEmpty()) {
            prompt.append("对话历史：\n");
            // 由于查询结果是倒序的，需要反转列表以按时间正序显示
            Collections.reverse(recentMessages);
            for (AiMessage msg : recentMessages) {
                if (msg.getMessageType() == MessageType.USER.getCode()) {
                    prompt.append("用户：").append(msg.getContent()).append("\n");
                } else if (msg.getMessageType() == MessageType.AI.getCode()) {
                    prompt.append("助手：").append(msg.getContent()).append("\n");
                }
            }
            prompt.append("\n");
        }
        
        prompt.append("当前用户消息：").append(userMessage).append("\n");
        
        if (intent != null) {
            prompt.append("识别意图：").append(intent).append("\n");
        }
        
        if (entities != null && !entities.isEmpty()) {
            prompt.append("提取实体：").append(entities).append("\n");
        }
        
        if (actionResult != null) {
            prompt.append("工具调用结果：").append(actionResult).append("\n");
        }
        
        prompt.append("\n请基于以上信息和对话历史生成合适的回复，要求：\n");
        prompt.append("1. 考虑对话上下文，保持对话连贯性\n");
        prompt.append("2. 语言通俗易懂，适合超市老板理解\n");
        prompt.append("3. 如果有工具调用结果，要清晰地说明结果\n");
        prompt.append("4. 如果操作失败，要给出建议\n");
        prompt.append("5. 保持友好和专业的语气\n");
        prompt.append("6. 回复长度控制在200字以内\n");
        
        return prompt.toString();
    }
    
    /**
     * 将意图映射到具体的操作
     */
    private String mapIntentToAction(String intent) {
        if (intent == null) {
            return null;
        }
        
        switch (intent) {
            case "QUERY_SALES":
                return ActionType.QUERY_SALES_DATA.getCode();
            case "CHECK_INVENTORY":
                return ActionType.CHECK_INVENTORY_STATUS.getCode();
            case "ADD_PRODUCT":
                return ActionType.ADD_NEW_PRODUCT.getCode();
            case "UPDATE_PRICE":
                return ActionType.UPDATE_PRODUCT_PRICE.getCode();
            case "QUERY_FINANCE":
                return ActionType.GET_FINANCIAL_OVERVIEW.getCode();
            case "GENERATE_REPORT":
                return ActionType.GENERATE_SALES_REPORT.getCode();
            case "QUERY_SALES_RANKING":
                return ActionType.GET_SALES_RANKING.getCode();
            default:
                log.warn("未知意图类型: {}", intent);
                return null;
        }
    }
}