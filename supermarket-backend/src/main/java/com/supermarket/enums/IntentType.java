package com.supermarket.enums;

/**
 * AI意图类型枚举
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
public enum IntentType {
    
    /**
     * 销售查询
     */
    QUERY_SALES("QUERY_SALES", "销售查询"),
    
    /**
     * 库存检查
     */
    CHECK_INVENTORY("CHECK_INVENTORY", "库存检查"),
    
    /**
     * 添加商品
     */
    ADD_PRODUCT("ADD_PRODUCT", "添加商品"),
    
    /**
     * 更新价格
     */
    UPDATE_PRICE("UPDATE_PRICE", "更新价格"),
    
    /**
     * 删除商品
     */
    REMOVE_PRODUCT("REMOVE_PRODUCT", "删除商品"),
    
    /**
     * 财务查询
     */
    QUERY_FINANCE("QUERY_FINANCE", "财务查询"),
    
    /**
     * 生成报表
     */
    GENERATE_REPORT("GENERATE_REPORT", "生成报表"),
    
    /**
     * 普通聊天
     */
    CHAT("CHAT", "普通聊天");
    
    private final String code;
    private final String description;
    
    IntentType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static IntentType fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return CHAT; // 默认为普通聊天
        }
        
        for (IntentType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        
        return CHAT; // 未知意图默认为普通聊天
    }
    
    /**
     * 根据名称获取枚举
     */
    public static IntentType fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return CHAT;
        }
        
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CHAT;
        }
    }
}