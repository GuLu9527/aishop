/**
 * 操作反馈管理器
 * 
 * @author AI Assistant
 * @since 2025-01-22
 */

import { ElMessage, ElNotification } from 'element-plus'

// 反馈类型
export type FeedbackType = 'success' | 'warning' | 'info' | 'error' | 'loading'

// 反馈配置
export interface FeedbackConfig {
  type: FeedbackType
  message: string
  duration?: number
  showClose?: boolean
  position?: 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left'
}

// 操作反馈配置
export interface OperationFeedbackConfig extends FeedbackConfig {
  operation: string
  details?: string
  actionText?: string
  onAction?: () => void
}

/**
 * 反馈管理器类
 */
export class FeedbackManager {
  private static instance: FeedbackManager
  private feedbackQueue: FeedbackConfig[] = []
  private isProcessing = false

  private constructor() {}

  public static getInstance(): FeedbackManager {
    if (!FeedbackManager.instance) {
      FeedbackManager.instance = new FeedbackManager()
    }
    return FeedbackManager.instance
  }

  /**
   * 显示成功反馈
   */
  public success(message: string, duration = 3000): void {
    this.showMessage({
      type: 'success',
      message,
      duration
    })
  }

  /**
   * 显示警告反馈
   */
  public warning(message: string, duration = 4000): void {
    this.showMessage({
      type: 'warning',
      message,
      duration
    })
  }

  /**
   * 显示信息反馈
   */
  public info(message: string, duration = 3000): void {
    this.showMessage({
      type: 'info',
      message,
      duration
    })
  }

  /**
   * 显示错误反馈
   */
  public error(message: string, duration = 5000): void {
    this.showMessage({
      type: 'error',
      message,
      duration
    })
  }

  /**
   * 显示加载反馈
   */
  public loading(message: string): () => void {
    const loadingInstance = ElMessage({
      type: 'info',
      message,
      duration: 0,
      showClose: false,
      customClass: 'loading-message'
    })

    return () => {
      loadingInstance.close()
    }
  }

  /**
   * 显示操作成功反馈
   */
  public operationSuccess(operation: string, details?: string): void {
    const message = details ? `${operation}成功：${details}` : `${operation}成功`
    
    this.showNotification({
      type: 'success',
      message: operation,
      details,
      duration: 3000
    })

    this.success(message)
  }

  /**
   * 显示操作失败反馈
   */
  public operationError(operation: string, error: string): void {
    const message = `${operation}失败：${error}`
    
    this.showNotification({
      type: 'error',
      message: operation,
      details: error,
      duration: 5000
    })

    this.error(message)
  }

  /**
   * 显示操作警告反馈
   */
  public operationWarning(operation: string, warning: string): void {
    const message = `${operation}警告：${warning}`
    
    this.showNotification({
      type: 'warning',
      message: operation,
      details: warning,
      duration: 4000
    })

    this.warning(message)
  }

  /**
   * 显示AI操作反馈
   */
  public aiOperation(operation: string, status: 'start' | 'success' | 'error', details?: string): void {
    switch (status) {
      case 'start':
        this.info(`AI正在${operation}...`)
        break
      case 'success':
        this.operationSuccess(`AI${operation}`, details)
        break
      case 'error':
        this.operationError(`AI${operation}`, details || '操作失败')
        break
    }
  }

  /**
   * 显示网络操作反馈
   */
  public networkOperation(operation: string, status: 'start' | 'success' | 'error' | 'timeout', details?: string): void {
    switch (status) {
      case 'start':
        this.info(`正在${operation}...`)
        break
      case 'success':
        this.operationSuccess(operation, details)
        break
      case 'error':
        this.operationError(operation, details || '网络错误')
        break
      case 'timeout':
        this.operationError(operation, '请求超时，请稍后重试')
        break
    }
  }

  /**
   * 显示数据操作反馈
   */
  public dataOperation(operation: string, count?: number, details?: string): void {
    let message = operation
    if (count !== undefined) {
      message += `（${count}条记录）`
    }
    if (details) {
      message += `：${details}`
    }
    
    this.operationSuccess(message)
  }

  /**
   * 显示用户操作反馈
   */
  public userAction(action: string, result: 'success' | 'cancel' | 'error', details?: string): void {
    switch (result) {
      case 'success':
        this.success(`${action}成功${details ? '：' + details : ''}`)
        break
      case 'cancel':
        this.info(`已取消${action}`)
        break
      case 'error':
        this.error(`${action}失败${details ? '：' + details : ''}`)
        break
    }
  }

  /**
   * 显示消息
   */
  private showMessage(config: FeedbackConfig): void {
    ElMessage({
      type: config.type === 'error' ? 'error' : config.type,
      message: config.message,
      duration: config.duration || 3000,
      showClose: config.showClose || false
    })
  }

  /**
   * 显示通知
   */
  private showNotification(config: {
    type: FeedbackType
    message: string
    details?: string
    duration?: number
  }): void {
    ElNotification({
      type: config.type === 'error' ? 'error' : config.type,
      title: config.message,
      message: config.details || '',
      duration: config.duration || 4000,
      position: 'top-right'
    })
  }

  /**
   * 清除所有反馈
   */
  public clear(): void {
    ElMessage.closeAll()
  }
}

// 导出单例实例
export const feedbackManager = FeedbackManager.getInstance()

// 便捷方法
export const showSuccess = (message: string, duration?: number) => {
  feedbackManager.success(message, duration)
}

export const showWarning = (message: string, duration?: number) => {
  feedbackManager.warning(message, duration)
}

export const showInfo = (message: string, duration?: number) => {
  feedbackManager.info(message, duration)
}

export const showError = (message: string, duration?: number) => {
  feedbackManager.error(message, duration)
}

export const showLoading = (message: string) => {
  return feedbackManager.loading(message)
}

export const showOperationSuccess = (operation: string, details?: string) => {
  feedbackManager.operationSuccess(operation, details)
}

export const showOperationError = (operation: string, error: string) => {
  feedbackManager.operationError(operation, error)
}

export const showAiOperation = (operation: string, status: 'start' | 'success' | 'error', details?: string) => {
  feedbackManager.aiOperation(operation, status, details)
}

export const showNetworkOperation = (operation: string, status: 'start' | 'success' | 'error' | 'timeout', details?: string) => {
  feedbackManager.networkOperation(operation, status, details)
}

export const showUserAction = (action: string, result: 'success' | 'cancel' | 'error', details?: string) => {
  feedbackManager.userAction(action, result, details)
}
