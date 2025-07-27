package com.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.supermarket.entity.FinanceRecord;
import com.supermarket.entity.Product;
import com.supermarket.entity.SaleOrder;
import com.supermarket.entity.SaleOrderItem;
import com.supermarket.enums.ActionType;
import com.supermarket.mapper.FinanceRecordMapper;
import com.supermarket.mapper.ProductMapper;
import com.supermarket.mapper.SaleOrderItemMapper;
import com.supermarket.mapper.SaleOrderMapper;
import com.supermarket.service.BusinessToolsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 业务工具服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessToolsServiceImpl implements BusinessToolsService {
    
    private final ProductMapper productMapper;
    private final SaleOrderMapper saleOrderMapper;
    private final SaleOrderItemMapper saleOrderItemMapper;
    private final FinanceRecordMapper financeRecordMapper;
    
    @Override
    public Map<String, Object> executeAction(String action, Map<String, Object> params, Long userId) {
        try {
            if (ActionType.QUERY_SALES_DATA.getCode().equals(action)) {
                String timeRange = (String) params.get("time_range");
                String productName = (String) params.get("product_name");
                return querySalesData(timeRange, productName);
            } else if (ActionType.CHECK_INVENTORY_STATUS.getCode().equals(action)) {
                String productNameForInventory = (String) params.get("product_name");
                return checkInventoryStatus(productNameForInventory);
            } else if (ActionType.ADD_NEW_PRODUCT.getCode().equals(action)) {
                return addProduct(params, userId);
            } else if (ActionType.UPDATE_PRODUCT_PRICE.getCode().equals(action)) {
                String productNameForPrice = (String) params.get("product_name");
                Double newPrice = (Double) params.get("new_price");
                return updateProductPrice(productNameForPrice, newPrice, userId);
            } else if (ActionType.GET_FINANCIAL_OVERVIEW.getCode().equals(action)) {
                String timeRangeForFinance = (String) params.get("time_range");
                return getFinancialOverview(timeRangeForFinance);
            } else if (ActionType.GENERATE_SALES_REPORT.getCode().equals(action)) {
                String reportTimeRange = (String) params.get("time_range");
                String reportType = (String) params.get("report_type");
                return generateSalesReport(reportTimeRange, reportType);
            } else if (ActionType.GET_SALES_RANKING.getCode().equals(action)) {
                String rankingTimeRange = (String) params.get("time_range");
                Integer limit = (Integer) params.get("limit");
                return getSalesRanking(rankingTimeRange, limit);
            } else {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "不支持的操作类型: " + action);
                return result;
            }
        } catch (Exception e) {
            log.error("执行业务操作失败: action={}, params={}", action, params, e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "操作执行失败: " + e.getMessage());
            return result;
        }
    }
    
    @Override
    public Map<String, Object> querySalesData(String timeRange, String productName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 计算时间范围
            LocalDateTime startTime = calculateStartTime(timeRange);
            LocalDateTime endTime = LocalDateTime.now();
            
            // 查询销售订单
            QueryWrapper<SaleOrder> orderQuery = new QueryWrapper<>();
            orderQuery.between("create_time", startTime, endTime);
            List<SaleOrder> orders = saleOrderMapper.selectList(orderQuery);
            
            if (orders.isEmpty()) {
                result.put("success", true);
                result.put("message", "该时间段内没有销售记录");
                result.put("total_amount", 0);
                result.put("order_count", 0);
                return result;
            }
            
            // 计算总金额和订单数量
            BigDecimal totalAmount = orders.stream()
                    .map(SaleOrder::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            int orderCount = orders.size();
            
            // 如果指定了商品名称，查询该商品的销售情况
            if (productName != null && !productName.trim().isEmpty()) {
                List<Long> orderIds = orders.stream().map(SaleOrder::getId).collect(Collectors.toList());
                
                QueryWrapper<SaleOrderItem> itemQuery = new QueryWrapper<>();
                itemQuery.in("order_id", orderIds);
                itemQuery.like("product_name", productName);
                List<SaleOrderItem> items = saleOrderItemMapper.selectList(itemQuery);
                
                if (items.isEmpty()) {
                    result.put("success", true);
                    result.put("message", "该时间段内没有找到商品 \"" + productName + "\" 的销售记录");
                    return result;
                }
                
                int productQuantity = items.stream().mapToInt(SaleOrderItem::getQuantity).sum();
                BigDecimal productAmount = items.stream()
                        .map(SaleOrderItem::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                result.put("product_name", productName);
                result.put("product_quantity", productQuantity);
                result.put("product_amount", productAmount);
            }
            
            result.put("success", true);
            result.put("total_amount", totalAmount);
            result.put("order_count", orderCount);
            result.put("time_range", timeRange);
            result.put("start_time", startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            result.put("end_time", endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
        } catch (Exception e) {
            log.error("查询销售数据失败", e);
            result.put("success", false);
            result.put("message", "查询销售数据失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> checkInventoryStatus(String productName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            QueryWrapper<Product> query = new QueryWrapper<>();
            query.eq("status", 1); // 在售商品
            
            if (productName != null && !productName.trim().isEmpty()) {
                query.like("product_name", productName);
            }
            
            List<Product> products = productMapper.selectList(query);
            
            if (products.isEmpty()) {
                result.put("success", true);
                result.put("message", productName != null ? 
                    "没有找到商品 \"" + productName + "\"" : "没有在售商品");
                return result;
            }
            
            // 检查低库存商品
            List<Map<String, Object>> lowStockProducts = new ArrayList<>();
            List<Map<String, Object>> normalProducts = new ArrayList<>();
            
            for (Product product : products) {
                Map<String, Object> productInfo = new HashMap<>();
                productInfo.put("id", product.getId());
                productInfo.put("name", product.getProductName());
                productInfo.put("stock_quantity", product.getStockQuantity());
                productInfo.put("min_stock", product.getMinStock());
                productInfo.put("unit", product.getUnit());
                
                if (product.getStockQuantity() <= product.getMinStock()) {
                    productInfo.put("status", "低库存");
                    lowStockProducts.add(productInfo);
                } else {
                    productInfo.put("status", "正常");
                    normalProducts.add(productInfo);
                }
            }
            
            result.put("success", true);
            result.put("total_products", products.size());
            result.put("low_stock_count", lowStockProducts.size());
            result.put("low_stock_products", lowStockProducts);
            result.put("normal_products", normalProducts);
            
            if (!lowStockProducts.isEmpty()) {
                result.put("has_alert", true);
                result.put("alert_message", "发现 " + lowStockProducts.size() + " 个商品库存不足，需要及时补货");
            } else {
                result.put("has_alert", false);
                result.put("alert_message", "所有商品库存正常");
            }
            
        } catch (Exception e) {
            log.error("检查库存状态失败", e);
            result.put("success", false);
            result.put("message", "检查库存失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> addProduct(Map<String, Object> productInfo, Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证必要参数
            String productName = (String) productInfo.get("product_name");
            if (productName == null || productName.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "商品名称不能为空");
                return result;
            }
            
            // 检查商品是否已存在
            QueryWrapper<Product> query = new QueryWrapper<>();
            query.eq("product_name", productName);
            Product existingProduct = productMapper.selectOne(query);
            
            if (existingProduct != null) {
                result.put("success", false);
                result.put("message", "商品 \"" + productName + "\" 已存在");
                return result;
            }
            
            // 创建新商品
            Product product = new Product();
            product.setProductName(productName);
            
            // 设置价格
            if (productInfo.containsKey("purchase_price")) {
                product.setPurchasePrice(new BigDecimal(productInfo.get("purchase_price").toString()));
            }
            if (productInfo.containsKey("selling_price")) {
                product.setSellingPrice(new BigDecimal(productInfo.get("selling_price").toString()));
            }
            
            // 设置库存
            if (productInfo.containsKey("quantity")) {
                product.setStockQuantity((Integer) productInfo.get("quantity"));
            } else {
                product.setStockQuantity(0);
            }
            
            // 设置单位
            if (productInfo.containsKey("unit")) {
                product.setUnit((String) productInfo.get("unit"));
            } else {
                product.setUnit("个");
            }
            
            // 设置默认值
            product.setCategoryId(1L); // 默认分类
            product.setMinStock(10); // 默认最低库存
            product.setMaxStock(1000); // 默认最高库存
            product.setStatus(1); // 在售
            product.setCreateBy(userId);
            product.setUpdateBy(userId);
            product.setCreateTime(LocalDateTime.now());
            product.setUpdateTime(LocalDateTime.now());
            
            productMapper.insert(product);
            
            result.put("success", true);
            result.put("message", "商品 \"" + productName + "\" 添加成功");
            result.put("product_id", product.getId());
            result.put("product_name", productName);
            
        } catch (Exception e) {
            log.error("添加商品失败", e);
            result.put("success", false);
            result.put("message", "添加商品失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> updateProductPrice(String productName, Double newPrice, Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (productName == null || productName.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "商品名称不能为空");
                return result;
            }
            
            if (newPrice == null || newPrice <= 0) {
                result.put("success", false);
                result.put("message", "价格必须大于0");
                return result;
            }
            
            // 查找商品
            QueryWrapper<Product> query = new QueryWrapper<>();
            query.like("product_name", productName);
            query.eq("status", 1);
            List<Product> products = productMapper.selectList(query);
            
            if (products.isEmpty()) {
                result.put("success", false);
                result.put("message", "没有找到商品 \"" + productName + "\"");
                return result;
            }
            
            if (products.size() > 1) {
                result.put("success", false);
                result.put("message", "找到多个匹配的商品，请提供更准确的商品名称");
                List<String> productNames = products.stream()
                        .map(Product::getProductName)
                        .collect(Collectors.toList());
                result.put("matched_products", productNames);
                return result;
            }
            
            // 更新价格
            Product product = products.get(0);
            BigDecimal oldPrice = product.getSellingPrice();
            product.setSellingPrice(new BigDecimal(newPrice.toString()));
            product.setUpdateBy(userId);
            product.setUpdateTime(LocalDateTime.now());
            
            productMapper.updateById(product);
            
            result.put("success", true);
            result.put("message", "商品 \"" + product.getProductName() + "\" 价格更新成功");
            result.put("product_name", product.getProductName());
            result.put("old_price", oldPrice);
            result.put("new_price", newPrice);
            
        } catch (Exception e) {
            log.error("更新商品价格失败", e);
            result.put("success", false);
            result.put("message", "更新价格失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> getLowStockAlert() {
        return checkInventoryStatus(null);
    }
    
    @Override
    public Map<String, Object> getSalesRanking(String timeRange, Integer limit) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 计算时间范围
            LocalDateTime startTime = calculateStartTime(timeRange);
            LocalDateTime endTime = LocalDateTime.now();
            
            // 设置默认限制
            int limitCount = limit != null ? limit : 10;
            
            // 查询销售订单
            QueryWrapper<SaleOrder> orderQuery = new QueryWrapper<>();
            orderQuery.between("create_time", startTime, endTime);
            List<SaleOrder> orders = saleOrderMapper.selectList(orderQuery);
            
            if (orders.isEmpty()) {
                result.put("success", true);
                result.put("message", "该时间段内没有销售记录");
                result.put("time_range", timeRange);
                result.put("ranking", new ArrayList<>());
                return result;
            }
            
            // 获取所有订单项
            List<Long> orderIds = orders.stream().map(SaleOrder::getId).collect(Collectors.toList());
            QueryWrapper<SaleOrderItem> itemQuery = new QueryWrapper<>();
            itemQuery.in("order_id", orderIds);
            List<SaleOrderItem> items = saleOrderItemMapper.selectList(itemQuery);
            
            // 按商品统计销量和销售额
            Map<String, Map<String, Object>> productStats = new HashMap<>();
            
            for (SaleOrderItem item : items) {
                String productName = item.getProductName();
                
                productStats.computeIfAbsent(productName, k -> {
                    Map<String, Object> stats = new HashMap<>();
                    stats.put("product_name", productName);
                    stats.put("total_quantity", 0);
                    stats.put("total_amount", BigDecimal.ZERO);
                    stats.put("order_count", 0);
                    return stats;
                });
                
                Map<String, Object> stats = productStats.get(productName);
                stats.put("total_quantity", (Integer) stats.get("total_quantity") + item.getQuantity());
                stats.put("total_amount", ((BigDecimal) stats.get("total_amount")).add(item.getSubtotal()));
                stats.put("order_count", (Integer) stats.get("order_count") + 1);
            }
            
            // 按销量排序并取前N名
            List<Map<String, Object>> ranking = productStats.values().stream()
                    .sorted((a, b) -> Integer.compare((Integer) b.get("total_quantity"), (Integer) a.get("total_quantity")))
                    .limit(limitCount)
                    .collect(Collectors.toList());
            
            // 添加排名信息
            for (int i = 0; i < ranking.size(); i++) {
                Map<String, Object> product = ranking.get(i);
                product.put("rank", i + 1);
                
                // 计算平均单价
                BigDecimal totalAmount = (BigDecimal) product.get("total_amount");
                Integer totalQuantity = (Integer) product.get("total_quantity");
                BigDecimal avgPrice = totalAmount.divide(new BigDecimal(totalQuantity), 2, RoundingMode.HALF_UP);
                product.put("avg_price", avgPrice);
            }
            
            // 生成排行榜总结
            StringBuilder summary = new StringBuilder();
            summary.append(String.format("📊 %s销售排行榜 (前%d名)\\n\\n", 
                    getTimeRangeDescription(timeRange), ranking.size()));
            
            for (int i = 0; i < Math.min(5, ranking.size()); i++) {
                Map<String, Object> product = ranking.get(i);
                summary.append(String.format("%d. %s - 销量: %d件, 销售额: ¥%.2f\\n",
                        i + 1,
                        product.get("product_name"),
                        product.get("total_quantity"),
                        product.get("total_amount")));
            }
            
            if (ranking.size() > 5) {
                summary.append(String.format("\\n...还有%d个商品未显示", ranking.size() - 5));
            }
            
            result.put("success", true);
            result.put("time_range", timeRange);
            result.put("period", String.format("从 %s 到 %s", 
                    startTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm")),
                    endTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"))));
            result.put("ranking", ranking);
            result.put("total_products", ranking.size());
            result.put("summary", summary.toString());
            
        } catch (Exception e) {
            log.error("获取销售排行失败", e);
            result.put("success", false);
            result.put("message", "获取销售排行失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> getFinancialOverview(String timeRange) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 计算时间范围
            LocalDateTime startTime = calculateStartTime(timeRange);
            LocalDateTime endTime = LocalDateTime.now();
            
            // 1. 计算销售收入和销售成本
            Map<String, Object> salesFinance = calculateSalesFinance(startTime, endTime);
            BigDecimal totalRevenue = (BigDecimal) salesFinance.get("total_revenue");
            BigDecimal totalCost = (BigDecimal) salesFinance.get("total_cost");
            Integer orderCount = (Integer) salesFinance.get("order_count");
            Integer totalQuantity = (Integer) salesFinance.get("total_quantity");
            
            // 2. 计算毛利润和毛利率
            BigDecimal grossProfit = totalRevenue.subtract(totalCost);
            BigDecimal grossMargin = BigDecimal.ZERO;
            if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
                grossMargin = grossProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
            }
            
            // 3. 获取其他收入和支出（从财务记录表）
            Map<String, BigDecimal> otherFinance = calculateOtherFinance(startTime, endTime);
            BigDecimal otherIncome = otherFinance.get("other_income");
            BigDecimal otherExpense = otherFinance.get("other_expense");
            
            // 4. 计算净利润
            BigDecimal netProfit = grossProfit.add(otherIncome).subtract(otherExpense);
            BigDecimal netMargin = BigDecimal.ZERO;
            BigDecimal totalIncome = totalRevenue.add(otherIncome);
            if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
                netMargin = netProfit.divide(totalIncome, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
            }
            
            // 5. 计算平均客单价和平均毛利
            BigDecimal avgOrderAmount = BigDecimal.ZERO;
            BigDecimal avgGrossProfit = BigDecimal.ZERO;
            if (orderCount > 0) {
                avgOrderAmount = totalRevenue.divide(new BigDecimal(orderCount), 2, RoundingMode.HALF_UP);
                avgGrossProfit = grossProfit.divide(new BigDecimal(orderCount), 2, RoundingMode.HALF_UP);
            }
            
            // 6. 构建详细的财务报告
            result.put("success", true);
            result.put("time_range", timeRange);
            result.put("period", String.format("从 %s 到 %s", 
                    startTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm")),
                    endTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"))));
            
            // 销售相关
            result.put("total_revenue", totalRevenue);
            result.put("total_cost", totalCost);
            result.put("order_count", orderCount);
            result.put("total_quantity", totalQuantity);
            result.put("avg_order_amount", avgOrderAmount);
            
            // 利润相关
            result.put("gross_profit", grossProfit);
            result.put("gross_margin", grossMargin.setScale(2, RoundingMode.HALF_UP));
            result.put("avg_gross_profit", avgGrossProfit);
            
            // 其他收支
            result.put("other_income", otherIncome);
            result.put("other_expense", otherExpense);
            
            // 净利润
            result.put("net_profit", netProfit);
            result.put("net_margin", netMargin.setScale(2, RoundingMode.HALF_UP));
            
            // 生成财务摘要
            StringBuilder summary = new StringBuilder();
            summary.append(String.format("💰 %s财务概况\\n\\n", getTimeRangeDescription(timeRange)));
            summary.append(String.format("📊 销售收入: ¥%.2f\\n", totalRevenue));
            summary.append(String.format("💸 销售成本: ¥%.2f\\n", totalCost));
            summary.append(String.format("📈 毛利润: ¥%.2f (%.2f%%)\\n", grossProfit, grossMargin));
            
            if (otherIncome.compareTo(BigDecimal.ZERO) > 0) {
                summary.append(String.format("💎 其他收入: ¥%.2f\\n", otherIncome));
            }
            if (otherExpense.compareTo(BigDecimal.ZERO) > 0) {
                summary.append(String.format("🏢 其他支出: ¥%.2f\\n", otherExpense));
            }
            
            summary.append(String.format("💵 净利润: ¥%.2f (%.2f%%)\\n", netProfit, netMargin));
            summary.append(String.format("🛒 订单数: %d笔\\n", orderCount));
            summary.append(String.format("📦 销售数量: %d件\\n", totalQuantity));
            summary.append(String.format("🎯 平均客单价: ¥%.2f", avgOrderAmount));
            
            result.put("summary", summary.toString());
            
        } catch (Exception e) {
            log.error("获取财务概况失败", e);
            result.put("success", false);
            result.put("message", "获取财务概况失败: " + e.getMessage());
        }
        
        return result;
    }
    
    private LocalDateTime calculateStartTime(String timeRange) {
        LocalDateTime now = LocalDateTime.now();
        
        if (timeRange == null) {
            return now.minusDays(1); // 默认昨天
        }
        
        switch (timeRange.toLowerCase()) {
            case "today":
                return now.toLocalDate().atStartOfDay();
            case "yesterday":
                return now.minusDays(1).toLocalDate().atStartOfDay();
            case "this_week":
                return now.minusDays(now.getDayOfWeek().getValue() - 1).toLocalDate().atStartOfDay();
            case "this_month":
                return now.withDayOfMonth(1).toLocalDate().atStartOfDay();
            case "last_week":
                return now.minusDays(now.getDayOfWeek().getValue() + 6).toLocalDate().atStartOfDay();
            case "last_month":
                return now.minusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay();
            default:
                return now.minusDays(1).toLocalDate().atStartOfDay();
        }
    }
    
    /**
     * 生成销售报表
     */
    private Map<String, Object> generateSalesReport(String timeRange, String reportType) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 计算时间范围
            LocalDateTime startTime = calculateStartTime(timeRange);
            LocalDateTime endTime = LocalDateTime.now();
            
            // 查询销售订单
            QueryWrapper<SaleOrder> orderQuery = new QueryWrapper<>();
            orderQuery.between("create_time", startTime, endTime);
            orderQuery.orderByDesc("create_time");
            List<SaleOrder> orders = saleOrderMapper.selectList(orderQuery);
            
            if (orders.isEmpty()) {
                result.put("success", true);
                result.put("message", "该时间段内没有销售记录");
                result.put("report_type", reportType);
                result.put("time_range", timeRange);
                return result;
            }
            
            // 基础统计
            BigDecimal totalAmount = orders.stream()
                    .map(SaleOrder::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            int totalOrders = orders.size();
            BigDecimal avgOrderAmount = totalAmount.divide(new BigDecimal(totalOrders), 2, RoundingMode.HALF_UP);
            
            // 按报表类型生成不同内容
            switch (reportType != null ? reportType.toLowerCase() : "summary") {
                case "detailed":
                    result.put("orders", orders.stream().limit(10).collect(Collectors.toList())); // 最近10个订单
                    // 查询订单详情
                    List<Long> orderIds = orders.stream().map(SaleOrder::getId).collect(Collectors.toList());
                    QueryWrapper<SaleOrderItem> itemQuery = new QueryWrapper<>();
                    itemQuery.in("order_id", orderIds);
                    List<SaleOrderItem> items = saleOrderItemMapper.selectList(itemQuery);
                    
                    // 商品销量统计
                    Map<String, Integer> productSales = items.stream()
                            .collect(Collectors.groupingBy(
                                    SaleOrderItem::getProductName,
                                    Collectors.summingInt(SaleOrderItem::getQuantity)
                            ));
                    
                    result.put("top_products", productSales.entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                            .limit(5)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (e1, e2) -> e1,
                                    LinkedHashMap::new
                            )));
                    break;
                    
                case "trend":
                    // 按天统计趋势
                    Map<String, BigDecimal> dailyTrend = orders.stream()
                            .collect(Collectors.groupingBy(
                                    order -> order.getCreateTime().toLocalDate().toString(),
                                    Collectors.reducing(BigDecimal.ZERO, SaleOrder::getTotalAmount, BigDecimal::add)
                            ));
                    result.put("daily_trend", dailyTrend);
                    break;
                    
                case "summary":
                default:
                    // 默认摘要报表
                    result.put("summary", Map.of(
                            "period", "从 " + startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) 
                                    + " 到 " + endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            "total_orders", totalOrders,
                            "total_amount", totalAmount,
                            "avg_order_amount", avgOrderAmount,
                            "growth_trend", totalOrders > 0 ? "正常营业" : "无销售记录"
                    ));
                    break;
            }
            
            result.put("success", true);
            result.put("report_type", reportType);
            result.put("time_range", timeRange);
            result.put("total_amount", totalAmount);
            result.put("total_orders", totalOrders);
            result.put("avg_order_amount", avgOrderAmount);
            result.put("generated_at", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
        } catch (Exception e) {
            log.error("生成销售报表失败", e);
            result.put("success", false);
            result.put("message", "生成销售报表失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 计算销售财务数据（收入和成本）
     */
    private Map<String, Object> calculateSalesFinance(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> result = new HashMap<>();
        
        // 查询销售订单
        QueryWrapper<SaleOrder> orderQuery = new QueryWrapper<>();
        orderQuery.between("create_time", startTime, endTime);
        List<SaleOrder> orders = saleOrderMapper.selectList(orderQuery);
        
        if (orders.isEmpty()) {
            result.put("total_revenue", BigDecimal.ZERO);
            result.put("total_cost", BigDecimal.ZERO);
            result.put("order_count", 0);
            result.put("total_quantity", 0);
            return result;
        }
        
        // 计算总收入
        BigDecimal totalRevenue = orders.stream()
                .map(SaleOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 获取所有订单项
        List<Long> orderIds = orders.stream().map(SaleOrder::getId).collect(Collectors.toList());
        QueryWrapper<SaleOrderItem> itemQuery = new QueryWrapper<>();
        itemQuery.in("order_id", orderIds);
        List<SaleOrderItem> items = saleOrderItemMapper.selectList(itemQuery);
        
        // 计算总成本和总数量
        BigDecimal totalCost = BigDecimal.ZERO;
        int totalQuantity = 0;
        
        for (SaleOrderItem item : items) {
            totalQuantity += item.getQuantity();
            
            // 查找商品的进货价
            QueryWrapper<Product> productQuery = new QueryWrapper<>();
            productQuery.eq("id", item.getProductId());
            Product product = productMapper.selectOne(productQuery);
            
            if (product != null && product.getPurchasePrice() != null) {
                // 使用实际进货价计算成本
                BigDecimal itemCost = product.getPurchasePrice()
                        .multiply(new BigDecimal(item.getQuantity()));
                totalCost = totalCost.add(itemCost);
            } else {
                // 如果没有进货价，使用销售价的70%作为估算成本
                BigDecimal estimatedCost = item.getSellingPrice()
                        .multiply(new BigDecimal("0.7"))
                        .multiply(new BigDecimal(item.getQuantity()));
                totalCost = totalCost.add(estimatedCost);
            }
        }
        
        result.put("total_revenue", totalRevenue);
        result.put("total_cost", totalCost);
        result.put("order_count", orders.size());
        result.put("total_quantity", totalQuantity);
        
        return result;
    }
    
    /**
     * 计算其他收入和支出（从财务记录表）
     */
    private Map<String, BigDecimal> calculateOtherFinance(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, BigDecimal> result = new HashMap<>();
        
        // 查询其他收入（record_type=1 且 business_type不是销售收入）
        QueryWrapper<FinanceRecord> incomeQuery = new QueryWrapper<>();
        incomeQuery.eq("record_type", 1) // 收入类型
                .ne("business_type", 1) // 不是销售收入
                .between("record_date", startTime, endTime);
        List<FinanceRecord> incomeRecords = financeRecordMapper.selectList(incomeQuery);
        
        BigDecimal otherIncome = incomeRecords.stream()
                .map(FinanceRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 查询其他支出（record_type=2 且 business_type不是采购支出）
        QueryWrapper<FinanceRecord> expenseQuery = new QueryWrapper<>();
        expenseQuery.eq("record_type", 2) // 支出类型
                .ne("business_type", 2) // 不是采购支出
                .between("record_date", startTime, endTime);
        List<FinanceRecord> expenseRecords = financeRecordMapper.selectList(expenseQuery);
        
        BigDecimal otherExpense = expenseRecords.stream()
                .map(FinanceRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        result.put("other_income", otherIncome);
        result.put("other_expense", otherExpense);
        
        return result;
    }
    
    /**
     * 获取时间范围描述
     */
    private String getTimeRangeDescription(String timeRange) {
        if (timeRange == null) return "最近";
        
        switch (timeRange.toLowerCase()) {
            case "today":
                return "今日";
            case "yesterday":
                return "昨日";
            case "this_week":
                return "本周";
            case "this_month":
                return "本月";
            case "last_week":
                return "上周";
            case "last_month":
                return "上月";
            default:
                return "最近";
        }
    }
}