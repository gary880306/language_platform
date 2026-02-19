<template>
  <el-card class="course-card" shadow="hover" @click="goToDetail">
    <div class="course-image">
      <img :src="imageUrl" :alt="course.courseName" loading="lazy" />
      <el-tag class="category-tag" size="small" type="info">{{ course.categoryName }}</el-tag>
    </div>
    <div class="course-info">
      <h3 class="course-name">{{ course.courseName }}</h3>
      <p class="teacher">
        <el-icon><User /></el-icon>
        {{ course.teacher }}
      </p>
      <p class="duration">
        <el-icon><Timer /></el-icon>
        {{ course.time }} 小時
      </p>
      <div class="course-footer">
        <span class="price">NT$ {{ formatPrice(course.price) }}</span>
        <el-button
          v-if="showCart"
          type="primary"
          size="small"
          :loading="addingToCart"
          @click.stop="addToCart"
        >
          加入購物車
        </el-button>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const props = defineProps({
  course: { type: Object, required: true },
  showCart: { type: Boolean, default: true },
})

const router = useRouter()
const cartStore = useCartStore()
const authStore = useAuthStore()
const addingToCart = ref(false)

const imageUrl = computed(() =>
  props.course.imageUrl || `/api/images/${props.course.imageId}` || '/placeholder.jpg'
)

function goToDetail() {
  router.push(`/courses/${props.course.courseId}`)
}

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
    await cartStore.addItem(props.course.courseId)
    ElMessage.success('已加入購物車')
  } catch (e) {
    const msg = e.response?.data?.message || '加入失敗'
    ElMessage.error(msg)
  } finally {
    addingToCart.value = false
  }
}
</script>

<style scoped>
.course-card {
  cursor: pointer;
  transition: transform 0.2s;
  height: 100%;
}
.course-card:hover {
  transform: translateY(-4px);
}
.course-image {
  position: relative;
  height: 180px;
  overflow: hidden;
  border-radius: 4px;
  margin-bottom: 12px;
}
.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.category-tag {
  position: absolute;
  top: 8px;
  left: 8px;
}
.course-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.course-name {
  font-size: 15px;
  font-weight: 600;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.teacher, .duration {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}
.course-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
}
.price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}
</style>
