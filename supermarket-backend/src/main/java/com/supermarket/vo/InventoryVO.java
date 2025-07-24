package com.supermarket.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 库存视图对象
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class InventoryVO {

    /**
     * 商品ID
     */
    private Long id;

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
     * 分类名称
     */
    private String categoryName;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 单位
     */
    private String unit;

    /**
     * 规格
     */
    private String specification;

    /**
     * 当前库存数量
     */
    private Integer stockQuantity;

    /**
     * 最低库存
     */
    private Integer minStock;

    /**
     * 最高库存
     */
    private Integer maxStock;

    /**
     * 进货价
     */
    private BigDecimal purchasePrice;

    /**
     * 销售价
     */
    private BigDecimal sellingPrice;

    /**
     * 库存状态：1-正常，2-预警，3-缺货
     */
    private Integer stockStatus;

    /**
     * 库存状态文本
     */
    private String stockStatusText;

    /**
     * 是否低库存
     */
    private Boolean isLowStock;
}
