package com.supermarket.mapper;

import com.supermarket.dto.SalesAnalysisDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 销售分析数据访问层
 * 使用MyBatis XML映射文件管理SQL语句，符合最佳实践
 */
@Mapper
public interface SalesAnalysisMapper {
    
    /**
     * 获取销售概览数据
     * @param dto 查询条件
     * @return 销售概览数据
     */
    Map<String, Object> getSalesOverview(SalesAnalysisDTO dto);
    
    /**
     * 获取销售趋势数据
     * @param dto 查询条件
     * @return 销售趋势数据列表
     */
    List<Map<String, Object>> getSalesTrend(SalesAnalysisDTO dto);
    
    /**
     * 获取商品分类销售数据
     * @param dto 查询条件
     * @return 分类销售数据列表
     */
    List<Map<String, Object>> getCategorySales(SalesAnalysisDTO dto);
    
    /**
     * 获取热销商品排行
     * @param dto 查询条件
     * @return 热销商品数据列表
     */
    List<Map<String, Object>> getTopProducts(SalesAnalysisDTO dto);
    
    /**
     * 获取收银员业绩数据
     * @param dto 查询条件
     * @return 收银员业绩数据列表
     */
    List<Map<String, Object>> getCashierPerformance(SalesAnalysisDTO dto);
    
    /**
     * 获取时段分析数据
     * @param dto 查询条件
     * @return 时段分析数据列表
     */
    List<Map<String, Object>> getTimeAnalysis(SalesAnalysisDTO dto);

    /**
     * 获取销售详情数据用于导出
     * @param dto 查询条件
     * @return 销售详情数据列表
     */
    List<Map<String, Object>> getSalesDetailForExport(SalesAnalysisDTO dto);

    /**
     * 获取当前小时销售数据
     * @return 当前小时销售数据
     */
    Map<String, Object> getCurrentHourSales();

    /**
     * 获取最近订单
     * @param limit 限制数量
     * @return 最近订单列表
     */
    List<Map<String, Object>> getRecentOrders(@Param("limit") Integer limit);

    /**
     * 获取库存预警数据
     * @return 库存预警数据列表
     */
    List<Map<String, Object>> getStockAlert();
}
