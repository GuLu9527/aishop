package com.supermarket.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品分类销售数据VO
 */
@Data
public class CategorySalesVO {
    
    /**
     * 分类销售数据项
     */
    @Data
    public static class CategorySalesItem {
        /**
         * 分类名称
         */
        private String name;
        
        /**
         * 销售额
         */
        private BigDecimal value;
        
        /**
         * 销售占比（百分比）
         */
        private BigDecimal percentage;
    }
    
    /**
     * 分类销售数据列表
     */
    private List<CategorySalesItem> data;
    
    /**
     * 总销售额
     */
    private BigDecimal totalSales;
}
