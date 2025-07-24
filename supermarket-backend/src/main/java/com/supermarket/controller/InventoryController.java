package com.supermarket.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermarket.common.Result;
import com.supermarket.dto.InventoryQueryDTO;
import com.supermarket.dto.StockAdjustDTO;
import com.supermarket.dto.BatchOperationResultDTO;
import com.supermarket.dto.BatchStockAdjustDTO;

import com.supermarket.service.InventoryService;
import com.supermarket.utils.JwtUtils;
import com.supermarket.vo.InventoryVO;
import com.supermarket.vo.StockRecordVO;
import com.supermarket.vo.InventoryStatisticsVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 库存管理控制器
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Tag(name = "库存管理", description = "库存管理相关接口")
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final JwtUtils jwtUtils;

    @Operation(summary = "分页查询库存列表")
    @GetMapping("/page")
    public Result<IPage<InventoryVO>> getInventoryPage(InventoryQueryDTO query) {
        try {
            IPage<InventoryVO> page = inventoryService.getInventoryPage(query);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("查询库存列表失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取库存统计信息")
    @GetMapping("/statistics")
    public Result<InventoryStatisticsVO> getInventoryStatistics() {
        try {
            InventoryStatisticsVO statistics = inventoryService.getInventoryStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error("获取库存统计失败：" + e.getMessage());
        }
    }

    @Operation(summary = "调整库存")
    @PostMapping("/adjust")
    public Result<String> adjustStock(@Valid @RequestBody StockAdjustDTO adjustDTO, 
                                    HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            boolean success = inventoryService.adjustStock(adjustDTO, userId);
            if (success) {
                return Result.success("库存调整成功");
            } else {
                return Result.error("库存调整失败");
            }
        } catch (Exception e) {
            return Result.error("库存调整失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取库存记录")
    @GetMapping("/records")
    public Result<IPage<StockRecordVO>> getStockRecords(
            @RequestParam(required = false) Long productId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            IPage<StockRecordVO> page = inventoryService.getStockRecords(productId, pageNum, pageSize);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("获取库存记录失败：" + e.getMessage());
        }
    }

    @Operation(summary = "设置库存预警")
    @PutMapping("/alert/{productId}")
    public Result<String> setStockAlert(@PathVariable Long productId,
                                      @RequestParam Integer minStock,
                                      @RequestParam Integer maxStock) {
        try {
            boolean success = inventoryService.setStockAlert(productId, minStock, maxStock);
            if (success) {
                return Result.success("库存预警设置成功");
            } else {
                return Result.error("库存预警设置失败");
            }
        } catch (Exception e) {
            return Result.error("库存预警设置失败：" + e.getMessage());
        }
    }

    @Operation(summary = "批量调整库存")
    @PostMapping("/batch-adjust")
    public Result<BatchOperationResultDTO> batchAdjustStock(
            @Validated @RequestBody BatchStockAdjustDTO batchStockAdjustDTO,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return Result.error("用户未登录");
            }

            BatchOperationResultDTO result = inventoryService.batchAdjustStock(batchStockAdjustDTO, userId);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.error("参数错误：" + e.getMessage());
        } catch (Exception e) {
            return Result.error("批量调整库存失败：" + e.getMessage());
        }
    }

    /**
     * 从token中获取用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtils.getUsernameFromToken(token);
            // 这里简化处理，实际应该从用户服务获取用户ID
            return 1L; // 临时返回固定值
        }
        return null;
    }
}
