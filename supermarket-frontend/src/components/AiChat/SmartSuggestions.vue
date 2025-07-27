<template>
  <div class="smart-suggestions-container">
    <!-- 快捷操作面板 -->
    <el-collapse v-model="activePanel" class="suggestions-panel">
      <!-- 常用功能 -->
      <el-collapse-item name="common" class="panel-item">
        <template #title>
          <div class="panel-title">
            <el-icon><Star /></el-icon>
            <span>常用功能</span>
            <el-badge :value="quickActions.length" type="primary" />
          </div>
        </template>
        
        <div class="action-grid">
          <div 
            v-for="action in quickActions" 
            :key="action.action"
            class="action-card"
            @click="selectAction(action)"
          >
            <div class="action-icon">{{ action.icon }}</div>
            <div class="action-content">
              <h4>{{ action.title }}</h4>
              <p>{{ action.description || getActionDescription(action.action) }}</p>
            </div>
            <div class="action-badge" v-if="action.badge">
              <el-badge :value="action.badge" />
            </div>
          </div>
        </div>
      </el-collapse-item>

      <!-- 智能推荐 -->
      <el-collapse-item name="smart" class="panel-item">
        <template #title>
          <div class="panel-title">
            <el-icon><MagicStick /></el-icon>
            <span>智能推荐</span>
            <el-tag size="small" type="success" v-if="smartSuggestions.length > 0">
              {{ smartSuggestions.length }} 个建议
            </el-tag>
          </div>
        </template>
        
        <div class="smart-suggestions-list">
          <div 
            v-for="(suggestion, index) in smartSuggestions" 
            :key="index"
            class="suggestion-item"
            @click="askQuestion(suggestion.question)"
          >
            <div class="suggestion-icon">
              <el-icon :class="getSuggestionIconClass(suggestion.type)">
                <component :is="getSuggestionIcon(suggestion.type)" />
              </el-icon>
            </div>
            <div class="suggestion-content">
              <div class="suggestion-title">{{ suggestion.title }}</div>
              <div class="suggestion-desc">{{ suggestion.description }}</div>
              <div class="suggestion-question">{{ suggestion.question }}</div>
            </div>
            <div class="suggestion-meta">
              <el-tag size="small" :type="getSuggestionTagType(suggestion.priority)">
                {{ getSuggestionPriorityText(suggestion.priority) }}
              </el-tag>
            </div>
          </div>
        </div>

        <!-- 生成更多建议 -->
        <div class="generate-more" v-if="canGenerateMore">
          <el-button 
            type="primary" 
            plain 
            size="small"
            @click="generateMoreSuggestions"
            :loading="generatingMore"
          >
            <el-icon><Refresh /></el-icon>
            生成更多建议
          </el-button>
        </div>
      </el-collapse-item>

      <!-- 最近使用 -->
      <el-collapse-item name="recent" class="panel-item" v-if="recentQuestions.length > 0">
        <template #title>
          <div class="panel-title">
            <el-icon><Clock /></el-icon>
            <span>最近使用</span>
            <el-tag size="small" type="info">{{ recentQuestions.length }}</el-tag>
          </div>
        </template>
        
        <div class="recent-questions">
          <div 
            v-for="question in recentQuestions.slice(0, 5)" 
            :key="question.id"
            class="recent-item"
            @click="askQuestion(question.content)"
          >
            <div class="recent-content">
              <span class="recent-text">{{ question.content }}</span>
              <span class="recent-time">{{ formatRelativeTime(question.timestamp) }}</span>
            </div>
            <el-button text size="small" @click.stop="removeRecentQuestion(question.id)">
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
        </div>
      </el-collapse-item>

      <!-- 帮助提示 -->
      <el-collapse-item name="help" class="panel-item">
        <template #title>
          <div class="panel-title">
            <el-icon><QuestionFilled /></el-icon>
            <span>使用技巧</span>
          </div>
        </template>
        
        <div class="help-tips">
          <div class="tip-item" v-for="tip in helpTips" :key="tip.id">
            <div class="tip-icon">💡</div>
            <div class="tip-content">
              <h5>{{ tip.title }}</h5>
              <p>{{ tip.content }}</p>
              <el-button 
                v-if="tip.example" 
                text 
                size="small" 
                @click="askQuestion(tip.example)"
              >
                试试看: {{ tip.example }}
              </el-button>
            </div>
          </div>
        </div>
      </el-collapse-item>
    </el-collapse>

    <!-- 浮动操作按钮 -->
    <div class="floating-actions" v-if="showFloatingActions">
      <el-button-group class="action-buttons">
        <el-button 
          type="primary" 
          circle 
          @click="quickAsk('今天销售怎么样？')"
          v-tooltip="'查看今日销售'"
        >
          📊
        </el-button>
        <el-button 
          type="success" 
          circle 
          @click="quickAsk('库存预警情况')"
          v-tooltip="'库存预警'"
        >
          📦
        </el-button>
        <el-button 
          type="warning" 
          circle 
          @click="quickAsk('本月财务概况')"
          v-tooltip="'财务概况'"
        >
          💰
        </el-button>
        <el-button 
          type="info" 
          circle 
          @click="quickAsk('需要什么帮助？')"
          v-tooltip="'获取帮助'"
        >
          ❓
        </el-button>
      </el-button-group>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, defineEmits } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Star,
  MagicStick,
  Clock,
  QuestionFilled,
  Refresh,
  Close,
  TrendCharts,
  ShoppingCart,
  Wallet,
  Setting,
  PieChart,
  Warning,
  InfoFilled
} from '@element-plus/icons-vue'
import { getQuickActions, getSmartSuggestions } from '@/api/ai'

