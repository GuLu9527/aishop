<template>
  <div class="cashier-container">
    <!-- 收银台头部 -->
    <div class="cashier-header">
      <div class="header-left">
        <div class="cashier-info">
          <el-icon class="cashier-icon"><ShoppingCart /></el-icon>
          <div class="cashier-details">
            <h2>收银台</h2>
            <p>
              <span v-if="cashierName === '加载中...'" class="loading-text">
                <el-icon class="is-loading"><Loading /></el-icon>
                正在加载用户信息...
              </span>
              <span v-else>
                收银员：{{ cashierName }}{{ cashierRole ? ` (${cashierRole})` : '' }} | 收银机：{{ terminalId }}
              </span>
            </p>
          </div>
        </div>
      </div>
      <div class="header-right">
        <div class="time-display">
          <div class="current-time">{{ currentTime }}</div>
          <div class="current-date">{{ currentDate }}</div>
        </div>
      </div>
    </div>

    <div class="cashier-main">
      <!-- 左侧：商品扫描和购物车 -->
      <div class="left-panel">
        <!-- 扫码区域 -->
        <div class="scan-section">
          <div class="scan-header">
            <el-icon class="scan-icon"><Search /></el-icon>
            <span>商品扫描</span>
          </div>
          <div class="scan-input">
            <el-input
              ref="barcodeInputRef"
              v-model="barcodeInput"
              placeholder="请扫描商品条码或手动输入"
              size="large"
              @keyup.enter="scanProduct"
              @focus="handleInputFocus"
            >
              <template #prepend>
                <el-icon><Search /></el-icon>
              </template>
              <template #append>
                <el-button @click="scanProduct" type="primary">
                  <el-icon><Plus /></el-icon>
                  添加
                </el-button>
              </template>
            </el-input>
          </div>
          <div class="scan-tips">
            <el-alert
              title="扫码提示：将条码对准扫描枪，或手动输入商品条码后按回车"
              type="info"
              :closable="false"
              show-icon
            />
          </div>
        </div>

        <!-- 购物车 -->
        <div class="cart-section">
          <div class="cart-header">
            <div class="cart-title">
              <el-icon class="cart-icon"><ShoppingCart /></el-icon>
              <span>购物清单</span>
              <el-badge :value="cartItems.length" class="cart-badge" />
            </div>
            <div class="cart-actions">
              <el-button size="small" @click="clearCart" :disabled="cartItems.length === 0">
                <el-icon><Delete /></el-icon>
                清空
              </el-button>
            </div>
          </div>

          <div class="cart-content">
            <div v-if="cartItems.length === 0" class="empty-cart">
              <el-empty description="购物车为空，请扫描商品" />
            </div>
            <div v-else class="cart-list">
              <div
                v-for="(item, index) in cartItems"
                :key="index"
                class="cart-item"
                :class="{ 'selected': selectedItemIndex === index }"
                @click="selectItem(index)"
              >
                <div class="item-info">
                  <div class="item-name">{{ item.productName }}</div>
                  <div class="item-details">
                    <span class="item-price">¥{{ item.sellingPrice }}</span>
                    <span class="item-barcode">{{ item.barcode }}</span>
                  </div>
                  <!-- 批次信息显示 -->
                  <div v-if="item.batchTrackingEnabled && item.batchInfo" class="batch-info">
                    <div class="batch-details">
                      <el-tag size="small" type="success" class="batch-tag">
                        <el-icon><Calendar /></el-icon>
                        批次: {{ item.batchInfo.batchCode }}
                      </el-tag>
                      <span v-if="item.batchInfo.expiryDate" class="expiry-info">
                        <el-tag
                          size="small"
                          :type="getExpiryTagType(item.batchInfo.remainingDays)"
                          class="expiry-tag"
                        >
                          <el-icon><Clock /></el-icon>
                          {{ formatExpiryInfo(item.batchInfo.expiryDate, item.batchInfo.remainingDays) }}
                        </el-tag>
                      </span>
                    </div>
                  </div>
                </div>
                <div class="item-quantity">
                  <el-input-number
                    v-model="item.quantity"
                    :min="1"
                    :max="item.stockQuantity"
                    size="small"
                    @change="updateItemQuantity(index, $event)"
                  />
                </div>
                <div class="item-total">
                  ¥{{ (item.sellingPrice * item.quantity).toFixed(2) }}
                </div>
                <div class="item-actions">
                  <el-button
                    size="small"
                    type="danger"
                    circle
                    @click="removeItem(index)"
                  >
                    <el-icon><Close /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：结算面板 -->
      <div class="right-panel">
        <!-- 金额显示 -->
        <div class="amount-section">
          <div class="amount-display">
            <div class="total-amount">
              <div class="amount-label">应收金额</div>
              <div class="amount-value">¥{{ totalAmount.toFixed(2) }}</div>
            </div>
            <div class="item-count">
              <div class="count-label">商品数量</div>
              <div class="count-value">{{ totalQuantity }}件</div>
            </div>
          </div>
        </div>

        <!-- 支付方式 -->
        <div class="payment-section">
          <div class="payment-header">
            <el-icon class="payment-icon"><CreditCard /></el-icon>
            <span>支付方式</span>
          </div>
          <div class="payment-methods">
            <el-radio-group v-model="paymentMethod" size="large">
              <el-radio-button label="cash">
                <el-icon><Money /></el-icon>
                现金
              </el-radio-button>
              <el-radio-button label="card">
                <el-icon><CreditCard /></el-icon>
                刷卡
              </el-radio-button>
              <el-radio-button label="alipay">
                <el-icon><CreditCard /></el-icon>
                支付宝
              </el-radio-button>
              <el-radio-button label="wechat">
                <el-icon><CreditCard /></el-icon>
                微信
              </el-radio-button>
            </el-radio-group>
          </div>

          <!-- 现金支付时的找零计算 -->
          <div v-if="paymentMethod === 'cash'" class="cash-payment">
            <div class="received-amount">
              <label>实收金额：</label>
              <el-input-number
                v-model="receivedAmount"
                :min="totalAmount"
                :precision="2"
                size="large"
                style="width: 200px"
                @change="calculateChange"
              />
            </div>
            <div class="change-amount" v-if="changeAmount > 0">
              <div class="change-label">找零金额</div>
              <div class="change-value">¥{{ changeAmount.toFixed(2) }}</div>
            </div>
          </div>
        </div>

        <!-- 结算按钮 -->
        <div class="checkout-section">
          <el-button
            type="primary"
            size="large"
            class="checkout-btn"
            :disabled="cartItems.length === 0 || (paymentMethod === 'cash' && receivedAmount < totalAmount)"
            @click="processPayment"
            :loading="processing"
          >
            <el-icon><Check /></el-icon>
            立即结算
          </el-button>

          <div class="quick-actions">
            <el-button size="default" @click="holdTransaction">
              <el-icon><Clock /></el-icon>
              挂单
            </el-button>
            <el-button size="default" @click="showHeldTransactions">
              <el-icon><List /></el-icon>
              取单
            </el-button>
            <el-button size="default" @click="showProductSelector">
              <el-icon><Search /></el-icon>
              选择商品
            </el-button>
          </div>
        </div>

        <!-- 快捷功能 -->
        <div class="quick-functions">
          <div class="function-header">
            <el-icon><Setting /></el-icon>
            <span>快捷功能</span>
          </div>
          <div class="function-buttons">
            <el-button @click="openDrawer" class="function-btn">
              <el-icon><Box /></el-icon>
              <span>开钱箱</span>
            </el-button>
            <el-button @click="printLastReceipt" class="function-btn">
              <el-icon><Printer /></el-icon>
              <span>重打小票</span>
            </el-button>
            <el-button @click="showSalesReport" class="function-btn">
              <el-icon><Document /></el-icon>
              <span>销售报表</span>
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 挂单列表对话框 -->
    <el-dialog
      v-model="heldTransactionsVisible"
      title="挂单列表"
      width="800px"
    >
      <el-table :data="heldTransactions" style="width: 100%">
        <el-table-column prop="id" label="挂单号" width="120" />
        <el-table-column prop="itemCount" label="商品数量" width="100" />
        <el-table-column prop="totalAmount" label="金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalAmount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="挂单时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="{ row, $index }">
            <el-button size="small" type="primary" @click="resumeTransaction(row, $index)">
              取单
            </el-button>
            <el-button size="small" type="danger" @click="deleteHeldTransactionItem($index)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 商品选择对话框 -->
    <el-dialog
      v-model="productSelectorVisible"
      title="选择商品"
      width="1000px"
      :close-on-click-modal="false"
    >
      <!-- 搜索区域 -->
      <div class="product-search">
        <el-form :model="productSearchForm" inline>
          <el-form-item label="商品名称">
            <el-input
              v-model="productSearchForm.keyword"
              placeholder="请输入商品名称或条码"
              style="width: 200px"
              @keyup.enter="searchProducts"
            />
          </el-form-item>
          <el-form-item label="商品分类">
            <el-select
              v-model="productSearchForm.categoryId"
              placeholder="请选择分类"
              style="width: 150px"
              clearable
            >
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.categoryName"
                :value="category.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchProducts">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetProductSearch">
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 商品列表 -->
      <el-table
        :data="productList"
        v-loading="productLoading"
        style="width: 100%"
        max-height="400px"
      >
        <el-table-column prop="productName" label="商品名称" min-width="150">
          <template #default="{ row }">
            <div class="product-info">
              <div class="product-name">{{ row.productName }}</div>
              <div class="product-barcode">{{ row.barcode }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ row.categoryName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sellingPrice" label="售价" width="80">
          <template #default="{ row }">
            <span class="price">¥{{ row.sellingPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stockQuantity" label="库存" width="80">
          <template #default="{ row }">
            <span :class="getStockClass(row)">{{ row.stockQuantity }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="60" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button
              size="small"
              type="primary"
              @click="selectProduct(row)"
              :disabled="row.stockQuantity <= 0"
            >
              <el-icon><Plus /></el-icon>
              添加
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="product-pagination">
        <el-pagination
          v-model:current-page="productPagination.pageNum"
          v-model:page-size="productPagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="productPagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleProductSizeChange"
          @current-change="handleProductCurrentChange"
        />
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="productSelectorVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import {
  ShoppingCart, Search, Plus, Delete, Close, CreditCard,
  Money, Check, Clock, List, Setting,
  Box, Printer, Document, Loading, Calendar
} from '@element-plus/icons-vue'
import {
  getProductList,
  getCategoryList,
  getProductByBarcode,
  processPayment as processPaymentAPI,
  saveHeldTransaction,
  getHeldTransactions,
  resumeHeldTransaction,
  deleteHeldTransaction,
  getCashierInfo
} from '@/api/cashier'
import { getUserInfo } from '@/api/user'

// 路由
const router = useRouter()

// 响应式数据
const barcodeInputRef = ref()
const barcodeInput = ref('')
const cartItems = ref<any[]>([])
const selectedItemIndex = ref(-1)
const paymentMethod = ref('cash')
const receivedAmount = ref(0)
const processing = ref(false)
const heldTransactionsVisible = ref(false)
const heldTransactions = ref<any[]>([])

// 商品选择相关
const productSelectorVisible = ref(false)
const productList = ref<any[]>([])
const productLoading = ref(false)
const categories = ref<any[]>([])
const productSearchForm = reactive({
  keyword: '',
  categoryId: undefined as number | undefined
})
const productPagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 收银员信息
const cashierName = ref('加载中...')
const cashierRole = ref('')
const currentUserId = ref<number | null>(null)
const terminalId = ref('POS001')

// 时间显示
const currentTime = ref('')
const currentDate = ref('')

// 计算属性
const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.sellingPrice * item.quantity), 0)
})

