package com.supermarket.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermarket.common.Result;
import com.supermarket.dto.FinanceQueryDTO;
import com.supermarket.dto.FinanceRecordDTO;
import com.supermarket.service.FinanceRecordService;
import com.supermarket.utils.UserContextUtils;
import com.supermarket.vo.FinanceRecordVO;
import com.supermarket.vo.FinanceStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 财务记录控制器
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/finance")
@RequiredArgsConstructor
@Tag(name = "财务管理", description = "财务记录管理相关接口")
@Validated
public class FinanceRecordController {

    private final FinanceRecordService financeRecordService;
    private final UserContextUtils userContextUtils;

    @Operation(summary = "分页查询财务记录")
    @GetMapping("/records")
    public Result<IPage<FinanceRecordVO>> getFinanceRecordPage(@Valid FinanceQueryDTO query) {
        try {
            // 参数验证和默认值设置
            if (query.getPageNum() == null || query.getPageNum() < 1) {
                query.setPageNum(1);
            }
            if (query.getPageSize() == null || query.getPageSize() < 1 || query.getPageSize() > 100) {
                query.setPageSize(10);
            }

            IPage<FinanceRecordVO> page = financeRecordService.getFinanceRecordPage(query);
            return Result.success(page);
        } catch (IllegalArgumentException e) {
            log.warn("查询财务记录参数错误: {}", e.getMessage());
            return Result.error("查询参数错误：" + e.getMessage());
        } catch (Exception e) {
            log.error("查询财务记录失败", e);
            return Result.error("查询财务记录失败：" + e.getMessage());
        }
    }

    @Operation(summary = "根据ID查询财务记录详情")
    @GetMapping("/records/{id}")
    public Result<FinanceRecordVO> getFinanceRecordById(
            @Parameter(description = "记录ID") @PathVariable Long id) {
        try {
            FinanceRecordVO record = financeRecordService.getFinanceRecordById(id);
            if (record == null) {
                return Result.error("财务记录不存在");
            }
            return Result.success(record);
        } catch (Exception e) {
            log.error("查询财务记录详情失败: id={}", id, e);
            return Result.error("查询财务记录详情失败：" + e.getMessage());
        }
    }

    @Operation(summary = "添加财务记录")
    @PostMapping("/records")
    public Result<String> addFinanceRecord(@Valid @RequestBody FinanceRecordDTO financeRecordDTO) {
        try {
            boolean success = financeRecordService.addFinanceRecord(financeRecordDTO);
            if (success) {
                return Result.success("添加财务记录成功");
            } else {
                return Result.error("添加财务记录失败");
            }
        } catch (Exception e) {
            log.error("添加财务记录失败", e);
            return Result.error("添加财务记录失败：" + e.getMessage());
        }
    }

    @Operation(summary = "更新财务记录")
    @PutMapping("/records/{id}")
    public Result<String> updateFinanceRecord(
            @Parameter(description = "记录ID") @PathVariable Long id,
            @Valid @RequestBody FinanceRecordDTO financeRecordDTO) {
        try {
            boolean success = financeRecordService.updateFinanceRecord(id, financeRecordDTO);
            if (success) {
                return Result.success("更新财务记录成功");
            } else {
                return Result.error("更新财务记录失败");
            }
        } catch (Exception e) {
            log.error("更新财务记录失败: id={}", id, e);
            return Result.error("更新财务记录失败：" + e.getMessage());
        }
    }

    @Operation(summary = "删除财务记录")
    @DeleteMapping("/records/{id}")
    public Result<String> deleteFinanceRecord(
            @Parameter(description = "记录ID") @PathVariable Long id) {
        try {
            boolean success = financeRecordService.deleteFinanceRecord(id);
            if (success) {
                return Result.success("删除财务记录成功");
            } else {
                return Result.error("删除财务记录失败");
            }
        } catch (Exception e) {
            log.error("删除财务记录失败: id={}", id, e);
            return Result.error("删除财务记录失败：" + e.getMessage());
        }
    }

    @Operation(summary = "批量删除财务记录")
    @DeleteMapping("/records/batch")
    public Result<String> batchDeleteFinanceRecords(@RequestBody List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要删除的记录");
            }
            
