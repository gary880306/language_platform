<template>
  <div class="orders-page">
    <h2 class="page-title">我的訂單</h2>
    <el-card v-loading="loading">
      <el-table :data="orders" style="width:100%">
        <el-table-column prop="orderId" label="訂單編號" width="100" />
        <el-table-column prop="createdDate" label="訂單日期" width="200" />
        <el-table-column label="折扣" width="120">
          <template #default="{ row }">
            {{ row.discount ? `NT$ ${row.discount}` : '無' }}
          </template>
        </el-table-column>
        <el-table-column label="總金額" width="140">
          <template #default="{ row }">
            <span class="price">NT$ {{ formatPrice(row.totalAmount - (row.discount || 0)) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button text type="primary" @click="viewDetail(row.orderId)">查看明細</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 訂單明細 Dialog -->
    <el-dialog v-model="showDetail" title="訂單明細" width="600px">
      <el-table :data="orderItems" style="width:100%">
        <el-table-column prop="courseName" label="課程名稱" />
        <el-table-column prop="price" label="價格" width="120">
          <template #default="{ row }">NT$ {{ formatPrice(row.price) }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ordersApi } from '@/api/cart'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const orders = ref([])
const showDetail = ref(false)
const orderItems = ref([])

onMounted(async () => {
  loading.value = true
  try {
    const res = await ordersApi.getUserOrders()
    orders.value = res.data
  } catch { ElMessage.error('載入訂單失敗') }
  finally { loading.value = false }
})

async function viewDetail(orderId) {
  try {
    const res = await ordersApi.getOrderDetail(orderId)
    orderItems.value = res.data.items
    showDetail.value = true
  } catch { ElMessage.error('載入明細失敗') }
}

function formatPrice(p) { return p?.toLocaleString('zh-TW') ?? '0' }
</script>

<style scoped>
.orders-page { padding: 8px 0; }
.page-title { font-size: 24px; margin-bottom: 24px; }
.price { font-weight: bold; color: #f56c6c; }
</style>
