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
.login-container {
  min-height: 100vh;
  height: 100vh;
  width: 100vw;
  display: flex;
  position: fixed;
  top: 0;
  left: 0;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  z-index: 9999;
}

/* 背景装饰 */
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
  background: rgba(255, 255, 255, 0.08);
  animation: float 8s ease-in-out infinite;

  &.shape-1 {
    width: 80px;
    height: 80px;
    top: 10%;
    left: 10%;
    animation-delay: 0s;
  }

  &.shape-2 {
    width: 120px;
    height: 120px;
    top: 20%;
    right: 15%;
    animation-delay: 2s;
  }

  &.shape-3 {
    width: 60px;
    height: 60px;
    bottom: 30%;
    left: 20%;
    animation-delay: 4s;
  }

  &.shape-4 {
    width: 100px;
    height: 100px;
    bottom: 20%;
    right: 10%;
    animation-delay: 6s;
  }

  &.shape-5 {
    width: 40px;
    height: 40px;
    top: 50%;
    left: 50%;
    animation-delay: 1s;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
    opacity: 0.6;
  }
  50% {
    transform: translateY(-15px);
    opacity: 0.8;
  }
}

/* 左侧品牌区域 */
.brand-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  z-index: 2;
  min-width: 0; // 防止flex项目溢出
}

.brand-content {
  max-width: 500px;
  color: white;
}

.brand-logo {
  text-align: center;
  margin-bottom: 60px;

  .logo-icon {
    margin-bottom: 20px;
    opacity: 0.9;
  }

  .brand-title {
    font-size: 48px;
    font-weight: 700;
    margin: 0 0 12px 0;
    color: #ffffff;
    text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  }

  .brand-subtitle {
    font-size: 18px;
    opacity: 0.8;
    font-weight: 300;
    letter-spacing: 2px;
    margin: 0;
  }
}

.feature-list {
  .feature-item {
    display: flex;
    align-items: center;
    margin-bottom: 32px;
    padding: 20px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 16px;
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    transition: all 0.3s ease;

    &:hover {
      background: rgba(255, 255, 255, 0.15);
      transform: translateX(10px);
    }

    .feature-icon {
      font-size: 32px;
      margin-right: 20px;
      color: #fff;
      opacity: 0.9;
    }

    .feature-text {
      h3 {
        font-size: 18px;
        font-weight: 600;
        margin: 0 0 4px 0;
        color: #fff;
      }

      p {
        font-size: 14px;
        margin: 0;
        opacity: 0.8;
        color: #f0f8ff;
      }
    }
  }
}

/* 右侧登录区域 */
.login-section {
  width: 480px;
  flex-shrink: 0; // 防止收缩
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  position: relative;
  z-index: 2;
}

.login-box {
  width: 100%;
  max-width: 400px;
  padding: 60px 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;

  .login-title {
    font-size: 32px;
    font-weight: 700;
    color: #2c3e50;
    margin: 0 0 12px 0;
  }

  .login-subtitle {
    font-size: 16px;
    color: #7f8c8d;
    margin: 0;
    font-weight: 400;
  }
}

.login-form {
  margin-bottom: 32px;

  .el-form-item {
    margin-bottom: 24px;
  }
}

.input-wrapper {
  position: relative;

  .custom-input {
    :deep(.el-input__wrapper) {
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      border: 2px solid transparent;
      background: #f8f9fa;
      transition: all 0.3s ease;

      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      &.is-focus {
        border-color: #667eea;
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
        background: #fff;
      }
    }

    :deep(.el-input__inner) {
      padding-left: 45px;
      font-size: 16px;
      color: #2c3e50;

      &::placeholder {
        color: #bdc3c7;
      }
    }
  }

  .input-icon {
    color: #7f8c8d;
    font-size: 18px;
  }
}

.login-button {
  width: 100%;
  height: 50px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
  }

  &:active {
    transform: translateY(0);
  }

  &.is-loading {
    background: linear-gradient(135deg, #bdc3c7 0%, #95a5a6 100%);
  }
}

.login-footer {
  text-align: center;

  .demo-account {
    display: inline-flex;
    align-items: center;
    padding: 12px 20px;
    background: #e8f4fd;
    border-radius: 8px;
    color: #3498db;
    font-size: 14px;
    border: 1px solid #d6eaf8;

    .info-icon {
      margin-right: 8px;
      font-size: 16px;
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
