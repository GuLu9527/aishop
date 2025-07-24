package com.supermarket.controller;

import com.supermarket.common.Result;
import com.supermarket.dto.SalesAnalysisDTO;
import com.supermarket.service.SalesAnalysisService;
import com.supermarket.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 销售分析控制器
 */
@Slf4j
@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
@Validated
@Tag(name = "销售分析", description = "销售数据分析和统计报表")
public class SalesAnalysisController {
    
    private final SalesAnalysisService salesAnalysisService;

    /**
     * 验证日期范围参数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 验证结果，null表示验证通过，否则返回错误信息
     */
    private String validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            return "开始日期不能大于结束日期";
        }

        if (startDate.isBefore(LocalDate.now().minusYears(2))) {
            return "查询时间不能超过2年";
        }

        if (endDate.isAfter(LocalDate.now())) {
            return "结束日期不能大于当前日期";
        }

        // 限制查询范围不超过1年
        if (startDate.isBefore(endDate.minusYears(1))) {
            return "查询时间范围不能超过1年";
        }

        return null;
    }
    
    @GetMapping("/overview")
    @Operation(summary = "获取销售概览数据", description = "获取指定时间范围内的销售概览统计数据")
    public Result<SalesOverviewVO> getSalesOverview(
            @Parameter(description = "开始日期", required = true, example = "2024-01-01")
            @RequestParam
            @NotNull(message = "开始日期不能为空")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(description = "结束日期", required = true, example = "2024-01-31")
            @RequestParam
            @NotNull(message = "结束日期不能为空")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        log.info("获取销售概览数据，开始日期：{}，结束日期：{}", startDate, endDate);

        // 参数验证
        String validationError = validateDateRange(startDate, endDate);
        if (validationError != null) {
            return Result.error(validationError);
        }

        try {
            SalesAnalysisDTO dto = new SalesAnalysisDTO();
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);

            SalesOverviewVO overview = salesAnalysisService.getSalesOverview(dto);
            return Result.success(overview);
        } catch (Exception e) {
            log.error("获取销售概览数据失败", e);
            return Result.error("获取销售概览数据失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/trend")
    @Operation(summary = "获取销售趋势数据", description = "获取指定时间范围和维度的销售趋势数据")
    public Result<SalesTrendVO> getSalesTrend(
            @Parameter(description = "开始日期", required = true, example = "2024-01-01")
            @RequestParam
            @NotNull(message = "开始日期不能为空")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(description = "结束日期", required = true, example = "2024-01-31")
            @RequestParam
            @NotNull(message = "结束日期不能为空")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @Parameter(description = "时间维度：day(按天)、week(按周)、month(按月)", example = "day")
            @RequestParam(defaultValue = "day")
            @Pattern(regexp = "^(day|week|month)$", message = "时间维度只能是day、week或month")
            String period) {

        log.info("获取销售趋势数据，开始日期：{}，结束日期：{}，时间维度：{}", startDate, endDate, period);

        // 参数验证
        String validationError = validateDateRange(startDate, endDate);
        if (validationError != null) {
            return Result.error(validationError);
        }

        try {
            SalesAnalysisDTO dto = new SalesAnalysisDTO();
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);
            dto.setPeriod(period);

            SalesTrendVO trend = salesAnalysisService.getSalesTrend(dto);
            return Result.success(trend);
        } catch (Exception e) {
            log.error("获取销售趋势数据失败", e);
            return Result.error("获取销售趋势数据失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/category")
    @Operation(summary = "获取商品分类销售数据", description = "获取各商品分类的销售额和占比数据")
    public Result<CategorySalesVO> getCategorySales(
            @Parameter(description = "开始日期", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", example = "2024-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("获取商品分类销售数据，开始日期：{}，结束日期：{}", startDate, endDate);
        
        SalesAnalysisDTO dto = new SalesAnalysisDTO();
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        
        CategorySalesVO categorySales = salesAnalysisService.getCategorySales(dto);
        return Result.success(categorySales);
    }
    
    @GetMapping("/top-products")
    @Operation(summary = "获取热销商品排行", description = "获取指定数量的热销商品排行榜")
    public Result<TopProductsVO> getTopProducts(
            @Parameter(description = "开始日期", required = true, example = "2024-01-01")
            @RequestParam
            @NotNull(message = "开始日期不能为空")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(description = "结束日期", required = true, example = "2024-01-31")
            @RequestParam
            @NotNull(message = "结束日期不能为空")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @Parameter(description = "排行数量，范围1-100", example = "10")
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "排行数量不能小于1")
            @Max(value = 100, message = "排行数量不能大于100")
            Integer limit) {

        log.info("获取热销商品排行，开始日期：{}，结束日期：{}，数量：{}", startDate, endDate, limit);

        // 参数验证
        String validationError = validateDateRange(startDate, endDate);
        if (validationError != null) {
            return Result.error(validationError);
        }

        try {
            SalesAnalysisDTO dto = new SalesAnalysisDTO();
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);
            dto.setLimit(limit);

            TopProductsVO topProducts = salesAnalysisService.getTopProducts(dto);
            return Result.success(topProducts);
        } catch (Exception e) {
            log.error("获取热销商品排行失败", e);
            return Result.error("获取热销商品排行失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/cashier-performance")
    @Operation(summary = "获取收银员业绩对比", description = "获取收银员业绩对比数据")
    public Result<CashierPerformanceVO> getCashierPerformance(
            @Parameter(description = "开始日期", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", example = "2024-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "指标类型", example = "sales")
            @RequestParam(defaultValue = "sales") String metric) {
        
        log.info("获取收银员业绩对比，开始日期：{}，结束日期：{}，指标：{}", startDate, endDate, metric);
        
        SalesAnalysisDTO dto = new SalesAnalysisDTO();
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setMetric(metric);
        
        CashierPerformanceVO performance = salesAnalysisService.getCashierPerformance(dto);
        return Result.success(performance);
    }
    
    @GetMapping("/time-analysis")
    @Operation(summary = "获取时段销售分析", description = "获取按小时或按日期的时段销售分析数据")
    public Result<TimeAnalysisVO> getTimeAnalysis(
            @Parameter(description = "开始日期", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", example = "2024-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "分析类型", example = "hourly")
            @RequestParam(defaultValue = "hourly") String type) {
        
        log.info("获取时段销售分析，开始日期：{}，结束日期：{}，类型：{}", startDate, endDate, type);
        
        SalesAnalysisDTO dto = new SalesAnalysisDTO();
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setType(type);
        
        TimeAnalysisVO timeAnalysis = salesAnalysisService.getTimeAnalysis(dto);
        return Result.success(timeAnalysis);
    }

    @GetMapping("/export")
    @Operation(summary = "导出销售报表", description = "导出指定时间范围的销售报表数据")
    public Result<List<Map<String, Object>>> exportSalesReport(
            @Parameter(description = "开始日期", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", example = "2024-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "导出类型", example = "overview")
            @RequestParam(defaultValue = "overview") String type) {

        SalesAnalysisDTO dto = new SalesAnalysisDTO();
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setType(type);

        List<Map<String, Object>> result = salesAnalysisService.exportSalesReport(dto);
        return Result.success(result);
    }

    @GetMapping("/realtime")
    @Operation(summary = "获取实时销售数据", description = "获取今日实时销售数据和最近订单")
    public Result<Map<String, Object>> getRealTimeSales() {
        Map<String, Object> result = salesAnalysisService.getRealTimeSales();
        return Result.success(result);
    }

    @GetMapping("/forecast")
    @Operation(summary = "获取销售预测数据", description = "基于历史数据预测未来销售趋势")
    public Result<List<Map<String, Object>>> getSalesForecast(
            @Parameter(description = "预测天数", example = "7")
            @RequestParam(defaultValue = "7") Integer days) {

        List<Map<String, Object>> result = salesAnalysisService.getSalesForecast(days);
        return Result.success(result);
    }

    @GetMapping("/stock-alert")
    @Operation(summary = "获取库存预警", description = "获取库存不足和缺货商品信息")
    public Result<List<Map<String, Object>>> getStockAlert() {
        List<Map<String, Object>> result = salesAnalysisService.getStockAlert();
        return Result.success(result);
    }

    @GetMapping("/test-today-data")
    @Operation(summary = "测试今日数据", description = "测试查询今日销售数据")
    public Result<Map<String, Object>> testTodayData() {
        SalesAnalysisDTO dto = new SalesAnalysisDTO();
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now());

        SalesOverviewVO todayData = salesAnalysisService.getSalesOverview(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("queryDate", LocalDate.now());
        result.put("todayData", todayData);
        result.put("timestamp", LocalDateTime.now());

        // 添加调试信息
        result.put("debug", Map.of(
            "startDate", dto.getStartDate(),
            "endDate", dto.getEndDate(),
            "currentDate", LocalDate.now(),
            "currentTime", LocalDateTime.now()
        ));

        return Result.success(result);
    }
}
