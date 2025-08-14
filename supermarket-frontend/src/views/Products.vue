<template>
  <div class="products-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <div class="header-title">
            <el-icon class="header-icon"><Box /></el-icon>
            <h2>商品管理</h2>
          </div>
          <p class="header-desc">管理超市商品信息，包括添加、编辑、删除和库存管理</p>
        </div>
        <div class="header-right">
          <div class="header-stats">
            <div class="stat-card">
              <div class="stat-number">{{ productList.length }}</div>
              <div class="stat-label">商品总数</div>
            </div>
            <div class="stat-card">
              <div class="stat-number">{{ productList.filter(p => p.status === 1).length }}</div>
              <div class="stat-label">在售商品</div>
            </div>
            <div class="stat-card">
              <div class="stat-number">{{ productList.filter(p => p.status === 0).length }}</div>
              <div class="stat-label">已下架</div>
            </div>
          </div>
          <el-button type="primary" size="large" @click="showAddDialog" class="add-btn">
            <el-icon><Plus /></el-icon>
            添加商品
          </el-button>
        </div>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-card">
        <div class="search-header">
          <el-icon class="search-icon"><Search /></el-icon>
          <span class="search-title">商品筛选</span>
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
        <el-form-item label="商品状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="全部" value="" />
            <el-option label="在售" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        
        <!-- 新增价格区间筛选 -->
        <el-form-item label="价格区间">
          <div style="display: flex; align-items: center; gap: 8px;">
            <el-input-number
              v-model="searchForm.minPrice"
              placeholder="最低价"
              :min="0"
              :precision="2"
              :step="1"
              controls-position="right"
              style="width: 120px"
            />
            <span style="color: #909399;">-</span>
            <el-input-number
              v-model="searchForm.maxPrice"
              placeholder="最高价"
              :min="0"
              :precision="2"
              :step="1"
              controls-position="right"
              style="width: 120px"
            />
          </div>
        </el-form-item>
        
        <!-- 新增库存状态筛选 -->
        <el-form-item label="库存状态">
          <el-select
            v-model="searchForm.stockStatus"
            placeholder="请选择库存状态"
            clearable
            style="width: 140px"
          >
            <el-option label="低库存" value="low" />
            <el-option label="正常库存" value="normal" />
            <el-option label="高库存" value="high" />
          </el-select>
        </el-form-item>
        
        <!-- 新增过期状态筛选 -->
        <el-form-item label="过期状态">
          <el-select
            v-model="searchForm.expiryStatus"
            placeholder="请选择过期状态"
            clearable
            style="width: 140px"
          >
            <el-option label="已过期" value="expired" />
            <el-option label="临期商品" value="expiring" />
            <el-option label="正常商品" value="normal" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="searchProducts">
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

    <!-- 批量操作工具栏 -->
    <div class="batch-toolbar" v-if="selectedProducts.length > 0">
      <div class="batch-info">
        <span>已选择 {{ selectedProducts.length }} 个商品</span>
      </div>
      <div class="batch-actions">
        <el-button type="success" @click="showBatchStatusDialog">
          <el-icon><Switch /></el-icon>
          批量上下架
        </el-button>
        <el-button type="primary" @click="showBatchCategoryDialog">
          <el-icon><Collection /></el-icon>
          批量分类
        </el-button>
        <el-button type="warning" @click="showBatchPriceDialog">
          <el-icon><Money /></el-icon>
          批量价格
        </el-button>
        <el-button type="danger" @click="showBatchDeleteDialog">
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
        <el-button type="info" @click="showBatchProductionDateDialog">
          <el-icon><Calendar /></el-icon>
          生产日期
        </el-button>

        <el-button @click="clearSelection">
          <el-icon><Close /></el-icon>
          取消选择
        </el-button>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="table-section">
      <div class="table-card">
        <div class="table-header">
          <div class="table-title">
            <el-icon class="table-icon"><List /></el-icon>
            <span>商品列表</span>
          </div>
          <div class="table-actions">

            <el-button size="small" @click="getProductList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
        <el-table
          ref="productTableRef"
          :data="productList"
          v-loading="loading"
          stripe
          style="width: 100%"
          class="modern-table"
          @selection-change="handleSelectionChange"
        >

        <el-table-column type="selection" width="55" />
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
        <el-table-column prop="brand" label="品牌" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="sellingPrice" label="售价" width="100">
          <template #default="{ row }">
            ¥{{ row.sellingPrice?.toFixed(2) || '0.00' }}
          </template>
        </el-table-column>

        <el-table-column prop="productionDate" label="生产日期" width="110">
          <template #default="{ row }">
            <span v-if="row.productionDate">{{ row.productionDate }}</span>
            <el-tag v-else type="info" size="small">未设置</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="过期状态" width="100">
          <template #default="{ row }">
            <div class="status-tags">
              <el-tag v-if="getExpirationStatus(row) === 'expired'" type="danger" size="small">
                已过期
              </el-tag>
              <el-tag v-else-if="getExpirationStatus(row) === 'expiring'" type="warning" size="small">
                临期
              </el-tag>
              <el-tag v-else-if="getExpirationStatus(row) === 'normal'" type="success" size="small">
                正常
              </el-tag>
              <el-tag v-else type="info" size="small">
                无日期
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="stockQuantity" label="当前库存" width="80">
          <template #default="{ row }">
            <span class="stock-number" :class="{ 'low-stock': row.isLowStock }">
              {{ row.stockQuantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '在售' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-tooltip content="查看商品详情" placement="top">
                <el-button size="small" circle @click="viewProduct(row)">
                  <el-icon><View /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="编辑商品" placement="top">
                <el-button size="small" type="primary" circle @click="editProduct(row)">
                  <el-icon><Edit /></el-icon>
                </el-button>
              </el-tooltip>

              <el-tooltip :content="row.status === 1 ? '下架商品' : '上架商品'" placement="top">
                <el-button
                  size="small"
                  :type="row.status === 1 ? 'warning' : 'success'"
                  circle
                  @click="toggleStatus(row)"
                >
                  <el-icon>
                    <Top v-if="row.status === 0" />
                    <Bottom v-else />
                  </el-icon>
                </el-button>
              </el-tooltip>
              
              <el-tooltip content="删除商品" placement="top">
                <el-button
                  size="small"
                  type="danger"
                  circle
                  @click="deleteProduct(row)"
                >
                  <el-icon><Delete /></el-icon>
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
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
      </div>
    </div>

    <!-- 添加/编辑商品对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="resetForm"
    >
      <el-form
        ref="productFormRef"
        :model="productForm"
        :rules="formRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="productName">
              <el-input
                v-model="productForm.productName"
                placeholder="请输入商品名称"
                :readonly="!isEdit"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品条码" prop="barcode">
              <el-input
                v-model="productForm.barcode"
                placeholder="请输入商品条码"
                :readonly="!isEdit"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品分类" prop="categoryId">
              <el-select
                v-model="productForm.categoryId"
                placeholder="请选择分类"
                style="width: 100%"
                :disabled="!isEdit"
              >
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.categoryName"
                  :value="category.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌" prop="brand">
              <el-input
                v-model="productForm.brand"
                placeholder="请输入品牌"
                :readonly="!isEdit"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-select
                v-model="productForm.unit"
                placeholder="请选择单位"
                style="width: 100%"
                :disabled="!isEdit"
              >
                <el-option label="个" value="个" />
                <el-option label="袋" value="袋" />
                <el-option label="包" value="包" />
                <el-option label="盒" value="盒" />
                <el-option label="瓶" value="瓶" />
                <el-option label="罐" value="罐" />
                <el-option label="桶" value="桶" />
                <el-option label="箱" value="箱" />
                <el-option label="斤" value="斤" />
                <el-option label="公斤" value="公斤" />
                <el-option label="克" value="克" />
                <el-option label="升" value="升" />
                <el-option label="毫升" value="毫升" />
                <el-option label="支" value="支" />
                <el-option label="条" value="条" />
                <el-option label="张" value="张" />
                <el-option label="片" value="片" />
                <el-option label="块" value="块" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格" prop="specification">
              <el-select
                v-model="productForm.specification"
                placeholder="请选择规格"
                style="width: 100%"
                filterable
                allow-create
                :disabled="!isEdit"
              >
                <el-option label="小包装" value="小包装" />
                <el-option label="中包装" value="中包装" />
                <el-option label="大包装" value="大包装" />
                <el-option label="家庭装" value="家庭装" />
                <el-option label="经济装" value="经济装" />
                <el-option label="50g" value="50g" />
                <el-option label="100g" value="100g" />
                <el-option label="200g" value="200g" />
                <el-option label="250g" value="250g" />
                <el-option label="500g" value="500g" />
                <el-option label="1kg" value="1kg" />
                <el-option label="250ml" value="250ml" />
                <el-option label="330ml" value="330ml" />
                <el-option label="500ml" value="500ml" />
                <el-option label="1L" value="1L" />
                <el-option label="1.5L" value="1.5L" />
                <el-option label="2L" value="2L" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="进货价" prop="purchasePrice">
              <el-input-number
                v-model="productForm.purchasePrice"
                :precision="2"
                :min="0"
                style="width: 100%"
                :disabled="!isEdit"
                @change="validatePrices"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="销售价" prop="sellingPrice">
              <el-input-number
                v-model="productForm.sellingPrice"
                :precision="2"
                :min="0"
                style="width: 100%"
                :disabled="!isEdit"
                @change="validatePrices"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="库存信息">
              <div class="stock-readonly">
                <span>当前库存：{{ productForm.stockQuantity || 0 }}</span>
                <span style="margin-left: 12px; color: #909399; font-size: 12px;">
                  库存管理请前往"库存管理"页面
                </span>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="保质期(天)" prop="shelfLifeDays">
              <el-input-number
                v-model="productForm.shelfLifeDays"
                :min="1"
                style="width: 100%"
                :disabled="!isEdit"
                @change="validateProductionDateAndShelfLife"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生产日期" prop="productionDate">
              <el-date-picker
                v-model="productForm.productionDate"
                type="date"
                placeholder="请选择生产日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
                :disabled="!isEdit"
                :disabled-date="disabledDate"
                @change="validateProductionDateAndShelfLife"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生产厂家" prop="manufacturer">
              <el-input
                v-model="productForm.manufacturer"
                placeholder="请输入生产厂家"
                :readonly="!isEdit"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- 预留空间或其他字段 -->
          </el-col>
        </el-row>

        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入商品描述"
            :readonly="!isEdit"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">
            {{ isEdit ? '取消' : '关闭' }}
          </el-button>
          <el-button
            v-if="isEdit"
            type="primary"
            @click="submitForm"
            :loading="submitting"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量操作对话框 -->
    <el-dialog
      v-model="batchDialogVisible"
      :title="batchDialogTitle"
      width="500px"
    >
      <el-form :model="batchForm" label-width="120px">
        <!-- 批量状态更新 -->
        <el-form-item v-if="batchOperationType === 'UPDATE_STATUS'" label="商品状态">
          <el-radio-group v-model="batchForm.statusValue">
            <el-radio :value="1">
              <el-icon style="margin-right: 4px; color: #67c23a;"><Top /></el-icon>
              上架
            </el-radio>
            <el-radio :value="0">
              <el-icon style="margin-right: 4px; color: #f56c6c;"><Bottom /></el-icon>
              下架
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 批量分类更新 -->
        <el-form-item v-if="batchOperationType === 'UPDATE_CATEGORY'" label="新分类">
          <el-select v-model="batchForm.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.categoryName"
              :value="category.id"
            />
          </el-select>
        </el-form-item>

        <!-- 批量价格更新 -->
        <template v-if="batchOperationType === 'UPDATE_PRICE'">
          <el-form-item label="调整方式">
            <el-radio-group v-model="batchForm.priceAdjustmentType">
              <el-radio value="FIXED">固定价格</el-radio>
              <el-radio value="PERCENTAGE">百分比调整</el-radio>
              <el-radio value="AMOUNT">金额调整</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="调整值">
            <el-input-number
              v-model="batchForm.priceValue"
              :min="0"
              :precision="2"
              style="width: 100%"
            />
            <div class="form-tip">
              <span v-if="batchForm.priceAdjustmentType === 'FIXED'">设置为固定价格</span>
              <span v-else-if="batchForm.priceAdjustmentType === 'PERCENTAGE'">调整百分比（如：10表示增加10%）</span>
              <span v-else>调整金额（正数增加，负数减少）</span>
            </div>
          </el-form-item>
        </template>

        <!-- 批量设置生产日期 -->
        <el-form-item v-if="batchOperationType === 'SET_PRODUCTION_DATE'" label="生产日期">
          <el-date-picker
            v-model="batchForm.productionDate"
            type="date"
            placeholder="请选择生产日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
            :disabled-date="disabledDate"
          />
          <div class="form-tip">
            <span style="color: #f56c6c;">注意：生产日期不能是未来日期</span>
          </div>
        </el-form-item>

        <!-- 批量删除确认 -->
        <el-form-item v-if="batchOperationType === 'DELETE'" label="删除确认">
          <div class="delete-warning">
            <el-alert
              title="危险操作警告"
              type="error"
              description="您即将删除选中的商品，此操作不可恢复！请确认您的操作。"
              show-icon
              :closable="false"
            />
          </div>
        </el-form-item>

        <!-- 操作原因 -->
        <el-form-item v-if="batchOperationType !== 'UPDATE_STATUS'" label="操作原因">
          <el-input
            v-model="batchForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入操作原因（可选）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="batchDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="executeBatchOperation"
            :disabled="selectedProducts.length === 0"
          >
            确定执行 ({{ selectedProducts.length }} 个商品)
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
// 路由功能已移除，库存管理使用专门页面
import {
  Plus, Search, Refresh, Box, List, Edit, Delete,
  Switch, Collection, Money, Close, Top, Bottom, View, Calendar
} from '@element-plus/icons-vue'
import {
  getProductPage,
  addProduct,
  updateProduct,
  deleteProduct as deleteProductApi,
  batchDeleteProducts,
  updateProductStatus,
  batchOperation,
  batchUpdateStatus,
  batchUpdateCategory,
  batchSetProductionDate
} from '@/api/product'
import { getAllCategories } from '@/api/category'
import type { Product, ProductForm } from '@/types/api'
import type {
  ProductBatchOperationDTO,
  ProductBatchOperationResultDTO,
  OperationType,
  PriceAdjustmentType
} from '@/types/product'
import { formatDate, disableFutureDate, formatForBackend } from '@/utils/dateUtils'

// 路由功能已移除，专注商品管理核心功能

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加商品')
const isEdit = ref(false)
const productFormRef = ref<FormInstance>()
const productTableRef = ref()

// 商品列表
const productList = ref<Product[]>([])
const categories = ref<any[]>([])

// 搜索表单
const searchForm = reactive({
  productName: '',
  barcode: '',
  categoryId: null as number | null,
  status: null as number | null,
  minPrice: null as number | null,
  maxPrice: null as number | null,
  stockStatus: null as string | null, // 'low', 'normal', 'high'
  expiryStatus: null as string | null  // 'expired', 'expiring', 'normal'
})



// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 商品表单
const productForm = reactive<ProductForm>({
  id: null,
  productName: '',
  barcode: '',
  categoryId: null,
  brand: '',
  unit: '',
  specification: '',
  purchasePrice: 0,
  sellingPrice: 0,
  stockQuantity: 0,
  minStock: 0,
  maxStock: 0,
  shelfLifeDays: null,
  productionDate: null,
  manufacturer: '',
  description: ''
})

// 批量操作相关数据
const selectedProducts = ref<Product[]>([])
const batchDialogVisible = ref(false)
const batchDialogTitle = ref('')
const batchOperationType = ref<OperationType>()
const batchForm = reactive({
  statusValue: 1,
  categoryId: null as number | null,
  priceAdjustmentType: 'FIXED' as PriceAdjustmentType,
  priceValue: 0,
  productionDate: '',
  reason: ''
})

// 表单验证规则
const formRules: FormRules = {
  productName: [
    { required: true, message: '请输入商品名称', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  unit: [
    { required: true, message: '请输入商品单位', trigger: 'blur' }
  ],
  sellingPrice: [
    { required: true, message: '请输入销售价', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '销售价必须大于0', trigger: 'blur' }
  ]
}

// 批量操作方法
const handleSelectionChange = (selection: Product[]) => {
  selectedProducts.value = selection
}

const clearSelection = () => {
  selectedProducts.value = []
  // 清除表格选择状态
  if (productTableRef.value) {
    productTableRef.value.clearSelection()
  }
}

const showBatchStatusDialog = () => {
  batchDialogTitle.value = '批量商品上下架'
  batchOperationType.value = 'UPDATE_STATUS' as OperationType
  resetBatchForm()
  batchDialogVisible.value = true
}

const showBatchCategoryDialog = () => {
  batchDialogTitle.value = '批量更新分类'
  batchOperationType.value = 'UPDATE_CATEGORY' as OperationType
  resetBatchForm()
  batchDialogVisible.value = true
}

const showBatchPriceDialog = () => {
  batchDialogTitle.value = '批量更新价格'
  batchOperationType.value = 'UPDATE_PRICE' as OperationType
  resetBatchForm()
  batchDialogVisible.value = true
}

const showBatchDeleteDialog = () => {
  batchDialogTitle.value = '批量删除商品'
  batchOperationType.value = 'DELETE' as OperationType
  resetBatchForm()
  batchDialogVisible.value = true
}

const showBatchProductionDateDialog = () => {
  batchDialogTitle.value = '批量设置生产日期'
  batchOperationType.value = 'SET_PRODUCTION_DATE' as OperationType
  resetBatchForm()
  batchDialogVisible.value = true
}



// 删除功能已移除，商品管理专注于基础信息管理

const executeBatchOperation = async () => {
  // 验证是否选择了商品
  if (selectedProducts.value.length === 0) {
    ElMessage({
      message: '请先选择要操作的商品',
      type: 'warning',
      duration: 3000,
      showClose: true
    })
    return
  }

  try {
    const productIds = selectedProducts.value.map(p => {
      // 对于大整数ID，直接使用字符串，避免精度丢失
      const id = String(p.id)
      if (!id || id === 'null' || id === 'undefined') {
        throw new Error(`无效的商品ID: ${p.id}`)
      }
      return id
    })
    let response: any

    switch (batchOperationType.value) {
      case 'UPDATE_STATUS':
        response = await batchUpdateStatus(productIds, batchForm.statusValue)
        break
      case 'UPDATE_CATEGORY':
        if (!batchForm.categoryId) {
          ElMessage.warning('请选择分类')
          return
        }
        response = await batchUpdateCategory(productIds, batchForm.categoryId)
        break
      case 'UPDATE_PRICE':
        if (!batchForm.priceValue || batchForm.priceValue <= 0) {
          ElMessage.warning('请输入有效的价格调整值')
          return
        }
        const priceOperationDTO: ProductBatchOperationDTO = {
          productIds,
          operationType: 'UPDATE_PRICE' as OperationType,
          priceAdjustmentType: batchForm.priceAdjustmentType,
          priceAdjustmentValue: batchForm.priceValue,
          reason: batchForm.reason
        }
        response = await batchOperation(priceOperationDTO)
        break
      case 'SET_PRODUCTION_DATE':
        // 验证生产日期
        if (!batchForm.productionDate) {
          ElMessage.warning('请选择生产日期')
          return
        }

        // 验证生产日期不能是未来日期
        const selectedDate = new Date(batchForm.productionDate)
        const today = new Date()
        today.setHours(23, 59, 59, 999) // 设置为当天的最后一刻

        if (selectedDate > today) {
          ElMessage.warning('生产日期不能是未来日期')
          return
        }

        response = await batchSetProductionDate(productIds, batchForm.productionDate, batchForm.reason)
        break
        
      case 'DELETE':
        // 批量删除前再次确认
        try {
          await ElMessageBox.confirm(
            `确定要删除选中的 ${selectedProducts.value.length} 个商品吗？此操作不可恢复！`,
            '删除确认',
            {
              confirmButtonText: '确定删除',
              cancelButtonText: '取消',
              type: 'error',
              dangerouslyUseHTMLString: true
            }
          )
        } catch {
          return // 用户取消删除
        }
        
        response = await batchDeleteProducts(productIds)
        break

      default:
        ElMessage.error('不支持的操作类型')
        return
    }

    if (response.data.code === 200) {
      const result: ProductBatchOperationResultDTO = response.data.data

      // 根据操作类型生成不同的消息
      const getOperationMessage = (type: OperationType, count: number) => {
        switch (type) {
          case 'UPDATE_STATUS':
            const statusText = batchForm.statusValue === 1 ? '上架' : '下架'
            return `批量${statusText}成功：${count} 个商品`
          case 'UPDATE_CATEGORY':
            return `批量分类更新成功：${count} 个商品`
          case 'UPDATE_PRICE':
            return `批量价格更新成功：${count} 个商品`
          case 'SET_PRODUCTION_DATE':
            return `批量设置生产日期成功：${count} 个商品`
          case 'DELETE':
            return `批量删除成功：${count} 个商品`
          default:
            return `批量操作成功：${count} 个商品`
        }
      }

      if (result.success) {
        ElMessage({
          message: getOperationMessage(batchOperationType.value!, result.successCount),
          type: 'success',
          duration: 3000,
          showClose: true
        })
      } else {
        // 根据操作类型生成部分成功的消息
        const getPartialSuccessMessage = (type: OperationType) => {
          switch (type) {
            case 'UPDATE_STATUS':
              return batchForm.statusValue === 1 ? '上架' : '下架'
            case 'UPDATE_CATEGORY':
              return '分类更新'
            case 'UPDATE_PRICE':
              return '价格更新'
            case 'SET_PRODUCTION_DATE':
              return '生产日期设置'
            case 'DELETE':
              return '删除'
            default:
              return '操作'
          }
        }

        const operationName = getPartialSuccessMessage(batchOperationType.value!)
        ElMessage({
          message: `批量${operationName}部分成功：成功 ${result.successCount} 个，失败 ${result.failureCount} 个`,
          type: 'warning',
          duration: 5000,
          showClose: true
        })
      }
      batchDialogVisible.value = false
      clearSelection()
      getProductList()
    } else {
      ElMessage({
        message: response.data.message || '批量操作失败',
        type: 'error',
        duration: 5000,
        showClose: true
      })
    }
  } catch (error) {
    ElMessage.error('批量操作失败')
    console.error('批量操作错误:', error)
  }
}

// 库存管理功能已移至专门的库存管理页面

// 页面加载时获取数据
onMounted(() => {
  getCategories()
  getProductList()
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

// 获取商品列表
const getProductList = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }

    const response = await getProductPage(params)
    if (response.data.code === 200) {
      productList.value = response.data.data.records
      pagination.total = response.data.data.total
    } else {
      ElMessage.error(response.data.message || '获取商品列表失败')
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索商品
const searchProducts = () => {
  // 价格区间验证
  if (searchForm.minPrice !== null && searchForm.maxPrice !== null) {
    if (searchForm.minPrice > searchForm.maxPrice) {
      ElMessage.warning('最低价格不能高于最高价格')
      return
    }
  }
  
  if (searchForm.minPrice !== null && searchForm.minPrice < 0) {
    ElMessage.warning('价格不能为负数')
    return
  }
  
  if (searchForm.maxPrice !== null && searchForm.maxPrice < 0) {
    ElMessage.warning('价格不能为负数')
    return
  }
  
  pagination.pageNum = 1
  getProductList()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    productName: '',
    barcode: '',
    categoryId: null,
    status: null,
    minPrice: null,
    maxPrice: null,
    stockStatus: null,
    expiryStatus: null
  })
  pagination.pageNum = 1
  getProductList()
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  getProductList()
}

// 当前页改变
const handleCurrentChange = (page: number) => {
  pagination.pageNum = page
  getProductList()
}

// 显示添加对话框
const showAddDialog = () => {
  dialogTitle.value = '添加商品'
  isEdit.value = true  // 添加商品时应该允许编辑
  dialogVisible.value = true
  resetForm()
}

// 查看商品
const viewProduct = (row: Product) => {
  dialogTitle.value = '查看商品详情'
  isEdit.value = false
  dialogVisible.value = true

  // 填充表单数据（只读模式）
  Object.assign(productForm, {
    id: row.id,
    productName: row.productName,
    barcode: row.barcode || '',
    categoryId: row.categoryId,
    brand: row.brand || '',
    unit: row.unit,
    specification: row.specification || '',
    purchasePrice: row.purchasePrice || 0,
    sellingPrice: row.sellingPrice,
    stockQuantity: row.stockQuantity,
    minStock: row.minStock,
    maxStock: row.maxStock,
    shelfLifeDays: row.shelfLifeDays || null,
    productionDate: row.productionDate || null, // 🔥 修复：添加生产日期赋值
    manufacturer: row.manufacturer || '',
    description: row.description || ''
  })
}

// 编辑商品
const editProduct = (row: Product) => {
  dialogTitle.value = '编辑商品'
  isEdit.value = true
  dialogVisible.value = true

  // 填充表单数据
  Object.assign(productForm, {
    id: row.id,
    productName: row.productName,
    barcode: row.barcode || '',
    categoryId: row.categoryId,
    brand: row.brand || '',
    unit: row.unit,
    specification: row.specification || '',
    purchasePrice: row.purchasePrice || 0,
    sellingPrice: row.sellingPrice,
    stockQuantity: row.stockQuantity,
    minStock: row.minStock,
    maxStock: row.maxStock,
    shelfLifeDays: row.shelfLifeDays || null,
    productionDate: row.productionDate || null, // 🔥 修复：添加生产日期赋值
    manufacturer: row.manufacturer || '',
    description: row.description || ''
  })
}

// 删除商品
const deleteProduct = async (row: Product) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除商品"${row.productName}"吗？此操作不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true
      }
    )

    const response = await deleteProductApi(String(row.id))
    
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      getProductList()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 切换商品状态
const toggleStatus = async (row: Product) => {
  const action = row.status === 1 ? '下架' : '上架'
  const newStatus = row.status === 1 ? 0 : 1

  try {
    await ElMessageBox.confirm(`确定要${action}该商品吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await updateProductStatus(String(row.id), newStatus)

    if (response.data.code === 200) {
      ElMessage.success(`${action}成功`)
      getProductList()
    } else {
      ElMessage.error(response.data.message || `${action}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`${action}失败:`, error)
      ElMessage.error(`${action}失败`)
    }
  }
}

// 提交表单
const submitForm = async () => {
  if (!productFormRef.value) return

  try {
    await productFormRef.value.validate()
    submitting.value = true

    let response
    if (productForm.id) {
      // 编辑商品
      response = await updateProduct(String(productForm.id), productForm)
    } else {
      // 添加商品
      response = await addProduct(productForm)
    }

    if (response.data.code === 200) {
      ElMessage.success(productForm.id ? '更新成功' : '添加成功')
      dialogVisible.value = false
      getProductList()
    } else {
      ElMessage.error(response.data.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// ==================== 商品管理核心方法 ====================

/**
 * 计算商品过期状态
 */
const getExpirationStatus = (product: Product): 'expired' | 'expiring' | 'normal' | 'unknown' => {
  if (!product.productionDate || !product.shelfLifeDays || product.shelfLifeDays <= 0) {
    return 'unknown'
  }
  
  const productionDate = new Date(product.productionDate)
  const expirationDate = new Date(productionDate)
  expirationDate.setDate(expirationDate.getDate() + product.shelfLifeDays)
  
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  expirationDate.setHours(0, 0, 0, 0)
  
  if (expirationDate < today) {
    return 'expired' // 已过期
  }
  
  const warningDate = new Date(today)
  warningDate.setDate(warningDate.getDate() + 7) // 7天内过期为临期
  
  if (expirationDate <= warningDate) {
    return 'expiring' // 临期
  }
  
  return 'normal' // 正常
}

// 使用统一的日期禁用函数
const disabledDate = disableFutureDate

// ==================== 表单实时验证方法 ====================

/**
 * 价格实时验证
 */
const validatePrices = () => {
  if (!isEdit.value) return
  
  const purchasePrice = productForm.purchasePrice
  const sellingPrice = productForm.sellingPrice
  
  // 检查进货价是否高于销售价
  if (purchasePrice && sellingPrice && purchasePrice > sellingPrice) {
    ElMessage({
      message: '注意：进货价高于销售价，可能导致亏损',
      type: 'warning',
      duration: 3000
    })
  }
}

/**
 * 生产日期和保质期实时验证
 */
const validateProductionDateAndShelfLife = () => {
  if (!isEdit.value) return
  
  const productionDate = productForm.productionDate
  const shelfLifeDays = productForm.shelfLifeDays
  
  if (productionDate && shelfLifeDays && shelfLifeDays > 0) {
    const prodDate = new Date(productionDate)
    const expirationDate = new Date(prodDate)
    expirationDate.setDate(expirationDate.getDate() + shelfLifeDays)
    
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    
    // 检查是否已过期
    if (expirationDate < today) {
      ElMessage({
        message: '警告：根据生产日期和保质期，该商品已过期',
        type: 'error',
        duration: 5000
      })
      return
    }
    
    // 检查是否临期（7天内过期）
    const warningDate = new Date(today)
    warningDate.setDate(warningDate.getDate() + 7)
    
    if (expirationDate <= warningDate) {
      ElMessage({
        message: '注意：该商品将在7天内过期',
        type: 'warning',
        duration: 4000
      })
    }
  }
}

// 重置批量操作表单
const resetBatchForm = () => {
  Object.assign(batchForm, {
    statusValue: 1,
    categoryId: null,
    priceAdjustmentType: 'FIXED' as PriceAdjustmentType,
    priceValue: 0,
    productionDate: '',
    reason: ''
  })
}

// 重置表单
const resetForm = () => {
  if (productFormRef.value) {
    productFormRef.value.resetFields()
  }
  Object.assign(productForm, {
    id: null,
    productName: '',
    barcode: '',
    categoryId: null,
    brand: '',
    unit: '',
    specification: '',
    purchasePrice: 0,
    sellingPrice: 0,
    stockQuantity: 0,
    minStock: 0,
    maxStock: 0,
    shelfLifeDays: null,
    productionDate: null,
    manufacturer: '',
    description: ''
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
}

.products-container {
  padding: 24px;
  background: var(--ios-system-background);
  min-height: 100vh;
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

.page-header h2 {
  margin: 0;
  font-size: 32px;
  font-weight: 700;
  color: var(--ios-label);
  letter-spacing: -0.6px;
  line-height: 1.2;
}

/* iOS风格搜索区域 */
.search-section {
  margin-bottom: 32px;
}

.search-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 20px;
  padding: 28px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);
}

.search-header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--ios-separator);
}

.search-icon {
  font-size: 24px;
  color: var(--ios-accent);
  margin-right: 12px;
  opacity: 0.9;
}

.search-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--ios-label);
  letter-spacing: -0.3px;
}

/* iOS风格表格区域 */
.table-section {
  background: transparent;
  border-radius: 20px;
  overflow: hidden;
}

.table-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 20px;
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
  padding: 24px 28px;
  background: rgba(248, 248, 248, 0.8);
  border-bottom: 1px solid var(--ios-separator);
}

.table-title {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: 600;
  color: var(--ios-label);
  letter-spacing: -0.3px;
}

.table-icon {
  font-size: 22px;
  color: var(--ios-accent);
  margin-right: 12px;
  opacity: 0.9;
}

.pagination-section {
  display: flex;
  justify-content: center;
  padding: 24px;
  background: rgba(248, 248, 248, 0.6);
  border-top: 1px solid var(--ios-separator);
}

.low-stock {
  color: #FF3B30;
  font-weight: 600;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* iOS风格表格样式 */
:deep(.el-table) {
  border-radius: 0;
  border: none;
  background: transparent;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', system-ui, sans-serif;
}

:deep(.el-table th) {
  background: transparent;
  color: var(--ios-secondary-label);
  font-weight: 600;
  font-size: 14px;
  border-bottom: 1px solid var(--ios-separator);
  padding: 16px 12px;
}

:deep(.el-table td) {
  padding: 16px 12px;
  border-bottom: 1px solid rgba(199, 199, 204, 0.3);
  font-size: 15px;
  color: var(--ios-label);
}

:deep(.el-table tr:hover > td) {
  background: rgba(0, 0, 0, 0.02) !important;
}

:deep(.el-table .el-table__body tr.current-row > td) {
  background: rgba(0, 122, 255, 0.05) !important;
}

// 表格内的标签样式
:deep(.el-tag) {
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  padding: 4px 8px;
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
    background: rgba(142, 142, 147, 0.15);
    color: #8E8E93;
  }
}

/* iOS风格表单样式 */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--ios-secondary-label);
  font-size: 15px;
}

