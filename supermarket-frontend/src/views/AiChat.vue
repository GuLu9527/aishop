<template>
  <div class="ai-chat-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <div class="header-title">
            <el-icon class="header-icon"><ChatDotRound /></el-icon>
            <h2>AI智能客服</h2>
          </div>
          <p class="header-desc">智能助手为您提供超市管理咨询服务</p>
        </div>
        <div class="header-right">
          <el-button @click="showChatHistory = true">
            <el-icon><Clock /></el-icon>
            聊天记录
          </el-button>
          <el-button @click="showChatSettings = true">
            <el-icon><Setting /></el-icon>
            设置
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧功能面板 -->
      <div class="quick-panel">
        <div class="panel-section">
          <div class="section-header">
            <h3 class="section-title">
              ⚡ 快捷服务
            </h3>
          </div>
          <div class="quick-actions">
            <div class="action-item" @click="sendMessage('今天的销售情况怎么样？')">
              <div class="action-icon sales">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <div class="action-content">
                <div class="action-title">销售查询</div>
                <div class="action-desc">查看销售数据</div>
              </div>
            </div>
            <div class="action-item" @click="sendMessage('检查当前库存状况')">
              <div class="action-icon inventory">
                <el-icon><Goods /></el-icon>
              </div>
              <div class="action-content">
                <div class="action-title">库存管理</div>
                <div class="action-desc">查看库存状态</div>
              </div>
            </div>
            <div class="action-item" @click="sendMessage('显示本月财务报表')">
              <div class="action-icon finance">
                <el-icon><Money /></el-icon>
              </div>
              <div class="action-content">
                <div class="action-title">财务概况</div>
                <div class="action-desc">查看财务报表</div>
              </div>
            </div>
            <div class="action-item" @click="sendMessage('哪些商品需要补货？')">
              <div class="action-icon warning">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="action-content">
                <div class="action-title">库存预警</div>
                <div class="action-desc">检查预警信息</div>
              </div>
            </div>
          </div>
        </div>

        <div class="panel-section">
          <div class="section-header">
            <h3 class="section-title">
              <el-icon><Star /></el-icon>
              常见问题
            </h3>
          </div>
          <div class="faq-list">
            <div class="faq-item" @click="sendMessage('热销商品排行榜')">
              🏆 <span class="faq-text">热销商品排行</span>
            </div>
            <div class="faq-item" @click="sendMessage('本周财务概况')">
              <el-icon class="faq-icon"><PieChart /></el-icon>
              <span class="faq-text">周报财务统计</span>
            </div>
            <div class="faq-item" @click="sendMessage('员工绩效统计')">
              <el-icon class="faq-icon"><User /></el-icon>
              <span class="faq-text">员工绩效分析</span>
            </div>
            <div class="faq-item" @click="sendMessage('给我一些经营优化建议')">
              💡 <span class="faq-text">经营优化建议</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 聊天区域 -->
      <div class="chat-container">
        <div class="chat-header">
          <div class="ai-status">
            <div class="status-indicator online"></div>
            <span class="status-text">AI客服在线</span>
            <el-tag size="small" type="success">实时响应</el-tag>
          </div>
          <el-button size="small" type="danger" plain @click="clearChat">
            <el-icon><Delete /></el-icon>
            清空对话
          </el-button>
        </div>

        <div class="chat-messages" id="chatMessages" ref="messagesContainer">
          <!-- 欢迎消息 -->
          <div v-if="messages.length === 0" class="welcome-section">
            <div class="welcome-card">
              <div class="ai-avatar">
                <el-icon class="ai-icon"><UserFilled /></el-icon>
              </div>
              <div class="welcome-content">
                <h3 class="welcome-title">欢迎使用AI智能客服</h3>
                <p class="welcome-desc">我是您的专属AI助手，可以为您提供以下服务：</p>
                <div class="service-grid">
                  <div class="service-item">
                    <span class="service-emoji">📊</span>
                    <span>查询销售数据和财务信息</span>
                  </div>
                  <div class="service-item">
                    <el-icon class="service-icon"><Goods /></el-icon>
                    <span>管理库存和商品信息</span>
                  </div>
                  <div class="service-item">
                    <span class="service-emoji">📈</span>
                    <span>分析经营数据和趋势</span>
                  </div>
                  <div class="service-item">
                    <span class="service-emoji">⚙️</span>
                    <span>提供智能管理建议</span>
                  </div>
                </div>
                <p class="welcome-prompt">请从左侧选择快捷服务或直接输入您的问题</p>
              </div>
            </div>
          </div>

          <!-- 消息列表 -->
          <div v-for="message in messages" :key="message.id" class="message-wrapper">
            <div v-if="message.role === 'USER'" class="user-message">
              <div class="message-content user-content">
                <div class="message-text" v-html="formatMessage(message.content)"></div>
                <div class="message-time">{{ formatTime(message.createTime) }}</div>
              </div>
              <div class="user-avatar">
                <el-icon><User /></el-icon>
              </div>
            </div>
            <div v-else-if="message.role === 'AI'" class="ai-message">
              <div class="ai-avatar">
                <el-icon><UserFilled /></el-icon>
              </div>
              <div class="message-content ai-content">
                <div class="message-text" v-html="formatMessage(message.content)"></div>
                <div class="message-time">{{ formatTime(message.createTime) }}</div>
                <div v-if="message.metadata?.actionResult" class="action-result">
                  <div class="result-header">
                    <el-icon><Monitor /></el-icon>
                    <span>操作结果</span>
                  </div>
                  <div class="result-content">
                    <pre>{{ formatActionResult(message.metadata.actionResult) }}</pre>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="isLoading" class="message-wrapper">
            <div class="ai-message">
              <div class="ai-avatar">
                <el-icon><UserFilled /></el-icon>
              </div>
              <div class="message-content ai-content typing-indicator">
                <div class="typing-animation">
                  <div class="typing-dot"></div>
                  <div class="typing-dot"></div>
                  <div class="typing-dot"></div>
                </div>
                <span class="typing-text">AI正在思考中...</span>
              </div>
            </div>
          </div>
        </div>

        <div class="chat-input-container">
          <div class="input-wrapper">
            <el-input 
              v-model="inputMessage"
              type="textarea"
              :rows="1"
              :autosize="{ minRows: 1, maxRows: 4 }"
              placeholder="请输入您的问题，按Enter发送..." 
              maxlength="500"
              show-word-limit
              @keyup.enter.native="handleEnterSend"
              :disabled="isLoading"
              class="message-input"
            />
            <el-button 
              type="primary" 
              @click="sendMessage(inputMessage)" 
              :disabled="!inputMessage.trim() || isLoading"
              :loading="isLoading"
              class="send-btn"
            >
              <el-icon><Promotion /></el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 聊天记录对话框 -->
    <el-dialog
      v-model="showChatHistory"
      title="聊天记录"
      width="600px"
      :before-close="handleHistoryClose"
    >
      <template #header>
        <div class="dialog-header">
          <el-icon class="dialog-icon"><Clock /></el-icon>
          <span>聊天记录</span>
        </div>
      </template>
      
      <div class="history-content">
        <div class="history-controls">
          <el-input 
            placeholder="搜索聊天记录..." 
            prefix-icon="Search"
            class="search-input"
          />
          <el-button type="danger" plain @click="clearAllHistory">
            <el-icon><Delete /></el-icon>
            清空记录
          </el-button>
        </div>
        
        <div class="history-list">
          <div v-if="isLoadingHistory" class="loading-state">
            <el-icon class="is-loading"><Loading /></el-icon>
            <p>加载聊天记录中...</p>
          </div>
          <el-empty v-else-if="userConversations.length === 0" description="暂无聊天记录">
            <template #image>
              <el-icon><ChatDotRound /></el-icon>
            </template>
            <el-button type="primary" @click="showChatHistory = false">开始对话</el-button>
          </el-empty>
          <div v-else class="conversation-list">
            <div 
              v-for="conversation in userConversations" 
              :key="conversation.sessionId || conversation.id" 
              class="conversation-item"
              @click="loadConversation(conversation)"
            >
              <div class="conversation-header">
                <h4>{{ conversation.title || '新会话' }}</h4>
                <el-tag size="small" :type="conversation.status === 'ACTIVE' ? 'success' : 'info'">
                  {{ conversation.status === 'ACTIVE' ? '进行中' : '已结束' }}
                </el-tag>
              </div>
              <div class="conversation-info">
                <span class="message-count">消息数: {{ conversation.messageCount || 0 }}</span>
                <span class="create-time">{{ formatTime(conversation.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 设置对话框 -->
    <el-dialog
      v-model="showChatSettings"
      title="设置"
      width="400px"
    >
      <template #header>
        <div class="dialog-header">
          <el-icon class="dialog-icon"><Setting /></el-icon>
          <span>聊天设置</span>
        </div>
      </template>
      
      <div class="settings-content">
        <div class="setting-group">
          <h4 class="group-title">基础设置</h4>
          <div class="setting-item">
            <el-switch 
              v-model="chatSettings.soundEnabled" 
              active-text="消息提示音"
              inactive-text="消息提示音"
            />
          </div>
          <div class="setting-item">
            <el-switch 
              v-model="chatSettings.enableTypingEffect" 
              active-text="智能打字效果"
              inactive-text="智能打字效果"
            />
          </div>
          <div class="setting-item">
            <el-switch 
              v-model="chatSettings.saveHistory" 
              active-text="保存聊天记录"
              inactive-text="保存聊天记录"
            />
          </div>
        </div>
        
        <div class="setting-group">
          <h4 class="group-title">显示设置</h4>
          <div class="setting-item">
            <el-switch 
              v-model="chatSettings.showTimestamp" 
              active-text="显示时间戳"
              inactive-text="显示时间戳"
            />
          </div>
          <div class="setting-item">
            <el-switch 
              v-model="chatSettings.autoScroll" 
              active-text="自动滚动"
              inactive-text="自动滚动"
            />
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ChatDotRound, Clock, Setting, TrendCharts, Goods, Money, Warning,
  Star, PieChart, User, UserFilled,
  Delete, Monitor, Promotion, Loading
} from '@element-plus/icons-vue'
import { chatWithAi, chatStreamWithAi, getQuickActions, getSmartSuggestions, getUserConversations, getChatHistory, deleteConversation, deleteAllConversations } from '@/api/ai'

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
    
    // 过滤掉可能的无效会话
    conversations = conversations.filter(conv => 
      conv && (conv.sessionId || conv.id) && conv.status !== 'DELETED'
    )
    
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
      
      // 过滤掉已删除的消息
      historyMessages = historyMessages.filter(msg => 
        msg && !msg.isDeleted && msg.content && msg.content.trim() !== ''
      )
      
      if (historyMessages.length > 0) {
        // 转换消息格式
        const formattedMessages = historyMessages.map((msg: any) => ({
          id: msg.id,
          role: msg.messageType === 1 ? 'USER' : 'AI',
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
        messages.value = []
      }
    } else {
      console.log('没有找到会话记录')
      userConversations.value = []
      messages.value = []
      currentConversationId.value = ''
    }
  } catch (error) {
    console.error('加载会话历史失败:', error)
    // 出错时清空本地数据
    userConversations.value = []
    messages.value = []
    currentConversationId.value = ''
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
      role: 'AI',
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
    
    // 使用批量删除接口
    const response = await deleteAllConversations(currentUser.id)
    console.log('批量删除响应:', response)
    
    // 清空本地数据
    userConversations.value = []
    messages.value = []
    currentConversationId.value = ''
    showChatHistory.value = false
    
    // 清空本地缓存
    localStorage.removeItem('chatSettings')
    
    if (response.data > 0) {
      ElMessage.success(`成功删除 ${response.data} 个历史记录`)
    } else {
      ElMessage.info('没有需要删除的历史记录')
    }
    
    // 强制重新加载会话列表以确保同步
    setTimeout(async () => {
      try {
        const response = await getUserConversations(currentUser.id)
        console.log('重新加载会话列表:', response)
        
        let conversations = []
        if (response.data) {
          if (response.data.code === 200 && response.data.data) {
            conversations = response.data.data
          } else if (Array.isArray(response.data)) {
            conversations = response.data
          }
        }
        
        userConversations.value = conversations
        
        if (conversations.length > 0) {
          console.warn('服务端仍有会话记录，可能删除未完全成功')
        }
      } catch (error) {
        console.error('重新加载会话列表失败:', error)
      }
    }, 1000)
    
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
        role: msg.messageType === 1 ? 'USER' : 'AI',
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

// 处理历史记录对话框关闭
const handleHistoryClose = () => {
  showChatHistory.value = false
}

// 处理Enter发送
const handleEnterSend = (event: KeyboardEvent) => {
  if (!event.shiftKey && inputMessage.value.trim()) {
    event.preventDefault()
    sendMessage(inputMessage.value)
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

<style scoped lang="scss">
// iOS 黑白灰色彩系统
:root {
  --ios-primary: #000000;
  --ios-secondary: #1C1C1E;
  --ios-tertiary: #2C2C2E;
  --ios-gray: #8E8E93;
  --ios-gray-light: #F2F2F7;
  --ios-gray-medium: #C7C7CC;
  --ios-gray-dark: #48484A;
  --ios-white: #FFFFFF;
  --ios-system-background: #F2F2F7;
  --ios-secondary-background: #FFFFFF;
  --ios-label: #000000;
  --ios-secondary-label: #3C3C43;
  --ios-tertiary-label: #3C3C4399;
  --ios-separator: #C7C7CC;
  --ios-accent: #1C1C1E;
  --ios-message-blue: #007AFF;
  --ios-message-gray: #F2F2F7;
}

.ai-chat-container {
  height: calc(100vh - 100px);
  display: flex;
  flex-direction: column;
  background: var(--ios-system-background);
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', system-ui, sans-serif;
}

/* iOS风格页面头部 */
.page-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 24px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  font-size: 28px;
  color: var(--ios-accent);
  opacity: 0.9;
}

.header-title h2 {
  margin: 0;
  font-size: 32px;
  font-weight: 700;
  color: var(--ios-label);
  letter-spacing: -0.6px;
  line-height: 1.2;
}

.header-desc {
  margin: 0;
  color: var(--ios-secondary-label);
  font-size: 16px;
  font-weight: 400;
  opacity: 0.8;
}

.header-right {
  display: flex;
  gap: 16px;
  
  .el-button {
    height: 44px;
    border-radius: 12px;
    font-size: 15px;
    font-weight: 500;
    padding: 0 20px;
    border: none;
    background: rgba(28, 28, 30, 0.08);
    color: var(--ios-label);
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
    transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
    
    &:hover {
      background: rgba(28, 28, 30, 0.12);
      transform: translateY(-1px);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
    }
    
    &:active {
      transform: scale(0.98);
    }
  }
}

/* 主内容区域 */
.main-content {
  flex: 1;
  display: flex;
  gap: 16px;
  overflow: hidden;
}

/* iOS风格快捷面板 */
.quick-panel {
  width: 280px;
  min-width: 260px;
  max-width: 320px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);
  overflow-y: auto;
  
  // 自定义滚动条
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  
  &::-webkit-scrollbar-thumb {
    background: var(--ios-separator);
    border-radius: 2px;
    
    &:hover {
      background: var(--ios-gray);
    }
  }
}

.panel-section {
  padding: 24px;
  border-bottom: 1px solid rgba(199, 199, 204, 0.3);
}

.panel-section:last-child {
  border-bottom: none;
}

.section-header {
  margin-bottom: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: var(--ios-label);
  letter-spacing: -0.3px;
}

/* iOS风格快捷操作 */
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.action-item {
  display: flex;
  align-items: center;
  padding: 20px 16px;
  background: rgba(28, 28, 30, 0.04);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0.0, 0.2, 1);
  border: 1px solid transparent;
  
  &:hover {
    background: rgba(28, 28, 30, 0.08);
    transform: translateY(-2px) scale(1.02);
    box-shadow: 
      0 4px 16px rgba(0, 0, 0, 0.08),
      0 2px 8px rgba(0, 0, 0, 0.06);
    border: 1px solid rgba(28, 28, 30, 0.1);
  }
  
  &:active {
    transform: scale(0.98);
  }
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 20px;
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  transition: all 0.25s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &.sales { background: linear-gradient(135deg, #34C759, #30B753); }
  &.inventory { background: linear-gradient(135deg, var(--ios-accent), var(--ios-secondary)); }
  &.finance { background: linear-gradient(135deg, #FF9500, #E6850E); }
  &.warning { background: linear-gradient(135deg, #FF3B30, #E0342E); }
}

.action-content {
  flex: 1;
}

.action-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--ios-label);
  margin-bottom: 6px;
  letter-spacing: -0.2px;
}

.action-desc {
  font-size: 14px;
  color: var(--ios-secondary-label);
  opacity: 0.8;
}

/* iOS风格常见问题 */
.faq-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.faq-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: rgba(28, 28, 30, 0.04);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0.0, 0.2, 1);
  border: 1px solid transparent;
  
  &:hover {
    background: rgba(28, 28, 30, 0.08);
    border: 1px solid rgba(28, 28, 30, 0.1);
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }
  
  &:active {
    transform: scale(0.98);
  }
}

.faq-icon {
  margin-right: 12px;
  color: var(--ios-accent);
  font-size: 18px;
  opacity: 0.8;
}

.faq-text {
  font-size: 15px;
  color: var(--ios-label);
  font-weight: 500;
}

/* iOS风格聊天容器 */
.chat-container {
  flex: 1;
  min-width: 0; /* 确保flex item能够收缩 */
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.chat-header {
  padding: 20px 24px;
  border-bottom: 1px solid rgba(199, 199, 204, 0.3);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(28, 28, 30, 0.02);
}

.ai-status {
  display: flex;
  align-items: center;
  gap: 16px;
}

.status-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #34C759;
  animation: pulse-green 2s infinite;
  box-shadow: 0 0 0 0 rgba(52, 199, 89, 0.7);
}

@keyframes pulse-green {
  0%, 100% {
    box-shadow: 0 0 0 0 rgba(52, 199, 89, 0.7);
  }
  50% {
    box-shadow: 0 0 0 8px rgba(52, 199, 89, 0);
  }
}

.status-text {
  font-size: 16px;
  color: var(--ios-label);
  font-weight: 600;
}

/* iOS风格聊天消息区域 */
.chat-messages {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  scroll-behavior: smooth;
  
  // 自定义滚动条
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  
  &::-webkit-scrollbar-thumb {
    background: var(--ios-separator);
    border-radius: 2px;
    
    &:hover {
      background: var(--ios-gray);
    }
  }
}

/* iOS风格欢迎区域 */
.welcome-section {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  min-height: 450px;
}

.welcome-card {
  background: rgba(28, 28, 30, 0.04);
  border-radius: 20px;
  padding: 40px;
  text-align: center;
  max-width: 550px;
  border: 1px solid rgba(199, 199, 204, 0.3);
  backdrop-filter: saturate(180%) blur(10px);
}

.welcome-card .ai-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #007AFF 0%, #0051D5 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  color: white;
  font-size: 32px;
  box-shadow: 0 4px 16px rgba(0, 122, 255, 0.2);
  border: 3px solid rgba(255, 255, 255, 0.9);
  
  .el-icon {
    font-size: 36px !important;
    opacity: 1 !important;
  }
}

.welcome-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--ios-label);
  margin-bottom: 16px;
  letter-spacing: -0.4px;
}

.welcome-desc {
  color: var(--ios-secondary-label);
  margin-bottom: 24px;
  line-height: 1.6;
  font-size: 16px;
  opacity: 0.9;
}

.service-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  border: 1px solid rgba(199, 199, 204, 0.3);
  font-size: 14px;
  color: var(--ios-label);
  font-weight: 500;
  transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &:hover {
    background: rgba(255, 255, 255, 0.95);
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }
}

.service-icon {
  color: var(--ios-accent);
  font-size: 18px;
  opacity: 0.8;
}

.service-emoji {
  font-size: 18px;
}

.welcome-prompt {
  color: var(--ios-secondary-label);
  font-size: 15px;
  margin: 0;
  opacity: 0.8;
}

/* iOS风格消息样式 */
.message-wrapper {
  margin-bottom: 24px;
  width: 100%;
}

.user-message {
  display: flex;
  justify-content: flex-end;
  align-items: flex-start;
  gap: 12px;
  width: 100%;
}

.ai-message {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  gap: 12px;
  width: 100%;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #34C759, #30B753);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(52, 199, 89, 0.3);
}

.ai-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #007AFF 0%, #0051D5 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 122, 255, 0.3);
  border: 2px solid rgba(255, 255, 255, 0.9);
  
  .el-icon {
    font-size: 18px !important;
    opacity: 1 !important;
  }
}

