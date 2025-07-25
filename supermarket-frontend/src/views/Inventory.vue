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
                  <el-button size="small" type="primary" circle @click="adjustStock(row)">
                    <el-icon><EditPen /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="库存记录" placement="top">
                  <el-button size="small" type="info" circle @click="viewStockRecord(row)">
                    <el-icon><Document /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="设置预警" placement="top">
                  <el-button size="small" type="warning" circle @click="setStockAlert(row)">
                    <el-icon><Bell /></el-icon>
                  </el-button>
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

<style scoped>
.inventory-container {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 页面头部样式 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  color: white;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  flex: 1;
}

.header-title {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.header-icon {
  font-size: 32px;
  margin-right: 16px;
  color: #fff;
}

.header-title h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #fff;
}

.header-desc {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
  color: #fff;
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
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 16px 20px;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-card.warning {
  background: rgba(245, 108, 108, 0.2);
  border-color: rgba(245, 108, 108, 0.3);
}

.stat-card.danger {
  background: rgba(245, 34, 45, 0.2);
  border-color: rgba(245, 34, 45, 0.3);
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  opacity: 0.9;
  color: #fff;
}

.adjust-btn {
  height: 48px;
  padding: 0 24px;
  font-size: 16px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

/* 搜索区域样式 */
.search-section {
  margin-bottom: 24px;
}

.search-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
}

.search-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f2f5;
}

.search-icon {
  font-size: 20px;
  color: #409eff;
  margin-right: 12px;
}

.search-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.search-form {
  margin: 0;
}

/* 表格区域样式 */
.table-section {
  background: transparent;
  border-radius: 16px;
  overflow: hidden;
}

.table-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: #fafbfc;
  border-bottom: 1px solid #e4e7ed;
}

.table-title {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.table-icon {
  font-size: 20px;
  color: #409eff;
  margin-right: 12px;
}

.table-actions {
  display: flex;
  gap: 12px;
}

/* 表格内容样式 */
.product-name {
  display: flex;
  align-items: center;
}

.product-icon {
  font-size: 16px;
  color: #909399;
  margin-right: 8px;
}

.stock-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.normal-stock {
  color: #67c23a;
  font-weight: 600;
}

.low-stock {
  color: #e6a23c;
  font-weight: 600;
}

.out-of-stock {
  color: #f56c6c;
  font-weight: 600;
}

.warning-icon {
  font-size: 14px;
  color: #e6a23c;
}

.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

/* 分页样式 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: #fafbfc;
  border-top: 1px solid #e4e7ed;
}

/* 对话框样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 库存记录样式 */
.record-header {
  margin-bottom: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.record-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.current-stock {
  font-size: 14px;
  color: #606266;
}

.record-pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.quantity-increase {
  color: #67c23a;
  font-weight: 600;
}

.quantity-decrease {
  color: #f56c6c;
  font-weight: 600;
}

/* 批量调整样式 */
.batch-header {
  margin-bottom: 20px;
}

.batch-selection {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}

/* 预警设置样式 */
.el-alert {
  margin-bottom: 20px;
}

/* 高亮行样式 */
:deep(.highlight-row) {
  background-color: #e6f7ff !important;
  animation: highlight-fade 3s ease-out;
}

@keyframes highlight-fade {
  0% {
    background-color: #1890ff !important;
    color: white !important;
  }
  100% {
    background-color: #e6f7ff !important;
    color: inherit !important;
  }
}
</style>
