<template>
  <div class="stock-alert-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>
        <el-icon><Warning /></el-icon>
        库存预警管理
      </h2>
      <p>管理商品库存预警，包括低库存、临期商品等预警信息</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card danger">
        <div class="stat-icon">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ statistics.totalPendingCount || 0 }}</div>
          <div class="stat-label">待处理预警</div>
        </div>
      </div>
      <div class="stat-card warning">
        <div class="stat-icon">
          <el-icon><Box /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ statistics.lowStockCount || 0 }}</div>
          <div class="stat-label">低库存预警</div>
        </div>
      </div>
      <div class="stat-card info">
        <div class="stat-icon">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ statistics.expiringCount || 0 }}</div>
          <div class="stat-label">临期商品预警</div>
        </div>
      </div>
      <div class="stat-card success">
        <div class="stat-icon">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ statistics.highStockCount || 0 }}</div>
          <div class="stat-label">高库存预警</div>
        </div>
      </div>
    </div>

    <!-- 搜索和操作区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="预警类型">
          <el-select v-model="searchForm.alertType" placeholder="请选择类型" clearable style="width: 150px">
            <el-option label="低库存" :value="1" />
            <el-option label="高库存" :value="2" />
            <el-option label="临期商品" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="未处理" :value="1" />
            <el-option label="已处理" :value="2" />
            <el-option label="已忽略" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急程度">
          <el-select v-model="searchForm.urgencyLevel" placeholder="请选择程度" clearable style="width: 120px">
            <el-option label="低" :value="1" />
            <el-option label="中" :value="2" />
            <el-option label="高" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品名称">
          <el-input
            v-model="searchForm.productName"
            placeholder="请输入商品名称"
            style="width: 200px"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchAlerts">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="success" @click="checkExpiringProductsHandler">
          <el-icon><Clock /></el-icon>
          检查临期商品
        </el-button>
        <el-button type="warning" @click="checkLowStockProductsHandler">
          <el-icon><Box /></el-icon>
          检查低库存
        </el-button>
        <el-button type="info" @click="showExpiringProducts">
          <el-icon><View /></el-icon>
          查看临期商品
        </el-button>
      </div>
    </div>

    <!-- 批量操作区域 -->
    <div v-if="selectedAlerts.length > 0" class="batch-section">
      <div class="batch-info">
        <span>已选择 {{ selectedAlerts.length }} 个预警</span>
      </div>
      <div class="batch-actions">
        <el-button type="success" @click="batchHandle('handle')">
          <el-icon><Check /></el-icon>
          批量处理
        </el-button>
        <el-button type="warning" @click="batchHandle('ignore')">
          <el-icon><Hide /></el-icon>
          批量忽略
        </el-button>
      </div>
    </div>

    <!-- 预警列表 -->
    <div class="table-section">
      <el-table
        ref="alertTableRef"
        :data="alertList"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        stripe
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="productName" label="商品名称" min-width="150">
          <template #default="{ row }">
            <div class="product-info">
              <div class="product-name">{{ row.productName }}</div>
              <div class="product-barcode">{{ row.barcode }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="alertType" label="预警类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getAlertTypeTagType(row.alertType)">
              {{ row.alertTypeText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="urgencyLevel" label="紧急程度" width="100">
          <template #default="{ row }">
            <el-tag :type="getUrgencyTagType(row.urgencyLevel)">
              {{ row.urgencyText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currentStock" label="当前库存" width="100" />
        <el-table-column prop="alertMessage" label="预警信息" min-width="200" show-overflow-tooltip />
        <el-table-column prop="expireDate" label="过期日期" width="120">
          <template #default="{ row }">
            <span v-if="row.expireDate">
              {{ row.expireDate }}
              <el-tag v-if="row.remainingDays !== undefined" size="small" :type="getRemainingDaysTagType(row.remainingDays)">
                {{ row.remainingDays }}天
              </el-tag>
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-tooltip v-if="row.status === 1" content="处理预警" placement="top">
                <el-button
                  size="small"
                  type="success"
                  circle
                  @click="handleSingleAlert(row, 'handle')"
                >
                  <el-icon><Check /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip v-if="row.status === 1" content="忽略预警" placement="top">
                <el-button
                  size="small"
                  type="warning"
                  circle
                  @click="handleSingleAlert(row, 'ignore')"
                >
                  <el-icon><Hide /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="查看详情" placement="top">
                <el-button
                  size="small"
                  type="info"
                  circle
                  @click="viewAlertDetail(row)"
                >
                  <el-icon><View /></el-icon>
                </el-button>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="getAlertList"
          @current-change="getAlertList"
        />
      </div>
    </div>

    <!-- 临期商品对话框 -->
    <el-dialog
      v-model="expiringDialogVisible"
      title="临期商品列表"
      width="800px"
    >
      <el-table :data="expiringProducts" stripe>
        <el-table-column prop="productName" label="商品名称" min-width="150" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="stockQuantity" label="库存数量" width="100" />
        <el-table-column prop="expireDate" label="过期日期" width="120" />
        <el-table-column prop="remainingDays" label="剩余天数" width="100">
          <template #default="{ row }">
            <el-tag :type="getRemainingDaysTagType(row.remainingDays)">
              {{ row.remainingDays }}天
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 预警详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="预警详情"
      width="600px"
    >
      <div v-if="currentAlert" class="alert-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="商品名称">{{ currentAlert.productName }}</el-descriptions-item>
          <el-descriptions-item label="商品条码">{{ currentAlert.barcode }}</el-descriptions-item>
          <el-descriptions-item label="商品分类">{{ currentAlert.categoryName }}</el-descriptions-item>
          <el-descriptions-item label="预警类型">
            <el-tag :type="getAlertTypeTagType(currentAlert.alertType)">
              {{ currentAlert.alertTypeText }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="当前库存">{{ currentAlert.currentStock }}</el-descriptions-item>
          <el-descriptions-item label="预警阈值">{{ currentAlert.thresholdValue || '-' }}</el-descriptions-item>
          <el-descriptions-item label="过期日期">{{ currentAlert.expireDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="剩余天数">
            <el-tag v-if="currentAlert.remainingDays !== undefined" :type="getRemainingDaysTagType(currentAlert.remainingDays)">
              {{ currentAlert.remainingDays }}天
            </el-tag>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="紧急程度">
            <el-tag :type="getUrgencyTagType(currentAlert.urgencyLevel)">
              {{ currentAlert.urgencyText }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="预警状态">
            <el-tag :type="getStatusTagType(currentAlert.status)">
              {{ currentAlert.statusText }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">{{ formatDateTime(currentAlert.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="预警信息" :span="2">{{ currentAlert.alertMessage }}</el-descriptions-item>
          <el-descriptions-item v-if="currentAlert.handlerName" label="处理人">{{ currentAlert.handlerName }}</el-descriptions-item>
          <el-descriptions-item v-if="currentAlert.handleTime" label="处理时间">{{ formatDateTime(currentAlert.handleTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Warning, Box, Clock, TrendCharts, Search, Refresh, Check, Hide, View
} from '@element-plus/icons-vue'
import {
  getStockAlertPage,
  getAlertStatistics,
  getExpiringProducts,
  handleAlert,
  batchHandleAlerts,
  checkExpiringProducts,
  checkLowStockProducts,
  type StockAlert,
  type StockAlertQueryParams,
  type AlertStatistics
} from '@/api/stockAlert'

// 响应式数据
const loading = ref(false)
const alertList = ref<StockAlert[]>([])
const selectedAlerts = ref<StockAlert[]>([])
const statistics = ref<AlertStatistics>({
  lowStockCount: 0,
  highStockCount: 0,
  expiringCount: 0,
  totalPendingCount: 0
})
const expiringProducts = ref<StockAlert[]>([])
const currentAlert = ref<StockAlert | null>(null)

// 对话框状态
const expiringDialogVisible = ref(false)
const detailDialogVisible = ref(false)

// 表格引用
const alertTableRef = ref()

// 搜索表单
const searchForm = reactive<StockAlertQueryParams>({
  alertType: undefined,
  status: undefined,
  urgencyLevel: undefined,
  productName: ''
})

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

// 页面初始化
onMounted(() => {
  getAlertList()
  getStatistics()
})

// 获取预警列表
const getAlertList = async () => {
  loading.value = true
  try {
    const params: StockAlertQueryParams = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }

    const response = await getStockAlertPage(params)
    if (response.data.code === 200) {
      alertList.value = response.data.data.records
      pagination.total = response.data.data.total
    } else {
      ElMessage.error(response.data.message || '获取预警列表失败')
    }
  } catch (error) {
    console.error('获取预警列表失败:', error)
    ElMessage.error('获取预警列表失败')
  } finally {
    loading.value = false
  }
}

// 获取统计信息
const getStatistics = async () => {
  try {
    const response = await getAlertStatistics()
    if (response.data.code === 200) {
      statistics.value = response.data.data
    }
  } catch (error) {
    console.error('获取统计信息失败:', error)
  }
}

// 搜索预警
const searchAlerts = () => {
  pagination.pageNum = 1
  getAlertList()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    alertType: undefined,
    status: undefined,
    urgencyLevel: undefined,
    productName: ''
  })
  pagination.pageNum = 1
  getAlertList()
}

// 表格选择变化
const handleSelectionChange = (selection: StockAlert[]) => {
  selectedAlerts.value = selection
}

// 处理单个预警
const handleSingleAlert = async (alert: StockAlert, action: string) => {
  const actionText = action === 'handle' ? '处理' : '忽略'

  try {
    await ElMessageBox.confirm(`确定要${actionText}该预警吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await handleAlert(alert.id, action, '1') // 假设当前用户ID为1

    if (response.data.code === 200) {
      ElMessage.success(`${actionText}成功`)
      getAlertList()
      getStatistics()
    } else {
      ElMessage.error(response.data.message || `${actionText}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`${actionText}预警失败:`, error)
      ElMessage.error(`${actionText}失败`)
    }
  }
}

// 批量处理预警
const batchHandle = async (action: string) => {
  const actionText = action === 'handle' ? '处理' : '忽略'

  try {
    await ElMessageBox.confirm(`确定要批量${actionText} ${selectedAlerts.value.length} 个预警吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const alertIds = selectedAlerts.value.map(alert => alert.id)
    const response = await batchHandleAlerts(alertIds, action, '1') // 假设当前用户ID为1

    if (response.data.code === 200) {
      ElMessage.success(response.data.data)
      getAlertList()
      getStatistics()
      selectedAlerts.value = []
      alertTableRef.value?.clearSelection()
    } else {
      ElMessage.error(response.data.message || `批量${actionText}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`批量${actionText}预警失败:`, error)
      ElMessage.error(`批量${actionText}失败`)
    }
  }
}

// 检查临期商品
const checkExpiringProductsHandler = async () => {
  try {
    const response = await checkExpiringProducts()
    if (response.data.code === 200) {
      ElMessage.success('临期商品检查已触发')
      setTimeout(() => {
        getAlertList()
        getStatistics()
      }, 2000)
    } else {
      ElMessage.error(response.data.message || '检查失败')
    }
  } catch (error) {
    console.error('检查临期商品失败:', error)
    ElMessage.error('检查失败')
  }
}

// 检查低库存商品
const checkLowStockProductsHandler = async () => {
  try {
    const response = await checkLowStockProducts()
    if (response.data.code === 200) {
      ElMessage.success('低库存检查已触发')
      setTimeout(() => {
        getAlertList()
        getStatistics()
      }, 2000)
    } else {
      ElMessage.error(response.data.message || '检查失败')
    }
  } catch (error) {
    console.error('检查低库存商品失败:', error)
    ElMessage.error('检查失败')
  }
}

// 查看临期商品
const showExpiringProducts = async () => {
  try {
    const response = await getExpiringProducts(7) // 查看7天内临期商品
    if (response.data.code === 200) {
      expiringProducts.value = response.data.data
      expiringDialogVisible.value = true
    } else {
      ElMessage.error(response.data.message || '获取临期商品失败')
    }
  } catch (error) {
    console.error('获取临期商品失败:', error)
    ElMessage.error('获取临期商品失败')
  }
}

// 查看预警详情
const viewAlertDetail = (alert: StockAlert) => {
  currentAlert.value = alert
  detailDialogVisible.value = true
}

// 获取预警类型标签类型
const getAlertTypeTagType = (alertType: number) => {
  switch (alertType) {
    case 1: return 'warning' // 低库存
    case 2: return 'success' // 高库存
    case 3: return 'danger'  // 临期商品
    default: return 'info'
  }
}

// 获取紧急程度标签类型
const getUrgencyTagType = (urgencyLevel: number) => {
  switch (urgencyLevel) {
    case 1: return 'info'    // 低
    case 2: return 'warning' // 中
    case 3: return 'danger'  // 高
    default: return 'info'
  }
}

// 获取状态标签类型
const getStatusTagType = (status: number) => {
  switch (status) {
    case 0: return 'info'    // 已忽略
    case 1: return 'warning' // 未处理
    case 2: return 'success' // 已处理
    default: return 'info'
  }
}

// 获取剩余天数标签类型
const getRemainingDaysTagType = (remainingDays: number) => {
  if (remainingDays <= 1) return 'danger'
  if (remainingDays <= 3) return 'warning'
  return 'success'
}

// 格式化日期时间
const formatDateTime = (dateTime: string | number[]) => {
  if (!dateTime) return '-'

  try {
    // 处理后端返回的数组格式 [2025, 7, 18, 11, 24, 8]
    if (Array.isArray(dateTime)) {
      const [year, month, day, hour, minute, second] = dateTime
      // 注意：JavaScript的月份是从0开始的，所以需要减1
      const date = new Date(year, month - 1, day, hour || 0, minute || 0, second || 0)
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
    return new Date(dateTime).toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch (error) {
    console.error('日期格式化失败:', dateTime, error)
    return '-'
  }
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
}

.stock-alert-container {
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

  h2 {
    margin: 0 0 12px 0;
    font-size: 32px;
    font-weight: 700;
    color: var(--ios-label);
    letter-spacing: -0.6px;
    line-height: 1.2;
    display: flex;
    align-items: center;
    gap: 16px;

    .el-icon {
      font-size: 28px;
      color: var(--ios-accent);
      opacity: 0.9;
    }
  }

  p {
    margin: 0;
    color: var(--ios-secondary-label);
    font-size: 16px;
    font-weight: 400;
    opacity: 0.8;
  }
}

/* iOS风格统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 18px;
  padding: 28px 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.25s cubic-bezier(0.4, 0.0, 0.2, 1);
  position: relative;

  &:hover {
    transform: translateY(-4px) scale(1.02);
    box-shadow: 
      0 8px 24px rgba(0, 0, 0, 0.1),
      0 4px 12px rgba(0, 0, 0, 0.08);
    background: rgba(255, 255, 255, 0.98);
  }

  &:active {
    transform: scale(0.98);
  }

  &.danger::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #FF3B30, #FF6B5A);
    border-radius: 18px 18px 0 0;
  }

  &.warning::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #FF9500, #FFB340);
    border-radius: 18px 18px 0 0;
  }

  &.info::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, var(--ios-accent), var(--ios-secondary));
    border-radius: 18px 18px 0 0;
  }

  &.success::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #34C759, #5AD773);
    border-radius: 18px 18px 0 0;
  }

  .stat-icon {
    width: 64px;
    height: 64px;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    transition: all 0.25s cubic-bezier(0.4, 0.0, 0.2, 1);
    flex-shrink: 0;
  }

  &.danger .stat-icon {
    background: linear-gradient(135deg, #FF3B30 0%, #E0342E 100%);
    color: var(--ios-white);
  }

  &.warning .stat-icon {
    background: linear-gradient(135deg, #FF9500 0%, #E6850E 100%);
    color: var(--ios-white);
  }

  &.info .stat-icon {
    background: linear-gradient(135deg, var(--ios-accent) 0%, var(--ios-secondary) 100%);
    color: var(--ios-white);
  }

  &.success .stat-icon {
    background: linear-gradient(135deg, #34C759 0%, #30B753 100%);
    color: var(--ios-white);
  }

  .stat-content {
    flex: 1;
    min-width: 0;
  }

  .stat-number {
    font-size: 32px;
    font-weight: 700;
    line-height: 1.1;
    margin-bottom: 8px;
    letter-spacing: -0.4px;
  }

  &.danger .stat-number {
    color: #FF3B30;
  }

  &.warning .stat-number {
    color: #FF9500;
  }

  &.info .stat-number {
    color: var(--ios-accent);
  }

  &.success .stat-number {
    color: #34C759;
  }

  .stat-label {
    font-size: 16px;
    color: var(--ios-secondary-label);
    font-weight: 500;
    opacity: 0.8;
  }
}

/* iOS风格搜索区域 */
.search-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 18px;
  padding: 28px;
  margin-bottom: 32px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);

  .action-buttons {
    margin-top: 24px;
    display: flex;
    gap: 16px;
    flex-wrap: wrap;
  }
}

/* iOS风格批量操作区域 */
.batch-section {
  background: rgba(52, 199, 89, 0.1);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 18px;
  padding: 24px;
  margin-bottom: 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid rgba(52, 199, 89, 0.3);
  box-shadow: 
    0 4px 16px rgba(52, 199, 89, 0.1),
    0 2px 8px rgba(52, 199, 89, 0.05);

  .batch-info {
    color: #1B5E20;
    font-weight: 600;
    font-size: 16px;
  }

  .batch-actions {
    display: flex;
    gap: 16px;
  }
}

/* iOS风格表格区域 */
.table-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;

  .pagination-section {
    padding: 24px;
    display: flex;
    justify-content: center;
    background: transparent;
  }
}

/* iOS风格商品信息 */
.product-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 8px 0;

  .product-name {
    font-weight: 600;
    color: var(--ios-label);
    font-size: 15px;
    letter-spacing: -0.1px;
  }

  .product-barcode {
    font-size: 13px;
    color: var(--ios-secondary-label);
    font-family: 'SF Mono', 'Monaco', 'Consolas', 'Courier New', monospace;
    background: rgba(28, 28, 30, 0.06);
    padding: 2px 6px;
    border-radius: 6px;
    font-weight: 500;
    display: inline-block;
    width: fit-content;
  }
}

/* iOS风格操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

/* iOS风格详情对话框 */
.alert-detail {
  padding: 20px 0;
}

/* Element Plus 组件样式覆盖 */
:deep(.el-form) {
  .el-form-item {
    margin-bottom: 20px;

    .el-form-item__label {
      color: var(--ios-label);
      font-weight: 500;
      font-size: 16px;
    }
  }

  .el-select,
  .el-input {
    .el-input__wrapper {
      border-radius: 12px;
      background: rgba(28, 28, 30, 0.04);
      box-shadow: none;
      border: 1px solid var(--ios-separator);
      transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);

      &:hover {
        background: rgba(28, 28, 30, 0.06);
        border-color: var(--ios-accent);
      }

      &.is-focus {
        background: rgba(28, 28, 30, 0.08);
        border-color: var(--ios-accent);
        box-shadow: 0 0 0 3px rgba(28, 28, 30, 0.1);
      }
    }
  }

  .el-button {
    border-radius: 12px;
    font-weight: 500;
    padding: 10px 20px;
    transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
    border: none;

    &--primary {
      background: var(--ios-accent);
      color: var(--ios-white);
      box-shadow: 0 2px 8px rgba(28, 28, 30, 0.25);

      &:hover {
        background: var(--ios-secondary);
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(28, 28, 30, 0.35);
      }
    }

    &--success {
      background: #34C759;
      color: var(--ios-white);
      box-shadow: 0 2px 8px rgba(52, 199, 89, 0.25);

      &:hover {
        background: #30B753;
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(52, 199, 89, 0.35);
      }
    }

    &--warning {
      background: #FF9500;
      color: var(--ios-white);
      box-shadow: 0 2px 8px rgba(255, 149, 0, 0.25);

      &:hover {
        background: #E6850E;
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(255, 149, 0, 0.35);
      }
    }

    &--info {
      background: rgba(28, 28, 30, 0.8);
      color: var(--ios-white);
      box-shadow: 0 2px 8px rgba(28, 28, 30, 0.25);

      &:hover {
        background: var(--ios-secondary);
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(28, 28, 30, 0.35);
      }
    }

    &:not([class*="--"]) {
      background: rgba(28, 28, 30, 0.08);
      color: var(--ios-label);
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

:deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-border-color: var(--ios-separator);
  --el-table-header-text-color: var(--ios-label);
  --el-table-text-color: var(--ios-label);
  border-radius: 0;

  th {
    background: rgba(28, 28, 30, 0.04);
    border-bottom: 1px solid var(--ios-separator);
    font-weight: 600;
    font-size: 15px;
    padding: 16px 12px;
  }

  td {
    border-bottom: 1px solid rgba(199, 199, 204, 0.3);
    padding: 16px 12px;
  }

  .el-button {
    padding: 8px 12px;
    font-size: 13px;
    border-radius: 8px;
  }
}

:deep(.el-tag) {
  border-radius: 8px;
  font-weight: 500;
  font-size: 13px;
  padding: 6px 12px;
  border: none;
  
  &.el-tag--success {
    background: rgba(52, 199, 89, 0.15);
    color: #34C759;
  }
  
  &.el-tag--danger {
    background: rgba(255, 59, 48, 0.15);
    color: #FF3B30;
  }

  &.el-tag--warning {
    background: rgba(255, 149, 0, 0.15);
    color: #FF9500;
  }

  &.el-tag--info {
    background: rgba(28, 28, 30, 0.15);
    color: var(--ios-accent);
  }
}

:deep(.el-descriptions) {
  .el-descriptions__label {
    font-weight: 600;
    color: var(--ios-secondary-label);
    font-size: 15px;
  }

  .el-descriptions__content {
    color: var(--ios-label);
    font-size: 15px;
  }
}

:deep(.el-dialog) {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.12),
    0 4px 16px rgba(0, 0, 0, 0.08);

  .el-dialog__header {
    padding: 24px 24px 0;
    border: none;

    .el-dialog__title {
      font-size: 20px;
      font-weight: 700;
      color: var(--ios-label);
      letter-spacing: -0.3px;
    }
  }

  .el-dialog__body {
    padding: 24px;
  }
}

:deep(.el-pagination) {
  .el-pager li {
    background: transparent;
    border-radius: 8px;
    margin: 0 2px;
    color: var(--ios-secondary-label);
    font-weight: 500;
    
    &.is-active {
      background: var(--ios-accent);
      color: var(--ios-white);
    }
    
    &:hover {
      background: rgba(28, 28, 30, 0.08);
      color: var(--ios-label);
    }
  }
  
  .btn-prev,
  .btn-next {
    background: transparent;
    border: none;
    border-radius: 8px;
    color: var(--ios-secondary-label);
    
    &:hover {
      background: rgba(28, 28, 30, 0.08);
      color: var(--ios-label);
    }
  }
  
  .el-select {
    .el-input__wrapper {
      border-radius: 8px;
    }
  }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stock-alert-container {
    padding: 20px;
  }

  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 16px;
  }

  .stat-card {
    padding: 20px 16px;
    
    .stat-icon {
      width: 56px;
      height: 56px;
      font-size: 24px;
    }
    
    .stat-number {
      font-size: 28px;
    }
  }

  .search-section,
  .batch-section,
  .page-header {
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .stock-alert-container {
    padding: 16px;
  }

  .page-header {
    padding: 20px;

    h2 {
      font-size: 24px;
      
      .el-icon {
        font-size: 22px;
      }
    }

    p {
      font-size: 14px;
    }
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .stat-card {
    padding: 16px;
    flex-direction: column;
    text-align: center;
    gap: 16px;
    
    .stat-icon {
      width: 48px;
      height: 48px;
      font-size: 20px;
    }
    
    .stat-number {
      font-size: 24px;
    }
    
    .stat-label {
      font-size: 14px;
    }
  }

  .search-section {
    padding: 16px;

    :deep(.el-form--inline) {
      .el-form-item {
        display: block;
        margin-right: 0;
        margin-bottom: 16px;

        .el-select,
        .el-input {
          width: 100%;
        }
      }
    }

    .action-buttons {
      margin-top: 16px;
      gap: 12px;
      
      .el-button {
        flex: 1;
        min-width: 0;
      }
    }
  }

  .batch-section {
    padding: 16px;
    flex-direction: column;
    gap: 16px;
    text-align: center;

    .batch-actions {
      gap: 12px;
      
      .el-button {
        flex: 1;
      }
    }
  }

  :deep(.el-table) {
    font-size: 14px;

    th,
    td {
      padding: 12px 8px;
    }

    .el-button {
      padding: 6px 8px;
      font-size: 12px;
    }
  }
}

@media (max-width: 480px) {
  .stock-alert-container {
    padding: 12px;
  }

  .page-header {
    padding: 16px;

    h2 {
      font-size: 20px;
    }
  }

  .search-section,
  .batch-section {
    padding: 12px;
  }

  .stat-card {
    padding: 12px;
    
    .stat-icon {
      width: 40px;
      height: 40px;
      font-size: 18px;
    }
    
    .stat-number {
      font-size: 20px;
    }
    
    .stat-label {
      font-size: 13px;
    }
  }

  .action-buttons {
    .el-button {
      font-size: 14px;
      padding: 8px 12px;
    }
  }
}
</style>
