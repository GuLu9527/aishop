package com.supermarket.enums;

/**
 * 对话状态枚举
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
public enum ConversationStatus {
    
    /**
     * 进行中
     */
    ACTIVE(1, "进行中"),
    
    /**
     * 已结束
     */
    ENDED(2, "已结束");
    
    private final Integer code;
    private final String description;
    
    ConversationStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static ConversationStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        
        for (ConversationStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("未知的对话状态代码: " + code);
    }
    
    /**
     * 根据名称获取枚举
     */
    public static ConversationStatus fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("未知的对话状态名称: " + name);
        }
    }
}