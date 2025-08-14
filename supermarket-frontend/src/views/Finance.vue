<template>
  <div class="finance-container">
    <div class="page-header">
      <h1>财务管理</h1>
      <p>管理超市的收支记录、查看财务报表和利润分析</p>
    </div>

    <!-- 财务统计卡片 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stats-card income-card">
            <div class="stats-content">
              <div class="stats-icon">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-value">¥{{ formatAmount(todayStats.todayIncome) }}</div>
                <div class="stats-label">今日收入</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card expense-card">
            <div class="stats-content">
              <div class="stats-icon">
                <el-icon><Money /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-value">¥{{ formatAmount(todayStats.todayExpense) }}</div>
                <div class="stats-label">今日支出</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card profit-card">
            <div class="stats-content">
              <div class="stats-icon">
                <el-icon><Wallet /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-value">¥{{ formatAmount(todayStats.todayNetProfit) }}</div>
                <div class="stats-label">今日净利润</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card rate-card">
            <div class="stats-content">
              <div class="stats-icon">
                <el-icon><DataAnalysis /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ formatPercent(todayStats.profitRate) }}%</div>
                <div class="stats-label">利润率</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 功能导航 -->
    <div class="nav-section">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="nav-card" @click="goToRecords">
            <div class="nav-content">
              <div class="nav-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="nav-info">
                <div class="nav-title">收支记录</div>
                <div class="nav-desc">查看和管理所有收支记录</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="nav-card" @click="goToReports">
            <div class="nav-content">
              <div class="nav-icon">
                <el-icon><PieChart /></el-icon>
              </div>
              <div class="nav-info">
                <div class="nav-title">财务报表</div>
                <div class="nav-desc">查看详细的财务报表和分析</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="nav-card" @click="goToAnalysis">
            <div class="nav-content">
              <div class="nav-icon">
                <el-icon><DataLine /></el-icon>
              </div>
              <div class="nav-info">
                <div class="nav-title">利润分析</div>
                <div class="nav-desc">分析利润趋势和经营状况</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 最近记录 -->
    <div class="recent-section">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>最近记录</span>
            <el-button type="primary" @click="goToRecords">查看全部</el-button>
          </div>
        </template>
        
        <el-table :data="recentRecords" style="width: 100%">
          <el-table-column prop="recordDate" label="日期" width="200">
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
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="operatorName" label="操作员" width="100" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  TrendCharts, Money, Wallet, DataAnalysis, Document,
  PieChart, DataLine, Calendar, Clock
} from '@element-plus/icons-vue'
import { getTodayFinanceStats, getFinanceRecordPage } from '@/api/finance'

const router = useRouter()

// 响应式数据
const todayStats = ref({
  todayIncome: 0,
  todayExpense: 0,
  todayNetProfit: 0,
  profitRate: 0
})

const recentRecords = ref([])

// 页面加载时获取数据
onMounted(() => {
  loadTodayStats()
  loadRecentRecords()
})

// 加载今日统计数据
const loadTodayStats = async () => {
  try {
    const response = await getTodayFinanceStats()
    if (response.data.code === 200) {
      todayStats.value = response.data.data || {
        todayIncome: 0,
        todayExpense: 0,
        todayNetProfit: 0,
        profitRate: 0
      }
    }
  } catch (error) {
    console.error('加载今日统计数据失败:', error)
  }
}

// 加载最近记录
const loadRecentRecords = async () => {
  try {
    const response = await getFinanceRecordPage({
      pageNum: 1,
      pageSize: 10
    })
    if (response.data.code === 200) {
      recentRecords.value = response.data.data.records || []
    }
  } catch (error) {
    console.error('加载最近记录失败:', error)
  }
}

// 导航方法
const goToRecords = () => {
  router.push('/finance/records')
}

const goToReports = () => {
  router.push('/finance/reports')
}

const goToAnalysis = () => {
  router.push('/finance/analysis')
}

// 格式化金额
const formatAmount = (amount: number) => {
  return (amount || 0).toFixed(2)
}

// 格式化百分比
const formatPercent = (percent: number) => {
  return (percent || 0).toFixed(2)
}

// 格式化日期时间
const formatDateTime = (dateTime: string | Date) => {
  if (!dateTime) return ''
  
  try {
    const date = new Date(dateTime)
    // 检查日期是否有效
    if (isNaN(date.getTime())) {
      return dateTime.toString()
    }
    
    // 使用中文格式显示日期时间
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false
    })
  } catch (error) {
    console.error('日期格式化错误:', error)
    return dateTime ? dateTime.toString() : ''
  }
}

