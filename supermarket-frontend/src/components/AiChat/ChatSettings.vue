<template>
  <div class="chat-settings-drawer">
    <el-drawer
      v-model="visible"
      title="聊天设置"
      direction="rtl"
      size="350px"
      :before-close="handleClose"
    >
      <div class="settings-content">
        <!-- 主题设置 -->
        <div class="settings-section">
          <div class="section-header">
            <el-icon><Sunny /></el-icon>
            <span>主题外观</span>
          </div>
          
          <div class="theme-options">
            <div 
              v-for="theme in themes" 
              :key="theme.key"
              class="theme-option"
              :class="{ active: currentTheme === theme.key }"
              @click="changeTheme(theme.key)"
            >
              <div class="theme-preview" :style="{ background: theme.gradient }">
                <div class="preview-bubble user-preview"></div>
                <div class="preview-bubble ai-preview"></div>
              </div>
              <div class="theme-info">
                <div class="theme-name">{{ theme.name }}</div>
                <div class="theme-desc">{{ theme.description }}</div>
              </div>
              <el-icon v-if="currentTheme === theme.key" class="check-icon">
                <Check />
              </el-icon>
            </div>
          </div>
        </div>

        <!-- 消息显示设置 -->
        <div class="settings-section">
          <div class="section-header">
            <el-icon><ChatLineSquare /></el-icon>
            <span>消息显示</span>
          </div>
          
          <div class="setting-item">
            <div class="setting-label">
              <span>显示时间戳</span>
              <span class="setting-desc">在消息旁显示发送时间</span>
            </div>
            <el-switch v-model="settings.showTimestamp" @change="saveSettings" />
          </div>

          <div class="setting-item">
            <div class="setting-label">
              <span>显示消息统计</span>
              <span class="setting-desc">显示消息状态和性能指标</span>
            </div>
            <el-switch v-model="settings.showMessageStats" @change="saveSettings" />
          </div>

          <div class="setting-item">
            <div class="setting-label">
              <span>显示建议问题</span>
              <span class="setting-desc">AI回复后显示相关问题建议</span>
            </div>
            <el-switch v-model="settings.showSuggestions" @change="saveSettings" />
          </div>

          <div class="setting-item">
            <div class="setting-label">
              <span>启用打字动效</span>
              <span class="setting-desc">AI回复时显示打字动画</span>
            </div>
            <el-switch v-model="settings.enableTypingEffect" @change="saveSettings" />
          </div>
        </div>

        <!-- 交互设置 */
        <div class="settings-section">
          <div class="section-header">
            <el-icon><Setting /></el-icon>
            <span>交互体验</span>
          </div>

          <div class="setting-item">
            <div class="setting-label">
              <span>自动滚动</span>
              <span class="setting-desc">新消息时自动滚动到底部</span>
            </div>
            <el-switch v-model="settings.autoScroll" @change="saveSettings" />
          </div>

          <div class="setting-item">
            <div class="setting-label">
              <span>声音提醒</span>
              <span class="setting-desc">收到AI回复时播放提示音</span>
            </div>
            <el-switch v-model="settings.soundEnabled" @change="saveSettings" />
          </div>

          <div class="setting-item">
            <div class="setting-label">
              <span>快捷键发送</span>
              <span class="setting-desc">使用 Enter 键发送消息</span>
            </div>
            <el-switch v-model="settings.enterToSend" @change="saveSettings" />
          </div>

          <div class="setting-item">
            <div class="setting-label">
              <span>字体大小</span>
              <span class="setting-desc">调整聊天消息的字体大小</span>
            </div>
            <div class="font-size-control">
              <el-slider
                v-model="settings.fontSize"
                :min="12"
                :max="18"
                :step="1"
                :marks="fontSizeMarks"
                @change="saveSettings"
              />
            </div>
          </div>
        </div>

        <!-- 个人资料设置 -->
        <div class="settings-section">
          <div class="section-header">
            <el-icon><User /></el-icon>
            <span>个人资料</span>
          </div>

          <div class="setting-item">
            <div class="setting-label">
              <span>头像选择</span>
              <span class="setting-desc">选择您的聊天头像</span>
            </div>
            <div class="avatar-options">
              <div 
                v-for="avatar in avatars" 
                :key="avatar"
                class="avatar-option"
                :class="{ active: settings.userAvatar === avatar }"
                @click="changeAvatar(avatar)"
              >
                {{ avatar }}
              </div>
            </div>
          </div>

          <div class="setting-item">
            <div class="setting-label">
              <span>显示名称</span>
              <span class="setting-desc">在聊天中显示的用户名</span>
            </div>
            <el-input
              v-model="settings.displayName"
              placeholder="请输入显示名称"
              maxlength="20"
              show-word-limit
              @blur="saveSettings"
            />
          </div>
        </div>

        <!-- 数据管理 -->
        <div class="settings-section">
          <div class="section-header">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据管理</span>
          </div>

          <div class="setting-item">
            <div class="setting-label">
              <span>聊天记录保存</span>
              <span class="setting-desc">自动保存聊天记录到本地</span>
            </div>
            <el-switch v-model="settings.saveHistory" @change="saveSettings" />
          </div>

          <div class="data-actions">
            <el-button @click="exportChatHistory" :loading="exportLoading">
              <el-icon><Download /></el-icon>
              导出聊天记录
            </el-button>
            
            <el-button @click="clearAllData" type="danger" plain :loading="clearLoading">
              <el-icon><Delete /></el-icon>
              清除所有数据
            </el-button>
          </div>
        </div>

        <!-- 重置设置 -->
        <div class="settings-section">
          <div class="reset-section">
            <el-button @click="resetSettings" plain>
              <el-icon><RefreshLeft /></el-icon>
              恢复默认设置
            </el-button>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Sunny,
  ChatLineSquare,
  Setting,
  User,
  DataAnalysis,
  Check,
  Download,
  Delete,
  RefreshLeft
} from '@element-plus/icons-vue'

