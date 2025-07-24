package com.supermarket.dto;

import lombok.Data;

/**
 * AI聊天请求DTO
 */
@Data
public class AiChatRequest {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 对话ID（前端传递的conversationId）
     */
    private String conversationId;
    
    /**
     * 用户消息内容
     */
    private String message;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户姓名
     */
    private String userName;
    
    /**
     * 消息类型：1-文本，2-语音
     */
    private Integer messageType = 1;
    
    /**
     * 是否需要创建新会话
     */
    private Boolean newSession = false;
    
    /**
     * 是否创建新对话
     */
    private Boolean createNewConversation = false;
}