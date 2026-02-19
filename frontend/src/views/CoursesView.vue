<template>
  <div class="courses-page">
    <!-- 搜尋與篩選 -->
    <div class="filter-bar">
      <el-select v-model="filters.categoryName" placeholder="所有分類" clearable style="width:150px" @change="fetchCourses">
        <el-option label="所有分類" value="" />
        <el-option v-for="c in categories" :key="c.categoryId" :label="c.categoryName" :value="c.categoryName" />
      </el-select>
      <el-select v-model="filters.orderBy" style="width:140px" @change="fetchCourses">
        <el-option label="最新上架" value="createdDate" />
        <el-option label="價格低到高" value="price_asc" />
        <el-option label="價格高到低" value="price_desc" />
      </el-select>
    </div>

    <!-- 課程列表 -->
    <div v-loading="loading">
      <div v-if="courses.length > 0" class="course-grid">
        <CourseCard v-for="course in courses" :key="course.courseId" :course="course" />
      </div>
      <el-empty v-else description="目前沒有符合的課程" />
    </div>

    <!-- 分頁 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="filters.page"
        v-model:page-size="filters.size"
        :total="total"
        :page-sizes="[8, 16, 24]"
        layout="total, sizes, prev, pager, next"
        @change="fetchCourses"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { coursesApi } from '@/api/courses'
import CourseCard from '@/components/CourseCard.vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const loading = ref(false)
const courses = ref([])
const categories = ref([])
const total = ref(0)

const filters = reactive({
  keyword: route.query.keyword || '',
  categoryName: '',
  orderBy: 'createdDate',
  sort: 'desc',
  page: 1,
  size: 8,
})

watch(() => route.query.keyword, (val) => {
  filters.keyword = val || ''
  filters.page = 1
  fetchCourses()
})

onMounted(async () => {
  await Promise.all([fetchCourses(), fetchCategories()])
})

async function fetchCourses() {
  loading.value = true
  try {
    const params = {
      keyword: filters.keyword || undefined,
      categoryName: filters.categoryName || undefined,
      orderBy: filters.orderBy.includes('_') ? 'price' : filters.orderBy,
      sort: filters.orderBy === 'price_asc' ? 'asc' : 'desc',
      page: filters.page,
      size: filters.size,
    }
    const res = await coursesApi.getAll(params)
    courses.value = res.data.courses
    total.value = res.data.total
  } catch {
    ElMessage.error('載入課程失敗')
  } finally {
    loading.value = false
  }
}

async function fetchCategories() {
  try {
    const res = await coursesApi.getCategories()
    categories.value = res.data
  } catch {}
}
</script>

<style scoped>
.courses-page { padding: 8px 0; }
.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px;
}
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}
</style>
