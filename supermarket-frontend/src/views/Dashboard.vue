<template>
  <div class="dashboard-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <div class="header-title">
            <el-icon class="header-icon"><Shop /></el-icon>
            <h2>智慧超市管理中心</h2>
          </div>
          <p class="header-desc">{{ currentTime }} | 欢迎回来，{{ currentUser }}</p>
        </div>
        <div class="header-right">
          <el-button type="primary" @click="refreshData" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
          <el-button @click="$router.push('/ai-assistant')">
            <el-icon><ChatDotRound /></el-icon>
            AI助手
          </el-button>
        </div>
      </div>
    </div>

    <!-- 核心数据统计 -->
    <div class="stats-section">
      <div class="header-stats">
        <div class="stat-card">
          <div class="stat-number">¥{{ formatNumber(stats.todaySales) }}</div>
          <div class="stat-label">今日销售额</div>
          <div class="stat-trend up">
            <el-icon><ArrowUp /></el-icon>
            +12.5%
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ stats.totalProducts }}</div>
          <div class="stat-label">商品总数</div>
          <div class="stat-trend up">
            <el-icon><ArrowUp /></el-icon>
            +3
          </div>
        </div>
        <div class="stat-card warning">
          <div class="stat-number">{{ stats.lowStockCount }}</div>
          <div class="stat-label">低库存预警</div>
          <div class="stat-trend down">
            <el-icon><ArrowDown /></el-icon>
            -2
          </div>
        </div>
        <div class="stat-card danger">
          <div class="stat-number">{{ stats.expiringSoonCount }}</div>
          <div class="stat-label">临期商品</div>
          <div class="stat-trend down">
            <el-icon><ArrowDown /></el-icon>
            -1
          </div>
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions-section">
      <div class="section-header">
        <h3 class="section-title">
          <el-icon><Operation /></el-icon>
          快捷操作
        </h3>
      </div>
      <div class="quick-actions">
        <div class="action-item" @click="$router.push('/products')">
          <div class="action-icon">
            <el-icon><Plus /></el-icon>
          </div>
          <div class="action-content">
            <div class="action-title">添加商品</div>
            <div class="action-desc">新增商品信息</div>
          </div>
          <el-icon class="action-arrow"><ArrowRight /></el-icon>
        </div>

        <div class="action-item" @click="$router.push('/cashier')">
          <div class="action-icon">
            <el-icon><Money /></el-icon>
          </div>
          <div class="action-content">
            <div class="action-title">开始收银</div>
            <div class="action-desc">进入收银界面</div>
          </div>
          <el-icon class="action-arrow"><ArrowRight /></el-icon>
        </div>

        <div class="action-item" @click="$router.push('/inventory')">
          <div class="action-icon">
            <el-icon><Goods /></el-icon>
          </div>
          <div class="action-content">
            <div class="action-title">库存管理</div>
            <div class="action-desc">查看库存状态</div>
          </div>
          <el-icon class="action-arrow"><ArrowRight /></el-icon>
        </div>

        <div class="action-item" @click="$router.push('/sales')">
          <div class="action-icon">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="action-content">
            <div class="action-title">销售分析</div>
            <div class="action-desc">查看销售报表</div>
          </div>
          <el-icon class="action-arrow"><ArrowRight /></el-icon>
        </div>
      </div>
    </div>

    <!-- 系统状态 -->
    <div class="status-section">
      <div class="section-header">
        <h3 class="section-title">
          <el-icon><Monitor /></el-icon>
          系统状态
        </h3>
      </div>
      <div class="status-grid">
        <div class="status-item">
          <div class="status-indicator online"></div>
          <div class="status-content">
            <div class="status-title">数据库连接</div>
            <div class="status-desc">运行正常</div>
          </div>
          <div class="status-value">99.9%</div>
        </div>

        <div class="status-item">
          <div class="status-indicator online"></div>
          <div class="status-content">
            <div class="status-title">系统服务</div>
            <div class="status-desc">运行正常</div>
          </div>
          <div class="status-value">100%</div>
        </div>

        <div class="status-item">
          <div class="status-indicator warning"></div>
          <div class="status-content">
            <div class="status-title">存储空间</div>
            <div class="status-desc">使用率较高</div>
          </div>
          <div class="status-value">78%</div>
        </div>

        <div class="status-item">
          <div class="status-indicator online"></div>
          <div class="status-content">
            <div class="status-title">最后更新</div>
            <div class="status-desc">{{ lastUpdateTime }}</div>
          </div>
          <div class="status-badge">实时</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Shop, Refresh, ChatDotRound, Money, Goods,
  ArrowUp, ArrowDown, Operation, Plus, ArrowRight, Monitor,
  TrendCharts
} from '@element-plus/icons-vue'
import { getDashboardStats } from '@/api/dashboard'

const router = useRouter()

// 响应式数据
const stats = ref({
  totalProducts: 0,
  todaySales: 0,
  lowStockCount: 0,
  expiringSoonCount: 0
})

const loading = ref(false)
const currentTime = ref('')
const currentUser = ref('管理员')
const lastUpdateTime = ref('')

// 定时器
let timeInterval: number | null = null

// 计算属性
const formatNumber = (num: number) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}



