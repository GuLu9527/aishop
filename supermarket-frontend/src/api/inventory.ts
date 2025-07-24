import request from '@/utils/request'

/**
 * 库存查询参数
 */
export interface InventoryQueryParams {
  productName?: string
  barcode?: string
  categoryId?: number
  stockStatus?: number
  pageNum?: number
  pageSize?: number
}

/**
 * 库存调整参数
 */
export interface StockAdjustParams {
  productId: string
  adjustType: number
  adjustQuantity: number
  reason: string
}

/**
 * 库存统计信息
 */
export interface InventoryStatistics {
  totalProducts: number
  lowStockCount: number
  outOfStockCount: number
  normalStockCount: number
}

/**
 * 库存信息
 */
export interface InventoryItem {
  id: number
  productName: string
  barcode: string
  categoryId: number
  categoryName: string
  brand: string
  unit: string
  specification: string
  stockQuantity: number
  minStock: number
  maxStock: number
  purchasePrice: number
  sellingPrice: number
  stockStatus: number
  stockStatusText: string
  isLowStock: boolean
}

/**
 * 库存记录
 */
export interface StockRecord {
  id: string
  productId: string
  productName: string
  changeType: number
  changeTypeText: string
  changeQuantity: number
  beforeQuantity: number
  afterQuantity: number
  unitPrice?: number
  totalAmount?: number
  reason: string
  batchNo?: string
  expireDate?: string
  operatorId: string
  operatorName: string
  createTime: string
}

/**
 * 分页查询库存列表
 */
export const getInventoryPage = (params: InventoryQueryParams) => {
  return request.get('/api/inventory/page', { params })
}

/**
 * 获取库存统计信息
 */
export const getInventoryStatistics = () => {
  return request.get('/api/inventory/statistics')
}

/**
 * 调整库存
 */
export const adjustStock = (data: StockAdjustParams) => {
  return request.post('/api/inventory/adjust', data)
}

/**
 * 获取库存记录
 */
export const getStockRecords = (params: {
  productId?: string
  pageNum?: number
  pageSize?: number
}) => {
  return request.get('/api/inventory/records', { params })
}

/**
 * 设置库存预警
 */
export const setStockAlert = (productId: string, minStock: number, maxStock: number) => {
  return request.put(`/api/inventory/alert/${productId}`, null, {
    params: { minStock, maxStock }
  })
}

/**
 * 批量调整库存参数
 */
export interface BatchStockAdjustParams {
  productIds: string[]
  adjustType: number
  adjustQuantity: number
  reason: string
}

/**
 * 批量调整结果
 */
export interface BatchAdjustResult {
  totalCount: number
  successCount: number
  failCount: number
  failDetails: string[]
  allSuccess: boolean
}

/**
 * 批量调整库存
 */
export const batchAdjustStock = (data: BatchStockAdjustParams) => {
  return request.post('/api/inventory/batch-adjust', data)
}
