<template>
  <div class="inventory-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <div class="header-title">
            <el-icon class="header-icon"><Box /></el-icon>
            <h2>库存管理</h2>
          </div>
          <p class="header-desc">管理商品库存，包括库存查询、调整、预警等功能</p>
        </div>
        <div class="header-right">
          <div class="header-stats">
            <div class="stat-card">
              <div class="stat-number">{{ inventoryList.length }}</div>
              <div class="stat-label">商品总数</div>
            </div>
            <div class="stat-card warning">
              <div class="stat-number">{{ lowStockCount }}</div>
              <div class="stat-label">库存预警</div>
            </div>
            <div class="stat-card danger">
              <div class="stat-number">{{ outOfStockCount }}</div>
              <div class="stat-label">缺货商品</div>
            </div>
          </div>
          <el-button type="primary" size="large" @click="showAdjustDialog" class="adjust-btn">
            <el-icon><EditPen /></el-icon>
            批量调整
          </el-button>
        </div>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-card">
        <div class="search-header">
          <el-icon class="search-icon"><Search /></el-icon>
          <span class="search-title">库存筛选</span>
        </div>
        <el-form :model="searchForm" inline class="search-form">
          <el-form-item label="商品名称">
            <el-input
              v-model="searchForm.productName"
              placeholder="请输入商品名称"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="商品条码">
            <el-input
              v-model="searchForm.barcode"
              placeholder="请输入商品条码"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="商品分类">
            <el-select
              v-model="searchForm.categoryId"
              placeholder="请选择分类"
              clearable
              style="width: 150px"
            >
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.categoryName"
                :value="category.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="库存状态">
            <el-select
              v-model="searchForm.stockStatus"
              placeholder="请选择状态"
              clearable
              style="width: 120px"
            >
              <el-option label="正常" :value="1" />
              <el-option label="预警" :value="2" />
              <el-option label="缺货" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchInventory">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetSearch">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 库存列表 -->
    <div class="table-section">
      <div class="table-card">
        <div class="table-header">
          <div class="table-title">
            <el-icon class="table-icon"><List /></el-icon>
            <span>库存列表</span>
          </div>
          <div class="table-actions">
            <el-button size="small" @click="getInventoryList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
            <el-button size="small" type="success" @click="exportInventory">
              <el-icon><Download /></el-icon>
              导出
            </el-button>
          </div>
        </div>
        <el-table
          :data="inventoryList"
          v-loading="loading"
          stripe
          style="width: 100%"
          class="modern-table"
        >
          <el-table-column prop="productName" label="商品名称" min-width="150">
            <template #default="{ row }">
              <div class="product-name">
                <el-icon class="product-icon"><Box /></el-icon>
                <span>{{ row.productName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="barcode" label="条码" width="150" />
          <el-table-column prop="categoryName" label="分类" width="120">
            <template #default="{ row }">
              <el-tag type="info" size="small">{{ row.categoryName }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="unit" label="单位" width="80" />
          <el-table-column prop="stockQuantity" label="当前库存" width="100">
            <template #default="{ row }">
              <div class="stock-info">
                <span :class="getStockClass(row)">
                  {{ row.stockQuantity }}
                </span>
                <el-icon v-if="row.stockQuantity <= row.minStock" class="warning-icon">
                  <Warning />
                </el-icon>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="minStock" label="最低库存" width="100" />
          <el-table-column prop="maxStock" label="最高库存" width="100" />
          <el-table-column label="库存状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStockStatusType(row)" size="small">
                {{ getStockStatusText(row) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-tooltip content="调整库存" placement="top">
                  <button class="action-btn action-btn-primary" @click="adjustStock(row)">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                      <path d="M11.5 3.5L12.5 4.5L5.5 11.5H4.5V10.5L11.5 3.5Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M10.5 4.5L11.5 5.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M13.5 13.5H7.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                  </button>
                </el-tooltip>
                <el-tooltip content="库存记录" placement="top">
                  <button class="action-btn action-btn-info" @click="viewStockRecord(row)">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                      <path d="M3 2H13C13.5523 2 14 2.44772 14 3V13C14 13.5523 13.5523 14 13 14H3C2.44772 14 2 13.5523 2 13V3C2 2.44772 2.44772 2 3 2Z" stroke="currentColor" stroke-width="1.5"/>
                      <path d="M5 6H11" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                      <path d="M5 8H11" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                      <path d="M5 10H9" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                    </svg>
                  </button>
                </el-tooltip>
                <el-tooltip content="设置预警" placement="top">
                  <button class="action-btn action-btn-warning" @click="setStockAlert(row)">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                      <path d="M8 2C6.34315 2 5 3.34315 5 5C5 6.30622 5.83481 7.41749 7 7.82929V10C7 10.5523 7.44772 11 8 11C8.55228 11 9 10.5523 9 10V7.82929C10.1652 7.41749 11 6.30622 11 5C11 3.34315 9.65685 2 8 2Z" stroke="currentColor" stroke-width="1.5"/>
                      <path d="M6 12.5C6 13.0523 6.44772 13.5 7 13.5H9C9.55228 13.5 10 13.0523 10 12.5V12H6V12.5Z" stroke="currentColor" stroke-width="1.5"/>
                    </svg>
                  </button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.pageNum"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <!-- 库存调整对话框 -->
    <el-dialog
      v-model="adjustDialogVisible"
      :title="adjustDialogTitle"
      width="600px"
      @close="resetAdjustForm"
    >
      <el-form
        ref="adjustFormRef"
        :model="adjustForm"
        :rules="adjustRules"
        label-width="120px"
      >
        <el-form-item label="商品名称">
          <el-input v-model="adjustForm.productName" readonly />
        </el-form-item>
        <el-form-item label="当前库存">
          <el-input v-model="adjustForm.currentStock" readonly />
        </el-form-item>
        <el-form-item label="调整类型" prop="adjustType">
          <el-radio-group v-model="adjustForm.adjustType">
            <el-radio :label="1">入库</el-radio>
            <el-radio :label="2">出库</el-radio>
            <el-radio :label="3">盘点</el-radio>
            <el-radio :label="4">损耗</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="调整数量" prop="adjustQuantity">
          <el-input-number
            v-model="adjustForm.adjustQuantity"
            :min="1"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="调整原因" prop="reason">
          <el-input
            v-model="adjustForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入调整原因"
          />
        </el-form-item>
        <el-form-item label="调整后库存">
          <el-input v-model="adjustedStock" readonly />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="adjustDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAdjust" :loading="adjusting">
            确定调整
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 库存记录对话框 -->
    <el-dialog
      v-model="recordDialogVisible"
      title="库存记录"
      width="1000px"
    >
      <div class="record-header">
        <div class="record-info">
          <span class="product-name">{{ recordProductName }}</span>
          <span class="current-stock">当前库存：{{ recordCurrentStock }}</span>
        </div>
      </div>

      <el-table
        :data="stockRecords"
        v-loading="recordLoading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="createTime" label="操作时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="changeTypeText" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getRecordTypeColor(row.changeType)" size="small">
              {{ row.changeTypeText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="changeQuantity" label="变动数量" width="100">
          <template #default="{ row }">
            <span :class="getQuantityClass(row.changeType)">
              {{ getQuantityPrefix(row.changeType) }}{{ row.changeQuantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="beforeQuantity" label="变动前" width="100" />
        <el-table-column prop="afterQuantity" label="变动后" width="100" />
        <el-table-column prop="reason" label="变动原因" min-width="150" />
        <el-table-column prop="operatorName" label="操作人" width="120" />
      </el-table>

      <!-- 分页 -->
      <div class="record-pagination">
        <el-pagination
          v-model:current-page="recordPagination.pageNum"
          v-model:page-size="recordPagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="recordPagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleRecordSizeChange"
          @current-change="handleRecordCurrentChange"
        />
      </div>
    </el-dialog>

    <!-- 库存预警设置对话框 -->
    <el-dialog
      v-model="alertDialogVisible"
      title="库存预警设置"
      width="500px"
    >
      <el-form
        ref="alertFormRef"
        :model="alertForm"
        :rules="alertRules"
        label-width="120px"
      >
        <el-form-item label="商品名称">
          <el-input v-model="alertForm.productName" readonly />
        </el-form-item>
        <el-form-item label="当前库存">
          <el-input v-model="alertForm.currentStock" readonly />
        </el-form-item>
        <el-form-item label="最低库存" prop="minStock">
          <el-input-number
            v-model="alertForm.minStock"
            :min="0"
            style="width: 100%"
            placeholder="请输入最低库存"
          />
        </el-form-item>
        <el-form-item label="最高库存" prop="maxStock">
          <el-input-number
            v-model="alertForm.maxStock"
            :min="0"
            style="width: 100%"
            placeholder="请输入最高库存"
          />
        </el-form-item>
        <el-alert
          title="当库存数量低于或等于最低库存时，系统将显示预警提示"
          type="info"
          :closable="false"
          show-icon
        />
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="alertDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAlert" :loading="alerting">
            确定设置
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量调整对话框 -->
    <el-dialog
      v-model="batchDialogVisible"
      title="批量库存调整"
      width="800px"
    >
      <div class="batch-header">
        <el-alert
          title="请选择要调整的商品，然后设置调整参数"
          type="info"
          :closable="false"
          show-icon
        />
      </div>

      <div class="batch-selection">
        <el-table
          ref="batchTableRef"
          :data="inventoryList"
          @selection-change="handleBatchSelectionChange"
          max-height="300"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="productName" label="商品名称" min-width="150" />
          <el-table-column prop="stockQuantity" label="当前库存" width="100" />
          <el-table-column prop="categoryName" label="分类" width="120" />
        </el-table>
      </div>

      <el-form
        ref="batchFormRef"
        :model="batchForm"
        :rules="batchRules"
        label-width="120px"
        style="margin-top: 20px"
      >
        <el-form-item label="调整类型" prop="adjustType">
          <el-radio-group v-model="batchForm.adjustType">
            <el-radio :label="1">入库</el-radio>
            <el-radio :label="2">出库</el-radio>
            <el-radio :label="3">盘点</el-radio>
            <el-radio :label="4">损耗</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="调整数量" prop="adjustQuantity">
          <el-input-number
            v-model="batchForm.adjustQuantity"
            :min="1"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="调整原因" prop="reason">
          <el-input
            v-model="batchForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入调整原因"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="batchDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitBatchAdjust" :loading="batchAdjusting">
            批量调整 ({{ selectedProducts.length }}个商品)
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useRoute } from 'vue-router'
import {
  Box, Search, Refresh, List, EditPen, Download, Warning, Document, Bell
} from '@element-plus/icons-vue'
import {
  getInventoryPage,
  getInventoryStatistics,
  adjustStock as adjustStockAPI,
  getStockRecords,
  setStockAlert as setStockAlertAPI,
  batchAdjustStock,
  type InventoryQueryParams,
  type StockAdjustParams,
  type InventoryItem,
  type InventoryStatistics,
  type BatchStockAdjustParams,
  type BatchAdjustResult
} from '@/api/inventory'
import { getAllCategories } from '@/api/category'
import * as XLSX from 'xlsx'

// 响应式数据
const loading = ref(false)
const adjusting = ref(false)
const adjustDialogVisible = ref(false)
const adjustDialogTitle = ref('库存调整')
const adjustFormRef = ref<FormInstance>()

// 库存记录相关
const recordDialogVisible = ref(false)
const recordLoading = ref(false)
const stockRecords = ref<any[]>([])
const recordProductName = ref('')
const recordCurrentStock = ref(0)
const recordPagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

// 库存预警相关
const alertDialogVisible = ref(false)
const alerting = ref(false)
const alertFormRef = ref<FormInstance>()

// 批量调整相关
const batchDialogVisible = ref(false)
const batchAdjusting = ref(false)
const batchFormRef = ref<FormInstance>()
const batchTableRef = ref()
const selectedProducts = ref<any[]>([])

// 库存列表
const inventoryList = ref<InventoryItem[]>([])
const categories = ref<any[]>([])
const statistics = ref<InventoryStatistics>({
  totalProducts: 0,
  lowStockCount: 0,
  outOfStockCount: 0,
  normalStockCount: 0
})

// 搜索表单
const searchForm = reactive({
  productName: '',
  barcode: '',
  categoryId: null as number | null,
  stockStatus: null as number | null
})

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

// 库存调整表单
const adjustForm = reactive({
  productId: null as string | null,
  productName: '',
  currentStock: 0,
  adjustType: 1,
  adjustQuantity: 1,
  reason: ''
})

// 库存预警表单
const alertForm = reactive({
  productId: null as string | null,
  productName: '',
  currentStock: 0,
  minStock: 0,
  maxStock: 0
})

// 批量调整表单
const batchForm = reactive({
  adjustType: 1,
  adjustQuantity: 1,
  reason: ''
})

// 表单验证规则
const adjustRules: FormRules = {
  adjustType: [
    { required: true, message: '请选择调整类型', trigger: 'change' }
  ],
  adjustQuantity: [
    { required: true, message: '请输入调整数量', trigger: 'blur' },
    { type: 'number', min: 1, message: '调整数量必须大于0', trigger: 'blur' }
  ],
  reason: [
    { required: true, message: '请输入调整原因', trigger: 'blur' }
  ]
}

// 库存预警验证规则
const alertRules: FormRules = {
  minStock: [
    { required: true, message: '请输入最低库存', trigger: 'blur' },
    { type: 'number', min: 0, message: '最低库存不能小于0', trigger: 'blur' }
  ],
  maxStock: [
    { required: true, message: '请输入最高库存', trigger: 'blur' },
    { type: 'number', min: 0, message: '最高库存不能小于0', trigger: 'blur' },
    {
      validator: (rule: any, value: number, callback: any) => {
        if (value <= alertForm.minStock) {
          callback(new Error('最高库存必须大于最低库存'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 批量调整验证规则
const batchRules: FormRules = {
  adjustType: [
    { required: true, message: '请选择调整类型', trigger: 'change' }
  ],
  adjustQuantity: [
    { required: true, message: '请输入调整数量', trigger: 'blur' },
    { type: 'number', min: 1, message: '调整数量必须大于0', trigger: 'blur' }
  ],
  reason: [
    { required: true, message: '请输入调整原因', trigger: 'blur' }
  ]
}

// 计算属性
const lowStockCount = computed(() => {
  return statistics.value.lowStockCount
})

const outOfStockCount = computed(() => {
  return statistics.value.outOfStockCount
})

const adjustedStock = computed(() => {
  const current = adjustForm.currentStock
  const quantity = adjustForm.adjustQuantity
  const type = adjustForm.adjustType

  if (type === 1 || type === 3) { // 入库或盘点增加
    return current + quantity
  } else { // 出库或损耗减少
    return Math.max(0, current - quantity)
  }
})

// 页面加载时获取数据
onMounted(() => {
  getCategories()

  // 检查是否有从商品管理页面传来的参数
  const route = useRoute()
  if (route.query.productId && route.query.productName) {
    // 设置搜索条件为指定商品
    searchForm.productName = route.query.productName as string

    // 显示定位提示
    ElMessage({
      message: `已定位到商品：${route.query.productName}`,
      type: 'info',
      duration: 3000,
      showClose: true
    })

    // 记录传递的商品ID，用于后续精确匹配
    const targetProductId = route.query.productId as string

    // 获取数据后自动滚动到目标商品
    nextTick(() => {
      setTimeout(() => {
        scrollToTargetProduct(targetProductId)
      }, 500) // 等待数据加载完成
    })
  }

  getInventoryList()
  getStatistics()
})

// 获取商品分类
const getCategories = async () => {
  try {
    const response = await getAllCategories()
    if (response.data.code === 200) {
      categories.value = response.data.data
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取统计信息
const getStatistics = async () => {
  try {
    const response = await getInventoryStatistics()
    if (response.data.code === 200) {
      statistics.value = response.data.data
    }
  } catch (error) {
    console.error('获取统计信息失败:', error)
  }
}

// 获取库存列表
const getInventoryList = async () => {
  loading.value = true
  try {
    const params: InventoryQueryParams = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }

    const response = await getInventoryPage(params)
    if (response.data.code === 200) {
      inventoryList.value = response.data.data.records
      pagination.total = response.data.data.total
    } else {
      ElMessage.error(response.data.message || '获取库存列表失败')
    }
  } catch (error) {
    console.error('获取库存列表失败:', error)
    ElMessage.error('获取库存列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索库存
const searchInventory = () => {
  pagination.pageNum = 1
  getInventoryList()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    productName: '',
    barcode: '',
    categoryId: null,
    stockStatus: null
  })
  pagination.pageNum = 1
  getInventoryList()
}

// 分页处理
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  getInventoryList()
}

const handleCurrentChange = (page: number) => {
  pagination.pageNum = page
  getInventoryList()
}

// 获取库存状态样式
const getStockClass = (row: any) => {
  if (row.stockQuantity === 0) return 'out-of-stock'
  if (row.stockQuantity <= row.minStock) return 'low-stock'
  return 'normal-stock'
}

const getStockStatusType = (row: any) => {
  if (row.stockQuantity === 0) return 'danger'
  if (row.stockQuantity <= row.minStock) return 'warning'
  return 'success'
}

const getStockStatusText = (row: any) => {
  if (row.stockQuantity === 0) return '缺货'
  if (row.stockQuantity <= row.minStock) return '预警'
  return '正常'
}

// 调整库存
const adjustStock = (row: any) => {
  adjustDialogTitle.value = '库存调整'
  adjustDialogVisible.value = true

  Object.assign(adjustForm, {
    productId: String(row.id), // 保持为字符串，避免精度丢失
    productName: row.productName,
    currentStock: row.stockQuantity,
    adjustType: 1,
    adjustQuantity: 1,
    reason: ''
  })
}

// 查看库存记录
const viewStockRecord = async (row: any) => {
  recordDialogVisible.value = true
  recordProductName.value = row.productName
  recordCurrentStock.value = row.stockQuantity
  recordPagination.pageNum = 1
  await getStockRecordList(String(row.id))
}

// 获取库存记录列表
const getStockRecordList = async (productId: string) => {
  recordLoading.value = true
  try {
    const params = {
      productId,
      pageNum: recordPagination.pageNum,
      pageSize: recordPagination.pageSize
    }

    const response = await getStockRecords(params)
    if (response.data.code === 200) {
      stockRecords.value = response.data.data.records
      recordPagination.total = response.data.data.total
    } else {
      ElMessage.error(response.data.message || '获取库存记录失败')
    }
  } catch (error) {
    console.error('获取库存记录失败:', error)
    ElMessage.error('获取库存记录失败')
  } finally {
    recordLoading.value = false
  }
}

// 库存记录分页处理
// 库存记录分页处理
const handleRecordSizeChange = (size: number) => {
  recordPagination.pageSize = size
  recordPagination.pageNum = 1
  const productId = getCurrentProductId()
  if (productId) {
    getStockRecordList(productId)
  }
}

const handleRecordCurrentChange = (page: number) => {
  recordPagination.pageNum = page
  const productId = getCurrentProductId()
  if (productId) {
    getStockRecordList(productId)
  }
}

// 获取当前查看记录的商品ID
const getCurrentProductId = (): string | undefined => {
  const currentProduct = inventoryList.value.find(item =>
    item.productName === recordProductName.value
  )
  return currentProduct?.id ? String(currentProduct.id) : undefined
}

// 格式化日期时间
const formatDateTime = (dateTime: string | number[] | null | undefined) => {
  if (!dateTime) return '暂无'

  try {
    // 处理后端返回的数组格式 [2025, 7, 18, 11, 24, 8]
    if (Array.isArray(dateTime)) {
      const [year, month, day, hour = 0, minute = 0, second = 0] = dateTime
      // 验证数组长度和数据有效性
      if (dateTime.length < 3 || !year || !month || !day) {
        console.warn('无效的日期数组格式:', dateTime)
        return '格式错误'
      }
      
      // 注意：JavaScript的月份是从0开始的，所以需要减1
      const date = new Date(year, month - 1, day, hour, minute, second)
      
      // 验证日期是否有效
      if (isNaN(date.getTime())) {
        console.warn('无效的日期值:', dateTime)
        return '日期无效'
      }
      
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    }

    // 处理字符串格式
    const date = new Date(dateTime)
    if (isNaN(date.getTime())) {
      console.warn('无效的日期字符串:', dateTime)
      return '日期无效'
    }
    
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch (error) {
    console.error('日期格式化失败:', dateTime, error)
    return '格式错误'
  }
}

// 滚动到目标商品
const scrollToTargetProduct = (targetProductId: string) => {
  const targetProduct = inventoryList.value.find(item =>
    String(item.id) === String(targetProductId)
  )

  if (targetProduct) {
    // 找到目标商品，高亮显示
    nextTick(() => {
      const tableRows = document.querySelectorAll('.el-table__row')
      const targetIndex = inventoryList.value.findIndex(item =>
        String(item.id) === String(targetProductId)
      )

      if (targetIndex >= 0 && tableRows[targetIndex]) {
        // 滚动到目标行
        tableRows[targetIndex].scrollIntoView({
          behavior: 'smooth',
          block: 'center'
        })

        // 高亮显示目标行
        tableRows[targetIndex].classList.add('highlight-row')
        setTimeout(() => {
          tableRows[targetIndex].classList.remove('highlight-row')
        }, 3000)

        ElMessage({
          message: `已找到并定位到商品：${targetProduct.productName}`,
          type: 'success',
          duration: 2000,
          showClose: true
        })
      }
    })
  } else {
    ElMessage({
      message: '未找到指定的商品，可能已被删除或不在当前页面',
      type: 'warning',
      duration: 3000,
      showClose: true
    })
  }
}

// 获取记录类型颜色
const getRecordTypeColor = (changeType: number) => {
  switch (changeType) {
    case 1: return 'success' // 入库
    case 2: return 'warning' // 出库
    case 3: return 'info'    // 盘点
    case 4: return 'danger'  // 损耗
    default: return ''
  }
}

// 获取数量样式
const getQuantityClass = (changeType: number) => {
  switch (changeType) {
    case 1:
    case 3:
      return 'quantity-increase'
    case 2:
    case 4:
      return 'quantity-decrease'
    default:
      return ''
  }
}

// 获取数量前缀
const getQuantityPrefix = (changeType: number) => {
  switch (changeType) {
    case 1:
    case 3:
      return '+'
    case 2:
    case 4:
      return '-'
    default:
      return ''
  }
}

// 设置库存预警
const setStockAlert = (row: any) => {
  console.log('设置库存预警，商品信息:', row)
  alertDialogVisible.value = true

  Object.assign(alertForm, {
    productId: String(row.id), // 保持为字符串，避免精度丢失
    productName: row.productName,
    currentStock: row.stockQuantity,
    minStock: row.minStock || 0,
    maxStock: row.maxStock || 0
  })

  console.log('预警表单数据:', alertForm)
}

// 提交库存预警设置
const submitAlert = async () => {
  if (!alertFormRef.value) return

  try {
    await alertFormRef.value.validate()
    alerting.value = true

    console.log('提交预警设置，参数:', {
      productId: alertForm.productId,
      minStock: alertForm.minStock,
      maxStock: alertForm.maxStock
    })

    const response = await setStockAlertAPI(
      alertForm.productId!,
      alertForm.minStock,
      alertForm.maxStock
    )

    console.log('预警设置响应:', response)

    if (response.data.code === 200) {
      ElMessage.success('库存预警设置成功')
      alertDialogVisible.value = false
      getInventoryList() // 刷新列表
      getStatistics() // 刷新统计
    } else {
      ElMessage.error(response.data.message || '库存预警设置失败')
    }
  } catch (error) {
    console.error('库存预警设置失败:', error)
    ElMessage.error('库存预警设置失败')
  } finally {
    alerting.value = false
  }
}

// 批量调整
const showAdjustDialog = () => {
  batchDialogVisible.value = true
  selectedProducts.value = []

  // 重置表单
  Object.assign(batchForm, {
    adjustType: 1,
    adjustQuantity: 1,
    reason: ''
  })
}

// 处理批量选择变化
const handleBatchSelectionChange = (selection: any[]) => {
  selectedProducts.value = selection
}

// 提交批量调整
const submitBatchAdjust = async () => {
  if (selectedProducts.value.length === 0) {
    ElMessage.warning('请选择要调整的商品')
    return
  }

  if (!batchFormRef.value) return

  try {
    await batchFormRef.value.validate()

    await ElMessageBox.confirm(
      `确定要对选中的 ${selectedProducts.value.length} 个商品进行批量调整吗？`,
      '确认批量调整',
      {
        type: 'warning',
        confirmButtonText: '确定调整',
        cancelButtonText: '取消'
      }
    )

    batchAdjusting.value = true

    // 调用批量调整API
    const params: BatchStockAdjustParams = {
      productIds: selectedProducts.value.map(product => String(product.id)),
      adjustType: batchForm.adjustType,
      adjustQuantity: batchForm.adjustQuantity,
      reason: batchForm.reason
    }

    const response = await batchAdjustStock(params)

    if (response.data.code === 200) {
      const result: BatchAdjustResult = response.data.data

      if (result.allSuccess) {
        ElMessage.success(`批量调整成功：共调整 ${result.successCount} 个商品`)
      } else {
        ElMessage.warning(`批量调整完成：成功 ${result.successCount} 个，失败 ${result.failCount} 个`)

        // 显示失败详情
        if (result.failDetails && result.failDetails.length > 0) {
          console.warn('批量调整失败详情:', result.failDetails)
        }
      }

      batchDialogVisible.value = false
      getInventoryList() // 刷新列表
      getStatistics() // 刷新统计
    } else {
      ElMessage.error(response.data.message || '批量调整失败')
    }

  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量调整失败:', error)
      ElMessage.error('批量调整失败')
    }
  } finally {
    batchAdjusting.value = false
  }
}

// 导出库存
const exportInventory = async () => {
  try {
    ElMessage.info('正在导出库存数据...')

    // 获取所有库存数据（不分页）
    const params: InventoryQueryParams = {
      ...searchForm,
      pageNum: 1,
      pageSize: 10000 // 获取所有数据
    }

    const response = await getInventoryPage(params)
    if (response.data.code === 200) {
      const data = response.data.data.records
      exportToExcel(data)
    } else {
      ElMessage.error('获取导出数据失败')
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 导出到Excel
const exportToExcel = (data: InventoryItem[]) => {
  // 准备导出数据
  const exportData = data.map(item => ({
    '商品名称': item.productName,
    '商品条码': item.barcode,
    '商品分类': item.categoryName,
    '品牌': item.brand || '',
    '单位': item.unit,
    '规格': item.specification || '',
    '当前库存': item.stockQuantity,
    '最低库存': item.minStock || 0,
    '最高库存': item.maxStock || 0,
    '进货价': item.purchasePrice || 0,
    '销售价': item.sellingPrice || 0,
    '库存状态': item.stockStatusText
  }))

  // 创建工作表
  const worksheet = XLSX.utils.json_to_sheet(exportData)

  // 设置列宽
  const colWidths = [
    { wch: 20 }, // 商品名称
    { wch: 15 }, // 商品条码
    { wch: 12 }, // 商品分类
    { wch: 12 }, // 品牌
    { wch: 8 },  // 单位
    { wch: 15 }, // 规格
    { wch: 10 }, // 当前库存
    { wch: 10 }, // 最低库存
    { wch: 10 }, // 最高库存
    { wch: 10 }, // 进货价
    { wch: 10 }, // 销售价
    { wch: 10 }  // 库存状态
  ]
  worksheet['!cols'] = colWidths

  // 创建工作簿
  const workbook = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(workbook, worksheet, '库存清单')

  // 生成文件名
  const fileName = `库存清单_${new Date().toISOString().slice(0, 10)}.xlsx`

  // 导出文件
  XLSX.writeFile(workbook, fileName)

  ElMessage.success('导出成功')
}

// 提交库存调整
const submitAdjust = async () => {
  if (!adjustFormRef.value) return

  try {
    await adjustFormRef.value.validate()
    adjusting.value = true

    const params: StockAdjustParams = {
      productId: adjustForm.productId!,
      adjustType: adjustForm.adjustType,
      adjustQuantity: adjustForm.adjustQuantity,
      reason: adjustForm.reason
    }

    console.log('库存调整参数:', params)

    const response = await adjustStockAPI(params)
    if (response.data.code === 200) {
      ElMessage.success('库存调整成功')
      adjustDialogVisible.value = false
      getInventoryList()
      getStatistics() // 刷新统计信息
    } else {
      ElMessage.error(response.data.message || '库存调整失败')
    }
  } catch (error) {
    console.error('库存调整失败:', error)
    ElMessage.error('库存调整失败')
  } finally {
    adjusting.value = false
  }
}

// 重置调整表单
const resetAdjustForm = () => {
  if (adjustFormRef.value) {
    adjustFormRef.value.resetFields()
  }
  Object.assign(adjustForm, {
    productId: null as string | null,
    productName: '',
    currentStock: 0,
    adjustType: 1,
    adjustQuantity: 1,
    reason: ''
  })
}
</script>

<style scoped lang="scss">
// iOS 黑白灰色彩系统
:root {
  --ios-primary: #000000;
  --ios-secondary: #1C1C1E;
  --ios-tertiary: #2C2C2E;
  --ios-gray: #8E8E93;
  --ios-gray-light: #F2F2F7;
  --ios-gray-medium: #C7C7CC;
  --ios-gray-dark: #48484A;
  --ios-white: #FFFFFF;
  --ios-system-background: #F2F2F7;
  --ios-secondary-background: #FFFFFF;
  --ios-label: #000000;
  --ios-secondary-label: #3C3C43;
  --ios-tertiary-label: #3C3C4399;
  --ios-separator: #C7C7CC;
  --ios-accent: #1C1C1E;
  
  // iOS状态颜色
  --ios-success: #34C759;
  --ios-warning: #FF9500;
  --ios-danger: #FF3B30;
}

.inventory-container {
  min-height: 100vh;
  background: var(--ios-system-background);
  padding: 24px;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', system-ui, sans-serif;
}

/* iOS风格页面头部 */
.page-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 32px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 32px;
}

.header-left {
  flex: 1;
}

.header-title {
  display: flex;
  align-items: center;
  margin-bottom: 12px;

  .header-icon {
    margin-right: 16px;
    font-size: 28px;
    color: var(--ios-accent);
    opacity: 0.9;
  }

  h2 {
    margin: 0;
    font-size: 32px;
    font-weight: 700;
    color: var(--ios-label);
    letter-spacing: -0.6px;
    line-height: 1.2;
  }
}

.header-desc {
  margin: 0;
  color: var(--ios-secondary-label);
  font-size: 16px;
  font-weight: 400;
  opacity: 0.8;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.header-stats {
  display: flex;
  gap: 16px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 16px;
  padding: 20px 24px;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.18);
  transition: all 0.25s cubic-bezier(0.4, 0.0, 0.2, 1);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);
  min-width: 100px;

  &:hover {
    transform: translateY(-2px) scale(1.02);
    box-shadow: 
      0 8px 24px rgba(0, 0, 0, 0.1),
      0 4px 12px rgba(0, 0, 0, 0.08);
    background: rgba(255, 255, 255, 0.98);
  }

  &:active {
    transform: scale(0.98);
  }

  &.warning {
    .stat-number {
      color: var(--ios-warning);
    }
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, var(--ios-warning), #FFB340);
      border-radius: 16px 16px 0 0;
    }
    
    position: relative;
  }

  &.danger {
    .stat-number {
      color: var(--ios-danger);
    }
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, var(--ios-danger), #FF6B5A);
      border-radius: 16px 16px 0 0;
    }
    
    position: relative;
  }

  .stat-number {
    font-size: 28px;
    font-weight: 700;
    color: var(--ios-label);
    margin-bottom: 8px;
    letter-spacing: -0.6px;
    line-height: 1.1;
  }

  .stat-label {
    font-size: 14px;
    color: var(--ios-secondary-label);
    font-weight: 500;
    opacity: 0.8;
  }
}

.adjust-btn {
  height: 50px;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 600;
  padding: 0 24px;
  background: var(--ios-accent);
  border: none;
  color: var(--ios-white);
  box-shadow: 0 2px 8px rgba(28, 28, 30, 0.25);
  transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);

  &:hover {
    background: var(--ios-secondary);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(28, 28, 30, 0.35);
  }

  &:active {
    transform: scale(0.98);
  }
}

/* iOS风格搜索区域 */
.search-section {
  margin-bottom: 32px;
}

.search-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 18px;
  padding: 24px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);
}

.search-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--ios-separator);

  .search-icon {
    font-size: 20px;
    color: var(--ios-accent);
    margin-right: 12px;
    opacity: 0.9;
  }

  .search-title {
    font-size: 18px;
    font-weight: 600;
    color: var(--ios-label);
    letter-spacing: -0.3px;
  }
}

.search-form {
  margin: 0;
}

:deep() {
  .search-form .el-form-item {
    margin-bottom: 16px;

    .el-form-item__label {
      color: var(--ios-secondary-label);
      font-size: 16px;
      font-weight: 500;
    }

    .el-input__wrapper {
      border-radius: 12px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
      border: 1px solid var(--ios-separator);
      background: var(--ios-secondary-background);
      transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
      min-height: 44px;

      &:hover {
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
        border-color: var(--ios-gray);
      }

      &.is-focus {
        border-color: var(--ios-accent);
        box-shadow: 0 0 0 3px rgba(28, 28, 30, 0.15);
      }
    }

    .el-input__inner {
      font-size: 16px;
      color: var(--ios-label);
      
      &::placeholder {
        color: var(--ios-tertiary-label);
      }
    }

    .el-select .el-input__wrapper {
      border-radius: 12px;
    }

    .el-button {
      height: 44px;
      border-radius: 12px;
      font-size: 16px;
      font-weight: 500;
      padding: 0 20px;
      transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);

      &--primary {
        background: var(--ios-accent);
        border: none;
        color: var(--ios-white);
        box-shadow: 0 2px 8px rgba(28, 28, 30, 0.25);

        &:hover {
          background: var(--ios-secondary);
          transform: translateY(-1px);
          box-shadow: 0 4px 12px rgba(28, 28, 30, 0.35);
        }
      }

      &:not(.el-button--primary) {
        background: rgba(28, 28, 30, 0.08);
        color: var(--ios-label);
        border: 1px solid var(--ios-separator);
        box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

        &:hover {
          background: rgba(28, 28, 30, 0.12);
          transform: translateY(-1px);
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
        }
      }

      &:active {
        transform: scale(0.98);
      }
    }
  }
}

/* iOS风格表格区域 */
.table-section {
  background: transparent;
  border-radius: 18px;
  overflow: hidden;
}

.table-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 18px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  background: rgba(28, 28, 30, 0.03);
  border-bottom: 1px solid var(--ios-separator);

  .table-title {
    display: flex;
    align-items: center;
    font-size: 18px;
    font-weight: 600;
    color: var(--ios-label);
    letter-spacing: -0.3px;

    .table-icon {
      font-size: 20px;
      color: var(--ios-accent);
      margin-right: 12px;
      opacity: 0.9;
    }
  }

  .table-actions {
    display: flex;
    gap: 12px;

    .el-button {
      height: 36px;
      border-radius: 10px;
      font-size: 14px;
      font-weight: 500;
      padding: 0 16px;
      transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);

      &--success {
        background: var(--ios-success);
        border: none;
        color: var(--ios-white);
        box-shadow: 0 2px 6px rgba(52, 199, 89, 0.25);

        &:hover {
          background: #30B653;
          transform: translateY(-1px);
        }
      }

      &:not(.el-button--success):not(.el-button--primary) {
        background: rgba(28, 28, 30, 0.08);
        color: var(--ios-label);
        border: 1px solid var(--ios-separator);

        &:hover {
          background: rgba(28, 28, 30, 0.12);
          transform: translateY(-1px);
        }
      }

      &:active {
        transform: scale(0.98);
      }
    }
  }
}

/* iOS风格表格内容 */
:deep() {
  .modern-table {
    border: none;
    background: transparent;

    .el-table__header {
      th {
        background: transparent;
        color: var(--ios-secondary-label);
        font-size: 14px;
        font-weight: 600;
        border: none;
        padding: 16px 12px;
        text-align: left;
      }
    }

    .el-table__body {
      tr {
        background: transparent;
        transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);

        &:hover {
          background: rgba(28, 28, 30, 0.05) !important;
        }

        td {
          border: none;
          padding: 16px 12px;
          color: var(--ios-label);
          font-size: 15px;
          font-weight: 400;
          border-bottom: 1px solid rgba(199, 199, 204, 0.3);

          &:last-child {
            border-bottom: none;
          }
        }
      }

      tr:last-child td {
        border-bottom: none;
      }
    }

    .el-table__row--striped {
      background: rgba(28, 28, 30, 0.02) !important;
    }
  }
}

.product-name {
  display: flex;
  align-items: center;

  .product-icon {
    font-size: 16px;
    color: var(--ios-gray);
    margin-right: 8px;
    opacity: 0.8;
  }

  span {
    font-weight: 500;
    color: var(--ios-label);
  }
}

.stock-info {
  display: flex;
  align-items: center;
  gap: 6px;

  .warning-icon {
    font-size: 14px;
    color: var(--ios-warning);
    animation: pulse-warning 2s infinite;
  }
}

.normal-stock {
  color: var(--ios-success);
  font-weight: 600;
}

.low-stock {
  color: var(--ios-warning);
  font-weight: 600;
}

.out-of-stock {
  color: var(--ios-danger);
  font-weight: 600;
}

@keyframes pulse-warning {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: center;
}

/* 自定义操作按钮样式 */
.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
  outline: none;
  position: relative;
  overflow: hidden;

  &:hover {
    transform: scale(1.1);
    box-shadow: 
      0 4px 12px rgba(0, 0, 0, 0.15),
      0 2px 6px rgba(0, 0, 0, 0.1);
  }

  &:active {
    transform: scale(0.95);
  }

  &:focus {
    outline: none;
    box-shadow: 0 0 0 3px rgba(0, 0, 0, 0.1);
  }

  // SVG图标样式
  svg {
    color: var(--ios-white);
    stroke: var(--ios-white);
    fill: none;
  }

  // 主要操作按钮 - 调整库存
  &.action-btn-primary {
    background: var(--ios-accent);
    color: var(--ios-white);
    box-shadow: 0 2px 6px rgba(28, 28, 30, 0.25);

    &:hover {
      background: var(--ios-secondary);
    }

    &:focus {
      box-shadow: 0 0 0 3px rgba(28, 28, 30, 0.15);
    }
  }

  // 信息按钮 - 库存记录  
  &.action-btn-info {
    background: var(--ios-gray);
    color: var(--ios-white);
    box-shadow: 0 2px 6px rgba(142, 142, 147, 0.25);

    &:hover {
      background: var(--ios-gray-dark);
    }

    &:focus {
      box-shadow: 0 0 0 3px rgba(142, 142, 147, 0.15);
    }
  }

  // 警告按钮 - 设置预警
  &.action-btn-warning {
    background: var(--ios-warning);
    color: var(--ios-white);
    box-shadow: 0 2px 6px rgba(255, 149, 0, 0.25);

    &:hover {
      background: #E6850E;
    }

    &:focus {
      box-shadow: 0 0 0 3px rgba(255, 149, 0, 0.15);
    }
  }
}

/* 操作按钮iOS风格 */
:deep() {
  .action-buttons .el-button {
    width: 32px !important;
    height: 32px !important;
    border-radius: 8px !important;
    padding: 0 !important;
    border: none !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1) !important;
    position: relative;
    overflow: hidden;
    min-width: auto !important;

    // 确保图标显示 - 强制覆盖Element Plus样式
    .el-icon {
      font-size: 14px !important;
      color: var(--ios-white) !important;
      z-index: 1;
      margin: 0 !important;
      width: auto !important;
      height: auto !important;
      line-height: 1 !important;
      display: inline-block !important;
    }

    // 针对具体图标强制显示
    svg {
      width: 14px !important;
      height: 14px !important;
      fill: currentColor !important;
      color: var(--ios-white) !important;
    }

    &:hover {
      transform: scale(1.05) !important;
      box-shadow: 
        0 4px 12px rgba(0, 0, 0, 0.15),
        0 2px 6px rgba(0, 0, 0, 0.1) !important;
    }

    &:active {
      transform: scale(0.95) !important;
    }

    // 主要操作按钮 - 调整库存
    &.el-button--primary {
      background: var(--ios-accent) !important;
      box-shadow: 0 2px 6px rgba(28, 28, 30, 0.25) !important;

      &:hover {
        background: var(--ios-secondary) !important;
      }

      &:focus {
        background: var(--ios-accent) !important;
        box-shadow: 0 0 0 3px rgba(28, 28, 30, 0.15) !important;
      }

      .el-icon,
      svg {
        color: var(--ios-white) !important;
        fill: var(--ios-white) !important;
      }
    }

    // 信息按钮 - 库存记录
    &.el-button--info {
      background: var(--ios-gray) !important;
      box-shadow: 0 2px 6px rgba(142, 142, 147, 0.25) !important;

      &:hover {
        background: var(--ios-gray-dark) !important;
      }

      &:focus {
        background: var(--ios-gray) !important;
        box-shadow: 0 0 0 3px rgba(142, 142, 147, 0.15) !important;
      }

      .el-icon,
      svg {
        color: var(--ios-white) !important;
        fill: var(--ios-white) !important;
      }
    }

    // 警告按钮 - 设置预警
    &.el-button--warning {
      background: var(--ios-warning) !important;
      box-shadow: 0 2px 6px rgba(255, 149, 0, 0.25) !important;

      &:hover {
        background: #E6850E !important;
      }

      &:focus {
        background: var(--ios-warning) !important;
        box-shadow: 0 0 0 3px rgba(255, 149, 0, 0.15) !important;
      }

      .el-icon,
      svg {
        color: var(--ios-white) !important;
        fill: var(--ios-white) !important;
      }
    }

    // 小尺寸按钮特殊处理
    &.el-button--small {
      min-height: auto !important;
      height: 32px !important;
      width: 32px !important;
      font-size: 14px !important;

      .el-icon {
        margin: 0 !important;
        font-size: 14px !important;
      }
    }

    // 圆形按钮
    &.is-circle {
      border-radius: 50% !important;
      width: 32px !important;
      height: 32px !important;
    }

    // 移除Element Plus默认的focus outline
    &:focus {
      outline: none !important;
    }

    // 确保按钮内容居中
    .el-button__content,
    & > span {
      display: flex !important;
      align-items: center !important;
      justify-content: center !important;
      width: 100%;
      height: 100%;
    }
  }

  // 确保tooltip正确显示
  .el-tooltip__trigger {
    display: inline-flex !important;
  }

  // 特别处理表格中的按钮容器
  .el-table .action-buttons {
    .el-tooltip {
      display: inline-flex;
      
      .el-tooltip__trigger {
        display: inline-flex;
      }
    }
  }

  // 强制显示所有Element Plus图标
  .el-icon {
    &[class*="edit"],
    &[class*="document"], 
    &[class*="bell"],
    &[class*="EditPen"],
    &[class*="Document"],
    &[class*="Bell"] {
      display: inline-block !important;
      opacity: 1 !important;
      visibility: visible !important;
      
      svg {
        display: inline-block !important;
        opacity: 1 !important;
        visibility: visible !important;
      }
    }
  }

  // 确保SVG图标正确显示
  .action-buttons .el-button {
    .el-icon {
      svg {
        pointer-events: none !important;
        vertical-align: middle !important;
      }
    }
  }
}

/* iOS风格分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 24px;
  background: rgba(28, 28, 30, 0.02);
  border-top: 1px solid var(--ios-separator);
}

:deep() {
  .el-pagination {
    .el-pager li {
      background: transparent;
      color: var(--ios-secondary-label);
      border-radius: 8px;
      margin: 0 2px;
      font-weight: 500;
      transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);

      &:hover {
        background: rgba(28, 28, 30, 0.08);
        color: var(--ios-label);
      }

      &.is-active {
        background: var(--ios-accent);
        color: var(--ios-white);
        font-weight: 600;
      }
    }

    .btn-prev,
    .btn-next {
      background: transparent;
      color: var(--ios-secondary-label);
      border-radius: 8px;
      font-weight: 500;

      &:hover {
        background: rgba(28, 28, 30, 0.08);
        color: var(--ios-label);
      }
    }

    .el-select .el-input {
      .el-input__wrapper {
        border-radius: 8px;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
      }
    }
  }
}

/* iOS风格对话框 */
:deep() {
  .el-dialog {
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 
      0 20px 40px rgba(0, 0, 0, 0.15),
      0 8px 24px rgba(0, 0, 0, 0.1);

    .el-dialog__header {
      background: rgba(28, 28, 30, 0.03);
      padding: 24px 24px 16px;
      border-bottom: 1px solid var(--ios-separator);

      .el-dialog__title {
        font-size: 20px;
        font-weight: 700;
        color: var(--ios-label);
        letter-spacing: -0.4px;
      }
    }

    .el-dialog__body {
      padding: 24px;
      background: var(--ios-secondary-background);

      .el-form-item {
        margin-bottom: 20px;

        .el-form-item__label {
          color: var(--ios-secondary-label);
          font-size: 16px;
          font-weight: 500;
        }

        .el-input__wrapper,
        .el-textarea__inner {
          border-radius: 12px;
          border: 1px solid var(--ios-separator);
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
          min-height: 44px;

          &:hover {
            border-color: var(--ios-gray);
          }

          &.is-focus {
            border-color: var(--ios-accent);
            box-shadow: 0 0 0 3px rgba(28, 28, 30, 0.15);
          }
        }

        .el-input__inner {
          font-size: 16px;
          color: var(--ios-label);
        }

        .el-radio-group {
          .el-radio {
            margin-right: 24px;

            .el-radio__label {
              font-size: 16px;
              color: var(--ios-label);
            }

            &.is-checked {
              .el-radio__input .el-radio__inner {
                background-color: var(--ios-accent);
                border-color: var(--ios-accent);
              }
            }
          }
        }

        .el-input-number {
          width: 100%;

          .el-input__wrapper {
            border-radius: 12px;
          }
        }
      }

      .el-alert {
        border-radius: 12px;
        border: 1px solid rgba(52, 199, 89, 0.2);
        background: rgba(52, 199, 89, 0.08);
        margin-bottom: 0;

        .el-alert__content {
          .el-alert__title {
            color: var(--ios-label);
            font-size: 14px;
          }
        }
      }
    }

    .el-dialog__footer {
      padding: 16px 24px 24px;
      background: rgba(28, 28, 30, 0.02);
      border-top: 1px solid var(--ios-separator);

      .dialog-footer {
        display: flex;
        justify-content: flex-end;
        gap: 12px;

        .el-button {
          height: 44px;
          border-radius: 12px;
          font-size: 16px;
          font-weight: 600;
          padding: 0 24px;
          transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);

          &--primary {
            background: var(--ios-accent);
            border: none;
            color: var(--ios-white);
            box-shadow: 0 2px 8px rgba(28, 28, 30, 0.25);

            &:hover {
              background: var(--ios-secondary);
              transform: translateY(-1px);
            }
          }

          &:not(.el-button--primary) {
            background: rgba(28, 28, 30, 0.08);
            color: var(--ios-label);
            border: 1px solid var(--ios-separator);

            &:hover {
              background: rgba(28, 28, 30, 0.12);
              transform: translateY(-1px);
            }
          }

          &:active {
            transform: scale(0.98);
          }
        }
      }
    }
  }
}

/* 库存记录样式 */
.record-header {
  margin-bottom: 20px;
  padding: 20px;
  background: rgba(28, 28, 30, 0.05);
  border-radius: 12px;
  border: 1px solid var(--ios-separator);

  .record-info {
    display: flex;
    align-items: center;
    gap: 20px;

    .product-name {
      font-size: 18px;
      font-weight: 600;
      color: var(--ios-label);
      letter-spacing: -0.3px;
    }

    .current-stock {
      font-size: 16px;
      color: var(--ios-secondary-label);
      font-weight: 500;
    }
  }
}

.record-pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid var(--ios-separator);
}

.quantity-increase {
  color: var(--ios-success);
  font-weight: 600;
}

.quantity-decrease {
  color: var(--ios-danger);
  font-weight: 600;
}

/* 批量调整样式 */
.batch-header {
  margin-bottom: 20px;

  .el-alert {
    border-radius: 12px;
    border: 1px solid rgba(52, 199, 89, 0.2);
    background: rgba(52, 199, 89, 0.08);
  }
}

.batch-selection {
  border: 1px solid var(--ios-separator);
  border-radius: 12px;
  overflow: hidden;
  background: var(--ios-secondary-background);
}

/* 表格选择框iOS风格 */
:deep() {
  .el-table {
    // 表格头部选择框
    .el-table__header {
      .el-checkbox {
        .el-checkbox__input {
          .el-checkbox__inner {
            border: 2px solid var(--ios-separator);
            border-radius: 6px;
            background-color: transparent;
            width: 18px;
            height: 18px;

            &:hover {
              border-color: var(--ios-accent);
            }

            &::after {
              border: 2px solid var(--ios-white);
              border-left: 0;
              border-top: 0;
              width: 6px;
              height: 10px;
              left: 4px;
              top: 1px;
            }
          }

          &.is-checked {
            .el-checkbox__inner {
              background-color: var(--ios-accent);
              border-color: var(--ios-accent);
            }
          }

          &.is-indeterminate {
            .el-checkbox__inner {
              background-color: var(--ios-accent);
              border-color: var(--ios-accent);

              &::before {
                background-color: var(--ios-white);
                width: 8px;
                height: 2px;
                border-radius: 1px;
                left: 50%;
                top: 50%;
                transform: translate(-50%, -50%);
              }
            }
          }
        }
      }
    }

    // 表格主体选择框
    .el-table__body {
      .el-checkbox {
        .el-checkbox__input {
          .el-checkbox__inner {
            border: 2px solid var(--ios-separator);
            border-radius: 6px;
            background-color: transparent;
            width: 18px;
            height: 18px;
            transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);

            &:hover {
              border-color: var(--ios-accent);
              transform: scale(1.1);
            }

            &::after {
              border: 2px solid var(--ios-white);
              border-left: 0;
              border-top: 0;
              width: 6px;
              height: 10px;
              left: 4px;
              top: 1px;
              transition: all 0.15s ease-in-out;
            }
          }

          &.is-checked {
            .el-checkbox__inner {
              background-color: var(--ios-accent);
              border-color: var(--ios-accent);
              transform: scale(1);
            }
          }

          &.is-focus {
            .el-checkbox__inner {
              box-shadow: 0 0 0 3px rgba(28, 28, 30, 0.15);
            }
          }
        }
      }

      // 选中行高亮
      tr.el-table__row--selected {
        background: rgba(28, 28, 30, 0.08) !important;
        
        &:hover {
          background: rgba(28, 28, 30, 0.12) !important;
        }
      }
    }
  }

  // 批量操作表格特殊样式
  .batch-selection .el-table {
    .el-table__header {
      th {
        background: rgba(28, 28, 30, 0.03);
        color: var(--ios-secondary-label);
        font-size: 14px;
        font-weight: 600;
        border: none;
        padding: 12px;
      }
    }

    .el-table__body {
      tr {
        transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);

        &:hover {
          background: rgba(28, 28, 30, 0.05) !important;
        }

        td {
          border: none;
          padding: 12px;
          color: var(--ios-label);
          font-size: 14px;
          border-bottom: 1px solid rgba(199, 199, 204, 0.2);
        }
      }

      tr:last-child td {
        border-bottom: none;
      }
    }
  }
}

/* 高亮行样式 */
:deep() {
  .highlight-row {
    background-color: rgba(52, 199, 89, 0.15) !important;
    animation: highlight-fade 3s ease-out;
  }
}

@keyframes highlight-fade {
  0% {
    background-color: var(--ios-success) !important;
    color: var(--ios-white) !important;
  }
  100% {
    background-color: rgba(52, 199, 89, 0.15) !important;
    color: inherit !important;
  }
}

/* Element Plus 标签组件iOS风格 */
:deep() {
  .el-tag {
    border-radius: 8px;
    font-weight: 500;
    font-size: 13px;
    padding: 4px 8px;

    &--info {
      background: rgba(142, 142, 147, 0.15);
      color: var(--ios-gray);
      border: 1px solid rgba(142, 142, 147, 0.2);
    }

    &--success {
      background: rgba(52, 199, 89, 0.15);
      color: var(--ios-success);
      border: 1px solid rgba(52, 199, 89, 0.2);
    }

    &--warning {
      background: rgba(255, 149, 0, 0.15);
      color: var(--ios-warning);
      border: 1px solid rgba(255, 149, 0, 0.2);
    }

    &--danger {
      background: rgba(255, 59, 48, 0.15);
      color: var(--ios-danger);
      border: 1px solid rgba(255, 59, 48, 0.2);
    }
  }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .header-content {
    flex-direction: column;
    gap: 20px;
    align-items: stretch;
  }

  .header-stats {
    justify-content: center;
    flex-wrap: wrap;
  }

  .stat-card {
    min-width: 120px;
  }
}

@media (max-width: 768px) {
  .inventory-container {
    padding: 16px;
  }

  .page-header {
    padding: 20px;
    margin-bottom: 20px;

    .header-title h2 {
      font-size: 24px;
    }

    .header-desc {
      font-size: 14px;
    }
  }

  .header-stats {
    gap: 12px;

    .stat-card {
      padding: 16px;
      min-width: 100px;

      .stat-number {
        font-size: 22px;
      }

      .stat-label {
        font-size: 12px;
      }
    }
  }

  .adjust-btn {
    width: 100%;
    margin-top: 16px;
  }

  .search-card {
    padding: 16px;
  }

  .table-header {
    padding: 16px;
    flex-direction: column;
    gap: 12px;
    align-items: stretch;

    .table-actions {
      justify-content: center;
    }
  }

  .action-buttons {
    flex-direction: column;
    gap: 4px;

    .el-button {
      width: 28px;
      height: 28px;
      font-size: 12px;
    }
  }
}

@media (max-width: 480px) {
  .inventory-container {
    padding: 12px;
  }

  .page-header {
    padding: 16px;

    .header-title h2 {
      font-size: 20px;
    }
  }

  .header-stats {
    .stat-card {
      padding: 12px;

      .stat-number {
        font-size: 18px;
      }

      .stat-label {
        font-size: 11px;
      }
    }
  }

  .search-card {
    padding: 12px;
  }

  .table-card {
    border-radius: 12px;
  }

  .table-header {
    padding: 12px;

    .table-title {
      font-size: 16px;
    }
  }
}
</style>