:deep(.el-form) {
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', system-ui, sans-serif;
}

:deep(.el-form--inline .el-form-item) {
  margin-right: 20px;
  margin-bottom: 20px;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
  border: 1px solid var(--ios-separator);
  background: var(--ios-secondary-background);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
  min-height: 40px;
  
  &:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    border-color: var(--ios-gray);
  }
  
  &.is-focus {
    border-color: var(--ios-accent);
    box-shadow: 0 0 0 3px rgba(28, 28, 30, 0.15);
  }
}

:deep(.el-input__inner) {
  font-size: 15px;
  color: var(--ios-label);
  
  &::placeholder {
    color: var(--ios-tertiary-label);
  }
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 10px;
}

:deep(.el-input-number .el-input__wrapper) {
  border-radius: 10px;
}

:deep(.el-textarea .el-textarea__inner) {
  border-radius: 12px;
  border: 1px solid var(--ios-separator);
  background: var(--ios-secondary-background);
  font-size: 15px;
  color: var(--ios-label);
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', system-ui, sans-serif;
  
  &:focus {
    border-color: var(--ios-accent);
    box-shadow: 0 0 0 3px rgba(28, 28, 30, 0.15);
  }
  
  &::placeholder {
    color: var(--ios-tertiary-label);
  }
}

