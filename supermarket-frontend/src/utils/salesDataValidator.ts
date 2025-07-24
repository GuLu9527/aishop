/**
 * 销售数据验证工具
 * 用于验证前端接收的数据格式是否符合预期
 */

// 销售概览数据验证
export interface SalesOverviewData {
  totalSales: number
  totalOrders: number
  totalProducts: number
  avgOrderValue: number
  salesTrend: number
  ordersTrend: number
  productsTrend: number
  avgTrend: number
}

// 销售趋势数据验证
export interface SalesTrendData {
  dates: string[]
  salesData: number[]
  ordersData: number[]
  period: string
}

// 分类销售数据验证
export interface CategorySalesData {
  data: Array<{
    name: string
    value: number
    percentage: number
  }>
  totalSales: number
}

// 热销商品数据验证
export interface TopProductsData {
  products: Array<{
    productId: number
    productName: string
    barcode: string
    categoryName: string
    quantity: number
    sales: number
    rank: number
  }>
  limit: number
}

// 收银员业绩数据验证
export interface CashierPerformanceData {
  cashiers: Array<{
    cashierId: number
    cashierName: string
    sales: number
    orders: number
    avgOrderValue: number
    rank: number
  }>
  metric: string
}

/**
 * 验证销售概览数据
 */
export const validateSalesOverview = (data: any): SalesOverviewData => {
  const defaultData: SalesOverviewData = {
    totalSales: 0,
    totalOrders: 0,
    totalProducts: 0,
    avgOrderValue: 0,
    salesTrend: 0,
    ordersTrend: 0,
    productsTrend: 0,
    avgTrend: 0
  }

  if (!data || typeof data !== 'object') {
    console.warn('销售概览数据格式错误:', data)
    return defaultData
  }

  return {
    totalSales: safeNumber(data.totalSales),
    totalOrders: safeNumber(data.totalOrders),
    totalProducts: safeNumber(data.totalProducts),
    avgOrderValue: safeNumber(data.avgOrderValue),
    salesTrend: safeNumber(data.salesTrend),
    ordersTrend: safeNumber(data.ordersTrend),
    productsTrend: safeNumber(data.productsTrend),
    avgTrend: safeNumber(data.avgTrend)
  }
}

/**
 * 验证销售趋势数据
 */
export const validateSalesTrend = (data: any): SalesTrendData => {
  const defaultData: SalesTrendData = {
    dates: [],
    salesData: [],
    ordersData: [],
    period: 'day'
  }

  if (!data || typeof data !== 'object') {
    console.warn('销售趋势数据格式错误:', data)
    return defaultData
  }

  return {
    dates: Array.isArray(data.dates) ? data.dates : [],
    salesData: Array.isArray(data.salesData) ? data.salesData.map(safeNumber) : [],
    ordersData: Array.isArray(data.ordersData) ? data.ordersData.map(safeNumber) : [],
    period: data.period || 'day'
  }
}

/**
 * 验证分类销售数据
 */
export const validateCategorySales = (data: any): CategorySalesData => {
  const defaultData: CategorySalesData = {
    data: [],
    totalSales: 0
  }

  if (!data || typeof data !== 'object') {
    console.warn('分类销售数据格式错误:', data)
    return defaultData
  }

  const categoryData = Array.isArray(data.data) ? data.data.map((item: any) => ({
    name: item.name || '',
    value: safeNumber(item.value),
    percentage: safeNumber(item.percentage)
  })) : []

  return {
    data: categoryData,
    totalSales: safeNumber(data.totalSales)
  }
}

/**
 * 验证热销商品数据
 */
export const validateTopProducts = (data: any): TopProductsData => {
  const defaultData: TopProductsData = {
    products: [],
    limit: 10
  }

  if (!data || typeof data !== 'object') {
    console.warn('热销商品数据格式错误:', data)
    return defaultData
  }

  const products = Array.isArray(data.products) ? data.products.map((item: any) => ({
    productId: safeNumber(item.productId),
    productName: item.productName || '',
    barcode: item.barcode || '',
    categoryName: item.categoryName || '',
    quantity: safeNumber(item.quantity),
    sales: safeNumber(item.sales),
    rank: safeNumber(item.rank)
  })) : []

  return {
    products,
    limit: safeNumber(data.limit, 10)
  }
}

/**
 * 验证收银员业绩数据
 */
export const validateCashierPerformance = (data: any): CashierPerformanceData => {
  const defaultData: CashierPerformanceData = {
    cashiers: [],
    metric: 'sales'
  }

  if (!data || typeof data !== 'object') {
    console.warn('收银员业绩数据格式错误:', data)
    return defaultData
  }

  const cashiers = Array.isArray(data.cashiers) ? data.cashiers.map((item: any) => ({
    cashierId: safeNumber(item.cashierId),
    cashierName: item.cashierName || '',
    sales: safeNumber(item.sales),
    orders: safeNumber(item.orders),
    avgOrderValue: safeNumber(item.avgOrderValue),
    rank: safeNumber(item.rank)
  })) : []

  return {
    cashiers,
    metric: data.metric || 'sales'
  }
}

/**
 * 安全获取数值
 */
export const safeNumber = (value: any, defaultValue: number = 0): number => {
  if (value === null || value === undefined) return defaultValue
  const num = typeof value === 'string' ? parseFloat(value) : Number(value)
  return isNaN(num) ? defaultValue : num
}

/**
 * 数据验证器映射
 */
export const dataValidators = {
  salesOverview: validateSalesOverview,
  salesTrend: validateSalesTrend,
  categorySales: validateCategorySales,
  topProducts: validateTopProducts,
  cashierPerformance: validateCashierPerformance
}
