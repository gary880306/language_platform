import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // ==================== 公開佈局 ====================
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      children: [
        { path: '', redirect: '/courses' },
        {
          path: 'courses',
          name: 'Courses',
          component: () => import('@/views/CoursesView.vue'),
        },
        {
          path: 'courses/:id',
          name: 'CourseDetail',
          component: () => import('@/views/CourseDetailView.vue'),
        },
        {
          path: 'forum',
          name: 'Forum',
          component: () => import('@/views/ForumView.vue'),
        },
        {
          path: 'forum/posts/:id',
          name: 'PostDetail',
          component: () => import('@/views/PostDetailView.vue'),
        },
      ],
    },

    // ==================== 認證頁面 ====================
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/LoginView.vue'),
      meta: { guestOnly: true },
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { guestOnly: true },
    },
    {
      path: '/forgot-password',
      name: 'ForgotPassword',
      component: () => import('@/views/ForgotPasswordView.vue'),
      meta: { guestOnly: true },
    },
    {
      path: '/reset-password',
      name: 'ResetPassword',
      component: () => import('@/views/ResetPasswordView.vue'),
    },

    // ==================== 需登入的頁面 ====================
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: 'cart',
          name: 'Cart',
          component: () => import('@/views/CartView.vue'),
        },
        {
          path: 'orders',
          name: 'Orders',
          component: () => import('@/views/OrdersView.vue'),
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('@/views/ProfileView.vue'),
        },
        {
          path: 'chatroom',
          name: 'ChatRoom',
          component: () => import('@/views/ChatRoomView.vue'),
        },
        {
          path: 'chatroom/:id',
          name: 'Chat',
          component: () => import('@/views/ChatView.vue'),
        },
      ],
    },

    // ==================== 管理員後台 ====================
    {
      path: '/admin',
      component: () => import('@/layouts/AdminLayout.vue'),
      meta: { requiresAdmin: true },
      children: [
        { path: '', redirect: '/admin/users' },
        {
          path: 'users',
          name: 'AdminUsers',
          component: () => import('@/views/admin/AdminUsersView.vue'),
        },
        {
          path: 'courses',
          name: 'AdminCourses',
          component: () => import('@/views/admin/AdminCoursesView.vue'),
        },
        {
          path: 'orders',
          name: 'AdminOrders',
          component: () => import('@/views/admin/AdminOrdersView.vue'),
        },
        {
          path: 'coupons',
          name: 'AdminCoupons',
          component: () => import('@/views/admin/AdminCouponsView.vue'),
        },
        {
          path: 'reports',
          name: 'AdminReports',
          component: () => import('@/views/admin/AdminReportsView.vue'),
        },
      ],
    },
    {
      path: '/admin/login',
      name: 'AdminLogin',
      component: () => import('@/views/admin/AdminLoginView.vue'),
      meta: { guestOnly: true },
    },

    // ==================== 404 ====================
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('@/views/NotFoundView.vue'),
    },
  ],
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) return savedPosition
    return { top: 0 }
  },
})

// 導航守衛
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAdmin) {
    if (!authStore.isLoggedIn || authStore.levelId !== 2) {
      return next('/admin/login')
    }
  }

  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    return next({ name: 'Login', query: { redirect: to.fullPath } })
  }

  if (to.meta.guestOnly && authStore.isLoggedIn) {
    return next(authStore.levelId === 2 ? '/admin' : '/courses')
  }

  next()
})

export default router
