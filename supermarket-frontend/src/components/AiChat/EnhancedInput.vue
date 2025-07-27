<template>
  <div class="enhanced-input-container">
    <!-- 智能建议栏 -->
    <transition name="slide-down">
      <div class="input-suggestions-bar" v-if="showSuggestions && suggestions.length > 0">
        <div class="suggestions-header">
          <el-icon><MagicStick /></el-icon>
          <span>智能建议</span>
          <el-button text size="small" @click="hideSuggestions">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
        <el-scrollbar class="suggestions-scroll">
          <div class="suggestions-list">
            <el-tag 
              v-for="(suggestion, index) in suggestions" 
              :key="index"
              size="small"
              class="suggestion-tag"
              :class="{ 'active': selectedSuggestionIndex === index }"
              @click="selectSuggestion(suggestion)"
            >
              {{ suggestion }}
            </el-tag>
          </div>
        </el-scrollbar>
      </div>
    </transition>

    <!-- 主输入区域 -->
    <div class="main-input-area">
      <!-- 功能工具栏 -->
      <div class="input-toolbar" v-if="showToolbar">
        <div class="toolbar-left">
          <el-button-group class="tool-group">
            <el-button 
              text 
              size="small" 
              @click="toggleQuickActions"
              :class="{ 'active': showQuickActions }"
              v-tooltip="'快捷操作'"
            >
              <el-icon><Grid /></el-icon>
            </el-button>
            <el-button 
              text 
              size="small" 
              @click="toggleVoiceInput"
              :class="{ 'active': isRecording }"
              v-tooltip="'语音输入'"
            >
              <el-icon><Microphone /></el-icon>
            </el-button>
            <el-button 
              text 
              size="small" 
              @click="toggleEmojiPicker"
              v-tooltip="'表情符号'"
            >
              <el-icon><ChatDotSquare /></el-icon>
            </el-button>
            <el-button 
              text 
              size="small" 
              @click="pasteFromClipboard"
              v-tooltip="'粘贴'"
            >
              <el-icon><DocumentCopy /></el-icon>
            </el-button>
          </el-button-group>
        </div>
        
        <div class="toolbar-right">
          <el-dropdown @command="handleTemplateCommand">
            <el-button text size="small" v-tooltip="'消息模板'">
              <el-icon><Document /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item 
                  v-for="template in messageTemplates" 
                  :key="template.id"
                  :command="template.content"
                >
                  {{ template.title }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 输入框区域 -->
      <div class="input-wrapper" :class="{ 'focused': isFocused, 'has-content': hasContent }">
        <div class="input-prefix" v-if="inputPrefix">
          <el-tag size="small" :type="inputPrefixType">{{ inputPrefix }}</el-tag>
        </div>

        <el-input
          ref="inputRef"
          v-model="inputValue"
          type="textarea"
          :placeholder="placeholder"
          :autosize="{ minRows: 1, maxRows: maxRows }"
          resize="none"
          class="main-input"
          @focus="handleFocus"
          @blur="handleBlur"
          @input="handleInput"
          @keydown="handleKeyDown"
          @paste="handlePaste"
        />

        <!-- 输入状态指示器 -->
        <div class="input-indicators">
          <!-- 字符计数 -->
          <div class="char-count" v-if="showCharCount">
            <span :class="{ 'over-limit': isOverLimit }">
              {{ inputValue.length }}{{ maxLength ? `/${maxLength}` : '' }}
            </span>
          </div>

          <!-- AI建议状态 */
          <div class="ai-status" v-if="isAnalyzing">
            <el-icon class="rotating"><Loading /></el-icon>
            <span>AI分析中...</span>
          </div>

          <!-- 语音录制状态 -->
          <div class="voice-status" v-if="isRecording">
            <div class="voice-wave">
              <span v-for="i in 3" :key="i" class="wave-bar"></span>
            </div>
            <span>正在录音...</span>
            <el-button text size="small" @click="stopRecording">
              <el-icon><VideoPlay /></el-icon>
            </el-button>
          </div>
        </div>

        <!-- 发送按钮 -->
        <div class="send-button-area">
          <transition name="scale">
            <el-button 
              v-if="hasContent || isRecording"
              type="primary" 
              :disabled="!canSend"
              :loading="isSending"
              @click="handleSend"
              class="send-btn"
              circle
            >
              <el-icon><Promotion /></el-icon>
            </el-button>
          </transition>
        </div>
      </div>

      <!-- 快捷操作面板 -->
      <transition name="slide-up">
        <div class="quick-actions-panel" v-if="showQuickActions">
          <div class="panel-header">
            <span>快捷操作</span>
            <el-button text size="small" @click="showQuickActions = false">
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
          <div class="actions-grid">
            <div 
              v-for="action in quickActionsList" 
              :key="action.id"
              class="quick-action-item"
              @click="selectQuickAction(action)"
            >
              <div class="action-icon">{{ action.icon }}</div>
              <span class="action-text">{{ action.text }}</span>
            </div>
          </div>
        </div>
      </transition>

      <!-- 表情选择器 -->
      <transition name="scale">
        <div class="emoji-picker" v-if="showEmojiPicker">
          <div class="emoji-header">
            <span>表情符号</span>
            <el-button text size="small" @click="showEmojiPicker = false">
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
          <div class="emoji-grid">
            <span 
              v-for="emoji in emojiList" 
              :key="emoji"
              class="emoji-item"
              @click="insertEmoji(emoji)"
            >
              {{ emoji }}
            </span>
          </div>
        </div>
      </transition>
    </div>

    <!-- 输入历史记录 -->
    <div class="input-history" v-if="showHistory && inputHistory.length > 0">
      <div class="history-header">
        <el-icon><Clock /></el-icon>
        <span>输入历史</span>
      </div>
      <div class="history-list">
        <div 
          v-for="(item, index) in inputHistory.slice(0, 3)" 
          :key="index"
          class="history-item"
          @click="selectHistoryItem(item)"
        >
          {{ item.length > 50 ? item.substring(0, 50) + '...' : item }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, defineEmits, defineProps, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  MagicStick,
  Close,
  Grid,
  Microphone,
  ChatDotSquare,
  DocumentCopy,
  Document,
  Loading,
  VideoPlay,
  Promotion,
  Clock
} from '@element-plus/icons-vue'

// Props
const props = defineProps<{
  modelValue: string
  placeholder?: string
  maxLength?: number
  maxRows?: number
  showToolbar?: boolean
  showCharCount?: boolean
  showHistory?: boolean
  disabled?: boolean
  loading?: boolean
}>()

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: string]
  send: [message: string]
  voiceInput: [audioData: Blob]
  templateSelect: [template: string]
}>()

