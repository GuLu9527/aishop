<template>
  <div class="chat-history-drawer">
    <el-drawer
      v-model="visible"
      title="聊天记录"
      direction="rtl"
      size="400px"
      :before-close="handleClose"
    >
      <div class="history-content">
        <!-- 搜索框 -->
        <div class="search-section">
          <el-input
            v-model="searchQuery"
            placeholder="搜索会话..."
            clearable
            @input="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>

        <!-- 创建新会话按钮 -->
        <div class="create-section">
          <el-button
            type="primary" 
            :icon="Plus" 
            @click="createNewConversation"
            size="small"
            style="width: 100%"
          >
            新建会话
          </el-button>
        </div>

        <!-- 会话列表 -->
        <div class="conversations-section">
          <div class="section-title">
            <span>会话列表</span>
            <el-button
              text
              size="small"
              @click="refreshConversations"
              :loading="refreshLoading"
            >
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="3" animated />
          </div>

          <div v-else-if="filteredConversations.length === 0" class="empty-state">
            <el-empty 
              description="暂无聊天记录" 
              :image-size="80"
            >
              <el-button type="primary" @click="createNewConversation">
                开始第一次对话
              </el-button>
            </el-empty>
          </div>

          <div v-else class="conversations-list">
            <div
              v-for="conversation in filteredConversations"
              :key="conversation.id"
              class="conversation-item"
              :class="{ active: currentConversationId === conversation.sessionId }"
              @click="selectConversation(conversation)"
            >
              <div class="conversation-header">
                <div class="conversation-title">
                  <el-icon class="chat-icon"><ChatDotSquare /></el-icon>
                  <span class="title-text">{{ getConversationTitle(conversation) }}</span>
                </div>
                <div class="conversation-actions">
                  <el-dropdown @command="(cmd) => handleConversationAction(cmd, conversation)">
                    <el-button text size="small">
                      <el-icon><MoreFilled /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="rename">
                          <el-icon><Edit /></el-icon>
                          重命名
                        </el-dropdown-item>
                        <el-dropdown-item command="delete" divided>
                          <el-icon><Delete /></el-icon>
                          删除
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </div>
              
              <div class="conversation-meta">
                <span class="message-count">{{ conversation.messageCount || 0 }} 条消息</span>
                <span class="last-time">{{ formatTime(conversation.lastMessageTime || conversation.updateTime) }}</span>
              </div>
              
              <div class="conversation-status">
                <el-tag
                  :type="getStatusType(conversation.status)"
                  size="small"
                  effect="plain"
                >
                  {{ getStatusText(conversation.status) }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>

        <!-- 统计信息 -->
        <div class="stats-section">
          <div class="stats-card">
            <div class="stat-item">
              <span class="stat-label">总会话数</span>
              <span class="stat-value">{{ conversations.length }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">活跃会话</span>
              <span class="stat-value">{{ activeConversationsCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-drawer>

    <!-- 重命名对话框 -->
    <el-dialog
      v-model="renameDialogVisible"
      title="重命名会话"
      width="400px"
      @close="resetRenameDialog"
    >
      <el-form :model="renameForm" :rules="renameRules" ref="renameFormRef">
        <el-form-item label="会话标题" prop="title">
          <el-input
            v-model="renameForm.title"
            placeholder="请输入会话标题"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="renameDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmRename" :loading="renameLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Plus,
  Refresh,
  ChatDotSquare,
  MoreFilled,
  Edit,
  Delete
} from '@element-plus/icons-vue'
import {
  getUserConversations,
  createConversation,
  deleteConversation,
  getChatHistory
} from '@/api/ai'

interface Conversation {
  id: number
  sessionId: string
  userId: number
  userName: string
  title: string
  status: number
  messageCount: number
  lastMessageTime: string
  createTime: string
  updateTime: string
}

// Props
interface Props {
  visible: boolean
  currentConversationId?: string
  userId: number
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  currentConversationId: '',
  userId: 1
})

// Emits
const emit = defineEmits<{
  'update:visible': [value: boolean]
  'conversation-selected': [conversation: Conversation]
  'conversation-created': [conversation: Conversation]
  'conversation-deleted': [conversationId: string]
}>()

// 响应式数据
const loading = ref(false)
const refreshLoading = ref(false)
const conversations = ref<Conversation[]>([])
const searchQuery = ref('')
const renameDialogVisible = ref(false)
const renameLoading = ref(false)
const renameForm = ref({ title: '' })
const renameFormRef = ref()
const currentRenameConversation = ref<Conversation | null>(null)

// 表单验证规则
const renameRules = {
  title: [
    { required: true, message: '请输入会话标题', trigger: 'blur' },
    { min: 1, max: 50, message: '标题长度在 1 到 50 个字符', trigger: 'blur' }
  ]
}

// 计算属性
const visible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

const filteredConversations = computed(() => {
  if (!searchQuery.value) {
    return conversations.value
  }
  return conversations.value.filter(conv =>
    conv.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    conv.sessionId.includes(searchQuery.value)
  )
})

const activeConversationsCount = computed(() => {
  return conversations.value.filter(conv => conv.status === 1).length
})

// 方法
const loadConversations = async () => {
  loading.value = true
  try {
    const response = await getUserConversations(props.userId)
    if (response.data && response.data.success) {
      conversations.value = response.data.data || []
    }
  } catch (error) {
    console.error('加载会话列表失败:', error)
    ElMessage.error('加载会话列表失败')
  } finally {
    loading.value = false
  }
}

const refreshConversations = async () => {
  refreshLoading.value = true
  try {
    await loadConversations()
    ElMessage.success('刷新成功')
  } catch (error) {
    ElMessage.error('刷新失败')
  } finally {
    refreshLoading.value = false
  }
}

const createNewConversation = async () => {
  try {
    const response = await createConversation(props.userId, '新对话')
    if (response.data && response.data.success) {
      const newConversation = response.data.data
      conversations.value.unshift(newConversation)
      emit('conversation-created', newConversation)
      ElMessage.success('创建新会话成功')
      visible.value = false
    }
  } catch (error) {
    console.error('创建会话失败:', error)
    ElMessage.error('创建会话失败')
  }
}

const selectConversation = async (conversation: Conversation) => {
  try {
    // 加载会话历史
    const response = await getChatHistory(conversation.sessionId, props.userId, 50)
    if (response.data && response.data.success) {
      emit('conversation-selected', {
        ...conversation,
        messages: response.data.data || []
      })
      visible.value = false
    }
  } catch (error) {
    console.error('加载会话历史失败:', error)
    ElMessage.error('加载会话历史失败')
  }
}

const getConversationTitle = (conversation: Conversation) => {
  if (conversation.title && conversation.title !== '新对话') {
    return conversation.title
  }
  return `对话 ${conversation.sessionId.slice(-6)}`
}

const formatTime = (timeStr: string) => {
  if (!timeStr) return ''
  
  const time = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - time.getTime()
  
  if (diff < 24 * 60 * 60 * 1000) {
    return time.toLocaleTimeString('zh-CN', { 
      hour: '2-digit', 
      minute: '2-digit' 
    })
  } else if (diff < 7 * 24 * 60 * 60 * 1000) {
    const days = Math.floor(diff / (24 * 60 * 60 * 1000))
    return `${days}天前`
  } else {
    return time.toLocaleDateString('zh-CN')
  }
}

const getStatusType = (status: number) => {
  switch (status) {
    case 1: return 'success'
    case 2: return 'info'
    case 3: return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status: number) => {
  switch (status) {
    case 1: return '活跃'
    case 2: return '已结束'
    case 3: return '已删除'
    default: return '未知'
  }
}

const handleSearch = () => {
  // 搜索功能已通过计算属性实现
}

const handleClose = (done: () => void) => {
  done()
}

const handleConversationAction = (command: string, conversation: Conversation) => {
  switch (command) {
    case 'rename':
      showRenameDialog(conversation)
      break
    case 'delete':
      confirmDeleteConversation(conversation)
      break
  }
}

const showRenameDialog = (conversation: Conversation) => {
  currentRenameConversation.value = conversation
  renameForm.value.title = conversation.title || `对话 ${conversation.sessionId.slice(-6)}`
  renameDialogVisible.value = true
}

const resetRenameDialog = () => {
  renameForm.value.title = ''
  currentRenameConversation.value = null
  renameFormRef.value?.resetFields()
}

const confirmRename = async () => {
  if (!renameFormRef.value) return
  
  try {
    await renameFormRef.value.validate()
    renameLoading.value = true
    
    // 这里应该调用重命名API，但当前后端没有此接口
    // 暂时只在前端更新
    if (currentRenameConversation.value) {
      const index = conversations.value.findIndex(
        conv => conv.id === currentRenameConversation.value!.id
      )
      if (index !== -1) {
        conversations.value[index].title = renameForm.value.title
      }
    }
    
    ElMessage.success('重命名成功')
    renameDialogVisible.value = false
    resetRenameDialog()
  } catch (error) {
    console.error('重命名失败:', error)
  } finally {
    renameLoading.value = false
  }
}

const confirmDeleteConversation = async (conversation: Conversation) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除会话"${getConversationTitle(conversation)}"吗？此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    const response = await deleteConversation(conversation.sessionId, props.userId)
    if (response.data && response.data.success) {
      conversations.value = conversations.value.filter(
        conv => conv.id !== conversation.id
      )
      emit('conversation-deleted', conversation.sessionId)
      ElMessage.success('删除成功')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除会话失败:', error)
      ElMessage.error('删除会话失败')
    }
  }
}

// 监听器
watch(() => props.visible, (newVal) => {
  if (newVal) {
    loadConversations()
  }
})

// 生命周期
onMounted(() => {
  if (props.visible) {
    loadConversations()
  }
})
</script>

<style scoped>
.chat-history-drawer {
  .history-content {
    display: flex;
    flex-direction: column;
    height: 100%;
    padding: 0 4px;
  }

  .search-section {
    margin-bottom: 16px;
  }

  .create-section {
    margin-bottom: 20px;
  }

  .conversations-section {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;
  }

  .section-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 12px;
    padding-bottom: 8px;
    border-bottom: 1px solid #ebeef5;
  }

  .loading-container {
    padding: 16px 0;
  }

  .empty-state {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .conversations-list {
    flex: 1;
    overflow-y: auto;
  }

  .conversation-item {
    padding: 12px;
    border: 1px solid var(--ios-separator, rgba(60, 60, 67, 0.12));
    border-radius: 12px;
    margin-bottom: 8px;
    cursor: pointer;
    transition: all 0.2s ease;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);

    &:hover {
      border-color: var(--ios-blue, #007AFF);
      background: rgba(0, 122, 255, 0.05);
      transform: translateY(-1px);
      box-shadow: 0 2px 8px rgba(0, 122, 255, 0.1);
    }

    &.active {
      border-color: var(--ios-blue, #007AFF);
      background: rgba(0, 122, 255, 0.1);
      box-shadow: 0 2px 8px rgba(0, 122, 255, 0.2);
    }

    &:active {
      transform: scale(0.98);
    }
  }

  .conversation-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 8px;
  }

  .conversation-title {
    display: flex;
    align-items: center;
    gap: 6px;
    flex: 1;
    min-width: 0;

    .chat-icon {
      color: #409eff;
      font-size: 16px;
      flex-shrink: 0;
    }

    .title-text {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .conversation-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 12px;
    color: #909399;
    margin-bottom: 8px;

    .message-count {
      background: #f0f2f6;
      padding: 2px 6px;
      border-radius: 10px;
    }
  }

  .stats-section {
    margin-top: 20px;
    padding-top: 16px;
    border-top: 1px solid #ebeef5;
  }

  .stats-card {
    background: #f8f9fa;
    border-radius: 8px;
    padding: 12px;
    display: flex;
    justify-content: space-around;
  }

  .stat-item {
    text-align: center;

    .stat-label {
      display: block;
      font-size: 12px;
      color: #909399;
      margin-bottom: 4px;
    }

    .stat-value {
      display: block;
      font-size: 18px;
      font-weight: 600;
      color: #409eff;
    }
  }
}

/* 自定义抽屉样式 */
:deep(.el-drawer__header) {
  padding: 20px 20px 10px 20px;
  margin-bottom: 0;
}

:deep(.el-drawer__body) {
  padding: 10px 20px 20px 20px;
}

/* 移动端优化 */
@media (max-width: 768px) {
  .chat-history-drawer {
    :deep(.el-drawer) {
      width: 85% !important;
      max-width: 350px;
    }

    :deep(.el-drawer__header) {
      padding: 16px 16px 8px 16px;
      font-size: 16px;
    }

    :deep(.el-drawer__body) {
      padding: 8px 16px 16px 16px;
    }

    .search-section {
      margin-bottom: 12px;
    }

    .create-section {
      margin-bottom: 16px;
    }

    .section-title {
      font-size: 13px;
      margin-bottom: 10px;
      padding-bottom: 6px;
    }

    .conversation-item {
      padding: 10px;
      margin-bottom: 6px;
      border-radius: 6px;
    }

    .conversation-title {
      gap: 4px;

      .chat-icon {
        font-size: 14px;
      }

      .title-text {
        font-size: 13px;
      }
    }

    .conversation-meta {
      font-size: 11px;
      margin-bottom: 6px;

      .message-count {
        padding: 1px 4px;
        border-radius: 8px;
      }
    }

    .stats-card {
      padding: 10px;
    }

    .stat-label {
      font-size: 11px;
    }

    .stat-value {
      font-size: 16px;
    }

    /* 优化触摸体验 */
    .conversation-item {
      min-height: 60px;
      display: flex;
      flex-direction: column;
      justify-content: center;
    }

    .conversation-actions {
      :deep(.el-button) {
        min-width: 32px;
        min-height: 32px;
      }
    }

    /* 优化滚动 */
    .conversations-list {
      -webkit-overflow-scrolling: touch;
    }
  }
}

/* 超小屏幕优化 */
@media (max-width: 480px) {
  .chat-history-drawer {
    :deep(.el-drawer) {
      width: 90% !important;
    }

    :deep(.el-drawer__header) {
      padding: 12px 12px 6px 12px;
      font-size: 15px;
    }

    :deep(.el-drawer__body) {
      padding: 6px 12px 12px 12px;
    }

    .conversation-item {
      padding: 8px;
    }

    .conversation-title .title-text {
      font-size: 12px;
    }

    .conversation-meta {
      font-size: 10px;
    }

    .stats-card {
      padding: 8px;
    }

    .stat-value {
      font-size: 14px;
    }
  }
}
</style>