import request from '@/utils/request'

// 用户相关接口

/**
 * 用户登录
 */
export const login = (data: { username: string; password: string }) => {
  return request.post('/api/auth/login', data)
}

/**
 * 获取用户信息
 */
export const getUserInfo = () => {
  return request.get('/api/auth/userinfo')
}

/**
 * 用户登出
 */
export const logout = () => {
  return request.post('/api/auth/logout')
}
