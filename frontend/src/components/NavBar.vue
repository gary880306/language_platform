<template>
  <nav class="navbar">
    <div class="navbar-container">
      <!-- Logo -->
      <router-link to="/courses" class="logo">
        <el-icon size="28" color="#409eff"><School /></el-icon>
        <span>EnjoyLearning</span>
      </router-link>

      <!-- 搜尋列 -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜尋課程..."
          clearable
          @keyup.enter="search"
          @clear="search"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
      </div>

      <!-- 導航連結 -->
      <div class="nav-links">
        <router-link to="/courses">課程</router-link>
        <router-link to="/forum">論壇</router-link>
        <router-link v-if="isLoggedIn" to="/chatroom">聊天室</router-link>

        <!-- 購物車 -->
        <router-link v-if="isLoggedIn" to="/cart" class="cart-link">
          <el-badge :value="cartCount" :hidden="cartCount === 0">
            <el-icon size="22"><ShoppingCart /></el-icon>
          </el-badge>
        </router-link>

        <!-- 未登入 -->
        <template v-if="!isLoggedIn">
          <el-button type="primary" size="small" @click="$router.push('/login')">登入</el-button>
          <el-button size="small" @click="$router.push('/register')">註冊</el-button>
        </template>

        <!-- 已登入 -->
        <template v-else>
          <el-dropdown @command="handleCommand">
            <div class="user-avatar">
              <el-icon><User /></el-icon>
              <span>{{ authStore.user?.userName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">個人資料</el-dropdown-item>
                <el-dropdown-item command="orders">我的訂單</el-dropdown-item>
                <el-dropdown-item command="coupons">我的優惠券</el-dropdown-item>
                <el-dropdown-item v-if="isAdmin" command="admin" divided>
                  進入後台
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>登出</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const cartStore = useCartStore()

const searchKeyword = ref(route.query.keyword || '')
const isLoggedIn = computed(() => authStore.isLoggedIn)
const isAdmin = computed(() => authStore.isAdmin)
const cartCount = computed(() => cartStore.itemCount)

onMounted(async () => {
  if (authStore.isLoggedIn) {
    try { await cartStore.fetchCart() } catch {}
  }
})

function search() {
  router.push({ name: 'Courses', query: { keyword: searchKeyword.value } })
}

async function handleCommand(command) {
  switch (command) {
    case 'profile': router.push('/profile'); break
    case 'orders': router.push('/orders'); break
    case 'coupons': router.push('/profile?tab=coupons'); break
    case 'admin': router.push('/admin'); break
    case 'logout':
      await authStore.logout()
      ElMessage.success('已登出')
      router.push('/courses')
      break
  }
}
</script>

<style scoped>
.navbar {
  background: #fff;
  height: 64px;
  display: flex;
  align-items: center;
}
.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 24px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  text-decoration: none;
  white-space: nowrap;
}
.search-bar {
  flex: 1;
  max-width: 360px;
}
.nav-links {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-left: auto;
}
.nav-links a {
  color: #606266;
  font-size: 14px;
  text-decoration: none;
  transition: color 0.2s;
}
.nav-links a:hover,
.nav-links a.router-link-active {
  color: #409eff;
}
.cart-link { font-size: 0; }
.user-avatar {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #606266;
  font-size: 14px;
}
</style>
