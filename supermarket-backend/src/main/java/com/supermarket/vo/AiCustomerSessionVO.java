package com.supermarket.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI客服会话VO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class AiCustomerSessionVO {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户姓名
     */
    private String customerName;

    /**
     * 客户联系方式
     */
    private String customerPhone;

    /**
     * 会话标题
     */
    private String sessionTitle;

    /**
     * 会话状态：1-进行中，2-已结束，3-转人工
     */
    private Integer status;

    /**
     * 消息总数
     */
    private Integer messageCount;

    /**
     * 最后一条消息内容
     */
    private String lastMessageContent;

    /**
     * 最后消息时间
     */
    private LocalDateTime lastMessageTime;

    /**
     * 是否需要人工介入
     */
    private Boolean needHumanIntervention;

    /**
     * 分配的客服人员ID
     */
    private Long assignedStaffId;

    /**
     * 分配的客服人员姓名
     */
    private String assignedStaffName;

    /**
     * 客户满意度评分
     */
    private BigDecimal customerSatisfaction;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    // 兼容前端字段名
    public String getCustomerContact() {
        return this.customerPhone;
    }

    public void setCustomerContact(String customerContact) {
        this.customerPhone = customerContact;
    }

    public BigDecimal getCustomerSatisfactionScore() {
        return this.customerSatisfaction;
    }

    public void setCustomerSatisfactionScore(BigDecimal customerSatisfactionScore) {
        this.customerSatisfaction = customerSatisfactionScore;
    }
}