package com.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.dto.StockAlertQueryDTO;
import com.supermarket.entity.Product;
import com.supermarket.entity.StockAlert;
import com.supermarket.mapper.ProductMapper;
import com.supermarket.mapper.StockAlertMapper;
import com.supermarket.service.StockAlertService;
import com.supermarket.service.SysConfigService;
import com.supermarket.vo.StockAlertVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 库存预警服务实现类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Slf4j
@Service
public class StockAlertServiceImpl extends ServiceImpl<StockAlertMapper, StockAlert> implements StockAlertService {

    @Autowired
    private StockAlertMapper stockAlertMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private SysConfigService sysConfigService;

    @Override
    public IPage<StockAlertVO> getAlertPage(StockAlertQueryDTO query) {
        Page<StockAlertVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        
        IPage<StockAlertVO> result = stockAlertMapper.selectAlertPage(
            page, 
            query.getAlertType(), 
            query.getStatus(), 
            query.getProductName()
        );

        // 计算剩余天数和紧急程度
        result.getRecords().forEach(alert -> {
            if (alert.getExpireDate() != null) {
                long days = ChronoUnit.DAYS.between(LocalDate.now(), alert.getExpireDate());
                alert.setRemainingDays((int) days);
            }
        });

        return result;
    }

    @Override
    @Transactional
    public Integer checkExpiringProducts() {
        try {
            // 获取临期预警天数配置
            String warningDaysStr = sysConfigService.getConfigValue("expire.warning.days", "7");
            Integer warningDays = Integer.parseInt(warningDaysStr);

            log.info("开始检查临期商品，预警天数: {}", warningDays);

            // 查询临期商品
            List<StockAlertVO> expiringProducts = stockAlertMapper.selectExpiringProducts(warningDays);

            int alertCount = 0;
            for (StockAlertVO product : expiringProducts) {
                // 检查是否已存在相同的预警
                StockAlert existingAlert = getActiveAlert(product.getProductId(),
                    StockAlert.AlertType.EXPIRING_SOON.getCode());

                if (existingAlert == null) {
                    // 创建新的临期预警
                    long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), product.getExpireDate());
                    String message = String.format("商品 %s 将在 %d 天后过期（%s）",
                        product.getProductName(), remainingDays, product.getExpireDate());

                    boolean created = createAlert(
                        product.getProductId(),
                        StockAlert.AlertType.EXPIRING_SOON.getCode(),
                        message,
                        warningDays,
                        product.getExpireDate().toString()
                    );

                    if (created) {
                        alertCount++;
                        log.info("创建临期商品预警: 商品ID={}, 过期日期={}",
                            product.getProductId(), product.getExpireDate());
                    }
                }
            }

