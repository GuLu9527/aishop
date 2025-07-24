import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi } from '@/api/user'
import request from '../utils/request'

export interface User {
  id: number
  username: string
  realName: string
  phone?: string
  email?: string
  roles: string[]
}

export const useUserStore = defineStore('user', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))
  const loading = ref(false)

  // 初始化时设置token到请求头
  if (token.value) {
    request.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
  }

  // 登录
  const login = async (username: string, password: string) => {
    loading.value = true
    try {
      const response = await loginApi({
        username,
        password
      })
      
      if (response.data.code === 200) {
        token.value = response.data.data.token
        user.value = response.data.data.userInfo
        localStorage.setItem('token', token.value!)

        // 设置request默认header
        request.defaults.headers.common['Authorization'] = `Bearer ${token.value}`

        console.log('登录成功，用户信息:', user.value)
        return { success: true }
      } else {
        return { success: false, message: response.data.message }
      }
    } catch (error: any) {
      console.error('登录失败:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || '登录失败，请检查网络连接' 
      }
    } finally {
      loading.value = false
    }
  }

  // 退出登录
  const logout = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    delete request.defaults.headers.common['Authorization']
  }

  // 获取用户信息
  const getUserInfo = async () => {
    if (!token.value) return false

    try {
      const response = await getUserInfoApi()
      if (response.data.code === 200) {
        user.value = response.data.data
        return true
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      logout()
    }
    return false
  }

  // 初始化token
  if (token.value) {
    request.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
  }

  return {
    user,
    token,
    loading,
    login,
    logout,
    getUserInfo
  }
})
