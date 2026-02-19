<template>
  <div class="reports-page">
    <!-- 總覽卡片 -->
    <el-row :gutter="16" style="margin-bottom:24px">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-value">NT$ {{ formatPrice(report.totalRevenue) }}</div>
          <div class="stat-label">總營收</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-value">{{ report.totalOrders }}</div>
          <div class="stat-label">總訂單數</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-value">{{ report.salesData?.length ?? 0 }}</div>
          <div class="stat-label">銷售課程數</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="24">
      <!-- 課程銷售排行 -->
      <el-col :span="14">
        <el-card>
          <template #header><span>課程銷售排行</span></template>
          <div v-loading="loading">
            <canvas ref="salesChartRef" height="300" />
          </div>
        </el-card>
      </el-col>

      <!-- 分類統計 -->
      <el-col :span="10">
        <el-card>
          <template #header><span>分類學習人數</span></template>
          <div v-loading="loading">
            <canvas ref="categoryChartRef" height="300" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 銷售明細表 -->
    <el-card style="margin-top:24px">
      <template #header><span>課程銷售明細</span></template>
      <el-table :data="report.salesData" style="width:100%">
        <el-table-column prop="courseName" label="課程名稱" />
        <el-table-column prop="salesCount" label="銷售數量" width="100" />
        <el-table-column label="銷售金額" width="140">
          <template #default="{ row }">NT$ {{ formatPrice(row.totalRevenue) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api/admin'
import { ElMessage } from 'element-plus'
import { Chart, registerables } from 'chart.js'

Chart.register(...registerables)

const loading = ref(false)
const report = ref({ totalRevenue: 0, totalOrders: 0, salesData: [], categoryData: [] })
const salesChartRef = ref(null)
const categoryChartRef = ref(null)

onMounted(async () => {
  loading.value = true
  try {
    const res = await adminApi.getReport()
    report.value = res.data
    renderCharts()
  } catch { ElMessage.error('載入報表失敗') }
  finally { loading.value = false }
})

function renderCharts() {
  const sales = report.value.salesData?.slice(0, 10) || []
  const cats = report.value.categoryData || []

  if (salesChartRef.value) {
    new Chart(salesChartRef.value, {
      type: 'bar',
      data: {
        labels: sales.map(s => s.courseName?.slice(0, 8) || ''),
        datasets: [{
          label: '銷售量',
          data: sales.map(s => s.salesCount || 0),
          backgroundColor: '#409eff88',
          borderColor: '#409eff',
          borderWidth: 1,
        }],
      },
      options: { responsive: true, plugins: { legend: { display: false } } },
    })
  }

  if (categoryChartRef.value && cats.length > 0) {
    new Chart(categoryChartRef.value, {
      type: 'doughnut',
      data: {
        labels: cats.map(c => c.categoryName || ''),
        datasets: [{
          data: cats.map(c => c.userCount || 0),
          backgroundColor: ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399'],
        }],
      },
      options: { responsive: true },
    })
  }
}

function formatPrice(p) { return p?.toLocaleString('zh-TW') ?? '0' }
</script>

<style scoped>
.reports-page { padding: 0; }
.stat-card { text-align: center; }
.stat-value { font-size: 28px; font-weight: bold; color: #409eff; margin-bottom: 4px; }
.stat-label { font-size: 14px; color: #909399; }
</style>