/* iOS风格按钮样式 */
:deep(.el-button) {
  border-radius: 10px;
  font-weight: 500;
  font-size: 14px;
  min-height: 36px;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', system-ui, sans-serif;
  transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &:hover {
    transform: translateY(-1px);
  }
  
  &:active {
    transform: scale(0.98);
  }
  
  &.is-circle {
    transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
    
    &:hover {
      transform: scale(1.05);
    }
    
    &:active {
      transform: scale(0.95);
    }
  }
}

:deep(.el-button--primary) {
  background: var(--ios-accent);
  border-color: var(--ios-accent);
  color: var(--ios-white);
  box-shadow: 0 2px 8px rgba(28, 28, 30, 0.25);
  
  &:hover {
    background: var(--ios-secondary);
    border-color: var(--ios-secondary);
    box-shadow: 0 4px 12px rgba(28, 28, 30, 0.35);
  }
}

:deep(.el-button--default) {
  background: rgba(28, 28, 30, 0.06);
  border-color: rgba(28, 28, 30, 0.15);
  color: var(--ios-label);
  
  &:hover {
    background: rgba(28, 28, 30, 0.1);
    border-color: rgba(28, 28, 30, 0.2);
  }
}

/* iOS风格对话框样式 */
:deep(.el-dialog) {
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(20px);
}

