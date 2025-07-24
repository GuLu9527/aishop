import request from '@/utils/request'

// 供货商接口类型定义
export interface Supplier {
  id?: number
  supplierName: string
  contactPerson?: string
  phone?: string
  address?: string
  email?: string
  creditRating?: number
  paymentTerms?: string
  deliveryCycle?: number
  status: number
  createTime?: string
  updateTime?: string
  productCount?: number
  lastPurchaseTime?: string
}

export interface SupplierQuery {
  pageNum: number
  pageSize: number
  supplierName?: string
  contactPerson?: string
  phone?: string
  creditRating?: number
  status?: number
  createTimeStart?: string
  createTimeEnd?: string
}

/**
 * 分页查询供货商列表
 */
export const getSupplierPage = (query: SupplierQuery) => {
  return request({
    url: '/api/suppliers/page',
    method: 'post',
    data: query
  })
}

/**
 * 根据ID查询供货商详情
 */
export const getSupplierById = (id: number) => {
  return request({
    url: `/api/suppliers/${id}`,
    method: 'get'
  })
}

/**
 * 添加供货商
 */
export const addSupplier = (data: Supplier) => {
  return request({
    url: '/api/suppliers',
    method: 'post',
    data
  })
}

/**
 * 更新供货商
 */
export const updateSupplier = (id: number, data: Supplier) => {
  return request({
    url: `/api/suppliers/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除供货商
 */
export const deleteSupplier = (id: number) => {
  return request({
    url: `/api/suppliers/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除供货商
 */
export const batchDeleteSuppliers = (ids: number[]) => {
  return request({
    url: '/api/suppliers/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 启用/禁用供货商
 */
export const updateSupplierStatus = (id: number, status: number) => {
  return request({
    url: `/api/suppliers/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 查询所有启用的供货商
 */
export const getActiveSuppliers = () => {
  return request({
    url: '/api/suppliers/active',
    method: 'get'
  })
}

/**
 * 检查供货商名称是否存在
 */
export const checkSupplierName = (supplierName: string, excludeId?: number) => {
  return request({
    url: '/api/suppliers/check-name',
    method: 'get',
    params: { supplierName, excludeId }
  })
}

/**
 * 获取供货商统计信息
 */
export const getSupplierStats = () => {
  return request({
    url: '/api/suppliers/stats',
    method: 'get'
  })
}

/**
 * 导出供货商数据
 */
export const exportSuppliers = (query?: SupplierQuery) => {
  return request({
    url: '/api/suppliers/export',
    method: 'post',
    data: query || {},
    responseType: 'blob'
  })
}
