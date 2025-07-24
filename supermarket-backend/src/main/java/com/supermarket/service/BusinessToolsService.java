package com.supermarket.service;

import java.util.Map;

/**
 * 业务工具服务接口
 * 为AI提供各种业务操作能力
 */
public interface BusinessToolsService {
    
    /**
     * 执行业务操作
     * @param action 操作类型
     * @param params 操作参数
     * @param userId 用户ID
     * @return 操作结果
     */
    Map<String, Object> executeAction(String action, Map<String, Object> params, Long userId);
    
    /**
     * 查询销售数据
     * @param timeRange 时间范围
     * @param productName 商品名称（可选）
     * @return 销售数据
     */
    Map<String, Object> querySalesData(String timeRange, String productName);
    
    /**
     * 检查库存状态
     * @param productName 商品名称（可选）
     * @return 库存信息
     */
    Map<String, Object> checkInventoryStatus(String productName);
    
    /**
     * 添加商品
     * @param productInfo 商品信息
     * @param userId 用户ID
     * @return 添加结果
     */
    Map<String, Object> addProduct(Map<String, Object> productInfo, Long userId);
    
    /**
     * 更新商品价格
     * @param productName 商品名称
     * @param newPrice 新价格
     * @param userId 用户ID
     * @return 更新结果
     */
    Map<String, Object> updateProductPrice(String productName, Double newPrice, Long userId);
    
    /**
     * 获取低库存预警
     * @return 预警信息
     */
    Map<String, Object> getLowStockAlert();
    
    /**
     * 获取销售排行
     * @param timeRange 时间范围
     * @param limit 限制数量
     * @return 排行数据
     */
    Map<String, Object> getSalesRanking(String timeRange, Integer limit);
    
    /**
     * 获取财务概况
     * @param timeRange 时间范围
     * @return 财务数据
     */
    Map<String, Object> getFinancialOverview(String timeRange);
}