import request from '@/utils/request'

// 商品分类相关接口

/**
 * 查询所有启用的分类
 */
export const getAllCategories = () => {
  return request.get('/api/categories')
}

/**
 * 根据ID查询分类
 */
export const getCategoryById = (id: number) => {
  return request.get(`/api/categories/${id}`)
}

/**
 * 添加分类
 */
export const addCategory = (data: any) => {
  return request.post('/api/categories', data)
}

/**
 * 更新分类
 */
export const updateCategory = (id: number, data: any) => {
  return request.put(`/api/categories/${id}`, data)
}

/**
 * 删除分类
 */
export const deleteCategory = (id: number) => {
  return request.delete(`/api/categories/${id}`)
}
