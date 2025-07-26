package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * AI客服评价实体类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_service_evaluation")
public class AiServiceEvaluation {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
     * 评价类型：ai_service,human_service,overall
     */
    @TableField("evaluation_type")
    private String evaluationType;

    /**
     * 满意度评分(1-5)
     */
    @TableField("satisfaction_score")
    private Integer satisfactionScore;

    /**
     * 响应速度评分(1-5)
     */
    @TableField("response_speed_score")
    private Integer responseSpeedScore;

    /**
     * 解决质量评分(1-5)
     */
    @TableField("solution_quality_score")
    private Integer solutionQualityScore;

    /**
     * 服务态度评分(1-5)
     */
    @TableField("attitude_score")
    private Integer attitudeScore;

    /**
     * 反馈内容
     */
    @TableField("feedback_content")
    private String feedbackContent;

    /**
     * 改进建议
     */
    @TableField("suggestions")
    private String suggestions;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}