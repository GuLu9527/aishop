package com.supermarket.service;

import com.supermarket.dto.AiChatRequest;
import com.supermarket.dto.AiChatResponse;
import com.supermarket.entity.AiConversation;
import com.supermarket.entity.AiMessage;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI聊天服务接口
 */
public interface AiChatService {
    
    /**
     * 处理用户消息并返回AI回复
     * @param request 聊天请求
     * @return AI回复
     */
    AiChatResponse chat(AiChatRequest request);
    
    /**
     * 流式处理用户消息并返回AI回复流
     * @param request 聊天请求
     * @return AI回复流
     */
    Flux<String> chatStream(AiChatRequest request);
    
    /**
     * 创建新的对话会话
     * @param userId 用户ID
     * @param userName 用户姓名
     * @return 会话信息
     */
    AiConversation createConversation(Long userId, String userName);
    
    /**
     * 获取用户的对话历史
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param limit 限制条数
     * @return 消息列表
     */
    List<AiMessage> getChatHistory(Long userId, String sessionId, Integer limit);
    
    /**
     * 获取用户的所有会话
     * @param userId 用户ID
     * @return 会话列表
     */
    List<AiConversation> getUserConversations(Long userId);
    
    /**
     * 结束会话
     * @param sessionId 会话ID
     */
    void endConversation(String sessionId);
    
    /**
     * 删除会话
     * @param sessionId 会话ID
     * @param userId 用户ID
     */
    void deleteConversation(String sessionId, Long userId);
    
    /**
     * 批量删除用户的所有会话
     * @param userId 用户ID
     * @return 删除的会话数量
     */
    int deleteAllUserConversations(Long userId);
}