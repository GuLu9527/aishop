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

<style scoped>
.finance-records-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  color: #333;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.batch-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-top: 1px solid #ebeef5;
  margin-top: 10px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.income-amount {
  color: #67c23a;
  font-weight: bold;
}

.expense-amount {
  color: #f56c6c;
  font-weight: bold;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 增强的日期时间单元格样式 */
.enhanced-date-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px 0;
}

.enhanced-date-cell.secondary {
  opacity: 0.8;
}

.date-main, .time-main {
  display: flex;
  align-items: center;
  gap: 6px;
}

.date-icon {
  color: #409eff;
  font-size: 14px;
}

.time-icon {
  color: #67c23a;
  font-size: 12px;
}

.date-text {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.time-text {
  font-size: 12px;
  color: #606266;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 4px;
}

.relative-time {
  font-size: 11px;
  color: #909399;
  font-style: italic;
  margin-left: 20px;
}

/* 增强的日期选择器样式 */
.enhanced-date-picker {
  width: 100%;
}

.enhanced-date-picker .el-input__wrapper {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.enhanced-date-picker .el-input__wrapper:hover {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.enhanced-date-picker .el-input__wrapper.is-focus {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 日期选择器提示 */
.date-picker-tip {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

.date-picker-tip .el-icon {
  color: #409eff;
}

/* 日期时间单元格样式（保留兼容性） */
.date-time-cell {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  line-height: 1.2;
}

.date-part {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 2px;
}

.time-part {
  font-size: 12px;
  color: #909399;
  font-family: 'Consolas', 'Monaco', monospace;
}
</style>
