<template>
  <div class="layout-wrapper">
    <!-- 侧边栏 -->
    <div class="sidebar" :class="{ 'sidebar-collapsed': isCollapse }">
      <div class="logo">
        <div class="logo-content">
          <!-- 超市管理SVG图标 -->
          <svg 
            v-if="!isCollapse" 
            class="logo-icon-full"
            width="32" 
            height="32" 
            viewBox="0 0 32 32" 
            fill="none" 
            xmlns="http://www.w3.org/2000/svg"
          >
            <!-- 购物车主体 -->
            <path d="M6 6H8L9.5 14H24L26 8H10" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
            <!-- 购物车底部 -->
            <path d="M9.5 14L8.5 18H22.5" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
            <!-- 购物车轮子 -->
            <circle cx="12" cy="22" r="2" stroke="white" stroke-width="2" fill="white"/>
            <circle cx="20" cy="22" r="2" stroke="white" stroke-width="2" fill="white"/>
            <!-- 货架线条 -->
            <path d="M26 12H30M26 16H30M26 20H30" stroke="white" stroke-width="1.5" stroke-linecap="round"/>
            <!-- 商品方块 -->
            <rect x="4" y="2" width="2" height="2" rx="0.5" fill="white"/>
            <rect x="28" y="4" width="2" height="2" rx="0.5" fill="white"/>
            <rect x="2" y="8" width="2" height="2" rx="0.5" fill="white"/>
          </svg>
          
          <!-- 折叠状态的简化图标 -->
          <svg 
            v-else 
            class="logo-icon-collapsed"
            width="24" 
            height="24" 
            viewBox="0 0 24 24" 
            fill="none" 
            xmlns="http://www.w3.org/2000/svg"
          >
            <!-- 简化的购物车图标 -->
            <path d="M4 4H6L7.5 12H18L20 6H8" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
            <circle cx="9" cy="18" r="1.5" stroke="white" stroke-width="2" fill="white"/>
            <circle cx="15" cy="18" r="1.5" stroke="white" stroke-width="2" fill="white"/>
          </svg>
          
          <div v-if="!isCollapse" class="logo-text">
            <h3 class="logo-title">超市管理</h3>
            <p class="logo-subtitle">Smart Market</p>
          </div>
        </div>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :unique-opened="true"
        router
        class="sidebar-menu"
        background-color="transparent"
        text-color="rgba(255, 255, 255, 0.8)"
        active-text-color="#000000"
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <template #title>仪表板</template>
        </el-menu-item>

        <el-menu-item index="/products">
          <el-icon><Box /></el-icon>
          <template #title>商品管理</template>
        </el-menu-item>

        <el-menu-item index="/inventory">
          <el-icon><Goods /></el-icon>
          <template #title>库存管理</template>
        </el-menu-item>

        <el-menu-item index="/stock-alerts">
          <el-icon><Warning /></el-icon>
          <template #title>库存预警</template>
        </el-menu-item>

        <el-menu-item index="/cashier">
          <el-icon><Money /></el-icon>
          <template #title>收银台</template>
        </el-menu-item>

        <el-menu-item index="/finance">
          <el-icon><OfficeBuilding /></el-icon>
          <template #title>财务管理</template>
        </el-menu-item>

        <el-menu-item index="/sales">
          <el-icon><TrendCharts /></el-icon>
          <template #title>销售分析</template>
        </el-menu-item>

        <el-menu-item index="/suppliers">
          <el-icon><Postcard /></el-icon>
          <template #title>供应商管理</template>
        </el-menu-item>

        <el-menu-item index="/ai-chat">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>AI智能助手</template>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 主内容区 -->
    <div class="main-wrapper">
      <!-- 顶部导航 -->
      <div class="header">
        <div class="header-left">
          <el-button
            type="text"
            @click="toggleCollapse"
            class="collapse-btn"
          >
            <el-icon><Expand v-if="isCollapse" /><Fold v-else /></el-icon>
          </el-button>
        </div>

        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ userStore.user?.realName || userStore.user?.username || '用户' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 主内容 -->
      <div class="main-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../stores/user'
import {
  House, Box, Goods, Warning, Money, OfficeBuilding, TrendCharts,
  ChatDotRound, User, ArrowDown, Expand, Fold, Postcard
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)

const activeMenu = computed(() => {
  return route.path
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = async (command: string) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      userStore.logout()
      ElMessage.success('退出成功')
      router.push('/login')
    } catch {
      // 用户取消
    }
  }
}

