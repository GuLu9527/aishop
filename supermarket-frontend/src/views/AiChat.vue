<template>
  <div class="app-container">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-left">
        <i class="fas fa-shopping-cart header-icon"></i>
        <h1 class="header-title">智能超市客服</h1>
      </div>
      <div class="header-right">
        <button class="header-btn" @click="showChatHistory = true">
          <i class="fas fa-history"></i>
          <span>聊天记录</span>
        </button>
        <button class="header-btn" @click="showChatSettings = true">
          <i class="fas fa-cog"></i>
          <span>设置</span>
        </button>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main class="main-content">
      <!-- 左侧功能面板 -->
      <aside class="sidebar">
        <div class="sidebar-section">
          <h3 class="sidebar-title">
            <i class="fas fa-bolt"></i>
            快捷服务
          </h3>
          <div class="quick-actions">
            <button class="quick-btn" @click="sendMessage('今天的销售情况怎么样？')">
              <i class="fas fa-chart-line"></i>
              <span>销售查询</span>
            </button>
            <button class="quick-btn" @click="sendMessage('检查当前库存状况')">
              <i class="fas fa-boxes"></i>
              <span>库存管理</span>
            </button>
            <button class="quick-btn" @click="sendMessage('显示本月财务报表')">
              <i class="fas fa-calculator"></i>
              <span>财务概况</span>
            </button>
            <button class="quick-btn" @click="sendMessage('哪些商品需要补货？')">
              <i class="fas fa-exclamation-triangle"></i>
              <span>库存预警</span>
            </button>
          </div>
        </div>

        <div class="sidebar-section">
          <h3 class="sidebar-title">
            <i class="fas fa-star"></i>
            常见问题
          </h3>
          <div class="faq-list">
            <button class="faq-item" @click="sendMessage('热销商品排行榜')">
              <i class="fas fa-fire"></i>
              <span>热销商品</span>
            </button>
            <button class="faq-item" @click="sendMessage('本周财务概况')">
              <i class="fas fa-chart-pie"></i>
              <span>周报统计</span>
            </button>
            <button class="faq-item" @click="sendMessage('员工绩效统计')">
              <i class="fas fa-users"></i>
              <span>员工绩效</span>
            </button>
            <button class="faq-item" @click="sendMessage('给我一些经营优化建议')">
              <i class="fas fa-lightbulb"></i>
              <span>经营建议</span>
            </button>
          </div>
        </div>
      </aside>

      <!-- 聊天区域 -->
      <section class="chat-container">
        <div class="chat-header">
          <div class="ai-status">
            <div class="status-indicator online"></div>
            <span class="status-text">AI客服在线</span>
          </div>
          <button class="clear-chat-btn" @click="clearChat">
            <i class="fas fa-trash"></i>
            清空对话
          </button>
        </div>

        <div class="chat-messages" id="chatMessages" ref="messagesContainer">
          <!-- 欢迎消息 -->
          <div v-if="messages.length === 0" class="welcome-message">
            <div class="ai-avatar">
              <i class="fas fa-robot"></i>
            </div>
            <div class="message-content">
              <p>您好！欢迎来到智能超市，我是您的专属AI客服助手。</p>
              <p>我可以帮您：</p>
              <ul>
                <li>🔍 查询销售数据和财务信息</li>
                <li>🎯 管理库存和商品信息</li>
                <li>📊 分析经营数据和趋势</li>
                <li>⚙️ 提供智能管理建议</li>
              </ul>
              <p>请问有什么可以帮助您的吗？</p>
            </div>
          </div>

          <!-- 消息列表 -->
          <div v-for="message in messages" :key="message.id" 
               :class="message.role === 'USER' ? 'user-message' : 'ai-message'">
            <div v-if="message.role === 'ASSISTANT'" class="ai-avatar">
              <i class="fas fa-robot"></i>
            </div>
            <div class="message-content">
              <div class="message-text" v-html="formatMessage(message.content)"></div>
              <div class="message-time">{{ formatTime(message.createTime) }}</div>
              <div v-if="message.metadata?.actionResult" class="action-result">
                <div class="result-header">
                  <i class="fas fa-chart-bar"></i>
                  操作结果
                </div>
                <pre class="result-content">{{ formatActionResult(message.metadata.actionResult) }}</pre>
              </div>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="isLoading" class="ai-message">
            <div class="ai-avatar">
              <i class="fas fa-robot"></i>
            </div>
            <div class="message-content typing-indicator">
              <div class="typing-dot"></div>
              <div class="typing-dot"></div>
              <div class="typing-dot"></div>
            </div>
          </div>
        </div>

        <div class="chat-input-container">
          <div class="input-wrapper">
            <input 
              type="text" 
              v-model="inputMessage"
              class="message-input" 
              placeholder="请输入您的问题..." 
              maxlength="500"
              @keyup.enter="sendMessage(inputMessage)"
              :disabled="isLoading"
            >
            <button class="send-btn" @click="sendMessage(inputMessage)" :disabled="!inputMessage.trim() || isLoading">
              <i class="fas fa-paper-plane"></i>
            </button>
          </div>
          <div class="input-tools">
            <button class="tool-btn">
              <i class="fas fa-microphone"></i>
            </button>
            <button class="tool-btn">
              <i class="fas fa-smile"></i>
            </button>
            <span class="char-count">{{ inputMessage.length }}/500</span>
          </div>
        </div>
      </section>
    </main>

    <!-- 聊天记录模态框 -->
    <div class="modal" :class="{ show: showChatHistory }" @click="closeModalOnBackdrop">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>聊天记录</h2>
          <button class="close-btn" @click="showChatHistory = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="history-controls">
            <input type="text" class="search-input" placeholder="搜索聊天记录...">
            <button class="clear-history-btn" @click="clearAllHistory">
              <i class="fas fa-trash"></i>
              清空记录
            </button>
          </div>
          <div class="history-list">
            <div v-if="isLoadingHistory" class="loading-history">
              <i class="fas fa-spinner fa-spin"></i>
              <p>加载聊天记录中...</p>
            </div>
            <div v-else-if="userConversations.length === 0" class="empty-history">
              <i class="fas fa-comments"></i>
              <p>暂无聊天记录</p>
              <p class="empty-hint">开始和AI助手对话吧，您的聊天记录会自动保存在这里</p>
            </div>
            <div v-else>
              <div v-for="conversation in userConversations" :key="conversation.sessionId || conversation.id" 
                   class="history-item" @click="loadConversation(conversation)">
                <div class="history-item-header">
                  <strong>{{ conversation.title || '新会话' }}</strong>
                  <span class="history-date">{{ formatTime(conversation.createTime) }}</span>
                </div>
                <div class="history-preview">
                  状态: {{ conversation.status === 'ACTIVE' ? '进行中' : '已结束' }} 
                  • 消息数量: {{ conversation.messageCount || 0 }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 设置模态框 -->
    <div class="modal" :class="{ show: showChatSettings }" @click="closeModalOnBackdrop">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>设置</h2>
          <button class="close-btn" @click="showChatSettings = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="setting-item">
            <label class="setting-label">
              <input type="checkbox" v-model="chatSettings.soundEnabled">
              <span class="checkmark"></span>
              消息提示音
            </label>
          </div>
          <div class="setting-item">
            <label class="setting-label">
              <input type="checkbox" v-model="chatSettings.enableTypingEffect">
              <span class="checkmark"></span>
              智能打字效果
            </label>
          </div>
          <div class="setting-item">
            <label class="setting-label">
              <input type="checkbox" v-model="chatSettings.saveHistory">
              <span class="checkmark"></span>
              保存聊天记录
            </label>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { chatWithAi, chatStreamWithAi, getQuickActions, getSmartSuggestions, getUserConversations, getChatHistory, deleteConversation } from '@/api/ai'

// 响应式数据
const messages = ref<any[]>([])
const inputMessage = ref('')
const isLoading = ref(false)
const isLoadingHistory = ref(false)
const messagesContainer = ref<HTMLElement>()
const showChatHistory = ref(false)
const showChatSettings = ref(false)
const userConversations = ref<any[]>([])
const chatSettings = ref({
  showTimestamp: true,
  showMessageStats: false,
  showSuggestions: true,
  enableTypingEffect: true,
  autoScroll: true,
  soundEnabled: false,
  enterToSend: true,
  fontSize: 14,
  userAvatar: '👤',
  displayName: '管理员',
  saveHistory: true
})

// 当前会话ID
const currentConversationId = ref<string>('')

// 用户信息
const currentUser = {
  id: 1,
  name: '管理员'
}

// 组件挂载
onMounted(async () => {
  // 加载聊天设置
  loadChatSettings()
  
  // 加载最新会话和聊天历史
  await loadLatestConversation()
})

// 加载最新会话和聊天历史
const loadLatestConversation = async () => {
  isLoadingHistory.value = true
  try {
    // 获取用户的会话列表
    const conversationResponse = await getUserConversations(currentUser.id)
    console.log('获取会话列表响应:', conversationResponse)
    
    // 检查响应结构并获取数据
    let conversations = []
    if (conversationResponse.data) {
      if (conversationResponse.data.code === 200 && conversationResponse.data.data) {
        conversations = conversationResponse.data.data
      } else if (Array.isArray(conversationResponse.data)) {
        conversations = conversationResponse.data
      }
    }
    
    if (conversations.length > 0) {
      userConversations.value = conversations
      
      // 获取最新的会话
      const latestConversation = conversations[0]
      currentConversationId.value = latestConversation.sessionId || latestConversation.id
      
      console.log('加载最新会话:', latestConversation)
      
      // 加载该会话的聊天历史
      const historyResponse = await getChatHistory(currentConversationId.value, currentUser.id, 50)
      console.log('获取聊天历史响应:', historyResponse)
      
      // 检查历史消息响应结构
      let historyMessages = []
      if (historyResponse.data) {
        if (historyResponse.data.code === 200 && historyResponse.data.data) {
          historyMessages = historyResponse.data.data
        } else if (Array.isArray(historyResponse.data)) {
          historyMessages = historyResponse.data
        }
      }
      
      if (historyMessages.length > 0) {
        // 转换消息格式
        const formattedMessages = historyMessages.map((msg: any) => ({
          id: msg.id,
          role: msg.messageType === 1 ? 'USER' : 'ASSISTANT',
          content: msg.content,
          createTime: new Date(msg.createTime),
          metadata: {
            intent: msg.intent,
            entities: msg.entities,
            action: msg.action,
            actionResult: msg.actionResult,
            suggested_questions: []
          }
        }))
        
        messages.value = formattedMessages
        console.log('加载聊天历史成功:', formattedMessages.length, '条消息')
        
        // 滚动到底部
        await nextTick()
        scrollToBottom()
      } else {
        console.log('没有找到历史消息')
      }
    } else {
      console.log('没有找到会话记录')
    }
  } catch (error) {
    console.error('加载会话历史失败:', error)
    // 不显示错误消息，静默失败
  } finally {
    isLoadingHistory.value = false
  }
}

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
    // 创建AI消息占位符
    const aiMessage = {
      id: Date.now() + 1,
      role: 'ASSISTANT',
      content: '',
      createTime: new Date(),
      isStreaming: true,
      metadata: {
        suggested_questions: [],
        intent: '',
        entities: {},
        actionResult: null
      }
    }
    messages.value.push(aiMessage)

    // 滚动到底部
    await nextTick()
    scrollToBottom()

    // 使用流式聊天
    chatStreamWithAi(
      {
        message: message.trim(),
        conversationId: currentConversationId.value,
        userId: currentUser.id,
        userName: currentUser.name,
        messageType: 1,
        createNewConversation: !currentConversationId.value
      },
      // onMessage: 接收流式数据
      (chunk: string) => {
        console.log('Received chunk:', chunk) // 调试日志
        
        // 找到对应的消息对象并更新
        const messageIndex = messages.value.findIndex(msg => msg.id === aiMessage.id)
        if (messageIndex !== -1) {
          // 直接更新响应式数组中的内容
          messages.value[messageIndex].content += chunk
          messages.value[messageIndex].isStreaming = true
          
          // 触发响应式更新
          messages.value = [...messages.value]
        }
        
        // 滚动到底部
        nextTick().then(() => {
          scrollToBottom()
        })
      },
      // onError: 处理错误
      (error: any) => {
        console.error('流式聊天错误:', error)
        
        const messageIndex = messages.value.findIndex(msg => msg.id === aiMessage.id)
        if (messageIndex !== -1) {
          if (messages.value[messageIndex].content.trim() === '') {
            messages.value[messageIndex].content = '抱歉，AI服务暂时不可用，请稍后重试。'
          }
          messages.value[messageIndex].isStreaming = false
          messages.value = [...messages.value]
        }
        ElMessage.error('AI服务异常')
      },
      // onComplete: 完成
      () => {
        console.log('Stream completed, final content:', aiMessage.content) // 调试日志
        
        const messageIndex = messages.value.findIndex(msg => msg.id === aiMessage.id)
        if (messageIndex !== -1) {
          messages.value[messageIndex].isStreaming = false
          messages.value = [...messages.value]
        }
        
        // 如果是新创建的会话，可能需要更新conversationId
        if (!currentConversationId.value && currentUser.id) {
          // 获取用户最新的会话ID
          getUserConversations(currentUser.id).then(response => {
            console.log('更新会话ID响应:', response)
            
            let conversations = []
            if (response.data) {
              if (response.data.code === 200 && response.data.data) {
                conversations = response.data.data
              } else if (Array.isArray(response.data)) {
                conversations = response.data
              }
            }
            
            if (conversations.length > 0) {
              const latestConversation = conversations[0]
              const newConversationId = latestConversation.sessionId || latestConversation.id
              if (newConversationId !== currentConversationId.value) {
                currentConversationId.value = newConversationId
                console.log('更新会话ID:', currentConversationId.value)
              }
            }
          }).catch(error => {
            console.error('获取会话列表失败:', error)
          })
        }
        
        // 确保最终状态正确
        nextTick(() => {
          scrollToBottom()
        })
      }
    )

  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('网络异常，请重试')
  } finally {
    isLoading.value = false
  }
}

