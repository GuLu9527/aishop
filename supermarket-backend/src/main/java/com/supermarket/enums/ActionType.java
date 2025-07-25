package com.supermarket.enums;

/**
 * AI操作类型枚举
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
public enum ActionType {
    
    /**
     * 查询销售数据
     */
    QUERY_SALES_DATA("QUERY_SALES_DATA", "查询销售数据"),
    
    /**
     * 检查库存状态
     */
    CHECK_INVENTORY_STATUS("CHECK_INVENTORY_STATUS", "检查库存状态"),
    
    /**
     * 检查库存（简化版）
     */
    CHECK_INVENTORY("CHECK_INVENTORY", "检查库存"),
    
    /**
     * 添加新商品
     */
    ADD_NEW_PRODUCT("ADD_NEW_PRODUCT", "添加新商品"),
    
    /**
     * 添加商品（简化版）
     */
    ADD_PRODUCT("ADD_PRODUCT", "添加商品"),
    
    /**
     * 更新商品价格
     */
    UPDATE_PRODUCT_PRICE("UPDATE_PRODUCT_PRICE", "更新商品价格"),
    
    /**
     * 删除商品
     */
    DELETE_PRODUCT("DELETE_PRODUCT", "删除商品"),
    
    /**
     * 获取财务概览
     */
    GET_FINANCIAL_OVERVIEW("GET_FINANCIAL_OVERVIEW", "获取财务概览"),
    
    /**
     * 查询财务信息
     */
    QUERY_FINANCE("QUERY_FINANCE", "查询财务信息"),
    
    /**
     * 生成销售报表
     */
    GENERATE_SALES_REPORT("GENERATE_SALES_REPORT", "生成销售报表"),
    
    /**
     * 获取库存预警
     */
    GET_INVENTORY_ALERTS("GET_INVENTORY_ALERTS", "获取库存预警"),
    
    /**
     * 获取销售排行
     */
    GET_SALES_RANKING("GET_SALES_RANKING", "获取销售排行"),
    
    /**
     * 无操作
     */
    NO_ACTION("NO_ACTION", "无操作");
    
    private final String code;
    private final String description;
    
    ActionType(String code, String description) {
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
    public static ActionType fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return NO_ACTION;
        }
        
        for (ActionType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        
        return NO_ACTION;
    }
    
    /**
     * 根据名称获取枚举
     */
    public static ActionType fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return NO_ACTION;
        }
        
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NO_ACTION;
        }
    }
}