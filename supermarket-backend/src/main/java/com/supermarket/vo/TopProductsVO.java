package com.supermarket.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 热销商品排行VO
 */
@Data
public class TopProductsVO {
    
    /**
     * 热销商品数据项
     */
    @Data
    public static class TopProductItem {
        /**
         * 商品ID
         */
        private Long productId;
        
        /**
         * 商品名称
         */
        private String productName;
        
        /**
         * 商品条码
         */
        private String barcode;
        
        /**
         * 销售数量
         */
        private Long quantity;
        
        /**
         * 销售额
         */
        private BigDecimal sales;
        
        /**
         * 分类名称
         */
        private String categoryName;
        
        /**
         * 排名
         */
        private Integer rank;
    }
    
    /**
     * 热销商品列表
     */
    private List<TopProductItem> products;
    
    /**
     * 查询限制数量
     */
    private Integer limit;
}
