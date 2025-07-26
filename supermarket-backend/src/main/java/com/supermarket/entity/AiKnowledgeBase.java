package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI知识库实体类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_knowledge_base")
public class AiKnowledgeBase {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 知识点标题
     */
    @TableField("title")
    private String title;

    /**
     * 知识点内容
     */
    @TableField("content")
    private String content;

    /**
     * 分类：product,service,policy,faq
     */
    @TableField("category")
    private String category;

    /**
     * 关键词，逗号分隔
     */
    @TableField("keywords")
    private String keywords;

    /**
     * 关联意图
     */
    @TableField("intent")
    private String intent;

    /**
     * 优先级，数字越大优先级越高
     */
    @TableField("priority")
    private Integer priority;

    /**
     * 命中次数
     */
    @TableField("hit_count")
    private Integer hitCount;

    /**
     * 有效性评分
     */
    @TableField("effectiveness_score")
    private BigDecimal effectivenessScore;

    /**
     * 状态：active,inactive,draft
     */
    @TableField("status")
    private String status;

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