package com.supermarket.exception;

/**
 * 用户未找到异常
 * 当尝试访问不存在的用户时抛出此异常
 */
public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
