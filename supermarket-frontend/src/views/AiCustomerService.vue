<template>
  <div class="ai-customer-service">
    <!-- 头部 -->
    <div class="chat-header">
      <div class="header-left">
        <el-icon size="24" color="#007bff">
          <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
            <path fill="currentColor" d="M300 400a60 60 0 1 1-120 0 60 60 0 0 1 120 0zm424 0a60 60 0 1 1-120 0 60 60 0 0 1 120 0zm-400 308a28 28 0 1 1-56 0 28 28 0 0 1 56 0zm280 0a28 28 0 1 1-56 0 28 28 0 0 1 56 0zm-640-228h896v-96c0-88.4-100.3-160-224-160s-224 71.6-224 160v96zm0 64v320c0 53 43 96 96 96h640c53 0 96-43 96-96V544H64z"/>
          </svg>
        </el-icon>
        <div class="header-info">
          <h2>AI智能客服</h2>
          <p>我是您的专属AI助手，随时为您解答问题</p>
        </div>
      </div>
      
      <div class="header-right">
        <el-button text @click="showSidebar = !showSidebar">
          <el-icon><InfoFilled /></el-icon>
          会话信息
        </el-button>
      </div>
    </div>
    
    <!-- 主体内容 -->
    <div class="chat-container">
      <!-- 聊天区域 -->
      <div class="chat-main" :class="{ 'with-sidebar': showSidebar }">
        <!-- 消息列表 -->
        <div class="message-list" ref="messageListRef">
          <div v-if="messages.length === 0" class="welcome-message">
            <div class="welcome-content">
              <el-icon size="48" color="#007bff">
                <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
                  <path fill="currentColor" d="M300 400a60 60 0 1 1-120 0 60 60 0 0 1 120 0zm424 0a60 60 0 1 1-120 0 60 60 0 0 1 120 0zm-400 308a28 28 0 1 1-56 0 28 28 0 0 1 56 0zm280 0a28 28 0 1 1-56 0 28 28 0 0 1 56 0zm-640-228h896v-96c0-88.4-100.3-160-224-160s-224 71.6-224 160v96zm0 64v320c0 53 43 96 96 96h640c53 0 96-43 96-96V544H64z"/>
                </svg>
              </el-icon>
              <h3>欢迎使用AI智能客服</h3>
              <p>我可以帮助您解决以下问题：</p>
              <ul>
                <li>订单查询与处理</li>
                <li>商品咨询与推荐</li>
                <li>退换货服务</li>
                <li>优惠活动信息</li>
                <li>账户与支付问题</li>
                <li>配送与物流查询</li>
              </ul>
              <p>请输入您的问题，我会尽快为您解答！</p>
            </div>
          </div>
          
          <ChatMessage
            v-for="(message, index) in messages"
            :key="index"
            :content="message.content"
            :is-user="message.isUser"
            :timestamp="message.timestamp"
            :user-avatar="userStore.user?.avatar"
            @thumbs-up="handleThumbsUp(message)"
            @thumbs-down="handleThumbsDown(message)"
          />
          
          <!-- AI正在输入提示 -->
          <ChatMessage
            v-if="isAiTyping"
            content=""
            :is-user="false"
            :timestamp="new Date().toISOString()"
            :is-typing="true"
          />
        </div>
        
        <!-- 输入区域 -->
        <ChatInput
          :disabled="isLoading"
          :sending="isLoading"
          @send="handleSendMessage"
          @clear-chat="handleClearChat"
          @transfer-to-human="handleTransferToHuman"
        />
      </div>
      
      <!-- 侧边栏 -->
      <ChatSidebar
        v-if="showSidebar && currentSession"
        :session-info="currentSession"
        :messages="messages"
        @close="showSidebar = false"
        @transfer-to-human="handleTransferToHuman"
        @end-session="handleEndSession"
      />
    </div>
    
    <!-- 加载遮罩 -->
    <div v-if="initializing" class="loading-overlay">
      <el-loading-spinner />
      <p>正在初始化会话...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import ChatMessage from '@/components/AiChat/ChatMessage.vue'