:deep(.el-dialog__header) {
  padding: 24px 24px 16px;
  border-bottom: 1px solid var(--ios-separator);
}

:deep(.el-dialog__title) {
  font-size: 20px;
  font-weight: 600;
  color: var(--ios-label);
  letter-spacing: -0.3px;
}

:deep(.el-dialog__body) {
  padding: 24px;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', system-ui, sans-serif;
}

:deep(.el-dialog__footer) {
  padding: 16px 24px 24px;
  border-top: 1px solid var(--ios-separator);
}

/* 查看模式样式 */
:deep(.el-input.is-disabled .el-input__inner),
:deep(.el-input__inner[readonly]) {
  background-color: #f8f9fa !important;
  border-color: #e9ecef !important;
  color: #495057 !important;
}

:deep(.el-select.is-disabled .el-input__inner) {
  background-color: #f8f9fa !important;
  border-color: #e9ecef !important;
  color: #495057 !important;
}

:deep(.el-input-number.is-disabled .el-input__inner) {
  background-color: #f8f9fa !important;
  border-color: #e9ecef !important;
  color: #495057 !important;
}

:deep(.el-textarea.is-disabled .el-textarea__inner),
:deep(.el-textarea__inner[readonly]) {
  background-color: #f8f9fa !important;
  border-color: #e9ecef !important;
  color: #495057 !important;
}

