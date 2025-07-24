package com.supermarket.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品批量操作DTO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class ProductBatchOperationDTO {

    /**
     * 操作类型枚举
     */
    public enum OperationType {
        UPDATE_STATUS,      // 批量更新状态
        UPDATE_CATEGORY,    // 批量更新分类
        UPDATE_PRICE,       // 批量更新价格
        UPDATE_STOCK,       // 批量更新库存
        DELETE,             // 批量删除
        EXPORT              // 批量导出
    }

    /**
     * 商品ID列表
     */
    @NotEmpty(message = "商品ID列表不能为空")
    private List<Long> productIds;

    /**
     * 操作类型
     */
    @NotNull(message = "操作类型不能为空")
    private OperationType operationType;

    /**
     * 新状态（用于批量更新状态）
     * 1-启用，0-禁用
     */
    private Integer newStatus;

    /**
     * 新分类ID（用于批量更新分类）
     */
    private Long newCategoryId;

    /**
     * 价格调整类型（用于批量更新价格）
     * FIXED - 固定价格
     * PERCENTAGE - 百分比调整
     * AMOUNT - 金额调整
     */
    private PriceAdjustmentType priceAdjustmentType;

    /**
     * 价格调整值
     */
    private BigDecimal priceAdjustmentValue;

    /**
     * 库存调整类型（用于批量更新库存）
     * SET - 设置为指定值
     * ADD - 增加指定数量
     * SUBTRACT - 减少指定数量
     */
    private StockAdjustmentType stockAdjustmentType;

    /**
     * 库存调整值
     */
    private Integer stockAdjustmentValue;

    /**
     * 操作原因/备注
     */
    private String reason;

    /**
     * 价格调整类型枚举
     */
    public enum PriceAdjustmentType {
        FIXED,      // 固定价格
        PERCENTAGE, // 百分比调整
        AMOUNT      // 金额调整
    }

    /**
     * 库存调整类型枚举
     */
    public enum StockAdjustmentType {
        SET,        // 设置为指定值
        ADD,        // 增加指定数量
        SUBTRACT    // 减少指定数量
    }
}
