import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

// 請求攔截器：自動帶入 JWT Token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 回應攔截器：統一處理錯誤
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status
    const message = error.response?.data?.message || error.response?.data?.error

    if (status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      ElMessage.error('登入已過期，請重新登入')
      window.location.href = '/login'
    } else if (status === 403) {
      ElMessage.error(message || '無權限執行此操作')
    } else if (status === 400) {
      // 400 由各自的呼叫方處理
    } else if (status >= 500) {
      ElMessage.error('伺服器發生錯誤，請稍後再試')
    }

    return Promise.reject(error)
  }
)

export default api