// Emits
const emit = defineEmits<{
  askQuestion: [question: string]
  selectAction: [action: any]
}>()

// 响应式数据
const activePanel = ref(['common', 'smart'])
const quickActions = ref<any[]>([])
const smartSuggestions = ref<any[]>([])
const recentQuestions = ref<any[]>([])
const generatingMore = ref(false)
const showFloatingActions = ref(true)

// 计算属性
const canGenerateMore = computed(() => smartSuggestions.value.length < 10)

// 帮助提示
const helpTips = [
  {
    id: 1,
    title: '快速查询销售数据',
    content: '可以询问特定时间的销售情况，如"昨天销售额多少"',
    example: '昨天销售额多少？'
  },
  {
    id: 2,
    title: '库存管理技巧',
    content: '支持按分类、商品名称查询库存，还能获取补货建议',
    example: '哪些商品库存不足？'
  },
  {
    id: 3,
    title: '财务分析功能',
    content: '可以查看收支明细、利润分析、成本统计等',
    example: '本月利润率如何？'
  },
  {
    id: 4,
    title: '智能建议获取',
    content: 'AI会根据数据分析给出经营建议和优化方案',
    example: '给我一些经营优化建议'
  }
]

// 组件挂载
onMounted(async () => {
  await loadData()
  loadRecentQuestions()
})

