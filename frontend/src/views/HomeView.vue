<template>
  <div class="home-view">
    <el-tabs v-model="activeTab" class="main-tabs">
      <el-tab-pane label="🏠 风格分析" name="analyze" />
      <el-tab-pane label="💬 设计咨询" name="chat" />
      <el-tab-pane label="📋 设计简报" name="brief" />
    </el-tabs>

    <StyleAnalyzer v-if="activeTab === 'analyze'" />
    <DesignChat v-if="activeTab === 'chat'" />
    <DesignBrief v-if="activeTab === 'brief'" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import StyleAnalyzer from '@/components/StyleAnalyzer.vue'
import DesignChat from '@/components/DesignChat.vue'
import DesignBrief from '@/components/DesignBrief.vue'

const activeTab = ref('analyze')
</script>

<style scoped>
.main-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}
.main-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 500;
}
.main-tabs :deep(.el-tabs__active-bar) {
  background-color: var(--brand);
}
.main-tabs :deep(.el-tabs__item.is-active) {
  color: var(--brand);
}
</style>