.message-content {
  max-width: 75%;
  min-width: 120px;
  padding: 16px 20px;
  line-height: 1.5;
  word-wrap: break-word;
  flex-shrink: 1;
  flex-grow: 0;
  font-size: 16px;
}

.user-content {
  background: var(--ios-message-blue);
  color: #000000 !important;
  border-radius: 20px 20px 6px 20px;
  box-shadow: 0 2px 12px rgba(0, 122, 255, 0.3);
  
  .message-text,
  .message-text *,
  .message-text p,
  .message-text div,
  .message-text span {
    color: #000000 !important;
  }
  
  .message-time {
    color: rgba(0, 0, 0, 0.7) !important;
  }
}

/* 使用:deep()确保动态生成的HTML内容也应用样式 */
:deep(.user-content) {
  background: var(--ios-message-blue) !important;
  color: #000000 !important;
  
  .message-text,
  .message-text *,
  .message-text p,
  .message-text div,
  .message-text span {
    color: #000000 !important;
  }
}

.ai-content {
  background: var(--ios-message-gray);
  color: var(--ios-label);
  border-radius: 20px 20px 20px 6px;
  border: 1px solid rgba(199, 199, 204, 0.3);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.message-time {
  font-size: 12px;
  margin-top: 8px;
  opacity: 0.7;
  font-weight: 400;
}

.user-message .message-time {
  text-align: right;
  color: rgba(0, 0, 0, 0.7) !important;
}

/* 额外的用户消息文本颜色确保 - 使用:deep()穿透scoped样式 */
:deep(.user-message .user-content) {
  color: #000000 !important;
}

:deep(.user-message .user-content .message-text) {
  color: #000000 !important;
}

:deep(.user-message .user-content .message-text *) {
  color: #000000 !important;
}

:deep(.user-message .user-content .message-text p) {
  color: #000000 !important;
  margin: 0 !important;
}

:deep(.user-message .user-content .message-text div) {
  color: #000000 !important;
}

:deep(.user-message .user-content .message-text span) {
  color: #000000 !important;
}

.ai-message .message-time {
  color: var(--ios-secondary-label);
}

/* iOS风格操作结果 */
.action-result {
  margin-top: 16px;
  border: 1px solid rgba(199, 199, 204, 0.3);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: saturate(180%) blur(10px);
}

.result-header {
  padding: 12px 16px;
  background: rgba(28, 28, 30, 0.04);
  border-bottom: 1px solid rgba(199, 199, 204, 0.3);
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--ios-label);
  border-radius: 12px 12px 0 0;
}

