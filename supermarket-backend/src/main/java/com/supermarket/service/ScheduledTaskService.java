package com.supermarket.service;

import com.supermarket.service.StockAlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 定时任务服务
 * 基于Spring Boot的@Scheduled注解实现定时任务
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Slf4j
@Service
public class ScheduledTaskService {

    @Autowired
    private StockAlertService stockAlertService;

    /**
     * 检查临期商品预警
     * 每天早上8点执行
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void checkExpiringProducts() {
        log.info("开始执行定时任务：检查临期商品预警");
        
        try {
            Integer alertCount = stockAlertService.checkExpiringProducts();
            log.info("临期商品预警检查完成，生成 {} 个预警", alertCount);
            
        } catch (Exception e) {
            log.error("临期商品预警检查失败", e);
        }
    }

    /**
     * 检查低库存预警
     * 每天早上8点30分执行
     */
    @Scheduled(cron = "0 30 8 * * ?")
    public void checkLowStockProducts() {
        log.info("开始执行定时任务：检查低库存预警");
        
        try {
            Integer alertCount = stockAlertService.checkLowStockProducts();
            log.info("低库存预警检查完成，生成 {} 个预警", alertCount);
            
        } catch (Exception e) {
            log.error("低库存预警检查失败", e);
        }
    }

    /**
     * 清理已处理的历史预警记录
     * 每周日凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * SUN")
    public void cleanupHandledAlerts() {
        log.info("开始执行定时任务：清理已处理的历史预警记录");
        
        try {
            // 保留30天的已处理记录
            Integer deletedCount = stockAlertService.cleanupHandledAlerts(30);
            log.info("历史预警记录清理完成，删除 {} 条记录", deletedCount);
            
        } catch (Exception e) {
            log.error("历史预警记录清理失败", e);
        }
    }

    /**
     * 手动触发临期商品检查
     * 用于测试或紧急情况
     */
    public void manualCheckExpiringProducts() {
        log.info("手动触发临期商品预警检查");
        checkExpiringProducts();
    }

    /**
     * 手动触发低库存检查
     * 用于测试或紧急情况
     */
    public void manualCheckLowStockProducts() {
        log.info("手动触发低库存预警检查");
        checkLowStockProducts();
    }

    /**
     * 手动触发预警记录清理
     * 用于测试或紧急情况
     */
    public void manualCleanupHandledAlerts() {
        log.info("手动触发预警记录清理");
        cleanupHandledAlerts();
    }
}
