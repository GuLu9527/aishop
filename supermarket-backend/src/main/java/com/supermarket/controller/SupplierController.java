package com.supermarket.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermarket.common.Result;
import com.supermarket.dto.SupplierDTO;
import com.supermarket.dto.SupplierQueryDTO;
import com.supermarket.service.SupplierService;
import com.supermarket.vo.SupplierVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import com.supermarket.util.ExcelUtil;

/**
 * 供货商管理控制器
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Tag(name = "供货商管理", description = "供货商管理相关接口")
@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
@Slf4j
public class SupplierController {

    private final SupplierService supplierService;

    @Operation(summary = "分页查询供货商列表")
    @PostMapping("/page")
    public Result<IPage<SupplierVO>> getSupplierPage(@RequestBody SupplierQueryDTO query) {
        try {
            IPage<SupplierVO> page = supplierService.getSupplierPage(query);
            return Result.success(page);
        } catch (Exception e) {
            log.error("查询供货商列表失败", e);
            return Result.error("查询供货商列表失败：" + e.getMessage());
        }
    }

    @Operation(summary = "根据ID查询供货商详情")
    @GetMapping("/{id}")
    public Result<SupplierVO> getSupplierById(
            @Parameter(description = "供货商ID") @PathVariable Long id) {
        try {
            SupplierVO supplier = supplierService.getSupplierById(id);
            if (supplier == null) {
                return Result.error("供货商不存在");
            }
            return Result.success(supplier);
        } catch (Exception e) {
            log.error("查询供货商详情失败: id={}", id, e);
            return Result.error("查询供货商详情失败：" + e.getMessage());
        }
    }

    @Operation(summary = "添加供货商")
    @PostMapping
    public Result<String> addSupplier(@Validated @RequestBody SupplierDTO supplierDTO) {
        try {
            boolean success = supplierService.addSupplier(supplierDTO);
            if (success) {
                return Result.success("添加供货商成功");
            } else {
                return Result.error("添加供货商失败");
            }
        } catch (Exception e) {
            log.error("添加供货商失败", e);
            return Result.error("添加供货商失败：" + e.getMessage());
        }
    }

    @Operation(summary = "更新供货商")
    @PutMapping("/{id}")
    public Result<String> updateSupplier(
            @Parameter(description = "供货商ID") @PathVariable Long id,
            @Validated @RequestBody SupplierDTO supplierDTO) {
        try {
            supplierDTO.setId(id);
            boolean success = supplierService.updateSupplier(supplierDTO);
            if (success) {
                return Result.success("更新供货商成功");
            } else {
                return Result.error("更新供货商失败");
            }
        } catch (Exception e) {
            log.error("更新供货商失败: id={}", id, e);
            return Result.error("更新供货商失败：" + e.getMessage());
        }
    }

    @Operation(summary = "删除供货商")
    @DeleteMapping("/{id}")
    public Result<String> deleteSupplier(
            @Parameter(description = "供货商ID") @PathVariable Long id) {
        try {
            boolean success = supplierService.deleteSupplier(id);
            if (success) {
                return Result.success("删除供货商成功");
            } else {
                return Result.error("删除供货商失败");
            }
        } catch (Exception e) {
            log.error("删除供货商失败: id={}", id, e);
            return Result.error("删除供货商失败：" + e.getMessage());
        }
    }

    @Operation(summary = "批量删除供货商")
    @DeleteMapping("/batch")
    public Result<String> deleteSuppliers(@RequestBody List<Long> ids) {
        try {
            boolean success = supplierService.deleteSuppliers(ids);
            if (success) {
                return Result.success("批量删除供货商成功");
            } else {
                return Result.error("批量删除供货商失败");
            }
        } catch (Exception e) {
            log.error("批量删除供货商失败: ids={}", ids, e);
            return Result.error("批量删除供货商失败：" + e.getMessage());
        }
    }

    @Operation(summary = "启用/禁用供货商")
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(
            @Parameter(description = "供货商ID") @PathVariable Long id,
            @Parameter(description = "状态：1-启用，0-禁用") @RequestParam Integer status) {
        try {
            boolean success = supplierService.updateStatus(id, status);
            if (success) {
                String statusText = status == 1 ? "启用" : "禁用";
                return Result.success(statusText + "供货商成功");
            } else {
                return Result.error("更新供货商状态失败");
            }
        } catch (Exception e) {
            log.error("更新供货商状态失败: id={}, status={}", id, status, e);
            return Result.error("更新供货商状态失败：" + e.getMessage());
        }
    }

    @Operation(summary = "查询所有启用的供货商")
    @GetMapping("/active")
    public Result<List<SupplierVO>> getActiveSuppliers() {
        try {
            List<SupplierVO> suppliers = supplierService.getActiveSuppliers();
            return Result.success(suppliers);
        } catch (Exception e) {
            log.error("查询启用的供货商失败", e);
            return Result.error("查询启用的供货商失败：" + e.getMessage());
        }
    }

    @Operation(summary = "检查供货商名称是否存在")
    @GetMapping("/check-name")
    public Result<Boolean> checkSupplierName(
            @Parameter(description = "供货商名称") @RequestParam String supplierName,
            @Parameter(description = "排除的ID") @RequestParam(required = false) Long excludeId) {
        try {
            boolean exists = supplierService.existsByName(supplierName, excludeId);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查供货商名称失败: supplierName={}", supplierName, e);
            return Result.error("检查供货商名称失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取供货商统计信息")
    @GetMapping("/stats")
    public Result<java.util.Map<String, Object>> getSupplierStats() {
        try {
            java.util.Map<String, Object> stats = supplierService.getSupplierStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取供货商统计信息失败", e);
            return Result.error("获取供货商统计信息失败：" + e.getMessage());
        }
    }

    @Operation(summary = "导出供货商数据", description = "根据查询条件导出供货商数据到Excel文件")
    @PostMapping("/export")
    public void exportSuppliers(
            @RequestBody(required = false) SupplierQueryDTO query,
            HttpServletResponse response) {
        try {
            // 如果查询条件为空，创建默认查询条件
            if (query == null) {
                query = new SupplierQueryDTO();
            }

            // 获取导出数据
            List<SupplierVO> suppliers = supplierService.getExportData(query);

            // 导出到Excel
            ExcelUtil.exportSuppliersToExcel(suppliers, response);

            log.info("导出供货商数据成功，共导出{}条记录", suppliers.size());
        } catch (Exception e) {
            log.error("导出供货商数据失败", e);
            try {
                response.reset();
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":500,\"message\":\"导出失败：" + e.getMessage() + "\"}");
            } catch (Exception ex) {
                log.error("写入错误响应失败", ex);
            }
        }
    }
}
