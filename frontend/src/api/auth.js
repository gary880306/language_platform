import api from './index'

export const authApi = {
  login: (data) => api.post('/auth/login', data),
  adminLogin: (data) => api.post('/auth/admin/login', data),
  logout: () => api.post('/auth/logout'),
  register: (data) => api.post('/auth/register', data),
  forgotPassword: (email) => api.post('/auth/forgot-password', { email }),
  resetPassword: (data) => api.post('/auth/reset-password', data),
  getMe: () => api.get('/auth/me'),
  getCaptcha: () => '/api/auth/captcha',
  getGoogleAuthUrl: () => api.get('/auth/google/url'),
  googleCallback: (code) => api.post('/auth/google/callback', { code }),
  githubCallback: (code) => api.post('/auth/github/callback', { code }),
}
