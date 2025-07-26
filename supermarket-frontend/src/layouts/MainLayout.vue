<template>
  <div class="layout-wrapper">
    <!-- 侧边栏 -->
    <div class="sidebar" :class="{ 'sidebar-collapsed': isCollapse }">
      <div class="logo">
        <h3 v-if="!isCollapse">超市管理</h3>
        <h3 v-else>超</h3>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :unique-opened="true"
        router
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
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

        <el-menu-item index="/ai-chat">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>AI智能助手</template>
        </el-menu-item>

        <el-menu-item index="/ai-customer-service">
          <el-icon><Service /></el-icon>
          <template #title>AI客服</template>
        </el-menu-item>

        <el-menu-item index="/ai-test">
          <el-icon><Setting /></el-icon>
          <template #title>AI服务测试</template>
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

<style scoped>
.layout-wrapper {
  display: flex;
  height: 100vh;
  width: 100vw;
  position: fixed;
  top: 0;
  left: 0;
}

.sidebar {
  width: 200px;
  background-color: #304156;
  transition: width 0.3s ease;
  display: flex;
  flex-direction: column;
  height: 100vh;
  position: relative;
  z-index: 1000;
}

.sidebar-collapsed {
  width: 64px;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4b;
  color: white;
  font-size: 16px;
  font-weight: bold;
  flex-shrink: 0;
}

.sidebar-menu {
  border: none;
  background-color: #304156;
  flex: 1;
  overflow-y: auto;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 200px;
}

.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.header {
  height: 60px;
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  flex-shrink: 0;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  font-size: 18px;
  color: #666;
  border: none;
  background: none;
  cursor: pointer;
  padding: 8px;
}

.collapse-btn:hover {
  background-color: #f5f5f5;
  border-radius: 4px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #666;
  font-size: 14px;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.user-info .el-icon {
  margin: 0 5px;
}

.main-content {
  flex: 1;
  background-color: #f5f5f5;
  padding: 20px;
  overflow-y: auto;
  overflow-x: hidden;
}
</style>
