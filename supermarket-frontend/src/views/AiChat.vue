<template>
  <div class="ai-chat-container">
    <!-- 简洁头部 -->
    <div class="chat-header">
      <div class="header-info">
        <div class="ai-avatar">🤖</div>
        <div class="ai-title">智能助手</div>
      </div>
      <div class="header-actions">
        <el-button text @click="clearChat" size="small">
          <el-icon><Delete /></el-icon>
        </el-button>
      </div>
    </div>

    <!-- 聊天区域 -->
    <div class="chat-messages" ref="messagesContainer">
      <!-- 欢迎消息 -->
      <div v-if="messages.length === 0" class="welcome-card">
        <div class="welcome-icon">💬</div>
        <h3>您好！我是超市智能助手</h3>
        <p>我可以帮您查看销售数据、管理库存、分析财务等</p>
        <div class="quick-suggestions">
          <el-button 
            v-for="suggestion in quickSuggestions" 
            :key="suggestion"
            size="small" 
            plain
            @click="sendMessage(suggestion)"
          >
            {{ suggestion }}
          </el-button>
        </div>
      </div>

      <!-- 消息列表 -->
      <div v-for="message in messages" :key="message.id" class="message-wrapper">
        <!-- 用户消息 -->
        <div v-if="message.role === 'USER'" class="message user-message">
          <div class="message-bubble">
            {{ message.content }}
          </div>
        </div>

        <!-- AI消息 -->
        <div v-else class="message ai-message">
          <div class="ai-avatar-small">🤖</div>
          <div class="message-bubble">
            <div v-html="formatAiMessage(message.content)"></div>
            
            <!-- 建议问题 -->
            <div v-if="message.metadata?.suggested_questions" class="suggestions">
              <el-button 
                v-for="question in message.metadata.suggested_questions.slice(0, 2)" 
                :key="question"
                size="small" 
                text
                @click="sendMessage(question)"
              >
                {{ question }}
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="isLoading" class="message ai-message">
        <div class="ai-avatar-small">🤖</div>
        <div class="message-bubble loading">
          <div class="typing-dots">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="chat-input">
      <div class="input-wrapper">
        <el-input
          v-model="inputMessage"
          placeholder="输入您的问题..."
          @keydown.enter.prevent="handleEnterKey"
          class="message-input"
        />
        <el-button 
          type="primary" 
          :disabled="!inputMessage.trim() || isLoading"
          @click="sendMessage(inputMessage)"
          class="send-btn"
        >
          <el-icon><Promotion /></el-icon>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete, Promotion } from '@element-plus/icons-vue'
import { chatWithAi } from '@/api/ai'

// 响应式数据
const messages = ref<any[]>([])
const inputMessage = ref('')
const isLoading = ref(false)
const messagesContainer = ref<HTMLElement>()

// 当前会话ID
const currentConversationId = ref<string>('')

// 用户信息
const currentUser = {
  id: 1,
  name: '用户'
}

// 快速建议
const quickSuggestions = [
  '今天销售怎么样？',
  '库存预警情况',
  '热销商品排行',
  '财务概况'
]

// 组件挂载
onMounted(() => {
  // 初始化
})

// 发送消息
const sendMessage = async (message: string) => {
  if (!message.trim() || isLoading.value) return

  // 添加用户消息
  const userMessage = {
    id: Date.now(),
    role: 'USER',
    content: message.trim(),
    createTime: new Date()
  }
  messages.value.push(userMessage)

  // 清空输入框
  inputMessage.value = ''
  isLoading.value = true

  // 滚动到底部
  await nextTick()
  scrollToBottom()

  try {
    // 调用AI接口
    const response = await chatWithAi({
      message: message.trim(),
      conversationId: currentConversationId.value,
      userId: currentUser.id,
      userName: currentUser.name,
      messageType: 1, // 1=文本, 2=语音
      createNewConversation: !currentConversationId.value
    })

    console.log('AI响应:', response.data) // 调试日志

    if (response.data.code === 200) {
      const aiResponse = response.data.data

      // 更新会话ID
      if (aiResponse.sessionId) {
        currentConversationId.value = aiResponse.sessionId
      }

      // 添加AI回复
      const aiMessage = {
        id: Date.now() + 1,
        role: 'ASSISTANT',
        content: aiResponse.message || '抱歉，我暂时无法处理您的请求。',
        createTime: new Date(),
        metadata: {
          suggested_questions: aiResponse.suggestions || [],
          intent: aiResponse.intent,
          entities: aiResponse.entities,
          actionResult: aiResponse.actionResult
        }
      }
      messages.value.push(aiMessage)

      // 滚动到底部
      await nextTick()
      scrollToBottom()

    } else {
      ElMessage.error(response.data.message || 'AI服务异常')
      
      // 添加错误消息
      const errorMessage = {
        id: Date.now() + 1,
        role: 'ASSISTANT',
        content: '抱歉，服务暂时不可用，请稍后重试。',
        createTime: new Date()
      }
      messages.value.push(errorMessage)
    }

  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('网络异常，请重试')
    
    // 添加错误消息
    const errorMessage = {
      id: Date.now() + 1,
      role: 'ASSISTANT',
      content: '网络连接异常，请检查网络后重试。',
      createTime: new Date()
    }
    messages.value.push(errorMessage)
  } finally {
    isLoading.value = false
  }
}

