package com.supermarket.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 销售趋势数据VO
 */
@Data
public class SalesTrendVO {
    
    /**
     * 时间标签列表
     */
    private List<String> dates;
    
    /**
     * 销售额数据
     */
    private List<BigDecimal> salesData;
    
    /**
     * 订单数数据
     */
    private List<Long> ordersData;
    
    /**
     * 时间维度
     */
    private String period;
}