// 清空聊天
const clearChat = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有聊天记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    messages.value = []
    currentConversationId.value = ''
    ElMessage.success('聊天记录已清空')
  } catch {
    // 用户取消
  }
}

// 清空所有历史记录
const clearAllHistory = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有历史记录吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 删除所有会话记录
    const deletePromises = userConversations.value.map(conversation => {
      const conversationId = conversation.sessionId || conversation.id
      return deleteConversation(conversationId, currentUser.id)
    })
    
    // 等待所有删除操作完成
    await Promise.all(deletePromises)
    
    // 清空本地数据
    userConversations.value = []
    messages.value = []
    currentConversationId.value = ''
    showChatHistory.value = false
    
    ElMessage.success('历史记录已清空')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空历史记录失败:', error)
      ElMessage.error('清空历史记录失败')
    }
  }
}

// 加载会话
const loadConversation = async (conversation: any) => {
  try {
    currentConversationId.value = conversation.sessionId || conversation.id
    showChatHistory.value = false
    
    // 加载该会话的聊天历史
    const historyResponse = await getChatHistory(currentConversationId.value, currentUser.id, 50)
    console.log('加载会话历史响应:', historyResponse)
    
    // 检查历史消息响应结构
    let historyMessages = []
    if (historyResponse.data) {
      if (historyResponse.data.code === 200 && historyResponse.data.data) {
        historyMessages = historyResponse.data.data
      } else if (Array.isArray(historyResponse.data)) {
        historyMessages = historyResponse.data
      }
    }
    
    if (historyMessages.length > 0) {
      // 转换消息格式
      const formattedMessages = historyMessages.map((msg: any) => ({
        id: msg.id,
        role: msg.messageType === 1 ? 'USER' : 'ASSISTANT',
        content: msg.content,
        createTime: new Date(msg.createTime),
        metadata: {
          intent: msg.intent,
          entities: msg.entities,
          action: msg.action,
          actionResult: msg.actionResult,
          suggested_questions: []
        }
      }))
      
      messages.value = formattedMessages
      console.log('切换会话成功，加载', formattedMessages.length, '条历史消息')
      
      // 滚动到底部
      await nextTick()
      scrollToBottom()
    } else {
      // 清空当前消息，显示空会话
      messages.value = []
      console.log('该会话没有历史消息')
    }
  } catch (error) {
    console.error('加载会话失败:', error)
    ElMessage.error('加载会话失败')
  }
}

