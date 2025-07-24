package com.supermarket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;

/**
 * 销售分析查询DTO
 */
@Data
public class SalesAnalysisDTO {
    
    /**
     * 开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    /**
     * 时间维度：day/week/month
     */
    private String period;
    
    /**
     * 指标类型：sales/orders
     */
    private String metric;
    
    /**
     * 分析类型：hourly/daily
     */
    private String type;
    
    /**
     * 限制数量（用于热销商品排行）
     */
    private Integer limit;
    
    /**
     * 收银员ID（可选）
     */
    private Long cashierId;
    
    /**
     * 商品分类ID（可选）
     */
    private Long categoryId;
}
