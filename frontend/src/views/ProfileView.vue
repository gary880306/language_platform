<template>
  <div class="profile-page">
    <h2 class="page-title">個人中心</h2>

    <el-tabs v-model="activeTab">
      <!-- 個人資料 -->
      <el-tab-pane label="個人資料" name="info">
        <el-card v-loading="loading">
          <el-form ref="formRef" :model="form" label-width="100px" style="max-width:480px">
            <el-form-item label="姓名">
              <el-input v-model="form.userName" />
            </el-form-item>
            <el-form-item label="Email">
              <el-input v-model="form.email" disabled />
            </el-form-item>
            <el-form-item label="手機號碼">
              <el-input v-model="form.phoneNumber" />
            </el-form-item>
            <el-form-item label="地址">
              <el-input v-model="form.address" />
            </el-form-item>
            <el-form-item label="生日">
              <el-date-picker v-model="form.birth" type="date" style="width:100%" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="saveProfile">儲存</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 我的訂單 -->
      <el-tab-pane label="我的訂單" name="orders">
        <el-card v-loading="ordersLoading">
          <el-table :data="orders" style="width:100%">
            <el-table-column prop="orderId" label="訂單編號" width="100" />
            <el-table-column prop="createdDate" label="訂單日期" width="180" />
            <el-table-column label="總金額" width="120">
              <template #default="{ row }">
                NT$ {{ formatPrice(row.totalAmount - (row.discount || 0)) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button text size="small" @click="viewOrder(row.orderId)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 我的優惠券 -->
      <el-tab-pane label="我的優惠券" name="coupons">
        <div v-loading="couponsLoading" class="coupon-grid">
          <el-card
            v-for="coupon in coupons"
            :key="coupon.couponId"
            class="coupon-card"
          >
            <div class="coupon-discount">
              {{ coupon.discountType === 'PERCENTAGE'
                  ? `${coupon.discountValue}% OFF`
                  : `NT$ ${coupon.discountValue} OFF` }}
            </div>
            <p class="coupon-desc">{{ coupon.description }}</p>
            <div class="coupon-validity">
              有效期至 {{ formatDate(coupon.endDate) }}
            </div>
            <el-button
              text
              type="danger"
              size="small"
              @click="deleteCoupon(coupon.couponId)"
            >
              刪除
            </el-button>
          </el-card>
          <el-empty v-if="!couponsLoading && coupons.length === 0" description="暫無優惠券" />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userApi } from '@/api/user'
import { ordersApi } from '@/api/cart'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()

const activeTab = ref(route.query.tab || 'info')
const loading = ref(false)
const saving = ref(false)
const ordersLoading = ref(false)
const couponsLoading = ref(false)

const form = ref({ userName: '', email: '', phoneNumber: '', address: '', birth: null })
const orders = ref([])
const coupons = ref([])

onMounted(async () => {
  loading.value = true
  try {
    const res = await userApi.getInfo()
    Object.assign(form.value, res.data)
  } catch { ElMessage.error('載入資料失敗') }
  finally { loading.value = false }

  await loadOrders()
  await loadCoupons()
})

async function loadOrders() {
  ordersLoading.value = true
  try {
    const res = await ordersApi.getUserOrders()
    orders.value = res.data
  } catch {} finally { ordersLoading.value = false }
}

async function loadCoupons() {
  couponsLoading.value = true
  try {
    const res = await userApi.getCoupons()
    coupons.value = res.data
  } catch {} finally { couponsLoading.value = false }
}

async function saveProfile() {
  saving.value = true
  try {
    await userApi.updateInfo(form.value)
    ElMessage.success('個人資料已更新')
  } catch { ElMessage.error('更新失敗') }
  finally { saving.value = false }
}

async function deleteCoupon(id) {
  await userApi.deleteCoupon(id)
  coupons.value = coupons.value.filter(c => c.couponId !== id)
  ElMessage.success('優惠券已刪除')
}

function viewOrder(orderId) { router.push(`/orders?id=${orderId}`) }
function formatPrice(p) { return p?.toLocaleString('zh-TW') ?? '0' }
function formatDate(d) { return dayjs(d).format('YYYY/MM/DD') }
</script>

<style scoped>
.profile-page { padding: 8px 0; }
.page-title { font-size: 24px; margin-bottom: 24px; }
.coupon-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}
.coupon-card { text-align: center; }
.coupon-discount { font-size: 28px; font-weight: bold; color: #f56c6c; margin-bottom: 8px; }
.coupon-desc { font-size: 13px; color: #606266; margin-bottom: 8px; }
.coupon-validity { font-size: 12px; color: #909399; margin-bottom: 8px; }
</style>