// 加载数据
const loadData = async () => {
  try {
    // 加载快捷操作
    const actionsResponse = await getQuickActions()
    if (actionsResponse.data.code === 200) {
      quickActions.value = actionsResponse.data.data
    }

    // 加载智能建议
    const suggestionsResponse = await getSmartSuggestions(1)
    if (suggestionsResponse.data.code === 200) {
      smartSuggestions.value = generateSmartSuggestions(suggestionsResponse.data.data)
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

// 生成智能建议
const generateSmartSuggestions = (baseSuggestions: string[]) => {
  const suggestions = [
    {
      type: 'sales',
      priority: 'high',
      title: '销售分析建议',
      description: '基于近期销售数据的分析建议',
      question: '分析本周销售趋势并给出建议'
    },
    {
      type: 'inventory',
      priority: 'medium',
      title: '库存优化',
      description: '根据销售情况优化库存配置',
      question: '根据销售数据优化库存配置'
    },
    {
      type: 'finance',
      priority: 'high',
      title: '成本控制',
      description: '分析成本结构，寻找优化空间',
      question: '分析当前成本结构并提供优化建议'
    },
    {
      type: 'promotion',
      priority: 'medium',
      title: '促销策略',
      description: '制定有效的促销活动方案',
      question: '为滞销商品制定促销策略'
    },
    {
      type: 'customer',
      priority: 'low',
      title: '客户分析',
      description: '了解客户购买行为和偏好',
      question: '分析客户购买行为特征'
    }
  ]

  // 添加基础建议
  baseSuggestions.forEach((suggestion, index) => {
    suggestions.push({
      type: 'general',
      priority: 'low',
      title: `建议 ${index + 1}`,
      description: '常用功能建议',
      question: suggestion
    })
  })

  return suggestions
}

// 操作描述
const getActionDescription = (action: string) => {
  const descriptions: Record<string, string> = {
    'query_sales_data': '查看销售数据和统计信息',
    'check_inventory': '检查库存状态和预警信息',
    'add_product': '添加新商品或管理商品信息',
    'query_finance': '查看财务报表和收支情况',
    'system_settings': '系统配置和参数设置'
  }
  return descriptions[action] || '快捷操作功能'
}

// 建议图标
const getSuggestionIcon = (type: string) => {
  const icons: Record<string, any> = {
    'sales': TrendCharts,
    'inventory': ShoppingCart,
    'finance': Wallet,
    'promotion': Star,
    'customer': InfoFilled,
    'general': Setting,
    'operation': PieChart,
    'staff': InfoFilled
  }
  return icons[type] || Setting
}

const getSuggestionIconClass = (type: string) => {
  const classes: Record<string, string> = {
    'sales': 'text-blue-500',
    'inventory': 'text-green-500',
    'finance': 'text-orange-500',
    'promotion': 'text-purple-500',
    'customer': 'text-red-500',
    'general': 'text-gray-500'
  }
  return classes[type] || 'text-gray-500'
}

const getSuggestionTagType = (priority: string) => {
  const types: Record<string, string> = {
    'high': 'danger',
    'medium': 'warning',
    'low': 'info'
  }
  return types[priority] || 'info'
}

const getSuggestionPriorityText = (priority: string) => {
  const texts: Record<string, string> = {
    'high': '重要',
    'medium': '一般',
    'low': '参考'
  }
  return texts[priority] || '参考'
}

// 格式化相对时间
const formatRelativeTime = (timestamp: number) => {
  const now = Date.now()
  const diff = now - timestamp
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return new Date(timestamp).toLocaleDateString()
}

// 选择操作
const selectAction = (action: any) => {
  emit('selectAction', action)
  addToRecentQuestions(action.title)
}

// 询问问题
const askQuestion = (question: string) => {
  emit('askQuestion', question)
  addToRecentQuestions(question)
}

// 快速询问
const quickAsk = (question: string) => {
  askQuestion(question)
}

// 添加到最近使用
const addToRecentQuestions = (content: string) => {
  const newQuestion = {
    id: Date.now(),
    content,
    timestamp: Date.now()
  }
  
  // 移除重复项
  recentQuestions.value = recentQuestions.value.filter(q => q.content !== content)
  
  // 添加到开头
  recentQuestions.value.unshift(newQuestion)
  
  // 保持最多10条
  if (recentQuestions.value.length > 10) {
    recentQuestions.value = recentQuestions.value.slice(0, 10)
  }
  
  // 保存到本地存储
  localStorage.setItem('ai_recent_questions', JSON.stringify(recentQuestions.value))
}

// 加载最近问题
const loadRecentQuestions = () => {
  try {
    const saved = localStorage.getItem('ai_recent_questions')
    if (saved) {
      recentQuestions.value = JSON.parse(saved)
    }
  } catch (error) {
    console.error('加载最近问题失败:', error)
  }
}

// 移除最近问题
const removeRecentQuestion = (id: number) => {
  recentQuestions.value = recentQuestions.value.filter(q => q.id !== id)
  localStorage.setItem('ai_recent_questions', JSON.stringify(recentQuestions.value))
}

// 生成更多建议
const generateMoreSuggestions = async () => {
  generatingMore.value = true
  
  try {
    // 模拟生成更多建议
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const moreSuggestions = [
      {
        type: 'operation',
        priority: 'medium',
        title: '运营效率',
        description: '分析运营流程，提升工作效率',
        question: '如何提升超市运营效率？'
      },
      {
        type: 'staff',
        priority: 'low',
        title: '员工管理',
        description: '员工绩效和排班优化',
        question: '分析员工工作效率和排班安排'
      }
    ]
    
    smartSuggestions.value.push(...moreSuggestions)
    ElMessage.success('已生成更多建议')
  } catch (error) {
    ElMessage.error('生成建议失败')
  } finally {
    generatingMore.value = false
  }
}
</script>

<style scoped>
.smart-suggestions-container {
  position: relative;
}

/* 建议面板 */
.suggestions-panel {
  border: none;
  background: transparent;
}

.panel-item {
  margin-bottom: 8px;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  overflow: hidden;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.panel-item :deep(.el-collapse-item__header) {
  background: #f8f9fa;
  border: none;
  padding: 12px 16px;
  font-weight: 600;
}

.panel-item :deep(.el-collapse-item__content) {
  padding: 16px;
  border: none;
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #303133;
}

/* 操作网格 */
.action-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 12px;
}

.action-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafafa;
}

.action-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-color: #667eea;
}

.action-icon {
  font-size: 24px;
  width: 48px;
  height: 48px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.action-content {
  flex: 1;
}

.action-content h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.action-content p {
  margin: 0;
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
}

.action-badge {
  flex-shrink: 0;
}

/* 智能建议列表 */
.smart-suggestions-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  border: 1px solid #f0f2f6;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafbfc;
}

.suggestion-item:hover {
  background: #f0f7ff;
  border-color: #b3d8ff;
}

.suggestion-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.suggestion-content {
  flex: 1;
}

.suggestion-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 2px;
}

