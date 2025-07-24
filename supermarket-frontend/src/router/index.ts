import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../layouts/MainLayout.vue'),
    meta: { title: '仪表板', requiresAuth: true },
    children: [
      {
        path: '',
        name: 'DashboardHome',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '仪表板' }
      }
    ]
  },
  {
    path: '/products',
    component: () => import('../layouts/MainLayout.vue'),
    meta: { title: '商品管理', requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Products',
        component: () => import('../views/Products.vue'),
        meta: { title: '商品管理' }
      }
    ]
  },
  {
    path: '/inventory',
    component: () => import('../layouts/MainLayout.vue'),
    meta: { title: '库存管理', requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Inventory',
        component: () => import('../views/Inventory.vue'),
        meta: { title: '库存管理' }
      }
    ]
  },
  {
    path: '/stock-alerts',
    component: () => import('../layouts/MainLayout.vue'),
    meta: { title: '库存预警', requiresAuth: true },
    children: [
      {
        path: '',
        name: 'StockAlerts',
        component: () => import('../views/StockAlert.vue'),
        meta: { title: '库存预警' }
      }
    ]
  },
  {
    path: '/sales',
    component: () => import('../layouts/MainLayout.vue'),
    meta: { title: '销售分析', requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Sales',
        component: () => import('../views/Sales.vue'),
        meta: { title: '销售分析' }
      }
    ]
  },
  {
    path: '/cashier',
    component: () => import('../layouts/MainLayout.vue'),
    meta: { title: '收银台', requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Cashier',
        component: () => import('../views/Cashier.vue'),
        meta: { title: '收银台' }
      }
    ]
  },
  {
    path: '/finance',
    component: () => import('../layouts/MainLayout.vue'),
    meta: { title: '财务管理', requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Finance',
        component: () => import('../views/Finance.vue'),
        meta: { title: '财务管理' }
      },
      {
        path: 'records',
        name: 'FinanceRecords',
        component: () => import('../views/FinanceRecords.vue'),
        meta: { title: '收支记录' }
      }
    ]
  },

  {
    path: '/test-chart',
    component: () => import('../layouts/MainLayout.vue'),
    meta: { title: '图表测试', requiresAuth: true },
    children: [
      {
        path: '',
        name: 'TestChart',
        component: () => import('../views/TestChart.vue'),
        meta: { title: '图表测试' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
