import axios from 'axios'
import { ElMessage } from 'element-plus'
import JSONbig from 'json-bigint'

// 配置json-bigint，将大整数转换为字符串
const JSONbigString = JSONbig({
  storeAsString: true
})

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 15000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  transformResponse: [
    function (data) {
      if (typeof data === 'string') {
        try {
          // 使用json-bigint处理大整数
          return JSONbigString.parse(data)
        } catch (e) {
          return data
        }
      }
      return data
    }
  ]
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 对于AI相关接口，增加调试日志
    if (response.config.url?.includes('/ai/')) {
      console.log('AI API响应:', response.config.url, response.data)
    }
    
    const { code, message } = response.data
    
    if (code === 200) {
      return response
    } else {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message || '请求失败'))
    }
  },
  (error) => {
    console.error('响应错误:', error)
    
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      window.location.href = '/login'
    } else if (error.response?.status === 403) {
      ElMessage.error('没有权限访问')
    } else if (error.response?.status === 500) {
      ElMessage.error('服务器内部错误')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    
    return Promise.reject(error)
  }
)

export default request