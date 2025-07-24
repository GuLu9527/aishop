package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商品实体类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product")
public class Product {

    /**
     * 商品ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
    @TableField("product_name")
    private String productName;

    /**
     * 商品条码
     */
    @TableField("barcode")
    private String barcode;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 品牌
     */
    @TableField("brand")
    private String brand;

    /**
     * 单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 规格
     */
    @TableField("specification")
    private String specification;

    /**
     * 进货价
     */
    @TableField("purchase_price")
    private BigDecimal purchasePrice;

    /**
     * 销售价
     */
    @TableField("selling_price")
    private BigDecimal sellingPrice;

    /**
     * 库存数量
     */
    @TableField("stock_quantity")
    private Integer stockQuantity;

    /**
     * 最低库存预警
     */
    @TableField("min_stock")
    private Integer minStock;

    /**
     * 最高库存预警
     */
    @TableField("max_stock")
    private Integer maxStock;

    /**
     * 保质期天数
     */
    @TableField("shelf_life_days")
    private Integer shelfLifeDays;

    /**
     * 生产日期
     */
    @TableField("production_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate productionDate;

    /**
     * 是否启用过期日期跟踪：1-启用，0-不启用
     */
    @TableField("expiry_tracking")
    private Integer expiryTracking;

    /**
     * 商品图片
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 商品描述
     */
    @TableField("description")
    private String description;

    /**
     * 生产厂家
     */
    @TableField("manufacturer")
    private String manufacturer;

    /**
     * 状态：1-正常，0-停用
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField("create_by")
    private Long createBy;

    /**
     * 更新人
     */
    @TableField("update_by")
    private Long updateBy;

    // ==================== 业务方法 ====================

    /**
     * 计算过期日期
     * @return 过期日期，如果生产日期或保质期为空则返回null
     */
    public LocalDate getExpirationDate() {
        if (productionDate != null && shelfLifeDays != null && shelfLifeDays > 0) {
            return productionDate.plusDays(shelfLifeDays);
        }
        return null;
    }

    /**
     * 判断是否已过期
     * @return true-已过期，false-未过期
     */
    public boolean isExpired() {
        LocalDate expirationDate = getExpirationDate();
        return expirationDate != null && expirationDate.isBefore(LocalDate.now());
    }

    /**
     * 判断是否临期（指定天数内过期）
     * @param warningDays 预警天数
     * @return true-临期，false-不临期
     */
    public boolean isExpiring(int warningDays) {
        LocalDate expirationDate = getExpirationDate();
        if (expirationDate == null) {
            return false;
        }
        LocalDate warningDate = LocalDate.now().plusDays(warningDays);
        return !expirationDate.isAfter(warningDate) && !isExpired();
    }

    /**
     * 获取剩余保质期天数
     * @return 剩余天数，负数表示已过期，null表示无法计算
     */
    public Integer getRemainingDays() {
        LocalDate expirationDate = getExpirationDate();
        if (expirationDate == null) {
            return null;
        }
        return (int) LocalDate.now().until(expirationDate).getDays();
    }

    /**
     * 判断是否有生产日期和保质期信息
     * @return true-有完整信息，false-信息不完整
     */
    public boolean hasExpirationInfo() {
        return productionDate != null && shelfLifeDays != null && shelfLifeDays > 0;
    }
}
