package com.supermarket.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 时段销售分析VO
 */
@Data
public class TimeAnalysisVO {
    
    /**
     * 时段销售数据项
     */
    @Data
    public static class TimeAnalysisItem {
        /**
         * 时间标签
         */
        private String timeLabel;
        
        /**
         * 销售额
         */
        private BigDecimal sales;
        
        /**
         * 订单数
         */
        private Long orders;
        
        /**
         * 时间排序值（用于排序）
         */
        private Integer timeOrder;
    }
    
    /**
     * 时段销售数据列表
     */
    private List<TimeAnalysisItem> data;
    
    /**
     * 分析类型：hourly/daily
     */
    private String type;
    
    /**
     * 峰值时段
     */
    private String peakTime;
    
    /**
     * 峰值销售额
     */
    private BigDecimal peakSales;
}
