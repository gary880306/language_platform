import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(null)
  const user = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  const levelId = computed(() => user.value?.levelId ?? 0)
  const isAdmin = computed(() => levelId.value === 2)

  /** 從 localStorage 恢復登入狀態 */
  function restoreAuth() {
    const savedToken = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')
    if (savedToken && savedUser) {
      token.value = savedToken
      user.value = JSON.parse(savedUser)
    }
  }

  /** 登入（一般用戶） */
  async function login(email, password, captcha) {
    const res = await authApi.login({ email, password, code: captcha })
    _setAuth(res.data)
    return res.data
  }

  /** 管理員登入 */
  async function adminLogin(email, password) {
    const res = await authApi.adminLogin({ email, password })
    _setAuth(res.data)
    return res.data
  }

  /** 登出 */
  async function logout() {
    try { await authApi.logout() } catch {}
    _clearAuth()
  }

  function _setAuth(data) {
    token.value = data.token
    user.value = {
      userId: data.userId,
      email: data.email,
      userName: data.userName,
      levelId: data.levelId,
    }
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  function _clearAuth() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return {
    token,
    user,
    isLoggedIn,
    levelId,
    isAdmin,
    restoreAuth,
    login,
    adminLogin,
    logout,
  }
})
