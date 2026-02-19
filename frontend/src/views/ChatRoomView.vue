<template>
  <div class="chatroom-page">
    <div class="chatroom-header">
      <h2>聊天室列表</h2>
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon> 建立聊天室
      </el-button>
    </div>

    <div v-loading="loading" class="room-list">
      <el-card
        v-for="room in rooms"
        :key="room.chatRoomId"
        class="room-card"
        shadow="hover"
        @click="$router.push(`/chatroom/${room.chatRoomId}`)"
      >
        <div class="room-info">
          <el-icon size="32" color="#409eff"><ChatDotRound /></el-icon>
          <div>
            <h3>{{ room.chatRoomName }}</h3>
            <span class="room-owner">房主 ID: {{ room.ownerId }}</span>
          </div>
        </div>
        <el-button type="primary" size="small">加入</el-button>
      </el-card>
      <el-empty v-if="!loading && rooms.length === 0" description="目前沒有聊天室" />
    </div>

    <el-dialog v-model="showCreateDialog" title="建立聊天室" width="400px">
      <el-form>
        <el-form-item label="聊天室名稱">
          <el-input v-model="newRoomName" placeholder="輸入聊天室名稱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="createRoom">建立</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { chatroomApi } from '@/api/user'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const loading = ref(false)
const creating = ref(false)
const rooms = ref([])
const showCreateDialog = ref(false)
const newRoomName = ref('')

onMounted(async () => {
  loading.value = true
  try { rooms.value = (await chatroomApi.getRooms()).data }
  catch { ElMessage.error('載入聊天室失敗') }
  finally { loading.value = false }
})

async function createRoom() {
  if (!newRoomName.value.trim()) { ElMessage.warning('請輸入聊天室名稱'); return }
  creating.value = true
  try {
    const res = await chatroomApi.createRoom({
      chatRoomName: newRoomName.value,
      ownerId: authStore.user.userId,
    })
    rooms.value.unshift(res.data)
    showCreateDialog.value = false
    newRoomName.value = ''
    ElMessage.success('聊天室建立成功')
  } catch { ElMessage.error('建立失敗') }
  finally { creating.value = false }
}
</script>

<style scoped>
.chatroom-page { padding: 8px 0; }
.chatroom-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.chatroom-header h2 { font-size: 24px; }
.room-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 16px; }
.room-card { cursor: pointer; }
.room-info { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.room-info h3 { margin-bottom: 4px; }
.room-owner { font-size: 12px; color: #909399; }
</style>
