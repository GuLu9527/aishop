package com.supermarket.exception;

/**
 * 认证异常
 * 当用户认证失败时抛出此异常
 */
public class AuthenticationException extends RuntimeException {
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