import ChatInput from '@/components/AiChat/ChatInput.vue'
import ChatSidebar from '@/components/AiChat/ChatSidebar.vue'
import {
  getOrCreateSession,
  sendCustomerMessage,
  getSessionHistory,
  transferToHuman,
  endSession,
  createChatRequest,
  validateMessage,
  MessageType
} from '@/api/aiCustomer'
import type { 
  AiCustomerSessionVO, 
  AiCustomerChatRequest,
  AiMessageVO 
} from '@/api/aiCustomer'

// 消息接口
interface ChatMessage {
  id?: string
  content: string
  isUser: boolean
  timestamp: string
  intent?: string
  messageType?: string
}

// 状态管理
const userStore = useUserStore()
const messageListRef = ref<HTMLElement>()
const showSidebar = ref(false)
const initializing = ref(true)
const isLoading = ref(false)
const isAiTyping = ref(false)

// 会话数据
const currentSession = ref<AiCustomerSessionVO | null>(null)
const messages = reactive<ChatMessage[]>([])

// 初始化
onMounted(async () => {
  await initializeChat()
})

// 监听消息变化，自动滚动到底部
watch(messages, () => {
  nextTick(() => {
    scrollToBottom()
  })
}, { deep: true })

// 初始化聊天
const initializeChat = async () => {
  try {
    initializing.value = true
    
    // 确保用户信息已加载
    if (!userStore.user) {
      await userStore.getUserInfo()
    }
    
    if (!userStore.user) {
      ElMessage.error('请先登录')
      return
    }
    
    // 创建或获取会话
    // 创建或获取会话
    const response = await getOrCreateSession(
      userStore.user.id,
      userStore.user.realName || userStore.user.username,
      userStore.user.phone || userStore.user.email
    )
    
    currentSession.value = response.data
    
    // 加载历史消息
    if (currentSession.value.messageCount > 0) {
      await loadChatHistory()
    }
    
  } catch (error) {
    console.error('初始化会话失败:', error)
    ElMessage.error('初始化会话失败，请刷新页面重试')
  } finally {
    initializing.value = false
  }
}

// 加载聊天历史
const loadChatHistory = async () => {
  if (!currentSession.value) return
  
  try {
    const response = await getSessionHistory(currentSession.value.sessionId)
    const historyMessages = response.data || []
    
    messages.splice(0, messages.length)
    historyMessages.forEach((msg: any) => {
      messages.push({
        id: msg.messageId || msg.id,
        content: msg.content,
        isUser: msg.messageType === 'user' || msg.messageType === 1,
        timestamp: msg.createTime || msg.timestamp,
        intent: msg.intent,
        messageType: msg.messageType
      })
    })
  } catch (error) {
    console.error('加载历史消息失败:', error)
    ElMessage.warning('加载历史消息失败，但不影响正常使用')
  }
}

// 发送消息
const handleSendMessage = async (message: string) => {
  if (!currentSession.value || !userStore.user) return
  
  // 验证消息内容
  if (!validateMessage(message)) {
    ElMessage.error('消息内容不能为空且不能超过1000字符')
    return
  }
  
  // 添加用户消息
  const userMessage: ChatMessage = {
    content: message,
    isUser: true,
    timestamp: new Date().toISOString()
  }
  messages.push(userMessage)
  
  try {
    isLoading.value = true
    isAiTyping.value = true
    
    // 创建聊天请求
    const chatRequest = createChatRequest(
      userStore.user.id,
      userStore.user.realName || userStore.user.username,
      message,
      currentSession.value.sessionId,
      userStore.user.phone || userStore.user.email
    )
    
    const response = await sendCustomerMessage(chatRequest)
    
    // 添加AI回复
    const aiMessage: ChatMessage = {
      id: response.data.messageId,
      content: response.data.content,
      isUser: false,
      timestamp: response.data.createTime,
      intent: response.data.intent
    }
    messages.push(aiMessage)
    
    // 更新会话信息
    currentSession.value.messageCount += 2
    currentSession.value.lastMessageContent = response.data.content
    currentSession.value.lastMessageTime = response.data.createTime
    currentSession.value.needHumanIntervention = response.data.needHumanIntervention
    
    // 如果需要人工介入，显示提示
    if (response.data.needHumanIntervention) {
      ElMessage.warning('检测到您的问题可能需要人工客服协助，建议转接人工客服')
    }
    
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败，请重试')
    
    // 添加错误提示消息
    const errorMessage: ChatMessage = {
      content: '抱歉，我暂时无法回复您的消息，请稍后重试或联系人工客服。',
      isUser: false,
      timestamp: new Date().toISOString()
    }
    messages.push(errorMessage)
  } finally {
    isLoading.value = false
    isAiTyping.value = false
  }
}

