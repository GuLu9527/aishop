<template>
  <div class="ai-test-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>AI服务测试</h2>
      <p class="description">测试后端AI服务接口的功能和性能</p>
    </div>

    <!-- 测试面板 -->
    <div class="test-panel">
      <!-- 聊天测试区域 -->
      <el-card class="chat-card">
        <template #header>
          <div class="card-header">
            <span>AI对话测试</span>
            <el-button type="danger" size="small" @click="clearChat">清空对话</el-button>
          </div>
        </template>

        <!-- 聊天历史 -->
        <div class="chat-history" ref="chatHistoryRef">
          <div v-for="message in messages" :key="message.id" class="message" :class="message.type">
            <div class="message-content">
              <div class="message-text" :class="{ 'error': message.isError }">
                {{ message.content }}
              </div>
              <div class="message-time">{{ message.timestamp }}</div>
            </div>
          </div>
          
          <!-- 流式响应显示 -->
          <div v-if="isStreaming" class="message ai">
            <div class="message-content">
              <div class="message-text streaming">
                {{ streamingMessage }}
                <span class="cursor">|</span>
              </div>
              <div class="message-time">正在回复...</div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="input-area">
          <el-row :gutter="10">
            <el-col :span="4">
              <el-input
                v-model="form.chatId"
                placeholder="Chat ID (可选)"
                size="large"
              />
            </el-col>
            <el-col :span="16">
              <el-input
                v-model="form.prompt"
                placeholder="请输入测试内容..."
                size="large"
                @keyup.enter="sendMessage"
                :disabled="loading"
              />
            </el-col>
            <el-col :span="4">
              <el-button
                type="primary"
                size="large"
                @click="sendMessage"
                :loading="loading"
                style="width: 100%"
              >
                发送
              </el-button>
            </el-col>
          </el-row>
        </div>

        <!-- 快捷测试 -->
        <div class="quick-tests">
          <div class="quick-tests-title">快捷测试：</div>
          <el-button
            v-for="test in quickTests"
            :key="test.label"
            size="small"
            @click="quickTest(test.prompt)"
            :disabled="loading"
          >
            {{ test.label }}
          </el-button>
        </div>
      </el-card>

      <!-- 统计信息 -->
      <el-card class="stats-card">
        <template #header>
          <span>测试统计</span>
        </template>
        
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ stats.totalRequests }}</div>
              <div class="stat-label">总请求数</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ stats.successRequests }}</div>
              <div class="stat-label">成功请求</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ stats.failedRequests }}</div>
              <div class="stat-label">失败请求</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ stats.averageResponseTime }}ms</div>
              <div class="stat-label">平均响应时间</div>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { testAiService } from '../api/aiTest'

// 响应式数据
const form = reactive({
  prompt: '',
  chatId: 'test-chat-' + Date.now()
})

const loading = ref(false)
const isStreaming = ref(false)
const streamingMessage = ref('')
const chatHistoryRef = ref<HTMLElement>()

const messages = ref<Array<{
  id: number
  type: 'user' | 'ai'
  content: string
  timestamp: string
  isError?: boolean
}>>([])

const stats = reactive({
  totalRequests: 0,
  successRequests: 0,
  failedRequests: 0,
  totalResponseTime: 0,
  averageResponseTime: 0
})

// 快捷测试选项
const quickTests = ref([
  { label: '基础问候', prompt: '你好，请介绍一下你自己' },
  { label: '数学计算', prompt: '请计算 123 + 456 等于多少？' },
  { label: '超市业务', prompt: '如何管理超市的库存？' },
  { label: 'AI能力', prompt: '你能帮助我做什么？' },
  { label: '时间查询', prompt: '现在是几点？' }
])

