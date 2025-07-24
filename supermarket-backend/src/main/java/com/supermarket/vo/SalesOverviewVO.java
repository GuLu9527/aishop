package com.supermarket.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 销售概览数据VO
 */
@Data
public class SalesOverviewVO {
    
    /**
     * 总销售额
     */
    private BigDecimal totalSales;
    
    /**
     * 总订单数
     */
    private Long totalOrders;
    
    /**
     * 总商品销量
     */
    private Long totalProducts;
    
    /**
     * 平均客单价
     */
    private BigDecimal avgOrderValue;
    
    /**
     * 销售额趋势（百分比）
     */
    private BigDecimal salesTrend;
    
    /**
     * 订单数趋势（百分比）
     */
    private BigDecimal ordersTrend;
    
    /**
     * 商品销量趋势（百分比）
     */
    private BigDecimal productsTrend;
    
    /**
     * 客单价趋势（百分比）
     */
    private BigDecimal avgTrend;
}
