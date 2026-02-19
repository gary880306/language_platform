<template>
  <div class="cart-page">
    <h2 class="page-title">購物車</h2>

    <div v-loading="loading">
      <template v-if="cartStore.items.length > 0">
        <el-row :gutter="24">
          <!-- 課程列表 -->
          <el-col :span="16">
            <el-card>
              <div v-for="item in cartStore.items" :key="item.itemId" class="cart-item">
                <img :src="item.course?.imageUrl || '/placeholder.jpg'" class="item-image" />
                <div class="item-info">
                  <h4>{{ item.course?.courseName }}</h4>
                  <span class="item-teacher">{{ item.course?.teacher }}</span>
                </div>
                <div class="item-price">NT$ {{ formatPrice(item.course?.price) }}</div>
                <el-button
                  text
                  type="danger"
                  :icon="Delete"
                  @click="removeItem(item.course?.courseId)"
                />
              </div>
            </el-card>
          </el-col>

          <!-- 結帳摘要 -->
          <el-col :span="8">
            <el-card class="summary-card">
              <h3>訂單摘要</h3>
              <div class="summary-row">
                <span>小計</span>
                <span>NT$ {{ formatPrice(cartStore.subtotal) }}</span>
              </div>

              <!-- 優惠券 -->
              <div class="coupon-input">
                <el-input v-model="couponCode" placeholder="輸入優惠碼" size="small">
                  <template #append>
                    <el-button size="small" @click="applyCoupon" :loading="applyingCoupon">套用</el-button>
                  </template>
                </el-input>
              </div>

              <div v-if="cartStore.appliedCoupon" class="summary-row discount-row">
                <span>優惠折扣</span>
                <span class="discount">- NT$ {{ formatPrice(cartStore.discount) }}</span>
                <el-button text size="small" type="danger" @click="cartStore.removeCoupon()">移除</el-button>
              </div>

              <el-divider />
              <div class="summary-row total-row">
                <span>總計</span>
                <span class="total">NT$ {{ formatPrice(cartStore.total) }}</span>
              </div>

              <el-button
                type="primary"
                size="large"
                style="width:100%;margin-top:16px"
                :loading="checkingOut"
                @click="checkout"
              >
                前往結帳
              </el-button>
            </el-card>
          </el-col>
        </el-row>
      </template>

      <el-empty v-else description="購物車是空的">
        <el-button type="primary" @click="$router.push('/courses')">瀏覽課程</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Delete } from '@element-plus/icons-vue'
import { useCartStore } from '@/stores/cart'
import { ElMessage } from 'element-plus'

const router = useRouter()
const cartStore = useCartStore()
const loading = ref(false)
const couponCode = ref('')
const applyingCoupon = ref(false)
const checkingOut = ref(false)

onMounted(async () => {
  loading.value = true
  try { await cartStore.fetchCart() } catch { ElMessage.error('載入購物車失敗') }
  finally { loading.value = false }
})

function formatPrice(price) {
  return price?.toLocaleString('zh-TW') ?? '0'
}

async function removeItem(courseId) {
  try {
    await cartStore.removeItem(courseId)
    ElMessage.success('已從購物車移除')
  } catch {
    ElMessage.error('移除失敗')
  }
}

async function applyCoupon() {
  if (!couponCode.value.trim()) return
  applyingCoupon.value = true
  try {
    await cartStore.applyCoupon(couponCode.value.trim())
    ElMessage.success('優惠券套用成功')
    couponCode.value = ''
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '優惠券無效')
  } finally {
    applyingCoupon.value = false
  }
}

async function checkout() {
  checkingOut.value = true
  try {
    const result = await cartStore.checkout()
    ElMessage.success('結帳成功！')
    router.push('/orders')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '結帳失敗')
  } finally {
    checkingOut.value = false
  }
}
</script>

<style scoped>
.cart-page { padding: 8px 0; }
.page-title { font-size: 24px; margin-bottom: 24px; }
.cart-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}
.cart-item:last-child { border-bottom: none; }
.item-image { width: 80px; height: 60px; object-fit: cover; border-radius: 4px; }
.item-info { flex: 1; }
.item-info h4 { margin-bottom: 4px; }
.item-teacher { font-size: 13px; color: #909399; }
.item-price { font-size: 16px; font-weight: bold; color: #f56c6c; white-space: nowrap; }
.summary-card h3 { margin-bottom: 16px; }
.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.discount { color: #67c23a; }
.coupon-input { margin: 12px 0; }
.total-row { font-size: 18px; font-weight: bold; }
.total { color: #f56c6c; font-size: 22px; }
</style>
