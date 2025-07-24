package com.supermarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量操作结果DTO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@Schema(description = "批量操作结果DTO")
public class BatchOperationResultDTO {

    @Schema(description = "操作是否全部成功")
    private Boolean allSuccess;

    @Schema(description = "总数量")
    private Integer totalCount;

    @Schema(description = "成功数量")
    private Integer successCount;

    @Schema(description = "失败数量")
    private Integer failureCount;

    @Schema(description = "成功的商品ID列表")
    private List<Long> successProductIds;

    @Schema(description = "失败的商品ID列表")
    private List<Long> failureProductIds;

    @Schema(description = "失败详情列表")
    private List<String> failureDetails;

    @Schema(description = "操作开始时间")
    private LocalDateTime startTime;

    @Schema(description = "操作结束时间")
    private LocalDateTime endTime;

    @Schema(description = "操作耗时（毫秒）")
    private Long duration;

    @Schema(description = "操作摘要")
    private String summary;

    public BatchOperationResultDTO() {
        this.successProductIds = new ArrayList<>();
        this.failureProductIds = new ArrayList<>();
        this.failureDetails = new ArrayList<>();
        this.startTime = LocalDateTime.now();
    }

    public BatchOperationResultDTO(Integer totalCount) {
        this();
        this.totalCount = totalCount;
        this.successCount = 0;
        this.failureCount = 0;
    }

    /**
     * 添加成功的商品ID
     */
    public void addSuccess(Long productId) {
        this.successProductIds.add(productId);
        this.successCount = this.successProductIds.size();
        updateFailureCount();
    }

    /**
     * 添加失败的商品ID和详情
     */
    public void addFailure(Long productId, String detail) {
        this.failureProductIds.add(productId);
        this.failureDetails.add(detail);
        this.failureCount = this.failureProductIds.size();
        updateFailureCount();
    }

    /**
     * 完成操作，计算结果
     */
    public void complete() {
        this.endTime = LocalDateTime.now();
        this.duration = java.time.Duration.between(startTime, endTime).toMillis();
        this.allSuccess = this.failureCount == 0;
        
        // 生成操作摘要
        this.summary = String.format("批量操作完成：总数 %d，成功 %d，失败 %d，耗时 %d ms",
                totalCount, successCount, failureCount, duration);
    }

    private void updateFailureCount() {
        if (this.totalCount != null) {
            this.failureCount = this.totalCount - this.successCount;
        }
    }

    /**
     * 获取成功率
     */
    public Double getSuccessRate() {
        if (totalCount == null || totalCount == 0) {
            return 0.0;
        }
        return (double) successCount / totalCount * 100;
    }

    /**
     * 是否有失败
     */
    public Boolean hasFailures() {
        return failureCount != null && failureCount > 0;
    }

    /**
     * 是否已完成
     */
    public Boolean isCompleted() {
        return endTime != null;
    }

    /**
     * 获取失败详情列表
     */
    public List<FailureDetail> getFailures() {
        List<FailureDetail> failures = new ArrayList<>();
        if (failureProductIds != null && failureDetails != null) {
            for (int i = 0; i < Math.min(failureProductIds.size(), failureDetails.size()); i++) {
                failures.add(new FailureDetail(failureProductIds.get(i), failureDetails.get(i)));
            }
        }
        return failures;
    }

    /**
     * 失败详情内部类
     */
    public static class FailureDetail {
        private Long productId;
        private String reason;

        public FailureDetail(Long productId, String reason) {
            this.productId = productId;
            this.reason = reason;
        }

        public Long getProductId() {
            return productId;
        }

        public String getReason() {
            return reason;
        }
    }
}
