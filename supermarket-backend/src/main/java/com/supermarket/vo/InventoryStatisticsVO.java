package com.supermarket.vo;

import lombok.Data;

/**
 * 库存统计信息视图对象
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class InventoryStatisticsVO {

    /**
     * 商品总数
     */
    private Long totalProducts;

    /**
     * 库存预警数量
     */
    private Long lowStockCount;

    /**
     * 缺货商品数量
     */
    private Long outOfStockCount;

    /**
     * 正常库存数量
     */
    private Long normalStockCount;
}
