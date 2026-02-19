<template>
  <div class="admin-login-page">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <el-icon size="32" color="#409eff"><Setting /></el-icon>
          <h2>後台管理登入</h2>
        </div>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="Email" prop="email">
          <el-input v-model="form.email" type="email" placeholder="管理員 Email" />
        </el-form-item>
        <el-form-item label="密碼" prop="password">
          <el-input v-model="form.password" type="password" placeholder="密碼" show-password />
        </el-form-item>
        <el-button type="primary" native-type="submit" :loading="loading" style="width:100%" size="large">
          登入後台
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)
const form = ref({ email: '', password: '' })
const rules = {
  email: [{ required: true, type: 'email', message: '請輸入 Email', trigger: 'blur' }],
  password: [{ required: true, message: '請輸入密碼', trigger: 'blur' }],
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await authStore.adminLogin(form.value.email, form.value.password)
    ElMessage.success('登入成功')
    router.push('/admin')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '登入失敗')
  } finally { loading.value = false }
}
</script>

<style scoped>
.admin-login-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #001529; }
.login-card { width: 100%; max-width: 400px; }
.card-header { text-align: center; }
.card-header h2 { margin-top: 8px; }
</style>
