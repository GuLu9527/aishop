<template>
  <div class="login-container">
    <!-- 左侧品牌区域 -->
    <div class="brand-section">
      <div class="brand-content">
        <div class="brand-logo">
          <div class="logo-icon">
            <el-icon :size="60"><Shop /></el-icon>
          </div>
          <h1 class="brand-title">智慧超市</h1>
          <p class="brand-subtitle">Smart Supermarket Management</p>
        </div>

        <div class="feature-list">
          <div class="feature-item">
            <el-icon class="feature-icon"><TrendCharts /></el-icon>
            <div class="feature-text">
              <h3>智能分析</h3>
              <p>实时销售数据分析与预测</p>
            </div>
          </div>
          <div class="feature-item">
            <el-icon class="feature-icon"><ChatDotRound /></el-icon>
            <div class="feature-text">
              <h3>AI助手</h3>
              <p>智能经营建议与决策支持</p>
            </div>
          </div>
          <div class="feature-item">
            <el-icon class="feature-icon"><Monitor /></el-icon>
            <div class="feature-text">
              <h3>全面管控</h3>
              <p>库存、财务、销售一体化管理</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录区域 -->
    <div class="login-section">
      <div class="login-box">
        <div class="login-header">
          <h2 class="login-title">欢迎回来</h2>
          <p class="login-subtitle">登录您的管理账户</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <div class="input-wrapper">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                size="large"
                class="custom-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><User /></el-icon>
                </template>
              </el-input>
            </div>
          </el-form-item>

          <el-form-item prop="password">
            <div class="input-wrapper">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                class="custom-input"
                show-password
                @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <el-icon class="input-icon"><Lock /></el-icon>
                </template>
              </el-input>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="userStore.loading"
              @click="handleLogin"
              class="login-button"
            >
              <span v-if="!userStore.loading">立即登录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <div class="demo-account">
            <el-icon class="info-icon"><InfoFilled /></el-icon>
            <span>演示账号：admin / admin123</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
        <div class="shape shape-5"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import {
  Shop, TrendCharts, ChatDotRound, Monitor, User, Lock, InfoFilled
} from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref<FormInstance>()

const loginForm = reactive({
  username: 'admin',
  password: 'admin123'
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      const result = await userStore.login(loginForm.username, loginForm.password)
      
      if (result.success) {
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } else {
        ElMessage.error(result.message || '登录失败')
      }
    }
  })
}
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
  --ios-black: #000000;
  --ios-system-background: #F2F2F7;
  --ios-secondary-background: #FFFFFF;
  --ios-label: #000000;
  --ios-secondary-label: #3C3C43;
  --ios-tertiary-label: #3C3C4399;
  --ios-accent: #1C1C1E;
}

// 深色模式支持
@media (prefers-color-scheme: dark) {
  :root {
    --ios-primary: #FFFFFF;
    --ios-secondary: #EBEBF5;
    --ios-tertiary: #EBEBF599;
    --ios-system-background: #000000;
    --ios-secondary-background: #1C1C1E;
    --ios-gray-light: #1C1C1E;
    --ios-label: #FFFFFF;
    --ios-secondary-label: #EBEBF5;
    --ios-tertiary-label: #EBEBF599;
    --ios-accent: #FFFFFF;
  }
}

