package com.supermarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.List;

/**
 * 批量库存调整DTO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@Schema(description = "批量库存调整DTO")
public class BatchStockAdjustDTO {

    @Schema(description = "商品ID列表", required = true)
    @NotEmpty(message = "商品ID列表不能为空")
    private List<Long> productIds;

    @Schema(description = "调整类型：1-入库，2-出库，3-盘点，4-调整", required = true)
    @NotNull(message = "调整类型不能为空")
    @Min(value = 1, message = "调整类型必须在1-4之间")
    @Max(value = 4, message = "调整类型必须在1-4之间")
    private Integer adjustType;

    @Schema(description = "调整数量", required = true)
    @NotNull(message = "调整数量不能为空")
    private Integer adjustQuantity;

    @Schema(description = "调整原因", required = true)
    @NotEmpty(message = "调整原因不能为空")
    private String reason;

    /**
     * 获取调整类型描述
     */
    public String getAdjustTypeDescription() {
        switch (adjustType) {
            case 1:
                return "入库";
            case 2:
                return "出库";
            case 3:
                return "盘点";
            case 4:
                return "调整";
            default:
                return "未知";
        }
    }

    /**
     * 验证调整数量是否合理
     */
    public boolean isValidAdjustQuantity() {
        if (adjustQuantity == null) {
            return false;
        }
        
        // 出库时数量应该为负数或者通过业务逻辑处理
        // 这里允许正数，在业务逻辑中根据类型处理正负
        return adjustQuantity != 0;
    }

    /**
     * 获取实际的库存变动数量
     * 根据调整类型返回正确的数量（正数表示增加，负数表示减少）
     */
    public Integer getActualChangeQuantity() {
        if (adjustQuantity == null) {
            return 0;
        }
        
        switch (adjustType) {
            case 1: // 入库
            case 4: // 调整（可正可负，由用户输入决定）
                return adjustQuantity;
            case 2: // 出库
                return -Math.abs(adjustQuantity); // 确保出库是负数
            case 3: // 盘点
                return adjustQuantity; // 盘点可能是正数或负数
            default:
                return adjustQuantity;
        }
    }
}
