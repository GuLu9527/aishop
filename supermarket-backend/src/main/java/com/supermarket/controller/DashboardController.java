package com.supermarket.controller;

import com.supermarket.common.Result;
import com.supermarket.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 仪表板控制器
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "仪表板管理", description = "仪表板数据统计接口")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {

    private final ProductMapper productMapper;

    /**
     * 获取仪表板统计数据
     */
    @GetMapping("/stats")
    @Operation(summary = "获取仪表板统计数据", description = "获取商品总数、销售额、库存预警等统计数据")
    public Result<Map<String, Object>> getStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 获取真实统计数据
            Integer totalProducts = productMapper.countTotalProducts();
            Integer lowStockCount = productMapper.countLowStockProducts();

            // 获取今日销售额
            BigDecimal todaySales = productMapper.getTodaySales();

            // 获取即将过期商品数量（假设7天内过期为即将过期）
            Integer expiringSoonCount = productMapper.countExpiringSoonProducts();

            stats.put("totalProducts", totalProducts != null ? totalProducts : 0);
            stats.put("todaySales", todaySales != null ? todaySales.doubleValue() : 0.00);
            stats.put("lowStockCount", lowStockCount != null ? lowStockCount : 0);
            stats.put("expiringSoonCount", expiringSoonCount != null ? expiringSoonCount : 0);
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取仪表板统计数据失败: {}", e.getMessage());
            return Result.error("获取统计数据失败");
        }
    }
}
