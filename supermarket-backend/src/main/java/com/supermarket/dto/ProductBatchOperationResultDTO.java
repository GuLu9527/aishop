package com.supermarket.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品批量操作结果DTO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductBatchOperationResultDTO {

    /**
     * 操作是否成功
     */
    private Boolean success;

    /**
     * 总操作数量
     */
    private Integer totalCount;

    /**
     * 成功操作数量
     */
    private Integer successCount;

    /**
     * 失败操作数量
     */
    private Integer failureCount;

    /**
     * 成功的商品ID列表
     */
    private List<Long> successProductIds;

    /**
     * 失败的商品ID列表
     */
    private List<Long> failureProductIds;

    /**
     * 失败原因列表
     */
    private List<String> failureReasons;

    /**
     * 操作详细信息
     */
    private String message;

    /**
     * 操作类型
     */
    private ProductBatchOperationDTO.OperationType operationType;

    /**
     * 创建成功结果
     */
    public static ProductBatchOperationResultDTO success(
            ProductBatchOperationDTO.OperationType operationType,
            int totalCount,
            int successCount,
            List<Long> successProductIds) {
        return ProductBatchOperationResultDTO.builder()
                .success(true)
                .operationType(operationType)
                .totalCount(totalCount)
                .successCount(successCount)
                .failureCount(0)
                .successProductIds(successProductIds)
                .message("批量操作成功")
                .build();
    }

    /**
     * 创建部分成功结果
     */
    public static ProductBatchOperationResultDTO partialSuccess(
            ProductBatchOperationDTO.OperationType operationType,
            int totalCount,
            int successCount,
            int failureCount,
            List<Long> successProductIds,
            List<Long> failureProductIds,
            List<String> failureReasons) {
        return ProductBatchOperationResultDTO.builder()
                .success(false)
                .operationType(operationType)
                .totalCount(totalCount)
                .successCount(successCount)
                .failureCount(failureCount)
                .successProductIds(successProductIds)
                .failureProductIds(failureProductIds)
                .failureReasons(failureReasons)
                .message(String.format("批量操作部分成功：成功%d个，失败%d个", successCount, failureCount))
                .build();
    }

    /**
     * 创建失败结果
     */
    public static ProductBatchOperationResultDTO failure(
            ProductBatchOperationDTO.OperationType operationType,
            int totalCount,
            List<Long> failureProductIds,
            List<String> failureReasons) {
        return ProductBatchOperationResultDTO.builder()
                .success(false)
                .operationType(operationType)
                .totalCount(totalCount)
                .successCount(0)
                .failureCount(totalCount)
                .failureProductIds(failureProductIds)
                .failureReasons(failureReasons)
                .message("批量操作失败")
                .build();
    }
}
