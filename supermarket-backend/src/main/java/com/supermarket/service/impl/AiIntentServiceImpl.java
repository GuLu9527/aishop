package com.supermarket.service.impl;

import com.supermarket.service.AiIntentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI意图识别服务实现类
 */
@Slf4j
@Service
public class AiIntentServiceImpl implements AiIntentService {
    
    // 意图关键词映射
    private static final Map<String, String[]> INTENT_KEYWORDS = new HashMap<>();
    
    static {
        // 销售查询相关
        INTENT_KEYWORDS.put("QUERY_SALES", new String[]{
            "销售", "卖", "营业额", "收入", "赚", "钱", "生意", "业绩", "流水"
        });
        
        // 库存查询相关
        INTENT_KEYWORDS.put("CHECK_INVENTORY", new String[]{
            "库存", "没货", "剩余", "还有", "多少", "补货", "进货", "缺货"
        });
        
        // 商品管理相关
        INTENT_KEYWORDS.put("ADD_PRODUCT", new String[]{
            "添加", "新增", "录入", "上架", "加", "商品"
        });
        
        INTENT_KEYWORDS.put("UPDATE_PRICE", new String[]{
            "修改", "更新", "调整", "价格", "改价"
        });
        
        INTENT_KEYWORDS.put("REMOVE_PRODUCT", new String[]{
            "删除", "下架", "移除", "去掉"
        });
        
        // 财务相关
        INTENT_KEYWORDS.put("QUERY_FINANCE", new String[]{
            "利润", "成本", "毛利", "财务", "账目", "盈利"
        });
        
        // 报表相关
        INTENT_KEYWORDS.put("GENERATE_REPORT", new String[]{
            "报表", "统计", "分析", "总结", "汇总"
        });
    }
    
    @Override
    public Map<String, Object> parseIntent(String userInput) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 识别意图
        String intent = matchIntentByKeywords(userInput);
        result.put("intent", intent);
        
        // 2. 提取实体
        Map<String, Object> entities = extractEntities(userInput, intent);
        result.put("entities", entities);
        
        // 3. 计算置信度
        double confidence = calculateConfidence(userInput, intent);
        result.put("confidence", confidence);
        
        log.info("意图识别结果: 输入={}, 意图={}, 实体={}, 置信度={}", 
                userInput, intent, entities, confidence);
        
