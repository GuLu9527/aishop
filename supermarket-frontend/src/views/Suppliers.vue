<template>
  <div class="suppliers-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>供货商管理</h2>
        <p>管理供货商信息，维护供应链关系</p>
      </div>
      <div class="header-stats">
        <div class="stat-card">
          <div class="stat-number">{{ supplierStats.totalCount || 0 }}</div>
          <div class="stat-label">供货商总数</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ supplierStats.activeCount || 0 }}</div>
          <div class="stat-label">正常供货商</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ supplierStats.inactiveCount || 0 }}</div>
          <div class="stat-label">停用供货商</div>
        </div>
      </div>
    </div>

    <!-- 搜索和操作区域 -->
    <div class="search-section">
      <el-card>
        <el-form :model="searchForm" inline>
          <el-form-item label="供货商名称">
            <el-input
              v-model="searchForm.supplierName"
              placeholder="请输入供货商名称"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="联系人">
            <el-input
              v-model="searchForm.contactPerson"
              placeholder="请输入联系人"
              clearable
              style="width: 150px"
            />
          </el-form-item>
          <el-form-item label="信用等级">
            <el-select v-model="searchForm.creditRating" placeholder="请选择信用等级" clearable>
              <el-option label="全部" value="" />
              <el-option label="1星" :value="1" />
              <el-option label="2星" :value="2" />
              <el-option label="3星" :value="3" />
              <el-option label="4星" :value="4" />
              <el-option label="5星" :value="5" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="正常" :value="1" />
              <el-option label="停用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getSupplierList">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetSearch">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 操作按钮区域 -->
    <div class="action-section">
      <div class="action-left">
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          添加供货商
        </el-button>
        <el-button
          type="danger"
          :disabled="selectedSuppliers.length === 0"
          @click="batchDeleteConfirm"
        >
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
        <el-button
          type="success"
          @click="handleExport"
          :loading="exportLoading"
        >
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
      </div>
      <div class="action-right">
        <el-button size="small" @click="getSupplierList">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 供货商列表 -->
    <div class="table-section">
      <el-card>
        <el-table
          v-loading="loading"
          :data="supplierList"
          @selection-change="handleSelectionChange"
          stripe
          style="width: 100%"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="supplierName" label="供货商名称" min-width="150">
            <template #default="{ row }">
              <div class="supplier-name">
                <span class="name">{{ row.supplierName }}</span>
                <el-tag v-if="row.status === 0" type="danger" size="small">已停用</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="contactPerson" label="联系人" width="100" />
          <el-table-column prop="phone" label="联系电话" width="130" />
          <el-table-column prop="creditRating" label="信用等级" width="100">
            <template #default="{ row }">
              <el-rate
                v-model="row.creditRating"
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value}星"
              />
            </template>
          </el-table-column>
          <el-table-column prop="deliveryCycle" label="供货周期" width="100">
            <template #default="{ row }">
              <span v-if="row.deliveryCycle">{{ row.deliveryCycle }}天</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="productCount" label="合作商品" width="100">
            <template #default="{ row }">
              <el-tag type="info" size="small">{{ row.productCount || 0 }}个</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="160">
            <template #default="{ row }">
              {{ formatDateTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-tooltip content="查看详情" placement="top">
                <el-button size="small" type="primary" circle @click="viewSupplier(row)">
                  <el-icon><View /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="编辑供货商" placement="top">
                <el-button size="small" type="success" circle @click="editSupplier(row)">
                  <el-icon><Edit /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip :content="row.status === 1 ? '停用供货商' : '启用供货商'" placement="top">
                <el-button 
                  size="small" 
                  :type="row.status === 1 ? 'warning' : 'success'" 
                  circle 
                  @click="toggleStatus(row)"
                >
                  <el-icon><Switch /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="删除供货商" placement="top">
                <el-button
                  size="small"
                  type="danger"
                  circle
                  @click="handleDeleteSupplier(row)"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </el-tooltip>
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
      </el-card>
    </div>

    <!-- 添加/编辑供货商对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :close-on-click-modal="false"
      class="supplier-dialog"
    >
      <el-form
        ref="supplierFormRef"
        :model="supplierForm"
        :rules="supplierRules"
        label-width="120px"
        class="supplier-form"
      >
        <!-- 基础信息区域 -->
        <div class="form-section">
          <div class="section-title">
            <el-icon><User /></el-icon>
            <span>基础信息</span>
          </div>
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="供货商名称" prop="supplierName">
                <el-input
                  v-model="supplierForm.supplierName"
                  placeholder="请输入供货商名称"
                  :disabled="!isEdit"
                  size="large"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系人" prop="contactPerson">
                <el-input
                  v-model="supplierForm.contactPerson"
                  placeholder="请输入联系人姓名"
                  :disabled="!isEdit"
                  size="large"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="联系电话" prop="phone">
                <el-input
                  v-model="supplierForm.phone"
                  placeholder="请输入联系电话"
                  :disabled="!isEdit"
                  size="large"
                >
                  <template #prefix>
                    <el-icon><Phone /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="邮箱地址" prop="email">
                <el-input
                  v-model="supplierForm.email"
                  placeholder="请输入邮箱地址"
                  :disabled="!isEdit"
                  size="large"
                >
                  <template #prefix>
                    <el-icon><Message /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="详细地址" prop="address">
            <el-input
              v-model="supplierForm.address"
              type="textarea"
              :rows="3"
              placeholder="请输入详细地址"
              :disabled="!isEdit"
              resize="none"
            />
          </el-form-item>
        </div>

        <!-- 业务信息区域 -->
        <div class="form-section">
          <div class="section-title">
            <el-icon><Setting /></el-icon>
            <span>业务信息</span>
          </div>
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="信用等级" prop="creditRating">
                <div class="credit-rating-container">
                  <el-rate
                    v-model="supplierForm.creditRating"
                    :disabled="!isEdit"
                    show-text
                    :texts="['极差', '较差', '一般', '良好', '优秀']"
                    size="large"
                  />
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="供货周期" prop="deliveryCycle">
                <el-input-number
                  v-model="supplierForm.deliveryCycle"
                  :min="1"
                  :max="365"
                  placeholder="请输入供货周期"
                  :disabled="!isEdit"
                  size="large"
                  style="width: 100%"
                >
                  <template #append>天</template>
                </el-input-number>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="状态" prop="status">
                <el-radio-group
                  v-model="supplierForm.status"
                  :disabled="!isEdit"
                  size="large"
                >
                  <el-radio :label="1" size="large">
                    <el-icon><Check /></el-icon>
                    正常
                  </el-radio>
                  <el-radio :label="0" size="large">
                    <el-icon><Close /></el-icon>
                    停用
                  </el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="付款条件" prop="paymentTerms">
            <el-input
              v-model="supplierForm.paymentTerms"
              type="textarea"
              :rows="3"
              placeholder="请输入付款条件，如：货到付款、月结30天等"
              :disabled="!isEdit"
              resize="none"
            />
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button v-if="isEdit" type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import {
  Plus, Search, Refresh, View, Edit, Delete, Switch,
  User, Phone, Message, Setting, Check, Close, Download
} from '@element-plus/icons-vue'
import {
  getSupplierPage,
  getSupplierById,
  addSupplier,
  updateSupplier,
  deleteSupplier,
  batchDeleteSuppliers,
  updateSupplierStatus,
  getSupplierStats,
  exportSuppliers
} from '@/api/supplier'

// 接口类型定义
interface Supplier {
  id?: number
  supplierName: string
  contactPerson?: string
  phone?: string
  address?: string
  email?: string
  creditRating?: number
  paymentTerms?: string
  deliveryCycle?: number
  status: number
  createTime?: string
  updateTime?: string
  productCount?: number
  lastPurchaseTime?: string
}

interface SupplierQuery {
  pageNum: number
  pageSize: number
  supplierName?: string
  contactPerson?: string
  phone?: string
  creditRating?: number
  status?: number
  createTimeStart?: string
  createTimeEnd?: string
}

// 响应式数据
const loading = ref(false)
const exportLoading = ref(false)
const supplierList = ref<Supplier[]>([])
const selectedSuppliers = ref<Supplier[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(true)
const supplierFormRef = ref<FormInstance>()

// 分页数据
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 搜索表单
const searchForm = reactive<SupplierQuery>({
  pageNum: 1,
  pageSize: 10,
  supplierName: '',
  contactPerson: '',
  phone: '',
  creditRating: undefined,
  status: undefined
})

// 供货商表单
const supplierForm = reactive<Supplier>({
  supplierName: '',
  contactPerson: '',
  phone: '',
  address: '',
  email: '',
  creditRating: 5,
  paymentTerms: '',
  deliveryCycle: undefined,
  status: 1
})

// 统计数据
const supplierStats = reactive({
  totalCount: 0,
  activeCount: 0,
  inactiveCount: 0
})

// 表单验证规则
const supplierRules = {
  supplierName: [
    { required: true, message: '请输入供货商名称', trigger: 'blur' },
    { min: 2, max: 100, message: '供货商名称长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  contactPerson: [
    { max: 50, message: '联系人姓名长度不能超过50个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^[1][3-9]\d{9}$|^0\d{2,3}-?\d{7,8}$/, message: '请输入正确的手机号或固定电话', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
    { max: 100, message: '邮箱长度不能超过100个字符', trigger: 'blur' }
  ],
  address: [
    { max: 200, message: '地址长度不能超过200个字符', trigger: 'blur' }
  ],
  creditRating: [
    { required: true, message: '请选择信用等级', trigger: 'change' }
  ],
  deliveryCycle: [
    { type: 'number', min: 1, max: 365, message: '供货周期应在1-365天之间', trigger: 'blur' }
  ],
  paymentTerms: [
    { max: 100, message: '付款条件长度不能超过100个字符', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取供货商列表
const getSupplierList = async () => {
  try {
    loading.value = true
    const query = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const response = await getSupplierPage(query)
    if (response.data.code === 200) {
      supplierList.value = response.data.data.records
      pagination.total = response.data.data.total
    } else {
      ElMessage.error(response.data.message || '获取供货商列表失败')
    }
  } catch (error) {
    console.error('获取供货商列表失败:', error)
    ElMessage.error('获取供货商列表失败')
  } finally {
    loading.value = false
  }
}

// 获取统计信息
const getStats = async () => {
  try {
    const response = await getSupplierStats()
    if (response.data.code === 200) {
      const stats = response.data.data
      supplierStats.totalCount = stats.totalCount || 0
      supplierStats.activeCount = stats.activeCount || 0
      supplierStats.inactiveCount = stats.inactiveCount || 0
    }
  } catch (error) {
    console.error('获取统计信息失败:', error)
  }
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    supplierName: '',
    contactPerson: '',
    phone: '',
    creditRating: undefined,
    status: undefined
  })
  pagination.pageNum = 1
  getSupplierList()
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  getSupplierList()
}

// 当前页改变
const handleCurrentChange = (page: number) => {
  pagination.pageNum = page
  getSupplierList()
}

// 选择改变
const handleSelectionChange = (selection: Supplier[]) => {
  selectedSuppliers.value = selection
}

// 显示添加对话框
const showAddDialog = () => {
  dialogTitle.value = '添加供货商'
  isEdit.value = true
  dialogVisible.value = true
  resetForm()
}

// 查看供货商
const viewSupplier = async (row: Supplier) => {
  try {
    const response = await getSupplierById(row.id!)
    if (response.data.code === 200) {
      dialogTitle.value = '查看供货商详情'
      isEdit.value = false
      dialogVisible.value = true
      Object.assign(supplierForm, response.data.data)
    } else {
      ElMessage.error(response.data.message || '获取供货商详情失败')
    }
  } catch (error) {
    console.error('获取供货商详情失败:', error)
    ElMessage.error('获取供货商详情失败')
  }
}

// 编辑供货商
const editSupplier = async (row: Supplier) => {
  try {
    const response = await getSupplierById(row.id!)
    if (response.data.code === 200) {
      dialogTitle.value = '编辑供货商'
      isEdit.value = true
      dialogVisible.value = true
      Object.assign(supplierForm, response.data.data)
    } else {
      ElMessage.error(response.data.message || '获取供货商详情失败')
    }
  } catch (error) {
    console.error('获取供货商详情失败:', error)
    ElMessage.error('获取供货商详情失败')
  }
}

// 切换状态
const toggleStatus = async (row: Supplier) => {
  const newStatus = row.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '启用' : '停用'

  try {
    await ElMessageBox.confirm(
      `确定要${statusText}供货商"${row.supplierName}"吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await updateSupplierStatus(row.id!, newStatus)
    if (response.data.code === 200) {
      ElMessage.success(`${statusText}成功`)
      getSupplierList()
    } else {
      ElMessage.error(response.data.message || `${statusText}失败`)
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error(`${statusText}供货商失败:`, error)
      ElMessage.error(`${statusText}失败`)
    }
  }
}

// 删除供货商
const handleDeleteSupplier = async (row: Supplier) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除供货商"${row.supplierName}"吗？删除后不可恢复！`,
      '确认删除',
      {
        type: 'warning',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'el-button--danger'
      }
    )

    const response = await deleteSupplier(row.id!)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      getSupplierList()
      getStats()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除供货商失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除确认
const batchDeleteConfirm = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedSuppliers.value.length} 个供货商吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const ids = selectedSuppliers.value.map(s => s.id!)
    const response = await batchDeleteSuppliers(ids)

    if (response.data.code === 200) {
      ElMessage.success('批量删除成功')
      getSupplierList()
      getStats()
    } else {
      ElMessage.error(response.data.message || '批量删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 提交表单
const submitForm = async () => {
  if (!supplierFormRef.value) return

  try {
    await supplierFormRef.value.validate()

    // 只提取需要的字段，避免发送显示字段
    const submitData = {
      id: supplierForm.id,
      supplierName: supplierForm.supplierName,
      contactPerson: supplierForm.contactPerson,
      phone: supplierForm.phone,
      address: supplierForm.address,
      email: supplierForm.email,
      creditRating: supplierForm.creditRating,
      paymentTerms: supplierForm.paymentTerms,
      deliveryCycle: supplierForm.deliveryCycle,
      status: supplierForm.status
    }

    const isAdd = !supplierForm.id
    const response = isAdd
      ? await addSupplier(submitData)
      : await updateSupplier(supplierForm.id!, submitData)

    if (response.data.code === 200) {
      ElMessage.success(isAdd ? '添加成功' : '更新成功')
      dialogVisible.value = false
      getSupplierList()
      getStats()
    } else {
      ElMessage.error(response.data.message || (isAdd ? '添加失败' : '更新失败'))
    }
  } catch (error) {
    console.error('提交表单失败:', error)
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(supplierForm, {
    id: undefined,
    supplierName: '',
    contactPerson: '',
    phone: '',
    address: '',
    email: '',
    creditRating: 5,
    paymentTerms: '',
    deliveryCycle: undefined,
    status: 1
  })
  supplierFormRef.value?.clearValidate()
}

// 格式化日期时间
const formatDateTime = (dateTime: string | number[]) => {
  if (!dateTime) return ''

  try {
    // 处理后端返回的数组格式 [2025, 7, 18, 11, 24, 8]
    if (Array.isArray(dateTime)) {
      const [year, month, day, hour, minute, second] = dateTime
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
    return ''
  }
}

// 导出供货商数据
const handleExport = async () => {
  try {
    exportLoading.value = true

    // 使用当前的搜索条件进行导出
    const exportQuery = {
      supplierName: searchForm.supplierName,
      contactPerson: searchForm.contactPerson,
      phone: searchForm.phone,
      creditRating: searchForm.creditRating,
      status: searchForm.status
    }

    const response = await exportSuppliers(exportQuery)

    // 创建下载链接
    const blob = new Blob([response.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })

    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url

    // 生成文件名
    const now = new Date()
    const timestamp = now.toISOString().slice(0, 19).replace(/[-:]/g, '').replace('T', '_')
    link.download = `供货商数据_${timestamp}.xlsx`

    // 触发下载
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出供货商数据失败:', error)
    ElMessage.error('导出失败')
  } finally {
    exportLoading.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  getSupplierList()
  getStats()
})
</script>

<style scoped>
.suppliers-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left h2 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.header-left p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.header-stats {
  display: flex;
  gap: 20px;
}

.stat-card {
  text-align: center;
  padding: 16px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;
  min-width: 100px;
}

.stat-card:nth-child(2) {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-card:nth-child(3) {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  opacity: 0.9;
}

.search-section {
  margin-bottom: 20px;
}

.action-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.action-left {
  display: flex;
  gap: 12px;
}

.table-section {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.supplier-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.supplier-name .name {
  font-weight: 500;
  color: #303133;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.dialog-footer {
  text-align: right;
}

/* 供货商对话框样式 */
.supplier-dialog {
  .el-dialog__body {
    padding: 30px;
    max-height: 70vh;
    overflow-y: auto;
  }
}

/* 供货商表单样式 */
.supplier-form {
  .form-section {
    margin-bottom: 32px;
    padding: 24px;
    background: #fafafa;
    border-radius: 8px;
    border: 1px solid #e4e7ed;
  }

  .section-title {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    font-size: 16px;
    font-weight: 600;
    color: #303133;

    .el-icon {
      margin-right: 8px;
      color: #409eff;
      font-size: 18px;
    }
  }

  .el-form-item {
    margin-bottom: 24px;
  }

  .el-form-item__label {
    font-weight: 500;
    color: #606266;
  }

  .el-input, .el-textarea {
    .el-input__inner, .el-textarea__inner {
      border-radius: 6px;
      transition: all 0.3s ease;
    }
  }

  .el-input:hover .el-input__inner,
  .el-textarea:hover .el-textarea__inner {
    border-color: #c0c4cc;
  }

  .el-input.is-focus .el-input__inner,
  .el-textarea.is-focus .el-textarea__inner {
    border-color: #409eff;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
  }

  .credit-rating-container {
    padding: 8px 0;

    .el-rate {
      display: flex;
      align-items: center;
    }

    .el-rate__text {
      margin-left: 12px;
      font-weight: 500;
      color: #606266;
    }
  }

  .el-radio-group {
    .el-radio {
      margin-right: 24px;

      .el-radio__label {
        display: flex;
        align-items: center;

        .el-icon {
          margin-right: 4px;
        }
      }
    }
  }

  .el-input-number {
    .el-input-number__increase,
    .el-input-number__decrease {
      border-radius: 0;
    }
  }
}

/* 表单验证错误样式 */
.supplier-form .el-form-item.is-error {
  .el-input__inner,
  .el-textarea__inner {
    border-color: #f56c6c;
  }

  .el-form-item__error {
    font-size: 12px;
    margin-top: 4px;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .suppliers-container {
    padding: 10px;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }

  .header-stats {
    justify-content: center;
    flex-wrap: wrap;
  }

  .action-section {
    flex-direction: column;
    gap: 12px;
  }

  .action-left {
    width: 100%;
    justify-content: center;
  }

  /* 移动端对话框优化 */
  .supplier-dialog {
    width: 95% !important;
    margin: 0 auto;

    .el-dialog__body {
      padding: 20px;
      max-height: 60vh;
    }
  }

  .supplier-form {
    .form-section {
      padding: 16px;
      margin-bottom: 20px;
    }

    .section-title {
      font-size: 14px;
      margin-bottom: 16px;
    }

    .el-form-item {
      margin-bottom: 16px;
    }

    .el-form-item__label {
      font-size: 14px;
    }

    /* 移动端单列布局 */
    .el-row .el-col {
      width: 100% !important;
      margin-bottom: 8px;
    }

    .el-radio-group .el-radio {
      margin-right: 16px;
      margin-bottom: 8px;
    }
  }
}

/* 平板设备优化 */
@media (max-width: 1024px) and (min-width: 769px) {
  .supplier-dialog {
    width: 90% !important;
  }

  .supplier-form {
    .form-section {
      padding: 20px;
    }
  }
}
</style>