// 处理回车键
const handleEnterKey = (event: KeyboardEvent) => {
  if (event.shiftKey) return
  sendMessage(inputMessage.value)
}

// 清空聊天
const clearChat = () => {
  messages.value = []
  currentConversationId.value = ''
  ElMessage.success('聊天记录已清空')
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 格式化AI消息
const formatAiMessage = (content: string) => {
  return content
    .replace(/\n/g, '<br>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
}

// 监听消息变化，自动滚动
watch(messages, async () => {
  await nextTick()
  scrollToBottom()
}, { deep: true })
</script>

<style scoped>
.ai-chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 120px); /* 减去导航栏和页边距的高度 */
  max-width: 800px;
  margin: 0 auto;
  background: #f8f9fa;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

/* 头部 */
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: white;
  border-bottom: 1px solid #e9ecef;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.header-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ai-avatar {
  font-size: 20px;
}

.ai-title {
  font-weight: 600;
  color: #2c3e50;
}

/* 聊天区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 12px 16px;
  background: #f8f9fa;
  min-height: 0; /* 确保flex子项可以收缩 */
}

/* 欢迎卡片 */
.welcome-card {
  text-align: center;
  padding: 20px 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 12px;
}

.welcome-icon {
  font-size: 36px;
  margin-bottom: 12px;
}

.welcome-card h3 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 18px;
}

.welcome-card p {
  margin: 0 0 20px 0;
  color: #6c757d;
  font-size: 14px;
}

.quick-suggestions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

/* 消息样式 */
.message-wrapper {
  margin-bottom: 12px;
}

.message {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.user-message {
  justify-content: flex-end;
}

.ai-message {
  justify-content: flex-start;
}

.ai-avatar-small {
  font-size: 16px;
  margin-top: 4px;
}

.message-bubble {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.4;
}

.user-message .message-bubble {
  background: #007bff;
  color: white;
  border-bottom-right-radius: 4px;
}

.ai-message .message-bubble {
  background: white;
  color: #2c3e50;
  border: 1px solid #e9ecef;
  border-bottom-left-radius: 4px;
}

.loading .message-bubble {
  padding: 16px;
}

/* 建议按钮 */
.suggestions {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

/* 输入区域 */
.chat-input {
  padding: 16px;
  background: white;
  border-top: 1px solid #e9ecef;
}

.input-wrapper {
  display: flex;
  gap: 8px;
  align-items: flex-end;
}

.message-input {
  flex: 1;
}

.send-btn {
  height: 40px;
  width: 40px;
  border-radius: 50%;
  padding: 0;
}

/* 加载动画 */
.typing-dots {
  display: flex;
  gap: 4px;
  align-items: center;
}

.typing-dots span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #6c757d;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-dots span:nth-child(1) { animation-delay: -0.32s; }
.typing-dots span:nth-child(2) { animation-delay: -0.16s; }

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

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 4px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #dee2e6;
  border-radius: 2px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #adb5bd;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ai-chat-container {
    max-width: 100%;
  }
  
  .message-bubble {
    max-width: 85%;
  }
  
  .welcome-card {
    padding: 24px 16px;
  }
  
  .quick-suggestions {
    flex-direction: column;
    align-items: center;
  }
}
</style>