.suggestion-desc {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.suggestion-question {
  font-size: 13px;
  color: #667eea;
  font-style: italic;
}

.suggestion-meta {
  flex-shrink: 0;
  align-self: center;
}

/* 生成更多 */
.generate-more {
  text-align: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f2f6;
}

/* 最近使用 */
.recent-questions {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.recent-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  border: 1px solid #f0f2f6;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafbfc;
}

.recent-item:hover {
  background: #f0f7ff;
  border-color: #b3d8ff;
}

.recent-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.recent-text {
  font-size: 13px;
  color: #303133;
}

.recent-time {
  font-size: 11px;
  color: #c0c4cc;
}

/* 帮助提示 */
.help-tips {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tip-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #f8f9ff;
  border: 1px solid #e1e6ff;
  border-radius: 8px;
}

.tip-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.tip-content h5 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.tip-content p {
  margin: 0 0 8px 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.4;
}

/* 浮动操作按钮 */
.floating-actions {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 1000;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  border-radius: 24px;
  padding: 8px;
  background: white;
}

.action-buttons .el-button {
  width: 48px;
  height: 48px;
  font-size: 18px;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .action-grid {
    grid-template-columns: 1fr;
  }
  
  .action-card {
    gap: 8px;
    padding: 10px;
  }
  
  .action-icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }
  
  .floating-actions {
    bottom: 16px;
    right: 16px;
  }
  
  .action-buttons .el-button {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }
}
</style>