<template>
  <div class="forgot-page">
    <el-card class="forgot-card">
      <template #header>
        <h2>忘記密碼</h2>
        <p>輸入您的 Email，我們將發送重設連結</p>
      </template>
      <el-form @submit.prevent="handleSubmit">
        <el-form-item>
          <el-input v-model="email" type="email" placeholder="請輸入您的 Email" size="large" />
        </el-form-item>
        <el-button type="primary" native-type="submit" :loading="loading" style="width:100%" size="large">
          發送重設連結
        </el-button>
      </el-form>
      <div style="text-align:center;margin-top:16px">
        <router-link to="/login">返回登入</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { authApi } from '@/api/auth'
import { ElMessage } from 'element-plus'

const email = ref('')
const loading = ref(false)

async function handleSubmit() {
  if (!email.value) { ElMessage.warning('請輸入 Email'); return }
  loading.value = true
  try {
    await authApi.forgotPassword(email.value)
    ElMessage.success('重設連結已發送，請檢查您的信箱')
    email.value = ''
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '發送失敗')
  } finally { loading.value = false }
}
</script>

<style scoped>
.forgot-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 24px; }
.forgot-card { width: 100%; max-width: 400px; }
.forgot-card h2 { font-size: 22px; margin-bottom: 4px; }
.forgot-card p { font-size: 13px; color: #909399; }
</style>
