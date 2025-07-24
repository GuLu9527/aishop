# AI智能助手前端界面设计方案

## 📋 目录
- [整体设计理念](#整体设计理念)
- [页面布局设计](#页面布局设计)
- [组件架构](#组件架构)
- [交互设计](#交互设计)
- [技术实现](#技术实现)

---

## 🎨 整体设计理念

### 设计原则
1. **简洁直观** - 类似微信聊天界面，用户零学习成本
2. **响应式设计** - 支持桌面端和移动端
3. **智能引导** - 主动提示用户可用功能
4. **快速操作** - 常用功能一键直达
5. **视觉反馈** - 清晰的状态提示和加载动画

### 视觉风格
- **主色调**: 蓝色系 (#409EFF) - 科技感和信任感
- **辅助色**: 绿色 (#67C23A) - 成功状态，灰色 (#909399) - 次要信息
- **字体**: 系统默认字体，确保清晰度
- **圆角**: 8px 统一圆角，现代化设计
- **阴影**: 轻微阴影增加层次感

---

## 📱 页面布局设计

### 整体布局结构
```
┌─────────────────────────────────────────────────────────┐
│                    顶部工具栏                            │
├─────────────────────────────────────────────────────────┤
│  侧边栏    │              主对话区域                     │
│           │  ┌─────────────────────────────────────┐    │
│  快捷功能  │  │           对话历史                   │    │
│           │  │                                     │    │
│  会话历史  │  │  [用户消息]              [AI回复]   │    │
│           │  │                                     │    │
│  智能推荐  │  │  [用户消息]              [AI回复]   │    │
│           │  └─────────────────────────────────────┘    │
│           │  ┌─────────────────────────────────────┐    │
│           │  │  输入框 + 发送按钮 + 语音按钮        │    │
│           │  └─────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
```

### 响应式适配
```css
/* 桌面端 (>1200px) */
.ai-chat-container {
  display: grid;
  grid-template-columns: 280px 1fr;
  height: 100vh;
}

/* 平板端 (768px-1200px) */
@media (max-width: 1200px) {
  .ai-chat-container {
    grid-template-columns: 240px 1fr;
  }
}

/* 移动端 (<768px) */
@media (max-width: 768px) {
  .ai-chat-container {
    grid-template-columns: 1fr;
  }
  .sidebar {
    position: fixed;
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }
  .sidebar.open {
    transform: translateX(0);
  }
}
```

---

## 🧩 组件架构

### 核心组件结构
```
AiChatPage.vue (主页面)
├── AiChatSidebar.vue (侧边栏)
│   ├── QuickActions.vue (快捷操作)
│   ├── ChatHistory.vue (会话历史)
│   └── SmartSuggestions.vue (智能推荐)
├── AiChatMain.vue (主对话区)
│   ├── ChatMessages.vue (消息列表)
│   │   ├── UserMessage.vue (用户消息)
│   │   ├── AiMessage.vue (AI回复)
│   │   └── SystemMessage.vue (系统消息)
│   └── ChatInput.vue (输入区域)
│       ├── TextInput.vue (文本输入)
│       ├── VoiceInput.vue (语音输入)
│       └── QuickButtons.vue (快捷按钮)
└── AiChatModals.vue (弹窗组件)
    ├── SmartFormModal.vue (智能表单)
    ├── ConfirmModal.vue (确认对话框)
    └── HelpModal.vue (帮助说明)
```

### 状态管理设计
```typescript
// stores/aiChat.ts
export const useAiChatStore = defineStore('aiChat', {
  state: () => ({
    // 当前会话
    currentSession: {
      id: '',
      title: '',
      messages: [] as ChatMessage[],
      context: {} as ConversationContext
    },
    
    // 会话历史
    sessionHistory: [] as ChatSession[],
    
    // UI状态
    ui: {
      isLoading: false,
      isRecording: false,
      sidebarVisible: true,
      currentInput: '',
      suggestions: [] as string[]
    },
    
    // 用户偏好
    preferences: {
      theme: 'light',
      voiceEnabled: true,
      autoSuggestions: true,
      messageSound: true
    }
  }),
  
  actions: {
    async sendMessage(content: string) {
      // 发送消息逻辑
    },
    
    async loadChatHistory() {
      // 加载历史记录
    },
    
    updateUserPreferences(prefs: UserPreferences) {
      // 更新用户偏好
    }
  }
})
```

---

## 🎯 交互设计

### 1. 消息输入交互
```vue
<!-- ChatInput.vue -->
<template>
  <div class="chat-input-container">
    <!-- 智能提示区域 -->
    <div v-if="suggestions.length" class="suggestions-bar">
      <el-tag
        v-for="suggestion in suggestions"
        :key="suggestion"
        @click="applySuggestion(suggestion)"
        class="suggestion-tag"
      >
        {{ suggestion }}
      </el-tag>
    </div>
    
    <!-- 输入区域 -->
    <div class="input-area">
      <el-input
        v-model="inputText"
        type="textarea"
        :rows="1"
        :autosize="{ minRows: 1, maxRows: 4 }"
        placeholder="输入您的问题，或说'帮助'查看可用功能..."
        @keydown.enter.exact="handleSend"
        @keydown.ctrl.enter="addNewLine"
        @input="handleInputChange"
        class="message-input"
      />
      
      <!-- 操作按钮组 -->
      <div class="action-buttons">
        <!-- 语音输入按钮 -->
        <el-button
          :type="isRecording ? 'danger' : 'primary'"
          :icon="isRecording ? 'VideoCamera' : 'Microphone'"
          circle
          @click="toggleVoiceInput"
          :loading="isProcessingVoice"
        />
        
        <!-- 发送按钮 -->
        <el-button
          type="primary"
          :icon="Send"
          @click="handleSend"
          :disabled="!inputText.trim() || isLoading"
          :loading="isLoading"
        >
          发送
        </el-button>
      </div>
    </div>
    
    <!-- 快捷操作按钮 -->
    <div class="quick-actions">
      <el-button-group>
        <el-button size="small" @click="insertQuickText('查看今日销售')">
          📊 今日销售
        </el-button>
        <el-button size="small" @click="insertQuickText('检查库存预警')">
          ⚠️ 库存预警
        </el-button>
        <el-button size="small" @click="insertQuickText('添加商品')">
          ➕ 添加商品
        </el-button>
      </el-button-group>
    </div>
  </div>
</template>
```

### 2. 消息展示交互
```vue
<!-- AiMessage.vue -->
<template>
  <div class="ai-message">
    <div class="message-header">
      <el-avatar :size="32" src="/ai-avatar.png" />
      <span class="sender-name">AI助手</span>
      <span class="timestamp">{{ formatTime(message.timestamp) }}</span>
    </div>
    
    <div class="message-content">
      <!-- 文本内容 -->
      <div v-if="message.type === 'text'" class="text-content">
        <div v-html="formatMessage(message.content)"></div>
      </div>
      
      <!-- 数据表格 -->
      <div v-else-if="message.type === 'table'" class="table-content">
        <el-table :data="message.data" size="small" stripe>
          <el-table-column
            v-for="column in message.columns"
            :key="column.prop"
            :prop="column.prop"
            :label="column.label"
            :width="column.width"
          />
        </el-table>
      </div>
      
      <!-- 图表展示 -->
      <div v-else-if="message.type === 'chart'" class="chart-content">
        <div ref="chartContainer" class="chart-container"></div>
      </div>
      
      <!-- 智能表单 -->
      <div v-else-if="message.type === 'form'" class="form-content">
        <SmartForm
          :schema="message.formSchema"
          @submit="handleFormSubmit"
          @cancel="handleFormCancel"
        />
      </div>
    </div>
    
    <!-- 操作按钮 -->
    <div class="message-actions">
      <el-button-group size="small">
        <el-button @click="copyMessage" :icon="DocumentCopy">
          复制
        </el-button>
        <el-button @click="regenerateResponse" :icon="Refresh">
          重新生成
        </el-button>
        <el-button @click="provideFeedback" :icon="ChatDotRound">
          反馈
        </el-button>
      </el-button-group>
    </div>
  </div>
</template>
```

### 3. 智能表单交互
```vue
<!-- SmartForm.vue -->
<template>
  <div class="smart-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
    >
      <!-- 动态表单字段 -->
      <el-form-item
        v-for="field in schema.fields"
        :key="field.name"
        :label="field.label"
        :prop="field.name"
      >
        <!-- 文本输入 -->
        <el-input
          v-if="field.type === 'text'"
          v-model="formData[field.name]"
          :placeholder="field.placeholder"
        />
        
        <!-- 数字输入 -->
        <el-input-number
          v-else-if="field.type === 'number'"
          v-model="formData[field.name]"
          :min="field.min"
          :max="field.max"
          :precision="field.precision"
        />
        
        <!-- 选择器 -->
        <el-select
          v-else-if="field.type === 'select'"
          v-model="formData[field.name]"
          :placeholder="field.placeholder"
        >
          <el-option
            v-for="option in field.options"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>
        
        <!-- 日期选择 -->
        <el-date-picker
          v-else-if="field.type === 'date'"
          v-model="formData[field.name]"
          type="date"
          :placeholder="field.placeholder"
        />
      </el-form-item>
      
      <!-- 表单操作 -->
      <el-form-item>
        <el-button type="primary" @click="handleSubmit">
          {{ schema.submitText || '确认' }}
        </el-button>
        <el-button @click="handleCancel">
          取消
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
```

---

## 💻 技术实现

### 1. WebSocket连接管理
```typescript
// composables/useWebSocket.ts
export function useWebSocket() {
  const socket = ref<WebSocket | null>(null)
  const isConnected = ref(false)
  const reconnectAttempts = ref(0)
  const maxReconnectAttempts = 5
  
  const connect = () => {
    try {
      socket.value = new WebSocket(WS_URL)
      
      socket.value.onopen = () => {
        isConnected.value = true
        reconnectAttempts.value = 0
        console.log('WebSocket连接成功')
      }
      
      socket.value.onmessage = (event) => {
        const message = JSON.parse(event.data)
        handleMessage(message)
      }
      
      socket.value.onclose = () => {
        isConnected.value = false
        handleReconnect()
      }
      
      socket.value.onerror = (error) => {
        console.error('WebSocket错误:', error)
      }
    } catch (error) {
      console.error('WebSocket连接失败:', error)
    }
  }
  
  const handleReconnect = () => {
    if (reconnectAttempts.value < maxReconnectAttempts) {
      reconnectAttempts.value++
      setTimeout(() => {
        console.log(`尝试重连 (${reconnectAttempts.value}/${maxReconnectAttempts})`)
        connect()
      }, 2000 * reconnectAttempts.value)
    }
  }
  
  const sendMessage = (message: any) => {
    if (socket.value && isConnected.value) {
      socket.value.send(JSON.stringify(message))
    }
  }
  
  return {
    isConnected: readonly(isConnected),
    connect,
    sendMessage
  }
}
```

### 2. 语音输入集成
```typescript
// composables/useVoiceInput.ts
export function useVoiceInput() {
  const isRecording = ref(false)
  const isSupported = ref(false)
  const recognition = ref<SpeechRecognition | null>(null)
  
  onMounted(() => {
    // 检查浏览器支持
    if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
      isSupported.value = true
      const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
      recognition.value = new SpeechRecognition()
      
      recognition.value.continuous = false
      recognition.value.interimResults = false
      recognition.value.lang = 'zh-CN'
      
      recognition.value.onresult = (event) => {
        const transcript = event.results[0][0].transcript
        onVoiceResult(transcript)
      }
      
      recognition.value.onerror = (event) => {
        console.error('语音识别错误:', event.error)
        isRecording.value = false
      }
      
      recognition.value.onend = () => {
        isRecording.value = false
      }
    }
  })
  
  const startRecording = () => {
    if (recognition.value && !isRecording.value) {
      isRecording.value = true
      recognition.value.start()
    }
  }
  
  const stopRecording = () => {
    if (recognition.value && isRecording.value) {
      recognition.value.stop()
    }
  }
  
  return {
    isSupported: readonly(isSupported),
    isRecording: readonly(isRecording),
    startRecording,
    stopRecording
  }
}
```

### 3. 消息渲染优化
```typescript
// composables/useMessageRenderer.ts
export function useMessageRenderer() {
  const formatMessage = (content: string) => {
    // Markdown渲染
    let html = marked(content)
    
    // 代码高亮
    html = html.replace(/<pre><code class="language-(\w+)">([\s\S]*?)<\/code><\/pre>/g, 
      (match, lang, code) => {
        return `<pre><code class="hljs language-${lang}">${hljs.highlight(code, {language: lang}).value}</code></pre>`
      })
    
    // 表情符号
    html = html.replace(/:\w+:/g, (match) => {
      const emoji = emojiMap[match]
      return emoji || match
    })
    
    // 链接处理
    html = html.replace(/<a /g, '<a target="_blank" rel="noopener noreferrer" ')
    
    return html
  }
  
  const renderChart = (container: HTMLElement, data: ChartData) => {
    const chart = echarts.init(container)
    chart.setOption(data.options)
    
    // 响应式调整
    const resizeObserver = new ResizeObserver(() => {
      chart.resize()
    })
    resizeObserver.observe(container)
    
    return chart
  }
  
  return {
    formatMessage,
    renderChart
  }
}
```

---

## 🎨 样式设计

### 主题配置
```scss
// styles/ai-chat-theme.scss
:root {
  // 主色调
  --ai-primary: #409EFF;
  --ai-primary-light: #79BBFF;
  --ai-primary-dark: #337ECC;

  // 辅助色
  --ai-success: #67C23A;
  --ai-warning: #E6A23C;
  --ai-danger: #F56C6C;
  --ai-info: #909399;

  // 背景色
  --ai-bg-primary: #FFFFFF;
  --ai-bg-secondary: #F5F7FA;
  --ai-bg-chat: #F8F9FA;

  // 文字色
  --ai-text-primary: #303133;
  --ai-text-regular: #606266;
  --ai-text-secondary: #909399;

  // 边框色
  --ai-border-light: #EBEEF5;
  --ai-border-base: #DCDFE6;

  // 阴影
  --ai-shadow-light: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  --ai-shadow-base: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04);
}

// 暗色主题
[data-theme="dark"] {
  --ai-bg-primary: #1A1A1A;
  --ai-bg-secondary: #2D2D2D;
  --ai-bg-chat: #252525;
  --ai-text-primary: #E4E7ED;
  --ai-text-regular: #CFD3DC;
  --ai-text-secondary: #A4A9B0;
  --ai-border-light: #414243;
  --ai-border-base: #4C4D4F;
}
```

### 组件样式
```scss
// 聊天容器
.ai-chat-container {
  height: 100vh;
  background: var(--ai-bg-secondary);
  display: grid;
  grid-template-columns: 280px 1fr;

  .sidebar {
    background: var(--ai-bg-primary);
    border-right: 1px solid var(--ai-border-light);
    display: flex;
    flex-direction: column;

    .sidebar-header {
      padding: 16px;
      border-bottom: 1px solid var(--ai-border-light);

      h3 {
        margin: 0;
        color: var(--ai-text-primary);
        font-size: 16px;
        font-weight: 600;
      }
    }

    .sidebar-content {
      flex: 1;
      overflow-y: auto;
      padding: 8px;
    }
  }

  .main-chat {
    display: flex;
    flex-direction: column;
    background: var(--ai-bg-chat);

    .chat-header {
      padding: 16px 24px;
      background: var(--ai-bg-primary);
      border-bottom: 1px solid var(--ai-border-light);
      display: flex;
      align-items: center;
      justify-content: space-between;

      .session-info {
        display: flex;
        align-items: center;
        gap: 12px;

        .session-title {
          font-size: 16px;
          font-weight: 600;
          color: var(--ai-text-primary);
        }

        .session-status {
          padding: 2px 8px;
          border-radius: 12px;
          font-size: 12px;
          background: var(--ai-success);
          color: white;
        }
      }
    }

    .messages-container {
      flex: 1;
      overflow-y: auto;
      padding: 16px 24px;

      .message-group {
        margin-bottom: 24px;

        &.user-message {
          display: flex;
          justify-content: flex-end;

          .message-bubble {
            max-width: 70%;
            background: var(--ai-primary);
            color: white;
            padding: 12px 16px;
            border-radius: 18px 18px 4px 18px;
            box-shadow: var(--ai-shadow-light);
          }
        }

        &.ai-message {
          display: flex;
          justify-content: flex-start;
          align-items: flex-start;
          gap: 12px;

          .avatar {
            flex-shrink: 0;
            margin-top: 4px;
          }

          .message-content {
            max-width: 70%;
            background: var(--ai-bg-primary);
            padding: 12px 16px;
            border-radius: 18px 18px 18px 4px;
            box-shadow: var(--ai-shadow-light);
            border: 1px solid var(--ai-border-light);

            .typing-indicator {
              display: flex;
              align-items: center;
              gap: 4px;

              .dot {
                width: 8px;
                height: 8px;
                border-radius: 50%;
                background: var(--ai-primary);
                animation: typing 1.4s infinite ease-in-out;

                &:nth-child(1) { animation-delay: -0.32s; }
                &:nth-child(2) { animation-delay: -0.16s; }
              }
            }
          }
        }
      }
    }

    .input-container {
      padding: 16px 24px;
      background: var(--ai-bg-primary);
      border-top: 1px solid var(--ai-border-light);
    }
  }
}

// 动画效果
@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes slideInUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.message-enter-active {
  animation: slideInUp 0.3s ease-out;
}
```

---

## 📱 移动端适配

### 响应式布局
```vue
<!-- AiChatPage.vue -->
<template>
  <div class="ai-chat-page" :class="{ 'mobile': isMobile }">
    <!-- 移动端顶部导航 -->
    <div v-if="isMobile" class="mobile-header">
      <el-button
        :icon="Menu"
        @click="toggleSidebar"
        class="menu-button"
      />
      <h3 class="page-title">AI智能助手</h3>
      <el-button
        :icon="Setting"
        @click="showSettings"
        class="settings-button"
      />
    </div>

    <!-- 侧边栏遮罩 -->
    <div
      v-if="isMobile && sidebarVisible"
      class="sidebar-overlay"
      @click="closeSidebar"
    />

    <!-- 主要内容区域 -->
    <div class="chat-container">
      <AiChatSidebar
        :class="{ 'mobile-sidebar': isMobile }"
        :visible="sidebarVisible"
        @close="closeSidebar"
      />

      <AiChatMain class="chat-main" />
    </div>

    <!-- 移动端底部输入栏 -->
    <div v-if="isMobile" class="mobile-input-bar">
      <ChatInput @send="handleSend" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useWindowSize } from '@vueuse/core'

const { width } = useWindowSize()
const isMobile = computed(() => width.value < 768)
const sidebarVisible = ref(false)

const toggleSidebar = () => {
  sidebarVisible.value = !sidebarVisible.value
}

const closeSidebar = () => {
  sidebarVisible.value = false
}

// 移动端自动隐藏侧边栏
watch(isMobile, (newVal) => {
  if (newVal) {
    sidebarVisible.value = false
  } else {
    sidebarVisible.value = true
  }
}, { immediate: true })
</script>
```

### 触摸手势支持
```typescript
// composables/useTouch.ts
export function useTouch() {
  const startX = ref(0)
  const startY = ref(0)
  const currentX = ref(0)
  const currentY = ref(0)

  const onTouchStart = (event: TouchEvent) => {
    const touch = event.touches[0]
    startX.value = touch.clientX
    startY.value = touch.clientY
  }

  const onTouchMove = (event: TouchEvent) => {
    const touch = event.touches[0]
    currentX.value = touch.clientX
    currentY.value = touch.clientY
  }

  const onTouchEnd = () => {
    const deltaX = currentX.value - startX.value
    const deltaY = currentY.value - startY.value

    // 水平滑动检测
    if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > 50) {
      if (deltaX > 0) {
        // 向右滑动 - 打开侧边栏
        emit('swipeRight')
      } else {
        // 向左滑动 - 关闭侧边栏
        emit('swipeLeft')
      }
    }
  }

  return {
    onTouchStart,
    onTouchMove,
    onTouchEnd
  }
}
```

---

## 🔧 性能优化

### 虚拟滚动
```vue
<!-- VirtualMessageList.vue -->
<template>
  <div
    ref="containerRef"
    class="virtual-list-container"
    @scroll="handleScroll"
  >
    <div
      class="virtual-list-phantom"
      :style="{ height: totalHeight + 'px' }"
    />

    <div
      class="virtual-list-content"
      :style="{ transform: `translateY(${offsetY}px)` }"
    >
      <div
        v-for="item in visibleItems"
        :key="item.id"
        class="virtual-list-item"
        :style="{ height: itemHeight + 'px' }"
      >
        <ChatMessage :message="item" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'

const props = defineProps<{
  messages: ChatMessage[]
  itemHeight: number
}>()

const containerRef = ref<HTMLElement>()
const scrollTop = ref(0)
const containerHeight = ref(0)

// 计算可见区域
const visibleCount = computed(() =>
  Math.ceil(containerHeight.value / props.itemHeight) + 2
)

const startIndex = computed(() =>
  Math.floor(scrollTop.value / props.itemHeight)
)

const endIndex = computed(() =>
  Math.min(startIndex.value + visibleCount.value, props.messages.length)
)

const visibleItems = computed(() =>
  props.messages.slice(startIndex.value, endIndex.value)
)

const totalHeight = computed(() =>
  props.messages.length * props.itemHeight
)

const offsetY = computed(() =>
  startIndex.value * props.itemHeight
)

const handleScroll = (event: Event) => {
  scrollTop.value = (event.target as HTMLElement).scrollTop
}

onMounted(() => {
  if (containerRef.value) {
    containerHeight.value = containerRef.value.clientHeight
  }
})
</script>
```

### 消息缓存策略
```typescript
// utils/messageCache.ts
class MessageCache {
  private cache = new Map<string, ChatMessage[]>()
  private maxSize = 50 // 最大缓存会话数

  get(sessionId: string): ChatMessage[] | undefined {
    return this.cache.get(sessionId)
  }

  set(sessionId: string, messages: ChatMessage[]): void {
    // LRU策略：如果超出最大缓存数，删除最旧的
    if (this.cache.size >= this.maxSize) {
      const firstKey = this.cache.keys().next().value
      this.cache.delete(firstKey)
    }

    this.cache.set(sessionId, messages)
  }

  clear(): void {
    this.cache.clear()
  }

  // 预加载相邻会话
  preload(sessionIds: string[]): void {
    sessionIds.forEach(id => {
      if (!this.cache.has(id)) {
        // 异步加载消息
        this.loadMessages(id)
      }
    })
  }

  private async loadMessages(sessionId: string): Promise<void> {
    try {
      const messages = await api.getChatHistory(sessionId)
      this.set(sessionId, messages)
    } catch (error) {
      console.error('预加载消息失败:', error)
    }
  }
}

export const messageCache = new MessageCache()
```

---

## 🧪 用户体验优化

### 智能输入建议
```typescript
// composables/useSmartSuggestions.ts
export function useSmartSuggestions() {
  const suggestions = ref<string[]>([])
  const isLoading = ref(false)

  // 基于用户输入的智能建议
  const getSuggestions = useDebounceFn(async (input: string) => {
    if (input.length < 2) {
      suggestions.value = []
      return
    }

    isLoading.value = true

    try {
      // 本地快速匹配
      const localSuggestions = getLocalSuggestions(input)

      // AI智能建议
      const aiSuggestions = await api.getSmartSuggestions(input)

      // 合并并去重
      suggestions.value = [...new Set([...localSuggestions, ...aiSuggestions])]
        .slice(0, 5) // 最多显示5个建议

    } catch (error) {
      console.error('获取建议失败:', error)
      suggestions.value = getLocalSuggestions(input)
    } finally {
      isLoading.value = false
    }
  }, 300)

  const getLocalSuggestions = (input: string): string[] => {
    const commonQueries = [
      '查看今日销售情况',
      '检查库存预警',
      '添加新商品',
      '查询商品信息',
      '分析销售趋势',
      '生成财务报表',
      '设置促销活动',
      '管理供应商信息'
    ]

    return commonQueries.filter(query =>
      query.toLowerCase().includes(input.toLowerCase())
    )
  }

  return {
    suggestions: readonly(suggestions),
    isLoading: readonly(isLoading),
    getSuggestions
  }
}
```

### 错误处理与重试
```vue
<!-- ErrorBoundary.vue -->
<template>
  <div class="error-boundary">
    <div v-if="hasError" class="error-content">
      <el-result
        icon="warning"
        title="出现了一些问题"
        :sub-title="errorMessage"
      >
        <template #extra>
          <el-button type="primary" @click="retry">
            重试
          </el-button>
          <el-button @click="reportError">
            报告问题
          </el-button>
        </template>
      </el-result>
    </div>

    <slot v-else />
  </div>
</template>

<script setup lang="ts">
import { ref, onErrorCaptured } from 'vue'

const hasError = ref(false)
const errorMessage = ref('')
const originalError = ref<Error | null>(null)

onErrorCaptured((error: Error) => {
  hasError.value = true
  errorMessage.value = error.message || '未知错误'
  originalError.value = error

  // 错误上报
  console.error('组件错误:', error)

  return false // 阻止错误继续传播
})

const retry = () => {
  hasError.value = false
  errorMessage.value = ''
  originalError.value = null

  // 触发重新渲染
  nextTick(() => {
    // 重新加载数据或重置状态
  })
}

const reportError = () => {
  // 发送错误报告
  if (originalError.value) {
    api.reportError({
      message: originalError.value.message,
      stack: originalError.value.stack,
      userAgent: navigator.userAgent,
      timestamp: new Date().toISOString()
    })
  }

  ElMessage.success('错误报告已发送，感谢您的反馈！')
}
</script>
```

---

*本文档提供了完整的AI前端界面设计方案，涵盖了布局、组件、交互、样式、性能优化等各个方面*
```
