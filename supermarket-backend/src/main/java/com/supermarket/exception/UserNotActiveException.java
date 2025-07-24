package com.supermarket.exception;

/**
 * 用户未激活异常
 * 当尝试访问已被禁用的用户账户时抛出此异常
 */
public class UserNotActiveException extends RuntimeException {
    
    public UserNotActiveException(String message) {
        super(message);
    }
    
    public UserNotActiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
