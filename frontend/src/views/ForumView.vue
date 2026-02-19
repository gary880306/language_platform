<template>
  <div class="forum-page">
    <div class="forum-header">
      <h2>語言學習論壇</h2>
      <el-button v-if="authStore.isLoggedIn" type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon> 發文
      </el-button>
    </div>

    <!-- 語言分類過濾 -->
    <div class="filter-bar">
      <el-radio-group v-model="selectedLanguage" @change="fetchPosts">
        <el-radio-button :label="null">全部</el-radio-button>
        <el-radio-button v-for="lang in languages" :key="lang.id" :label="lang.id">
          {{ lang.language }}
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- 貼文列表 -->
    <div v-loading="loading">
      <div v-if="posts.length > 0" class="post-list">
        <el-card
          v-for="post in posts"
          :key="post.id"
          class="post-card"
          shadow="hover"
          @click="goToPost(post.id)"
        >
          <div class="post-header">
            <div class="post-author">
              <el-avatar size="small" :icon="User" />
              <span>{{ post.userName || '用戶' }}</span>
            </div>
            <span class="post-time">{{ formatTime(post.createTime) }}</span>
          </div>
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-preview">{{ post.content?.slice(0, 100) }}...</p>
          <div class="post-footer">
            <span><el-icon><ChatDotRound /></el-icon> {{ post.commentCount ?? 0 }} 留言</span>
            <span><el-icon><Star /></el-icon> {{ post.likeCount ?? 0 }} 讚</span>
          </div>
        </el-card>
      </div>
      <el-empty v-else description="暫無貼文" />
    </div>

    <!-- 發文 Dialog -->
    <el-dialog v-model="showCreateDialog" title="發表新文章" width="600px">
      <el-form :model="newPost" label-position="top">
        <el-form-item label="語言分類">
          <el-select v-model="newPost.languageId" placeholder="選擇語言" style="width:100%">
            <el-option v-for="lang in languages" :key="lang.id" :label="lang.language" :value="lang.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="標題">
          <el-input v-model="newPost.title" placeholder="輸入文章標題" />
        </el-form-item>
        <el-form-item label="內容">
          <el-input v-model="newPost.content" type="textarea" :rows="6" placeholder="輸入文章內容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="createPost">發布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { forumApi } from '@/api/forum'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-tw'

dayjs.extend(relativeTime)
dayjs.locale('zh-tw')

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const submitting = ref(false)
const posts = ref([])
const languages = ref([])
const selectedLanguage = ref(null)
const showCreateDialog = ref(false)
const newPost = ref({ title: '', content: '', languageId: null })

onMounted(async () => {
  await Promise.all([fetchPosts(), fetchLanguages()])
})

async function fetchPosts() {
  loading.value = true
  try {
    const res = await forumApi.getPosts(selectedLanguage.value)
    posts.value = res.data
  } catch { ElMessage.error('載入貼文失敗') }
  finally { loading.value = false }
}

async function fetchLanguages() {
  try {
    const res = await forumApi.getLanguages()
    languages.value = res.data
  } catch {}
}

async function createPost() {
  if (!newPost.value.title || !newPost.value.content) {
    ElMessage.warning('標題與內容不可為空')
    return
  }
  submitting.value = true
  try {
    await forumApi.createPost(newPost.value)
    ElMessage.success('發文成功')
    showCreateDialog.value = false
    newPost.value = { title: '', content: '', languageId: null }
    await fetchPosts()
  } catch { ElMessage.error('發文失敗') }
  finally { submitting.value = false }
}

function goToPost(id) { router.push(`/forum/posts/${id}`) }
function formatTime(time) { return dayjs(time).fromNow() }
</script>

<style scoped>
.forum-page { padding: 8px 0; }
.forum-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.forum-header h2 { font-size: 24px; }
.filter-bar { margin-bottom: 20px; }
.post-list { display: flex; flex-direction: column; gap: 16px; }
.post-card { cursor: pointer; }
.post-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.post-author { display: flex; align-items: center; gap: 8px; font-size: 13px; }
.post-time { font-size: 12px; color: #909399; }
.post-title { font-size: 18px; margin-bottom: 8px; }
.post-preview { color: #606266; font-size: 14px; line-height: 1.6; margin-bottom: 12px; }
.post-footer { display: flex; gap: 16px; font-size: 13px; color: #909399; }
.post-footer span { display: flex; align-items: center; gap: 4px; }
</style>
