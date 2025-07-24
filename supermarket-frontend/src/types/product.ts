// 商品批量操作相关类型定义

/**
 * 操作类型枚举
 */
export enum OperationType {
  UPDATE_STATUS = 'UPDATE_STATUS',              // 批量更新状态
  UPDATE_CATEGORY = 'UPDATE_CATEGORY',          // 批量更新分类
  UPDATE_PRICE = 'UPDATE_PRICE',                // 批量更新价格
  UPDATE_STOCK = 'UPDATE_STOCK',                // 批量更新库存
  SET_PRODUCTION_DATE = 'SET_PRODUCTION_DATE',  // 批量设置生产日期
  DELETE = 'DELETE',                            // 批量删除
  EXPORT = 'EXPORT'                             // 批量导出
}

/**
 * 价格调整类型枚举
 */
export enum PriceAdjustmentType {
  FIXED = 'FIXED',          // 固定价格
  PERCENTAGE = 'PERCENTAGE', // 百分比调整
  AMOUNT = 'AMOUNT'         // 金额调整
}

/**
 * 库存调整类型枚举
 */
export enum StockAdjustmentType {
  SET = 'SET',              // 设置为指定值
  ADD = 'ADD',              // 增加指定数量
  SUBTRACT = 'SUBTRACT'     // 减少指定数量
}

/**
 * 商品批量操作DTO
 */
export interface ProductBatchOperationDTO {
  /** 商品ID列表 */
  productIds: string[]
  /** 操作类型 */
  operationType: OperationType
  /** 新状态（用于批量更新状态） */
  newStatus?: number
  /** 新分类ID（用于批量更新分类） */
  newCategoryId?: number
  /** 价格调整类型（用于批量更新价格） */
  priceAdjustmentType?: PriceAdjustmentType
  /** 价格调整值 */
  priceAdjustmentValue?: number
  /** 库存调整类型（用于批量更新库存） */
  stockAdjustmentType?: StockAdjustmentType
  /** 库存调整值 */
  stockAdjustmentValue?: number
  /** 生产日期（用于批量设置生产日期） */
  productionDate?: string
  /** 操作原因/备注 */
  reason?: string
}

/**
 * 商品批量操作结果DTO
 */
export interface ProductBatchOperationResultDTO {
  /** 操作是否成功 */
  success: boolean
  /** 总操作数量 */
  totalCount: number
  /** 成功操作数量 */
  successCount: number
  /** 失败操作数量 */
  failureCount: number
  /** 成功的商品ID列表 */
  successProductIds: string[]
  /** 失败的商品ID列表 */
  failureProductIds: string[]
  /** 失败原因列表 */
  failureReasons: string[]
  /** 操作详细信息 */
  message: string
  /** 操作类型 */
  operationType: OperationType
}

/**
 * 批量操作表单数据
 */
export interface BatchOperationForm {
  /** 选中的商品ID列表 */
  selectedIds: string[]
  /** 操作类型 */
  operationType: OperationType
  /** 状态值 */
  statusValue?: number
  /** 分类ID */
  categoryId?: number
  /** 价格调整类型 */
  priceAdjustmentType?: PriceAdjustmentType
  /** 价格调整值 */
  priceValue?: number
  /** 库存调整类型 */
  stockAdjustmentType?: StockAdjustmentType
  /** 库存调整值 */
  stockValue?: number
  /** 操作原因 */
  reason?: string
}
