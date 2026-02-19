<template>
  <el-card>
    <template #header><span>訂單管理</span></template>

    <el-table :data="orders" v-loading="loading" style="width:100%">
      <el-table-column prop="orderId" label="訂單ID" width="80" />
      <el-table-column label="用戶" prop="userName" width="120" />
      <el-table-column prop="createdDate" label="下單時間" width="180" />
      <el-table-column label="折扣" width="100">
        <template #default="{ row }">
          {{ row.discount ? `NT$ ${row.discount}` : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="總金額" width="130">
        <template #default="{ row }">
          <span class="price">NT$ {{ formatPrice(row.totalAmount - (row.discount || 0)) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="viewDetail(row.orderId)">明細</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showDetail" title="訂單明細" width="560px">
      <el-table :data="orderItems">
        <el-table-column prop="courseName" label="課程名稱" />
        <el-table-column label="價格" width="120">
          <template #default="{ row }">NT$ {{ formatPrice(row.price) }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api/admin'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const orders = ref([])
const showDetail = ref(false)
const orderItems = ref([])

onMounted(async () => {
  loading.value = true
  try { orders.value = (await adminApi.getOrders()).data }
  catch { ElMessage.error('載入訂單失敗') }
  finally { loading.value = false }
})

async function viewDetail(orderId) {
  try {
    const res = await adminApi.getOrderDetail(orderId)
    orderItems.value = res.data.items
    showDetail.value = true
  } catch { ElMessage.error('載入明細失敗') }
}

function formatPrice(p) { return p?.toLocaleString('zh-TW') ?? '0' }
</script>

<style scoped>
.price { font-weight: bold; color: #f56c6c; }
</style>