// Props
interface Props {
  visible: boolean
}

const props = withDefaults(defineProps<Props>(), {
  visible: false
})

// Emits
const emit = defineEmits<{
  'update:visible': [value: boolean]
  'settings-changed': [settings: any]
  'theme-changed': [theme: string]
}>()

// 响应式数据
const exportLoading = ref(false)
const clearLoading = ref(false)
const currentTheme = ref('default')

// 默认设置
const defaultSettings = {
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
}

const settings = ref({ ...defaultSettings })

// 主题配置
const themes = [
  {
    key: 'default',
    name: 'iOS经典',
    description: 'iOS系统蓝色',
    gradient: 'linear-gradient(135deg, #007AFF 0%, #5856D6 100%)'
  },
  {
    key: 'warm',
    name: '暖阳橙',
    description: '温暖橙色调',
    gradient: 'linear-gradient(135deg, #FF9500 0%, #FF6B35 100%)'
  },
  {
    key: 'nature',
    name: '翠绿青',
    description: '自然绿色调',
    gradient: 'linear-gradient(135deg, #30D158 0%, #32ADE6 100%)'
  },
  {
    key: 'sunset',
    name: '晚霞紫',
    description: '梦幻紫粉色',
    gradient: 'linear-gradient(135deg, #AF52DE 0%, #FF2D92 100%)'
  },
  {
    key: 'dark',
    name: '深空灰',
    description: '深色系主题',
    gradient: 'linear-gradient(135deg, #1C1C1E 0%, #48484A 100%)'
  }
]

// 头像选项
const avatars = ['👤', '👨‍💼', '👩‍💼', '🧑‍💻', '👨‍🏫', '👩‍🏫', '🤖', '🌟', '💎', '🚀']

// 字体大小标记
const fontSizeMarks = {
  12: '小',
  14: '中',
  16: '大',
  18: '特大'
}

// 计算属性
const visible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

// 方法
const loadSettings = () => {
  try {
    const savedSettings = localStorage.getItem('ai_chat_settings')
    if (savedSettings) {
      settings.value = { ...defaultSettings, ...JSON.parse(savedSettings) }
    }
    
    const savedTheme = localStorage.getItem('ai_chat_theme')
    if (savedTheme) {
      currentTheme.value = savedTheme
    }
  } catch (error) {
    console.error('加载设置失败:', error)
    settings.value = { ...defaultSettings }
  }
}

const saveSettings = () => {
  try {
    localStorage.setItem('ai_chat_settings', JSON.stringify(settings.value))
    emit('settings-changed', settings.value)
    ElMessage.success('设置已保存')
  } catch (error) {
    console.error('保存设置失败:', error)
    ElMessage.error('保存设置失败')
  }
}

const changeTheme = (themeKey: string) => {
  currentTheme.value = themeKey
  localStorage.setItem('ai_chat_theme', themeKey)
  emit('theme-changed', themeKey)
  
  // 应用主题到根元素
  const theme = themes.find(t => t.key === themeKey)
  if (theme) {
    document.documentElement.style.setProperty('--ai-chat-primary-gradient', theme.gradient)
  }
  
  ElMessage.success(`已切换到${theme?.name || '默认主题'}`)
}

const changeAvatar = (avatar: string) => {
  settings.value.userAvatar = avatar
  saveSettings()
}

const exportChatHistory = async () => {
  exportLoading.value = true
  try {
    // 模拟导出功能
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    const data = {
      settings: settings.value,
      theme: currentTheme.value,
      exportTime: new Date().toISOString(),
      version: '1.0.0'
    }
    
    const blob = new Blob([JSON.stringify(data, null, 2)], { 
      type: 'application/json' 
    })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `ai-chat-settings-${Date.now()}.json`
    link.click()
    URL.revokeObjectURL(url)
    
    ElMessage.success('聊天记录导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  } finally {
    exportLoading.value = false
  }
}

const clearAllData = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清除所有聊天数据和设置吗？此操作不可恢复。',
      '清除确认',
      {
        confirmButtonText: '确定清除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    clearLoading.value = true
    
    // 清除本地存储
    localStorage.removeItem('ai_chat_settings')
    localStorage.removeItem('ai_chat_theme')
    localStorage.removeItem('ai_chat_history')
    
    // 重置设置
    settings.value = { ...defaultSettings }
    currentTheme.value = 'default'
    
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('所有数据已清除')
    visible.value = false
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清除数据失败:', error)
      ElMessage.error('清除数据失败')
    }
  } finally {
    clearLoading.value = false
  }
}