const totalQuantity = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

const changeAmount = computed(() => {
  return Math.max(0, receivedAmount.value - totalAmount.value)
})

// 页面加载时的初始化
onMounted(() => {
  updateTime()
  setInterval(updateTime, 1000)
  focusBarcodeInput()
  loadCategories()
  loadCurrentUser()
})

onUnmounted(() => {
  // 清理定时器
})

// 更新时间显示
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN')
  currentDate.value = now.toLocaleDateString('zh-CN')
}

// 聚焦条码输入框
const focusBarcodeInput = () => {
  nextTick(() => {
    if (barcodeInputRef.value) {
      barcodeInputRef.value.focus()
    }
  })
}

// 加载当前用户信息
const loadCurrentUser = async (retryCount = 0) => {
  try {
    // 首先从localStorage获取用户信息
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      try {
        const user = JSON.parse(userInfo)
        if (user && (user.realName || user.username)) {
          setUserInfo(user)
          return
        }
      } catch (e) {
        console.warn('解析本地用户信息失败:', e)
        localStorage.removeItem('userInfo')
      }
    }

    // 如果本地没有有效用户信息，调用API获取
    const response = await getUserInfo()
    if (response.data.code === 200) {
      const user = response.data.data
      setUserInfo(user)

      // 保存到localStorage
      localStorage.setItem('userInfo', JSON.stringify(user))
    } else {
      throw new Error(response.data.message || '获取用户信息失败')
    }

  } catch (error) {
    console.error('加载用户信息失败:', error)

    // 重试机制：最多重试2次
    if (retryCount < 2) {
      setTimeout(() => {
        loadCurrentUser(retryCount + 1)
      }, 1000 * (retryCount + 1)) // 递增延迟
      return
    }

    // 重试失败后的处理
    cashierName.value = '获取失败'

    // 检查是否有token
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage({
        message: '登录已过期，请重新登录',
        type: 'error',
        duration: 4000,
        showClose: true
      })
      cashierName.value = '未登录'
    } else {
      ElMessage({
        message: '获取用户信息失败，请刷新页面重试',
        type: 'warning',
        duration: 3000,
        showClose: true
      })
    }
  }
}

