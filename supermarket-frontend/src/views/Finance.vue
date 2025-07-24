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

<style scoped>
.finance-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 28px;
}

.page-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.stats-section {
  margin-bottom: 30px;
}

.stats-card {
  height: 120px;
  cursor: default;
}

.stats-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stats-icon {
  font-size: 40px;
  margin-right: 20px;
}

.income-card .stats-icon {
  color: #67c23a;
}

.expense-card .stats-icon {
  color: #f56c6c;
}

.profit-card .stats-icon {
  color: #409eff;
}

.rate-card .stats-icon {
  color: #e6a23c;
}

.stats-info {
  flex: 1;
}

.stats-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.income-card .stats-value {
  color: #67c23a;
}

.expense-card .stats-value {
  color: #f56c6c;
}

.profit-card .stats-value {
  color: #409eff;
}

.rate-card .stats-value {
  color: #e6a23c;
}

.stats-label {
  color: #666;
  font-size: 14px;
}

.nav-section {
  margin-bottom: 30px;
}

.nav-card {
  height: 100px;
  cursor: pointer;
  transition: all 0.3s;
}

.nav-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.nav-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.nav-icon {
  font-size: 32px;
  color: #409eff;
  margin-right: 15px;
}

.nav-info {
  flex: 1;
}

.nav-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.nav-desc {
  color: #666;
  font-size: 12px;
}

.recent-section {
  margin-bottom: 30px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.income-amount {
  color: #67c23a;
  font-weight: bold;
}

.expense-amount {
  color: #f56c6c;
  font-weight: bold;
}

/* 增强的日期时间单元格样式 */
.enhanced-date-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px 0;
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
</style>