// 关闭模态框
const closeModalOnBackdrop = (event: Event) => {
  if (event.target === event.currentTarget) {
    showChatHistory.value = false
    showChatSettings.value = false
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 格式化消息
const formatMessage = (content: string) => {
  return content.replace(/\n/g, '<br>')
}

// 格式化时间
const formatTime = (time: Date | string) => {
  if (!time) return '--'
  try {
    const date = new Date(time)
    if (isNaN(date.getTime())) return '--'
    return date.toLocaleString('zh-CN', {
      hour: '2-digit',
      minute: '2-digit',
      month: '2-digit',
      day: '2-digit'
    })
  } catch (error) {
    console.error('时间格式化错误:', error)
    return '--'
  }
}

// 格式化操作结果
const formatActionResult = (result: any) => {
  if (!result) return ''
  return JSON.stringify(result, null, 2)
}

// 加载聊天设置
const loadChatSettings = () => {
  try {
    const saved = localStorage.getItem('chatSettings')
    if (saved) {
      chatSettings.value = { ...chatSettings.value, ...JSON.parse(saved) }
    }
  } catch (error) {
    console.error('加载设置失败:', error)
  }
}

// 监听设置变化
watch(chatSettings, (newSettings) => {
  localStorage.setItem('chatSettings', JSON.stringify(newSettings))
}, { deep: true })
</script>

<style scoped>
/* 引入原型样式 */
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css');

/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.app-container {
  height: 80vh;
  max-height: 800px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  background: #ffffff;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* 顶部导航栏 */
.header {
  background: linear-gradient(135deg, #409EFF 0%, #337ECC 100%);
  color: white;
  padding: 0 20px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  border-radius: 8px 8px 0 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  font-size: 24px;
  color: #67C23A;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.header-right {
  display: flex;
  gap: 12px;
}

.header-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

.header-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

/* 主要内容区域 */
.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 左侧边栏 */
.sidebar {
  width: 240px;
  background: #ffffff;
  border-right: 1px solid #e4e7ed;
  padding: 16px;
  overflow-y: auto;
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.05);
}

.sidebar-section {
  margin-bottom: 24px;
}

.sidebar-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.sidebar-title i {
  color: #409EFF;
}

/* 快捷操作按钮 */
.quick-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.quick-btn {
  background: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 12px 8px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  text-align: center;
  color: #606266;
}

.quick-btn:hover {
  border-color: #409EFF;
  background: #ecf5ff;
  color: #409EFF;
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.quick-btn i {
  font-size: 18px;
}

.quick-btn span {
  font-size: 11px;
  font-weight: 500;
}

/* 常见问题列表 */
.faq-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.faq-item {
  background: #ffffff;
  border: 1px solid #f0f0f0;
  border-radius: 6px;
  padding: 10px 12px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 10px;
  color: #606266;
  text-align: left;
  font-size: 13px;
}

.faq-item:hover {
  border-color: #c6e2ff;
  background: #ecf5ff;
  color: #409EFF;
}

.faq-item i {
  font-size: 16px;
  width: 20px;
  text-align: center;
}

/* 聊天容器 */
.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  overflow: hidden;
}

.chat-header {
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #ffffff;
}

.ai-status {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #67C23A;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}

.status-text {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.clear-chat-btn {
  background: none;
  border: 1px solid #DCDFE6;
  color: #909399;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
}

.clear-chat-btn:hover {
  border-color: #F56C6C;
  color: #F56C6C;
  background: #fef0f0;
}

/* 聊天消息区域 */
.chat-messages {
  flex: 1;
  padding: 16px 20px;
  overflow-y: auto;
  scroll-behavior: smooth;
  min-height: 300px;
}

.welcome-message {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  animation: fadeInUp 0.5s ease-out;
}

.ai-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF, #79bbff);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  flex-shrink: 0;
}

.message-content {
  background: #f5f7fa;
  border-radius: 12px 12px 12px 4px;
  padding: 16px;
  max-width: 70%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.message-content p {
  margin-bottom: 8px;
  line-height: 1.5;
}

.message-content ul {
  margin: 8px 0;
  padding-left: 20px;
}

.message-content li {
  margin-bottom: 4px;
}

/* 用户消息样式 */
.user-message {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
  animation: fadeInUp 0.3s ease-out;
}

.user-message .message-content {
  background: #409EFF;
  color: white;
  border-radius: 12px 12px 4px 12px;
  max-width: 70%;
}

/* AI消息样式 */
.ai-message {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  animation: fadeInUp 0.3s ease-out;
}

.ai-message .message-content {
  background: #f5f7fa;
  border-radius: 12px 12px 12px 4px;
  max-width: 70%;
}

/* 消息时间戳 */
.message-time {
  font-size: 11px;
  color: #c0c4cc;
  margin-top: 4px;
  text-align: right;
}

.ai-message .message-time {
  text-align: left;
}

/* 操作结果显示 */
.action-result {
  margin-top: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  background: #fafafa;
}

.result-header {
  padding: 8px 12px;
  background: #f0f2f5;
  border-bottom: 1px solid #e4e7ed;
  font-size: 12px;
  font-weight: 500;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 6px;
}

.result-content {
  padding: 12px;
  font-size: 12px;
  background: #ffffff;
  border-radius: 0 0 6px 6px;
  max-height: 200px;
  overflow-y: auto;
  white-space: pre-wrap;
  font-family: 'Monaco', 'Menlo', monospace;
}

/* 聊天输入区域 */
.chat-input-container {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  background: #ffffff;
}

.input-wrapper {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 8px;
}

.message-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #DCDFE6;
  border-radius: 20px;
  outline: none;
  font-size: 14px;
  transition: all 0.3s;
  background: #ffffff;
}

.message-input:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.send-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #409EFF;
  border: none;
  color: white;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.send-btn:hover {
  background: #79bbff;
  transform: scale(1.05);
}

.send-btn:disabled {
  background: #c0c4cc;
  cursor: not-allowed;
  transform: none;
}

.input-tools {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tool-btn {
  background: none;
  border: none;
  color: #909399;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.3s;
  font-size: 14px;
}

.tool-btn:hover {
  color: #409EFF;
  background: #ecf5ff;
}

.char-count {
  font-size: 12px;
  color: #c0c4cc;
}

/* 模态框样式 */
.modal {
  display: none;
  position: fixed;
  z-index: 2000;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
}

.modal.show {
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fadeIn 0.3s ease-out;
}

.modal-content {
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow: hidden;
  animation: slideInUp 0.3s ease-out;
}

.modal-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f5f7fa;
}

.modal-header h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #909399;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.3s;
}

.close-btn:hover {
  color: #F56C6C;
  background: #fef0f0;
}

.modal-body {
  padding: 24px;
  max-height: 60vh;
  overflow-y: auto;
}

/* 聊天记录样式 */
.history-controls {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  align-items: center;
}

.search-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #DCDFE6;
  border-radius: 6px;
  outline: none;
  font-size: 14px;
}

.search-input:focus {
  border-color: #409EFF;
}

.clear-history-btn {
  background: none;
  border: 1px solid #F56C6C;
  color: #F56C6C;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
}

.clear-history-btn:hover {
  background: #fef0f0;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.empty-history {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
}

.empty-history i {
  font-size: 48px;
  margin-bottom: 16px;
  color: #c0c4cc;
}

.empty-hint {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 8px;
  line-height: 1.4;
}

.loading-history {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
}

.loading-history i {
  font-size: 24px;
  margin-bottom: 12px;
  color: #409EFF;
}

.history-item {
  background: #f5f7fa;
  border-radius: 6px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #f0f0f0;
}

.history-item:hover {
  background: #ecf5ff;
  border-color: #c6e2ff;
}

.history-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.history-date {
  font-size: 12px;
  color: #909399;
}

.history-preview {
  font-size: 14px;
  color: #606266;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 设置页面样式 */
.setting-item {
  margin-bottom: 20px;
}

.setting-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
}

.setting-label input[type="checkbox"] {
  display: none;
}

.checkmark {
  width: 16px;
  height: 16px;
  border: 2px solid #DCDFE6;
  border-radius: 2px;
  margin-right: 12px;
  position: relative;
  transition: all 0.3s;
}

.setting-label input[type="checkbox"]:checked + .checkmark {
  background: #409EFF;
  border-color: #409EFF;
}

.setting-label input[type="checkbox"]:checked + .checkmark::after {
  content: '';
  position: absolute;
  left: 4px;
  top: 1px;
  width: 4px;
  height: 8px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

/* 打字效果 */
.typing-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 12px 12px 12px 4px;
  margin-bottom: 16px;
}

.typing-dot {
  width: 6px;
  height: 6px;
  background: #c0c4cc;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-dot:nth-child(1) { animation-delay: -0.32s; }
.typing-dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f5f7fa;
}

::-webkit-scrollbar-thumb {
  background: #DCDFE6;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #c0c4cc;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    height: auto;
    border-right: none;
    border-bottom: 1px solid #e4e7ed;
  }
  
  .quick-actions {
    grid-template-columns: repeat(4, 1fr);
  }
  
  .quick-btn {
    padding: 12px 8px;
  }
  
  .quick-btn span {
    font-size: 10px;
  }
  
  .chat-messages {
    padding: 16px;
  }
  
  .message-content {
    max-width: 85%;
  }
  
  .modal-content {
    width: 95%;
    margin: 20px;
  }
  
  .header {
    padding: 0 16px;
  }
  
  .header-title {
    font-size: 18px;
  }
  
  .header-btn span {
    display: none;
  }
}
</style>