            boolean success = financeRecordService.batchDeleteFinanceRecords(ids);
            if (success) {
                return Result.success("批量删除财务记录成功");
            } else {
                return Result.error("批量删除财务记录失败");
            }
        } catch (Exception e) {
            log.error("批量删除财务记录失败", e);
            return Result.error("批量删除财务记录失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取财务统计信息")
    @GetMapping("/stats")
    public Result<FinanceStatsVO> getFinanceStats(
            @Parameter(description = "开始日期") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        try {
            FinanceStatsVO stats = financeRecordService.getFinanceStats(startDate, endDate);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取财务统计信息失败", e);
            return Result.error("获取财务统计信息失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取今日财务统计")
    @GetMapping("/stats/today")
    public Result<FinanceStatsVO> getTodayFinanceStats() {
        try {
            FinanceStatsVO stats = financeRecordService.getTodayFinanceStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取今日财务统计失败", e);
            return Result.error("获取今日财务统计失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取本月财务统计")
    @GetMapping("/stats/month")
    public Result<FinanceStatsVO> getMonthFinanceStats() {
        try {
            FinanceStatsVO stats = financeRecordService.getMonthFinanceStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取本月财务统计失败", e);
            return Result.error("获取本月财务统计失败：" + e.getMessage());
        }
    }

    @Operation(summary = "导出财务记录数据")
    @GetMapping("/records/export")
    public Result<List<FinanceRecordVO>> exportFinanceRecords(@Valid FinanceQueryDTO query) {
        try {
            List<FinanceRecordVO> records = financeRecordService.getExportData(query);
            return Result.success(records);
        } catch (Exception e) {
            log.error("导出财务记录数据失败", e);
            return Result.error("导出财务记录数据失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取财务统计概览")
    @GetMapping("/stats/overview")
    public Result<Map<String, Object>> getFinanceOverview(
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            // 默认查询最近30天
            if (startDate == null) {
                startDate = LocalDate.now().minusDays(30);
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }

            Map<String, Object> overview = financeRecordService.getFinanceOverview(startDate, endDate);
            return Result.success(overview);
        } catch (Exception e) {
            log.error("获取财务统计概览失败", e);
            return Result.error("获取财务统计概览失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取收支趋势数据")
    @GetMapping("/stats/trend")
    public Result<Map<String, Object>> getFinanceTrend(
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "统计周期：day/week/month") @RequestParam(defaultValue = "day") String period) {
        try {
            // 默认查询最近30天
            if (startDate == null) {
                startDate = LocalDate.now().minusDays(30);
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }

            Map<String, Object> trend = financeRecordService.getFinanceTrend(startDate, endDate, period);
            return Result.success(trend);
        } catch (Exception e) {
            log.error("获取收支趋势数据失败", e);
            return Result.error("获取收支趋势数据失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取业务类型统计")
    @GetMapping("/stats/business-type")
    public Result<List<Map<String, Object>>> getBusinessTypeStats(
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            // 默认查询最近30天
            if (startDate == null) {
                startDate = LocalDate.now().minusDays(30);
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }

            List<Map<String, Object>> stats = financeRecordService.getBusinessTypeStats(startDate, endDate);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取业务类型统计失败", e);
            return Result.error("获取业务类型统计失败：" + e.getMessage());
        }
    }

    @Operation(summary = "创建测试财务数据")
    @PostMapping("/test/create-today-data")
    public Result<String> createTodayTestData() {
        try {
            // 创建今天的测试财务记录
            LocalDateTime now = LocalDateTime.now();

            // 销售收入记录
            financeRecordService.recordSalesIncome(
                new BigDecimal("494.10"),
                "SO20250721001",
                1L,
                "测试销售收入 - 今日销售数据"
            );

            financeRecordService.recordSalesIncome(
                new BigDecimal("156.70"),
                "SO20250721002",
                2L,
                "测试销售收入 - 下午销售"
            );

            // 其他收入
            FinanceRecordDTO otherIncome = new FinanceRecordDTO();
            otherIncome.setRecordType(1); // 收入
            otherIncome.setBusinessType(3); // 其他收入
            otherIncome.setAmount(new BigDecimal("50.00"));
            otherIncome.setDescription("测试其他收入");
            otherIncome.setRecordDate(now);
            financeRecordService.addFinanceRecord(otherIncome);

            // 采购支出
            FinanceRecordDTO purchaseExpense = new FinanceRecordDTO();
            purchaseExpense.setRecordType(2); // 支出
            purchaseExpense.setBusinessType(2); // 采购支出
            purchaseExpense.setAmount(new BigDecimal("200.00"));
            purchaseExpense.setDescription("测试采购支出");
            purchaseExpense.setRecordDate(now);
            financeRecordService.addFinanceRecord(purchaseExpense);

            return Result.success("测试财务数据创建成功");
        } catch (Exception e) {
            log.error("创建测试财务数据失败", e);
            return Result.error("创建测试财务数据失败：" + e.getMessage());
        }
    }
}
