<template>
  <div class="not-found">
    <el-result icon="404" title="404" sub-title="找不到此頁面">
      <template #extra>
        <el-button type="primary" @click="$router.push('/courses')">回到首頁</el-button>
      </template>
    </el-result>
  </div>
</template>

<style scoped>
.not-found { min-height: 100vh; display: flex; align-items: center; justify-content: center; }
</style>
