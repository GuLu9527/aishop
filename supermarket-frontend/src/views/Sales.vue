<template>
  <div class="sales-analysis-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">
          <el-icon><TrendCharts /></el-icon>
          销售分析
        </h1>
        <p class="page-subtitle">实时销售数据分析与统计报表</p>
      </div>
      <div class="header-right">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="handleDateChange"
          class="date-picker"
        />
        <el-button type="primary" @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
        <el-button type="success" @click="exportData" :loading="exportLoading">
          <el-icon><Download /></el-icon>
          导出报表
        </el-button>
      </div>
    </div>

    <!-- 实时数据展示 -->
    <el-card class="realtime-card" shadow="hover" style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">
          <span style="font-weight: bold; color: #303133;">📊 实时销售数据</span>
          <el-button type="text" @click="refreshRealTimeData" :loading="realTimeLoading">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="realtime-item">
            <div class="realtime-label">今日销售额</div>
            <div class="realtime-value primary">¥{{ formatNumber(realTimeData.todaySales || 0) }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="realtime-item">
            <div class="realtime-label">当前小时销售</div>
            <div class="realtime-value success">¥{{ formatNumber(realTimeData.currentHourSales || 0) }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="realtime-item">
            <div class="realtime-label">最后更新</div>
            <div class="realtime-value info">{{ formatTime(realTimeData.updateTime) }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 数据概览卡片 -->
    <div class="overview-cards">
      <div class="overview-card">
        <div class="card-icon sales">
          <el-icon><Money /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">¥{{ formatNumber(safeNumber(overviewData.totalSales)) }}</div>
          <div class="card-label">总销售额</div>
          <div class="card-trend" :class="safeNumber(overviewData.salesTrend) > 0 ? 'up' : 'down'">
            <el-icon>
              <ArrowUp v-if="safeNumber(overviewData.salesTrend) > 0" />
              <ArrowDown v-else />
            </el-icon>
            {{ Math.abs(safeNumber(overviewData.salesTrend)).toFixed(1) }}%
          </div>
        </div>
      </div>

      <div class="overview-card">
        <div class="card-icon orders">
          <el-icon><Document /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ formatNumber(safeNumber(overviewData.totalOrders)) }}</div>
          <div class="card-label">订单数量</div>
          <div class="card-trend" :class="safeNumber(overviewData.ordersTrend) > 0 ? 'up' : 'down'">
            <el-icon>
              <ArrowUp v-if="safeNumber(overviewData.ordersTrend) > 0" />
              <ArrowDown v-else />
            </el-icon>
            {{ Math.abs(safeNumber(overviewData.ordersTrend)).toFixed(1) }}%
          </div>
        </div>
      </div>

      <div class="overview-card">
        <div class="card-icon products">
          <el-icon><Box /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ formatNumber(safeNumber(overviewData.totalProducts)) }}</div>
          <div class="card-label">商品销量</div>
          <div class="card-trend" :class="safeNumber(overviewData.productsTrend) > 0 ? 'up' : 'down'">
            <el-icon>
              <ArrowUp v-if="safeNumber(overviewData.productsTrend) > 0" />
              <ArrowDown v-else />
            </el-icon>
            {{ Math.abs(safeNumber(overviewData.productsTrend)).toFixed(1) }}%
          </div>
        </div>
      </div>

      <div class="overview-card">
        <div class="card-icon avg">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">¥{{ formatNumber(safeNumber(overviewData.avgOrderValue)) }}</div>
          <div class="card-label">客单价</div>
          <div class="card-trend" :class="safeNumber(overviewData.avgTrend) > 0 ? 'up' : 'down'">
            <el-icon>
              <ArrowUp v-if="safeNumber(overviewData.avgTrend) > 0" />
              <ArrowDown v-else />
            </el-icon>
            {{ Math.abs(safeNumber(overviewData.avgTrend)).toFixed(1) }}%
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <!-- 销售趋势图 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>销售趋势</h3>
          <el-radio-group v-model="salesTrendPeriod" @change="updateSalesTrend">
            <el-radio-button value="day">日</el-radio-button>
            <el-radio-button value="week">周</el-radio-button>
            <el-radio-button value="month">月</el-radio-button>
          </el-radio-group>
        </div>
        <div class="chart-content">
          <v-chart
            v-if="salesTrendOption && Object.keys(salesTrendOption).length > 0"
            class="chart"
            :option="salesTrendOption"
            :loading="chartLoading.salesTrend"
            autoresize
          />
          <div v-else-if="!chartLoading.salesTrend" class="empty-chart">
            <el-empty description="暂无销售趋势数据" />
          </div>
        </div>
      </div>

      <!-- 商品分类销售占比 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>商品分类销售占比</h3>
          <el-switch
            v-model="showCategoryValues"
            active-text="显示数值"
            @change="updateCategoryChart"
          />
        </div>
        <div class="chart-content">
          <v-chart
            class="chart"
            :option="categoryPieOption"
            :loading="chartLoading.categoryPie"
            autoresize
          />
        </div>
      </div>
    </div>

    <!-- 第二行图表 -->
    <div class="charts-container">
      <!-- 热销商品排行 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>热销商品排行</h3>
          <el-select v-model="topProductsCount" @change="updateTopProducts">
            <el-option label="Top 5" :value="5" />
            <el-option label="Top 10" :value="10" />
            <el-option label="Top 20" :value="20" />
          </el-select>
        </div>
        <div class="chart-content">
          <v-chart
            class="chart"
            :option="topProductsOption"
            :loading="chartLoading.topProducts"
            autoresize
          />
        </div>
      </div>

      <!-- 收银员业绩对比 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>收银员业绩对比</h3>
          <el-radio-group v-model="performanceMetric" @change="updatePerformanceChart">
            <el-radio-button value="sales">销售额</el-radio-button>
            <el-radio-button value="orders">订单数</el-radio-button>
          </el-radio-group>
        </div>
        <div class="chart-content">
          <v-chart
            class="chart"
            :option="performanceOption"
            :loading="chartLoading.performance"
            autoresize
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  TrendCharts, Refresh, Money, Document, Box,
  ArrowUp, ArrowDown, Download
} from '@element-plus/icons-vue'
import {
  getSalesOverview,
  getSalesTrend,
  getCategorySales,
  getTopProducts,
  getCashierPerformance,
  getTimeAnalysis,
  exportSalesReport,
  getRealTimeSales,
  getStockAlert
} from '@/api/sales'

