<template>
  <div class="enhanced-message" :class="messageTypeClass">
    <!-- 时间分隔符 -->
    <div v-if="showTimeBreak" class="time-separator">
      <div class="time-line"></div>
      <span class="time-text">{{ formatMessageDate(message.createTime) }}</span>
      <div class="time-line"></div>
    </div>

    <!-- 消息内容 -->
    <div class="message-container">
      <!-- 用户消息 -->
      <div v-if="message.role === 'USER'" class="user-message">
        <div class="message-content">
          <div class="message-bubble user-bubble">
            <div class="message-text">{{ message.content }}</div>
            <div class="message-meta">
              <span class="message-time">{{ formatTime(message.createTime) }}</span>
              <el-icon class="message-status" v-if="message.status === 'sending'">
                <Loading />
              </el-icon>
              <el-icon class="message-status success" v-else-if="message.status === 'sent'">
                <Check />
              </el-icon>
              <el-icon class="message-status error" v-else-if="message.status === 'failed'">
                <Close />
              </el-icon>
            </div>
          </div>
          <div class="user-avatar">
            <img v-if="userAvatar" :src="userAvatar" alt="用户头像" />
            <span v-else>{{ userName.charAt(0) }}</span>
          </div>
        </div>
      </div>

      <!-- AI消息 -->
      <div v-else class="ai-message">
        <div class="message-content">
          <div class="ai-avatar" :class="{ 'thinking': message.isStreaming }">
            <div class="avatar-ring" v-if="message.isStreaming"></div>
            <span class="avatar-icon">🤖</span>
          </div>
          
          <div class="message-bubble ai-bubble">
            <!-- 消息文本 -->
            <div class="message-text">
              <div 
                v-if="message.content"
                v-html="formatAiMessage(message.content)" 
                :class="{
                  'streaming': message.isStreaming,
                  'markdown-content': hasMarkdown(message.content)
                }"
              ></div>
              
              <!-- 流式打字光标 -->
              <span v-if="message.isStreaming" class="typing-cursor">|</span>
              
              <!-- 空内容时的加载状态 -->
              <div v-if="!message.content && message.isStreaming" class="message-loading">
                <div class="loading-dots">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
                <span class="loading-text">AI正在思考...</span>
              </div>
            </div>

            <!-- 消息类型标识 -->
            <div v-if="message.metadata?.intent" class="message-type-badge">
              <el-tag size="small" :type="getIntentTagType(message.metadata.intent)">
                {{ getIntentDisplayName(message.metadata.intent) }}
              </el-tag>
            </div>

            <!-- 操作结果展示 -->
            <div v-if="message.metadata?.actionResult" class="action-result">
              <div class="result-header">
                <el-icon><TrendCharts /></el-icon>
                <span>操作结果</span>
              </div>
              <div class="result-content">
                <component 
                  :is="getResultComponent(message.metadata.actionResult.type)"
                  :data="message.metadata.actionResult.data"
                />
              </div>
            </div>

            <!-- 消息操作按钮 -->
            <div class="message-actions" v-if="!message.isStreaming && message.content">
              <el-button-group class="action-group">
                <el-button 
                  text 
                  size="small" 
                  @click="copyMessage"
                  v-tooltip="'复制内容'"
                >
                  <el-icon><DocumentCopy /></el-icon>
                </el-button>
                <el-button 
                  text 
                  size="small" 
                  @click="likeMessage"
                  v-tooltip="'有用'"
                  :class="{ 'liked': message.liked }"
                >
                  <el-icon><Star /></el-icon>
                </el-button>
                <el-button 
                  text 
                  size="small" 
                  @click="dislikeMessage"
                  v-tooltip="'无用'"
                  :class="{ 'disliked': message.disliked }"
                >
                  <el-icon><CircleClose /></el-icon>
                </el-button>
                <el-button 
                  text 
                  size="small" 
                  @click="regenerateMessage"
                  v-tooltip="'重新生成'"
                >
                  <el-icon><Refresh /></el-icon>
                </el-button>
                <el-dropdown @command="handleMoreAction">
                  <el-button text size="small">
                    <el-icon><MoreFilled /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="edit">
                        <el-icon><Edit /></el-icon>
                        编辑消息
                      </el-dropdown-item>
                      <el-dropdown-item command="share">
                        <el-icon><Share /></el-icon>
                        分享消息
                      </el-dropdown-item>
                      <el-dropdown-item command="report" divided>
                        <el-icon><Warning /></el-icon>
                        举报内容
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </el-button-group>
            </div>

            <!-- 相关问题建议 -->
            <div v-if="message.metadata?.suggested_questions?.length" class="suggested-questions">
              <div class="suggestions-header">
                <el-icon><ChatDotSquare /></el-icon>
                <span>相关问题</span>
              </div>
              <div class="suggestions-list">
                <el-button 
                  v-for="question in message.metadata.suggested_questions.slice(0, 3)" 
                  :key="question"
                  size="small"
                  plain
                  round
                  class="suggestion-btn"
                  @click="$emit('askQuestion', question)"
                >
                  {{ question }}
                </el-button>
              </div>
            </div>

            <!-- 消息时间和状态 -->
            <div class="message-footer">
              <span class="message-time">{{ formatTime(message.createTime) }}</span>
              <div class="message-stats" v-if="showStats">
                <span class="response-time" v-if="message.responseTime">
                  <el-icon><Timer /></el-icon>
                  {{ message.responseTime }}ms
                </span>
                <span class="confidence-score" v-if="message.confidence">
                  置信度: {{ Math.round(message.confidence * 100) }}%
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, defineEmits, defineProps } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Loading,
  Check,
  Close,
  DocumentCopy,
  Star,
  CircleClose,
  Refresh,
  MoreFilled,
  Edit,
  Share,
  Warning,
  ChatDotSquare,
  Timer,
  TrendCharts
} from '@element-plus/icons-vue'

