package com.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.supermarket.entity.Product;
import com.supermarket.entity.SaleOrder;
import com.supermarket.entity.SaleOrderItem;
import com.supermarket.mapper.ProductMapper;
import com.supermarket.mapper.SaleOrderMapper;
import com.supermarket.mapper.SaleOrderItemMapper;
import com.supermarket.service.BusinessToolsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
    
    @Override
    public Map<String, Object> executeAction(String action, Map<String, Object> params, Long userId) {
        try {
            switch (action) {
                case "QUERY_SALES":
                    String timeRange = (String) params.get("time_range");
                    String productName = (String) params.get("product_name");
                    return querySalesData(timeRange, productName);
                    
                case "CHECK_INVENTORY":
                    String productNameForInventory = (String) params.get("product_name");
                    return checkInventoryStatus(productNameForInventory);
                    
                case "ADD_PRODUCT":
                    return addProduct(params, userId);
                    
                case "UPDATE_PRICE":
                    String productNameForPrice = (String) params.get("product_name");
                    Double newPrice = (Double) params.get("new_price");
                    return updateProductPrice(productNameForPrice, newPrice, userId);
                    
                case "QUERY_FINANCE":
                    String timeRangeForFinance = (String) params.get("time_range");
                    return getFinancialOverview(timeRangeForFinance);
                    
                default:
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
            if (limit == null) {
                limit = 10;
            }
            
            LocalDateTime startTime = calculateStartTime(timeRange);
            LocalDateTime endTime = LocalDateTime.now();
            
            // 这里需要自定义SQL查询销售排行，暂时返回模拟数据
            result.put("success", true);
            result.put("message", "销售排行功能正在开发中");
            result.put("time_range", timeRange);
            
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
            // 获取销售数据
            Map<String, Object> salesData = querySalesData(timeRange, null);
            
            if (!(Boolean) salesData.get("success")) {
                return salesData;
            }
            
            BigDecimal totalRevenue = (BigDecimal) salesData.get("total_amount");
            Integer orderCount = (Integer) salesData.get("order_count");
            
            // 计算平均客单价
            BigDecimal avgOrderAmount = BigDecimal.ZERO;
            if (orderCount > 0) {
                avgOrderAmount = totalRevenue.divide(new BigDecimal(orderCount), 2, RoundingMode.HALF_UP);
            }
            
            result.put("success", true);
            result.put("total_revenue", totalRevenue);
            result.put("order_count", orderCount);
            result.put("avg_order_amount", avgOrderAmount);
            result.put("time_range", timeRange);
            
            // 简单的利润估算（假设毛利率30%）
            BigDecimal estimatedProfit = totalRevenue.multiply(new BigDecimal("0.3"));
            result.put("estimated_profit", estimatedProfit);
            result.put("profit_margin", "30%");
            
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
}