// 更新当前时间
const updateCurrentTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    weekday: 'long',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 加载仪表板数据
const loadDashboardData = async () => {
  try {
    loading.value = true
    const response = await getDashboardStats()
    stats.value = response.data.data
    lastUpdateTime.value = new Date().toLocaleTimeString('zh-CN')
  } catch (error) {
    console.error('加载仪表板数据失败:', error)
    // 如果API调用失败，使用模拟数据
    stats.value = {
      totalProducts: 156,
      todaySales: 2847.60,
      lowStockCount: 8,
      expiringSoonCount: 3
    }
    lastUpdateTime.value = new Date().toLocaleTimeString('zh-CN')
  } finally {
    loading.value = false
  }
}

// 刷新数据
const refreshData = async () => {
  await loadDashboardData()
}

// 生命周期
onMounted(() => {
  loadDashboardData()
  updateCurrentTime()

  // 每分钟更新时间
  timeInterval = setInterval(updateCurrentTime, 60000)
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
})
</script>

<style scoped lang="scss">
.dashboard-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
}

/* 页面头部 */
.page-header {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .header-left {
    .header-title {
      display: flex;
      align-items: center;
      margin-bottom: 8px;

      .header-icon {
        margin-right: 12px;
        font-size: 24px;
        color: #409eff;
      }

      h2 {
        margin: 0;
        font-size: 24px;
        font-weight: 600;
        color: #303133;
      }
    }

    .header-desc {
      margin: 0;
      color: #909399;
      font-size: 14px;
    }
  }

  .header-right {
    display: flex;
    gap: 12px;
  }


}

/* 统计卡片区域 */
.stats-section {
  margin-bottom: 24px;

  .header-stats {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
  }
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  .stat-number {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 8px;
  }

  .stat-label {
    font-size: 14px;
    color: #909399;
    margin-bottom: 8px;
  }

  .stat-trend {
    font-size: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;

    &.up {
      color: #67c23a;
    }

    &.down {
      color: #f56c6c;
    }
  }

  &.warning {
    .stat-number {
      color: #e6a23c;
    }
  }

  &.danger {
    .stat-number {
      color: #f56c6c;
    }
  }
}



/* 快捷操作区域 */
.quick-actions-section {
  margin-bottom: 24px;

  .section-header {
    margin-bottom: 16px;

    .section-title {
      display: flex;
      align-items: center;
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: #303133;

      .el-icon {
        margin-right: 8px;
        color: #409eff;
      }
    }
  }

  .quick-actions {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 16px;
  }
}

.action-item {
  background: white;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  .action-icon {
    width: 40px;
    height: 40px;
    border-radius: 8px;
    background: #409eff;
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 12px;
    font-size: 18px;
  }

  .action-content {
    flex: 1;

    .action-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 4px;
    }

    .action-desc {
      font-size: 14px;
      color: #909399;
    }
  }

  .action-arrow {
    color: #c0c4cc;
    transition: all 0.3s ease;
  }

  &:hover .action-arrow {
    color: #409eff;
    transform: translateX(4px);
  }
}

/* 系统状态区域 */
.status-section {
  .section-header {
    margin-bottom: 16px;

    .section-title {
      display: flex;
      align-items: center;
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: #303133;

      .el-icon {
        margin-right: 8px;
        color: #409eff;
      }
    }
  }

  .status-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
  }
}

.status-item {
  background: white;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .status-indicator {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    margin-right: 12px;

    &.online {
      background: #67c23a;
    }

    &.warning {
      background: #e6a23c;
    }

    &.offline {
      background: #f56c6c;
    }
  }

  .status-content {
    flex: 1;

    .status-title {
      font-size: 14px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 4px;
    }

    .status-desc {
      font-size: 12px;
      color: #909399;
    }
  }

  .status-value {
    font-size: 14px;
    font-weight: 600;
    color: #67c23a;
  }

  .status-badge {
    background: #f0f9ff;
    color: #409eff;
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;
  }
}



/* 响应式设计 */

/* 响应式设计 */
@media (max-width: 1200px) {
  .page-header {
    .header-content {
      flex-direction: column;
      gap: 16px;
      text-align: center;
    }
  }

  .header-stats {
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    gap: 12px;
  }

  .quick-actions {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .status-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }

  .page-header {
    padding: 16px;

    .header-left .header-title h2 {
      font-size: 20px;
    }

    .header-right {
      flex-direction: column;
      width: 100%;

      .el-button {
        width: 100%;
      }
    }
  }

  .header-stats {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 12px;
  }

  .stat-card {
    padding: 16px;

    .stat-number {
      font-size: 24px;
    }

    .stat-label {
      font-size: 13px;
    }
  }
}

@media (max-width: 480px) {
  .dashboard-container {
    padding: 12px;
  }

  .page-header {
    padding: 12px;

    .header-left .header-title h2 {
      font-size: 18px;
    }
  }

  .header-stats {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .stat-card {
    padding: 12px;

    .stat-number {
      font-size: 20px;
    }

    .stat-label {
      font-size: 12px;
    }
  }

  .action-item {
    padding: 12px;

    .action-icon {
      width: 32px;
      height: 32px;
      font-size: 16px;
    }

    .action-content {
      .action-title {
        font-size: 14px;
      }

      .action-desc {
        font-size: 12px;
      }
    }
  }

  .status-item {
    padding: 12px;

    .status-content {
      .status-title {
        font-size: 13px;
      }

      .status-desc {
        font-size: 11px;
      }
    }
  }
}
</style>
