<template>
  <div class="chat-message" :class="{ 'user-message': isUser, 'ai-message': !isUser }">
    <div class="message-avatar">
      <el-avatar v-if="isUser" :size="40" :src="userAvatar">
        <el-icon><User /></el-icon>
      </el-avatar>
      <div v-else class="ai-avatar" :class="{ typing: isTyping }">
        <el-icon size="20" color="#fff">
          <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
            <path fill="currentColor" d="M300 400a60 60 0 1 1-120 0 60 60 0 0 1 120 0zm424 0a60 60 0 1 1-120 0 60 60 0 0 1 120 0zm-400 308a28 28 0 1 1-56 0 28 28 0 0 1 56 0zm280 0a28 28 0 1 1-56 0 28 28 0 0 1 56 0zm-640-228h896v-96c0-88.4-100.3-160-224-160s-224 71.6-224 160v96zm0 64v320c0 53 43 96 96 96h640c53 0 96-43 96-96V544H64z"/>
          </svg>
        </el-icon>
      </div>
    </div>
    
    <div class="message-content">
      <div class="message-bubble" :class="{ 'user-bubble': isUser, 'ai-bubble': !isUser }">
        <div v-if="isTyping" class="typing-indicator">
          <span></span>
          <span></span>
          <span></span>
        </div>
        <div v-else class="message-text" v-html="formattedContent"></div>
      </div>
      
      <div class="message-time">{{ formatTime(timestamp) }}</div>
      
      <!-- AI消息的操作按钮 -->
      <div v-if="!isUser && !isTyping" class="message-actions">
        <el-button size="small" text @click="copyMessage">
          <el-icon><CopyDocument /></el-icon>
          复制
        </el-button>
        <el-button size="small" text @click="$emit('thumbsUp')">
          有用
        </el-button>
        <el-button size="small" text @click="$emit('thumbsDown')">
          无用
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { User, CopyDocument } from '@element-plus/icons-vue'

interface Props {
  content: string
  isUser: boolean
  timestamp: string
  userAvatar?: string
  isTyping?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  userAvatar: '',
  isTyping: false
})

const emit = defineEmits<{
  thumbsUp: []
  thumbsDown: []
}>()

const formattedContent = computed(() => {
  return props.content.replace(/\n/g, '<br>')
})

const formatTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  if (diff < 60000) { // 1分钟内
    return '刚刚'
  } else if (diff < 3600000) { // 1小时内
    return `${Math.floor(diff / 60000)}分钟前`
  } else if (diff < 86400000) { // 24小时内
    return `${Math.floor(diff / 3600000)}小时前`
  } else {
    return date.toLocaleString('zh-CN', {
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  }
}

const copyMessage = async () => {
  try {
    await navigator.clipboard.writeText(props.content)
    ElMessage.success('已复制到剪贴板')
  } catch (error) {
    ElMessage.error('复制失败')
  }
}
</script>

<style scoped>
.chat-message {
  display: flex;
  margin-bottom: 20px;
  animation: fadeIn 0.3s ease-in;
}

.user-message {
  flex-direction: row-reverse;
}

.ai-message {
  flex-direction: row;
}

.message-avatar {
  flex-shrink: 0;
  margin: 0 12px;
}

.ai-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.ai-avatar.typing::after {
  content: '';
  position: absolute;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #fff;
  bottom: 2px;
  right: 2px;
  animation: pulse 1.5s infinite;
}

.message-content {
  flex: 1;
  max-width: 70%;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 18px;
  word-wrap: break-word;
  position: relative;
}

.user-bubble {
  background: #007bff;
  color: white;
  margin-left: auto;
  border-bottom-right-radius: 6px;
}

.ai-bubble {
  background: #f8f9fa;
  color: #333;
  border: 1px solid #e9ecef;
  border-bottom-left-radius: 6px;
}

.message-text {
  line-height: 1.5;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  text-align: center;
}

.user-message .message-time {
  text-align: right;
}

.ai-message .message-time {
  text-align: left;
}

.message-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
  opacity: 0;
  transition: opacity 0.2s;
}

.chat-message:hover .message-actions {
  opacity: 1;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  align-items: center;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #999;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}
</style>