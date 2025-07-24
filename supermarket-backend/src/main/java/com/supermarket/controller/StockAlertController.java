package com.supermarket.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermarket.common.Result;
import com.supermarket.dto.StockAlertQueryDTO;
import com.supermarket.service.ScheduledTaskService;
import com.supermarket.service.StockAlertService;
import com.supermarket.vo.StockAlertVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

/**
 * 库存预警控制器
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/stock-alerts")
@Tag(name = "库存预警管理", description = "库存预警相关接口")
public class StockAlertController {

    @Autowired
    private StockAlertService stockAlertService;

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @Operation(summary = "分页查询库存预警列表")
    @GetMapping("/page")
    public Result<IPage<StockAlertVO>> getAlertPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "预警类型") @RequestParam(required = false) Integer alertType,
            @Parameter(description = "预警状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "商品名称") @RequestParam(required = false) String productName,
            @Parameter(description = "紧急程度") @RequestParam(required = false) Integer urgencyLevel,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate) {
        try {
            StockAlertQueryDTO query = new StockAlertQueryDTO();
            query.setPageNum(pageNum);
            query.setPageSize(pageSize);
            query.setAlertType(alertType);
            query.setStatus(status);
            query.setProductName(productName);
            query.setUrgencyLevel(urgencyLevel);
            query.setCategoryId(categoryId);
            query.setStartDate(startDate);
            query.setEndDate(endDate);

            IPage<StockAlertVO> result = stockAlertService.getAlertPage(query);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            log.warn("查询库存预警列表参数错误: {}", e.getMessage());
            return Result.error("参数错误：" + e.getMessage());
        } catch (Exception e) {
            log.error("查询库存预警列表失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取预警统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getAlertStatistics() {
        try {
            Map<String, Object> statistics = stockAlertService.getAlertStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取预警统计信息失败", e);
            return Result.error("获取统计信息失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取未处理预警数量")
    @GetMapping("/pending-count")
    public Result<Integer> getPendingAlertCount() {
        try {
            Integer count = stockAlertService.getPendingAlertCount();
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取未处理预警数量失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取临期商品列表")
    @GetMapping("/expiring-products")
    public Result<List<StockAlertVO>> getExpiringProducts(
            @Parameter(description = "预警天数") @RequestParam(required = false) Integer warningDays) {
        try {
            List<StockAlertVO> products = stockAlertService.getExpiringProducts(warningDays);
            return Result.success(products);
        } catch (Exception e) {
            log.error("获取临期商品列表失败", e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    @Operation(summary = "处理预警")
    @PutMapping("/{alertId}/handle")
    public Result<String> handleAlert(
            @Parameter(description = "预警ID") @PathVariable Long alertId,
            @Parameter(description = "处理动作：handle-处理，ignore-忽略") @RequestParam String action,
            @Parameter(description = "处理人ID") @RequestParam Long handlerId) {
        try {
            boolean success = stockAlertService.handleAlert(alertId, handlerId, action);
            if (success) {
                return Result.success("处理成功");
            } else {
                return Result.error("处理失败");
            }
        } catch (Exception e) {
            log.error("处理预警失败：alertId={}, action={}", alertId, action, e);
            return Result.error("处理失败：" + e.getMessage());
        }
    }

    @Operation(summary = "批量处理预警")
    @PutMapping("/batch-handle")
    public Result<String> batchHandleAlerts(
            @Parameter(description = "预警ID列表") @RequestBody List<Long> alertIds,
            @Parameter(description = "处理动作：handle-处理，ignore-忽略") @RequestParam String action,
            @Parameter(description = "处理人ID") @RequestParam Long handlerId) {
        try {
            Integer successCount = stockAlertService.batchHandleAlerts(alertIds, handlerId, action);
            return Result.success(String.format("批量处理完成，成功处理 %d 个预警", successCount));
        } catch (Exception e) {
            log.error("批量处理预警失败：action={}", action, e);
            return Result.error("批量处理失败：" + e.getMessage());
        }
    }

    @Operation(summary = "手动检查临期商品")
    @PostMapping("/check-expiring")
    public Result<String> checkExpiringProducts() {
        try {
            scheduledTaskService.manualCheckExpiringProducts();
            return Result.success("临期商品检查已触发");
        } catch (Exception e) {
            log.error("手动检查临期商品失败", e);
            return Result.error("检查失败：" + e.getMessage());
        }
    }

    @Operation(summary = "手动检查低库存商品")
    @PostMapping("/check-low-stock")
    public Result<String> checkLowStockProducts() {
        try {
            scheduledTaskService.manualCheckLowStockProducts();
            return Result.success("低库存检查已触发");
        } catch (Exception e) {
            log.error("手动检查低库存商品失败", e);
            return Result.error("检查失败：" + e.getMessage());
        }
    }

    @Operation(summary = "清理已处理的历史预警")
    @DeleteMapping("/cleanup")
    public Result<String> cleanupHandledAlerts(
            @Parameter(description = "保留天数") @RequestParam(defaultValue = "30") Integer retentionDays) {
        try {
            Integer deletedCount = stockAlertService.cleanupHandledAlerts(retentionDays);
            return Result.success(String.format("清理完成，删除 %d 条历史记录", deletedCount));
        } catch (Exception e) {
            log.error("清理历史预警失败", e);
            return Result.error("清理失败：" + e.getMessage());
        }
    }
}