// 增强的日期格式化（用于表格显示）
const formatDateEnhanced = (dateTime: string | Date | number[]) => {
  if (!dateTime) return ''

  try {
    let date: Date

    // 处理后端返回的数组格式 [2024, 7, 21, 16, 45]
    if (Array.isArray(dateTime)) {
      const [year, month, day, hour = 0, minute = 0] = dateTime
      date = new Date(year, month - 1, day, hour, minute)
    } else {
      date = new Date(dateTime)
    }

    if (isNaN(date.getTime())) {
      return dateTime.toString()
    }

    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const day = date.getDate()
    const hour = date.getHours()
    const minute = date.getMinutes()

    // 格式化为 2025-7-8-11:00
    return `${year}-${month}-${day}-${hour}:${minute.toString().padStart(2, '0')}`
  } catch (error) {
    console.error('日期格式化错误:', error)
    return dateTime ? dateTime.toString() : ''
  }
}

// 格式化时间（仅时间）
const formatTime = (dateTime: string | Date | number[]) => {
  if (!dateTime) return ''

  try {
    let date: Date

    // 处理后端返回的数组格式 [2024, 7, 21, 16, 45]
    if (Array.isArray(dateTime)) {
      const [year, month, day, hour = 0, minute = 0] = dateTime
      date = new Date(year, month - 1, day, hour, minute)
    } else {
      date = new Date(dateTime)
    }

    if (isNaN(date.getTime())) {
      return ''
    }

    const hour = date.getHours()
    const minute = date.getMinutes()

    // 格式化为 11:00
    return `${hour}:${minute.toString().padStart(2, '0')}`
  } catch (error) {
    console.error('时间格式化错误:', error)
    return ''
  }
}

// 获取相对时间
const getRelativeTime = (dateTime: string | Date | number[]) => {
  if (!dateTime) return ''

  try {
    let date: Date

    // 处理后端返回的数组格式 [2024, 7, 21, 16, 45]
    if (Array.isArray(dateTime)) {
      const [year, month, day, hour = 0, minute = 0] = dateTime
      date = new Date(year, month - 1, day, hour, minute)
    } else {
      date = new Date(dateTime)
    }

    if (isNaN(date.getTime())) {
      return ''
    }

    const now = new Date()
    const diffMs = now.getTime() - date.getTime()
    const diffMinutes = Math.floor(diffMs / (1000 * 60))
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))

    if (diffMinutes < 1) {
      return '刚刚'
    } else if (diffMinutes < 60) {
      return `${diffMinutes}分钟前`
    } else if (diffHours < 24) {
      return `${diffHours}小时前`
    } else if (diffDays < 7) {
      return `${diffDays}天前`
    } else {
      return ''
    }
  } catch (error) {
    console.error('相对时间计算错误:', error)
    return ''
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

.finance-container {
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

  h1 {
    margin: 0 0 12px 0;
    font-size: 32px;
    font-weight: 700;
    color: var(--ios-label);
    letter-spacing: -0.6px;
    line-height: 1.2;
  }

  p {
    margin: 0;
    color: var(--ios-secondary-label);
    font-size: 16px;
    font-weight: 400;
    opacity: 0.8;
  }
}

/* iOS风格统计区域 */
.stats-section {
  margin-bottom: 32px;

  :deep(.el-col) {
    margin-bottom: 20px;
    
    @media (max-width: 768px) {
      margin-bottom: 16px;
    }
  }
}

:deep(.stats-card) {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);
  height: 130px;
  cursor: default;
  transition: all 0.25s cubic-bezier(0.4, 0.0, 0.2, 1);

  .el-card__body {
    padding: 24px;
    height: 100%;
  }

  &:hover {
    transform: translateY(-2px) scale(1.02);
    box-shadow: 
      0 6px 20px rgba(0, 0, 0, 0.08),
      0 3px 10px rgba(0, 0, 0, 0.06);
    background: rgba(255, 255, 255, 0.98);
  }

  &:active {
    transform: scale(0.98);
  }
}

.stats-content {
  display: flex;
  align-items: center;
  height: 100%;
  gap: 20px;
}

