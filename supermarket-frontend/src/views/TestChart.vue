<template>
  <div class="test-chart-container">
    <h1>图表测试页面</h1>
    
    <!-- 测试按钮 -->
    <div class="test-buttons">
      <el-button @click="testMessage('success')">测试成功提示</el-button>
      <el-button @click="testMessage('error')">测试错误提示</el-button>
      <el-button @click="testMessage('warning')">测试警告提示</el-button>
      <el-button @click="testMessage('info')">测试信息提示</el-button>
      <el-button @click="testChart">测试图表</el-button>
    </div>

    <!-- 测试图表 -->
    <div class="chart-container">
      <v-chart
        class="chart"
        :option="testOption"
        autoresize
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
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

// 测试图表配置
const testOption = ref({
  title: {
    text: '测试图表',
    left: 'center'
  },
  tooltip: {
    trigger: 'axis'
  },
  xAxis: {
    type: 'category',
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  },
  yAxis: {
    type: 'value'
  },
  series: [{
    name: '销售额',
    type: 'line',
    data: [120, 200, 150, 80, 70, 110, 130],
    smooth: true,
    itemStyle: { color: '#409EFF' },
    areaStyle: { opacity: 0.3 }
  }]
})

// 测试消息提示
const testMessage = (type: 'success' | 'error' | 'warning' | 'info') => {
  ElMessage({
    message: `这是一个${type}类型的消息提示`,
    type: type,
    duration: 3000,
    showClose: true
  })
}

// 测试图表更新
const testChart = () => {
  const newData = Array.from({ length: 7 }, () => Math.floor(Math.random() * 300))
  testOption.value = {
    ...testOption.value,
    series: [{
      ...testOption.value.series[0],
      data: newData
    }]
  }
  
  ElMessage({
    message: '图表数据已更新',
    type: 'success',
    duration: 2000,
    showClose: true
  })
}
</script>

<style scoped>
.test-chart-container {
  padding: 20px;
}

.test-buttons {
  margin: 20px 0;
  display: flex;
  gap: 10px;
}

.chart-container {
  margin-top: 20px;
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.chart {
  width: 100%;
  height: 400px;
}
</style>