// 设置用户信息的辅助函数
const setUserInfo = (user: any) => {
  cashierName.value = user.realName || user.username || '未知用户'
  cashierRole.value = getRoleDisplayName(user.role || user.roleName || '')
  currentUserId.value = user.id || user.userId || null

  // 根据用户ID生成终端ID
  if (currentUserId.value) {
    terminalId.value = `POS${String(currentUserId.value).padStart(3, '0')}`
  }
}

// 获取角色显示名称
const getRoleDisplayName = (role: string) => {
  const roleMap: Record<string, string> = {
    'admin': '管理员',
    'manager': '经理',
    'cashier': '收银员',
    'staff': '员工'
  }
  return roleMap[role.toLowerCase()] || role
}

// 处理输入框聚焦
const handleInputFocus = () => {
  // 确保输入框始终聚焦，方便扫码枪输入
}

// 扫描商品
const scanProduct = async () => {
  if (!barcodeInput.value.trim()) {
    ElMessage({
      message: '请输入商品条码',
      type: 'warning',
      duration: 3000,
      showClose: true
    })
    return
  }

  try {
    // 调用API根据条码查询商品
    const response = await getProductByBarcode(barcodeInput.value)

    if (response.data.code === 200 && response.data.data) {
      const product = response.data.data

      // 检查库存
      if (product.stockQuantity <= 0) {
        ElMessage({
          message: `商品 ${product.productName} 库存不足`,
          type: 'warning',
          duration: 3000,
          showClose: true
        })
        barcodeInput.value = ''
        focusBarcodeInput()
        return
      }

      // 检查商品状态
      if (product.status !== 1) {
        ElMessage({
          message: `商品 ${product.productName} 已下架`,
          type: 'warning',
          duration: 3000,
          showClose: true
        })
        barcodeInput.value = ''
        focusBarcodeInput()
        return
      }

      addToCart(product)
      barcodeInput.value = ''
      focusBarcodeInput()
    } else {
      ElMessage.error(response.data.message || '商品不存在或已下架')
      barcodeInput.value = ''
      focusBarcodeInput()
    }

  } catch (error) {
    console.error('扫描商品失败:', error)
    ElMessage.error('商品不存在或已下架')
    barcodeInput.value = ''
    focusBarcodeInput()
  }
}

