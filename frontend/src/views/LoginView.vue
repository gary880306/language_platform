<template>
  <div class="login-page">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <el-icon size="32" color="#409eff"><School /></el-icon>
          <h2>EnjoyLearning</h2>
          <p>歡迎回來，請登入帳號</p>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="Email" prop="email">
          <el-input v-model="form.email" placeholder="請輸入 Email" type="email" />
        </el-form-item>

        <el-form-item label="密碼" prop="password">
          <el-input v-model="form.password" type="password" placeholder="請輸入密碼" show-password />
        </el-form-item>

        <el-form-item label="驗證碼" prop="captcha">
          <div class="captcha-row">
            <el-input v-model="form.captcha" placeholder="請輸入驗證碼" style="flex:1" />
            <img :src="captchaUrl" alt="驗證碼" class="captcha-img" @click="refreshCaptcha" title="點擊刷新" />
          </div>
        </el-form-item>

        <el-button type="primary" native-type="submit" :loading="loading" style="width:100%" size="large">
          登入
        </el-button>
      </el-form>

      <el-divider>或使用第三方登入</el-divider>

      <div class="oauth-buttons">
        <el-button @click="googleLogin" style="width:100%;margin-bottom:8px">
          <img src="/google-icon.svg" width="18" style="margin-right:8px" /> Google 登入
        </el-button>
      </div>

      <div class="login-links">
        <router-link to="/forgot-password">忘記密碼？</router-link>
        <span>還沒有帳號？<router-link to="/register">立即註冊</router-link></span>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { authApi } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const formRef = ref(null)
const loading = ref(false)
const captchaKey = ref(Date.now())
const captchaUrl = computed(() => `/api/auth/captcha?t=${captchaKey.value}`)

const form = ref({ email: '', password: '', captcha: '' })
const rules = {
  email: [{ required: true, type: 'email', message: '請輸入有效的 Email', trigger: 'blur' }],
  password: [{ required: true, message: '請輸入密碼', trigger: 'blur' }],
  captcha: [{ required: true, message: '請輸入驗證碼', trigger: 'blur' }],
}

function refreshCaptcha() {
  captchaKey.value = Date.now()
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await authStore.login(form.value.email, form.value.password, form.value.captcha)
    ElMessage.success('登入成功')
    const redirect = route.query.redirect || '/courses'
    router.push(redirect)
  } catch (e) {
    const msg = e.response?.data?.message || '登入失敗'
    ElMessage.error(msg)
    refreshCaptcha()
    form.value.captcha = ''
  } finally {
    loading.value = false
  }
}

async function googleLogin() {
  try {
    const res = await authApi.getGoogleAuthUrl()
    window.location.href = res.data.url
  } catch {
    ElMessage.error('無法取得 Google 登入連結')
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 24px;
}
.login-card {
  width: 100%;
  max-width: 420px;
}
.card-header {
  text-align: center;
}
.card-header h2 { margin: 8px 0 4px; font-size: 24px; }
.card-header p { color: #909399; font-size: 14px; }
.captcha-row {
  display: flex;
  gap: 12px;
  align-items: center;
}
.captcha-img {
  height: 38px;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid #dcdfe6;
}
.oauth-buttons { margin-bottom: 16px; }
.login-links {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  margin-top: 8px;
}
.login-links a { color: #409eff; }
</style>