// 响应式数据
const inputRef = ref()
const inputValue = ref(props.modelValue || '')
const isFocused = ref(false)
const isAnalyzing = ref(false)
const isRecording = ref(false)
const isSending = ref(false)
const showSuggestions = ref(false)
const showQuickActions = ref(false)
const showEmojiPicker = ref(false)
const selectedSuggestionIndex = ref(-1)
const suggestions = ref<string[]>([])
const inputHistory = ref<string[]>([])
const inputPrefix = ref('')
const inputPrefixType = ref('info')

// 计算属性
const hasContent = computed(() => inputValue.value.trim().length > 0)
const isOverLimit = computed(() => props.maxLength ? inputValue.value.length > props.maxLength : false)
const canSend = computed(() => hasContent.value && !isOverLimit.value && !props.disabled && !isSending.value)

// 监听器
watch(() => props.modelValue, (newVal) => {
  inputValue.value = newVal || ''
})

watch(inputValue, (newVal) => {
  emit('update:modelValue', newVal)
})

// 快捷操作列表
const quickActionsList = [
  { id: 1, icon: '📊', text: '销售数据', action: '查看今日销售数据' },
  { id: 2, icon: '📦', text: '库存查询', action: '检查库存状况' },
  { id: 3, icon: '💰', text: '财务报表', action: '显示本月财务报表' },
  { id: 4, icon: '📈', text: '趋势分析', action: '分析销售趋势' },
  { id: 5, icon: '🔍', text: '商品搜索', action: '搜索商品信息' },
  { id: 6, icon: '⚠️', text: '库存预警', action: '查看库存预警' }
]

// 消息模板
const messageTemplates = [
  { id: 1, title: '销售查询', content: '请帮我查看今天的销售情况，包括营业额和订单数量' },
  { id: 2, title: '库存盘点', content: '检查所有商品的库存状况，特别是预警商品' },
  { id: 3, title: '财务分析', content: '分析本月的收支情况和利润率' },
  { id: 4, title: '商品管理', content: '我需要添加新商品，请指导具体步骤' },
  { id: 5, title: '员工绩效', content: '查看员工的销售绩效和工作表现' }
]

