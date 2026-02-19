import api from './index'

export const adminApi = {
  // 用戶管理
  getUsers: () => api.get('/admin/users'),
  updateUserStatus: (userId, isActive) =>
    api.patch(`/admin/users/${userId}/status`, { isActive }),

  // 優惠券
  getCoupons: () => api.get('/admin/coupons'),
  getAvailableCoupons: () => api.get('/admin/coupons/available'),
  createCoupon: (data) => api.post('/admin/coupons', data),
  updateCouponStatus: (id, isActive) =>
    api.patch(`/admin/coupons/${id}/activate`, { isActive }),
  deleteCoupon: (id) => api.delete(`/admin/coupons/${id}`),
  sendCoupons: (data) => api.post('/admin/coupons/send', data),

  // 訂單
  getOrders: () => api.get('/admin/orders'),
  getOrderDetail: (id) => api.get(`/admin/orders/${id}`),

  // 報表
  getReport: () => api.get('/admin/reports'),
  getSalesData: () => api.get('/admin/reports/sales'),
  getCategoryData: () => api.get('/admin/reports/categories'),
}
