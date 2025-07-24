package com.supermarket.dto;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 库存调整DTO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class StockAdjustDTO {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 调整类型：1-入库，2-出库，3-盘点，4-损耗
     */
    @NotNull(message = "调整类型不能为空")
    private Integer adjustType;

    /**
     * 调整数量
     */
    @NotNull(message = "调整数量不能为空")
    @Min(value = 1, message = "调整数量必须大于0")
    private Integer adjustQuantity;

    /**
     * 调整原因
     */
    @NotNull(message = "调整原因不能为空")
    private String reason;
}