.login-container {
  min-height: 100vh;
  height: 100vh;
  width: 100vw;
  display: flex;
  position: fixed;
  top: 0;
  left: 0;
  overflow: hidden;
  // 黑白灰渐变背景
  background: linear-gradient(135deg, 
    #1C1C1E 0%, 
    #2C2C2E 30%,
    #48484A 70%,
    #636366 100%);
  z-index: 9999;
}

/* iOS风格背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
  overflow: hidden;
}

.floating-shapes {
  position: relative;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  border-radius: 50%;
  // 更加克制的透明度，符合iOS设计
  background: rgba(255, 255, 255, 0.05);
  animation: float 12s ease-in-out infinite;

  &.shape-1 {
    width: 120px;
    height: 120px;
    top: 15%;
    left: 8%;
    animation-delay: 0s;
    // 添加毛玻璃效果
    backdrop-filter: blur(2px);
  }

  &.shape-2 {
    width: 80px;
    height: 80px;
    top: 25%;
    right: 12%;
    animation-delay: 3s;
    backdrop-filter: blur(2px);
  }

  &.shape-3 {
    width: 60px;
    height: 60px;
    bottom: 35%;
    left: 15%;
    animation-delay: 6s;
    backdrop-filter: blur(2px);
  }

  &.shape-4 {
    width: 100px;
    height: 100px;
    bottom: 15%;
    right: 8%;
    animation-delay: 9s;
    backdrop-filter: blur(2px);
  }

  &.shape-5 {
    width: 40px;
    height: 40px;
    top: 60%;
    left: 45%;
    animation-delay: 1.5s;
    backdrop-filter: blur(2px);
  }
}

// iOS风格的更加自然的动画
@keyframes float {
  0%, 100% {
    transform: translateY(0px) scale(1);
    opacity: 0.3;
  }
  50% {
    transform: translateY(-8px) scale(1.02);
    opacity: 0.5;
  }
}

/* 左侧品牌区域 - iOS风格 */
.brand-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  z-index: 2;
  min-width: 0;
}

.brand-content {
  max-width: 480px;
  color: var(--ios-white);
  text-align: center;
}

.brand-logo {
  text-align: center;
  margin-bottom: 64px; // 增加间距提升层级感

  .logo-icon {
    margin-bottom: 24px;
    opacity: 0.95;
    // 增强的图标阴影
    filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.3));
  }

  .brand-title {
    // SF Pro Display Heavy规范
    font-size: 48px;
    font-weight: 700; // iOS Heavy
    margin: 0 0 20px 0;
    color: #FFFFFF; // 纯白色确保最高对比度
    // 增强的文字阴影提升可读性
    text-shadow: 
      0 2px 4px rgba(0, 0, 0, 0.4),
      0 4px 8px rgba(0, 0, 0, 0.2);
    letter-spacing: -0.8px; // iOS紧致字距
    line-height: 1.1;
  }

  .brand-subtitle {
    font-size: 18px; // 适当增大提升层级感
    font-weight: 400; // iOS Regular
    color: rgba(255, 255, 255, 0.85); // 85%透明度白色
    letter-spacing: 0.3px;
    line-height: 1.3;
    margin: 0;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  }
}

.feature-list {
  .feature-item {
    display: flex;
    align-items: center;
    margin-bottom: 24px;
    padding: 20px 24px;
    // 增强的iOS风格毛玻璃卡片
    background: rgba(255, 255, 255, 0.12);
    border-radius: 20px; // iOS更大的圆角
    backdrop-filter: blur(24px);
    border: 1px solid rgba(255, 255, 255, 0.18);
    // iOS风格微妙过渡
    transition: all 0.25s cubic-bezier(0.4, 0.0, 0.2, 1);
    
    // 增强的阴影效果
    box-shadow: 
      0 2px 8px rgba(0, 0, 0, 0.15),
      inset 0 1px 0 rgba(255, 255, 255, 0.1);

    &:hover {
      background: rgba(255, 255, 255, 0.15);
      transform: translateY(-2px);
      box-shadow: 
        0 4px 16px rgba(0, 0, 0, 0.2),
        inset 0 1px 0 rgba(255, 255, 255, 0.15);
    }

    &:active {
      transform: scale(0.98);
    }

    .feature-icon {
      font-size: 28px;
      margin-right: 20px;
      color: #FFFFFF; // 纯白色图标
      opacity: 0.95;
    }

    .feature-text {
      h3 {
        font-size: 18px; // 增大特性标题
        font-weight: 600; // iOS Semibold
        margin: 0 0 8px 0;
        color: #FFFFFF; // 纯白色标题
        letter-spacing: -0.3px;
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
      }

      p {
        font-size: 16px; // 增大描述文字
        margin: 0;
        color: rgba(255, 255, 255, 0.78); // 78%透明度
        font-weight: 400;
        line-height: 1.3;
        text-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
      }
    }
  }
}

