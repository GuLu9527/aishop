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

<style scoped>
.stock-alert-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  background: white;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-header p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-card.danger {
  border-left: 4px solid #f56c6c;
}

.stat-card.warning {
  border-left: 4px solid #e6a23c;
}

.stat-card.info {
  border-left: 4px solid #409eff;
}

.stat-card.success {
  border-left: 4px solid #67c23a;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-card.danger .stat-icon {
  background-color: #f56c6c;
}

.stat-card.warning .stat-icon {
  background-color: #e6a23c;
}

.stat-card.info .stat-icon {
  background-color: #409eff;
}

.stat-card.success .stat-icon {
  background-color: #67c23a;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.search-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.action-buttons {
  margin-top: 16px;
  display: flex;
  gap: 12px;
}

.batch-section {
  background: #e1f3d8;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid #b3d8a4;
}

.batch-info {
  color: #529b2e;
  font-weight: 500;
}

.batch-actions {
  display: flex;
  gap: 12px;
}

.table-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.product-name {
  font-weight: 500;
  color: #303133;
}

.product-barcode {
  font-size: 12px;
  color: #909399;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.pagination-section {
  padding: 20px;
  display: flex;
  justify-content: center;
}

.alert-detail {
  padding: 16px 0;
}

:deep(.el-table) {
  border-radius: 0;
}

:deep(.el-table th) {
  background-color: #fafafa;
  color: #606266;
  font-weight: 500;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-descriptions__content) {
  color: #303133;
}
</style>
