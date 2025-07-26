package com.supermarket.dto;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * AI客服评价请求DTO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class AiServiceEvaluationRequest {

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
     * 评价类型：ai_service,human_service,overall
     */
    @NotBlank(message = "评价类型不能为空")
    private String evaluationType;

    /**
     * 满意度评分(1-5)
     */
    @NotNull(message = "满意度评分不能为空")
    @Min(value = 1, message = "满意度评分最小为1")
    @Max(value = 5, message = "满意度评分最大为5")
    private Integer satisfactionScore;

    /**
     * 响应速度评分(1-5)
     */
    @Min(value = 1, message = "响应速度评分最小为1")
    @Max(value = 5, message = "响应速度评分最大为5")
    private Integer responseSpeedScore;

    /**
     * 解决质量评分(1-5)
     */
    @Min(value = 1, message = "解决质量评分最小为1")
    @Max(value = 5, message = "解决质量评分最大为5")
    private Integer solutionQualityScore;

    /**
     * 服务态度评分(1-5)
     */
    @Min(value = 1, message = "服务态度评分最小为1")
    @Max(value = 5, message = "服务态度评分最大为5")
    private Integer attitudeScore;

    /**
     * 反馈内容
     */
    private String feedbackContent;

    /**
     * 改进建议
     */
    private String suggestions;
}