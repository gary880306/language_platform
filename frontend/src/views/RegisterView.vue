<template>
  <div class="register-page">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <el-icon size="32" color="#409eff"><School /></el-icon>
          <h2>建立帳號</h2>
          <p>加入 EnjoyLearning，開始學習語言</p>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="handleRegister">
        <el-form-item label="姓名" prop="userName">
          <el-input v-model="form.userName" placeholder="請輸入姓名" />
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input v-model="form.email" placeholder="請輸入 Email" type="email" />
        </el-form-item>
        <el-form-item label="密碼" prop="password">
          <el-input v-model="form.password" type="password" placeholder="至少 8 個字元" show-password />
        </el-form-item>
        <el-form-item label="確認密碼" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="再次輸入密碼" show-password />
        </el-form-item>
        <el-form-item label="手機號碼" prop="phoneNumber">
          <el-input v-model="form.phoneNumber" placeholder="09xx-xxx-xxx（選填）" />
        </el-form-item>
        <el-form-item label="生日" prop="birth">
          <el-date-picker v-model="form.birth" type="date" placeholder="選擇生日" style="width:100%" />
        </el-form-item>

        <el-button type="primary" native-type="submit" :loading="loading" style="width:100%" size="large">
          註冊
        </el-button>
      </el-form>

      <div class="register-link">
        已有帳號？<router-link to="/login">立即登入</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = ref({
  userName: '', email: '', password: '',
  confirmPassword: '', phoneNumber: '', birth: null,
})

const rules = {
  userName: [{ required: true, message: '姓名不可為空', trigger: 'blur' }],
  email: [{ required: true, type: 'email', message: '請輸入有效的 Email', trigger: 'blur' }],
  password: [
    { required: true, message: '密碼不可為空', trigger: 'blur' },
    { min: 8, message: '密碼至少 8 個字元', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '請確認密碼', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.value.password) callback(new Error('兩次密碼不一致'))
        else callback()
      },
      trigger: 'blur',
    },
  ],
}

async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await authApi.register({
      userName: form.value.userName,
      email: form.value.email,
      password: form.value.password,
      phoneNumber: form.value.phoneNumber || null,
      birth: form.value.birth,
    })
    ElMessage.success('註冊成功，請登入')
    router.push('/login')
  } catch (e) {
    const errors = e.response?.data
    if (typeof errors === 'object') {
      const msg = Object.values(errors).join('、')
      ElMessage.error(msg)
    } else {
      ElMessage.error('註冊失敗')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 24px;
}
.register-card { width: 100%; max-width: 480px; }
.card-header { text-align: center; }
.card-header h2 { margin: 8px 0 4px; font-size: 24px; }
.card-header p { color: #909399; font-size: 14px; }
.register-link { text-align: center; margin-top: 16px; font-size: 13px; }
.register-link a { color: #409eff; }
</style>
