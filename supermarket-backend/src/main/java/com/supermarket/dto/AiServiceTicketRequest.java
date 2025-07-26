package com.supermarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * AI客服工单创建请求DTO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class AiServiceTicketRequest {

    /**
     * 会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    /**
     * 客户姓名
     */
    @NotBlank(message = "客户姓名不能为空")
    private String customerName;

    /**
     * 客户联系方式
     */
    private String customerContact;

    /**
     * 问题类型：product_issue,order_issue,payment_issue,refund_issue,complaint,other
     */
    @NotBlank(message = "问题类型不能为空")
    private String issueType;

    /**
     * 问题标题
     */
    @NotBlank(message = "问题标题不能为空")
    private String issueTitle;

    /**
     * 问题描述
     */
    @NotBlank(message = "问题描述不能为空")
    private String issueDescription;

    /**
     * 优先级：low,medium,high,urgent
     */
    private String priority = "medium";
}