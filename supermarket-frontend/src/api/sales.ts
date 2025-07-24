import request from '@/utils/request'

// 销售分析相关接口

/**
 * 获取销售概览数据
 */
export const getSalesOverview = (params: {
  startDate: string
  endDate: string
}) => {
  return request.get('/api/sales/overview', { params })
}

/**
 * 获取销售趋势数据
 */
export const getSalesTrend = (params: {
  startDate: string
  endDate: string
  period: 'day' | 'week' | 'month'
}) => {
  return request.get('/api/sales/trend', { params })
}

/**
 * 获取商品分类销售数据
 */
export const getCategorySales = (params: {
  startDate: string
  endDate: string
}) => {
  return request.get('/api/sales/category', { params })
}

/**
 * 获取热销商品排行
 */
export const getTopProducts = (params: {
  startDate: string
  endDate: string
  limit: number
}) => {
  return request.get('/api/sales/top-products', { params })
}

/**
 * 获取收银员业绩数据
 */
export const getCashierPerformance = (params: {
  startDate: string
  endDate: string
  metric: 'sales' | 'orders'
}) => {
  return request.get('/api/sales/cashier-performance', { params })
}

/**
 * 获取时段销售分析数据
 */
export const getTimeAnalysis = (params: {
  startDate: string
  endDate: string
  type: 'hourly' | 'daily'
}) => {
  return request.get('/api/sales/time-analysis', { params })
}

/**
 * 导出销售报表
 */
export const exportSalesReport = (params: {
  startDate: string
  endDate: string
  type: 'overview' | 'detail'
}) => {
  return request.get('/api/sales/export', { 
    params,
    responseType: 'blob'
  })
}

/**
 * 获取实时销售数据
 */
export const getRealTimeSales = () => {
  return request.get('/api/sales/realtime')
}

/**
 * 获取销售预测数据
 */
export const getSalesForecast = (params: {
  days: number
}) => {
  return request.get('/api/sales/forecast', { params })
}

/**
 * 获取商品库存预警
 */
export const getStockAlert = () => {
  return request.get('/api/sales/stock-alert')
}
