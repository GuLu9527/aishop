package com.supermarket.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI客服聊天响应VO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class AiCustomerChatResponse {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 识别的意图
     */
    private String intent;

    /**
     * 是否需要人工介入
     */
    private Boolean needHumanIntervention;

    /**
     * 建议操作
     */
    private List<String> suggestedActions;

    /**
     * 关联的知识库ID
     */
    private Long knowledgeBaseId;

    /**
     * 处理耗时（毫秒）
     */
    private Long processingTime;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}