// 表情列表
const emojiList = [
  '😀', '😃', '😄', '😁', '😆', '😅', '😂', '🤣',
  '😊', '😇', '🙂', '🙃', '😉', '😌', '😍', '🥰',
  '😘', '😗', '😙', '😚', '😋', '😛', '😝', '😜',
  '🤪', '🤨', '🧐', '🤓', '😎', '🤩', '🥳', '😏',
  '👍', '👎', '👌', '✌️', '🤞', '🤟', '🤘', '🤙',
  '💪', '🙏', '✨', '🎉', '🎊', '💫', '⭐', '🌟'
]

// 方法
const handleFocus = () => {
  isFocused.value = true
  loadInputHistory()
  
  // 如果输入框为空，显示建议
  if (!hasContent.value) {
    loadSuggestions()
  }
}

const handleBlur = () => {
  isFocused.value = false
  // 延迟隐藏建议，以便点击建议项
  setTimeout(() => {
    if (!isFocused.value) {
      showSuggestions.value = false
    }
  }, 200)
}

const handleInput = (value: string) => {
  inputValue.value = value
  
  // 实时分析输入内容
  if (value.length > 2) {
    analyzeInput(value)
  } else {
    showSuggestions.value = false
  }
}

const handleKeyDown = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    
    // 如果有选中的建议，使用建议
    if (showSuggestions.value && selectedSuggestionIndex.value >= 0) {
      selectSuggestion(suggestions.value[selectedSuggestionIndex.value])
      return
    }
    
    handleSend()
  } else if (event.key === 'ArrowUp' && showSuggestions.value) {
    event.preventDefault()
    selectedSuggestionIndex.value = Math.max(0, selectedSuggestionIndex.value - 1)
  } else if (event.key === 'ArrowDown' && showSuggestions.value) {
    event.preventDefault()
    selectedSuggestionIndex.value = Math.min(suggestions.value.length - 1, selectedSuggestionIndex.value + 1)
  } else if (event.key === 'Escape') {
    showSuggestions.value = false
    showQuickActions.value = false
    showEmojiPicker.value = false
    selectedSuggestionIndex.value = -1
  }
}

const handlePaste = (event: ClipboardEvent) => {
  // 可以在这里处理特殊的粘贴逻辑
  console.log('粘贴事件:', event)
}

const handleSend = () => {
  if (!canSend.value) return
  
  const message = inputValue.value.trim()
  if (!message) return
  
  // 添加到历史记录
  addToHistory(message)
  
  // 发送消息
  emit('send', message)
  
  // 清空输入框
  inputValue.value = ''
  hideSuggestions()
  
  // 聚焦输入框
  nextTick(() => {
    inputRef.value?.focus()
  })
}

const analyzeInput = async (text: string) => {
  isAnalyzing.value = true
  
  try {
    // 模拟AI分析输入内容
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 根据输入内容生成建议
    const newSuggestions = generateSuggestions(text)
    suggestions.value = newSuggestions
    showSuggestions.value = newSuggestions.length > 0
    selectedSuggestionIndex.value = -1
    
    // 检测输入意图并设置前缀
    detectInputIntent(text)
  } catch (error) {
    console.error('分析输入失败:', error)
  } finally {
    isAnalyzing.value = false
  }
}

const generateSuggestions = (text: string): string[] => {
  const suggestions: string[] = []
  const lowerText = text.toLowerCase()
  
  if (lowerText.includes('销售') || lowerText.includes('营业')) {
    suggestions.push(
      '今天的销售额是多少？',
      '本周销售趋势如何？',
      '热销商品有哪些？',
      '各分类销售占比如何？'
    )
  } else if (lowerText.includes('库存') || lowerText.includes('商品')) {
    suggestions.push(
      '哪些商品库存不足？',
      '库存预警商品列表',
      '库存周转率分析',
      '需要补货的商品'
    )
  } else if (lowerText.includes('财务') || lowerText.includes('利润')) {
    suggestions.push(
      '本月收支情况',
      '利润率分析',
      '成本结构分析',
      '资金流水查询'
    )
  } else if (lowerText.includes('员工') || lowerText.includes('绩效')) {
    suggestions.push(
      '员工销售绩效',
      '员工工作时长统计',
      '绩效排行榜',
      '员工管理建议'
    )
  } else {
    suggestions.push(
      '查看今日概况',
      '系统使用帮助',
      '常见问题解答'
    )
  }
  
  return suggestions.slice(0, 4)
}

const detectInputIntent = (text: string) => {
  const lowerText = text.toLowerCase()
  
  if (lowerText.includes('销售')) {
    inputPrefix.value = '销售'
    inputPrefixType.value = 'success'
  } else if (lowerText.includes('库存')) {
    inputPrefix.value = '库存'
    inputPrefixType.value = 'warning'
  } else if (lowerText.includes('财务')) {
    inputPrefix.value = '财务'
    inputPrefixType.value = 'info'
  } else if (lowerText.includes('帮助')) {
    inputPrefix.value = '帮助'
    inputPrefixType.value = 'primary'
  } else {
    inputPrefix.value = ''
  }
}