/* 右侧登录区域 - iOS风格 */
.login-section {
  width: 480px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  // iOS风格毛玻璃背景
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(40px);
  position: relative;
  z-index: 2;
  // iOS风格边缘阴影
  box-shadow: -2px 0 40px rgba(0, 0, 0, 0.1);
}

.login-box {
  width: 100%;
  max-width: 380px;
  padding: 60px 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 48px;

  .login-title {
    // iOS Large Title字体规范
    font-size: 34px;
    font-weight: 700; // iOS Heavy
    color: var(--ios-label);
    margin: 0 0 12px 0;
    letter-spacing: -0.8px; // iOS紧致字距
  }

  .login-subtitle {
    font-size: 17px; // iOS Body字号
    color: var(--ios-secondary-label);
    margin: 0;
    font-weight: 400;
    opacity: 0.8;
  }
}

.login-form {
  margin-bottom: 36px;

  .el-form-item {
    margin-bottom: 20px;
  }
}

.input-wrapper {
  position: relative;

  .custom-input {
    :deep(.el-input__wrapper) {
      // iOS风格圆角
      border-radius: 14px;
      // iOS风格微妙阴影
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
      border: 1px solid var(--ios-gray-medium);
      background: var(--ios-secondary-background);
      transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
      min-height: 48px; // iOS标准触控高度

      &:hover {
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
        border-color: var(--ios-gray);
      }

      &.is-focus {
        border-color: var(--ios-accent);
        box-shadow: 0 0 0 3px rgba(28, 28, 30, 0.15);
        background: var(--ios-white);
      }
    }

    :deep(.el-input__inner) {
      padding-left: 48px;
      font-size: 17px; // iOS Body字号
      color: var(--ios-label);
      font-weight: 400;

      &::placeholder {
        color: var(--ios-tertiary-label);
        font-weight: 400;
      }
    }
  }

  .input-icon {
    color: var(--ios-gray);
    font-size: 20px;
    opacity: 0.8;
  }
}

.login-button {
  width: 100%;
  height: 50px; // iOS标准按钮高度
  border-radius: 14px; // iOS风格圆角
  font-size: 17px; // iOS Body字号
  font-weight: 600; // iOS Semibold
  // 使用黑色主色调
  background: var(--ios-accent);
  border: none;
  // iOS风格微妙阴影
  box-shadow: 0 2px 8px rgba(28, 28, 30, 0.25);
  transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
  color: var(--ios-white);

  &:hover {
    // iOS风格hover效果
    background: var(--ios-secondary); // 稍微浅一点的灰色
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(28, 28, 30, 0.35);
  }

  &:active {
    transform: scale(0.98);
    background: var(--ios-tertiary);
  }

  &.is-loading {
    background: var(--ios-gray);
    box-shadow: 0 2px 8px rgba(142, 142, 147, 0.25);
  }
}

.login-footer {
  text-align: center;

  .demo-account {
    display: inline-flex;
    align-items: center;
    padding: 12px 20px;
    // iOS风格灰色信息卡片
    background: rgba(142, 142, 147, 0.08);
    border-radius: 12px;
    color: var(--ios-gray);
    font-size: 15px; // iOS Footnote字号
    border: 1px solid rgba(142, 142, 147, 0.15);
    font-weight: 400;

    .info-icon {
      margin-right: 8px;
      font-size: 16px;
      opacity: 0.8;
    }
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .login-container {
    flex-direction: column;
  }

  .brand-section {
    flex: none;
    padding: 40px 20px;

    .brand-content {
      max-width: 100%;
    }

    .brand-title {
      font-size: 36px;
    }

    .feature-list {
      display: none;
    }
  }

  .login-section {
    width: 100%;
    min-height: 60vh;
  }
}

@media (max-width: 768px) {
  .brand-section {
    padding: 20px;

    .brand-title {
      font-size: 28px;
    }

    .brand-subtitle {
      font-size: 14px;
    }
  }

  .login-box {
    padding: 40px 20px;
  }

  .login-title {
    font-size: 24px !important;
  }
}
</style>
