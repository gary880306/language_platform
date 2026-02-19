<template>
  <div v-loading="loading" class="course-detail">
    <template v-if="course">
      <el-row :gutter="32">
        <!-- 左側：課程資訊 -->
        <el-col :span="16">
          <el-image :src="courseImageUrl" fit="cover" class="course-banner" />
          <h1 class="course-title">{{ course.courseName }}</h1>
          <div class="course-meta">
            <el-tag>{{ course.categoryName }}</el-tag>
            <span><el-icon><User /></el-icon> {{ course.teacher }}</span>
            <span><el-icon><Timer /></el-icon> {{ course.time }} 小時</span>
          </div>
          <el-divider />
          <div class="course-description">
            <h3>課程介紹</h3>
            <p>{{ course.description }}</p>
          </div>
          <div v-if="course.videoUrl" class="course-video">
            <h3>課程預覽</h3>
            <div class="video-wrapper">
              <iframe :src="course.videoUrl" frameborder="0" allowfullscreen />
            </div>
          </div>
        </el-col>

        <!-- 右側：購買卡片 -->
        <el-col :span="8">
          <el-card class="purchase-card" shadow="always">
            <div class="price">NT$ {{ formatPrice(course.price) }}</div>
            <el-button
              type="primary"
              size="large"
              style="width:100%;margin-top:16px"
              :loading="addingToCart"
              @click="addToCart"
            >
              加入購物車
            </el-button>
            <el-button
              size="large"
              style="width:100%;margin-top:8px"
              @click="goToCart"
            >
              立即購買
            </el-button>
            <el-divider />
            <div class="course-includes">
              <p><el-icon><Clock /></el-icon> {{ course.time }} 小時完整課程</p>
              <p><el-icon><VideoPlay /></el-icon> 高畫質影片</p>
              <p><el-icon><Phone /></el-icon> 手機/電腦皆可觀看</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { coursesApi } from '@/api/courses'
import { useCartStore } from '@/stores/cart'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const authStore = useAuthStore()

const loading = ref(false)
const addingToCart = ref(false)
const course = ref(null)

const courseImageUrl = computed(() =>
  course.value?.imageUrl || `/api/images/${course.value?.imageId}` || '/placeholder.jpg'
)

onMounted(async () => {
  loading.value = true
  try {
    const res = await coursesApi.getById(route.params.id)
    course.value = res.data
  } catch {
    ElMessage.error('載入課程失敗')
    router.push('/courses')
  } finally {
    loading.value = false
  }
})

function formatPrice(price) {
  return price?.toLocaleString('zh-TW') ?? '0'
}

async function addToCart() {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('請先登入')
    router.push('/login')
    return
  }
  addingToCart.value = true
  try {
    await cartStore.addItem(course.value.courseId)
    ElMessage.success('已加入購物車')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '加入失敗')
  } finally {
    addingToCart.value = false
  }
}

async function goToCart() {
  await addToCart()
  router.push('/cart')
}
</script>

<style scoped>
.course-detail { padding: 8px 0; }
.course-banner {
  width: 100%;
  height: 320px;
  border-radius: 8px;
  margin-bottom: 20px;
  object-fit: cover;
}
.course-title { font-size: 28px; margin-bottom: 12px; }
.course-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  color: #606266;
  font-size: 14px;
  margin-bottom: 16px;
}
.course-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}
.course-description h3, .course-video h3 { margin-bottom: 12px; }
.course-description p { line-height: 1.8; color: #606266; }
.video-wrapper {
  position: relative;
  padding-bottom: 56.25%;
  height: 0;
}
.video-wrapper iframe {
  position: absolute;
  top: 0; left: 0;
  width: 100%; height: 100%;
}
.purchase-card { position: sticky; top: 88px; }
.price { font-size: 32px; font-weight: bold; color: #f56c6c; }
.course-includes { display: flex; flex-direction: column; gap: 8px; font-size: 14px; color: #606266; }
.course-includes p { display: flex; align-items: center; gap: 6px; }
</style>