            log.info("临期商品检查完成，共生成 {} 个预警", alertCount);
            return alertCount;

        } catch (Exception e) {
            log.error("检查临期商品失败", e);
            throw new RuntimeException("检查临期商品失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Integer checkLowStockProducts() {
        try {
            log.info("开始检查低库存商品");

            // 查询所有在售商品
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Product::getStatus, 1)
                   .isNotNull(Product::getMinStock)
                   .gt(Product::getMinStock, 0);
            
            List<Product> products = productMapper.selectList(wrapper);
            
            int alertCount = 0;
            for (Product product : products) {
                // 检查库存是否低于预警线
                if (product.getStockQuantity() != null && 
                    product.getStockQuantity() <= product.getMinStock()) {
                    
                    // 检查是否已存在相同的预警
                    StockAlert existingAlert = getActiveAlert(product.getId(), 
                        StockAlert.AlertType.LOW_STOCK.getCode());
                    
                    if (existingAlert == null) {
                        // 创建新的低库存预警
                        String message = String.format("商品 %s 库存不足，当前库存: %d，最低库存: %d", 
                            product.getProductName(), product.getStockQuantity(), product.getMinStock());
                        
                        boolean created = createAlert(
                            product.getId(),
                            StockAlert.AlertType.LOW_STOCK.getCode(),
                            message,
                            product.getMinStock(),
                            null
                        );
                        
                        if (created) {
                            alertCount++;
                            log.info("创建低库存预警: 商品ID={}, 当前库存={}, 最低库存={}", 
                                product.getId(), product.getStockQuantity(), product.getMinStock());
                        }
                    }
                }
            }

            log.info("低库存检查完成，共生成 {} 个预警", alertCount);
            return alertCount;

        } catch (Exception e) {
            log.error("检查低库存商品失败", e);
            throw new RuntimeException("检查低库存商品失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean handleAlert(Long alertId, Long handlerId, String action) {
        try {
            StockAlert alert = this.getById(alertId);
            if (alert == null) {
                log.warn("预警记录不存在: {}", alertId);
                return false;
            }

            Integer newStatus;
            switch (action.toLowerCase()) {
                case "handle":
                    newStatus = StockAlert.AlertStatus.HANDLED.getCode();
                    break;
                case "ignore":
                    newStatus = StockAlert.AlertStatus.IGNORED.getCode();
                    break;
                default:
                    log.warn("无效的处理动作: {}", action);
                    return false;
            }

            return updateAlertStatus(alertId, newStatus, handlerId);

        } catch (Exception e) {
            log.error("处理预警失败: alertId={}, action={}", alertId, action, e);
            return false;
        }
    }

    @Override
    @Transactional
    public Integer batchHandleAlerts(List<Long> alertIds, Long handlerId, String action) {
        int successCount = 0;
        for (Long alertId : alertIds) {
            if (handleAlert(alertId, handlerId, action)) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public Map<String, Object> getAlertStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 获取各类型预警数量
        List<StockAlertVO> alertStats = stockAlertMapper.selectAlertStatistics();
        Map<Integer, Integer> alertCounts = alertStats.stream()
            .collect(Collectors.toMap(StockAlertVO::getAlertType, StockAlertVO::getCount));
        
        statistics.put("lowStockCount", alertCounts.getOrDefault(1, 0));
        statistics.put("highStockCount", alertCounts.getOrDefault(2, 0));
        statistics.put("expiringCount", alertCounts.getOrDefault(3, 0));
        statistics.put("totalPendingCount", getPendingAlertCount());
        
        return statistics;
    }

    @Override
    public Integer getPendingAlertCount() {
        return stockAlertMapper.countPendingAlerts();
    }

    @Override
    public List<StockAlertVO> getExpiringProducts(Integer warningDays) {
        if (warningDays == null) {
            String warningDaysStr = sysConfigService.getConfigValue("expire.warning.days", "7");
            warningDays = Integer.parseInt(warningDaysStr);
        }

        List<StockAlertVO> products = stockAlertMapper.selectExpiringProducts(warningDays);

        // 计算剩余天数
        products.forEach(product -> {
            if (product.getExpireDate() != null) {
                long days = ChronoUnit.DAYS.between(LocalDate.now(), product.getExpireDate());
                product.setRemainingDays((int) days);
            }
        });

        return products;
    }

    @Override
    @Transactional
    public Integer cleanupHandledAlerts(Integer retentionDays) {
        if (retentionDays == null) {
            retentionDays = 30; // 默认保留30天
        }
        
        try {
            Integer deletedCount = stockAlertMapper.deleteHandledAlerts(retentionDays);
            log.info("清理已处理预警记录完成，删除 {} 条记录", deletedCount);
            return deletedCount;
        } catch (Exception e) {
            log.error("清理已处理预警记录失败", e);
            throw new RuntimeException("清理预警记录失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean createAlert(Long productId, Integer alertType, String message, 
                              Integer thresholdValue, String expireDate) {
        try {
            StockAlert alert = new StockAlert();
            alert.setProductId(productId);
            alert.setAlertType(alertType);
            alert.setAlertMessage(message);
            alert.setThresholdValue(thresholdValue);
            alert.setStatus(StockAlert.AlertStatus.PENDING.getCode());
            
            if (expireDate != null) {
                alert.setExpireDate(LocalDate.parse(expireDate));
            }
            
            // 获取当前库存
            Product product = productMapper.selectById(productId);
            if (product != null) {
                alert.setCurrentStock(product.getStockQuantity());
            }
            
            return this.save(alert);
            
        } catch (Exception e) {
            log.error("创建预警记录失败: productId={}, alertType={}", productId, alertType, e);
            return false;
        }
    }

    @Override
    public StockAlert getActiveAlert(Long productId, Integer alertType) {
        LambdaQueryWrapper<StockAlert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StockAlert::getProductId, productId)
               .eq(StockAlert::getAlertType, alertType)
               .eq(StockAlert::getStatus, StockAlert.AlertStatus.PENDING.getCode())
               .orderByDesc(StockAlert::getCreateTime)
               .last("LIMIT 1");

        return this.getOne(wrapper);
    }



    @Override
    @Transactional
    public boolean updateAlertStatus(Long alertId, Integer status, Long handlerId) {
        try {
            StockAlert alert = new StockAlert();
            alert.setId(alertId);
            alert.setStatus(status);
            alert.setHandlerId(handlerId);
            alert.setHandleTime(LocalDateTime.now());
            
            return this.updateById(alert);
            
        } catch (Exception e) {
            log.error("更新预警状态失败: alertId={}, status={}", alertId, status, e);
            return false;
        }
    }
}
