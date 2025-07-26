package com.supermarket.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * AI客服聊天请求DTO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class AiCustomerChatRequest {

    /**
     * 会话ID（可选，新会话时为空）
     */
    private String sessionId;

    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    /**
     * 客户姓名
     */
    private String customerName;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;

    /**
     * 消息类型：text,image,voice
     */
    private String messageType = "text";

    /**
     * 客户联系方式
     */
    private String customerContact;
}