/* iOS风格头部内容 */
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
  font-size: 28px;
  margin-right: 16px;
  color: var(--ios-accent);
  opacity: 0.9;
}

.header-desc {
  margin: 0;
  font-size: 16px;
  color: var(--ios-secondary-label);
  opacity: 0.8;
  font-weight: 400;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.header-stats {
  display: flex;
  gap: 20px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 16px;
  padding: 20px 24px;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.25s cubic-bezier(0.4, 0.0, 0.2, 1);
  min-width: 100px;
}

.stat-card:hover {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 
    0 6px 20px rgba(0, 0, 0, 0.1),
    0 3px 10px rgba(0, 0, 0, 0.06);
  background: rgba(255, 255, 255, 0.98);
}

.stat-card.warning {
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #FF9500, #FFB340);
    border-radius: 16px 16px 0 0;
  }
  position: relative;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: var(--ios-label);
  margin-bottom: 6px;
  letter-spacing: -0.5px;
  line-height: 1.1;
}

.stat-label {
  font-size: 14px;
  color: var(--ios-secondary-label);
  opacity: 0.8;
  font-weight: 500;
}

.add-btn {
  height: 44px;
  padding: 0 24px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 12px;
  background: var(--ios-accent);
  color: var(--ios-white);
  border: none;
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

.stock-number {
  font-weight: 600;
}

.stock-number.low-stock {
  color: #f56c6c;
}

.warning-icon {
  font-size: 14px;
  color: #f56c6c;
}

.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: #fafbfc;
  border-top: 1px solid #e4e7ed;
}

