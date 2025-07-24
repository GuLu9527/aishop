// API相关类型定义

// 通用响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页响应类型
export interface PageResponse<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 商品相关类型
export interface Product {
  id: string  // 长整型ID作为字符串处理
  productName: string
  barcode?: string
  categoryId: number
  categoryName?: string
  brand?: string
  unit: string
  specification?: string
  purchasePrice?: number
  sellingPrice: number
  stockQuantity: number
  minStock: number
  maxStock: number
  shelfLifeDays?: number
  productionDate?: string  // 新增：生产日期
  expirationDate?: string  // 新增：过期日期（计算字段）
  remainingDays?: number   // 新增：剩余天数（计算字段）
  isExpired?: boolean      // 新增：是否过期（计算字段）
  isExpiring?: boolean     // 新增：是否临期（计算字段）
  imageUrl?: string
  description?: string
  manufacturer?: string
  status: number
  createTime?: string
  updateTime?: string
  isLowStock?: boolean
}

// 商品表单类型
export interface ProductForm {
  id?: string | null  // 长整型ID作为字符串处理
  productName: string
  barcode?: string
  categoryId?: number | null
  brand?: string
  unit: string
  specification?: string
  purchasePrice?: number
  sellingPrice: number
  stockQuantity: number
  minStock: number
  maxStock: number
  shelfLifeDays?: number | null
  productionDate?: string | null  // 新增：生产日期
  manufacturer?: string
  description?: string
}

// 商品查询参数
export interface ProductQuery {
  productName?: string
  barcode?: string
  categoryId?: number | null
  brand?: string
  status?: number | null
  lowStock?: boolean
  productionDateStart?: string  // 新增：生产日期开始
  productionDateEnd?: string    // 新增：生产日期结束
  expiring?: boolean           // 新增：是否查询临期商品
  expiringDays?: number        // 新增：临期预警天数
  pageNum: number
  pageSize: number
}

// 商品分类类型
export interface ProductCategory {
  id: number
  categoryName: string
  parentId: number
  description?: string
  sortOrder: number
  status: number
  createTime?: string
  updateTime?: string
}

// 用户类型
export interface User {
  id: number
  username: string
  realName?: string
  phone?: string
  email?: string
  avatar?: string
  roles?: string[]
}

// 登录响应类型
export interface LoginResponse {
  token: string
  userInfo: User
}

// 仪表板统计类型
export interface DashboardStats {
  totalProducts: number
  todaySales: number
  lowStockCount: number
  expiringSoonCount: number
}

// 批量设置生产日期DTO
export interface BatchProductionDateDTO {
  productIds: string[]
  productionDate: string
  reason?: string
}

// 过期统计信息
export interface ExpirationStatistics {
  expiringCount: number        // 临期商品数量
  expiredCount: number         // 已过期商品数量
  totalWithProductionDate: number  // 有生产日期的商品总数
  expirationRate: number       // 过期率
  success: boolean
  message: string
}

// 批量操作结果
export interface BatchOperationResult {
  totalCount: number
  successCount: number
  failureCount: number
  successIds: string[]
  failures: Array<{
    id: string
    reason: string
  }>
  completed: boolean
}