.stats-icon {
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

.income-card .stats-icon {
  background: linear-gradient(135deg, #34C759 0%, #30B753 100%);
  color: var(--ios-white);
}

.expense-card .stats-icon {
  background: linear-gradient(135deg, #FF3B30 0%, #E0342E 100%);
  color: var(--ios-white);
}

.profit-card .stats-icon {
  background: linear-gradient(135deg, var(--ios-accent) 0%, var(--ios-secondary) 100%);
  color: var(--ios-white);
}

.rate-card .stats-icon {
  background: linear-gradient(135deg, #FF9500 0%, #E6850E 100%);
  color: var(--ios-white);
}

.stats-info {
  flex: 1;
  min-width: 0;
}

.stats-value {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 6px;
  letter-spacing: -0.4px;
  line-height: 1.1;
}

.income-card .stats-value {
  color: #34C759;
}

.expense-card .stats-value {
  color: #FF3B30;
}

.profit-card .stats-value {
  color: var(--ios-accent);
}

.rate-card .stats-value {
  color: #FF9500;
}

.stats-label {
  color: var(--ios-secondary-label);
  font-size: 16px;
  font-weight: 500;
  opacity: 0.8;
}

/* iOS风格导航区域 */
.nav-section {
  margin-bottom: 32px;
  
  :deep(.el-col) {
    margin-bottom: 20px;
    
    @media (max-width: 768px) {
      margin-bottom: 16px;
    }
  }
}

:deep(.nav-card) {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);
  height: 120px;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0.0, 0.2, 1);

  .el-card__body {
    padding: 24px;
    height: 100%;
  }

  &:hover {
    transform: translateY(-4px) scale(1.02);
    box-shadow: 
      0 8px 24px rgba(0, 0, 0, 0.1),
      0 4px 12px rgba(0, 0, 0, 0.08);
    background: rgba(255, 255, 255, 0.98);

    .nav-icon {
      transform: scale(1.1);
      box-shadow: 0 6px 16px rgba(28, 28, 30, 0.25);
    }
  }

  &:active {
    transform: scale(0.98);
  }
}

.nav-content {
  display: flex;
  align-items: center;
  height: 100%;
  gap: 20px;
}

.nav-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  background: linear-gradient(135deg, var(--ios-accent) 0%, var(--ios-secondary) 100%);
  color: var(--ios-white);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(28, 28, 30, 0.25);
  transition: all 0.25s cubic-bezier(0.4, 0.0, 0.2, 1);
  flex-shrink: 0;
}

.nav-info {
  flex: 1;
  min-width: 0;
}

.nav-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--ios-label);
  margin-bottom: 6px;
  letter-spacing: -0.2px;
}

.nav-desc {
  color: var(--ios-secondary-label);
  font-size: 15px;
  font-weight: 400;
  opacity: 0.8;
  line-height: 1.3;
}

/* iOS风格最近记录区域 */
.recent-section {
  margin-bottom: 32px;

  :deep(.el-card) {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: saturate(180%) blur(20px);
    border-radius: 18px;
    border: 1px solid rgba(255, 255, 255, 0.18);
    box-shadow: 
      0 4px 16px rgba(0, 0, 0, 0.06),
      0 2px 8px rgba(0, 0, 0, 0.04);

    .el-card__header {
      background: transparent;
      border-bottom: 1px solid var(--ios-separator);
      padding: 24px 24px 20px;

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        span {
          font-size: 20px;
          font-weight: 600;
          color: var(--ios-label);
          letter-spacing: -0.3px;
        }

        .el-button {
          height: 36px;
          border-radius: 10px;
          font-size: 14px;
          font-weight: 500;
          padding: 0 16px;
          border: none;
          background: var(--ios-accent);
          color: var(--ios-white);
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
      }
    }

    .el-card__body {
      padding: 24px;
    }

    .el-table {
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
      }

      td {
        border-bottom: 1px solid rgba(199, 199, 204, 0.3);
        padding: 16px 12px;
      }
    }
  }
}

.income-amount {
  color: #34C759;
  font-weight: 600;
  font-size: 16px;
}

.expense-amount {
  color: #FF3B30;
  font-weight: 600;
  font-size: 16px;
}

/* iOS风格增强的日期时间单元格 */
.enhanced-date-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 12px 8px;
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

/* iOS风格标签 */
:deep(.el-tag) {
  border-radius: 8px;
  font-weight: 500;
  font-size: 13px;
  padding: 4px 12px;
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

/* 响应式设计 */
@media (max-width: 1200px) {
  .finance-container {
    padding: 20px;
  }

  :deep(.el-col) {
    &[class*="span-6"] {
      flex: 0 0 50%;
      max-width: 50%;
    }

    &[class*="span-8"] {
      flex: 0 0 100%;
      max-width: 100%;
    }
  }
}

@media (max-width: 768px) {
  .finance-container {
    padding: 16px;
  }

  .page-header {
    padding: 20px;

    h1 {
      font-size: 24px;
    }

    p {
      font-size: 14px;
    }
  }

  :deep(.el-col) {
    &[class*="span-6"],
    &[class*="span-8"] {
      flex: 0 0 100%;
      max-width: 100%;
    }
  }

  .stats-content,
  .nav-content {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .stats-icon,
  .nav-icon {
    width: 48px;
    height: 48px;
    font-size: 20px;
  }

  .stats-value {
    font-size: 24px;
  }

  .nav-title {
    font-size: 16px;
  }

  .nav-desc {
    font-size: 13px;
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
  .finance-container {
    padding: 12px;
  }

  .page-header {
    padding: 16px;

    h1 {
      font-size: 20px;
    }
  }

  :deep(.stats-card),
  :deep(.nav-card) {
    .el-card__body {
      padding: 16px;
    }
  }

  .stats-icon,
  .nav-icon {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }

  .stats-value {
    font-size: 20px;
  }

  .stats-label,
  .nav-title {
    font-size: 14px;
  }

  .nav-desc {
    font-size: 12px;
  }

  :deep(.recent-section .el-card) {
    .el-card__header {
      padding: 16px;

      .card-header span {
        font-size: 18px;
      }
    }

    .el-card__body {
      padding: 16px;
    }
  }
}
</style>
