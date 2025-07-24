package com.supermarket.vo;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商品VO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class ProductVO {

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
     * 进货价
     */
    private BigDecimal purchasePrice;

    /**
     * 销售价
     */
    private BigDecimal sellingPrice;

    /**
     * 库存数量
     */
    private Integer stockQuantity;

    /**
     * 最低库存预警
     */
    private Integer minStock;

    /**
     * 最高库存预警
     */
    private Integer maxStock;

    /**
     * 保质期天数
     */
    private Integer shelfLifeDays;

    /**
     * 生产日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate productionDate;

    /**
     * 过期日期（计算字段）
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    /**
     * 剩余保质期天数（计算字段）
     */
    private Integer remainingDays;

    /**
     * 是否已过期（计算字段）
     */
    private Boolean isExpired;

    /**
     * 是否临期（计算字段）
     */
    private Boolean isExpiring;

    /**
     * 是否启用批次管理：1-启用，0-不启用
     */
    private Integer batchTracking;

    /**
     * 是否启用过期日期跟踪：1-启用，0-不启用
     */
    private Integer expiryTracking;

    /**
     * 批次管理状态文本
     */
    private String batchTrackingText;

    /**
     * 商品图片
     */
    private String imageUrl;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 生产厂家
     */
    private String manufacturer;

    /**
     * 状态：1-正常，0-停用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 是否低库存
     */
    private Boolean isLowStock;

    // ==================== 业务方法 ====================

    /**
     * 设置计算字段的值
     * 在Service层转换时调用
     */
    public void calculateExpirationInfo() {
        if (productionDate != null && shelfLifeDays != null && shelfLifeDays > 0) {
            // 计算过期日期
            this.expirationDate = productionDate.plusDays(shelfLifeDays);

            // 计算剩余天数
            LocalDate now = LocalDate.now();
            this.remainingDays = (int) now.until(expirationDate).getDays();

            // 判断是否过期
            this.isExpired = expirationDate.isBefore(now);

            // 判断是否临期（7天内过期）
            this.isExpiring = !isExpired && remainingDays <= 7 && remainingDays >= 0;
        } else {
            this.expirationDate = null;
            this.remainingDays = null;
            this.isExpired = false;
            this.isExpiring = false;
        }
    }
}
