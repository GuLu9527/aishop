<template>
  <div class="suppliers-container">
    <!-- iOS风格页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <div class="header-title">
            <el-icon class="header-icon"><OfficeBuilding /></el-icon>
            <h2>供应商管理中心</h2>
          </div>
          <p class="header-desc">统一管理供应商信息，优化供应链合作关系</p>
        </div>
        <div class="header-right">
          <el-button type="primary" @click="getSupplierList" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
          <el-button @click="handleExport" :loading="exportLoading">
            <el-icon><Download /></el-icon>
            导出数据
          </el-button>
        </div>
      </div>
    </div>

    <!-- iOS风格统计卡片 -->
    <div class="stats-section">
      <div class="header-stats">
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><OfficeBuilding /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ supplierStats.totalCount || 0 }}</div>
            <div class="stat-label">供应商总数</div>
          </div>
          <div class="stat-trend up">
            <el-icon><TrendCharts /></el-icon>
            总览
          </div>
        </div>
        <div class="stat-card success">
          <div class="stat-icon">
            <el-icon><Check /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ supplierStats.activeCount || 0 }}</div>
            <div class="stat-label">正常供应</div>
          </div>
          <div class="stat-trend up">
            <el-icon><ArrowUp /></el-icon>
            +5%
          </div>
        </div>
        <div class="stat-card warning">
          <div class="stat-icon">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ supplierStats.inactiveCount || 0 }}</div>
            <div class="stat-label">暂停合作</div>
          </div>
          <div class="stat-trend down">
            <el-icon><ArrowDown /></el-icon>
            -2%
          </div>
        </div>
      </div>
    </div>

    <!-- iOS风格搜索区域 -->
    <div class="search-section">
      <div class="section-header">
        <h3 class="section-title">
          <el-icon><Search /></el-icon>
          搜索筛选
        </h3>
      </div>
      <div class="search-card">
        <el-form :model="searchForm" class="search-form">
          <div class="search-row">
            <div class="search-item">
              <label class="search-label">供应商名称</label>
              <el-input
                v-model="searchForm.supplierName"
                placeholder="搜索供应商名称"
                clearable
                size="large"
              >
                <template #prefix>
                  <el-icon><OfficeBuilding /></el-icon>
                </template>
              </el-input>
            </div>
            <div class="search-item">
              <label class="search-label">联系人</label>
              <el-input
                v-model="searchForm.contactPerson"
                placeholder="搜索联系人"
                clearable
                size="large"
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </div>
            <div class="search-item">
              <label class="search-label">信用等级</label>
              <el-select v-model="searchForm.creditRating" placeholder="选择信用等级" clearable size="large">
                <el-option label="全部" value="" />
                <el-option label="1星" :value="1" />
                <el-option label="2星" :value="2" />
                <el-option label="3星" :value="3" />
                <el-option label="4星" :value="4" />
                <el-option label="5星" :value="5" />
              </el-select>
            </div>
            <div class="search-item">
              <label class="search-label">状态</label>
              <el-select v-model="searchForm.status" placeholder="选择状态" clearable size="large">
                <el-option label="全部" value="" />
                <el-option label="正常" :value="1" />
                <el-option label="停用" :value="0" />
              </el-select>
            </div>
          </div>
          <div class="search-actions">
            <el-button type="primary" @click="getSupplierList" size="large">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetSearch" size="large">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </div>
        </el-form>
      </div>
    </div>

    <!-- iOS风格操作区域 -->
    <div class="action-section">
      <div class="section-header">
        <h3 class="section-title">
          <el-icon><Setting /></el-icon>
          快捷操作
        </h3>
      </div>
      <div class="action-cards">
        <div class="action-card primary" @click="showAddDialog">
          <div class="action-icon">
            <el-icon><Plus /></el-icon>
          </div>
          <div class="action-content">
            <div class="action-title">添加供应商</div>
            <div class="action-desc">新增供应商信息</div>
          </div>
          <el-icon class="action-arrow"><ArrowRight /></el-icon>
        </div>
        <div 
          class="action-card danger" 
          :class="{ disabled: selectedSuppliers.length === 0 }"
          @click="selectedSuppliers.length > 0 && batchDeleteConfirm()"
        >
          <div class="action-icon">
            <el-icon><Delete /></el-icon>
          </div>
          <div class="action-content">
            <div class="action-title">批量删除</div>
            <div class="action-desc">已选中 {{ selectedSuppliers.length }} 项</div>
          </div>
          <el-icon class="action-arrow"><ArrowRight /></el-icon>
        </div>
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
          <el-table-column prop="supplierName" label="供货商名称" width="260">
            <template #default="{ row }">
              <div class="supplier-name">
                <span class="name">{{ row.supplierName }}</span>
                <el-tag v-if="row.status === 0" type="danger" size="small">已停用</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="contactPerson" label="联系人" width="100" />
          <el-table-column prop="phone" label="联系电话" width="200" />
          <el-table-column prop="creditRating" label="信用等级" width="200">
            <template #default="{ row }">
              <div class="credit-rating-display">
                <el-rate
                  v-model="row.creditRating"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value}星"
                  size="small"
                />
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="deliveryCycle" label="供货周期" width="130">
            <template #default="{ row }">
              <span v-if="row.deliveryCycle">{{ row.deliveryCycle }}天</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="productCount" label="合作商品" width="130">
            <template #default="{ row }">
              <el-tag type="info" size="small">{{ row.productCount || 0 }}个</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="200">
            <template #default="{ row }">
              <div class="enhanced-date-cell">
                <div class="date-text">{{ formatDateTime(row.createTime).split(' ')[0] }}</div>
                <div class="time-text">{{ formatDateTime(row.createTime).split(' ')[1] }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <div class="operation-buttons">
                <el-tooltip content="查看详情" placement="top">
                  <el-button size="small" type="primary" circle @click="viewSupplier(row)" class="op-btn">
                    <el-icon><View /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="编辑供货商" placement="top">
                  <el-button size="small" type="success" circle @click="editSupplier(row)" class="op-btn">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip :content="row.status === 1 ? '停用供货商' : '启用供货商'" placement="top">
                  <el-button 
                    size="small" 
                    :type="row.status === 1 ? 'warning' : 'success'" 
                    circle 
                    @click="toggleStatus(row)"
                    class="op-btn"
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
                    class="op-btn"
                  >
                    <el-icon><Delete /></el-icon>
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
  User, Phone, Message, Setting, Check, Close, Download,
  OfficeBuilding, TrendCharts, ArrowUp, ArrowDown, Warning,
  ArrowRight
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

.suppliers-container {
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

  .header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .header-left {
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
  }

  .header-right {
    display: flex;
    gap: 16px;
    
    .el-button {
      height: 44px;
      border-radius: 12px;
      font-size: 16px;
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
}

/* iOS风格统计卡片区域 */
.stats-section {
  margin-bottom: 32px;

  .header-stats {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 20px;
  }
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 18px;
  padding: 28px;
  display: flex;
  align-items: center;
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

  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 16px;
    background: linear-gradient(135deg, var(--ios-accent) 0%, var(--ios-secondary) 100%);
    color: var(--ios-white);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 20px;
    font-size: 24px;
    box-shadow: 0 4px 12px rgba(28, 28, 30, 0.25);
    transition: all 0.25s cubic-bezier(0.4, 0.0, 0.2, 1);
  }

  .stat-content {
    flex: 1;

    .stat-number {
      font-size: 36px;
      font-weight: 700;
      color: var(--ios-label);
      margin-bottom: 8px;
      letter-spacing: -0.8px;
      line-height: 1.1;
    }

    .stat-label {
      font-size: 16px;
      color: var(--ios-secondary-label);
      font-weight: 500;
      opacity: 0.8;
    }
  }

  .stat-trend {
    font-size: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    font-weight: 500;
    padding: 6px 12px;
    border-radius: 12px;
    margin-left: 12px;

    &.up {
      color: #1C1C1E;
      background: rgba(52, 199, 89, 0.15);
      border: 1px solid rgba(52, 199, 89, 0.2);
      
      .el-icon {
        color: #34C759;
      }
    }

    &.down {
      color: #1C1C1E;
      background: rgba(255, 59, 48, 0.15);
      border: 1px solid rgba(255, 59, 48, 0.2);
      
      .el-icon {
        color: #FF3B30;
      }
    }
  }

  &.success {
    .stat-icon {
      background: linear-gradient(135deg, #34C759 0%, #30B753 100%);
    }
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, #34C759, #30B753);
      border-radius: 18px 18px 0 0;
    }
  }

  &.warning {
    .stat-icon {
      background: linear-gradient(135deg, #FF9500 0%, #E6850E 100%);
    }
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, #FF9500, #E6850E);
      border-radius: 18px 18px 0 0;
    }
  }
}

/* iOS风格搜索区域 */
.search-section {
  margin-bottom: 32px;

  .section-header {
    margin-bottom: 24px;

    .section-title {
      display: flex;
      align-items: center;
      margin: 0;
      font-size: 24px;
      font-weight: 700;
      color: var(--ios-label);
      letter-spacing: -0.4px;

      .el-icon {
        margin-right: 12px;
        color: var(--ios-accent);
        font-size: 26px;
        opacity: 0.9;
      }
    }
  }

  .search-card {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: saturate(180%) blur(20px);
    border-radius: 18px;
    padding: 28px;
    border: 1px solid rgba(255, 255, 255, 0.18);
    box-shadow: 
      0 4px 16px rgba(0, 0, 0, 0.06),
      0 2px 8px rgba(0, 0, 0, 0.04);
  }

  .search-form {
    .search-row {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 24px;
      margin-bottom: 24px;
    }

    .search-item {
      .search-label {
        display: block;
        margin-bottom: 8px;
        font-weight: 600;
        color: var(--ios-label);
        font-size: 16px;
      }

      :deep(.el-input),
      :deep(.el-select) {
        .el-input__wrapper {
          border-radius: 12px;
          background: rgba(255, 255, 255, 0.8);
          border: 1px solid rgba(199, 199, 204, 0.3);
          transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
          
          &:hover {
            background: rgba(255, 255, 255, 0.9);
            border-color: var(--ios-accent);
          }
          
          &.is-focus {
            background: rgba(255, 255, 255, 1);
            border-color: var(--ios-accent);
            box-shadow: 0 0 0 3px rgba(28, 28, 30, 0.1);
          }
        }
      }
    }

    .search-actions {
      display: flex;
      gap: 16px;
      justify-content: center;
    }
  }
}

/* iOS风格操作区域 */
.action-section {
  margin-bottom: 32px;

  .section-header {
    margin-bottom: 24px;

    .section-title {
      display: flex;
      align-items: center;
      margin: 0;
      font-size: 24px;
      font-weight: 700;
      color: var(--ios-label);
      letter-spacing: -0.4px;

      .el-icon {
        margin-right: 12px;
        color: var(--ios-accent);
        font-size: 26px;
        opacity: 0.9;
      }
    }
  }

  .action-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 20px;
  }
}

.action-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 18px;
  padding: 24px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0.0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);

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

  &.disabled {
    opacity: 0.5;
    cursor: not-allowed;
    transform: none !important;

    &:hover {
      transform: none !important;
      box-shadow: 
        0 4px 16px rgba(0, 0, 0, 0.06),
        0 2px 8px rgba(0, 0, 0, 0.04);
    }
  }

  .action-icon {
    width: 56px;
    height: 56px;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 20px;
    font-size: 24px;
    box-shadow: 0 4px 12px rgba(28, 28, 30, 0.25);
    transition: all 0.25s cubic-bezier(0.4, 0.0, 0.2, 1);
  }

  .action-content {
    flex: 1;

    .action-title {
      font-size: 18px;
      font-weight: 600;
      color: var(--ios-label);
      margin-bottom: 6px;
      letter-spacing: -0.2px;
    }

    .action-desc {
      font-size: 15px;
      color: var(--ios-secondary-label);
      opacity: 0.8;
      font-weight: 400;
    }
  }

  .action-arrow {
    color: var(--ios-tertiary-label);
    transition: all 0.25s cubic-bezier(0.4, 0.0, 0.2, 1);
    font-size: 18px;
  }

  &.primary {
    .action-icon {
      background: linear-gradient(135deg, var(--ios-accent) 0%, var(--ios-secondary) 100%);
      color: var(--ios-white);
    }
  }

  &.danger {
    .action-icon {
      background: linear-gradient(135deg, rgba(255, 59, 48, 0.9) 0%, rgba(255, 107, 90, 0.9) 100%);
      color: var(--ios-white);
    }
  }

  &:hover:not(.disabled) {
    .action-icon {
      transform: scale(1.05);
      box-shadow: 0 6px 16px rgba(28, 28, 30, 0.35);
    }
    
    .action-arrow {
      color: var(--ios-accent);
      transform: translateX(6px);
    }
  }
}

