<template>
  <el-container class="main-layout">
    <el-header height="64px">
      <NavBar />
    </el-header>
    <el-main class="main-content">
      <router-view />
    </el-main>
    <el-footer height="80px">
      <AppFooter />
    </el-footer>
  </el-container>
</template>

<script setup>
import NavBar from '@/components/NavBar.vue'
import AppFooter from '@/components/AppFooter.vue'
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.el-header {
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.main-content {
  flex: 1;
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}
</style>