// ECharts imports
import { use } from "echarts/core"
import { CanvasRenderer } from "echarts/renderers"
import { LineChart, BarChart, PieChart } from "echarts/charts"
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from "echarts/components"
import VChart from "vue-echarts"

use([
  CanvasRenderer,
  LineChart,
  BarChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// 响应式数据
const loading = ref(false)
const exportLoading = ref(false)
const realTimeLoading = ref(false)
const dateRange = ref([
  new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
  new Date().toISOString().split('T')[0]
])

// 实时数据
const realTimeData = ref({
  todaySales: 0,
  currentHourSales: 0,
  updateTime: null
})

// 控制参数
const salesTrendPeriod = ref('day')
const showCategoryValues = ref(true)
const topProductsCount = ref(10)
const performanceMetric = ref('sales')

// 图表加载状态
const chartLoading = reactive({
  salesTrend: false,
  categoryPie: false,
  topProducts: false,
  performance: false
})

// 概览数据
const overviewData = reactive({
  totalSales: 0,
  totalOrders: 0,
  totalProducts: 0,
  avgOrderValue: 0,
  salesTrend: 0,
  ordersTrend: 0,
  productsTrend: 0,
  avgTrend: 0
})

// 图表配置
const salesTrendOption = ref({
  title: { text: '销售趋势', left: 'center' },
  tooltip: { trigger: 'axis' },
  xAxis: { type: 'category', data: [] },
  yAxis: { type: 'value', name: '销售额(元)' },
  series: [{ name: '销售额', type: 'line', data: [], smooth: true }]
})

const categoryPieOption = ref({
  title: { text: '商品分类销售', left: 'center' },
  tooltip: { trigger: 'item' },
  legend: { orient: 'vertical', left: 'left' },
  series: [{ name: '销售额', type: 'pie', radius: ['40%', '70%'], data: [] }]
})

const topProductsOption = ref({
  title: { text: '热销商品排行', left: 'center' },
  tooltip: { trigger: 'axis' },
  xAxis: { type: 'value' },
  yAxis: { type: 'category', data: [] },
  series: [{ name: '销量', type: 'bar', data: [] }]
})

const performanceOption = ref({
  title: { text: '收银员业绩', left: 'center' },
  tooltip: { trigger: 'axis' },
  xAxis: { type: 'category', data: [] },
  yAxis: { type: 'value' },
  series: [{ name: '销售额', type: 'bar', data: [] }]
})

// 工具函数
const formatNumber = (num: number | string) => {
  const numValue = typeof num === 'string' ? parseFloat(num) : num
  if (isNaN(numValue)) return '0'

  if (numValue >= 10000) {
    return (numValue / 10000).toFixed(1) + '万'
  }
  return numValue.toLocaleString()
}

const formatTime = (time: any) => {
  console.log('formatTime 输入:', time, '类型:', typeof time) // 调试日志

  if (!time) {
    console.log('formatTime: 时间为空')
    return '--:--'
  }

  // 如果已经是格式化的字符串（yyyy-MM-dd HH:mm:ss），直接提取时间部分
  if (typeof time === 'string' && time.includes(' ')) {
    const timePart = time.split(' ')[1]
    if (timePart && timePart.includes(':')) {
      console.log('formatTime: 直接使用时间部分:', timePart)
      return timePart
    }
  }

  let date: Date

  // 处理不同的时间格式
  if (time instanceof Date) {
    date = time
  } else if (typeof time === 'string') {
    // 处理字符串格式的时间
    date = new Date(time)
  } else if (typeof time === 'object' && time !== null) {
    // 处理对象格式的时间（可能是LocalDateTime的JSON格式）
    if (Array.isArray(time) && time.length >= 6) {
      // [2025, 7, 21, 16, 45, 30] 格式
      date = new Date(time[0], time[1] - 1, time[2], time[3], time[4], time[5])
    } else {
      // 尝试转换为字符串再解析
      date = new Date(String(time))
    }
  } else {
    date = new Date(time)
  }

  console.log('formatTime 解析后的date:', date, '有效性:', !isNaN(date.getTime()))

  if (isNaN(date.getTime())) {
    console.log('formatTime: 日期解析失败')
    return '--:--'
  }

  const result = date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  })

  console.log('formatTime 结果:', result)
  return result
}