.table-section {
  :deep(.el-card) {
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
}

.supplier-name {
  display: flex;
  align-items: center;
  gap: 12px;

  .name {
    font-weight: 600;
    color: var(--ios-label);
    font-size: 15px;
    letter-spacing: -0.1px;
  }
}

/* 增强的日期时间显示 */
.enhanced-date-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 12px 8px;

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
    display: inline-block;
    width: fit-content;
  }
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 24px 0;
}

.dialog-footer {
  text-align: right;
  
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

/* iOS风格供货商对话框样式 */
:deep(.supplier-dialog) {
  .el-dialog {
    border-radius: 20px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: saturate(180%) blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.18);
    box-shadow: 
      0 8px 32px rgba(0, 0, 0, 0.12),
      0 4px 16px rgba(0, 0, 0, 0.08);
  }
  
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
    max-height: 70vh;
    overflow-y: auto;
    
    // 自定义滚动条
    &::-webkit-scrollbar {
      width: 4px;
    }
    
    &::-webkit-scrollbar-thumb {
      background: var(--ios-separator);
      border-radius: 2px;
    }
  }
}

/* iOS风格供货商表单样式 */
.supplier-form {
  .form-section {
    margin-bottom: 32px;
    padding: 28px;
    background: rgba(28, 28, 30, 0.04);
    border-radius: 16px;
    border: 1px solid rgba(199, 199, 204, 0.3);
    backdrop-filter: saturate(180%) blur(10px);
  }

  .section-title {
    display: flex;
    align-items: center;
    margin-bottom: 24px;
    font-size: 18px;
    font-weight: 700;
    color: var(--ios-label);
    letter-spacing: -0.3px;

    .el-icon {
      margin-right: 12px;
      color: var(--ios-accent);
      font-size: 20px;
      opacity: 0.9;
    }
  }

  :deep(.el-form-item) {
    margin-bottom: 24px;

    .el-form-item__label {
      font-weight: 600;
      color: var(--ios-label);
      font-size: 16px;
    }
  }

  :deep(.el-input),
  :deep(.el-textarea),
  :deep(.el-select),
  :deep(.el-input-number) {
    .el-input__wrapper,
    .el-textarea__inner {
      border-radius: 12px;
      background: rgba(255, 255, 255, 0.8);
      border: 1px solid rgba(199, 199, 204, 0.3);
      transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
      
      &:hover {
        background: rgba(255, 255, 255, 0.9);
        border-color: var(--ios-accent);
      }
      
      &.is-focus {
        background: rgba(255, 255, 255, 1);
        border-color: var(--ios-accent);
        box-shadow: 0 0 0 3px rgba(28, 28, 30, 0.1);
      }
    }
  }

  .credit-rating-container {
    padding: 12px 0;

    :deep(.el-rate) {
      display: flex;
      align-items: center;
      
      .el-rate__item {
        margin-right: 8px;
      }
    }

    :deep(.el-rate__text) {
      margin-left: 16px;
      font-weight: 600;
      color: var(--ios-label);
      font-size: 16px;
    }
  }

  :deep(.el-radio-group) {
    .el-radio {
      margin-right: 32px;
      
      .el-radio__label {
        display: flex;
        align-items: center;
        color: var(--ios-label);
        font-weight: 500;
        font-size: 16px;

        .el-icon {
          margin-right: 8px;
        }
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

  :deep(.el-input-number) {
    width: 100%;
    
    .el-input-number__increase,
    .el-input-number__decrease {
      border-radius: 0;
      background: rgba(28, 28, 30, 0.06);
      border-color: rgba(199, 199, 204, 0.3);
      
      &:hover {
        background: rgba(28, 28, 30, 0.1);
        color: var(--ios-accent);
      }
    }
  }
}

/* iOS风格表单验证错误样式 */
.supplier-form :deep(.el-form-item.is-error) {
  .el-input__wrapper,
  .el-textarea__inner {
    border-color: #FF3B30;
    background: rgba(255, 59, 48, 0.08);
  }

  .el-form-item__error {
    font-size: 14px;
    margin-top: 8px;
    color: #FF3B30;
    font-weight: 500;
  }
}

/* iOS风格Element Plus组件覆盖 */
:deep(.el-form) {
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
    
    &--danger {
      background: rgba(255, 59, 48, 0.1);
      color: #FF3B30;
      border: 1px solid rgba(255, 59, 48, 0.2);
      box-shadow: none;
      
      &:hover {
        background: rgba(255, 59, 48, 0.15);
        border-color: #FF3B30;
        transform: translateY(-1px);
      }
      
      &:disabled {
        opacity: 0.5;
        transform: none;
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
    padding: 8px;
    font-size: 16px;
    border-radius: 50%;
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

/* iOS风格操作按钮组 */
.operation-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 8px 4px;
  
  .op-btn {
    width: 36px !important;
    height: 36px !important;
    border-radius: 12px !important;
    transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1) !important;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12) !important;
    
    &:hover {
      transform: translateY(-1px) scale(1.05) !important;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.18) !important;
    }
    
    &:active {
      transform: scale(0.95) !important;
    }
    
    .el-icon {
      font-size: 18px !important;
    }
    
    // 不同类型按钮的个性化样式
    &.el-button--primary {
      background: linear-gradient(135deg, #007AFF 0%, #0051D5 100%) !important;
      border: none !important;
      
      &:hover {
        background: linear-gradient(135deg, #0051D5 0%, #003F99 100%) !important;
      }
    }
    
    &.el-button--success {
      background: linear-gradient(135deg, #34C759 0%, #30B753 100%) !important;
      border: none !important;
      
      &:hover {
        background: linear-gradient(135deg, #30B753 0%, #2CA44D 100%) !important;
      }
    }
    
    &.el-button--warning {
      background: linear-gradient(135deg, #FF9500 0%, #E6850E 100%) !important;
      border: none !important;
      
      &:hover {
        background: linear-gradient(135deg, #E6850E 0%, #CC7A0C 100%) !important;
      }
    }
    
    &.el-button--danger {
      background: linear-gradient(135deg, #FF3B30 0%, #E6342A 100%) !important;
      border: none !important;
      
      &:hover {
        background: linear-gradient(135deg, #E6342A 0%, #CC2E24 100%) !important;
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
  
  &.el-tag--info {
    background: rgba(28, 28, 30, 0.15);
    color: var(--ios-accent);
  }
  
  &.el-tag--danger {
    background: rgba(255, 59, 48, 0.15);
    color: #FF3B30;
  }
}

:deep(.el-rate) {
  .el-rate__item {
    margin-right: 4px;
  }
}

/* 表格中信用等级显示优化 */
.credit-rating-display {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px 0;
  
  :deep(.el-rate) {
    display: flex;
    align-items: center;
    flex-wrap: nowrap;
    
    .el-rate__item {
      margin-right: 2px;
      flex-shrink: 0;
    }
    
    .el-rate__text {
      margin-left: 6px;
      font-size: 12px;
      font-weight: 600;
      color: var(--ios-secondary-label);
      white-space: nowrap;
      flex-shrink: 0;
    }
    
    // 小尺寸星星样式
    &.el-rate--small {
      .el-rate__item {
        font-size: 14px;
        margin-right: 1px;
      }
      
      .el-rate__text {
        font-size: 11px;
        margin-left: 4px;
      }
    }
  }
}

:deep(.el-tooltip__popper) {
  background: rgba(28, 28, 30, 0.9);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 8px;
  border: none;
  
  &.is-dark {
    background: rgba(28, 28, 30, 0.9);
    
    .el-tooltip__arrow::before {
      background: rgba(28, 28, 30, 0.9);
      border: none;
    }
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
  .suppliers-container {
    padding: 20px;
  }

  .page-header {
    flex-direction: column;
    gap: 24px;
    text-align: center;
    
    .header-stats {
      justify-content: center;
      flex-wrap: wrap;
      gap: 16px;
    }
  }
  
  .stat-card {
    min-width: 100px;
    padding: 20px;
    
    .stat-number {
      font-size: 28px;
    }
    
    .stat-label {
      font-size: 13px;
    }
  }

  .action-section {
    flex-direction: column;
    gap: 16px;

    .action-left {
      width: 100%;
      justify-content: center;
      flex-wrap: wrap;
    }
  }
}

@media (max-width: 768px) {
  .suppliers-container {
    padding: 16px;
  }

  .page-header {
    padding: 20px;
    
    .header-left {
      h2 {
        font-size: 24px;
      }
      
      p {
        font-size: 14px;
      }
    }
    
    .header-stats {
      gap: 12px;
      
      .stat-card {
        min-width: 80px;
        padding: 16px;
        
        .stat-number {
          font-size: 24px;
        }
        
        .stat-label {
          font-size: 12px;
        }
      }
    }
  }

  :deep(.search-section .el-card),
  :deep(.table-section .el-card) {
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
      .el-input {
        width: 100%;
      }
    }
  }

  .action-left {
    gap: 12px;
    
    .el-button {
      flex: 1;
      min-width: 0;
    }
  }

  /* 移动端对话框优化 */
  :deep(.supplier-dialog) {
    .el-dialog {
      width: 95% !important;
      margin: 0 auto;
    }
    
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
      font-size: 16px;
      margin-bottom: 16px;
    }

    :deep(.el-form-item) {
      margin-bottom: 16px;
      
      .el-form-item__label {
        font-size: 14px;
      }
    }

    /* 移动端单列布局 */
    :deep(.el-row .el-col) {
      width: 100% !important;
      margin-bottom: 8px;
    }

    :deep(.el-radio-group .el-radio) {
      margin-right: 16px;
      margin-bottom: 8px;
      
      .el-radio__label {
        font-size: 14px;
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
      width: 28px;
      height: 28px;
      font-size: 14px;
    }
  }
  
  // 移动端操作按钮优化
  .operation-buttons {
    gap: 6px;
    padding: 6px 2px;
    
    .op-btn {
      width: 32px !important;
      height: 32px !important;
      border-radius: 10px !important;
      box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1) !important;
      
      .el-icon {
        font-size: 16px !important;
      }
      
      &:hover {
        transform: scale(1.05) !important;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15) !important;
      }
    }
  }
  
  // 移动端信用等级显示优化
  .credit-rating-display {
    :deep(.el-rate) {
      .el-rate__item {
        font-size: 12px;
        margin-right: 1px;
      }
      
      .el-rate__text {
        font-size: 10px;
        margin-left: 3px;
      }
    }
  }
}

@media (max-width: 480px) {
  .suppliers-container {
    padding: 12px;
  }

  .page-header {
    padding: 16px;
    
    .header-left h2 {
      font-size: 20px;
    }
    
    .header-stats {
      grid-template-columns: 1fr;
      
      .stat-card {
        width: 100%;
      }
    }
  }

  :deep(.search-section .el-card),
  :deep(.table-section .el-card) {
    .el-card__body {
      padding: 12px;
    }
  }

  .supplier-form {
    .form-section {
      padding: 12px;
    }
  }
}

/* 平板设备优化 */
@media (max-width: 1024px) and (min-width: 769px) {
  :deep(.supplier-dialog) {
    .el-dialog {
      width: 90% !important;
    }
  }

  .supplier-form {
    .form-section {
      padding: 24px;
    }
  }
}
</style>
