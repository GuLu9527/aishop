package com.supermarket.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 收银员业绩对比VO
 */
@Data
public class CashierPerformanceVO {
    
    /**
     * 收银员业绩数据项
     */
    @Data
    public static class CashierPerformanceItem {
        /**
         * 收银员ID
         */
        private Long cashierId;
        
        /**
         * 收银员姓名
         */
        private String cashierName;
        
        /**
         * 销售额
         */
        private BigDecimal sales;
        
        /**
         * 订单数
         */
        private Long orders;
        
        /**
         * 平均客单价
         */
        private BigDecimal avgOrderValue;
        
        /**
         * 排名
         */
        private Integer rank;
    }
    
    /**
     * 收银员业绩列表
     */
    private List<CashierPerformanceItem> cashiers;
    
    /**
     * 指标类型：sales/orders
     */
    private String metric;
}
