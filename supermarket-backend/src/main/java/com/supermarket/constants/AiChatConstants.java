package com.supermarket.constants;

/**
 * AI聊天相关常量
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
public class AiChatConstants {
    
    /**
     * 默认AI模型名称
     */
    public static final String DEFAULT_MODEL_NAME = "tongyi-qwen";
    
    /**
     * 默认会话标题
     */
    public static final String DEFAULT_CONVERSATION_TITLE = "新对话";
    
    /**
     * 最大消息历史数量
     */
    public static final int MAX_MESSAGE_HISTORY = 100;
    
    /**
     * 默认建议问题数量
     */
    public static final int DEFAULT_SUGGESTION_COUNT = 3;
    
    /**
     * 意图识别最低置信度阈值
     */
    public static final double MIN_CONFIDENCE_THRESHOLD = 0.3;
    
    /**
     * AI响应超时时间（毫秒）
     */
    public static final long AI_RESPONSE_TIMEOUT = 30000L;
    
    /**
     * 默认错误响应
     */
    public static final String DEFAULT_ERROR_RESPONSE = "抱歉，我暂时无法处理您的请求，请稍后再试。";
    
    /**
     * AI服务未配置响应
     */
    public static final String AI_SERVICE_NOT_CONFIGURED = "AI服务尚未配置，请联系管理员设置API密钥。";
    
    /**
     * 时间范围常量
     */
    public static class TimeRange {
        public static final String TODAY = "today";
        public static final String YESTERDAY = "yesterday";
        public static final String THIS_WEEK = "this_week";
        public static final String LAST_WEEK = "last_week";
        public static final String THIS_MONTH = "this_month";
        public static final String LAST_MONTH = "last_month";
        public static final String THIS_YEAR = "this_year";
        public static final String LAST_YEAR = "last_year";
    }
    
    /**
     * 实体类型常量
     */
    public static class EntityType {
        public static final String PRODUCT_NAME = "product_name";
        public static final String TIME_RANGE = "time_range";
        public static final String QUANTITY = "quantity";
        public static final String UNIT = "unit";
        public static final String PRICE = "price";
        public static final String NEW_PRICE = "new_price";
        public static final String PURCHASE_PRICE = "purchase_price";
        public static final String SELLING_PRICE = "selling_price";
    }
    
    /**
     * 建议问题模板
     */
    public static class SuggestionTemplates {
        public static final String[] SALES_SUGGESTIONS = {
            "查看本周销售趋势",
            "哪些商品卖得最好？",
            "今天的营业额是多少？"
        };
        
        public static final String[] INVENTORY_SUGGESTIONS = {
            "哪些商品库存不足？",
            "查看库存预警",
            "需要补货的商品有哪些？"
        };
        
        public static final String[] PRODUCT_SUGGESTIONS = {
            "添加新商品",
            "修改商品价格",
            "查看商品信息"
        };
        
        public static final String[] FINANCE_SUGGESTIONS = {
            "查看本月利润",
            "分析成本结构",
            "生成财务报表"
        };
        
        public static final String[] DEFAULT_SUGGESTIONS = {
            "查看今天的销售情况",
            "检查库存状态",
            "生成销售报表"
        };
    }
    
    /**
     * 正则表达式模式
     */
    public static class RegexPatterns {
        public static final String PRICE_PATTERN = "(\\d+(?:\\.\\d+)?)\\s*元";
        public static final String QUANTITY_PATTERN = "(\\d+)\\s*(个|瓶|包|盒|箱|斤|公斤|kg)";
        public static final String DATE_PATTERN = "(\\d{4})[-/年](\\d{1,2})[-/月](\\d{1,2})[日]?";
        public static final String TIME_PATTERN = "(\\d{1,2})[:时](\\d{1,2})[分]?";
    }
    
    /**
     * 私有构造函数，防止实例化
     */
    private AiChatConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}