.result-content {
  padding: 16px;
  max-height: 250px;
  overflow-y: auto;
  
  // 自定义滚动条
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: var(--ios-separator);
    border-radius: 2px;
  }
}

.result-content pre {
  margin: 0;
  font-size: 13px;
  font-family: 'SF Mono', 'Monaco', 'Consolas', 'Courier New', monospace;
  white-space: pre-wrap;
  word-wrap: break-word;
  color: var(--ios-label);
  line-height: 1.4;
}

/* iOS风格打字效果 */
.typing-indicator {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 0;
}

.typing-animation {
  display: flex;
  gap: 6px;
}

.typing-dot {
  width: 10px;
  height: 10px;
  background: var(--ios-gray);
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-dot:nth-child(1) { animation-delay: -0.32s; }
.typing-dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.4;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.typing-text {
  font-size: 15px;
  color: var(--ios-secondary-label);
  font-weight: 500;
  opacity: 0.8;
}

/* iOS风格输入区域 */
.chat-input-container {
  padding: 20px 24px;
  border-top: 1px solid rgba(199, 199, 204, 0.3);
  background: rgba(28, 28, 30, 0.02);
  backdrop-filter: saturate(180%) blur(10px);
}

.input-wrapper {
  display: flex;
  gap: 16px;
  align-items: flex-end;
}

.message-input {
  flex: 1;
  
  :deep(.el-textarea__inner) {
    border-radius: 20px;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(199, 199, 204, 0.3);
    font-size: 16px;
    line-height: 1.5;
    padding: 12px 20px;
    font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', system-ui, sans-serif;
    transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
    
    &:hover {
      background: rgba(255, 255, 255, 0.95);
      border-color: var(--ios-accent);
    }
    
    &:focus {
      background: rgba(255, 255, 255, 1);
      border-color: var(--ios-accent);
      box-shadow: 0 0 0 3px rgba(28, 28, 30, 0.1);
    }
    
    &::placeholder {
      color: var(--ios-secondary-label);
      opacity: 0.8;
    }
  }
}

.send-btn {
  height: 48px;
  min-width: 48px;
  border-radius: 50%;
  background: var(--ios-message-blue);
  color: white;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 122, 255, 0.3);
  transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &:hover {
    background: #0056CC;
    transform: scale(1.05);
    box-shadow: 0 4px 12px rgba(0, 122, 255, 0.4);
  }
  
  &:active {
    transform: scale(0.95);
  }
  
  &:disabled {
    background: var(--ios-separator);
    color: var(--ios-secondary-label);
    box-shadow: none;
    transform: none;
  }
}