// 添加商品到购物车
const addToCart = async (product: any) => {
  const existingIndex = cartItems.value.findIndex(item => item.barcode === product.barcode)

  if (existingIndex >= 0) {
    // 商品已存在，增加数量
    const existingItem = cartItems.value[existingIndex]
    if (existingItem.quantity < product.stockQuantity) {
      existingItem.quantity++
      ElMessage.success(`${product.productName} 数量+1`)
    } else {
      ElMessage.warning('库存不足')
    }
  } else {
    // 检查是否启用批次管理
    const batchTrackingEnabled = await checkBatchTracking(product.id)

    // 新商品，添加到购物车
    const cartItem = {
      ...product,
      quantity: 1,
      batchTrackingEnabled: batchTrackingEnabled
    }

    // 如果启用批次管理，获取批次信息
    if (batchTrackingEnabled) {
      try {
        const batchInfo = await getBatchInfoForProduct(product.id)
        cartItem.batchInfo = batchInfo
      } catch (error) {
        console.error('获取批次信息失败:', error)
        ElMessage.warning('获取批次信息失败，将使用传统模式')
        cartItem.batchTrackingEnabled = false
      }
    }

    cartItems.value.push(cartItem)
    ElMessage.success(`已添加 ${product.productName}`)
  }
}

// 获取商品的批次信息
const getBatchInfoForProduct = async (productId: number) => {
  try {
    const response = await fetch(`/api/product-batch/available/${productId}`)
    const result = await response.json()

    if (result.code === 200 && result.data && result.data.length > 0) {
      // 返回最早过期的批次信息（FIFO）
      const batch = result.data[0]
      return {
        batchId: batch.id,
        batchCode: batch.batchCode,
        productionDate: batch.productionDate,
        expiryDate: batch.expiryDate,
        availableQuantity: batch.availableQuantity,
        remainingDays: batch.remainingDays
      }
    }
    return null
  } catch (error) {
    console.error('获取批次信息失败:', error)
    throw error
  }
}

// 选择购物车项目
const selectItem = (index: number) => {
  selectedItemIndex.value = index
}

// 更新商品数量
const updateItemQuantity = (index: number, quantity: number) => {
  if (quantity > 0) {
    cartItems.value[index].quantity = quantity
  }
}

// 移除商品
const removeItem = (index: number) => {
  const item = cartItems.value[index]
  cartItems.value.splice(index, 1)
  ElMessage.success(`已移除 ${item.productName}`)

  if (selectedItemIndex.value >= cartItems.value.length) {
    selectedItemIndex.value = -1
  }
}

// 清空购物车
const clearCart = async () => {
  try {
    await ElMessageBox.confirm('确定要清空购物车吗？', '确认清空', {
      type: 'warning'
    })
    cartItems.value = []
    selectedItemIndex.value = -1
    ElMessage.success('购物车已清空')
  } catch {
    // 用户取消
  }
}

// 计算找零
const calculateChange = () => {
  // 找零金额通过计算属性自动计算
}

