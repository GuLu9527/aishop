package com.supermarket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 商品DTO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class ProductDTO {

    /**
     * 商品ID（更新时需要）
     */
    private Long id;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String productName;

    /**
     * 商品条码
     */
    private String barcode;

    /**
     * 分类ID
     */
    @NotNull(message = "商品分类不能为空")
    private Long categoryId;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 单位
     */
    @NotBlank(message = "商品单位不能为空")
    private String unit;

    /**
     * 规格
     */
    private String specification;

    /**
     * 进货价
     */
    @DecimalMin(value = "0.01", message = "进货价必须大于0")
    private BigDecimal purchasePrice;

    /**
     * 销售价
     */
    @NotNull(message = "销售价不能为空")
    @DecimalMin(value = "0.01", message = "销售价必须大于0")
    private BigDecimal sellingPrice;

    /**
     * 库存数量
     */
    @Min(value = 0, message = "库存数量不能为负数")
    private Integer stockQuantity;

    /**
     * 最低库存预警
     */
    @Min(value = 0, message = "最低库存不能为负数")
    private Integer minStock;

    /**
     * 最高库存预警
     */
    @Min(value = 0, message = "最高库存不能为负数")
    private Integer maxStock;

    /**
     * 保质期天数
     */
    @Min(value = 1, message = "保质期必须大于0天")
    private Integer shelfLifeDays;

    /**
     * 生产日期
     */
    @PastOrPresent(message = "生产日期不能是未来日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate productionDate;

    /**
     * 是否启用批次管理：1-启用，0-不启用
     */
    private Integer batchTracking;

    /**
     * 是否启用过期日期跟踪：1-启用，0-不启用
     */
    private Integer expiryTracking;

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

    // ==================== 业务验证方法 ====================

    /**
     * 验证生产日期和保质期的业务逻辑
     */
    @AssertTrue(message = "当设置了保质期时，生产日期不能为空")
    public boolean isProductionDateValid() {
        // 如果设置了保质期，生产日期不能为空
        if (shelfLifeDays != null && shelfLifeDays > 0) {
            return productionDate != null;
        }
        return true;
    }

    /**
     * 验证商品是否已过期
     */
    @AssertTrue(message = "商品已过期，不能添加过期商品")
    public boolean isNotExpired() {
        if (productionDate != null && shelfLifeDays != null && shelfLifeDays > 0) {
            LocalDate expirationDate = productionDate.plusDays(shelfLifeDays);
            return !expirationDate.isBefore(LocalDate.now());
        }
        return true;
    }
}
