import api from './index'

export const userApi = {
  getInfo: () => api.get('/user/info'),
  updateInfo: (data) => api.put('/user/info', data),
  getCoupons: () => api.get('/user/coupons'),
  deleteCoupon: (couponId) => api.delete(`/user/coupons/${couponId}`),
}

export const chatroomApi = {
  getRooms: () => api.get('/chatrooms'),
  createRoom: (data) => api.post('/chatrooms', data),
  getMessages: (roomId) => api.get(`/chatrooms/${roomId}/messages`),
}
