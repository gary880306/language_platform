<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>優惠券管理</span>
        <el-button type="primary" @click="openCreateDialog">
          <el-icon><Plus /></el-icon> 新增優惠券
        </el-button>
      </div>
    </template>

    <el-table :data="coupons" v-loading="loading" style="width:100%">
      <el-table-column prop="couponId" label="ID" width="60" />
      <el-table-column prop="code" label="代碼" width="130" />
      <el-table-column prop="description" label="說明" />
      <el-table-column label="折扣" width="120">
        <template #default="{ row }">
          {{ row.discountType === 'PERCENTAGE' ? `${row.discountValue}%` : `NT$ ${row.discountValue}` }}
        </template>
      </el-table-column>
      <el-table-column prop="quantity" label="數量" width="70" />
      <el-table-column prop="endDate" label="到期日" width="120" />
      <el-table-column label="狀態" width="90">
        <template #default="{ row }">
          <el-tag :type="row.isActive ? 'success' : 'danger'" size="small">
            {{ row.isActive ? '啟用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-switch :model-value="row.isActive" @change="(v) => toggleStatus(row, v)" />
          <el-button text type="danger" size="small" @click="deleteCoupon(row.couponId)" style="margin-left:8px">
            刪除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增優惠券 Dialog -->
    <el-dialog v-model="showDialog" title="新增優惠券" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="優惠碼">
          <el-input v-model="form.code" placeholder="e.g. SUMMER2024" />
        </el-form-item>
        <el-form-item label="說明">
          <el-input v-model="form.description" />
        </el-form-item>
        <el-form-item label="折扣類型">
          <el-radio-group v-model="form.discountType">
            <el-radio-button label="FIXED">固定金額</el-radio-button>
            <el-radio-button label="PERCENTAGE">百分比</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="form.discountType === 'PERCENTAGE' ? '折扣(%)' : '折扣(NT$)'">
          <el-input-number v-model="form.discountValue" :min="1" :max="form.discountType === 'PERCENTAGE' ? 100 : 99999" style="width:100%" />
        </el-form-item>
        <el-form-item label="數量">
          <el-input-number v-model="form.quantity" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="有效期間">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="開始日期"
            end-placeholder="結束日期"
            style="width:100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveCoupon">新增</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const coupons = ref([])
const showDialog = ref(false)
const dateRange = ref(null)
const form = ref({ code: '', description: '', discountType: 'FIXED', discountValue: 100, quantity: 10, isActive: true })

onMounted(async () => {
  loading.value = true
  try { coupons.value = (await adminApi.getCoupons()).data }
  catch { ElMessage.error('載入優惠券失敗') }
  finally { loading.value = false }
})

function openCreateDialog() {
  form.value = { code: '', description: '', discountType: 'FIXED', discountValue: 100, quantity: 10, isActive: true }
  dateRange.value = null
  showDialog.value = true
}

async function saveCoupon() {
  saving.value = true
  try {
    const data = {
      ...form.value,
      startDate: dateRange.value?.[0] || null,
      endDate: dateRange.value?.[1] || null,
    }
    await adminApi.createCoupon(data)
    ElMessage.success('優惠券新增成功')
    showDialog.value = false
    coupons.value = (await adminApi.getCoupons()).data
  } catch { ElMessage.error('新增失敗') }
  finally { saving.value = false }
}

async function toggleStatus(coupon, isActive) {
  try {
    await adminApi.updateCouponStatus(coupon.couponId, isActive)
    coupon.isActive = isActive
  } catch { ElMessage.error('更新失敗') }
}

async function deleteCoupon(id) {
  await ElMessageBox.confirm('確定刪除此優惠券？', '刪除確認', { type: 'warning' })
  try {
    await adminApi.deleteCoupon(id)
    coupons.value = coupons.value.filter(c => c.couponId !== id)
    ElMessage.success('已刪除')
  } catch { ElMessage.error('刪除失敗') }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
