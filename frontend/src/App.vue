<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// 應用啟動時從 localStorage 恢復登入狀態
onMounted(() => {
  authStore.restoreAuth()
})
</script>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: 'Noto Sans TC', 'Microsoft JhengHei', sans-serif;
  background-color: #f5f7fa;
  color: #303133;
}

a {
  text-decoration: none;
  color: inherit;
}
</style>
