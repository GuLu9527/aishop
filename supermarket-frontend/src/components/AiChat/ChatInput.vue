<template>
  <div class="chat-input-container">
    <!-- 快捷回复 -->
    <div v-if="showQuickReplies" class="quick-replies">
      <el-tag
        v-for="reply in quickReplies"
        :key="reply"
        class="quick-reply-tag"
        @click="selectQuickReply(reply)"
      >
        {{ reply }}
      </el-tag>
    </div>
    
    <!-- 输入框 -->
    <div class="input-wrapper">
      <el-input
        v-model="inputMessage"
        type="textarea"
        :rows="1"
        :autosize="{ minRows: 1, maxRows: 4 }"
        placeholder="请输入您的问题..."
        @keydown.enter.exact="handleSend"
        @keydown.enter.shift.exact="handleNewLine"
        :disabled="disabled"
        class="message-input"
      />
      
      <div class="input-actions">
        <el-button
          type="primary"
          :disabled="!inputMessage.trim() || disabled"
          @click="handleSend"
          :loading="sending"
          class="send-button"
        >
          <el-icon v-if="!sending"><Promotion /></el-icon>
          发送
        </el-button>
      </div>
    </div>
    
    <!-- 工具栏 -->
    <div class="toolbar">
      <el-button size="small" text @click="toggleQuickReplies">
        <el-icon><ChatDotRound /></el-icon>
        快捷回复
      </el-button>
      <el-button size="small" text @click="clearChat">
        <el-icon><Delete /></el-icon>
        清空对话
      </el-button>
      <el-button size="small" text @click="$emit('transferToHuman')">
        <el-icon><Service /></el-icon>
        转人工客服
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Promotion, ChatDotRound, Delete, Service } from '@element-plus/icons-vue'

interface Props {
  disabled?: boolean
  sending?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  disabled: false,
  sending: false
})

const emit = defineEmits<{
  send: [message: string]
  clearChat: []
  transferToHuman: []
}>()

const inputMessage = ref('')
const showQuickReplies = ref(false)

const quickReplies = [
  '查询订单状态',
  '退货退款',
  '商品咨询',
  '优惠活动',
  '配送问题',
  '账户问题',
  '投诉建议',
  '联系人工客服'
]

const handleSend = () => {
  if (!inputMessage.value.trim() || props.disabled) return
  
  emit('send', inputMessage.value.trim())
  inputMessage.value = ''
}

const handleNewLine = () => {
  inputMessage.value += '\n'
}

const selectQuickReply = (reply: string) => {
  inputMessage.value = reply
  showQuickReplies.value = false
  handleSend()
}

const toggleQuickReplies = () => {
  showQuickReplies.value = !showQuickReplies.value
}

const clearChat = () => {
  emit('clearChat')
}
</script>

<style scoped>
.chat-input-container {
  background: white;
  border-top: 1px solid #e9ecef;
  padding: 16px;
}

.quick-replies {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.quick-reply-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.quick-reply-tag:hover {
  background: #007bff;
  color: white;
}

.input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.message-input {
  flex: 1;
}

.message-input :deep(.el-textarea__inner) {
  border-radius: 20px;
  border: 1px solid #ddd;
  padding: 12px 16px;
  resize: none;
  font-size: 14px;
  line-height: 1.5;
}

.message-input :deep(.el-textarea__inner):focus {
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.input-actions {
  display: flex;
  align-items: center;
}

.send-button {
  border-radius: 20px;
  padding: 12px 20px;
  font-weight: 500;
}

.toolbar {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.toolbar .el-button {
  color: #666;
}

.toolbar .el-button:hover {
  color: #007bff;
}
</style>