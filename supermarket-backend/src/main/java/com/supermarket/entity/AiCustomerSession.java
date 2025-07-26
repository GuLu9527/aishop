package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI客服会话实体类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_customer_session")
public class AiCustomerSession {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话唯一标识
     */
    @TableField("session_id")
    private String sessionId;

    /**
     * 客户ID
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 客户姓名
     */
    @TableField("customer_name")
    private String customerName;

    /**
     * 客户联系方式
     */
    @TableField("customer_phone")
    private String customerPhone;

    /**
     * 会话标题
     */
    @TableField("session_title")
    private String sessionTitle;

    /**
     * 会话状态：1-进行中，2-已结束，3-转人工
     */
    @TableField("status")
    private Integer status;

    /**
     * 消息总数
     */
    @TableField("message_count")
    private Integer messageCount;

    /**
     * 最后一条消息内容
     */
    @TableField("last_message_content")
    private String lastMessageContent;

    /**
     * 最后消息时间
     */
    @TableField("last_message_time")
    private LocalDateTime lastMessageTime;

    /**
     * 是否需要人工介入：0-否，1-是
     */
    @TableField("need_human_intervention")
    private Boolean needHumanIntervention;

    /**
     * 分配的客服人员ID
     */
    @TableField("assigned_staff_id")
    private Long assignedStaffId;

    /**
     * 分配的客服人员姓名
     */
    @TableField("assigned_staff_name")
    private String assignedStaffName;

    /**
     * 客户满意度评分（1-5分）
     */
    @TableField("customer_satisfaction")
    private BigDecimal customerSatisfaction;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    // 为了兼容服务层代码，添加一些别名方法
    public String getCustomerContact() {
        return this.customerPhone;
    }

    public void setCustomerContact(String customerContact) {
        this.customerPhone = customerContact;
    }

    public Long getAssignedTo() {
        return this.assignedStaffId;
    }

    public void setAssignedTo(Long assignedTo) {
        this.assignedStaffId = assignedTo;
    }

    public BigDecimal getSatisfactionScore() {
        return this.customerSatisfaction;
    }

    public void setSatisfactionScore(BigDecimal satisfactionScore) {
        this.customerSatisfaction = satisfactionScore;
    }
}