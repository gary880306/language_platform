<template>
  <div class="chat-page">
    <div class="chat-header">
      <el-page-header @back="$router.push('/chatroom')">
        <template #content>{{ roomName }}</template>
      </el-page-header>
      <el-tag :type="connected ? 'success' : 'danger'" size="small">
        {{ connected ? '已連線' : '連線中...' }}
      </el-tag>
    </div>

    <!-- 訊息列表 -->
    <div ref="messageContainer" class="message-list">
      <div
        v-for="(msg, i) in messages"
        :key="i"
        :class="['message', msg.type === 'JOIN' || msg.type === 'LEAVE' ? 'system' : (msg.sender === username ? 'own' : 'other')]"
      >
        <template v-if="msg.type === 'CHAT'">
          <el-avatar size="small" :icon="User" class="msg-avatar" />
          <div class="msg-bubble">
            <span class="msg-sender">{{ msg.sender }}</span>
            <div class="msg-content">{{ msg.message }}</div>
          </div>
        </template>
        <div v-else class="system-msg">
          {{ msg.sender }} {{ msg.type === 'JOIN' ? '加入了聊天室' : '離開了聊天室' }}
        </div>
      </div>
    </div>

    <!-- 輸入區 -->
    <div class="message-input">
      <el-input
        v-model="inputText"
        placeholder="輸入訊息..."
        @keyup.enter="sendMessage"
      />
      <el-button type="primary" @click="sendMessage" :disabled="!connected">
        <el-icon><Promotion /></el-icon>
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { chatroomApi } from '@/api/user'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { ElMessage } from 'element-plus'

const route = useRoute()
const authStore = useAuthStore()
const roomId = Number(route.params.id)
const username = authStore.user?.userName

const messages = ref([])
const inputText = ref('')
const connected = ref(false)
const roomName = ref('聊天室')
const messageContainer = ref(null)

let stompClient = null

onMounted(async () => {
  // 載入歷史訊息
  try {
    const res = await chatroomApi.getMessages(roomId)
    messages.value = res.data
    scrollToBottom()
  } catch {}

  // 連接 WebSocket
  connect()
})

onUnmounted(() => {
  if (stompClient?.active) {
    stompClient.publish({
      destination: `/app/chat/${roomId}/leaveUser`,
      headers: { roomId: String(roomId) },
      body: JSON.stringify({ sender: username, type: 'LEAVE' }),
    })
    stompClient.deactivate()
  }
})

function connect() {
  stompClient = new Client({
    webSocketFactory: () => new SockJS('/chatroom-websocket'),
    onConnect: () => {
      connected.value = true
      // 訂閱聊天室
      stompClient.subscribe(`/topic/chatroom/${roomId}`, (frame) => {
        const msg = JSON.parse(frame.body)
        messages.value.push(msg)
        scrollToBottom()
      })
      // 通知加入
      stompClient.publish({
        destination: `/app/chat/${roomId}/addUser`,
        headers: { roomId: String(roomId) },
        body: JSON.stringify({ sender: username, type: 'JOIN' }),
      })
    },
    onDisconnect: () => { connected.value = false },
    onStompError: () => { ElMessage.error('WebSocket 連線失敗') },
  })
  stompClient.activate()
}

function sendMessage() {
  const text = inputText.value.trim()
  if (!text || !stompClient?.active) return

  stompClient.publish({
    destination: '/app/chat.sendMessage',
    headers: { roomId: String(roomId) },
    body: JSON.stringify({ sender: username, message: text, type: 'CHAT' }),
  })
  inputText.value = ''
}

async function scrollToBottom() {
  await nextTick()
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight
  }
}
</script>

<style scoped>
.chat-page {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 140px);
  padding: 8px 0;
}
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.message-list {
  flex: 1;
  overflow-y: auto;
  background: #f0f2f5;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.message { display: flex; gap: 8px; }
.message.own { flex-direction: row-reverse; }
.message.system { justify-content: center; }
.system-msg { font-size: 12px; color: #909399; background: rgba(0,0,0,0.05); padding: 4px 12px; border-radius: 12px; }
.msg-bubble { max-width: 60%; }
.msg-sender { font-size: 12px; color: #909399; margin-bottom: 4px; display: block; }
.message.own .msg-sender { text-align: right; }
.msg-content {
  background: #fff;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
.message.own .msg-content { background: #409eff; color: #fff; }
.message-input {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}
</style>
