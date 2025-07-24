/**
 * 错误处理工具类
 * 
 * @author AI Assistant
 * @since 2025-01-22
 */

import { ElMessage, ElNotification } from 'element-plus'

// 错误类型枚举
export enum ErrorType {
  NETWORK = 'NETWORK',
  TIMEOUT = 'TIMEOUT',
  SERVER = 'SERVER',
  VALIDATION = 'VALIDATION',
  AUTH = 'AUTH',
  UNKNOWN = 'UNKNOWN'
}

// 错误信息接口
export interface ErrorInfo {
  type: ErrorType
  code?: string | number
  message: string
  originalError?: any
  timestamp: Date
}

// 错误处理配置
export interface ErrorHandlerConfig {
  showMessage?: boolean
  showNotification?: boolean
  logToConsole?: boolean
  reportToServer?: boolean
}

/**
 * 错误处理器类
 */
export class ErrorHandler {
  private static instance: ErrorHandler
  private config: ErrorHandlerConfig = {
    showMessage: true,
    showNotification: false,
    logToConsole: true,
    reportToServer: false
  }

  private constructor() {}

  public static getInstance(): ErrorHandler {
    if (!ErrorHandler.instance) {
      ErrorHandler.instance = new ErrorHandler()
    }
    return ErrorHandler.instance
  }

  /**
   * 设置配置
   */
  public setConfig(config: Partial<ErrorHandlerConfig>): void {
    this.config = { ...this.config, ...config }
  }

  /**
   * 处理错误
   */
  public handle(error: any, config?: Partial<ErrorHandlerConfig>): ErrorInfo {
    const errorInfo = this.parseError(error)
    const finalConfig = { ...this.config, ...config }

    // 控制台日志
    if (finalConfig.logToConsole) {
      console.error('错误处理:', errorInfo)
    }

    // 显示消息
    if (finalConfig.showMessage) {
      this.showErrorMessage(errorInfo)
    }

    // 显示通知
    if (finalConfig.showNotification) {
      this.showErrorNotification(errorInfo)
    }

    // 上报服务器
    if (finalConfig.reportToServer) {
      this.reportToServer(errorInfo)
    }

    return errorInfo
  }

  /**
   * 解析错误
   */
  private parseError(error: any): ErrorInfo {
    const timestamp = new Date()

    // 网络错误
    if (error.code === 'NETWORK_ERROR' || error.message?.includes('Network Error')) {
      return {
        type: ErrorType.NETWORK,
        code: error.code || 'NETWORK_ERROR',
        message: '网络连接失败，请检查网络设置',
        originalError: error,
        timestamp
      }
    }

    // 超时错误
    if (error.code === 'TIMEOUT' || error.message?.includes('timeout')) {
      return {
        type: ErrorType.TIMEOUT,
        code: error.code || 'TIMEOUT',
        message: '请求超时，请稍后重试',
        originalError: error,
        timestamp
      }
    }

    // HTTP错误
    if (error.response) {
      const status = error.response.status
      const data = error.response.data

      if (status === 401) {
        return {
          type: ErrorType.AUTH,
          code: status,
          message: '登录已过期，请重新登录',
          originalError: error,
          timestamp
        }
      }

      if (status === 403) {
        return {
          type: ErrorType.AUTH,
          code: status,
          message: '权限不足，无法执行此操作',
          originalError: error,
          timestamp
        }
      }

      if (status >= 400 && status < 500) {
        return {
          type: ErrorType.VALIDATION,
          code: status,
          message: data?.message || '请求参数错误',
          originalError: error,
          timestamp
        }
      }

      if (status >= 500) {
        return {
          type: ErrorType.SERVER,
          code: status,
          message: data?.message || '服务器内部错误，请稍后重试',
          originalError: error,
          timestamp
        }
      }
    }

    // AI相关错误
    if (error.message?.includes('AI') || error.message?.includes('模型')) {
      return {
        type: ErrorType.SERVER,
        code: 'AI_ERROR',
        message: 'AI服务暂时不可用，请稍后重试',
        originalError: error,
        timestamp
      }
    }

    // 流式传输错误
    if (error.message?.includes('stream') || error.message?.includes('流式')) {
      return {
        type: ErrorType.NETWORK,
        code: 'STREAM_ERROR',
        message: '数据传输中断，请重新发送消息',
        originalError: error,
        timestamp
      }
    }

    // 默认错误
    return {
      type: ErrorType.UNKNOWN,
      code: error.code || 'UNKNOWN',
      message: error.message || '发生未知错误，请稍后重试',
      originalError: error,
      timestamp
    }
  }

  /**
   * 显示错误消息
   */
  private showErrorMessage(errorInfo: ErrorInfo): void {
    const messageType = this.getMessageType(errorInfo.type)
    
    ElMessage({
      type: messageType,
      message: errorInfo.message,
      duration: this.getMessageDuration(errorInfo.type),
      showClose: true
    })
  }

  /**
   * 显示错误通知
   */
  private showErrorNotification(errorInfo: ErrorInfo): void {
    ElNotification({
      type: 'error',
      title: this.getErrorTitle(errorInfo.type),
      message: errorInfo.message,
      duration: 5000,
      position: 'top-right'
    })
  }

  /**
   * 获取消息类型
   */
  private getMessageType(errorType: ErrorType): 'error' | 'warning' | 'info' {
    switch (errorType) {
      case ErrorType.NETWORK:
      case ErrorType.TIMEOUT:
        return 'warning'
      case ErrorType.VALIDATION:
        return 'warning'
      case ErrorType.AUTH:
        return 'error'
      case ErrorType.SERVER:
        return 'error'
      default:
        return 'error'
    }
  }

  /**
   * 获取消息持续时间
   */
  private getMessageDuration(errorType: ErrorType): number {
    switch (errorType) {
      case ErrorType.NETWORK:
      case ErrorType.TIMEOUT:
        return 4000
      case ErrorType.AUTH:
        return 6000
      default:
        return 3000
    }
  }

  /**
   * 获取错误标题
   */
  private getErrorTitle(errorType: ErrorType): string {
    switch (errorType) {
      case ErrorType.NETWORK:
        return '网络错误'
      case ErrorType.TIMEOUT:
        return '请求超时'
      case ErrorType.SERVER:
        return '服务器错误'
      case ErrorType.VALIDATION:
        return '参数错误'
      case ErrorType.AUTH:
        return '权限错误'
      default:
        return '系统错误'
    }
  }

  /**
   * 上报错误到服务器
   */
  private async reportToServer(errorInfo: ErrorInfo): Promise<void> {
    try {
      // TODO: 实现错误上报逻辑
      console.log('上报错误到服务器:', errorInfo)
    } catch (e) {
      console.error('错误上报失败:', e)
    }
  }
}

// 导出单例实例
export const errorHandler = ErrorHandler.getInstance()

// 便捷方法
export const handleError = (error: any, config?: Partial<ErrorHandlerConfig>): ErrorInfo => {
  return errorHandler.handle(error, config)
}

// 特定错误处理方法
export const handleNetworkError = (error: any): ErrorInfo => {
  return handleError(error, { showNotification: true })
}

export const handleAuthError = (error: any): ErrorInfo => {
  return handleError(error, { 
    showNotification: true,
    reportToServer: true 
  })
}

export const handleValidationError = (error: any): ErrorInfo => {
  return handleError(error, { showMessage: true })
}

export const handleServerError = (error: any): ErrorInfo => {
  return handleError(error, { 
    showNotification: true,
    reportToServer: true 
  })
}