/* iOS风格批量操作工具栏 */
.batch-toolbar {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 18px;
  padding: 20px 28px;
  margin: 24px 0;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.batch-info {
  font-weight: 600;
  font-size: 16px;
  color: var(--ios-label);
  display: flex;
  align-items: center;
  
  &::before {
    content: '';
    width: 8px;
    height: 8px;
    background: #34C759;
    border-radius: 50%;
    margin-right: 12px;
    animation: pulse-green 2s infinite;
  }
}

.batch-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.batch-actions .el-button {
  height: 36px;
  padding: 0 16px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 10px;
  border: 1px solid rgba(28, 28, 30, 0.15);
  background: rgba(28, 28, 30, 0.06);
  color: var(--ios-label);
  transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &:hover {
    background: rgba(28, 28, 30, 0.1);
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  &:active {
    transform: scale(0.98);
  }
  
  // 特定类型按钮颜色
  &.el-button--success {
    background: rgba(52, 199, 89, 0.1);
    border-color: rgba(52, 199, 89, 0.2);
    color: #34C759;
    
    &:hover {
      background: rgba(52, 199, 89, 0.15);
    }
  }
  
  &.el-button--primary {
    background: rgba(0, 122, 255, 0.1);
    border-color: rgba(0, 122, 255, 0.2);
    color: #007AFF;
    
    &:hover {
      background: rgba(0, 122, 255, 0.15);
    }
  }
  
  &.el-button--warning {
    background: rgba(255, 149, 0, 0.1);
    border-color: rgba(255, 149, 0, 0.2);
    color: #FF9500;
    
    &:hover {
      background: rgba(255, 149, 0, 0.15);
    }
  }
  
  &.el-button--danger {
    background: rgba(255, 59, 48, 0.1);
    border-color: rgba(255, 59, 48, 0.2);
    color: #FF3B30;
    
    &:hover {
      background: rgba(255, 59, 48, 0.15);
    }
  }
  
  &.el-button--info {
    background: rgba(142, 142, 147, 0.1);
    border-color: rgba(142, 142, 147, 0.2);
    color: #8E8E93;
    
    &:hover {
      background: rgba(142, 142, 147, 0.15);
    }
  }
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

/* 库存信息样式 */
.stock-readonly {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  font-size: 14px;
  color: #606266;
}

.stock-info {
  display: flex;
  align-items: center;
}

.stock-number {
  font-weight: 500;
}

/* 批量状态选择样式 */
:deep(.el-radio) {
  margin-right: 20px;

  .el-radio__label {
    display: flex;
    align-items: center;
    font-weight: 500;
  }
}

:deep(.el-radio.is-checked .el-radio__label) {
  color: var(--el-color-primary);
}

/* 状态按钮样式优化 */
.el-button.is-circle {
  transition: all 0.3s ease;
}

.el-button.is-circle:hover {
  transform: scale(1.1);
}

/* 过期状态样式 */
.expired-date {
  color: #f56c6c;
  font-weight: bold;
}

.expiring-date {
  color: #e6a23c;
  font-weight: bold;
}

.status-tags {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

/* 删除警告样式 */
.delete-warning {
  margin-bottom: 16px;
}

.delete-warning :deep(.el-alert) {
  border-radius: 8px;
}

.delete-warning :deep(.el-alert__title) {
  font-weight: 600;
}
</style>