// 安全获取数值
const safeNumber = (value: any, defaultValue: number = 0): number => {
  if (value === null || value === undefined) return defaultValue
  const num = typeof value === 'string' ? parseFloat(value) : Number(value)
  return isNaN(num) ? defaultValue : num
}

// 错误处理工具
const handleApiError = (error: any, operation: string) => {
  console.error(`${operation}失败:`, error)
  const message = error?.response?.data?.message || error?.message || `${operation}失败`
  ElMessage({
    message,
    type: 'error',
    duration: 3000,
    showClose: true
  })
}

// 数据验证工具
const validateDateRange = () => {
  if (!dateRange.value || !dateRange.value[0] || !dateRange.value[1]) {
    ElMessage({
      message: '请选择有效的日期范围',
      type: 'warning',
      duration: 3000,
      showClose: true
    })
    return false
  }

  const start = new Date(dateRange.value[0])
  const end = new Date(dateRange.value[1])

  if (start > end) {
    ElMessage({
      message: '开始日期不能大于结束日期',
      type: 'warning',
      duration: 3000,
      showClose: true
    })
    return false
  }

  // 限制查询范围不超过1年
  const diffTime = Math.abs(end.getTime() - start.getTime())
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

  if (diffDays > 365) {
    ElMessage({
      message: '查询时间范围不能超过1年',
      type: 'warning',
      duration: 3000,
      showClose: true
    })
    return false
  }

  return true
}