        return result;
    }
    
    @Override
    public String matchIntentByKeywords(String userInput) {
        String input = userInput.toLowerCase();
        
        // 计算每个意图的匹配分数
        Map<String, Integer> scores = new HashMap<>();
        
        for (Map.Entry<String, String[]> entry : INTENT_KEYWORDS.entrySet()) {
            String intent = entry.getKey();
            String[] keywords = entry.getValue();
            
            int score = 0;
            for (String keyword : keywords) {
                if (input.contains(keyword)) {
                    score++;
                }
            }
            
            if (score > 0) {
                scores.put(intent, score);
            }
        }
        
        // 返回得分最高的意图
        return scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("CHAT"); // 默认为普通聊天
    }
    
    @Override
    public Map<String, Object> extractEntities(String userInput, String intent) {
        Map<String, Object> entities = new HashMap<>();
        
        switch (intent) {
            case "QUERY_SALES":
                extractSalesEntities(userInput, entities);
                break;
            case "CHECK_INVENTORY":
                extractInventoryEntities(userInput, entities);
                break;
            case "ADD_PRODUCT":
                extractProductEntities(userInput, entities);
                break;
            case "UPDATE_PRICE":
                extractPriceEntities(userInput, entities);
                break;
            default:
                // 通用实体提取
                extractCommonEntities(userInput, entities);
                break;
        }
        
        return entities;
    }
    
    private void extractSalesEntities(String userInput, Map<String, Object> entities) {
        // 提取时间范围
        if (userInput.contains("今天") || userInput.contains("今日")) {
            entities.put("time_range", "today");
        } else if (userInput.contains("昨天")) {
            entities.put("time_range", "yesterday");
        } else if (userInput.contains("本周") || userInput.contains("这周")) {
            entities.put("time_range", "this_week");
        } else if (userInput.contains("本月") || userInput.contains("这个月")) {
            entities.put("time_range", "this_month");
        }
        
        // 提取商品名称
        String productName = extractProductName(userInput);
        if (productName != null) {
            entities.put("product_name", productName);
        }
    }
    
    private void extractInventoryEntities(String userInput, Map<String, Object> entities) {
        // 提取商品名称
        String productName = extractProductName(userInput);
        if (productName != null) {
            entities.put("product_name", productName);
        }
        
        // 提取数量
        Pattern quantityPattern = Pattern.compile("(\\d+)\\s*(个|瓶|包|盒|箱|斤|公斤|kg)");
        Matcher matcher = quantityPattern.matcher(userInput);
        if (matcher.find()) {
            entities.put("quantity", Integer.parseInt(matcher.group(1)));
            entities.put("unit", matcher.group(2));
        }
    }
    
    private void extractProductEntities(String userInput, Map<String, Object> entities) {
        // 提取商品名称
        String productName = extractProductName(userInput);
        if (productName != null) {
            entities.put("product_name", productName);
        }
        
        // 提取价格
        Pattern pricePattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*元");
        Matcher matcher = pricePattern.matcher(userInput);
        while (matcher.find()) {
            double price = Double.parseDouble(matcher.group(1));
            if (userInput.substring(0, matcher.start()).contains("进价") || 
                userInput.substring(0, matcher.start()).contains("成本")) {
                entities.put("purchase_price", price);
            } else if (userInput.substring(0, matcher.start()).contains("售价") || 
                      userInput.substring(0, matcher.start()).contains("卖")) {
                entities.put("selling_price", price);
            }
        }
        
        // 提取数量和单位
        Pattern quantityPattern = Pattern.compile("(\\d+)\\s*(个|瓶|包|盒|箱|斤|公斤|kg)");
        matcher = quantityPattern.matcher(userInput);
        if (matcher.find()) {
            entities.put("quantity", Integer.parseInt(matcher.group(1)));
            entities.put("unit", matcher.group(2));
        }
    }
    
    private void extractPriceEntities(String userInput, Map<String, Object> entities) {
        // 提取商品名称
        String productName = extractProductName(userInput);
        if (productName != null) {
            entities.put("product_name", productName);
        }
        
        // 提取新价格
        Pattern pricePattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*元");
        Matcher matcher = pricePattern.matcher(userInput);
        if (matcher.find()) {
            entities.put("new_price", Double.parseDouble(matcher.group(1)));
        }
    }
    
    private void extractCommonEntities(String userInput, Map<String, Object> entities) {
        // 提取商品名称
        String productName = extractProductName(userInput);
        if (productName != null) {
            entities.put("product_name", productName);
        }
        
        // 提取数字
        Pattern numberPattern = Pattern.compile("\\d+(?:\\.\\d+)?");
        Matcher matcher = numberPattern.matcher(userInput);
        if (matcher.find()) {
            entities.put("number", Double.parseDouble(matcher.group()));
        }
    }
    
    private String extractProductName(String userInput) {
        // 常见商品名称模式
        String[] commonProducts = {
            "可口可乐", "百事可乐", "雪碧", "芬达", "康师傅", "统一", "农夫山泉", "怡宝",
            "面包", "牛奶", "酸奶", "鸡蛋", "苹果", "香蕉", "橙子", "西瓜",
            "大米", "面条", "方便面", "饼干", "薯片", "瓜子", "花生", "糖果",
            "洗发水", "沐浴露", "牙膏", "牙刷", "毛巾", "纸巾", "洗衣粉", "洗洁精"
        };
        
        for (String product : commonProducts) {
            if (userInput.contains(product)) {
                return product;
            }
        }
        
        return null;
    }
    
    private double calculateConfidence(String userInput, String intent) {
        if ("CHAT".equals(intent)) {
            return 0.3; // 默认聊天置信度较低
        }
        
        String[] keywords = INTENT_KEYWORDS.get(intent);
        if (keywords == null) {
            return 0.5;
        }
        
        int matchCount = 0;
        for (String keyword : keywords) {
            if (userInput.toLowerCase().contains(keyword)) {
                matchCount++;
            }
        }
        
        // 基于匹配关键词数量计算置信度
        double confidence = Math.min(0.9, 0.3 + (matchCount * 0.2));
        return confidence;
    }
}