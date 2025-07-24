import request from '@/utils/request'

// 财务记录相关接口

/**
 * 分页查询财务记录
 */
export const getFinanceRecordPage = (params: any) => {
  return request({
    url: '/api/finance/records',
    method: 'get',
    params
  })
}

/**
 * 根据ID查询财务记录详情
 */
export const getFinanceRecordById = (id: number) => {
  return request({
    url: `/api/finance/records/${id}`,
    method: 'get'
  })
}

/**
 * 添加财务记录
 */
export const addFinanceRecord = (data: any) => {
  return request({
    url: '/api/finance/records',
    method: 'post',
    data
  })
}

/**
 * 更新财务记录
 */
export const updateFinanceRecord = (id: number, data: any) => {
  return request({
    url: `/api/finance/records/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除财务记录
 */
export const deleteFinanceRecord = (id: number) => {
  return request({
    url: `/api/finance/records/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除财务记录
 */
export const batchDeleteFinanceRecords = (ids: number[]) => {
  return request({
    url: '/api/finance/records/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 获取财务统计信息
 */
export const getFinanceStats = (params?: any) => {
  return request({
    url: '/api/finance/stats',
    method: 'get',
    params
  })
}

/**
 * 获取今日财务统计
 */
export const getTodayFinanceStats = () => {
  return request({
    url: '/api/finance/stats/today',
    method: 'get'
  })
}

/**
 * 获取本月财务统计
 */
export const getMonthFinanceStats = () => {
  return request({
    url: '/api/finance/stats/month',
    method: 'get'
  })
}

/**
 * 导出财务记录数据
 */
export const exportFinanceRecords = (params: any) => {
  return request({
    url: '/api/finance/records/export',
    method: 'get',
    params
  })
}
