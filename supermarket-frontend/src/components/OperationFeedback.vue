<template>
  <div class="operation-feedback">
    <!-- 成功反馈 -->
    <transition name="feedback-slide">
      <div v-if="showSuccess" class="feedback-item success">
        <el-icon class="feedback-icon"><SuccessFilled /></el-icon>
        <span class="feedback-text">{{ successMessage }}</span>
      </div>
    </transition>

    <!-- 警告反馈 -->
    <transition name="feedback-slide">
      <div v-if="showWarning" class="feedback-item warning">
        <el-icon class="feedback-icon"><WarningFilled /></el-icon>
        <span class="feedback-text">{{ warningMessage }}</span>
      </div>
    </transition>

    <!-- 信息反馈 -->
    <transition name="feedback-slide">
      <div v-if="showInfo" class="feedback-item info">
        <el-icon class="feedback-icon"><InfoFilled /></el-icon>
        <span class="feedback-text">{{ infoMessage }}</span>
      </div>
    </transition>

    <!-- 加载反馈 -->
    <transition name="feedback-slide">
      <div v-if="showLoading" class="feedback-item loading">
        <el-icon class="feedback-icon is-loading"><Loading /></el-icon>
        <span class="feedback-text">{{ loadingMessage }}</span>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { SuccessFilled, WarningFilled, InfoFilled, Loading } from '@element-plus/icons-vue'

// Props
interface Props {
  type?: 'success' | 'warning' | 'info' | 'loading'
  message?: string
  duration?: number
  show?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'info',
  message: '',
  duration: 3000,
  show: false
})

// Emits
const emit = defineEmits<{
  close: []
}>()

// 响应式数据
const showSuccess = ref(false)
const showWarning = ref(false)
const showInfo = ref(false)
const showLoading = ref(false)

const successMessage = ref('')
const warningMessage = ref('')
const infoMessage = ref('')
const loadingMessage = ref('')

// 定时器
let timer: NodeJS.Timeout | null = null

// 监听props变化
watch(() => props.show, (newVal) => {
  if (newVal) {
    showFeedback()
  } else {
    hideFeedback()
  }
})

watch(() => props.type, () => {
  if (props.show) {
    showFeedback()
  }
})

watch(() => props.message, () => {
  if (props.show) {
    showFeedback()
  }
})

// 显示反馈
const showFeedback = () => {
  // 清除之前的定时器
  if (timer) {
    clearTimeout(timer)
    timer = null
  }

  // 重置所有状态
  hideFeedback()

  // 根据类型显示对应的反馈
  switch (props.type) {
    case 'success':
      showSuccess.value = true
      successMessage.value = props.message
      break
    case 'warning':
      showWarning.value = true
      warningMessage.value = props.message
      break
    case 'info':
      showInfo.value = true
      infoMessage.value = props.message
      break
    case 'loading':
      showLoading.value = true
      loadingMessage.value = props.message
      return // loading类型不自动隐藏
  }

  // 设置自动隐藏定时器（loading类型除外）
  if (props.duration > 0 && props.type !== 'loading') {
    timer = setTimeout(() => {
      hideFeedback()
      emit('close')
    }, props.duration)
  }
}

// 隐藏反馈
const hideFeedback = () => {
  showSuccess.value = false
  showWarning.value = false
  showInfo.value = false
  showLoading.value = false
}

// 暴露方法
defineExpose({
  showFeedback,
  hideFeedback
})
</script>

<style scoped lang="scss">
.operation-feedback {
  position: fixed;
  top: 80px;
  right: 20px;
  z-index: 9999;
  pointer-events: none;

  .feedback-item {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    margin-bottom: 8px;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(8px);
    pointer-events: auto;
    min-width: 200px;
    max-width: 400px;

    .feedback-icon {
      font-size: 18px;
      margin-right: 8px;
      flex-shrink: 0;
    }

    .feedback-text {
      font-size: 14px;
      font-weight: 500;
      line-height: 1.4;
      word-break: break-word;
    }

    &.success {
      background: rgba(103, 194, 58, 0.9);
      color: white;
      border: 1px solid rgba(103, 194, 58, 0.3);

      .feedback-icon {
        color: white;
      }
    }

    &.warning {
      background: rgba(230, 162, 60, 0.9);
      color: white;
      border: 1px solid rgba(230, 162, 60, 0.3);

      .feedback-icon {
        color: white;
      }
    }

    &.info {
      background: rgba(64, 158, 255, 0.9);
      color: white;
      border: 1px solid rgba(64, 158, 255, 0.3);

      .feedback-icon {
        color: white;
      }
    }

    &.loading {
      background: rgba(144, 147, 153, 0.9);
      color: white;
      border: 1px solid rgba(144, 147, 153, 0.3);

      .feedback-icon {
        color: white;
      }
    }
  }
}

// 动画效果
.feedback-slide-enter-active,
.feedback-slide-leave-active {
  transition: all 0.3s ease;
}

.feedback-slide-enter-from {
  transform: translateX(100%);
  opacity: 0;
}

.feedback-slide-leave-to {
  transform: translateX(100%);
  opacity: 0;
}

// 响应式设计
@media (max-width: 768px) {
  .operation-feedback {
    top: 60px;
    right: 10px;
    left: 10px;

    .feedback-item {
      min-width: auto;
      max-width: none;
    }
  }
}
</style>