// Props
const props = defineProps<{
  message: any
  showTimeBreak?: boolean
  userAvatar?: string
  userName?: string
  showStats?: boolean
}>()

// Emits
const emit = defineEmits<{
  askQuestion: [question: string]
  copy: [content: string]
  like: [message: any]
  dislike: [message: any]
  regenerate: [message: any]
  edit: [message: any]
  share: [message: any]
  report: [message: any]
}>()

// Computed
const messageTypeClass = computed(() => ({
  'user-type': props.message.role === 'USER',
  'ai-type': props.message.role === 'ASSISTANT',
  'streaming': props.message.isStreaming
}))

// Methods
const formatTime = (date: Date | string) => {
  const d = new Date(date)
  return d.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatMessageDate = (date: Date | string) => {
  const d = new Date(date)
  const now = new Date()
  
  if (d.toDateString() === now.toDateString()) {
    return '今天'
  }
  
  const yesterday = new Date(now)
  yesterday.setDate(yesterday.getDate() - 1)
  if (d.toDateString() === yesterday.toDateString()) {
    return '昨天'
  }
  
  return d.toLocaleDateString('zh-CN', {
    month: 'long',
    day: 'numeric'
  })
}

const formatAiMessage = (content: string) => {
  if (!content) return ''
  
  return content
    // Markdown格式转换
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.*?)\*/g, '<em>$1</em>')
    .replace(/`(.*?)`/g, '<code class="inline-code">$1</code>')
    .replace(/```([\s\S]*?)```/g, '<pre><code class="code-block">$1</code></pre>')
    // 链接转换
    .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank" rel="noopener">$1</a>')
    // 换行转换
    .replace(/\n/g, '<br>')
    // 表格简单支持
    .replace(/\|(.+)\|/g, (match, content) => {
      const cells = content.split('|').map((cell: string) => `<td>${cell.trim()}</td>`).join('')
      return `<table><tr>${cells}</tr></table>`
    })
}

const hasMarkdown = (content: string) => {
  return /(\*\*.*?\*\*|\*.*?\*|`.*?`|```[\s\S]*?```|\[.*?\]\(.*?\))/.test(content)
}

const getIntentTagType = (intent: string) => {
  const intentTypes: Record<string, string> = {
    'query_sales': 'success',
    'query_inventory': 'warning',
    'query_finance': 'info',
    'add_product': 'primary',
    'help': 'info'
  }
  return intentTypes[intent] || 'info'
}

const getIntentDisplayName = (intent: string) => {
  const intentNames: Record<string, string> = {
    'query_sales': '销售查询',
    'query_inventory': '库存查询',
    'query_finance': '财务查询',
    'add_product': '商品管理',
    'help': '帮助信息'
  }
  return intentNames[intent] || intent
}

const getResultComponent = (type: string) => {
  // 这里可以根据不同的结果类型返回不同的组件
  const components: Record<string, string> = {
    'table': 'DataTable',
    'chart': 'DataChart',
    'list': 'DataList',
    'card': 'DataCard'
  }
  return components[type] || 'div'
}

