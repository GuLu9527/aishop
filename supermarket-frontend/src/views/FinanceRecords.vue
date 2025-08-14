<template>
  <div class="finance-records-container">
    <div class="page-header">
      <h1>收支记录</h1>
      <div class="header-actions">
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          添加记录
        </el-button>
        <el-button @click="exportRecords">
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
      </div>
    </div>

    <!-- 搜索筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="记录类型">
          <el-select v-model="searchForm.recordType" placeholder="请选择" clearable>
            <el-option label="收入" :value="1" />
            <el-option label="支出" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务类型">
          <el-select v-model="searchForm.businessType" placeholder="请选择" clearable>
            <el-option label="销售收入" :value="1" />
            <el-option label="采购支出" :value="2" />
            <el-option label="其他收入" :value="3" />
            <el-option label="其他支出" :value="4" />
            <el-option label="退货退款" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额范围">
          <el-input v-model="searchForm.minAmount" placeholder="最小金额" style="width: 120px" />
          <span style="margin: 0 10px">-</span>
          <el-input v-model="searchForm.maxAmount" placeholder="最大金额" style="width: 120px" />
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            class="enhanced-date-picker"
            :shortcuts="dateShortcuts"
            :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="searchForm.description" placeholder="请输入描述关键词" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchRecords">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table 
        :data="tableData" 
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="recordDate" label="记录日期" width="200">
          <template #default="{ row }">
            <div class="enhanced-date-cell">
              <div class="date-main">
                <el-icon class="date-icon"><Calendar /></el-icon>
                <span class="date-text">{{ formatDateEnhanced(row.recordDate) }}</span>
              </div>
              <div class="time-main">
                <el-icon class="time-icon"><Clock /></el-icon>
                <span class="time-text">{{ formatTime(row.recordDate) }}</span>
              </div>
              <div class="relative-time">{{ getRelativeTime(row.recordDate) }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="recordTypeText" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.recordType === 1 ? 'success' : 'danger'">
              {{ row.recordTypeText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="businessTypeText" label="业务类型" width="120" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">
            <span :class="row.recordType === 1 ? 'income-amount' : 'expense-amount'">
              {{ row.recordType === 1 ? '+' : '-' }}¥{{ formatAmount(row.amount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="订单号" width="150" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="operatorName" label="操作员" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="200">
          <template #default="{ row }">
            <div class="enhanced-date-cell secondary">
              <div class="date-main">
                <el-icon class="date-icon"><Calendar /></el-icon>
                <span class="date-text">{{ formatDateEnhanced(row.createTime) }}</span>
              </div>
              <div class="time-main">
                <el-icon class="time-icon"><Clock /></el-icon>
                <span class="time-text">{{ formatTime(row.createTime) }}</span>
              </div>
              <div class="relative-time">{{ getRelativeTime(row.createTime) }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteRecord(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 批量操作 -->
      <div class="batch-actions" v-if="selectedRecords.length > 0">
        <span>已选择 {{ selectedRecords.length }} 条记录</span>
        <el-button type="danger" size="small" @click="batchDelete">批量删除</el-button>
      </div>

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
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="记录类型" prop="recordType">
          <el-radio-group v-model="form.recordType">
            <el-radio :label="1">收入</el-radio>
            <el-radio :label="2">支出</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="业务类型" prop="businessType">
          <el-select v-model="form.businessType" placeholder="请选择业务类型">
            <el-option label="销售收入" :value="1" />
            <el-option label="采购支出" :value="2" />
            <el-option label="其他收入" :value="3" />
            <el-option label="其他支出" :value="4" />
            <el-option label="退货退款" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input v-model="form.amount" placeholder="请输入金额">
            <template #prepend>¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="订单号">
          <el-input v-model="form.orderNo" placeholder="请输入订单号（可选）" />
        </el-form-item>
        <el-form-item label="描述说明" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入描述说明"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input 
            v-model="form.remark" 
            type="textarea" 
            :rows="2"
            placeholder="请输入备注（可选）"
          />
        </el-form-item>
        <el-form-item label="记录日期" prop="recordDate">
          <el-date-picker
            v-model="form.recordDate"
            type="datetime"
            placeholder="选择记录日期"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            class="enhanced-date-picker"
            :shortcuts="singleDateShortcuts"
            :default-time="new Date(2000, 1, 1, new Date().getHours(), new Date().getMinutes(), 0)"
          />
          <div class="date-picker-tip">
            <el-icon><InfoFilled /></el-icon>
            <span>默认为当前时间，可手动调整</span>
          </div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Download, Calendar, Clock, InfoFilled } from '@element-plus/icons-vue'
import {
  getFinanceRecordPage,
  addFinanceRecord,
  updateFinanceRecord,
  deleteFinanceRecord,
  batchDeleteFinanceRecords,
  exportFinanceRecords
} from '@/api/finance'
import {
  formatDateTime,
  formatDateEnhanced,
  formatTime,
  getRelativeTime,
  dateTimeRangeShortcuts,
  singleDateTimeShortcuts,
  formatForBackend,
  disableFutureDate
} from '@/utils/dateUtils'

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const selectedRecords = ref<any[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加记录')
const isEdit = ref(false)
const editId = ref(null)

// 搜索表单
const searchForm = reactive({
  recordType: null,
  businessType: null,
  minAmount: '',
  maxAmount: '',
  description: '',
  recordStartDate: '',
  recordEndDate: '',
  pageNum: 1,
  pageSize: 10
})

const dateRange = ref([])

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表单数据
const form = reactive({
  recordType: 1,
  businessType: null,
  amount: '',
  orderNo: '',
  description: '',
  remark: '',
  recordDate: ''
})

// 表单引用
const formRef = ref()

// 表单验证规则
const rules = {
  recordType: [
    { required: true, message: '请选择记录类型', trigger: 'change' }
  ],
  businessType: [
    { required: true, message: '请选择业务类型', trigger: 'change' }
  ],
  amount: [
    { required: true, message: '请输入金额', trigger: 'blur' },
    { pattern: /^\d+(\.\d{1,2})?$/, message: '请输入正确的金额格式', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入描述说明', trigger: 'blur' }
  ],
  recordDate: [
    { required: true, message: '请选择记录日期', trigger: 'change' }
  ]
}

// 使用统一的日期快捷选项
const dateShortcuts = dateTimeRangeShortcuts

// 单个日期快捷选项
const singleDateShortcuts = [
  {
    text: '现在',
    value: new Date()
  },
  {
    text: '今天开始',
    value: () => {
      const now = new Date()
      return new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0)
    }
  },
  {
    text: '今天结束',
    value: () => {
      const now = new Date()
      return new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59)
    }
  }
]

// 页面加载时获取数据
onMounted(() => {
  loadRecords()
})

// 监听日期范围变化
watch(dateRange, (newVal) => {
  if (newVal && newVal.length === 2) {
    searchForm.recordStartDate = newVal[0]
    searchForm.recordEndDate = newVal[1]
  } else {
    searchForm.recordStartDate = ''
    searchForm.recordEndDate = ''
  }
})

// 加载记录数据
const loadRecords = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    
    const response = await getFinanceRecordPage(params)
    if (response.data.code === 200) {
      tableData.value = response.data.data.records || []
      pagination.total = response.data.data.total || 0
    } else {
      ElMessage.error(response.data.message || '加载数据失败')
    }
  } catch (error) {
    console.error('加载记录失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索记录
const searchRecords = () => {
  pagination.pageNum = 1
  loadRecords()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    recordType: null,
    businessType: null,
    minAmount: '',
    maxAmount: '',
    description: '',
    recordStartDate: '',
    recordEndDate: ''
  })
  dateRange.value = []
  pagination.pageNum = 1
  loadRecords()
}

// 显示添加对话框
const showAddDialog = () => {
  dialogTitle.value = '添加记录'
  isEdit.value = false
  editId.value = null
  resetForm()
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row: any) => {
  dialogTitle.value = '编辑记录'
  isEdit.value = true
  editId.value = row.id
  
  Object.assign(form, {
    recordType: row.recordType,
    businessType: row.businessType,
    amount: row.amount,
    orderNo: row.orderNo || '',
    description: row.description,
    remark: row.remark || '',
    recordDate: row.recordDate
  })
  
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    recordType: 1,
    businessType: null,
    amount: '',
    orderNo: '',
    description: '',
    remark: '',
    recordDate: ''
  })
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        const formData = { ...form }

        // 确保日期格式正确
        if (formData.recordDate) {
          // 使用统一的日期格式化工具
          const dateValue = (formData.recordDate as any) instanceof Date
            ? (formData.recordDate as unknown as Date)
            : new Date(formData.recordDate as string)
          formData.recordDate = formatForBackend(dateValue, true)
        }

        console.log('提交的表单数据:', formData)

        if (isEdit.value && editId.value) {
          const response = await updateFinanceRecord(editId.value, formData)
          if (response.data.code === 200) {
            ElMessage.success('更新记录成功')
            dialogVisible.value = false
            loadRecords()
          } else {
            ElMessage.error(response.data.message || '更新记录失败')
          }
        } else {
          const response = await addFinanceRecord(formData)
          if (response.data.code === 200) {
            ElMessage.success('添加记录成功')
            dialogVisible.value = false
            loadRecords()
          } else {
            ElMessage.error(response.data.message || '添加记录失败')
          }
        }
      } catch (error) {
        console.error('提交表单失败:', error)
        ElMessage.error('操作失败')
      }
    }
  })
}