const loadSuggestions = () => {
  suggestions.value = [
    '今天销售怎么样？',
    '检查库存状况',
    '查看财务报表',
    '需要什么帮助？'
  ]
  showSuggestions.value = true
}

const selectSuggestion = (suggestion: string) => {
  inputValue.value = suggestion
  hideSuggestions()
  nextTick(() => {
    inputRef.value?.focus()
  })
}

const hideSuggestions = () => {
  showSuggestions.value = false
  selectedSuggestionIndex.value = -1
}

const toggleQuickActions = () => {
  showQuickActions.value = !showQuickActions.value
  showEmojiPicker.value = false
}

const toggleVoiceInput = () => {
  if (isRecording.value) {
    stopRecording()
  } else {
    startRecording()
  }
}

const toggleEmojiPicker = () => {
  showEmojiPicker.value = !showEmojiPicker.value
  showQuickActions.value = false
}

const startRecording = () => {
  // 这里实现语音录制逻辑
  isRecording.value = true
  ElMessage.info('开始录音...')
}

const stopRecording = () => {
  isRecording.value = false
  ElMessage.success('录音结束')
  // 这里处理录音数据
}

const pasteFromClipboard = async () => {
  try {
    const text = await navigator.clipboard.readText()
    if (text) {
      inputValue.value += text
      ElMessage.success('已粘贴内容')
    }
  } catch (error) {
    ElMessage.error('无法访问剪贴板')
  }
}

const handleTemplateCommand = (template: string) => {
  inputValue.value = template
  emit('templateSelect', template)
  nextTick(() => {
    inputRef.value?.focus()
  })
}

const selectQuickAction = (action: any) => {
  inputValue.value = action.action
  showQuickActions.value = false
  nextTick(() => {
    inputRef.value?.focus()
  })
}

const insertEmoji = (emoji: string) => {
  const textarea = inputRef.value?.$el?.querySelector('textarea')
  if (textarea) {
    const start = textarea.selectionStart
    const end = textarea.selectionEnd
    const beforeText = inputValue.value.substring(0, start)
    const afterText = inputValue.value.substring(end)
    inputValue.value = beforeText + emoji + afterText
    
    nextTick(() => {
      textarea.selectionStart = textarea.selectionEnd = start + emoji.length
      textarea.focus()
    })
  }
  showEmojiPicker.value = false
}

const loadInputHistory = () => {
  try {
    const saved = localStorage.getItem('ai_input_history')
    if (saved) {
      inputHistory.value = JSON.parse(saved)
    }
  } catch (error) {
    console.error('加载输入历史失败:', error)
  }
}

const addToHistory = (text: string) => {
  // 移除重复项
  inputHistory.value = inputHistory.value.filter(item => item !== text)
  
  // 添加到开头
  inputHistory.value.unshift(text)
  
  // 保持最多20条
  if (inputHistory.value.length > 20) {
    inputHistory.value = inputHistory.value.slice(0, 20)
  }
  
  // 保存到本地存储
  localStorage.setItem('ai_input_history', JSON.stringify(inputHistory.value))
}

const selectHistoryItem = (item: string) => {
  inputValue.value = item
  nextTick(() => {
    inputRef.value?.focus()
  })
}
</script>

<style scoped>
.enhanced-input-container {
  position: relative;
}

/* 智能建议栏 */
.input-suggestions-bar {
  background: white;
  border: 1px solid #e4e7ed;
  border-bottom: none;
  border-radius: 8px 8px 0 0;
  padding: 8px 12px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
}

.suggestions-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 12px;
  color: #909399;
  font-weight: 600;
}

.suggestions-header > div:first-child {
  display: flex;
  align-items: center;
  gap: 4px;
}

.suggestions-scroll {
  max-height: 120px;
}

.suggestions-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.suggestion-tag {
  cursor: pointer;
  border: 1px solid #e1e6ff;
  background: #f8f9ff;
  color: #667eea;
  transition: all 0.3s ease;
}

.suggestion-tag:hover,
.suggestion-tag.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

/* 主输入区域 */
.main-input-area {
  position: relative;
}

/* 工具栏 */
.input-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  background: #f8f9fa;
  border: 1px solid #e4e7ed;
  border-bottom: none;
  border-radius: 8px 8px 0 0;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 4px;
}

.tool-group .el-button {
  color: #909399;
  transition: color 0.3s ease;
}

