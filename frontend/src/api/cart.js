import api from './index'

export const cartApi = {
  getCart: () => api.get('/cart'),
  addToCart: (courseId) => api.post('/cart/add', { courseId }),
  removeFromCart: (courseId) => api.delete(`/cart/items/${courseId}`),
  applyCoupon: (couponCode) => api.post('/cart/coupon', { couponCode }),
  checkout: (couponId) => api.post('/cart/checkout', { couponId }),
}

export const ordersApi = {
  getUserOrders: () => api.get('/user/orders'),
  getOrderDetail: (orderId) => api.get(`/user/orders/${orderId}`),

  // 管理員
  getAllOrders: () => api.get('/admin/orders'),
  getAdminOrderDetail: (orderId) => api.get(`/admin/orders/${orderId}`),
}
