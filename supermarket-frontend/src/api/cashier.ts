import request from '@/utils/request'

/**
 * 商品信息
 */
export interface ProductInfo {
  id: number
  productName: string
  barcode: string
  sellingPrice: number
  stockQuantity: number
  unit: string
  categoryName: string
  status: number
}

/**
 * 购物车商品
 */
export interface CartItem extends ProductInfo {
  quantity: number
}

/**
 * 支付信息
 */
export interface PaymentInfo {
  items: CartItem[]
  paymentMethod: string
  totalAmount: number
  receivedAmount?: number
  changeAmount?: number
  cashierId: number
  terminalId: string
}

/**
 * 销售记录
 */
export interface SaleRecord {
  id: string
  orderNo: string
  items: CartItem[]
  totalAmount: number
  paymentMethod: string
  receivedAmount?: number
  changeAmount?: number
  cashierId: number
  cashierName: string
  terminalId: string
  saleTime: string
  status: number
}

/**
 * 挂单信息
 */
export interface HeldTransaction {
  id: string
  items: CartItem[]
  itemCount: number
  totalAmount: number
  cashierId: number
  terminalId: string
  createTime: string
}

/**
 * 根据条码查询商品
 */
export const getProductByBarcode = (barcode: string) => {
  return request.get('/api/cashier/product', { params: { barcode } })
}

/**
 * 获取商品列表（用于手动选择）
 */
export const getProductList = (params: {
  keyword?: string
  categoryId?: number
  pageNum?: number
  pageSize?: number
}) => {
  return request.get('/api/cashier/products', { params })
}

/**
 * 获取商品分类列表
 */
export const getCategoryList = () => {
  return request.get('/api/cashier/categories')
}

/**
 * 处理支付
 */
export const processPayment = (paymentInfo: PaymentInfo) => {
  return request.post('/api/cashier/payment', paymentInfo)
}

/**
 * 保存挂单
 */
export const saveHeldTransaction = (transaction: Omit<HeldTransaction, 'id' | 'createTime'>) => {
  return request.post('/api/cashier/hold', transaction)
}

/**
 * 获取挂单列表
 */
export const getHeldTransactions = (cashierId: number, terminalId: string) => {
  return request.get('/api/cashier/held-transactions', { 
    params: { cashierId, terminalId } 
  })
}

/**
 * 恢复挂单
 */
export const resumeHeldTransaction = (transactionId: string) => {
  return request.post(`/api/cashier/resume/${transactionId}`)
}

/**
 * 删除挂单
 */
export const deleteHeldTransaction = (transactionId: string) => {
  return request.delete(`/api/cashier/held-transaction/${transactionId}`)
}

/**
 * 获取销售记录
 */
export const getSaleRecords = (params: {
  cashierId?: number
  terminalId?: string
  startDate?: string
  endDate?: string
  pageNum?: number
  pageSize?: number
}) => {
  return request.get('/api/cashier/sales', { params })
}

/**
 * 重打小票
 */
export const reprintReceipt = (saleId: string) => {
  return request.post(`/api/cashier/reprint/${saleId}`)
}

/**
 * 开钱箱
 */
export const openCashDrawer = (terminalId: string) => {
  return request.post('/api/cashier/open-drawer', { terminalId })
}

/**
 * 获取收银员信息
 */
export const getCashierInfo = () => {
  return request.get('/api/cashier/info')
}
