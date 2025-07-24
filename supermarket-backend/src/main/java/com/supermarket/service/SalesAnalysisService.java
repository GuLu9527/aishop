package com.supermarket.service;

import com.supermarket.dto.SalesAnalysisDTO;
import com.supermarket.vo.*;
import java.util.List;
import java.util.Map;

/**
 * 销售分析服务接口
 */
public interface SalesAnalysisService {
    
    /**
     * 获取销售概览数据
     * @param dto 查询条件
     * @return 销售概览数据
     */
    SalesOverviewVO getSalesOverview(SalesAnalysisDTO dto);
    
    /**
     * 获取销售趋势数据
     * @param dto 查询条件
     * @return 销售趋势数据
     */
    SalesTrendVO getSalesTrend(SalesAnalysisDTO dto);
    
    /**
     * 获取商品分类销售数据
     * @param dto 查询条件
     * @return 分类销售数据
     */
    CategorySalesVO getCategorySales(SalesAnalysisDTO dto);
    
    /**
     * 获取热销商品排行
     * @param dto 查询条件
     * @return 热销商品排行
     */
    TopProductsVO getTopProducts(SalesAnalysisDTO dto);
    
    /**
     * 获取收银员业绩对比
     * @param dto 查询条件
     * @return 收银员业绩数据
     */
    CashierPerformanceVO getCashierPerformance(SalesAnalysisDTO dto);
    
    /**
     * 获取时段销售分析
     * @param dto 查询条件
     * @return 时段分析数据
     */
    TimeAnalysisVO getTimeAnalysis(SalesAnalysisDTO dto);

    /**
     * 导出销售报表
     * @param dto 查询条件
     * @return 导出数据
     */
    List<Map<String, Object>> exportSalesReport(SalesAnalysisDTO dto);

    /**
     * 获取实时销售数据
     * @return 实时销售数据
     */
    Map<String, Object> getRealTimeSales();

    /**
     * 获取销售预测数据
     * @param days 预测天数
     * @return 预测数据
     */
    List<Map<String, Object>> getSalesForecast(Integer days);

    /**
     * 获取商品库存预警
     * @return 库存预警数据
     */
    List<Map<String, Object>> getStockAlert();
}