// 处理支付
const processPayment = async () => {
  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }

  if (paymentMethod.value === 'cash' && receivedAmount.value < totalAmount.value) {
    ElMessage.warning('实收金额不足')
    return
  }

  try {
    processing.value = true

    // 准备支付数据
    const paymentData = {
      items: cartItems.value.map(item => ({
        productId: item.id,
        productName: item.productName,
        barcode: item.barcode,
        sellingPrice: item.sellingPrice,
        quantity: item.quantity,
        unit: item.unit,
        subtotal: item.sellingPrice * item.quantity
      })),
      paymentMethod: paymentMethod.value,
      totalAmount: totalAmount.value,
      receivedAmount: paymentMethod.value === 'cash' ? receivedAmount.value : totalAmount.value,
      changeAmount: paymentMethod.value === 'cash' ? changeAmount.value : 0,
      cashierId: currentUserId.value || 1, // 使用当前登录用户ID
      terminalId: terminalId.value
    }

    // 调用支付API
    const response = await processPaymentAPI(paymentData)

    if (response.data.code === 200) {
      const orderNo = response.data.data
      ElMessage.success(`支付成功！订单号：${orderNo}`)

      // 清空购物车
      cartItems.value = []
      selectedItemIndex.value = -1
      receivedAmount.value = 0

      // 打印小票
      printReceipt(orderNo)
    } else {
      ElMessage.error(response.data.message || '支付失败')
    }

  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败，请重试')
  } finally {
    processing.value = false
    focusBarcodeInput()
  }
}

// 挂单
const holdTransaction = async () => {
  if (cartItems.value.length === 0) {
    ElMessage({
      message: '购物车为空，无法挂单',
      type: 'warning',
      duration: 3000,
      showClose: true
    })
    return
  }

  try {
    const heldTransactionData = {
      items: cartItems.value.map(item => ({
        productId: item.id,
        productName: item.productName,
        barcode: item.barcode,
        sellingPrice: item.sellingPrice,
        quantity: item.quantity,
        unit: item.unit,
        subtotal: item.sellingPrice * item.quantity
      })),
      itemCount: totalQuantity.value,
      totalAmount: totalAmount.value,
      cashierId: currentUserId.value || 1,
      terminalId: terminalId.value
    }

    const response = await saveHeldTransaction(heldTransactionData)

    if (response.data.code === 200) {
      const transactionNo = response.data.data
      cartItems.value = []
      selectedItemIndex.value = -1

      ElMessage({
        message: `挂单成功，挂单号：${transactionNo}`,
        type: 'success',
        duration: 2000,
        showClose: true
      })
      focusBarcodeInput()
    } else {
      ElMessage.error(response.data.message || '挂单失败')
    }
  } catch (error) {
    console.error('挂单失败:', error)
    ElMessage.error('挂单失败，请重试')
  }
}

// 显示挂单列表
const showHeldTransactions = async () => {
  try {
    const response = await getHeldTransactions(currentUserId.value || 1, terminalId.value)
    if (response.data.code === 200) {
      heldTransactions.value = response.data.data || []
      heldTransactionsVisible.value = true
    } else {
      ElMessage.error(response.data.message || '获取挂单列表失败')
    }
  } catch (error) {
    console.error('获取挂单列表失败:', error)
    ElMessage.error('获取挂单列表失败，请重试')
  }
}

// 恢复挂单
const resumeTransaction = async (transaction: any, index: number) => {
  try {
    console.log('开始恢复挂单:', transaction.id)
    const response = await resumeHeldTransaction(transaction.id)
    console.log('取单响应:', response.data)

    if (response.data.code === 200) {
      const resumedData = response.data.data
      console.log('恢复的数据:', resumedData)

      // 确保items是数组且不为空
      if (resumedData.items && Array.isArray(resumedData.items)) {
        // 清空当前购物车
        cartItems.value.splice(0, cartItems.value.length)

        // 逐个添加商品到购物车，确保响应式更新
        resumedData.items.forEach((item: any) => {
          cartItems.value.push({
            ...item,
            // 确保必要字段存在
            productId: item.productId,
            productName: item.productName || '未知商品',
            barcode: item.barcode || '',
            sellingPrice: item.sellingPrice || 0,
            quantity: item.quantity || 1
          })
        })

        console.log('购物车更新后:', cartItems.value)

        // 重置选中项
        selectedItemIndex.value = -1

        ElMessage({
          message: `已恢复挂单：${transaction.id}，共${cartItems.value.length}件商品`,
          type: 'success',
          duration: 3000,
          showClose: true
        })
      } else {
        console.warn('恢复的商品列表为空或格式错误:', resumedData.items)
        ElMessage.warning('挂单中没有有效的商品')
      }

      heldTransactionsVisible.value = false

      // 刷新挂单列表
      showHeldTransactions()

      // 检查是否有变化
      if (resumedData.hasChanges) {
        // 构建变化信息
        let changeMessage = '挂单恢复成功，但发现以下变化：\n\n'
        resumedData.changeMessages.forEach((msg: string, index: number) => {
          changeMessage += `${index + 1}. ${msg}\n`
        })

        // 检查金额变化
        if (resumedData.originalAmount && resumedData.totalAmount !== resumedData.originalAmount) {
          changeMessage += `\n💰 金额变化：¥${resumedData.originalAmount} → ¥${resumedData.totalAmount}`
        }

        ElMessageBox.alert(changeMessage, '挂单恢复提示', {
          confirmButtonText: '确定',
          type: 'warning',
          customClass: 'resume-transaction-alert'
        })
      }
    } else {
      console.error('取单失败:', response.data.message)
      ElMessage.error(response.data.message || '恢复挂单失败')
    }
  } catch (error) {
    console.error('恢复挂单失败:', error)
    ElMessage.error('恢复挂单失败，请重试')
  }
}