// 删除记录
const deleteRecord = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '确认删除', {
      type: 'warning'
    })
    
    const response = await deleteFinanceRecord(id)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      loadRecords()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除记录失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const batchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRecords.value.length} 条记录吗？`, '确认删除', {
      type: 'warning'
    })
    
    const ids = selectedRecords.value.map((record: any) => record.id)
    const response = await batchDeleteFinanceRecords(ids)
    if (response.data.code === 200) {
      ElMessage.success('批量删除成功')
      selectedRecords.value = []
      loadRecords()
    } else {
      ElMessage.error(response.data.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 导出记录
const exportRecords = async () => {
  try {
    const params = { ...searchForm }
    const response = await exportFinanceRecords(params)
    if (response.data.code === 200) {
      // 这里可以实现具体的导出逻辑，比如下载Excel文件
      ElMessage.success('导出成功')
    } else {
      ElMessage.error(response.data.message || '导出失败')
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 表格选择变化
const handleSelectionChange = (selection: any[]) => {
  selectedRecords.value = selection
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  loadRecords()
}

// 当前页变化
const handleCurrentChange = (page: number) => {
  pagination.pageNum = page
  loadRecords()
}

// 格式化金额
const formatAmount = (amount: number) => {
  return (amount || 0).toFixed(2)
}

// 本地日期格式化函数已移除，使用统一的 dateUtils 工具








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

.finance-records-container {
  min-height: 100vh;
  background: var(--ios-system-background);
  padding: 24px;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', system-ui, sans-serif;
}

/* iOS风格页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 20px;
  padding: 32px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);

  h1 {
    margin: 0;
    font-size: 32px;
    font-weight: 700;
    color: var(--ios-label);
    letter-spacing: -0.6px;
    line-height: 1.2;
  }
}

.header-actions {
  display: flex;
  gap: 16px;
  
  .el-button {
    height: 44px;
    border-radius: 12px;
    font-size: 15px;
    font-weight: 500;
    padding: 0 20px;
    border: none;
    transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
    
    &--primary {
      background: var(--ios-accent);
      color: var(--ios-white);
      box-shadow: 0 2px 8px rgba(28, 28, 30, 0.25);
      
      &:hover {
        background: var(--ios-secondary);
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(28, 28, 30, 0.35);
      }
      
      &:active {
        transform: scale(0.98);
      }
    }
    
    &:not(.el-button--primary) {
      background: rgba(28, 28, 30, 0.08);
      color: var(--ios-label);
      box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
      
      &:hover {
        background: rgba(28, 28, 30, 0.12);
        transform: translateY(-1px);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
      }
    }
  }
}

/* iOS风格搜索卡片 */
:deep(.search-card) {
  margin-bottom: 32px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);

  .el-card__body {
    padding: 28px;
  }
}

/* iOS风格表格卡片 */
:deep(.table-card) {
  margin-bottom: 32px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;

  .el-card__body {
    padding: 28px;
  }
}

.batch-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-top: 1px solid rgba(199, 199, 204, 0.3);
  margin-top: 20px;
  
  span {
    font-size: 16px;
    color: var(--ios-secondary-label);
    font-weight: 500;
  }
  
  .el-button {
    border-radius: 12px;
    font-weight: 500;
    
    &--danger {
      background: rgba(255, 59, 48, 0.1);
      color: #FF3B30;
      border: 1px solid rgba(255, 59, 48, 0.2);
      
      &:hover {
        background: rgba(255, 59, 48, 0.15);
        border-color: #FF3B30;
        transform: translateY(-1px);
      }
    }
  }
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* iOS风格金额显示 */
.income-amount {
  color: #34C759;
  font-weight: 600;
  font-size: 16px;
  letter-spacing: -0.2px;
}

.expense-amount {
  color: #FF3B30;
  font-weight: 600;
  font-size: 16px;
  letter-spacing: -0.2px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  
  .el-button {
    border-radius: 12px;
    font-weight: 500;
    padding: 10px 20px;
    
    &--primary {
      background: var(--ios-accent);
      border: none;
      
      &:hover {
        background: var(--ios-secondary);
      }
    }
  }
}

/* iOS风格增强的日期时间单元格样式 */
.enhanced-date-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 12px 8px;
  
  &.secondary {
    opacity: 0.8;
  }
}

.date-main, .time-main {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-icon {
  color: var(--ios-accent);
  font-size: 16px;
  opacity: 0.8;
}

.time-icon {
  color: #34C759;
  font-size: 14px;
  opacity: 0.8;
}

.date-text {
  font-size: 15px;
  font-weight: 600;
  color: var(--ios-label);
  letter-spacing: -0.1px;
}

.time-text {
  font-size: 13px;
  color: var(--ios-secondary-label);
  font-family: 'SF Mono', 'Monaco', 'Consolas', 'Courier New', monospace;
  background: rgba(28, 28, 30, 0.06);
  padding: 4px 8px;
  border-radius: 8px;
  font-weight: 500;
}

.relative-time {
  font-size: 12px;
  color: var(--ios-gray);
  font-style: italic;
  margin-left: 24px;
  opacity: 0.7;
}

/* iOS风格增强的日期选择器样式 */
.enhanced-date-picker {
  width: 100%;
  
  :deep(.el-input__wrapper) {
    border-radius: 12px;
    background: rgba(28, 28, 30, 0.04);
    border: 1px solid var(--ios-separator);
    box-shadow: none;
    transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
    
    &:hover {
      background: rgba(28, 28, 30, 0.06);
      border-color: var(--ios-accent);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    }
    
    &.is-focus {
      background: rgba(28, 28, 30, 0.08);
      border-color: var(--ios-accent);
      box-shadow: 0 0 0 3px rgba(28, 28, 30, 0.1);
    }
  }
}

/* iOS风格日期选择器提示 */
.date-picker-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  font-size: 14px;
  color: var(--ios-secondary-label);
  opacity: 0.8;
  
  .el-icon {
    color: var(--ios-accent);
    opacity: 0.8;
  }
}

/* iOS风格日期时间单元格样式（保留兼容性） */
.date-time-cell {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  line-height: 1.3;
  gap: 4px;
}

.date-part {
  font-size: 15px;
  font-weight: 600;
  color: var(--ios-label);
  letter-spacing: -0.1px;
}

.time-part {
  font-size: 13px;
  color: var(--ios-secondary-label);
  font-family: 'SF Mono', 'Monaco', 'Consolas', 'Courier New', monospace;
  background: rgba(28, 28, 30, 0.06);
  padding: 2px 6px;
  border-radius: 6px;
  font-weight: 500;
}

/* iOS风格Element Plus组件覆盖 */
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
  
  .el-radio-group {
    .el-radio {
      margin-right: 24px;
      
      .el-radio__label {
        color: var(--ios-label);
        font-weight: 500;
      }
      
      .el-radio__inner {
        border-color: var(--ios-separator);
        
        &:hover {
          border-color: var(--ios-accent);
        }
      }
      
      &.is-checked {
        .el-radio__inner {
          background: var(--ios-accent);
          border-color: var(--ios-accent);
        }
        
        .el-radio__label {
          color: var(--ios-accent);
        }
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

    &:not(.el-button--primary) {
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
    
    &--danger {
      background: rgba(255, 59, 48, 0.1);
      color: #FF3B30;
      border: 1px solid rgba(255, 59, 48, 0.2);
      
      &:hover {
        background: rgba(255, 59, 48, 0.15);
        border-color: #FF3B30;
      }
    }
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
  .finance-records-container {
    padding: 20px;
  }

  .page-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
    
    .header-actions {
      width: 100%;
      justify-content: center;
    }
  }

  :deep(.search-card),
  :deep(.table-card) {
    .el-card__body {
      padding: 20px;
    }
  }
}

@media (max-width: 768px) {
  .finance-records-container {
    padding: 16px;
  }

  .page-header {
    padding: 20px;
    
    h1 {
      font-size: 24px;
    }
    
    .header-actions {
      flex-direction: column;
      gap: 12px;
      
      .el-button {
        width: 100%;
      }
    }
  }

  :deep(.search-card),
  :deep(.table-card) {
    .el-card__body {
      padding: 16px;
    }
  }

  :deep(.el-form--inline) {
    .el-form-item {
      display: block;
      margin-right: 0;
      margin-bottom: 16px;
      width: 100%;
      
      .el-select,
      .el-input,
      .el-date-picker {
        width: 100%;
      }
    }
  }

  .batch-actions {
    flex-direction: column;
    gap: 12px;
    text-align: center;
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

  .enhanced-date-cell {
    padding: 8px 4px;
    gap: 4px;
    
    .date-text {
      font-size: 14px;
    }
    
    .time-text {
      font-size: 12px;
      padding: 2px 6px;
    }
    
    .relative-time {
      font-size: 11px;
      margin-left: 16px;
    }
  }
}

@media (max-width: 480px) {
  .finance-records-container {
    padding: 12px;
  }

  .page-header {
    padding: 16px;
    
    h1 {
      font-size: 20px;
    }
  }

  :deep(.search-card),
  :deep(.table-card) {
    .el-card__body {
      padding: 12px;
    }
  }
}
</style>
