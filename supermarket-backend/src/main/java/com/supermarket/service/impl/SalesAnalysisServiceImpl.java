package com.supermarket.service.impl;

import com.supermarket.dto.SalesAnalysisDTO;
import com.supermarket.mapper.SalesAnalysisMapper;
import com.supermarket.service.SalesAnalysisService;
import com.supermarket.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 销售分析服务实现类
 * 使用MyBatis Mapper进行数据访问，符合Spring Boot + MyBatis最佳实践
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalesAnalysisServiceImpl implements SalesAnalysisService {

    private final SalesAnalysisMapper salesAnalysisMapper;

    @Override
    public SalesOverviewVO getSalesOverview(SalesAnalysisDTO dto) {
        log.info("获取销售概览数据，查询条件：{}", dto);

        SalesOverviewVO overview = new SalesOverviewVO();

        // 获取当前期间数据
        Map<String, Object> currentData = salesAnalysisMapper.getSalesOverview(dto);
        setCurrentPeriodValues(overview, currentData);

        // 获取上一期间数据并计算趋势
        SalesAnalysisDTO prevDto = buildPreviousPeriodDto(dto);
        Map<String, Object> previousData = salesAnalysisMapper.getSalesOverview(prevDto);
        calculateAndSetTrends(overview, currentData, previousData);

        return overview;
    }

    @Override
    public SalesTrendVO getSalesTrend(SalesAnalysisDTO dto) {
        log.info("获取销售趋势数据，查询条件：{}", dto);

        List<Map<String, Object>> results = salesAnalysisMapper.getSalesTrend(dto);

        SalesTrendVO trend = new SalesTrendVO();
        trend.setPeriod(dto.getPeriod());
        trend.setDates(results.stream()
            .map(row -> (String) row.get("period_label"))
            .toList());
        trend.setSalesData(results.stream()
            .map(row -> (BigDecimal) row.get("sales"))
            .toList());
        trend.setOrdersData(results.stream()
            .map(row -> ((Number) row.get("orders")).longValue())
            .toList());

        return trend;
    }

    @Override
    public CategorySalesVO getCategorySales(SalesAnalysisDTO dto) {
        log.info("获取商品分类销售数据，查询条件：{}", dto);

        List<Map<String, Object>> results = salesAnalysisMapper.getCategorySales(dto);

        CategorySalesVO categoryVO = new CategorySalesVO();
        BigDecimal totalSales = results.stream()
            .map(row -> (BigDecimal) row.get("value"))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        categoryVO.setTotalSales(totalSales);
        categoryVO.setData(results.stream()
            .map(row -> buildCategoryItem(row, totalSales))
            .toList());

        return categoryVO;
    }

    @Override
    public TopProductsVO getTopProducts(SalesAnalysisDTO dto) {
        log.info("获取热销商品排行，查询条件：{}", dto);

        List<Map<String, Object>> results = salesAnalysisMapper.getTopProducts(dto);

        TopProductsVO topProductsVO = new TopProductsVO();
        topProductsVO.setLimit(dto.getLimit());
        topProductsVO.setProducts(buildTopProductItems(results));

        return topProductsVO;
    }

    @Override
    public CashierPerformanceVO getCashierPerformance(SalesAnalysisDTO dto) {
        log.info("获取收银员业绩对比，查询条件：{}", dto);

        List<Map<String, Object>> results = salesAnalysisMapper.getCashierPerformance(dto);

        CashierPerformanceVO performanceVO = new CashierPerformanceVO();
        performanceVO.setMetric(dto.getMetric());
        performanceVO.setCashiers(buildCashierItems(results));

        return performanceVO;
    }

    @Override
    public TimeAnalysisVO getTimeAnalysis(SalesAnalysisDTO dto) {
        log.info("获取时段销售分析，查询条件：{}", dto);

        List<Map<String, Object>> results = salesAnalysisMapper.getTimeAnalysis(dto);

        TimeAnalysisVO timeAnalysisVO = new TimeAnalysisVO();
        timeAnalysisVO.setType(dto.getType());

        List<TimeAnalysisVO.TimeAnalysisItem> data = results.stream()
            .map(this::buildTimeAnalysisItem)
            .toList();

        timeAnalysisVO.setData(data);

        // 设置峰值时段
        data.stream()
            .max((a, b) -> a.getSales().compareTo(b.getSales()))
            .ifPresent(peak -> {
                timeAnalysisVO.setPeakTime(peak.getTimeLabel());
                timeAnalysisVO.setPeakSales(peak.getSales());
            });

        return timeAnalysisVO;
    }

    @Override
    public List<Map<String, Object>> exportSalesReport(SalesAnalysisDTO dto) {
        log.info("导出销售报表，查询条件：{}", dto);

        // 根据导出类型返回不同的数据
        String exportType = dto.getType() != null ? dto.getType() : "overview";

        switch (exportType) {
            case "detail":
                return salesAnalysisMapper.getSalesDetailForExport(dto);
            case "trend":
                return salesAnalysisMapper.getSalesTrend(dto);
            case "category":
                return salesAnalysisMapper.getCategorySales(dto);
            case "products":
                return salesAnalysisMapper.getTopProducts(dto);
            default:
                // 返回概览数据
                Map<String, Object> overview = salesAnalysisMapper.getSalesOverview(dto);
                return List.of(overview);
        }
    }

    @Override
    public Map<String, Object> getRealTimeSales() {
        log.info("获取实时销售数据");

        // 获取今日实时数据
        SalesAnalysisDTO todayDto = new SalesAnalysisDTO();
        todayDto.setStartDate(LocalDate.now());
        todayDto.setEndDate(LocalDate.now());

        log.info("查询今日数据，日期范围：{} 到 {}", todayDto.getStartDate(), todayDto.getEndDate());

        Map<String, Object> todayData = salesAnalysisMapper.getSalesOverview(todayDto);
        log.info("今日销售数据：{}", todayData);

        // 获取本小时数据
        Map<String, Object> hourlyData = salesAnalysisMapper.getCurrentHourSales();
        log.info("当前小时数据：{}", hourlyData);

        // 获取最近订单
        List<Map<String, Object>> recentOrders = salesAnalysisMapper.getRecentOrders(10);
        log.info("最近订单数量：{}", recentOrders.size());

        Map<String, Object> realTimeData = new HashMap<>();
        realTimeData.put("today", todayData);
        realTimeData.put("currentHour", hourlyData);
        realTimeData.put("recentOrders", recentOrders);

        // 确保时间格式正确
        LocalDateTime now = LocalDateTime.now();
        realTimeData.put("updateTime", now.toString()); // 转换为ISO字符串格式
        realTimeData.put("updateTimeFormatted", now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        log.info("实时数据响应：{}", realTimeData);

        return realTimeData;
    }

    @Override
    public List<Map<String, Object>> getSalesForecast(Integer days) {
        log.info("获取销售预测数据，预测天数：{}", days);

        // 基于历史数据的简单预测算法
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusDays(days * 2); // 使用2倍天数的历史数据

        SalesAnalysisDTO dto = new SalesAnalysisDTO();
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setPeriod("day");

        List<Map<String, Object>> historicalData = salesAnalysisMapper.getSalesTrend(dto);

        // 简单的移动平均预测
        return generateForecastData(historicalData, days);
    }

    @Override
    public List<Map<String, Object>> getStockAlert() {
        log.info("获取商品库存预警");

        return salesAnalysisMapper.getStockAlert();
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 生成预测数据
     */
    private List<Map<String, Object>> generateForecastData(List<Map<String, Object>> historicalData, Integer days) {
        List<Map<String, Object>> forecastData = new ArrayList<>();

        if (historicalData.isEmpty()) {
            return forecastData;
        }

        // 计算移动平均值
        int windowSize = Math.min(7, historicalData.size()); // 7天移动平均
        BigDecimal avgSales = BigDecimal.ZERO;

        // 取最近windowSize天的平均值
        for (int i = historicalData.size() - windowSize; i < historicalData.size(); i++) {
            Map<String, Object> data = historicalData.get(i);
            BigDecimal sales = (BigDecimal) data.get("sales");
            if (sales != null) {
                avgSales = avgSales.add(sales);
            }
        }
        avgSales = avgSales.divide(BigDecimal.valueOf(windowSize), 2, RoundingMode.HALF_UP);

        // 生成预测数据
        LocalDate startDate = LocalDate.now();
        for (int i = 0; i < days; i++) {
            Map<String, Object> forecast = new HashMap<>();
            forecast.put("date", startDate.plusDays(i).toString());
            forecast.put("predictedSales", avgSales);
            forecast.put("confidence", 0.7); // 简单的置信度
            forecastData.add(forecast);
        }

        return forecastData;
    }

    /**
     * 构建上一期间的查询条件
     */
    private SalesAnalysisDTO buildPreviousPeriodDto(SalesAnalysisDTO dto) {
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        long daysBetween = endDate.toEpochDay() - startDate.toEpochDay();

        SalesAnalysisDTO prevDto = new SalesAnalysisDTO();
        prevDto.setStartDate(startDate.minusDays(daysBetween + 1));
        prevDto.setEndDate(startDate.minusDays(1));

        return prevDto;
    }

    /**
     * 设置当前期间的数值
     */
    private void setCurrentPeriodValues(SalesOverviewVO overview, Map<String, Object> currentData) {
        overview.setTotalSales(getBigDecimalValue(currentData, "total_sales"));
        overview.setTotalOrders(getLongValue(currentData, "total_orders"));
        overview.setTotalProducts(getLongValue(currentData, "total_products"));
        overview.setAvgOrderValue(getBigDecimalValue(currentData, "avg_order_value"));
    }

    /**
     * 计算并设置趋势数据
     */
    private void calculateAndSetTrends(SalesOverviewVO overview,
                                     Map<String, Object> currentData,
                                     Map<String, Object> previousData) {

        overview.setSalesTrend(calculateTrend(
            getBigDecimalValue(currentData, "total_sales"),
            getBigDecimalValue(previousData, "total_sales")
        ));

        overview.setOrdersTrend(calculateTrend(
            BigDecimal.valueOf(getLongValue(currentData, "total_orders")),
            BigDecimal.valueOf(getLongValue(previousData, "total_orders"))
        ));

        overview.setProductsTrend(calculateTrend(
            BigDecimal.valueOf(getLongValue(currentData, "total_products")),
            BigDecimal.valueOf(getLongValue(previousData, "total_products"))
        ));

        overview.setAvgTrend(calculateTrend(
            getBigDecimalValue(currentData, "avg_order_value"),
            getBigDecimalValue(previousData, "avg_order_value")
        ));
    }

    /**
     * 构建分类销售项目
     */
    private CategorySalesVO.CategorySalesItem buildCategoryItem(Map<String, Object> row, BigDecimal totalSales) {
        CategorySalesVO.CategorySalesItem item = new CategorySalesVO.CategorySalesItem();
        item.setName(getStringValue(row, "name"));

        BigDecimal value = getBigDecimalValue(row, "value");
        item.setValue(value);

        // 计算占比
        BigDecimal percentage = calculatePercentage(value, totalSales);
        item.setPercentage(percentage);

        return item;
    }

    /**
     * 构建热销商品项目列表
     */
    private List<TopProductsVO.TopProductItem> buildTopProductItems(List<Map<String, Object>> results) {
        List<TopProductsVO.TopProductItem> items = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            TopProductsVO.TopProductItem item = buildTopProductItem(results.get(i));
            item.setRank(i + 1); // 设置排名
            items.add(item);
        }
        return items;
    }

    /**
     * 构建单个热销商品项目
     */
    private TopProductsVO.TopProductItem buildTopProductItem(Map<String, Object> row) {
        TopProductsVO.TopProductItem item = new TopProductsVO.TopProductItem();

        item.setProductId(getLongValue(row, "product_id"));
        item.setProductName(getStringValue(row, "product_name"));
        item.setBarcode(getStringValue(row, "barcode"));
        item.setCategoryName(getStringValue(row, "category_name"));
        item.setQuantity(getLongValue(row, "quantity"));
        item.setSales(getBigDecimalValue(row, "sales"));

        return item;
    }

    /**
     * 构建收银员项目列表
     */
    private List<CashierPerformanceVO.CashierPerformanceItem> buildCashierItems(List<Map<String, Object>> results) {
        List<CashierPerformanceVO.CashierPerformanceItem> items = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            CashierPerformanceVO.CashierPerformanceItem item = buildCashierItem(results.get(i));
            item.setRank(i + 1); // 设置排名
            items.add(item);
        }
        return items;
    }

    /**
     * 构建单个收银员项目
     */
    private CashierPerformanceVO.CashierPerformanceItem buildCashierItem(Map<String, Object> row) {
        CashierPerformanceVO.CashierPerformanceItem item = new CashierPerformanceVO.CashierPerformanceItem();

        item.setCashierId(getLongValue(row, "cashier_id"));
        item.setCashierName(getStringValue(row, "cashier_name"));
        item.setSales(getBigDecimalValue(row, "sales"));
        item.setOrders(getLongValue(row, "orders"));
        item.setAvgOrderValue(getBigDecimalValue(row, "avg_order_value"));

        return item;
    }

    /**
     * 构建时段分析项目
     */
    private TimeAnalysisVO.TimeAnalysisItem buildTimeAnalysisItem(Map<String, Object> row) {
        TimeAnalysisVO.TimeAnalysisItem item = new TimeAnalysisVO.TimeAnalysisItem();

        item.setTimeOrder(getIntegerValue(row, "time_order"));
        item.setTimeLabel(getStringValue(row, "time_label"));
        item.setSales(getBigDecimalValue(row, "sales"));
        item.setOrders(getLongValue(row, "orders"));

        return item;
    }

    // ==================== 工具方法 ====================

    /**
     * 计算趋势百分比
     */
    private BigDecimal calculateTrend(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return current != null && current.compareTo(BigDecimal.ZERO) > 0
                ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }

        if (current == null) {
            return BigDecimal.valueOf(-100);
        }

        return current.subtract(previous)
            .divide(previous, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * 计算百分比
     */
    private BigDecimal calculatePercentage(BigDecimal part, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return part.divide(total, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * 安全地从Map中获取BigDecimal值
     */
    private BigDecimal getBigDecimalValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value instanceof BigDecimal ? (BigDecimal) value : BigDecimal.ZERO;
    }

    /**
     * 安全地从Map中获取Long值
     */
    private Long getLongValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value instanceof Number ? ((Number) value).longValue() : 0L;
    }

    /**
     * 安全地从Map中获取Integer值
     */
    private Integer getIntegerValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }

    /**
     * 安全地从Map中获取String值
     */
    private String getStringValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value != null ? value.toString() : "";
    }
}