const resetSettings = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要恢复默认设置吗？',
      '重置确认',
      {
        confirmButtonText: '确定重置',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    settings.value = { ...defaultSettings }
    currentTheme.value = 'default'
    
    localStorage.setItem('ai_chat_settings', JSON.stringify(settings.value))
    localStorage.setItem('ai_chat_theme', 'default')
    
    emit('settings-changed', settings.value)
    emit('theme-changed', 'default')
    
    ElMessage.success('设置已重置为默认值')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置设置失败:', error)
    }
  }
}

const handleClose = (done: () => void) => {
  done()
}

// 生命周期
onMounted(() => {
  loadSettings()
  
  // 应用当前主题
  const theme = themes.find(t => t.key === currentTheme.value)
  if (theme) {
    document.documentElement.style.setProperty('--ai-chat-primary-gradient', theme.gradient)
  }
})

// 监听器
watch(() => props.visible, (newVal) => {
  if (newVal) {
    loadSettings()
  }
})
</script>

<style scoped>
.chat-settings-drawer {
  .settings-content {
    padding: 0 4px;
  }

  .settings-section {
    margin-bottom: 24px;
    padding-bottom: 20px;
    border-bottom: 1px solid #f0f2f6;

    &:last-child {
      border-bottom: none;
      margin-bottom: 0;
    }
  }

  .section-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 16px;

    .el-icon {
      color: #409eff;
    }
  }

  /* 主题选择 */
  .theme-options {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .theme-option {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    border: 2px solid var(--ios-separator, rgba(60, 60, 67, 0.12));
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.2s ease;
    position: relative;
    background: rgba(255, 255, 255, 0.5);
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);

    &:hover {
      border-color: var(--ios-blue, #007AFF);
      background: rgba(0, 122, 255, 0.05);
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(0, 122, 255, 0.1);
    }

    &.active {
      border-color: var(--ios-blue, #007AFF);
      background: rgba(0, 122, 255, 0.1);
      box-shadow: 0 4px 12px rgba(0, 122, 255, 0.2);
    }

    &:active {
      transform: scale(0.98);
    }
  }

  .theme-preview {
    width: 50px;
    height: 35px;
    border-radius: 6px;
    position: relative;
    overflow: hidden;
    flex-shrink: 0;

    .preview-bubble {
      position: absolute;
      width: 16px;
      height: 8px;
      border-radius: 4px;
    }

    .user-preview {
      top: 4px;
      right: 4px;
      background: rgba(255, 255, 255, 0.9);
    }

    .ai-preview {
      bottom: 4px;
      left: 4px;
      background: rgba(255, 255, 255, 0.7);
    }
  }

  .theme-info {
    flex: 1;

    .theme-name {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 2px;
    }

    .theme-desc {
      font-size: 12px;
      color: #909399;
    }
  }

  .check-icon {
    color: #67c23a;
    font-size: 18px;
  }

  /* 设置项 */
  .setting-item {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 12px 0;
    border-bottom: 1px solid #f5f7fa;

    &:last-child {
      border-bottom: none;
    }
  }

  .setting-label {
    flex: 1;
    margin-right: 16px;

    span:first-child {
      display: block;
      font-size: 14px;
      color: #303133;
      margin-bottom: 2px;
    }

    .setting-desc {
      font-size: 12px;
      color: #909399;
      line-height: 1.4;
    }
  }

  .font-size-control {
    width: 120px;
    margin-top: 8px;
  }

  /* 头像选择 */
  .avatar-options {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 8px;
    margin-top: 8px;
  }

  .avatar-option {
    width: 36px;
    height: 36px;
    border: 2px solid #e4e7ed;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    font-size: 16px;
    transition: all 0.3s ease;

    &:hover {
      border-color: #c6e2ff;
      background: #f5f9ff;
    }

    &.active {
      border-color: #409eff;
      background: #f0f8ff;
    }
  }

  /* 数据操作 */
  .data-actions {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin-top: 12px;

    .el-button {
      justify-content: flex-start;
    }
  }

  /* 重置区域 */
  .reset-section {
    text-align: center;
    padding: 12px 0;

    .el-button {
      width: 100%;
    }
  }
}

/* 移动端优化 */
@media (max-width: 768px) {
  .chat-settings-drawer {
    :deep(.el-drawer) {
      width: 85% !important;
      max-width: 350px;
    }

    .theme-option {
      padding: 10px;
      gap: 10px;
    }

    .theme-preview {
      width: 40px;
      height: 28px;
    }

    .avatar-options {
      grid-template-columns: repeat(6, 1fr);
    }

    .avatar-option {
      width: 32px;
      height: 32px;
      font-size: 14px;
    }

    .setting-item {
      flex-direction: column;
      align-items: stretch;
      gap: 8px;
    }

    .setting-label {
      margin-right: 0;
    }

    .font-size-control {
      width: 100%;
    }
  }
}
</style>