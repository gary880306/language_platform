<template>
  <div class="reset-page">
    <el-card class="reset-card">
      <template #header><h2>重設密碼</h2></template>
      <el-form @submit.prevent="handleSubmit" label-position="top">
        <el-form-item label="新密碼">
          <el-input v-model="form.newPassword" type="password" show-password placeholder="至少 8 個字元" />
        </el-form-item>
        <el-form-item label="確認密碼">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="再次輸入密碼" />
        </el-form-item>
        <el-button type="primary" native-type="submit" :loading="loading" style="width:100%" size="large">
          確認重設
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { authApi } from '@/api/auth'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const form = ref({ newPassword: '', confirmPassword: '' })

async function handleSubmit() {
  if (form.value.newPassword !== form.value.confirmPassword) {
    ElMessage.error('兩次密碼不一致')
    return
  }
  loading.value = true
  try {
    await authApi.resetPassword({ token: route.query.token, ...form.value })
    ElMessage.success('密碼重設成功，請重新登入')
    router.push('/login')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '重設失敗')
  } finally { loading.value = false }
}
</script>

<style scoped>
.reset-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 24px; }
.reset-card { width: 100%; max-width: 400px; }
</style>
