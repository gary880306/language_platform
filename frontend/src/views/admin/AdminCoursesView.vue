<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>課程管理</span>
        <el-button type="primary" @click="openCreateDialog">
          <el-icon><Plus /></el-icon> 新增課程
        </el-button>
      </div>
    </template>

    <el-table :data="courses" v-loading="loading" style="width:100%">
      <el-table-column prop="courseId" label="ID" width="60" />
      <el-table-column label="封面" width="80">
        <template #default="{ row }">
          <el-image :src="row.imageUrl || `/api/images/${row.imageId}`" style="width:60px;height:40px;object-fit:cover;border-radius:4px" />
        </template>
      </el-table-column>
      <el-table-column prop="courseName" label="課程名稱" min-width="150" />
      <el-table-column prop="categoryName" label="分類" width="100" />
      <el-table-column prop="teacher" label="講師" width="100" />
      <el-table-column prop="time" label="時數" width="80">
        <template #default="{ row }">{{ row.time }}h</template>
      </el-table-column>
      <el-table-column prop="price" label="價格" width="100">
        <template #default="{ row }">NT$ {{ formatPrice(row.price) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="openEditDialog(row)">編輯</el-button>
          <el-button text type="danger" size="small" @click="deleteCourse(row.courseId)">刪除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/編輯課程 Dialog -->
    <el-dialog v-model="showDialog" :title="editingId ? '編輯課程' : '新增課程'" width="600px">
      <el-form ref="formRef" :model="form" label-width="90px">
        <el-form-item label="課程名稱" required>
          <el-input v-model="form.courseName" />
        </el-form-item>
        <el-form-item label="分類" required>
          <el-select v-model="form.categoryId" style="width:100%">
            <el-option v-for="c in categories" :key="c.categoryId" :label="c.categoryName" :value="c.categoryId" />
          </el-select>
        </el-form-item>
        <el-form-item label="講師">
          <el-input v-model="form.teacher" />
        </el-form-item>
        <el-form-item label="時數（小時）">
          <el-input-number v-model="form.time" :min="0.5" :step="0.5" style="width:100%" />
        </el-form-item>
        <el-form-item label="價格（NT$）">
          <el-input-number v-model="form.price" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="課程影片">
          <el-input v-model="form.videoUrl" placeholder="YouTube 或影片 URL" />
        </el-form-item>
        <el-form-item label="課程封面">
          <el-upload
            :auto-upload="false"
            :show-file-list="false"
            accept="image/*"
            @change="handleFileChange"
          >
            <el-button size="small">選擇圖片</el-button>
            <span v-if="form.imageFile" style="margin-left:8px;font-size:12px">{{ form.imageFile.name }}</span>
          </el-upload>
        </el-form-item>
        <el-form-item label="課程介紹">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveCourse">儲存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { coursesApi } from '@/api/courses'
import { adminApi } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const courses = ref([])
const categories = ref([])
const showDialog = ref(false)
const editingId = ref(null)
const form = ref({ courseName: '', categoryId: null, teacher: '', time: 1, price: 100, videoUrl: '', description: '', imageFile: null })

onMounted(async () => {
  loading.value = true
  try {
    const [courseRes, catRes] = await Promise.all([
      coursesApi.adminGetAll(),
      coursesApi.getCategories(),
    ])
    courses.value = courseRes.data.courses
    categories.value = catRes.data
  } catch { ElMessage.error('載入失敗') }
  finally { loading.value = false }
})

function openCreateDialog() {
  editingId.value = null
  form.value = { courseName: '', categoryId: null, teacher: '', time: 1, price: 100, videoUrl: '', description: '', imageFile: null }
  showDialog.value = true
}

function openEditDialog(course) {
  editingId.value = course.courseId
  form.value = { ...course, imageFile: null }
  showDialog.value = true
}

function handleFileChange(file) {
  form.value.imageFile = file.raw
}

async function saveCourse() {
  saving.value = true
  try {
    const fd = new FormData()
    fd.append('courseName', form.value.courseName)
    fd.append('categoryId', form.value.categoryId)
    fd.append('teacher', form.value.teacher)
    fd.append('time', form.value.time)
    fd.append('price', form.value.price)
    fd.append('videoUrl', form.value.videoUrl)
    fd.append('description', form.value.description)
    if (form.value.imageFile) fd.append('imageUrl', form.value.imageFile)

    if (editingId.value) {
      await coursesApi.adminUpdate(editingId.value, fd)
    } else {
      await coursesApi.adminCreate(fd)
    }

    ElMessage.success(editingId.value ? '課程已更新' : '課程已新增')
    showDialog.value = false
    const res = await coursesApi.adminGetAll()
    courses.value = res.data.courses
  } catch { ElMessage.error('儲存失敗') }
  finally { saving.value = false }
}

async function deleteCourse(id) {
  await ElMessageBox.confirm('確定刪除此課程？', '刪除確認', { type: 'warning' })
  try {
    await coursesApi.adminDelete(id)
    courses.value = courses.value.filter(c => c.courseId !== id)
    ElMessage.success('課程已刪除')
  } catch { ElMessage.error('刪除失敗') }
}

function formatPrice(p) { return p?.toLocaleString('zh-TW') ?? '0' }
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
