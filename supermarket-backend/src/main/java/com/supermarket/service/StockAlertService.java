package com.supermarket.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermarket.dto.StockAlertQueryDTO;
import com.supermarket.entity.StockAlert;
import com.supermarket.vo.StockAlertVO;

import java.util.List;
import java.util.Map;

/**
 * 库存预警服务接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
public interface StockAlertService {

    /**
     * 分页查询库存预警列表
     *
     * @param query 查询条件
     * @return 预警列表
     */
    IPage<StockAlertVO> getAlertPage(StockAlertQueryDTO query);

    /**
     * 检查并生成临期商品预警
     *
     * @return 生成的预警数量
     */
    Integer checkExpiringProducts();

    /**
     * 检查并生成低库存预警
     *
     * @return 生成的预警数量
     */
    Integer checkLowStockProducts();

    /**
     * 处理预警
     *
     * @param alertId 预警ID
     * @param handlerId 处理人ID
     * @param action 处理动作：handle-处理，ignore-忽略
     * @return 是否成功
     */
    boolean handleAlert(Long alertId, Long handlerId, String action);

    /**
     * 批量处理预警
     *
     * @param alertIds 预警ID列表
     * @param handlerId 处理人ID
     * @param action 处理动作：handle-处理，ignore-忽略
     * @return 处理成功的数量
     */
    Integer batchHandleAlerts(List<Long> alertIds, Long handlerId, String action);

    /**
     * 获取预警统计信息
     *
     * @return 统计信息
     */
    Map<String, Object> getAlertStatistics();

    /**
     * 获取未处理预警数量
     *
     * @return 未处理预警数量
     */
    Integer getPendingAlertCount();

    /**
     * 获取临期商品列表
     *
     * @param warningDays 预警天数
     * @return 临期商品列表
     */
    List<StockAlertVO> getExpiringProducts(Integer warningDays);

    /**
     * 清理已处理的历史预警
     *
     * @param retentionDays 保留天数
     * @return 清理数量
     */
    Integer cleanupHandledAlerts(Integer retentionDays);

    /**
     * 创建预警记录
     *
     * @param productId 商品ID
     * @param alertType 预警类型
     * @param message 预警信息
     * @param thresholdValue 阈值
     * @param expireDate 过期日期（临期商品）
     * @return 是否成功
     */
    boolean createAlert(Long productId, Integer alertType, String message, 
                       Integer thresholdValue, String expireDate);

    /**
     * 获取商品的活跃预警
     *
     * @param productId 商品ID
     * @param alertType 预警类型
     * @return 预警记录
     */
    StockAlert getActiveAlert(Long productId, Integer alertType);

    /**
     * 更新预警状态
     *
     * @param alertId 预警ID
     * @param status 新状态
     * @param handlerId 处理人ID
     * @return 是否成功
     */
    boolean updateAlertStatus(Long alertId, Integer status, Long handlerId);
}