// 发送消息
const sendMessage = async () => {
  if (!form.prompt.trim() || loading.value) {
    ElMessage.warning('请输入测试内容')
    return
  }

  loading.value = true
  isStreaming.value = true
  const startTime = Date.now()
  
  // 添加用户消息到对话历史
  const userMessage = {
    id: Date.now(),
    type: 'user' as const,
    content: form.prompt,
    timestamp: new Date().toLocaleTimeString()
  }
  messages.value.push(userMessage)

  const currentPrompt = form.prompt
  form.prompt = '' // 清空输入框
  streamingMessage.value = ''

  await scrollToBottom()

  try {
    // 模拟流式响应（因为实际的流式响应需要特殊处理）
    const response = await testAiService({
      prompt: currentPrompt,
      chatId: form.chatId || undefined
    })

    // 如果是流式响应，需要特殊处理
    if (response.data && typeof response.data === 'string') {
      // 模拟打字效果
      const text = response.data
      for (let i = 0; i < text.length; i++) {
        streamingMessage.value += text[i]
        await new Promise(resolve => setTimeout(resolve, 20))
        await scrollToBottom()
      }
    } else {
      streamingMessage.value = response.data || '收到AI响应'
    }

    // 添加AI响应到对话历史
    const aiMessage = {
      id: Date.now() + 1,
      type: 'ai' as const,
      content: streamingMessage.value,
      timestamp: new Date().toLocaleTimeString()
    }
    messages.value.push(aiMessage)

    const endTime = Date.now()
    const responseTime = endTime - startTime

    // 更新统计信息
    stats.totalRequests++
    stats.successRequests++
    stats.totalResponseTime += responseTime
    stats.averageResponseTime = Math.round(stats.totalResponseTime / stats.successRequests)

    ElMessage.success(`AI响应完成，耗时: ${responseTime}ms`)
  } catch (error: any) {
    console.error('AI服务测试失败:', error)
    
    // 添加错误消息
    const errorMessage = {
      id: Date.now() + 1,
      type: 'ai' as const,
      content: `错误: ${error.message || '请求失败'}`,
      timestamp: new Date().toLocaleTimeString(),
      isError: true
    }
    messages.value.push(errorMessage)
    
    // 更新统计信息
    stats.totalRequests++
    stats.failedRequests++
    
    ElMessage.error('AI服务测试失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
    isStreaming.value = false
    streamingMessage.value = ''
    await scrollToBottom()
  }
}

// 快捷测试
const quickTest = (testPrompt: string) => {
  form.prompt = testPrompt
  sendMessage()
}

// 清空对话
const clearChat = () => {
  messages.value = []
  stats.totalRequests = 0
  stats.successRequests = 0
  stats.failedRequests = 0
  stats.totalResponseTime = 0
  stats.averageResponseTime = 0
  form.chatId = 'test-chat-' + Date.now()
  ElMessage.success('对话已清空')
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (chatHistoryRef.value) {
    chatHistoryRef.value.scrollTop = chatHistoryRef.value.scrollHeight
  }
}

// 生命周期
onMounted(() => {
  // 添加欢迎消息
  messages.value.push({
    id: Date.now(),
    type: 'ai',
    content: '欢迎使用AI服务测试页面！您可以在这里测试AI聊天功能。',
    timestamp: new Date().toLocaleTimeString()
  })
})
</script>

<style scoped lang="scss">
.ai-test-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
  
  h2 {
    margin: 0 0 8px 0;
    color: #303133;
  }
  
  .description {
    margin: 0;
    color: #909399;
    font-size: 14px;
  }
}

.test-panel {
  display: grid;
  gap: 20px;
}

.chat-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.chat-history {
  height: 400px;
  overflow-y: auto;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 20px;
  
  .message {
    margin-bottom: 15px;
    display: flex;
    
    &.user {
      justify-content: flex-end;
      
      .message-content {
        background-color: #409eff;
        color: white;
        max-width: 70%;
      }
    }
    
    &.ai {
      justify-content: flex-start;
      
      .message-content {
        background-color: white;
        border: 1px solid #e4e7ed;
        max-width: 70%;
      }
    }
  }
  
  .message-content {
    padding: 10px 15px;
    border-radius: 12px;
    
    .message-text {
      margin-bottom: 5px;
      line-height: 1.5;
      
      &.error {
        color: #f56c6c;
      }
      
      &.streaming {
        .cursor {
          animation: blink 1s infinite;
        }
      }
    }
    
    .message-time {
      font-size: 12px;
      opacity: 0.7;
    }
  }
}

.input-area {
  margin-bottom: 15px;
}

.quick-tests {
  .quick-tests-title {
    margin-bottom: 10px;
    font-size: 14px;
    color: #606266;
  }
  
  .el-button {
    margin-right: 8px;
    margin-bottom: 8px;
  }
}

.stats-card {
  .stat-item {
    text-align: center;
    
    .stat-value {
      font-size: 24px;
      font-weight: bold;
      color: #409eff;
      margin-bottom: 5px;
    }
    
    .stat-label {
      font-size: 14px;
      color: #909399;
    }
  }
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}
</style>