onMounted(async () => {
  // 获取用户信息
  console.log('MainLayout mounted, token:', userStore.token, 'user:', userStore.user)
  if (userStore.token && !userStore.user) {
    const success = await userStore.getUserInfo()
    console.log('获取用户信息结果:', success, userStore.user)
    if (!success) {
      // 如果获取用户信息失败，跳转到登录页
      router.push('/login')
    }
  }
})
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
  --ios-system-background: #F2F2F7;
  --ios-secondary-background: #FFFFFF;
  --ios-label: #000000;
  --ios-secondary-label: #3C3C43;
  --ios-tertiary-label: #3C3C4399;
  --ios-separator: #C7C7CC;
  --ios-accent: #1C1C1E;
}

// 深色模式支持
@media (prefers-color-scheme: dark) {
  :root {
    --ios-system-background: #000000;
    --ios-secondary-background: #1C1C1E;
    --ios-gray-light: #1C1C1E;
    --ios-label: #FFFFFF;
    --ios-secondary-label: #EBEBF5;
    --ios-tertiary-label: #EBEBF599;
    --ios-separator: #38383A;
    --ios-accent: #FFFFFF;
  }
}

.layout-wrapper {
  display: flex;
  height: 100vh;
  width: 100vw;
  position: fixed;
  top: 0;
  left: 0;
  background: var(--ios-system-background);
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', system-ui, sans-serif;
}

.sidebar {
  width: 240px; // 稍微增大符合iOS标准
  // iOS风格半透明毛玻璃背景
  background: rgba(28, 28, 30, 0.95);
  backdrop-filter: saturate(180%) blur(20px);
  border-right: 0.5px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1); // iOS标准缓动
  display: flex;
  flex-direction: column;
  height: 100vh;
  position: relative;
  z-index: 1000;
  // iOS风格微妙阴影
  box-shadow: 2px 0 20px rgba(0, 0, 0, 0.1);
}

.sidebar-collapsed {
  width: 80px; // 增大折叠宽度，更符合iOS
}

.logo {
  height: 80px; // 增大Logo区域高度
  display: flex;
  align-items: center;
  justify-content: center;
  // iOS风格渐变背景
  background: linear-gradient(135deg, 
    rgba(28, 28, 30, 1) 0%, 
    rgba(44, 44, 46, 1) 100%);
  color: var(--ios-white);
  flex-shrink: 0;
  border-bottom: 0.5px solid rgba(255, 255, 255, 0.1);
  padding: 0 16px;
  
  .logo-content {
    display: flex;
    align-items: center;
    gap: 12px;
    transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  }
  
  .logo-icon-full,
  .logo-icon-collapsed {
    flex-shrink: 0;
    opacity: 0.95;
    transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
    
    &:hover {
      opacity: 1;
      transform: scale(1.05);
    }
  }
  
  .logo-text {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    
    .logo-title {
      font-size: 18px; // iOS Body字号
      font-weight: 600; // iOS Semibold
      margin: 0;
      line-height: 1.2;
      letter-spacing: -0.3px; // iOS字距
      color: #FFFFFF !important; // 强制纯白色
    }
    
    .logo-subtitle {
      font-size: 12px; // iOS Caption字号
      font-weight: 400;
      margin: 0;
      line-height: 1.2;
      letter-spacing: 0.5px;
      color: rgba(255, 255, 255, 0.7);
      text-transform: uppercase;
    }
  }
}

// iOS风格侧边栏菜单
.sidebar-menu {
  border: none;
  background: transparent;
  flex: 1;
  overflow-y: auto;
  padding: 16px 12px;
  
  // 自定义滚动条样式
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  
  &::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.2);
    border-radius: 2px;
  }
  
  // iOS风格菜单项 - 完全覆盖Element Plus样式
  :deep(.el-menu-item) {
    height: 48px !important; // iOS标准触控高度
    margin-bottom: 4px;
    border-radius: 12px !important; // iOS圆角
    color: rgba(255, 255, 255, 0.8) !important;
    font-size: 16px;
    font-weight: 400;
    transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1) !important;
    background: transparent !important;
    border: 1px solid transparent !important;
    
    // 强制覆盖所有可能的Element Plus状态
    &,
    &:focus,
    &:active,
    &.is-focused {
      color: rgba(255, 255, 255, 0.8) !important;
      background: transparent !important;
    }
    
    &:hover {
      background: #FFFFFF !important;
      color: #000000 !important;
      transform: scale(1.02);
      box-shadow: 
        0 2px 8px rgba(0, 0, 0, 0.15),
        inset 0 1px 0 rgba(255, 255, 255, 0.8) !important;
      border: 1px solid rgba(255, 255, 255, 0.3) !important;
      
      .el-icon {
        color: #000000 !important;
        opacity: 1 !important;
      }
    }
    
    &.is-active {
      background: #FFFFFF !important;
      color: #000000 !important;
      font-weight: 600 !important;
      box-shadow: 
        0 2px 8px rgba(0, 0, 0, 0.15),
        inset 0 1px 0 rgba(255, 255, 255, 0.8) !important;
      border: 1px solid rgba(255, 255, 255, 0.3) !important;
      position: relative;
      
      .el-icon {
        opacity: 1 !important;
        transform: scale(1.05);
        color: #000000 !important;
      }
      
      // hover状态下保持反色效果
      &:hover {
        background: #FFFFFF !important;
        color: #000000 !important;
        transform: scale(1.02);
        
        .el-icon {
          color: #000000 !important;
        }
      }
    }
    
    .el-icon {
      font-size: 20px;
      margin-right: 12px;
      opacity: 0.9;
      transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
    }
  }
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 240px;
}

