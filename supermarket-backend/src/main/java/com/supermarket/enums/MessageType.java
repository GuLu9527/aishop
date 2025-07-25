package com.supermarket.enums;

/**
 * 消息类型枚举
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
public enum MessageType {
    
    /**
     * 用户消息
     */
    USER(1, "用户消息"),
    
    /**
     * AI回复
     */
    AI(2, "AI回复"),
    
    /**
     * 系统消息
     */
    SYSTEM(3, "系统消息");
    
    private final Integer code;
    private final String description;
    
    MessageType(Integer code, String description) {
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
    public static MessageType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        
        for (MessageType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("未知的消息类型代码: " + code);
    }
    
    /**
     * 根据名称获取枚举
     */
    public static MessageType fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("未知的消息类型名称: " + name);
        }
    }
}