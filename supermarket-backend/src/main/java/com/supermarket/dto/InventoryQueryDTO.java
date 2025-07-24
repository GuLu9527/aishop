package com.supermarket.dto;

import lombok.Data;

/**
 * 库存查询DTO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class InventoryQueryDTO {

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品条码
     */
    private String barcode;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 库存状态：1-正常，2-预警，3-缺货
     */
    private Integer stockStatus;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 页大小
     */
    private Integer pageSize = 20;
}
