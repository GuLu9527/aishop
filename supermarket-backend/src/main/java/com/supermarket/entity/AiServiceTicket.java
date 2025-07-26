package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * AI客服工单实体类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_service_ticket")
public class AiServiceTicket {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 工单编号
     */
    @TableField("ticket_no")
    private String ticketNo;

    /**
     * 会话ID
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
    @TableField("customer_contact")
    private String customerContact;

    /**
     * 问题类型：product_issue,order_issue,payment_issue,refund_issue,complaint,other
     */
    @TableField("issue_type")
    private String issueType;

    /**
     * 问题标题
     */
    @TableField("issue_title")
    private String issueTitle;

    /**
     * 问题描述
     */
    @TableField("issue_description")
    private String issueDescription;

    /**
     * 优先级：low,medium,high,urgent
     */
    @TableField("priority")
    private String priority;

    /**
     * 工单状态：open,in_progress,resolved,closed,cancelled
     */
    @TableField("status")
    private String status;

    /**
     * 分配给的客服人员ID
     */
    @TableField("assigned_to")
    private Long assignedTo;

    /**
     * 分配给的客服人员姓名
     */
    @TableField("assigned_to_name")
    private String assignedToName;

    /**
     * 解决方案
     */
    @TableField("solution")
    private String solution;

    /**
     * 解决时间
     */
    @TableField("resolved_time")
    private LocalDateTime resolvedTime;

    /**
     * 关闭时间
     */
    @TableField("closed_time")
    private LocalDateTime closedTime;

    /**
     * 创建人ID
     */
    @TableField("create_by")
    private Long createBy;

    /**
     * 更新人ID
     */
    @TableField("update_by")
    private Long updateBy;

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
}