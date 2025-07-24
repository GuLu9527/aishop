package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * AI消息实体类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_message")
@Slf4j
public class AiMessage {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    @TableField("conversation_id")
    private Long conversationId;

    /**
     * 会话标识
     */
    @TableField("session_id")
    private String sessionId;

    /**
     * 消息类型：USER-用户消息，AI-AI回复，SYSTEM-系统消息
     */
    @TableField("message_type")
    private String messageType;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 用户ID（发送者）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户姓名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 识别的意图
     */
    @TableField("intent")
    private String intent;

    /**
     * 提取的实体（JSON格式）
     */
    @TableField("entities")
    private String entities;

    /**
     * 执行的操作
     */
    @TableField("action")
    private String action;

    /**
     * 操作结果（JSON格式）
     */
    @TableField("action_result")
    private String actionResult;

    /**
     * 模型名称
     */
    @TableField("model_name")
    private String modelName;

    /**
     * 处理耗时（毫秒）
     */
    @TableField("processing_time")
    private Long processingTime;

    /**
     * 是否成功
     */
    @TableField("success")
    private Boolean success;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

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

    // 便捷方法：设置实体Map
    public void setEntities(Map<String, Object> entitiesMap) {
        if (entitiesMap != null) {
            try {
                this.entities = objectMapper.writeValueAsString(entitiesMap);
            } catch (JsonProcessingException e) {
                log.error("序列化entities失败", e);
                this.entities = "{}";
            }
        }
    }

    // 便捷方法：设置操作结果Map
    public void setActionResult(Map<String, Object> actionResultMap) {
        if (actionResultMap != null) {
            try {
                this.actionResult = objectMapper.writeValueAsString(actionResultMap);
            } catch (JsonProcessingException e) {
                log.error("序列化actionResult失败", e);
                this.actionResult = "{}";
            }
        }
    }
}