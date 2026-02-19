<template>
  <div v-loading="loading" class="post-detail">
    <template v-if="post">
      <el-page-header @back="$router.back()">
        <template #content><span>文章詳情</span></template>
      </el-page-header>

      <el-card class="post-card" style="margin-top:16px">
        <h1 class="post-title">{{ post.title }}</h1>
        <div class="post-meta">
          <el-avatar size="small" :icon="User" />
          <span>{{ post.userName || '用戶' }}</span>
          <span>{{ formatTime(post.createTime) }}</span>
        </div>
        <el-divider />
        <div class="post-content">{{ post.content }}</div>
        <div class="post-actions">
          <el-button
            :type="userLiked ? 'primary' : 'default'"
            :icon="Star"
            @click="toggleLike"
            :loading="liking"
          >
            {{ likesCount }} 個讚
          </el-button>
          <template v-if="authStore.user?.userId === post.userId || authStore.isAdmin">
            <el-button type="danger" @click="deletePost">刪除文章</el-button>
          </template>
        </div>
      </el-card>

      <!-- 留言區 -->
      <div class="comments-section">
        <h3>留言 ({{ comments.length }})</h3>

        <!-- 新增留言 -->
        <div v-if="authStore.isLoggedIn" class="comment-input">
          <el-input
            v-model="newComment"
            type="textarea"
            :rows="3"
            placeholder="分享你的想法..."
          />
          <el-button type="primary" style="margin-top:8px" @click="addComment" :loading="submitting">
            送出留言
          </el-button>
        </div>
        <el-alert v-else type="info" show-icon :closable="false">
          <router-link to="/login">登入</router-link> 後才能留言
        </el-alert>

        <!-- 留言列表 -->
        <div class="comment-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <el-avatar size="small" :icon="User" />
            <div class="comment-body">
              <div class="comment-header">
                <span class="comment-author">{{ comment.userName || '用戶' }}</span>
                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
              </div>
              <p class="comment-content">{{ comment.content }}</p>
            </div>
            <el-button
              v-if="authStore.user?.userId === comment.userId || authStore.isAdmin"
              text
              type="danger"
              size="small"
              @click="deleteComment(comment.id)"
            >
              刪除
            </el-button>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Star, User } from '@element-plus/icons-vue'
import { forumApi } from '@/api/forum'
import { useAuthStore } from '@/stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-tw'

dayjs.extend(relativeTime)
dayjs.locale('zh-tw')

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const liking = ref(false)
const submitting = ref(false)
const post = ref(null)
const comments = ref([])
const likesCount = ref(0)
const userLiked = ref(false)
const newComment = ref('')

onMounted(async () => {
  loading.value = true
  try {
    const [postRes, commentsRes, countRes] = await Promise.all([
      forumApi.getPostById(route.params.id),
      forumApi.getComments(route.params.id),
      forumApi.getLikesCount(route.params.id),
    ])
    post.value = postRes.data
    comments.value = commentsRes.data
    likesCount.value = countRes.data

    if (authStore.isLoggedIn) {
      const likedRes = await forumApi.checkLiked(route.params.id)
      userLiked.value = likedRes.data
    }
  } catch { ElMessage.error('載入文章失敗') }
  finally { loading.value = false }
})

async function toggleLike() {
  if (!authStore.isLoggedIn) { ElMessage.warning('請先登入'); return }
  liking.value = true
  try {
    if (userLiked.value) {
      await forumApi.unlikePost(route.params.id)
      likesCount.value--
      userLiked.value = false
    } else {
      await forumApi.likePost(route.params.id)
      likesCount.value++
      userLiked.value = true
    }
  } catch {} finally { liking.value = false }
}

async function addComment() {
  if (!newComment.value.trim()) return
  submitting.value = true
  try {
    await forumApi.createComment({ content: newComment.value, postId: Number(route.params.id) })
    newComment.value = ''
    const res = await forumApi.getComments(route.params.id)
    comments.value = res.data
  } catch { ElMessage.error('留言失敗') }
  finally { submitting.value = false }
}

async function deleteComment(id) {
  await forumApi.deleteComment(id)
  comments.value = comments.value.filter(c => c.id !== id)
}

async function deletePost() {
  await ElMessageBox.confirm('確定刪除此文章？', '刪除確認', { type: 'warning' })
  await forumApi.deletePost(route.params.id)
  ElMessage.success('文章已刪除')
  router.push('/forum')
}

function formatTime(time) { return dayjs(time).fromNow() }
</script>

<style scoped>
.post-detail { padding: 8px 0; }
.post-title { font-size: 26px; margin-bottom: 12px; }
.post-meta { display: flex; align-items: center; gap: 10px; font-size: 13px; color: #909399; }
.post-content { font-size: 15px; line-height: 1.9; white-space: pre-line; }
.post-actions { margin-top: 20px; display: flex; gap: 12px; }
.comments-section { margin-top: 32px; }
.comments-section h3 { margin-bottom: 16px; font-size: 18px; }
.comment-input { margin-bottom: 24px; }
.comment-list { display: flex; flex-direction: column; gap: 16px; }
.comment-item { display: flex; gap: 12px; align-items: flex-start; }
.comment-body { flex: 1; }
.comment-header { display: flex; gap: 10px; align-items: center; margin-bottom: 4px; }
.comment-author { font-weight: 600; font-size: 13px; }
.comment-time { font-size: 12px; color: #909399; }
.comment-content { font-size: 14px; line-height: 1.6; }
</style>
