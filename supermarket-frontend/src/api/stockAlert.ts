import request from '@/utils/request'

/**
 * 库存预警查询参数
 */
export interface StockAlertQueryParams {
  pageNum?: number
  pageSize?: number
  alertType?: number
  status?: number
  productName?: string
  urgencyLevel?: number
  categoryId?: string
  startDate?: string
  endDate?: string
}

/**
 * 库存预警信息
 */
export interface StockAlert {
  id: string
  productId: string
  productName: string
  barcode: string
  categoryName: string
  alertType: number
  alertTypeText: string
  currentStock: number
  stockQuantity: number
  thresholdValue: number
  shelfLifeDays: number
  expireDate: string
  remainingDays: number
  alertMessage: string
  status: number
  statusText: string
  handlerId: string
  handlerName: string
  handleTime: string
  createTime: string
  updateTime: string
  urgencyLevel: number
  urgencyText: string
}

/**
 * 预警统计信息
 */
export interface AlertStatistics {
  lowStockCount: number
  highStockCount: number
  expiringCount: number
  totalPendingCount: number
}

/**
 * 分页查询库存预警列表
 */
export const getStockAlertPage = (params: StockAlertQueryParams) => {
  return request.get('/api/stock-alerts/page', { params })
}

/**
 * 获取预警统计信息
 */
export const getAlertStatistics = () => {
  return request.get('/api/stock-alerts/statistics')
}

/**
 * 获取未处理预警数量
 */
export const getPendingAlertCount = () => {
  return request.get('/api/stock-alerts/pending-count')
}

/**
 * 获取临期商品列表
 */
export const getExpiringProducts = (warningDays?: number) => {
  return request.get('/api/stock-alerts/expiring-products', {
    params: { warningDays }
  })
}

/**
 * 处理预警
 */
export const handleAlert = (alertId: string, action: string, handlerId: string) => {
  return request.put(`/api/stock-alerts/${alertId}/handle`, null, {
    params: { action, handlerId }
  })
}

/**
 * 批量处理预警
 */
export const batchHandleAlerts = (alertIds: string[], action: string, handlerId: string) => {
  return request.put('/api/stock-alerts/batch-handle', alertIds, {
    params: { action, handlerId }
  })
}

/**
 * 手动检查临期商品
 */
export const checkExpiringProducts = () => {
  return request.post('/api/stock-alerts/check-expiring')
}

/**
 * 手动检查低库存商品
 */
export const checkLowStockProducts = () => {
  return request.post('/api/stock-alerts/check-low-stock')
}

/**
 * 清理已处理的历史预警
 */
export const cleanupHandledAlerts = (retentionDays: number = 30) => {
  return request.delete('/api/stock-alerts/cleanup', {
    params: { retentionDays }
  })
}
