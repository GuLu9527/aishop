package com.supermarket.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * AI聊天响应DTO
 */
@Data
public class AiChatResponse {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * AI回复内容
     */
    private String message;
    
    /**
     * 识别的意图
     */
    private String intent;
    
    /**
     * 提取的实体
     */
    private Map<String, Object> entities;
    
    /**
     * 执行的操作
     */
    private String action;
    
    /**
     * 操作结果
     */
    private Map<String, Object> actionResult;
    
    /**
     * 建议的后续问题
     */
    private List<String> suggestions;
    
    /**
     * 处理耗时（毫秒）
     */
    private Long processTime;
    
    /**
     * 是否成功
     */
    private Boolean success = true;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 消息类型：1-普通回复，2-确认操作，3-需要更多信息，4-操作结果
     */
    private Integer messageType = 1;
    
    /**
     * 是否需要用户确认
     */
    private Boolean needConfirmation = false;
    
    /**
     * 确认操作的参数
     */
    private Map<String, Object> confirmationData;
}