/* iOS风格对话框样式 */
.dialog-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dialog-icon {
  font-size: 20px;
  color: var(--ios-accent);
  opacity: 0.9;
}

/* iOS风格历史记录 */
.history-content {
  max-height: 65vh;
}

.history-controls {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  align-items: center;
  
  .search-input {
    flex: 1;
    
    :deep(.el-input__wrapper) {
      border-radius: 12px;
      background: rgba(28, 28, 30, 0.06);
      border: 1px solid rgba(199, 199, 204, 0.3);
      
      &:hover {
        background: rgba(28, 28, 30, 0.08);
        border-color: var(--ios-accent);
      }
      
      &.is-focus {
        background: rgba(28, 28, 30, 0.1);
        border-color: var(--ios-accent);
        box-shadow: 0 0 0 3px rgba(28, 28, 30, 0.1);
      }
    }
  }
  
  .el-button {
    border-radius: 12px;
    font-weight: 500;
    
    &--danger {
      background: rgba(255, 59, 48, 0.1);
      color: #FF3B30;
      border: 1px solid rgba(255, 59, 48, 0.2);
      
      &:hover {
        background: rgba(255, 59, 48, 0.15);
        border-color: #FF3B30;
      }
    }
  }
}

.history-list {
  max-height: 450px;
  overflow-y: auto;
  
  // 自定义滚动条
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: var(--ios-separator);
    border-radius: 2px;
  }
}