.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

// iOS风格顶部导航栏
.header {
  height: 70px; // 增大高度
  // 毛玻璃背景
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 0.5px solid var(--ios-separator);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  flex-shrink: 0;
  // iOS风格阴影
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
}

// iOS风格折叠按钮
.collapse-btn {
  width: 44px; // iOS最小触控尺寸
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: var(--ios-secondary-label);
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &:hover {
    background: rgba(0, 0, 0, 0.06);
    color: var(--ios-label);
    transform: scale(1.05);
  }
  
  &:active {
    transform: scale(0.95);
    background: rgba(0, 0, 0, 0.1);
  }
}

.header-right {
  display: flex;
  align-items: center;
}

// iOS风格用户信息
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: var(--ios-secondary-label);
  font-size: 16px; // iOS Body字号
  font-weight: 400;
  padding: 8px 16px;
  border-radius: 12px;
  transition: all 0.2s cubic-bezier(0.4, 0.0, 0.2, 1);
  min-height: 44px; // iOS触控友好
  
  &:hover {
    background: rgba(0, 0, 0, 0.06);
    color: var(--ios-label);
  }
  
  &:active {
    background: rgba(0, 0, 0, 0.1);
    transform: scale(0.98);
  }
  
  .el-icon {
    margin: 0 6px;
    font-size: 18px;
    opacity: 0.8;
  }
}

// iOS风格主内容区域
.main-content {
  flex: 1;
  background: var(--ios-system-background);
  padding: 24px; // iOS标准内边距
  overflow-y: auto;
  overflow-x: hidden;
  
  // 自定义滚动条
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  
  &::-webkit-scrollbar-thumb {
    background: var(--ios-separator);
    border-radius: 3px;
    
    &:hover {
      background: var(--ios-gray);
    }
  }
}

/* iOS风格响应式布局 */
@media (max-width: 1024px) {
  .sidebar {
    width: 240px;
    position: fixed;
    left: 0;
    top: 0;
    z-index: 2000;
    transform: translateX(-100%);
    transition: transform 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
    
    &.sidebar-show {
      transform: translateX(0);
    }
  }
  
  .sidebar-collapsed {
    width: 80px;
  }
  
  .main-wrapper {
    width: 100%;
    margin-left: 0;
  }
  
  .header {
    padding: 0 16px;
  }
  
  .main-content {
    padding: 16px;
  }
}

@media (max-width: 768px) {
  .logo {
    height: 70px;
    font-size: 16px;
  }
  
  .header {
    height: 60px;
    padding: 0 12px;
  }
  
  .collapse-btn {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }
  
  .user-info {
    font-size: 14px;
    padding: 6px 12px;
    
    .el-icon {
      font-size: 16px;
    }
  }
  
  .main-content {
    padding: 12px;
  }
  
  .sidebar-menu {
    padding: 12px 8px;
    
    :deep(.el-menu-item) {
      height: 44px;
      font-size: 15px;
      border-radius: 10px;
    }
  }
}

@media (max-width: 480px) {
  .sidebar {
    width: 280px; // 在小屏幕上增大侧边栏宽度
  }
  
  .header {
    padding: 0 8px;
  }
  
  .main-content {
    padding: 8px;
  }
}

/* iOS风格的毛玻璃遮罩层 */
.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(4px);
  z-index: 1500;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &.show {
    opacity: 1;
    visibility: visible;
  }
}
</style>
