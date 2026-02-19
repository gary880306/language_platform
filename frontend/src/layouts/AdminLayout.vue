<template>
  <el-container class="admin-layout">
    <!-- 側邊欄 -->
    <el-aside width="220px" class="admin-sidebar">
      <div class="logo">
        <el-icon size="24"><School /></el-icon>
        <span>EnjoyLearning 後台</span>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#001529"
        text-color="#a6adb4"
        active-text-color="#409eff"
      >
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用戶管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/courses">
          <el-icon><VideoPlay /></el-icon>
          <span>課程管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <el-icon><ShoppingCart /></el-icon>
          <span>訂單管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/coupons">
          <el-icon><Discount /></el-icon>
          <span>優惠券管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/reports">
          <el-icon><TrendCharts /></el-icon>
          <span>銷售報表</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主內容區 -->
    <el-container>
      <el-header height="60px" class="admin-header">
        <div class="header-left">
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <span class="admin-name">{{ authStore.user?.userName }}</span>
          <el-button text @click="logout">
            <el-icon><SwitchButton /></el-icon>
            登出
          </el-button>
        </div>
      </el-header>
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const pageTitleMap = {
  '/admin/users': '用戶管理',
  '/admin/courses': '課程管理',
  '/admin/orders': '訂單管理',
  '/admin/coupons': '優惠券管理',
  '/admin/reports': '銷售報表',
}

const pageTitle = computed(() => pageTitleMap[route.path] || '後台管理')

async function logout() {
  await authStore.logout()
  ElMessage.success('已登出')
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}
.admin-sidebar {
  background-color: #001529;
  display: flex;
  flex-direction: column;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 20px;
  color: #fff;
  font-size: 15px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.el-menu {
  border-right: none;
  flex: 1;
}
.admin-header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  border-bottom: 1px solid #e8e8e8;
}
.page-title {
  font-size: 18px;
  font-weight: 600;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.admin-main {
  background: #f0f2f5;
  padding: 24px;
}
</style>