.loading-state {
  text-align: center;
  padding: 48px 24px;
  color: var(--ios-secondary-label);
  
  .el-icon {
    font-size: 28px;
    margin-bottom: 16px;
    color: var(--ios-accent);
  }
  
  p {
    font-size: 16px;
    font-weight: 500;
  }
}

.conversation-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.conversation-item {
  background: rgba(28, 28, 30, 0.04);
  border-radius: 16px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0.0, 0.2, 1);
  border: 1px solid rgba(199, 199, 204, 0.3);
  
  &:hover {
    background: rgba(28, 28, 30, 0.08);
    border-color: var(--ios-accent);
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  }
  
  &:active {
    transform: scale(0.98);
  }
}

.conversation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  
  h4 {
    margin: 0;
    font-size: 16px;
    color: var(--ios-label);
    font-weight: 600;
    letter-spacing: -0.2px;
  }
}

.conversation-info {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: var(--ios-secondary-label);
  opacity: 0.8;
}

/* iOS风格设置 */
.settings-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.setting-group {
  border-bottom: 1px solid rgba(199, 199, 204, 0.3);
  padding-bottom: 20px;
  
  &:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }
}

.group-title {
  margin: 0 0 16px 0;
  font-size: 18px;
  font-weight: 700;
  color: var(--ios-label);
  letter-spacing: -0.3px;
}

