package com.supermarket.service.impl;

import com.supermarket.constants.AiChatConstants;
import com.supermarket.enums.IntentType;
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
        INTENT_KEYWORDS.put(IntentType.QUERY_SALES.getCode(), new String[]{
            "销售", "卖", "营业额", "收入", "赚", "钱", "生意", "业绩", "流水", "今天", "昨天", "本周", "本月", "情况", "怎么样"
        });
        
        // 库存查询相关
        INTENT_KEYWORDS.put(IntentType.CHECK_INVENTORY.getCode(), new String[]{
            "库存", "没货", "剩余", "还有", "多少", "补货", "进货", "缺货"
        });
        
        // 商品管理相关
        INTENT_KEYWORDS.put(IntentType.ADD_PRODUCT.getCode(), new String[]{
            "添加", "新增", "录入", "上架", "加", "商品"
        });
        
        INTENT_KEYWORDS.put(IntentType.UPDATE_PRICE.getCode(), new String[]{
            "修改", "更新", "调整", "价格", "改价"
        });
        
        INTENT_KEYWORDS.put(IntentType.REMOVE_PRODUCT.getCode(), new String[]{
            "删除", "下架", "移除", "去掉"
        });
        
        // 财务相关
        INTENT_KEYWORDS.put(IntentType.QUERY_FINANCE.getCode(), new String[]{
            "利润", "成本", "毛利", "财务", "账目", "盈利"
        });
        
        // 报表相关
        INTENT_KEYWORDS.put(IntentType.GENERATE_REPORT.getCode(), new String[]{
            "报表", "统计", "分析", "总结", "汇总", "报告", "图表", "数据", "趋势"
        });
        
        // 销售排行相关
        INTENT_KEYWORDS.put(IntentType.QUERY_SALES_RANKING.getCode(), new String[]{
            "排行", "排名", "热销", "畅销", "卖得好", "卖的好", "最受欢迎", "销量", "哪个", "哪些", "什么商品", "top", "前几名"
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
                .orElse(IntentType.CHAT.getCode()); // 默认为普通聊天
    }
    
    @Override
    public Map<String, Object> extractEntities(String userInput, String intent) {
        Map<String, Object> entities = new HashMap<>();
        
        if (IntentType.QUERY_SALES.getCode().equals(intent)) {
            extractSalesEntities(userInput, entities);
        } else if (IntentType.CHECK_INVENTORY.getCode().equals(intent)) {
            extractInventoryEntities(userInput, entities);
        } else if (IntentType.ADD_PRODUCT.getCode().equals(intent)) {
            extractProductEntities(userInput, entities);
        } else if (IntentType.UPDATE_PRICE.getCode().equals(intent)) {
            extractPriceEntities(userInput, entities);
        } else if (IntentType.QUERY_SALES_RANKING.getCode().equals(intent)) {
            extractRankingEntities(userInput, entities);
        } else {
            // 通用实体提取
            extractCommonEntities(userInput, entities);
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
        if (IntentType.CHAT.getCode().equals(intent)) {
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
    
    private void extractRankingEntities(String userInput, Map<String, Object> entities) {
        // 提取时间范围
        if (userInput.contains("今天") || userInput.contains("今日")) {
            entities.put("time_range", "today");
        } else if (userInput.contains("昨天")) {
            entities.put("time_range", "yesterday");
        } else if (userInput.contains("本周") || userInput.contains("这周")) {
            entities.put("time_range", "this_week");
        } else if (userInput.contains("本月") || userInput.contains("这个月")) {
            entities.put("time_range", "this_month");
        } else if (userInput.contains("最近")) {
            entities.put("time_range", "this_week"); // 默认最近为本周
        }
        
        // 提取数量限制
        Pattern numberPattern = Pattern.compile("前(\\d+)名|top\\s*(\\d+)|(\\d+)个");
        Matcher matcher = numberPattern.matcher(userInput.toLowerCase());
        if (matcher.find()) {
            String limitStr = matcher.group(1) != null ? matcher.group(1) : 
                            matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
            try {
                int limit = Integer.parseInt(limitStr);
                entities.put("limit", Math.min(limit, 20)); // 最大20个
            } catch (NumberFormatException e) {
                entities.put("limit", 10); // 默认10个
            }
        } else {
            entities.put("limit", 10); // 默认显示前10名
        }
        
        // 标记这是排行查询
        entities.put("query_type", "ranking");
    }
}