// 事件处理
const handleDateChange = () => {
  if (!validateDateRange()) {
    return
  }
  console.log('日期变更:', dateRange.value)
  refreshData()
}

const refreshData = async () => {
  if (!validateDateRange()) {
    return
  }

  loading.value = true
  try {
    // 调用真实API获取概览数据
    const overviewResponse = await getSalesOverview({
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    })

    console.log('概览数据API响应:', overviewResponse.data)

    if (overviewResponse.data.code === 200 || overviewResponse.data.success) {
      const data = overviewResponse.data.data
      if (data) {
        Object.assign(overviewData, data)
        console.log('概览数据更新成功:', overviewData)
      } else {
        console.warn('概览数据为空')
      }
    } else {
      throw new Error(overviewResponse.data.message || '获取概览数据失败')
    }

    // 初始化图表数据
    await initCharts()

    ElMessage({
      message: '数据刷新成功',
      type: 'success',
      duration: 2000,
      showClose: true
    })
  } catch (error) {
    handleApiError(error, '刷新数据')
  } finally {
    loading.value = false
  }
}

// 初始化图表数据
const initCharts = async () => {
  try {
    // 并行初始化所有图表
    await Promise.allSettled([
      updateSalesTrend(),
      updateCategoryChart(),
      updateTopProducts(),
      updatePerformanceChart()
    ])
  } catch (error) {
    console.error('初始化图表失败:', error)
    ElMessage({
      message: '初始化图表失败，请稍后重试',
      type: 'error',
      duration: 4000,
      showClose: true
    })
    // 重置加载状态
    Object.keys(chartLoading).forEach(key => {
      chartLoading[key as keyof typeof chartLoading] = false
    })
  }
}

// 通用图表更新方法
const updateChart = async (
  chartType: keyof typeof chartLoading,
  apiCall: () => Promise<any>,
  optionBuilder: (data: any) => any
) => {
  console.log(`更新${chartType}图表`)
  chartLoading[chartType] = true
  try {
    const response = await apiCall()
    console.log(`${chartType}图表API响应:`, response.data)

    // 检查响应格式
    if (response.data.code === 200 || response.data.success) {
      const data = response.data.data
      // 数据验证
      if (!data) {
        console.warn(`${chartType}图表数据为空`)
        // 设置空数据的默认图表
        const optionRef = getChartOptionRef(chartType)
        optionRef.value = optionBuilder({ data: [], dates: [], salesData: [], products: [], cashiers: [] })
        return
      }

      const optionRef = getChartOptionRef(chartType)
      const newOption = optionBuilder(data)
      optionRef.value = newOption
      console.log(`${chartType}图表更新成功`, { data, newOption })
    } else {
      throw new Error(response.data.message || `获取${chartType}数据失败`)
    }
  } catch (error) {
    console.error(`更新${chartType}图表失败:`, error)
    handleApiError(error, `更新${chartType}图表`)

    // 设置错误状态的默认图表
    const optionRef = getChartOptionRef(chartType)
    optionRef.value = optionBuilder({ data: [], dates: [], salesData: [], products: [], cashiers: [] })
  } finally {
    chartLoading[chartType] = false
  }
}

// 获取图表配置引用
const getChartOptionRef = (chartType: keyof typeof chartLoading) => {
  const optionMap = {
    salesTrend: salesTrendOption,
    categoryPie: categoryPieOption,
    topProducts: topProductsOption,
    performance: performanceOption
  }
  return optionMap[chartType]
}

