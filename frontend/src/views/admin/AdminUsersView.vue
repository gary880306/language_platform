<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>用戶管理</span>
        <el-input v-model="searchText" placeholder="搜尋用戶..." style="width:200px" clearable>
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
      </div>
    </template>

    <el-table :data="filteredUsers" v-loading="loading" style="width:100%">
      <el-table-column prop="userId" label="ID" width="60" />
      <el-table-column prop="userName" label="姓名" />
      <el-table-column prop="email" label="Email" />
      <el-table-column prop="phoneNumber" label="手機" width="130" />
      <el-table-column prop="authType" label="登入方式" width="100">
        <template #default="{ row }">
          <el-tag size="small" :type="row.authType === 'local' ? 'info' : 'warning'">
            {{ row.authType || 'local' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="狀態" width="100">
        <template #default="{ row }">
          <el-tag :type="row.active ? 'success' : 'danger'" size="small">
            {{ row.active ? '正常' : '停權' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-switch
            :model-value="row.active"
            :loading="row._updating"
            @change="(val) => toggleStatus(row, val)"
          />
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { adminApi } from '@/api/admin'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const users = ref([])
const searchText = ref('')

const filteredUsers = computed(() =>
  users.value.filter(u =>
    !searchText.value ||
    u.userName?.includes(searchText.value) ||
    u.email?.includes(searchText.value)
  )
)

onMounted(async () => {
  loading.value = true
  try { users.value = (await adminApi.getUsers()).data }
  catch { ElMessage.error('載入用戶失敗') }
  finally { loading.value = false }
})

async function toggleStatus(user, isActive) {
  user._updating = true
  try {
    await adminApi.updateUserStatus(user.userId, isActive)
    user.active = isActive
    ElMessage.success(`已${isActive ? '啟用' : '停用'}帳號`)
  } catch { ElMessage.error('更新失敗') }
  finally { user._updating = false }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
