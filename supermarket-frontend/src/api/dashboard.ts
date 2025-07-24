import request from '@/utils/request'

// 仪表板相关接口

/**
 * 获取仪表板统计数据
 */
export const getDashboardStats = () => {
  return request.get('/api/dashboard/stats')
}