// 图表配置构建器
const chartOptionBuilders = {
  salesTrend: (data: any) => ({
    title: {
      text: '销售趋势分析',
      left: 'center',
      textStyle: {
        fontSize: 18,
        fontWeight: 'bold',
        color: '#303133'
      }
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(50, 50, 50, 0.9)',
      borderColor: '#409EFF',
      borderWidth: 1,
      textStyle: {
        color: '#fff'
      },
      formatter: (params: any) => {
        const param = params[0]
        return `
          <div style="padding: 8px;">
            <div style="font-weight: bold; margin-bottom: 4px;">${param.name}</div>
            <div style="color: #67C23A;">销售额: ¥${formatNumber(param.value)}</div>
          </div>
        `
      }
    },
    grid: {
      left: '5%',
      right: '5%',
      bottom: '10%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.dates || [],
      boundaryGap: false,
      axisLine: {
        lineStyle: {
          color: '#E4E7ED'
        }
      },
      axisLabel: {
        color: '#606266',
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      name: '销售额(元)',
      nameTextStyle: {
        color: '#909399',
        fontSize: 12
      },
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#606266',
        fontSize: 12,
        formatter: (value: number) => `¥${formatNumber(value)}`
      },
      splitLine: {
        lineStyle: {
          color: '#F2F6FC',
          type: 'dashed'
        }
      }
    },
    series: [{
      name: '销售额',
      type: 'line',
      data: data.salesData || [],
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: {
        width: 4,
        color: '#409EFF',
        shadowColor: 'rgba(64, 158, 255, 0.3)',
        shadowBlur: 10,
        shadowOffsetY: 3
      },
      itemStyle: {
        color: '#409EFF',
        borderColor: '#fff',
        borderWidth: 2,
        shadowColor: 'rgba(64, 158, 255, 0.5)',
        shadowBlur: 10
      },
      areaStyle: {
        opacity: 0.6,
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(64, 158, 255, 0.4)' },
            { offset: 0.5, color: 'rgba(64, 158, 255, 0.2)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ]
        }
      },
      emphasis: {
        focus: 'series',
        itemStyle: {
          color: '#67C23A',
          borderColor: '#fff',
          borderWidth: 3,
          shadowColor: 'rgba(103, 194, 58, 0.5)',
          shadowBlur: 15
        }
      }
    }]
  }),

  categoryPie: (data: any) => ({
    title: {
      text: '商品分类销售占比',
      left: 'center',
      textStyle: { fontSize: 16, fontWeight: 'bold' }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: ¥{c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'middle',
      data: data.data?.map((item: any) => item.name) || []
    },
    series: [{
      name: '销售额',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['60%', '50%'],
      data: data.data?.map((item: any) => ({
        value: item.value,
        name: item.name
      })) || [],
      label: {
        show: showCategoryValues.value,
        formatter: '{b}: {d}%'
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }),

  topProducts: (data: any) => ({
    title: {
      text: '热销商品排行榜',
      left: 'center',
      textStyle: {
        fontSize: 18,
        fontWeight: 'bold',
        color: '#303133'
      }
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(50, 50, 50, 0.9)',
      borderColor: '#67C23A',
      borderWidth: 1,
      textStyle: {
        color: '#fff'
      },
      formatter: (params: any) => {
        const param = params[0]
        const index = data.products?.findIndex((item: any) => item.productName === param.name) + 1
        return `
          <div style="padding: 8px;">
            <div style="font-weight: bold; margin-bottom: 4px;">🏆 第${index}名</div>
            <div style="color: #67C23A; margin-bottom: 2px;">${param.name}</div>
            <div style="color: #E6A23C;">销量: ${param.value}件</div>
          </div>
        `
      }
    },
    grid: {
      left: '5%',
      right: '8%',
      bottom: '8%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      name: '销量(件)',
      nameTextStyle: {
        color: '#909399',
        fontSize: 12
      },
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#606266',
        fontSize: 12
      },
      splitLine: {
        lineStyle: {
          color: '#F2F6FC',
          type: 'dashed'
        }
      }
    },
    yAxis: {
      type: 'category',
      data: data.products?.map((item: any) => item.productName).reverse() || [],
      axisLine: {
        lineStyle: {
          color: '#E4E7ED'
        }
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        interval: 0,
        color: '#606266',
        fontSize: 12,
        formatter: (value: string) => value.length > 10 ? value.substring(0, 10) + '...' : value
      }
    },
    series: [{
      name: '销量',
      type: 'bar',
      data: data.products?.map((item: any, index: number) => ({
        value: item.quantity,
        itemStyle: {
          color: index === 0 ? '#FFD700' : // 金色 - 第一名
                index === 1 ? '#C0C0C0' : // 银色 - 第二名
                index === 2 ? '#CD7F32' : // 铜色 - 第三名
                '#67C23A' // 绿色 - 其他
        }
      })).reverse() || [],
      itemStyle: {
        borderRadius: [0, 6, 6, 0],
        shadowColor: 'rgba(0, 0, 0, 0.1)',
        shadowBlur: 5,
        shadowOffsetX: 2
      },
      label: {
        show: true,
        position: 'right',
        color: '#303133',
        fontSize: 12,
        fontWeight: 'bold',
        formatter: (params: any) => {
          const rank = data.products?.length - params.dataIndex
          const medal = rank === 1 ? '🥇' : rank === 2 ? '🥈' : rank === 3 ? '🥉' : '📦'
          return `${medal} ${params.value}件`
        }
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 15,
          shadowOffsetX: 3,
          shadowColor: 'rgba(0, 0, 0, 0.2)'
        }
      }
    }]
  }),

  performance: (data: any) => ({
    title: {
      text: '收银员业绩对比',
      left: 'center',
      textStyle: { fontSize: 16, fontWeight: 'bold' }
    },
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const param = params[0]
        const unit = performanceMetric.value === 'sales' ? '元' : '单'
        const value = performanceMetric.value === 'sales' ? `¥${formatNumber(param.value)}` : param.value
        return `${param.name}<br/>${param.seriesName}: ${value}${unit}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.cashiers?.map((item: any) => item.cashierName) || [],
      axisLabel: {
        interval: 0,
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: performanceMetric.value === 'sales' ? '销售额(元)' : '订单数(单)',
      axisLabel: {
        formatter: (value: number) => {
          return performanceMetric.value === 'sales' ? `¥${formatNumber(value)}` : value.toString()
        }
      }
    },
    series: [{
      name: performanceMetric.value === 'sales' ? '销售额' : '订单数',
      type: 'bar',
      data: data.cashiers?.map((item: any) =>
        performanceMetric.value === 'sales' ? item.sales : item.orders
      ) || [],
      itemStyle: { color: '#409EFF' },
      label: {
        show: true,
        position: 'top',
        formatter: (params: any) => {
          if (performanceMetric.value === 'sales') {
            return `¥${formatNumber(params.value)}`
          }
          return params.value.toString()
        }
      }
    }]
  })
}

// 重构后的图表更新方法
const updateSalesTrend = () => updateChart(
  'salesTrend',
  () => getSalesTrend({
    startDate: dateRange.value[0],
    endDate: dateRange.value[1],
    period: salesTrendPeriod.value as 'day' | 'week' | 'month'
  }),
  chartOptionBuilders.salesTrend
)

const updateCategoryChart = () => updateChart(
  'categoryPie',
  () => getCategorySales({
    startDate: dateRange.value[0],
    endDate: dateRange.value[1]
  }),
  chartOptionBuilders.categoryPie
)

const updateTopProducts = () => updateChart(
  'topProducts',
  () => getTopProducts({
    startDate: dateRange.value[0],
    endDate: dateRange.value[1],
    limit: topProductsCount.value
  }),
  chartOptionBuilders.topProducts
)

const updatePerformanceChart = () => updateChart(
  'performance',
  () => getCashierPerformance({
    startDate: dateRange.value[0],
    endDate: dateRange.value[1],
    metric: performanceMetric.value as 'sales' | 'orders'
  }),
  chartOptionBuilders.performance
)

// 刷新实时数据
const refreshRealTimeData = async () => {
  realTimeLoading.value = true
  try {
    const response = await getRealTimeSales()
    if (response.data.code === 200) {
      const data = response.data.data
      console.log('实时数据API响应:', data) // 调试日志
      console.log('updateTime 原始值:', data.updateTime, '类型:', typeof data.updateTime) // 调试日志

      realTimeData.value = {
        todaySales: data.today?.total_sales || 0,
        currentHourSales: data.currentHour?.hourSales || 0,
        updateTime: data.updateTimeFormatted || data.updateTime
      }
      console.log('实时数据更新:', realTimeData.value) // 调试日志
      console.log('更新后的 updateTime:', realTimeData.value.updateTime) // 调试日志
    }
  } catch (error) {
    handleApiError(error, '获取实时数据')
  } finally {
    realTimeLoading.value = false
  }
}

// 导出数据
const exportData = async () => {
  exportLoading.value = true
  try {
    const response = await exportSalesReport({
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      type: 'detail'
    })

    if (response.data.code === 200) {
      // 处理导出数据
      const data = response.data.data
      if (data && data.length > 0) {
        // 转换为CSV格式并下载
        downloadCSV(data, `销售报表_${dateRange.value[0]}_${dateRange.value[1]}.csv`)
        ElMessage.success('报表导出成功')
      } else {
        ElMessage.warning('暂无数据可导出')
      }
    } else {
      throw new Error(response.data.message || '导出失败')
    }
  } catch (error) {
    handleApiError(error, '导出报表')
  } finally {
    exportLoading.value = false
  }
}

// CSV下载工具函数
const downloadCSV = (data: any[], filename: string) => {
  if (!data || data.length === 0) return

  // 获取表头
  const headers = Object.keys(data[0])
  const csvContent = [
    headers.join(','),
    ...data.map(row => headers.map(header => `"${row[header] || ''}"`).join(','))
  ].join('\n')

  // 创建下载链接
  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  link.setAttribute('href', url)
  link.setAttribute('download', filename)
  link.style.visibility = 'hidden'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// 生命周期
onMounted(async () => {
  await refreshData()
  await refreshRealTimeData()

  // 设置定时刷新实时数据（每30秒）
  setInterval(refreshRealTimeData, 30000)
})
</script>

<style scoped>
.sales-analysis-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header-left .page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.header-left .page-subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.date-picker {
  width: 300px;
}

/* 实时数据卡片样式 */
.realtime-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.realtime-item {
  text-align: center;
  padding: 16px;

  .realtime-label {
    font-size: 14px;
    color: #909399;
    margin-bottom: 8px;
  }

  .realtime-value {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 4px;

    &.primary {
      color: #409EFF;
    }

    &.success {
      color: #67C23A;
    }

    &.info {
      color: #909399;
      font-size: 16px;
    }
  }
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.overview-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 15px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.overview-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.card-icon.sales {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-icon.orders {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.card-icon.products {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.card-icon.avg {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.card-content {
  flex: 1;
}

.card-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 5px;
}

.card-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.card-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 600;
}

.card-trend.up {
  color: #67c23a;
}

.card-trend.down {
  color: #f56c6c;
}

.charts-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.chart-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
  background: #fafafa;
}

.chart-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-content {
  padding: 20px;
}

.chart {
  width: 100%;
  height: 400px;
}

.empty-chart {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-chart .el-empty {
  padding: 0;
}
</style>