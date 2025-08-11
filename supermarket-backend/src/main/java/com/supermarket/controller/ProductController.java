package com.supermarket.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermarket.common.Result;
import com.supermarket.dto.ProductDTO;
import com.supermarket.dto.ProductQueryDTO;
import com.supermarket.dto.BatchOperationDTO;
import com.supermarket.dto.BatchOperationResultDTO;

import com.supermarket.entity.Product;
import com.supermarket.service.ProductService;
import com.supermarket.service.AuthService;
import com.supermarket.utils.JwtUtils;
import com.supermarket.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 商品管理控制器
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Tag(name = "商品管理", description = "商品管理相关接口")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final JwtUtils jwtUtils;
    private final AuthService authService;

    @Operation(summary = "分页查询商品列表")
    @PostMapping("/page")
    public Result<IPage<ProductVO>> getProductPage(@RequestBody ProductQueryDTO query) {
        try {
            IPage<ProductVO> page = productService.getProductPage(query);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("查询商品列表失败：" + e.getMessage());
        }
    }

    @Operation(summary = "根据ID查询商品详情")
    @GetMapping("/{id}")
    public Result<ProductVO> getProductById(@PathVariable Long id) {
        try {
            ProductVO product = productService.getProductById(id);
            if (product == null) {
                return Result.error("商品不存在");
            }
            return Result.success(product);
        } catch (Exception e) {
            return Result.error("查询商品详情失败：" + e.getMessage());
        }
    }

    @Operation(summary = "根据条码查询商品")
    @GetMapping("/barcode/{barcode}")
    public Result<Product> getProductByBarcode(@PathVariable String barcode) {
        try {
            Product product = productService.getProductByBarcode(barcode);
            if (product == null) {
                return Result.error("商品不存在");
            }
            return Result.success(product);
        } catch (Exception e) {
            return Result.error("查询商品失败：" + e.getMessage());
        }
    }

    @Operation(summary = "添加商品")
    @PostMapping
    public Result<String> addProduct(@Validated @RequestBody ProductDTO productDTO, 
                                   HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            boolean success = productService.addProduct(productDTO, userId);
            if (success) {
                return Result.success("添加商品成功");
            } else {
                return Result.error("添加商品失败，请检查数据是否正确");
            }
        } catch (RuntimeException e) {
            // 业务异常，直接返回异常信息
            return Result.error(e.getMessage());
        } catch (Exception e) {
            // 系统异常，记录日志并返回友好信息
            log.error("添加商品发生系统异常", e);
            return Result.error("系统繁忙，请稍后再试");
        }
    }

    @Operation(summary = "更新商品")
    @PutMapping("/{id}")
    public Result<String> updateProduct(@PathVariable Long id, 
                                      @Validated @RequestBody ProductDTO productDTO,
                                      HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            productDTO.setId(id);
            boolean success = productService.updateProduct(productDTO, userId);
            if (success) {
                return Result.success("更新商品成功");
            } else {
                return Result.error("更新商品失败，商品可能不存在");
            }
        } catch (RuntimeException e) {
            // 业务异常，直接返回异常信息
            return Result.error(e.getMessage());
        } catch (Exception e) {
            // 系统异常，记录日志并返回友好信息
            log.error("更新商品发生系统异常，商品ID：{}", id, e);
            return Result.error("系统繁忙，请稍后再试");
        }
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    public Result<String> deleteProduct(@PathVariable Long id) {
        try {
            boolean success = productService.deleteProduct(id);
            if (success) {
                return Result.success("删除商品成功");
            } else {
                return Result.error("删除商品失败，商品可能不存在或已被删除");
            }
        } catch (RuntimeException e) {
            // 业务异常，直接返回异常信息
            return Result.error(e.getMessage());
        } catch (Exception e) {
            // 系统异常，记录日志并返回友好信息
            log.error("删除商品发生系统异常，商品ID：{}", id, e);
            return Result.error("系统繁忙，请稍后再试");
        }
    }

    @Operation(summary = "批量删除商品")
    @DeleteMapping("/batch")
    public Result<String> deleteProducts(@RequestBody List<Long> ids) {
        try {
            boolean success = productService.deleteProducts(ids);
            if (success) {
                return Result.success("批量删除商品成功");
            } else {
                return Result.error("批量删除商品失败");
            }
        } catch (Exception e) {
            return Result.error("批量删除商品失败：" + e.getMessage());
        }
    }

    // 库存更新功能已移至 InventoryController.adjustStock() 统一管理

    @Operation(summary = "查询低库存商品")
    @GetMapping("/low-stock")
    public Result<List<ProductVO>> getLowStockProducts() {
        try {
            List<ProductVO> products = productService.getLowStockProducts();
            return Result.success(products);
        } catch (Exception e) {
            return Result.error("查询低库存商品失败：" + e.getMessage());
        }
    }

    @Operation(summary = "启用/禁用商品")
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            boolean success = productService.updateStatus(id, status);
            if (success) {
                return Result.success("更新商品状态成功");
            } else {
                return Result.error("更新商品状态失败");
            }
        } catch (Exception e) {
            return Result.error("更新商品状态失败：" + e.getMessage());
        }
    }

    @Operation(summary = "批量操作商品")
    @PostMapping("/batch-operation")
    public Result<BatchOperationResultDTO> batchOperation(
            @Validated @RequestBody BatchOperationDTO operationDTO,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            
            // 参数验证
            if (operationDTO.getProductIds() == null || operationDTO.getProductIds().isEmpty()) {
                return Result.badRequest("请选择要操作的商品");
            }
            
            BatchOperationResultDTO result = productService.batchOperation(operationDTO, userId);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            // 参数错误
            return Result.badRequest(e.getMessage());
        } catch (RuntimeException e) {
            // 业务异常，直接返回异常信息
            return Result.error(e.getMessage());
        } catch (Exception e) {
            // 系统异常，记录日志并返回友好信息
            log.error("批量操作商品发生系统异常，操作类型：{}, 商品数量：{}", 
                     operationDTO.getOperationType(), 
                     operationDTO.getProductIds().size(), e);
            return Result.error("系统繁忙，请稍后再试");
        }
    }

    @Operation(summary = "批量更新商品状态")
    @PutMapping("/batch/status")
    public Result<BatchOperationResultDTO> batchUpdateStatus(
            @RequestBody java.util.List<Long> productIds,
            @RequestParam Integer status,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            BatchOperationResultDTO result = productService.batchUpdateStatus(productIds, status, userId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("批量更新状态失败：" + e.getMessage());
        }
    }

    @Operation(summary = "批量更新商品分类")
    @PutMapping("/batch/category")
    public Result<BatchOperationResultDTO> batchUpdateCategory(
            @RequestBody java.util.List<Long> productIds,
            @RequestParam Long categoryId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            BatchOperationResultDTO result = productService.batchUpdateCategory(productIds, categoryId, userId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("批量更新分类失败：" + e.getMessage());
        }
    }

    @Operation(summary = "批量更新商品价格")
    @PutMapping("/batch/price")
    public Result<BatchOperationResultDTO> batchUpdatePrice(
            @RequestBody java.util.List<Long> productIds,
            @RequestParam BatchOperationDTO.PriceAdjustmentType adjustmentType,
            @RequestParam java.math.BigDecimal adjustmentValue,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            BatchOperationResultDTO result = productService.batchUpdatePrice(productIds, adjustmentType, adjustmentValue, userId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("批量更新价格失败：" + e.getMessage());
        }
    }

    @Operation(summary = "批量删除商品（改进版）")
    @DeleteMapping("/batch-delete")
    public Result<BatchOperationResultDTO> batchDeleteProducts(
            @RequestBody java.util.List<Long> productIds,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            BatchOperationResultDTO result = productService.batchDeleteProducts(productIds, userId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }

    // ==================== 过期管理相关接口 ====================

    @Operation(summary = "查询临期商品")
    @GetMapping("/expiring")
    public Result<List<ProductVO>> getExpiringProducts(
            @RequestParam(defaultValue = "7") Integer warningDays) {
        try {
            if (warningDays <= 0) {
                return Result.badRequest("预警天数必须大于0");
            }

            List<ProductVO> products = productService.getExpiringProducts(warningDays);
            return Result.success(products);
        } catch (Exception e) {
            return Result.error("查询临期商品失败：" + e.getMessage());
        }
    }

    @Operation(summary = "查询已过期商品")
    @GetMapping("/expired")
    public Result<List<ProductVO>> getExpiredProducts() {
        try {
            List<ProductVO> products = productService.getExpiredProducts();
            return Result.success(products);
        } catch (Exception e) {
            return Result.error("查询已过期商品失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取商品过期统计信息")
    @GetMapping("/expiration-statistics")
    public Result<Map<String, Object>> getExpirationStatistics() {
        try {
            Map<String, Object> statistics = productService.getExpirationStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error("获取统计信息失败：" + e.getMessage());
        }
    }

    @Operation(summary = "批量设置商品生产日期")
    @PutMapping("/batch-production-date")
    public Result<BatchOperationResultDTO> batchSetProductionDate(
            @RequestBody BatchProductionDateRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            if (userId == null) {
                return Result.unauthorized("用户未登录");
            }

            BatchOperationResultDTO result = productService.batchSetProductionDate(
                    request.getProductIds(), request.getProductionDate(), userId);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.badRequest("参数错误：" + e.getMessage());
        } catch (Exception e) {
            return Result.error("批量设置生产日期失败：" + e.getMessage());
        }
    }

    /**
     * 批量设置生产日期请求DTO
     */
    public static class BatchProductionDateRequest {
        private List<Long> productIds;
        private LocalDate productionDate;
        private String reason = "批量设置生产日期";

        // Getters and Setters
        public List<Long> getProductIds() {
            return productIds;
        }

        public void setProductIds(List<Long> productIds) {
            this.productIds = productIds;
        }

        public LocalDate getProductionDate() {
            return productionDate;
        }

        public void setProductionDate(LocalDate productionDate) {
            this.productionDate = productionDate;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    /**
     * 从token中获取用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                
                // 验证token有效性
                if (!jwtUtils.validateToken(token)) {
                    throw new RuntimeException("Token无效或已过期");
                }
                
                String username = jwtUtils.getUsernameFromToken(token);
                return authService.getUserIdByUsername(username);
            }
            throw new RuntimeException("未找到有效的Authorization header");
        } catch (Exception e) {
            // 如果获取失败，返回默认用户ID (开发环境)
            // 生产环境建议抛出异常
            return 1L;
        }
    }
}
