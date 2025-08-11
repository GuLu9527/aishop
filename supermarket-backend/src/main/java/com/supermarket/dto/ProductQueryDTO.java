package com.supermarket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

/**
 * 商品查询DTO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class ProductQueryDTO {

    /**
     * 商品名称（模糊查询）
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
     * 品牌
     */
    private String brand;

    /**
     * 状态：1-正常，0-停用
     */
    private Integer status;

    /**
     * 是否低库存预警
     */
    private Boolean lowStock;

    /**
     * 生产日期开始（查询范围）
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate productionDateStart;

    /**
     * 生产日期结束（查询范围）
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate productionDateEnd;

    /**
     * 是否查询临期商品
     */
    private Boolean expiring;

    /**
     * 临期预警天数（配合expiring使用）
     */
    @Min(value = 1, message = "预警天数必须大于0")
    private Integer expiringDays = 7;

    /**
     * 最低价格
     */
    private java.math.BigDecimal minPrice;

    /**
     * 最高价格
     */
    private java.math.BigDecimal maxPrice;

    /**
     * 库存状态：low-低库存，normal-正常库存，high-高库存
     */
    private String stockStatus;

    /**
     * 过期状态：expired-已过期，expiring-临期，normal-正常
     */
    private String expiryStatus;

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}
