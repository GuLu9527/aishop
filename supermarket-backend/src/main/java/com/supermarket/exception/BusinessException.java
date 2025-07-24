package com.supermarket.exception;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(String message) {
        super(message);
        this.code = "BUSINESS_ERROR";
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * 常用的业务异常代码
     */
    public static class ErrorCode {
        public static final String PARAM_ERROR = "PARAM_ERROR";
        public static final String AUTH_ERROR = "AUTH_ERROR";
        public static final String PERMISSION_ERROR = "PERMISSION_ERROR";
        public static final String PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";
        public static final String PRODUCT_DISABLED = "PRODUCT_DISABLED";
        public static final String INSUFFICIENT_STOCK = "INSUFFICIENT_STOCK";
        public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
        public static final String USER_DISABLED = "USER_DISABLED";
        public static final String INVALID_TOKEN = "INVALID_TOKEN";
        public static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";
        public static final String DUPLICATE_DATA = "DUPLICATE_DATA";
        public static final String DATA_NOT_FOUND = "DATA_NOT_FOUND";
        public static final String OPERATION_FAILED = "OPERATION_FAILED";
    }

    /**
     * 快速创建常用异常的静态方法
     */
    public static BusinessException paramError(String message) {
        return new BusinessException(ErrorCode.PARAM_ERROR, message);
    }

    public static BusinessException authError(String message) {
        return new BusinessException(ErrorCode.AUTH_ERROR, message);
    }

    public static BusinessException permissionError(String message) {
        return new BusinessException(ErrorCode.PERMISSION_ERROR, message);
    }

    public static BusinessException productNotFound(String message) {
        return new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, message);
    }

    public static BusinessException insufficientStock(String message) {
        return new BusinessException(ErrorCode.INSUFFICIENT_STOCK, message);
    }

    public static BusinessException userNotFound(String message) {
        return new BusinessException(ErrorCode.USER_NOT_FOUND, message);
    }

    public static BusinessException invalidToken(String message) {
        return new BusinessException(ErrorCode.INVALID_TOKEN, message);
    }

    public static BusinessException dataNotFound(String message) {
        return new BusinessException(ErrorCode.DATA_NOT_FOUND, message);
    }

    public static BusinessException operationFailed(String message) {
        return new BusinessException(ErrorCode.OPERATION_FAILED, message);
    }
}