// 清空对话
const handleClearChat = async () => {
  try {
    await ElMessageBox.confirm('确定要清空当前对话吗？', '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    messages.splice(0, messages.length)
    ElMessage.success('对话已清空')
  } catch {
    // 用户取消操作
  }
}

// 转接人工客服
const handleTransferToHuman = async () => {
  if (!currentSession.value) return
  
  try {
    await ElMessageBox.confirm('确定要转接人工客服吗？', '转接确认', {
      confirmButtonText: '确定转接',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    await transferToHuman(currentSession.value.sessionId, '用户主动请求转接')
    
    // 更新会话状态
    currentSession.value.status = 3
    currentSession.value.needHumanIntervention = true
    
    // 添加系统消息
    const systemMessage: ChatMessage = {
      content: '您的会话已转接至人工客服，请稍等片刻，客服人员会尽快为您服务。',
      isUser: false,
      timestamp: new Date().toISOString()
    }
    messages.push(systemMessage)
    
    ElMessage.success('已成功转接人工客服')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('转接人工客服失败:', error)
      ElMessage.error('转接失败，请重试')
    }
  }
}

// 结束会话
const handleEndSession = async () => {
  if (!currentSession.value) return
  
  try {
    await ElMessageBox.confirm('确定要结束当前会话吗？', '结束会话', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await endSession(currentSession.value.sessionId)
    
    // 更新会话状态
    currentSession.value.status = 2
    
    // 添加系统消息
    const systemMessage: ChatMessage = {
      content: '会话已结束，感谢您的使用！如有其他问题，请重新开始对话。',
      isUser: false,
      timestamp: new Date().toISOString()
    }
    messages.push(systemMessage)
    
    ElMessage.success('会话已结束')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('结束会话失败:', error)
      ElMessage.error('结束会话失败，请重试')
    }
  }
}

// 点赞处理
const handleThumbsUp = (message: ChatMessage) => {
  ElMessage.success('感谢您的反馈！')
  // 这里可以发送反馈到后端
}

// 点踩处理
const handleThumbsDown = (message: ChatMessage) => {
  ElMessage.info('我们会持续改进服务质量')
  // 这里可以发送反馈到后端
}

// 滚动到底部
const scrollToBottom = () => {
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}
</script>

<style scoped>
.ai-customer-service {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.chat-header {
  background: white;
  padding: 16px 24px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-info h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.header-info p {
  margin: 4px 0 0 0;
  font-size: 14px;
  color: #666;
}

.chat-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
}

.chat-main.with-sidebar {
  margin-right: 320px;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  scroll-behavior: smooth;
}

.welcome-message {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  min-height: 400px;
}

.welcome-content {
  text-align: center;
  max-width: 500px;
  padding: 40px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.welcome-content h3 {
  margin: 16px 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.welcome-content p {
  margin: 16px 0;
  font-size: 16px;
  color: #666;
  line-height: 1.6;
}

.welcome-content ul {
  text-align: left;
  margin: 20px 0;
  padding-left: 20px;
}

.welcome-content li {
  margin: 8px 0;
  font-size: 14px;
  color: #666;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.loading-overlay p {
  margin-top: 16px;
  font-size: 16px;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-main.with-sidebar {
    margin-right: 0;
  }
  
  .chat-header {
    padding: 12px 16px;
  }
  
  .header-info h2 {
    font-size: 18px;
  }
  
  .message-list {
    padding: 16px;
  }
  
  .welcome-content {
    padding: 24px;
    margin: 0 16px;
  }
}

/* 滚动条样式 */
.message-list::-webkit-scrollbar {
  width: 6px;
}

.message-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.message-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.message-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>