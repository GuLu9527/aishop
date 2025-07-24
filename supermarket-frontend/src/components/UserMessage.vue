<template>
  <div class="user-message-container">
    <!-- 消息内容区域 -->
    <div class="user-content">
      <!-- 消息正文 -->
      <div class="message-body">
        {{ content }}
      </div>
      
      <!-- 消息时间 -->
      <div class="message-time">
        {{ formatTime(timestamp) }}
      </div>
    </div>
    
    <!-- 用户头像 -->
    <div class="user-avatar">
      <div class="avatar-icon">
        <i class="el-icon-user"></i>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  content: string
  timestamp?: Date | string
}

const props = withDefaults(defineProps<Props>(), {
  timestamp: () => new Date()
})

// 格式化时间
const formatTime = (time: Date | string) => {
  const date = typeof time === 'string' ? new Date(time) : time
  return date.toLocaleTimeString('zh-CN', { 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}
</script>

<style scoped lang="scss">
.user-message-container {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  justify-content: flex-end;
  max-width: 100%;
}

.user-content {
  max-width: 70%;
  min-width: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  padding: 12px 16px;
  box-shadow: 0 2px 12px rgba(102, 126, 234, 0.3);
  position: relative;
  
  &::after {
    content: '';
    position: absolute;
    right: -8px;
    top: 12px;
    width: 0;
    height: 0;
    border-top: 8px solid transparent;
    border-bottom: 8px solid transparent;
    border-left: 8px solid #667eea;
  }
}

.message-body {
  line-height: 1.5;
  font-size: 14px;
  word-break: break-word;
  margin-bottom: 4px;
}

.message-time {
  font-size: 11px;
  opacity: 0.8;
  text-align: right;
}

.user-avatar {
  flex-shrink: 0;
  
  .avatar-icon {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #8b4513;
    font-size: 16px;
    box-shadow: 0 2px 8px rgba(252, 182, 159, 0.3);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .user-message-container {
    gap: 8px;
    margin-bottom: 16px;
  }
  
  .user-content {
    max-width: 85%;
    padding: 10px 12px;
    
    &::after {
      right: -6px;
      border-left-width: 6px;
    }
  }
  
  .message-body {
    font-size: 13px;
  }
  
  .user-avatar .avatar-icon {
    width: 32px;
    height: 32px;
    font-size: 14px;
  }
}
</style>
