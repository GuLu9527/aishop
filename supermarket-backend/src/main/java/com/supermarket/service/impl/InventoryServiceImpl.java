package com.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.dto.InventoryQueryDTO;
import com.supermarket.dto.StockAdjustDTO;

import com.supermarket.dto.BatchOperationResultDTO;
import com.supermarket.dto.BatchStockAdjustDTO;

import com.supermarket.entity.Product;
import com.supermarket.entity.ProductCategory;
import com.supermarket.entity.StockRecord;
import com.supermarket.mapper.ProductMapper;
import com.supermarket.mapper.ProductCategoryMapper;
import com.supermarket.mapper.StockRecordMapper;
import com.supermarket.service.InventoryService;

import com.supermarket.vo.InventoryVO;
import com.supermarket.vo.StockRecordVO;
import com.supermarket.vo.InventoryStatisticsVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java.util.stream.Collectors;

/**
 * 库存服务实现类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductMapper productMapper;
    private final ProductCategoryMapper categoryMapper;
    private final StockRecordMapper stockRecordMapper;

    @Override
    public IPage<InventoryVO> getInventoryPage(InventoryQueryDTO query) {
        // 参数验证
        if (query == null) {
            throw new IllegalArgumentException("查询参数不能为空");
        }

        // 设置默认分页参数
        Integer pageNum = query.getPageNum();
        Integer pageSize = query.getPageSize();
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1 || pageSize > 1000) {
            pageSize = 20;
        }

        try {
            Page<Product> page = new Page<>(pageNum, pageSize);

            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Product::getStatus, 1); // 只查询启用的商品

            // 商品名称模糊查询
            if (StringUtils.hasText(query.getProductName())) {
                wrapper.like(Product::getProductName, query.getProductName().trim());
            }

            // 商品条码查询
            if (StringUtils.hasText(query.getBarcode())) {
                wrapper.like(Product::getBarcode, query.getBarcode().trim());
            }

            // 分类查询
            if (query.getCategoryId() != null && query.getCategoryId() > 0) {
                wrapper.eq(Product::getCategoryId, query.getCategoryId());
            }

            // 按创建时间倒序
            wrapper.orderByDesc(Product::getCreateTime);

            IPage<Product> productPage = productMapper.selectPage(page, wrapper);

            if (productPage == null || productPage.getRecords() == null) {
                Page<InventoryVO> emptyResult = new Page<>(pageNum, pageSize);
                emptyResult.setRecords(new ArrayList<>());
                emptyResult.setTotal(0);
                return emptyResult;
            }

            // 转换为InventoryVO
            List<InventoryVO> inventoryList = productPage.getRecords().stream()
                    .filter(Objects::nonNull)
                    .map(this::convertToInventoryVO)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // 根据库存状态筛选
            if (query.getStockStatus() != null && query.getStockStatus() >= 1 && query.getStockStatus() <= 3) {
                inventoryList = inventoryList.stream()
                        .filter(item -> item.getStockStatus().equals(query.getStockStatus()))
                        .collect(Collectors.toList());
            }

            Page<InventoryVO> result = new Page<>(pageNum, pageSize);
            result.setRecords(inventoryList);
            result.setTotal(productPage.getTotal());

            return result;

        } catch (Exception e) {
            throw new RuntimeException("查询库存列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean adjustStock(StockAdjustDTO adjustDTO, Long operatorId) {
        // 参数验证
        if (adjustDTO == null) {
            throw new IllegalArgumentException("库存调整参数不能为空");
        }
        if (adjustDTO.getProductId() == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        if (adjustDTO.getAdjustType() == null || adjustDTO.getAdjustType() < 1 || adjustDTO.getAdjustType() > 4) {
            throw new IllegalArgumentException("调整类型无效，必须为1-4之间的整数");
        }
        if (adjustDTO.getAdjustQuantity() == null || adjustDTO.getAdjustQuantity() <= 0) {
            throw new IllegalArgumentException("调整数量必须大于0");
        }
        if (!StringUtils.hasText(adjustDTO.getReason())) {
            throw new IllegalArgumentException("调整原因不能为空");
        }
        if (operatorId == null) {
            throw new IllegalArgumentException("操作人ID不能为空");
        }

        // 获取商品信息
        Product product = productMapper.selectById(adjustDTO.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("商品不存在，ID: " + adjustDTO.getProductId());
        }

        if (product.getStatus() != 1) {
            throw new IllegalStateException("商品已停用，无法调整库存");
        }

        // 获取当前库存，确保不为null
        Integer currentStock = product.getStockQuantity();
        if (currentStock == null) {
            currentStock = 0;
            product.setStockQuantity(0);
        }

        Integer adjustQuantity = adjustDTO.getAdjustQuantity();
        Integer newStock;

        // 根据调整类型计算新库存
        switch (adjustDTO.getAdjustType()) {
            case 1: // 入库
                newStock = currentStock + adjustQuantity;
                break;
            case 2: // 出库
                if (currentStock < adjustQuantity) {
                    throw new IllegalStateException(
                        String.format("库存不足，当前库存: %d，尝试出库: %d", currentStock, adjustQuantity)
                    );
                }
                newStock = currentStock - adjustQuantity;
                break;
            case 3: // 盘点调整
                newStock = currentStock + adjustQuantity;
                break;
            case 4: // 损耗
                if (currentStock < adjustQuantity) {
                    throw new IllegalStateException(
                        String.format("库存不足，当前库存: %d，损耗数量: %d", currentStock, adjustQuantity)
                    );
                }
                newStock = currentStock - adjustQuantity;
                break;
            default:
                throw new IllegalArgumentException("无效的调整类型: " + adjustDTO.getAdjustType());
        }

        // 验证新库存不能为负数
        if (newStock < 0) {
            throw new IllegalStateException("调整后库存不能为负数");
        }

        try {
            // 更新商品库存
            product.setStockQuantity(newStock);
            product.setUpdateTime(LocalDateTime.now());
            int updateResult = productMapper.updateById(product);

            if (updateResult != 1) {
                throw new RuntimeException("更新商品库存失败");
            }

            // 记录库存变动
            recordStockChange(
                adjustDTO.getProductId(),
                adjustDTO.getAdjustType(),
                adjustQuantity,
                currentStock,
                newStock,
                adjustDTO.getReason(),
                operatorId
            );

            return true;

        } catch (Exception e) {
            throw new RuntimeException("库存调整失败: " + e.getMessage(), e);
        }
    }

    @Override
    public IPage<StockRecordVO> getStockRecords(Long productId, Integer pageNum, Integer pageSize) {
        Page<StockRecord> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<StockRecord> wrapper = new LambdaQueryWrapper<>();
        if (productId != null) {
            wrapper.eq(StockRecord::getProductId, productId);
        }
        wrapper.orderByDesc(StockRecord::getCreateTime);
        
        IPage<StockRecord> recordPage = stockRecordMapper.selectPage(page, wrapper);
        
        // 转换为StockRecordVO
        List<StockRecordVO> recordList = recordPage.getRecords().stream()
                .map(this::convertToStockRecordVO)
                .collect(Collectors.toList());
        
        Page<StockRecordVO> result = new Page<>(pageNum, pageSize);
        result.setRecords(recordList);
        result.setTotal(recordPage.getTotal());
        
        return result;
    }

    @Override
    public boolean setStockAlert(Long productId, Integer minStock, Integer maxStock) {
        // 参数验证
        if (productId == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        if (minStock == null || minStock < 0) {
            throw new IllegalArgumentException("最低库存不能为空且不能小于0");
        }
        if (maxStock == null || maxStock < 0) {
            throw new IllegalArgumentException("最高库存不能为空且不能小于0");
        }
        if (maxStock <= minStock) {
            throw new IllegalArgumentException("最高库存必须大于最低库存");
        }

        try {
            Product product = productMapper.selectById(productId);
            if (product == null) {
                throw new IllegalArgumentException("商品不存在，ID: " + productId);
            }

            if (product.getStatus() != 1) {
                throw new IllegalStateException("商品已停用，无法设置库存预警");
            }

            product.setMinStock(minStock);
            product.setMaxStock(maxStock);
            product.setUpdateTime(LocalDateTime.now());

            int updateResult = productMapper.updateById(product);
            if (updateResult != 1) {
                throw new RuntimeException("更新商品库存预警失败");
            }

            return true;

        } catch (Exception e) {
            throw new RuntimeException("设置库存预警失败: " + e.getMessage(), e);
        }
    }

    @Override
    public InventoryStatisticsVO getInventoryStatistics() {
        InventoryStatisticsVO statistics = new InventoryStatisticsVO();
        
        // 查询所有启用的商品
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        List<Product> products = productMapper.selectList(wrapper);
        
        statistics.setTotalProducts((long) products.size());
        
        // 统计各种库存状态
        long lowStockCount = products.stream()
                .filter(p -> p.getStockQuantity() != null && p.getMinStock() != null 
                        && p.getStockQuantity() <= p.getMinStock() && p.getStockQuantity() > 0)
                .count();
        
        long outOfStockCount = products.stream()
                .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() == 0)
                .count();
        
        long normalStockCount = products.size() - lowStockCount - outOfStockCount;
        
        statistics.setLowStockCount(lowStockCount);
        statistics.setOutOfStockCount(outOfStockCount);
        statistics.setNormalStockCount(normalStockCount);
        
        return statistics;
    }

    /**
     * 转换为库存VO
     *
     * @param product 商品实体
     * @return 库存VO
     */
    private InventoryVO convertToInventoryVO(Product product) {
        if (product == null) {
            return null;
        }

        try {
            InventoryVO vo = new InventoryVO();
            BeanUtils.copyProperties(product, vo);

            // 设置分类名称
            if (product.getCategoryId() != null && product.getCategoryId() > 0) {
                try {
                    ProductCategory category = categoryMapper.selectById(product.getCategoryId());
                    if (category != null && StringUtils.hasText(category.getCategoryName())) {
                        vo.setCategoryName(category.getCategoryName());
                    } else {
                        vo.setCategoryName("未分类");
                    }
                } catch (Exception e) {
                    vo.setCategoryName("未知分类");
                }
            } else {
                vo.setCategoryName("未分类");
            }

            // 设置库存状态
            Integer stockQuantity = product.getStockQuantity();
            Integer minStock = product.getMinStock();

            // 确保库存数量不为null
            if (stockQuantity == null) {
                stockQuantity = 0;
                vo.setStockQuantity(0);
            }

            // 确保最低库存不为null
            if (minStock == null) {
                minStock = 0;
                vo.setMinStock(0);
            }

            // 计算库存状态
            if (stockQuantity == 0) {
                vo.setStockStatus(3); // 缺货
                vo.setStockStatusText("缺货");
                vo.setIsLowStock(true);
            } else if (stockQuantity <= minStock) {
                vo.setStockStatus(2); // 预警
                vo.setStockStatusText("预警");
                vo.setIsLowStock(true);
            } else {
                vo.setStockStatus(1); // 正常
                vo.setStockStatusText("正常");
                vo.setIsLowStock(false);
            }

            return vo;

        } catch (Exception e) {
            throw new RuntimeException("转换库存VO失败: " + e.getMessage(), e);
        }
    }

    /**
     * 转换为库存记录VO
     */
    private StockRecordVO convertToStockRecordVO(StockRecord record) {
        StockRecordVO vo = new StockRecordVO();
        BeanUtils.copyProperties(record, vo);
        
        // 设置变动类型文本
        switch (record.getChangeType()) {
            case 1:
                vo.setChangeTypeText("入库");
                break;
            case 2:
                vo.setChangeTypeText("出库");
                break;
            case 3:
                vo.setChangeTypeText("盘点");
                break;
            case 4:
                vo.setChangeTypeText("损耗");
                break;
            default:
                vo.setChangeTypeText("未知");
        }
        
        // 设置商品名称
        if (record.getProductId() != null) {
            try {
                Product product = productMapper.selectById(record.getProductId());
                if (product != null && StringUtils.hasText(product.getProductName())) {
                    vo.setProductName(product.getProductName());
                } else {
                    vo.setProductName("未知商品");
                }
            } catch (Exception e) {
                vo.setProductName("获取失败");
            }
        } else {
            vo.setProductName("未知商品");
        }

        // 设置过期日期字符串
        if (record.getExpireDate() != null) {
            vo.setExpireDate(record.getExpireDate().toString());
        }

        // 设置操作人姓名（这里简化处理，实际应该查询用户表）
        if (record.getOperatorId() != null) {
            vo.setOperatorName("操作员" + record.getOperatorId());
        } else {
            vo.setOperatorName("未知操作员");
        }

        return vo;
    }

    /**
     * 记录库存变动
     *
     * @param productId 商品ID
     * @param changeType 变动类型：1-入库，2-出库，3-盘点，4-损耗
     * @param changeQuantity 变动数量
     * @param beforeQuantity 变动前数量
     * @param afterQuantity 变动后数量
     * @param reason 变动原因
     * @param operatorId 操作人ID
     */
    private void recordStockChange(Long productId, Integer changeType, Integer changeQuantity,
                                 Integer beforeQuantity, Integer afterQuantity, String reason, Long operatorId) {

        // 参数验证
        if (productId == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        if (changeType == null || changeType < 1 || changeType > 4) {
            throw new IllegalArgumentException("变动类型无效");
        }
        if (changeQuantity == null || changeQuantity <= 0) {
            throw new IllegalArgumentException("变动数量必须大于0");
        }
        if (beforeQuantity == null || beforeQuantity < 0) {
            throw new IllegalArgumentException("变动前数量不能为负数");
        }
        if (afterQuantity == null || afterQuantity < 0) {
            throw new IllegalArgumentException("变动后数量不能为负数");
        }
        if (!StringUtils.hasText(reason)) {
            throw new IllegalArgumentException("变动原因不能为空");
        }
        if (operatorId == null) {
            throw new IllegalArgumentException("操作人ID不能为空");
        }

        try {
            StockRecord record = new StockRecord();
            record.setProductId(productId);
            record.setChangeType(changeType);
            record.setChangeQuantity(changeQuantity);
            record.setBeforeQuantity(beforeQuantity);
            record.setAfterQuantity(afterQuantity);
            record.setReason(reason);
            record.setOperatorId(operatorId);
            record.setCreateTime(LocalDateTime.now());

            int insertResult = stockRecordMapper.insert(record);
            if (insertResult != 1) {
                throw new RuntimeException("插入库存记录失败");
            }

        } catch (Exception e) {
            throw new RuntimeException("记录库存变动失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchOperationResultDTO batchAdjustStock(BatchStockAdjustDTO batchStockAdjustDTO, Long operatorId) {
        // 参数验证
        if (batchStockAdjustDTO == null) {
            throw new IllegalArgumentException("批量库存调整参数不能为空");
        }
        if (batchStockAdjustDTO.getProductIds() == null || batchStockAdjustDTO.getProductIds().isEmpty()) {
            throw new IllegalArgumentException("商品ID列表不能为空");
        }
        if (batchStockAdjustDTO.getAdjustType() == null || batchStockAdjustDTO.getAdjustType() < 1 || batchStockAdjustDTO.getAdjustType() > 4) {
            throw new IllegalArgumentException("调整类型无效，必须在1-4之间");
        }
        if (!batchStockAdjustDTO.isValidAdjustQuantity()) {
            throw new IllegalArgumentException("调整数量无效，不能为0");
        }
        if (batchStockAdjustDTO.getReason() == null || batchStockAdjustDTO.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("调整原因不能为空");
        }
        if (operatorId == null) {
            throw new IllegalArgumentException("操作人ID不能为空");
        }

        BatchOperationResultDTO result = new BatchOperationResultDTO(batchStockAdjustDTO.getProductIds().size());

        // 获取实际的库存变动数量
        Integer actualChangeQuantity = batchStockAdjustDTO.getActualChangeQuantity();

        for (Long productId : batchStockAdjustDTO.getProductIds()) {
            try {
                StockAdjustDTO adjustDTO = new StockAdjustDTO();
                adjustDTO.setProductId(productId);
                adjustDTO.setAdjustType(batchStockAdjustDTO.getAdjustType());
                adjustDTO.setAdjustQuantity(actualChangeQuantity);
                adjustDTO.setReason(batchStockAdjustDTO.getReason() + " - " + batchStockAdjustDTO.getAdjustTypeDescription());

                boolean success = adjustStock(adjustDTO, operatorId);
                if (success) {
                    result.addSuccess(productId);
                } else {
                    result.addFailure(productId, "库存调整失败");
                }
            } catch (Exception e) {
                log.error("调整商品库存失败: productId={}, adjustType={}, quantity={}",
                         productId, batchStockAdjustDTO.getAdjustType(), actualChangeQuantity, e);
                result.addFailure(productId, "调整异常: " + e.getMessage());
            }
        }

        result.complete();
        log.info("批量库存调整完成: 类型={}, 总数={}, 成功={}, 失败={}",
                batchStockAdjustDTO.getAdjustTypeDescription(),
                batchStockAdjustDTO.getProductIds().size(),
                result.getSuccessCount(),
                result.getFailureCount());

        return result;
    }
}
