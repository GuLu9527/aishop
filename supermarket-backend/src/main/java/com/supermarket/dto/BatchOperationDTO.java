package com.supermarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 批量操作DTO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@Schema(description = "批量操作DTO")
public class BatchOperationDTO {

    /**
     * 操作类型枚举
     */
    public enum OperationType {
        UPDATE_STATUS(1, "批量上下架"),
        UPDATE_CATEGORY(2, "批量分类"),
        UPDATE_PRICE(3, "批量价格"),
        DELETE(4, "批量删除"),
        STOCK_ADJUST(5, "批量库存调整");

        private final Integer code;
        private final String description;

        OperationType(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static OperationType fromCode(Integer code) {
            for (OperationType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("未知的操作类型: " + code);
        }
    }

    /**
     * 价格调整类型枚举
     */
    public enum PriceAdjustmentType {
        FIXED(1, "固定价格"),
        PERCENTAGE(2, "百分比调整"),
        AMOUNT(3, "金额调整");

        private final Integer code;
        private final String description;

        PriceAdjustmentType(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static PriceAdjustmentType fromCode(Integer code) {
            for (PriceAdjustmentType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("未知的价格调整类型: " + code);
        }
    }

    @Schema(description = "商品ID列表")
    @NotEmpty(message = "商品ID列表不能为空")
    private List<Long> productIds;

    @Schema(description = "操作类型")
    @NotNull(message = "操作类型不能为空")
    private OperationType operationType;

    @Schema(description = "新状态（上下架操作时使用）")
    private Integer newStatus;

    @Schema(description = "新分类ID（分类操作时使用）")
    private Long newCategoryId;

    @Schema(description = "价格调整类型（价格操作时使用）")
    private PriceAdjustmentType priceAdjustmentType;

    @Schema(description = "价格调整值（价格操作时使用）")
    private BigDecimal priceAdjustmentValue;

    @Schema(description = "操作原因")
    private String reason;

    // 库存调整相关字段
    @Schema(description = "库存调整类型（1-入库，2-出库，3-盘点，4-调整）")
    private Integer adjustType;

    @Schema(description = "调整数量")
    private Integer adjustQuantity;
}
