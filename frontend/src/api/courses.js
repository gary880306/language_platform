import api from './index'

export const coursesApi = {
  // 公開
  getAll: (params) => api.get('/courses', { params }),
  getById: (id) => api.get(`/courses/${id}`),
  getCategories: () => api.get('/categories'),

  // 管理員
  adminGetAll: () => api.get('/admin/courses'),
  adminGetById: (id) => api.get(`/admin/courses/${id}`),
  adminCreate: (formData) => api.post('/admin/courses', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
  adminUpdate: (id, formData) => api.put(`/admin/courses/${id}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
  adminDelete: (id) => api.delete(`/admin/courses/${id}`),
  adminValidate: (formData) => api.post('/admin/courses/validate', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
}