.tool-group .el-button:hover,
.tool-group .el-button.active {
  color: #667eea;
}

/* 输入框包装器 */
.input-wrapper {
  position: relative;
  border: 1px solid #e4e7ed;
  border-radius: 0 0 8px 8px;
  background: white;
  padding: 12px 16px;
  transition: all 0.3s ease;
  display: flex;
  align-items: flex-end;
  gap: 12px;
}

.input-wrapper.focused {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.input-wrapper.has-content {
  border-color: #67c23a;
}

.input-prefix {
  flex-shrink: 0;
  align-self: flex-start;
  margin-top: 4px;
}

.main-input {
  flex: 1;
  border: none;
  background: transparent;
}

.main-input :deep(.el-textarea__inner) {
  border: none;
  background: transparent;
  resize: none;
  padding: 0;
  font-size: 14px;
  line-height: 1.5;
  box-shadow: none;
  color: #303133;
}

.main-input :deep(.el-textarea__inner):focus {
  box-shadow: none;
}

.main-input :deep(.el-textarea__inner)::placeholder {
  color: #c0c4cc;
}

/* 输入指示器 */
.input-indicators {
  position: absolute;
  top: 8px;
  right: 60px;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.char-count.over-limit {
  color: #f56c6c;
}

.ai-status {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #667eea;
}

.rotating {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.voice-status {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #f56c6c;
}

.voice-wave {
  display: flex;
  gap: 2px;
}

.wave-bar {
  width: 2px;
  height: 12px;
  background: #f56c6c;
  border-radius: 1px;
  animation: wave 1.2s infinite ease-in-out;
}

.wave-bar:nth-child(1) { animation-delay: -0.4s; }
.wave-bar:nth-child(2) { animation-delay: -0.2s; }
.wave-bar:nth-child(3) { animation-delay: 0s; }

@keyframes wave {
  0%, 40%, 100% { transform: scaleY(0.4); }
  20% { transform: scaleY(1); }
}

/* 发送按钮 */
.send-button-area {
  flex-shrink: 0;
}

.send-btn {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.send-btn:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.send-btn:disabled {
  background: #c0c4cc;
  transform: none;
  box-shadow: none;
}

/* 快捷操作面板 */
.quick-actions-panel {
  position: absolute;
  bottom: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e4e7ed;
  border-bottom: none;
  border-radius: 8px 8px 0 0;
  box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f2f6;
  font-size: 13px;
  font-weight: 600;
  color: #303133;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 8px;
  padding: 12px;
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 12px 8px;
  border: 1px solid #f0f2f6;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafbfc;
}

.quick-action-item:hover {
  background: #f0f7ff;
  border-color: #b3d8ff;
  transform: translateY(-1px);
}

.action-icon {
  font-size: 20px;
}

.action-text {
  font-size: 11px;
  color: #606266;
  text-align: center;
}

/* 表情选择器 */
.emoji-picker {
  position: absolute;
  bottom: 100%;
  right: 0;
  width: 280px;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  z-index: 20;
}

.emoji-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f2f6;
  font-size: 13px;
  font-weight: 600;
  color: #303133;
}

.emoji-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 4px;
  padding: 12px;
  max-height: 200px;
  overflow-y: auto;
}

.emoji-item {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.emoji-item:hover {
  background: #f0f2f6;
}

/* 输入历史 */
.input-history {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e4e7ed;
  border-top: none;
  border-radius: 0 0 8px 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 5;
}

.history-header {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  font-size: 12px;
  color: #909399;
  font-weight: 600;
  border-bottom: 1px solid #f0f2f6;
}

.history-list {
  max-height: 120px;
  overflow-y: auto;
}

.history-item {
  padding: 8px 12px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
  transition: background-color 0.3s ease;
  border-bottom: 1px solid #f8f9fa;
}

.history-item:hover {
  background: #f0f7ff;
}

.history-item:last-child {
  border-bottom: none;
}

/* 动画 */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter-from,
.slide-down-leave-to {
  transform: translateY(-10px);
  opacity: 0;
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  transform: translateY(10px);
  opacity: 0;
}

.scale-enter-active,
.scale-leave-active {
  transition: all 0.3s ease;
}

.scale-enter-from,
.scale-leave-to {
  transform: scale(0.8);
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .input-toolbar {
    padding: 6px 8px;
  }
  
  .input-wrapper {
    padding: 10px 12px;
  }
  
  .quick-actions-panel .actions-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .emoji-picker {
    width: 240px;
  }
  
  .emoji-grid {
    grid-template-columns: repeat(6, 1fr);
  }
}
</style>