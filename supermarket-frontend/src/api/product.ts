import request from '@/utils/request'
import type { ProductQuery, ProductForm } from '@/types/api'

// 商品相关接口

/**
 * 分页查询商品列表
 */
export const getProductPage = (params: Partial<ProductQuery>) => {
  return request.post('/api/products/page', params)
}

/**
 * 根据ID查询商品详情
 */
export const getProductById = (id: number) => {
  return request.get(`/api/products/${id}`)
}

/**
 * 根据条码查询商品
 */
export const getProductByBarcode = (barcode: string) => {
  return request.get(`/api/products/barcode/${barcode}`)
}

/**
 * 添加商品
 */
export const addProduct = (data: ProductForm) => {
  return request.post('/api/products', data)
}

/**
 * 更新商品
 */
export const updateProduct = (id: string, data: ProductForm) => {
  return request.put(`/api/products/${id}`, data)
}

/**
 * 删除商品
 */
export const deleteProduct = (id: string) => {
  return request.delete(`/api/products/${id}`)
}

/**
 * 批量删除商品
 */
export const deleteProducts = (ids: string[]) => {
  return request.delete('/api/products/batch', { data: ids })
}

/**
 * 批量操作商品
 */
export const batchOperation = (data: any) => {
  return request.post('/api/products/batch-operation', data)
}

/**
 * 批量更新商品状态
 */
export const batchUpdateStatus = (productIds: string[], status: number) => {
  return request.put(`/api/products/batch/status?status=${status}`, productIds)
}

/**
 * 批量更新商品分类
 */
export const batchUpdateCategory = (productIds: string[], categoryId: number) => {
  return request.put(`/api/products/batch/category?categoryId=${categoryId}`, productIds)
}

/**
 * 批量删除商品（改进版）
 */
export const batchDeleteProducts = (productIds: string[]) => {
  return request.delete('/api/products/batch-delete', { data: productIds })
}

/**
 * 更新商品库存
 */
export const updateProductStock = (id: string, quantity: number, reason: string) => {
  return request.put(`/api/products/${id}/stock`, null, {
    params: { quantity, reason }
  })
}

/**
 * 查询低库存商品
 */
export const getLowStockProducts = () => {
  return request.get('/api/products/low-stock')
}

/**
 * 启用/禁用商品
 */
export const updateProductStatus = (id: string, status: number) => {
  return request.put(`/api/products/${id}/status`, null, {
    params: { status }
  })
}

/**
 * 批量设置商品生产日期
 */
export const batchSetProductionDate = (productIds: string[], productionDate: string, reason?: string) => {
  return request.put('/api/products/batch-production-date', {
    productIds,
    productionDate,
    reason
  })
}

/**
 * 查询临期商品
 */
export const getExpiringProducts = (warningDays: number = 7) => {
  return request.get(`/api/products/expiring?warningDays=${warningDays}`)
}

/**
 * 查询已过期商品
 */
export const getExpiredProducts = () => {
  return request.get('/api/products/expired')
}

/**
 * 获取过期统计信息
 */
export const getExpirationStatistics = () => {
  return request.get('/api/products/expiration-statistics')
}