// 删除挂单
const deleteHeldTransactionItem = async (index: number) => {
  const transaction = heldTransactions.value[index]
  if (!transaction) return

  try {
    await ElMessageBox.confirm(
      `确定要删除挂单 ${transaction.id} 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    const response = await deleteHeldTransaction(transaction.id)
    if (response.data.code === 200) {
      heldTransactions.value.splice(index, 1)
      ElMessage({
        message: '删除挂单成功',
        type: 'success',
        duration: 2000,
        showClose: true
      })
    } else {
      ElMessage.error(response.data.message || '删除挂单失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除挂单失败:', error)
      ElMessage.error('删除挂单失败，请重试')
    }
  }
}

// 打印小票
const printReceipt = (orderNo?: string) => {
  if (orderNo) {
    ElMessage.info(`正在打印小票...订单号：${orderNo}`)
  } else {
    ElMessage.info('正在打印小票...')
  }
  // TODO: 实现小票打印功能
  // 可以调用打印机API或生成PDF等
}

// 开钱箱
const openDrawer = () => {
  ElMessage.info('钱箱已打开')
  // TODO: 实现开钱箱功能
}

// 重打小票
const printLastReceipt = () => {
  ElMessage.info('正在重打上一张小票...')
  // TODO: 实现重打小票功能
}

// 显示销售报表
const showSalesReport = () => {
  // 跳转到销售分析页面
  router.push('/sales')
}

// 显示商品选择对话框
const showProductSelector = () => {
  productSelectorVisible.value = true
  searchProducts()
}

// 获取过期标签类型
const getExpiryTagType = (remainingDays: number | null) => {
  if (remainingDays === null) return 'info'
  if (remainingDays < 0) return 'danger'  // 已过期
  if (remainingDays <= 1) return 'danger' // 即将过期
  if (remainingDays <= 3) return 'warning' // 临期
  return 'success' // 正常
}

// 格式化过期信息
const formatExpiryInfo = (expiryDate: string, remainingDays: number | null) => {
  if (remainingDays === null) return '无过期日期'
  if (remainingDays < 0) return `已过期 ${Math.abs(remainingDays)} 天`
  if (remainingDays === 0) return '今日过期'
  if (remainingDays <= 7) return `${remainingDays} 天后过期`
  return `${expiryDate} 过期`
}

// 检查商品是否启用批次管理
const checkBatchTracking = async (productId: number) => {
  try {
    const response = await fetch(`/api/inventory/batch-tracking/${productId}`)
    const result = await response.json()
    return result.code === 200 ? result.data : false
  } catch (error) {
    console.error('检查批次管理状态失败:', error)
    return false
  }
}

// 加载商品分类
const loadCategories = async () => {
  try {
    // 调用API获取分类列表
    const response = await getCategoryList()

    if (response.data.code === 200) {
      categories.value = response.data.data || []
    } else {
      console.error('加载分类失败:', response.data.message)
      // 使用默认分类作为备选
      categories.value = [
        { id: 1, categoryName: '饮料' },
        { id: 2, categoryName: '零食' },
        { id: 3, categoryName: '日用品' },
        { id: 4, categoryName: '生鲜' }
      ]
    }
  } catch (error) {
    console.error('加载分类失败:', error)
    // 使用默认分类作为备选
    categories.value = [
      { id: 1, categoryName: '饮料' },
      { id: 2, categoryName: '零食' },
      { id: 3, categoryName: '日用品' },
      { id: 4, categoryName: '生鲜' }
    ]
  }
}

// 搜索商品
const searchProducts = async () => {
  try {
    productLoading.value = true

    // 调用API搜索商品
    const response = await getProductList({
      keyword: productSearchForm.keyword,
      categoryId: productSearchForm.categoryId,
      pageNum: productPagination.pageNum,
      pageSize: productPagination.pageSize
    })

    if (response.data.code === 200) {
      const pageData = response.data.data
      productList.value = pageData.records || []
      productPagination.total = pageData.total || 0
    } else {
      ElMessage.error(response.data.message || '搜索商品失败')
      productList.value = []
      productPagination.total = 0
    }

  } catch (error) {
    console.error('搜索商品失败:', error)
    ElMessage.error('搜索商品失败')
    productList.value = []
    productPagination.total = 0
  } finally {
    productLoading.value = false
  }
}

// 重置商品搜索
const resetProductSearch = () => {
  productSearchForm.keyword = ''
  productSearchForm.categoryId = undefined
  productPagination.pageNum = 1
  searchProducts()
}

// 选择商品添加到购物车
const selectProduct = (product: any) => {
  addToCart(product)
  ElMessage.success(`已添加 ${product.productName}`)
}

// 处理商品分页大小变化
const handleProductSizeChange = (size: number) => {
  productPagination.pageSize = size
  productPagination.pageNum = 1
  searchProducts()
}

// 处理商品分页页码变化
const handleProductCurrentChange = (page: number) => {
  productPagination.pageNum = page
  searchProducts()
}

// 获取库存状态样式
const getStockClass = (product: any) => {
  if (product.stockQuantity <= 0) {
    return 'stock-out'
  } else if (product.stockQuantity <= 10) {
    return 'stock-low'
  }
  return 'stock-normal'
}
</script>

<style scoped>
.cashier-container {
  height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 收银台头部 */
.cashier-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 12px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
}

.cashier-info {
  display: flex;
  align-items: center;
}

.cashier-icon {
  font-size: 32px;
  margin-right: 16px;
}

.cashier-details h2 {
  margin: 0 0 2px 0;
  font-size: 18px;
  font-weight: 600;
}

.cashier-details p {
  margin: 0;
  opacity: 0.9;
  font-size: 12px;
}

.header-right {
  text-align: right;
}

.current-time {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 2px;
}

.current-date {
  font-size: 12px;
  opacity: 0.9;
}

.loading-text {
  display: flex;
  align-items: center;
  gap: 6px;
  opacity: 0.8;
}

.loading-text .el-icon {
  font-size: 14px;
}

/* 主要内容区域 */
.cashier-main {
  flex: 1;
  display: flex;
  gap: 12px;
  padding: 12px;
  overflow: hidden;
  height: calc(100vh - 60px);
}

.left-panel {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow: hidden;
  height: 100%;
}

.right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 320px;
  height: 100%;
  overflow: hidden;
}

/* 扫码区域 */
.scan-section {
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
}

.scan-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.scan-icon {
  font-size: 18px;
  color: #409eff;
  margin-right: 8px;
}

.scan-input {
  margin-bottom: 12px;
}

.scan-tips {
  margin-top: 12px;
}

/* 购物车区域 */
.cart-section {
  flex: 1;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #e4e7ed;
  background: #fafbfc;
  flex-shrink: 0;
}

.cart-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.cart-icon {
  font-size: 20px;
  color: #409eff;
  margin-right: 12px;
}

.cart-badge {
  margin-left: 12px;
}

.cart-content {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.empty-cart {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cart-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
  min-height: 0;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cart-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.cart-item.selected {
  border-color: #409eff;
  background: #f0f9ff;
}

.item-info {
  flex: 1;
  margin-right: 16px;
}

.item-name {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.item-details {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.item-price {
  color: #f56c6c;
  font-weight: 600;
}

/* 批次信息样式 */
.batch-info {
  margin-top: 6px;
}

.batch-details {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  align-items: center;
}

.batch-tag {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
}

.batch-tag .el-icon {
  font-size: 10px;
  margin-right: 2px;
}

.expiry-info {
  display: flex;
  align-items: center;
}

.expiry-tag {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
}

.expiry-tag .el-icon {
  font-size: 10px;
  margin-right: 2px;
}

.item-quantity {
  margin-right: 16px;
}

.item-total {
  font-size: 16px;
  font-weight: 600;
  color: #409eff;
  margin-right: 16px;
  min-width: 80px;
  text-align: right;
}

/* 金额显示区域 */
.amount-section {
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
}

.amount-display {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.total-amount {
  text-align: center;
  flex: 1;
}

.amount-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 6px;
}

.amount-value {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
  line-height: 1.2;
}

.item-count {
  text-align: center;
  flex: 1;
}

.count-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.count-value {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
  line-height: 1.2;
}

/* 支付方式区域 */
.payment-section {
  background: white;
  border-radius: 12px;
  padding: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
}

.payment-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.payment-icon {
  font-size: 18px;
  color: #409eff;
  margin-right: 8px;
}

.payment-methods {
  margin-bottom: 12px;
}

.payment-methods .el-radio-group {
  width: 100%;
  display: flex;
}

.payment-methods .el-radio-button {
  flex: 1;
}

.payment-methods .el-radio-button__inner {
  width: 100%;
  padding: 6px 8px;
  font-size: 11px;
}

.cash-payment {
  border-top: 1px solid #e4e7ed;
  padding-top: 12px;
}

.received-amount {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  gap: 6px;
}

.received-amount label {
  font-weight: 600;
  font-size: 12px;
  white-space: nowrap;
}

.change-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 10px;
  background: #f0f9ff;
  border-radius: 6px;
  border: 1px solid #409eff;
}

.change-label {
  font-weight: 600;
  color: #303133;
  font-size: 12px;
}

.change-value {
  font-size: 16px;
  font-weight: bold;
  color: #67c23a;
}

/* 结算区域 */
.checkout-section {
  background: white;
  border-radius: 12px;
  padding: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
}

.checkout-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  border-radius: 8px;
}

.quick-actions {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.quick-actions .el-button {
  flex: 1;
  min-width: 0;
  height: 32px;
  font-size: 12px;
  border-radius: 6px;
}

.quick-actions .el-button .el-icon {
  margin-right: 3px;
  font-size: 12px;
}

/* 快捷功能区域 */
.quick-functions {
  background: white;
  border-radius: 12px;
  padding: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.function-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  flex-shrink: 0;
}

.function-header .el-icon {
  font-size: 16px;
  color: #409eff;
  margin-right: 6px;
}

.function-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.function-buttons .el-button {
  justify-content: flex-start;
  height: 36px;
  padding: 0 12px;
  font-size: 12px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.function-buttons .el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.function-buttons .el-button .el-icon {
  margin-right: 6px;
  font-size: 14px;
}

.function-btn {
  display: flex !important;
  align-items: center !important;
  white-space: nowrap !important;
  overflow: hidden !important;
}

.function-btn span {
  flex: 1;
  text-align: left;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 商品选择对话框样式 */
.product-search {
  margin-bottom: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.product-info {
  display: flex;
  flex-direction: column;
}

.product-name {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.product-barcode {
  font-size: 12px;
  color: #909399;
}

.price {
  color: #f56c6c;
  font-weight: 600;
}

.stock-normal {
  color: #67c23a;
  font-weight: 600;
}

.stock-low {
  color: #e6a23c;
  font-weight: 600;
}

.stock-out {
  color: #f56c6c;
  font-weight: 600;
}

.product-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .right-panel {
    min-width: 300px;
  }

  .amount-value {
    font-size: 22px;
  }

  .count-value {
    font-size: 16px;
  }

  .function-buttons .el-button {
    height: 32px;
    font-size: 11px;
  }
}

@media (max-width: 1200px) {
  .cashier-main {
    flex-direction: row;
    gap: 10px;
    padding: 10px;
  }

  .right-panel {
    min-width: 280px;
    gap: 8px;
  }

  .amount-section,
  .payment-section,
  .checkout-section,
  .quick-functions {
    padding: 12px;
  }

  .quick-actions .el-button {
    height: 28px;
    font-size: 11px;
  }

  .checkout-btn {
    height: 42px;
    font-size: 14px;
  }
}

@media (max-width: 1024px) {
  .cashier-main {
    flex-direction: column;
    height: calc(100vh - 60px);
  }

  .left-panel {
    flex: 1;
    min-height: 60%;
  }

  .right-panel {
    flex: none;
    height: 40%;
    min-width: auto;
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-template-rows: auto auto;
    gap: 8px;
  }

  .amount-section {
    grid-column: 1 / -1;
  }

  .payment-section {
    grid-column: 1;
  }

  .checkout-section {
    grid-column: 2;
  }

  .quick-functions {
    display: none;
  }
}

@media (max-width: 768px) {
  .cashier-header {
    padding: 8px 16px;
    height: 50px;
  }

  .cashier-main {
    height: calc(100vh - 50px);
    padding: 8px;
    gap: 8px;
  }

  .cashier-details h2 {
    font-size: 16px;
  }

  .cashier-details p {
    font-size: 11px;
  }

  .current-time {
    font-size: 14px;
  }

  .current-date {
    font-size: 11px;
  }

  .right-panel {
    height: 35%;
  }

  .left-panel {
    min-height: 65%;
  }

  .payment-methods .el-radio-button__inner {
    padding: 4px 6px;
    font-size: 10px;
  }

  .checkout-btn {
    height: 38px;
    font-size: 13px;
  }

  .quick-actions .el-button {
    height: 26px;
    font-size: 10px;
  }
}

/* 挂单恢复提示框样式 */
:deep(.resume-transaction-alert) {
  .el-message-box__message {
    white-space: pre-line;
    text-align: left;
    font-family: 'Courier New', monospace;
    font-size: 14px;
    line-height: 1.6;
    max-height: 300px;
    overflow-y: auto;
  }

  .el-message-box {
    max-width: 500px;
  }
}
</style>
