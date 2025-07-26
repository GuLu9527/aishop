<template>
  <div class="chat-sidebar">
    <div class="sidebar-header">
      <h3>会话信息</h3>
      <el-button size="small" text @click="$emit('close')">
        <el-icon><Close /></el-icon>
      </el-button>
    </div>
    
    <div class="sidebar-content">
      <!-- 会话状态 -->
      <div class="info-section">
        <h4>会话状态</h4>
        <div class="status-item">
          <span class="label">状态：</span>
          <el-tag :type="getStatusType(sessionInfo.status)">
            {{ getStatusText(sessionInfo.status) }}
          </el-tag>
        </div>
        <div class="status-item">
          <span class="label">消息数：</span>
          <span>{{ sessionInfo.messageCount }}</span>
        </div>
        <div class="status-item">
          <span class="label">开始时间：</span>
          <span>{{ formatTime(sessionInfo.createTime) }}</span>
        </div>
      </div>
      
      <!-- 客户信息 -->
      <div class="info-section">
        <h4>客户信息</h4>
        <div class="status-item">
          <span class="label">姓名：</span>
          <span>{{ sessionInfo.customerName }}</span>
        </div>
        <div v-if="sessionInfo.customerContact" class="status-item">
          <span class="label">联系方式：</span>
          <span>{{ sessionInfo.customerContact }}</span>
        </div>
      </div>
      
      <!-- 服务评价 -->
      <div v-if="sessionInfo.status === 2" class="info-section">
        <h4>服务评价</h4>
        <div class="evaluation-form">
          <div class="rating-item">
            <span class="label">整体满意度：</span>
            <el-rate v-model="evaluation.satisfactionScore" />
          </div>
          <div class="rating-item">
            <span class="label">响应速度：</span>
            <el-rate v-model="evaluation.responseSpeedScore" />
          </div>
          <div class="rating-item">
            <span class="label">解决质量：</span>
            <el-rate v-model="evaluation.solutionQualityScore" />
          </div>
          <div class="rating-item">
            <span class="label">服务态度：</span>
            <el-rate v-model="evaluation.serviceAttitudeScore" />
          </div>
          
          <el-input
            v-model="evaluation.feedbackContent"
            type="textarea"
            :rows="3"
            placeholder="请输入您的反馈意见..."
            class="feedback-input"
          />
          
          <el-button type="primary" @click="submitEvaluation" :loading="submittingEvaluation">
            提交评价
          </el-button>
        </div>
      </div>
      
      <!-- 操作按钮 -->
      <div class="action-section">
        <el-button v-if="sessionInfo.status === 1" type="warning" @click="$emit('transferToHuman')">
          转人工客服
        </el-button>
        <el-button v-if="sessionInfo.status === 1" type="danger" @click="$emit('endSession')">
          结束会话
        </el-button>
        <el-button @click="exportChat">
          导出对话记录
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Close } from '@element-plus/icons-vue'
import { submitServiceEvaluation } from '@/api/aiCustomer'
import type { AiCustomerSessionVO, AiServiceEvaluationRequest } from '@/api/aiCustomer'

interface Props {
  sessionInfo: AiCustomerSessionVO
  messages: any[]
}

const props = defineProps<Props>()

const emit = defineEmits<{
  close: []
  transferToHuman: []
  endSession: []
}>()

const submittingEvaluation = ref(false)

const evaluation = reactive<Omit<AiServiceEvaluationRequest, 'sessionId' | 'customerId'>>({
  evaluationType: 1,
  satisfactionScore: 5,
  responseSpeedScore: 5,
  solutionQualityScore: 5,
  serviceAttitudeScore: 5,
  feedbackContent: '',
  improvementSuggestions: ''
})

const getStatusType = (status: number) => {
  switch (status) {
    case 1: return 'success'
    case 2: return 'info'
    case 3: return 'warning'
    default: return 'info'
  }
}

const getStatusText = (status: number) => {
  switch (status) {
    case 1: return '进行中'
    case 2: return '已结束'
    case 3: return '已转人工'
    default: return '未知'
  }
}

const formatTime = (time: string) => {
  return new Date(time).toLocaleString('zh-CN')
}

const submitEvaluation = async () => {
  try {
    submittingEvaluation.value = true
    
    await submitServiceEvaluation({
      sessionId: props.sessionInfo.sessionId,
      customerId: props.sessionInfo.customerId,
      ...evaluation
    })
    
    ElMessage.success('评价提交成功，感谢您的反馈！')
  } catch (error) {
    ElMessage.error('评价提交失败，请稍后重试')
  } finally {
    submittingEvaluation.value = false
  }
}

const exportChat = () => {
  const content = props.messages.map(msg => 
    `${msg.isUser ? '用户' : 'AI客服'} [${new Date(msg.timestamp).toLocaleString()}]:\n${msg.content}\n`
  ).join('\n')
  
  const blob = new Blob([content], { type: 'text/plain;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `客服对话记录_${props.sessionInfo.sessionId}.txt`
  a.click()
  URL.revokeObjectURL(url)
  
  ElMessage.success('对话记录已导出')
}
</script>

<style scoped>
.chat-sidebar {
  width: 320px;
  background: white;
  border-left: 1px solid #e9ecef;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e9ecef;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.sidebar-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

.info-section {
  margin-bottom: 24px;
}

.info-section h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.label {
  color: #666;
  font-weight: 500;
}

.evaluation-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.rating-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rating-item .label {
  font-size: 13px;
}

.feedback-input {
  margin-top: 8px;
}

.action-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}
</style>