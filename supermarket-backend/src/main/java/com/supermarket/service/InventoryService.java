package com.supermarket.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermarket.dto.InventoryQueryDTO;
import com.supermarket.dto.StockAdjustDTO;
import com.supermarket.dto.BatchOperationResultDTO;
import com.supermarket.dto.BatchStockAdjustDTO;

import com.supermarket.vo.InventoryVO;
import com.supermarket.vo.StockRecordVO;
import com.supermarket.vo.InventoryStatisticsVO;


/**
 * 库存服务接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
public interface InventoryService {

    /**
     * 分页查询库存列表
     *
     * @param query 查询条件
     * @return 库存列表
     */
    IPage<InventoryVO> getInventoryPage(InventoryQueryDTO query);

    /**
     * 调整库存
     *
     * @param adjustDTO 调整信息
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    boolean adjustStock(StockAdjustDTO adjustDTO, Long operatorId);

    /**
     * 获取库存记录
     *
     * @param productId 商品ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 库存记录列表
     */
    IPage<StockRecordVO> getStockRecords(Long productId, Integer pageNum, Integer pageSize);

    /**
     * 设置库存预警
     *
     * @param productId 商品ID
     * @param minStock 最低库存
     * @param maxStock 最高库存
     * @return 是否成功
     */
    boolean setStockAlert(Long productId, Integer minStock, Integer maxStock);

    /**
     * 获取库存统计信息
     *
     * @return 统计信息
     */
    InventoryStatisticsVO getInventoryStatistics();

    /**
     * 批量调整库存
     *
     * @param batchStockAdjustDTO 批量库存调整DTO
     * @param operatorId 操作人ID
     * @return 批量操作结果
     */
    BatchOperationResultDTO batchAdjustStock(BatchStockAdjustDTO batchStockAdjustDTO, Long operatorId);
}
