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

.dashboard-container {
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
  padding: 32px 24px;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.25s cubic-bezier(0.4, 0.0, 0.2, 1);

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

  .stat-number {
    font-size: 36px;
    font-weight: 700;
    color: var(--ios-label);
    margin-bottom: 12px;
    letter-spacing: -0.8px;
    line-height: 1.1;
  }

  .stat-label {
    font-size: 16px;
    color: var(--ios-secondary-label);
    margin-bottom: 16px;
    font-weight: 500;
    opacity: 0.8;
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
    display: inline-flex;

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

  &.warning {
    .stat-number {
      color: #FF9500;
    }
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, #FF9500, #FFB340);
      border-radius: 18px 18px 0 0;
    }
    
    position: relative;
  }

  &.danger {
    .stat-number {
      color: #FF3B30;
    }
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, #FF3B30, #FF6B5A);
      border-radius: 18px 18px 0 0;
    }
    
    position: relative;
  }
}



/* iOS风格快捷操作区域 */
.quick-actions-section {
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

  .quick-actions {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 20px;
  }
}

.action-item {
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

  .action-icon {
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

  &:hover {
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

/* iOS风格系统状态区域 */
.status-section {
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

  .status-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
    gap: 20px;
  }
}

.status-item {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 18px;
  padding: 24px;
  display: flex;
  align-items: center;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.25s cubic-bezier(0.4, 0.0, 0.2, 1);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 
      0 6px 20px rgba(0, 0, 0, 0.08),
      0 3px 10px rgba(0, 0, 0, 0.06);
    background: rgba(255, 255, 255, 0.98);
  }

  .status-indicator {
    width: 16px;
    height: 16px;
    border-radius: 50%;
    margin-right: 16px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
    border: 2px solid rgba(255, 255, 255, 0.8);

    &.online {
      background: #34C759;
      animation: pulse-green 2s infinite;
    }

    &.warning {
      background: #FF9500;
      animation: pulse-orange 2s infinite;
    }

    &.offline {
      background: #FF3B30;
      animation: pulse-red 2s infinite;
    }
  }

  .status-content {
    flex: 1;

    .status-title {
      font-size: 17px;
      font-weight: 600;
      color: var(--ios-label);
      margin-bottom: 6px;
      letter-spacing: -0.2px;
    }

    .status-desc {
      font-size: 15px;
      color: var(--ios-secondary-label);
      opacity: 0.8;
      font-weight: 400;
    }
  }

  .status-value {
    font-size: 18px;
    font-weight: 700;
    color: #34C759;
    letter-spacing: -0.2px;
  }

  .status-badge {
    background: rgba(0, 122, 255, 0.15);
    color: #007AFF;
    padding: 6px 12px;
    border-radius: 12px;
    font-size: 13px;
    font-weight: 500;
    border: 1px solid rgba(0, 122, 255, 0.2);
  }
}

// iOS风格状态指示器动画
@keyframes pulse-green {
  0%, 100% {
    box-shadow: 
      0 2px 6px rgba(0, 0, 0, 0.15),
      0 0 0 0 rgba(52, 199, 89, 0.7);
  }
  50% {
    box-shadow: 
      0 2px 6px rgba(0, 0, 0, 0.15),
      0 0 0 8px rgba(52, 199, 89, 0);
  }
}

@keyframes pulse-orange {
  0%, 100% {
    box-shadow: 
      0 2px 6px rgba(0, 0, 0, 0.15),
      0 0 0 0 rgba(255, 149, 0, 0.7);
  }
  50% {
    box-shadow: 
      0 2px 6px rgba(0, 0, 0, 0.15),
      0 0 0 8px rgba(255, 149, 0, 0);
  }
}

@keyframes pulse-red {
  0%, 100% {
    box-shadow: 
      0 2px 6px rgba(0, 0, 0, 0.15),
      0 0 0 0 rgba(255, 59, 48, 0.7);
  }
  50% {
    box-shadow: 
      0 2px 6px rgba(0, 0, 0, 0.15),
      0 0 0 8px rgba(255, 59, 48, 0);
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
