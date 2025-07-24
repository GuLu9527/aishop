package com.supermarket.dto;

import lombok.Data;

import jakarta.validation.constraints.Min;

/**
 * 库存预警查询DTO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class StockAlertQueryDTO {

    /**
     * 页码
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    /**
     * 页大小
     */
    @Min(value = 1, message = "页大小必须大于0")
    private Integer pageSize = 10;

    /**
     * 预警类型：1-低库存，2-高库存，3-临期商品
     */
    private Integer alertType;

    /**
     * 预警状态：0-已忽略，1-未处理，2-已处理
     */
    private Integer status;

    /**
     * 商品名称（模糊查询）
     */
    private String productName;

    /**
     * 紧急程度：1-低，2-中，3-高
     */
    private Integer urgencyLevel;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}
