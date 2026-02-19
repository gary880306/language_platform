import api from './index'

export const forumApi = {
  // 貼文
  getPosts: (languageId) => api.get('/forum/posts', { params: languageId ? { languageId } : {} }),
  getPostById: (id) => api.get(`/forum/posts/${id}`),
  createPost: (data) => api.post('/forum/posts', data),
  updatePost: (id, data) => api.put(`/forum/posts/${id}`, data),
  deletePost: (id) => api.delete(`/forum/posts/${id}`),

  // 按讚
  likePost: (postId) => api.post(`/forum/posts/${postId}/like`),
  unlikePost: (postId) => api.delete(`/forum/posts/${postId}/like`),
  getLikesCount: (postId) => api.get(`/forum/posts/${postId}/likes/count`),
  checkLiked: (postId) => api.get(`/forum/posts/${postId}/likes/check`),

  // 留言
  getComments: (postId) => api.get('/forum/comments', { params: { postId } }),
  createComment: (data) => api.post('/forum/comments', data),
  updateComment: (id, data) => api.put(`/forum/comments/${id}`, data),
  deleteComment: (id) => api.delete(`/forum/comments/${id}`),

  // 語言分類
  getLanguages: () => api.get('/forum/languages'),
}