const copyMessage = async () => {
  try {
    await navigator.clipboard.writeText(props.message.content)
    ElMessage.success('已复制到剪贴板')
    emit('copy', props.message.content)
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

const likeMessage = () => {
  props.message.liked = !props.message.liked
  if (props.message.liked) {
    props.message.disliked = false
  }
  emit('like', props.message)
}

const dislikeMessage = () => {
  props.message.disliked = !props.message.disliked
  if (props.message.disliked) {
    props.message.liked = false
  }
  emit('dislike', props.message)
}

const regenerateMessage = () => {
  emit('regenerate', props.message)
}

const handleMoreAction = (command: string) => {
  switch (command) {
    case 'edit':
      emit('edit', props.message)
      break
    case 'share':
      emit('share', props.message)
      break
    case 'report':
      emit('report', props.message)
      break
  }
}
</script>

<style scoped>
.enhanced-message {
  margin-bottom: 16px;
}

/* 时间分隔符 */
.time-separator {
  display: flex;
  align-items: center;
  margin: 24px 0 16px;
  gap: 12px;
}

.time-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, transparent, #e4e7ed, transparent);
}

.time-text {
  font-size: 12px;
  color: #909399;
  background: white;
  padding: 4px 12px;
  border-radius: 12px;
  border: 1px solid #e4e7ed;
  white-space: nowrap;
}

/* 消息容器 */
.message-container {
  max-width: 800px;
  margin: 0 auto;
}

/* 用户消息 */
.user-message .message-content {
  display: flex;
  justify-content: flex-end;
  align-items: flex-end;
  gap: 8px;
  margin-bottom: 8px;
}

.user-bubble {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 12px 16px;
  border-radius: 18px;
  border-bottom-right-radius: 6px;
  max-width: 70%;
  word-wrap: break-word;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.user-bubble .message-text {
  line-height: 1.5;
  margin-bottom: 4px;
}

.user-bubble .message-meta {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 4px;
  font-size: 11px;
  opacity: 0.8;
}

.message-status {
  font-size: 12px;
}

.message-status.success {
  color: #67c23a;
}

.message-status.error {
  color: #f56c6c;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  flex-shrink: 0;
  overflow: hidden;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* AI消息 */
.ai-message .message-content {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 8px;
}

.ai-avatar {
  position: relative;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: white;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.ai-avatar.thinking {
  animation: pulse 2s infinite;
}

.avatar-ring {
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  border: 2px solid #667eea;
  border-radius: 50%;
  border-top-color: transparent;
  animation: spin 1s linear infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.ai-bubble {
  flex: 1;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 18px;
  border-bottom-left-radius: 6px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  max-width: calc(100% - 48px);
}

.message-text {
  line-height: 1.6;
  color: #303133;
  position: relative;
}

.streaming {
  position: relative;
}

.typing-cursor {
  display: inline-block;
  color: #667eea;
  font-weight: bold;
  animation: blink 1s infinite;
  margin-left: 2px;
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

.message-loading {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #909399;
}

.loading-dots {
  display: flex;
  gap: 4px;
}

.loading-dots span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #c0c4cc;
  animation: loading-bounce 1.4s infinite ease-in-out;
}

.loading-dots span:nth-child(1) { animation-delay: -0.32s; }
.loading-dots span:nth-child(2) { animation-delay: -0.16s; }

@keyframes loading-bounce {
  0%, 80%, 100% { transform: scale(1); opacity: 0.6; }
  40% { transform: scale(1.2); opacity: 1; }
}

.loading-text {
  font-size: 13px;
}

/* Markdown内容样式 */
.markdown-content :deep(strong) {
  font-weight: 600;
  color: #303133;
}

.markdown-content :deep(em) {
  font-style: italic;
  color: #606266;
}

.markdown-content :deep(.inline-code) {
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 2px 6px;
  font-family: 'Monaco', 'Consolas', monospace;
  font-size: 13px;
  color: #e6a23c;
}

.markdown-content :deep(.code-block) {
  background: #f8f9fa;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 12px;
  font-family: 'Monaco', 'Consolas', monospace;
  font-size: 13px;
  color: #303133;
  white-space: pre-wrap;
  overflow-x: auto;
}

.markdown-content :deep(a) {
  color: #667eea;
  text-decoration: none;
}

.markdown-content :deep(a:hover) {
  text-decoration: underline;
}

.markdown-content :deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin: 8px 0;
}

.markdown-content :deep(td) {
  border: 1px solid #e4e7ed;
  padding: 8px 12px;
  text-align: left;
}

/* 消息类型标识 */
.message-type-badge {
  margin: 8px 0;
}

/* 操作结果 */
.action-result {
  margin: 12px 0;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}

.result-header {
  background: #f5f7fa;
  padding: 8px 12px;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  border-bottom: 1px solid #e4e7ed;
}

.result-content {
  padding: 12px;
}

/* 消息操作 */
.message-actions {
  margin-top: 12px;
  padding-top: 8px;
  border-top: 1px solid #f0f2f6;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.ai-message:hover .message-actions {
  opacity: 1;
}

.action-group {
  display: flex;
  gap: 4px;
}

.action-group .el-button {
  color: #909399;
  transition: color 0.3s ease;
}

.action-group .el-button:hover {
  color: #667eea;
}

.action-group .el-button.liked {
  color: #f6d55c;
  transform: scale(1.1);
}

.action-group .el-button.disliked {
  color: #f56c6c;
  transform: scale(1.1);
}

/* 建议问题 */
.suggested-questions {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #f0f2f6;
}

.suggestions-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 8px;
}

.suggestions-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.suggestion-btn {
  border-color: #e1e6ff;
  color: #667eea;
  background: #f8f9ff;
  font-size: 12px;
  height: 28px;
  padding: 0 12px;
  transition: all 0.3s ease;
}

.suggestion-btn:hover {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

/* 消息底部信息 */
.message-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 11px;
  color: #c0c4cc;
}

.message-stats {
  display: flex;
  align-items: center;
  gap: 12px;
}

.response-time,
.confidence-score {
  display: flex;
  align-items: center;
  gap: 2px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-bubble,
  .ai-bubble {
    max-width: 85%;
  }
  
  .suggested-questions .suggestions-list {
    justify-content: flex-start;
  }
  
  .message-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .message-stats {
    gap: 8px;
  }
}
</style>