.setting-item {
  margin-bottom: 16px;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  :deep(.el-switch) {
    .el-switch__core {
      border-radius: 16px;
      height: 32px;
      background: var(--ios-separator);
      border: none;
      
      &:after {
        border-radius: 50%;
        width: 28px;
        height: 28px;
        top: 2px;
        left: 2px;
        background: white;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
      }
    }
    
    &.is-checked .el-switch__core {
      background: #34C759;
      
      &:after {
        left: calc(100% - 30px);
      }
    }
    
    .el-switch__label {
      font-size: 16px;
      font-weight: 500;
      color: var(--ios-label);
      
      &.is-active {
        color: var(--ios-label);
      }
    }
  }
}

/* iOS风格Element Plus组件覆盖 */
:deep(.el-dialog) {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.12),
    0 4px 16px rgba(0, 0, 0, 0.08);
  
  .el-dialog__header {
    padding: 24px 24px 0;
    border: none;
    
    .el-dialog__title {
      font-size: 20px;
      font-weight: 700;
      color: var(--ios-label);
      letter-spacing: -0.3px;
    }
  }
  
  .el-dialog__body {
    padding: 24px;
    color: var(--ios-label);
  }
  
  .el-dialog__close {
    font-size: 18px;
    color: var(--ios-secondary-label);
    
    &:hover {
      color: var(--ios-label);
    }
  }
}

