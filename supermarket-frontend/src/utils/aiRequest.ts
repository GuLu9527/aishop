import axios, { type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

// 创建专用于AI客服的axios实例
const aiRequest = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 15000,
  withCredentials: false, // AI客服请求不需要凭证，避免CORS预检请求
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'Origin': 'http://localhost:5173',
    'X-Requested-With': 'XMLHttpRequest'
  }
})

// 请求拦截器
aiRequest.interceptors.request.use(
  (config) => {
    // 对于GET请求，确保参数正确编码
    if (config.method === 'get' && config.params) {
      const searchParams = new URLSearchParams()
      Object.keys(config.params).forEach(key => {
        if (config.params[key] !== null && config.params[key] !== undefined) {
          searchParams.append(key, String(config.params[key]))
        }
      })
      config.paramsSerializer = () => searchParams.toString()
    }
    
    console.log('AI请求配置:', {
      url: config.url,
      method: config.method,
      params: config.params,
      data: config.data
    })
    
    return config
  },
  (error) => {
    console.error('AI请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
aiRequest.interceptors.response.use(
  (response) => {
    console.log('AI响应成功:', response.data)
    return response
  },
  (error) => {
    console.error('AI响应错误:', error)
    
    // 详细的CORS错误诊断
    if (error.code === 'ERR_NETWORK') {
      console.error('网络错误，可能是CORS问题:', {
        url: error.config?.url,
        method: error.config?.method,
        headers: error.config?.headers
      })
      ElMessage.error('网络连接失败，请检查服务器状态')
    } else if (error.response?.status === 0) {
      console.error('CORS预检请求失败')
      ElMessage.error('跨域请求失败，请联系管理员')
    } else {
      ElMessage.error(error.message || '请求失败')
    }
    
    return Promise.reject(error)
  }
)

// 封装GET请求
export const aiGet = <T = any>(url: string, params?: any): Promise<AxiosResponse<T>> => {
  return aiRequest.get(url, { params })
}

// 封装POST请求
export const aiPost = <T = any>(url: string, data?: any): Promise<AxiosResponse<T>> => {
  return aiRequest.post(url, data)
}

// 封装PUT请求
export const aiPut = <T = any>(url: string, data?: any): Promise<AxiosResponse<T>> => {
  return aiRequest.put(url, data)
}

// 封装DELETE请求
export const aiDelete = <T = any>(url: string): Promise<AxiosResponse<T>> => {
  return aiRequest.delete(url)
}

export default aiRequest