:deep(.el-tag) {
  border-radius: 8px;
  font-weight: 500;
  font-size: 13px;
  padding: 6px 12px;
  border: none;
  
  &.el-tag--success {
    background: rgba(52, 199, 89, 0.15);
    color: #34C759;
  }
  
  &.el-tag--info {
    background: rgba(28, 28, 30, 0.15);
    color: var(--ios-accent);
  }
}

:deep(.el-empty) {
  .el-empty__image {
    .el-icon {
      font-size: 48px;
      color: var(--ios-separator);
    }
  }
  
  .el-empty__description {
    color: var(--ios-secondary-label);
    font-size: 16px;
    font-weight: 500;
  }
  
  .el-button {
    border-radius: 12px;
    font-weight: 500;
    
    &--primary {
      background: var(--ios-accent);
      border: none;
      
      &:hover {
        background: var(--ios-secondary);
      }
    }
  }
}

/* iOS风格全局滚动条 */
::-webkit-scrollbar {
  width: 4px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: var(--ios-separator);
  border-radius: 2px;
  
  &:hover {
    background: var(--ios-gray);
  }
}

/* 响应式设计 */
@media (max-width: 1600px) {
  .quick-panel {
    width: 260px;
    min-width: 240px;
  }
}

@media (max-width: 1400px) {
  .quick-panel {
    width: 240px;
    min-width: 220px;
  }
}

@media (max-width: 1200px) {
  .main-content {
    flex-direction: column;
    gap: 20px;
  }
  
  .quick-panel {
    width: 100%;
    min-width: auto;
    max-width: none;
    order: 2;
    max-height: 300px;
    
    .panel-section {
      padding: 16px;
    }
    
    .quick-actions {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 12px;
    }
  }
  
  .chat-container {
    order: 1;
    min-height: 500px;
  }
}

@media (max-width: 768px) {
  .ai-chat-container {
    height: calc(100vh - 80px);
  }
  
  .page-header {
    padding: 20px;
    margin-bottom: 16px;
    
    .header-content {
      flex-direction: column;
      gap: 16px;
      text-align: center;
    }
    
    .header-title h2 {
      font-size: 24px;
    }
    
    .header-desc {
      font-size: 14px;
    }
    
    .header-right {
      width: 100%;
      justify-content: center;
      
      .el-button {
        flex: 1;
        max-width: 120px;
      }
    }
  }
  
  .quick-panel {
    .quick-actions {
      grid-template-columns: 1fr;
    }
    
    .action-item {
      padding: 16px;
      
      .action-icon {
        width: 40px;
        height: 40px;
        font-size: 18px;
      }
      
      .action-title {
        font-size: 15px;
      }
      
      .action-desc {
        font-size: 13px;
      }
    }
    
    .faq-list {
      gap: 8px;
      
      .faq-item {
        padding: 12px;
        
        .faq-text {
          font-size: 14px;
        }
      }
    }
  }
  
  .chat-container {
    .chat-messages {
      padding: 16px;
    }
    
    .message-content {
      max-width: 85%;
      font-size: 15px;
      padding: 14px 16px;
    }
    
    .welcome-card {
      padding: 24px;
      
      .ai-avatar {
        width: 64px;
        height: 64px;
        font-size: 28px;
      }
      
      .welcome-title {
        font-size: 20px;
      }
      
      .service-grid {
        grid-template-columns: 1fr;
        gap: 12px;
        
        .service-item {
          padding: 12px;
        }
      }
    }
    
    .chat-input-container {
      padding: 16px;
      
      .input-wrapper {
        gap: 12px;
      }
      
      .send-btn {
        height: 44px;
        min-width: 44px;
      }
    }
  }
}

@media (max-width: 480px) {
  .page-header {
    padding: 16px;
    
    .header-title h2 {
      font-size: 20px;
    }
  }
  
  .quick-panel {
    .panel-section {
      padding: 12px;
    }
    
    .section-title {
      font-size: 16px;
    }
    
    .action-item {
      padding: 12px;
      
      .action-icon {
        width: 36px;
        height: 36px;
        font-size: 16px;
      }
    }
  }
  
  .chat-container {
    .chat-header {
      padding: 16px;
      
      .status-text {
        font-size: 14px;
      }
    }
    
    .chat-messages {
      padding: 12px;
    }
    
    .message-content {
      font-size: 14px;
      padding: 12px 14px;
    }
    
    .welcome-card {
      padding: 20px;
      
      .welcome-title {
        font-size: 18px;
      }
    }
    
    .chat-input-container {
      padding: 12px;
      
      .send-btn {
        height: 40px;
        min-width: 40px;
      }
    }
  }
}
</style>