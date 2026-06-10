<template>
  <div class="design-brief">
    <div class="brief-hero">
      <h2>📋 设计简报</h2>
      <p>基于您之前分析结果生成的完整设计简报，可作为与设计师沟通的参考文档</p>
    </div>

    <div v-if="!store.analyzeResult" class="no-data">
      <div class="no-data-icon">📝</div>
      <h3>暂无设计简报</h3>
      <p>请先在「风格分析」页面完成一次空间分析，系统将自动生成设计简报</p>
      <el-button type="primary" size="large" @click="$emit('switchTab', 'analyze')">
        前往风格分析
      </el-button>
    </div>

    <div v-else class="brief-content">
      <div class="brief-card">
        <div class="brief-section">
          <h3>📌 项目概况</h3>
          <div class="brief-meta">
            <div class="meta-item">
              <span class="meta-label">推荐风格</span>
              <span class="meta-value highlight">{{ store.analyzeResult.recommendedStyle }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">风格定位</span>
              <span class="meta-value">{{ store.analyzeResult.styleDescription }}</span>
            </div>
          </div>
        </div>

        <div class="brief-section">
          <h3>🎨 色彩设计</h3>
          <p>{{ store.analyzeResult.colorScheme }}</p>
        </div>

        <div class="brief-section">
          <h3>🪑 家具选型</h3>
          <p>{{ store.analyzeResult.furnitureGuide }}</p>
        </div>

        <div class="brief-section">
          <h3>✨ 软装搭配</h3>
          <p>{{ store.analyzeResult.decorTips }}</p>
        </div>

        <div v-if="store.analyzeResult.alternativeStyles?.length" class="brief-section">
          <h3>🔄 备选方案</h3>
          <el-table :data="store.analyzeResult.alternativeStyles" size="small">
            <el-table-column prop="styleName" label="风格" width="120" />
            <el-table-column label="匹配度" width="100">
              <template #default="{ row }">
                <el-progress :percentage="row.matchScore" :stroke-width="8" :show-text="true" />
              </template>
            </el-table-column>
            <el-table-column prop="reason" label="推荐理由" />
          </el-table>
        </div>

        <div class="brief-footer">
          <el-button type="primary" @click="printBrief">🖨️ 打印简报</el-button>
          <span class="brief-disclaimer">* 设计建议由 AI 生成，仅供参考</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useDesignStore } from '@/stores/design'

const store = useDesignStore()

const emit = defineEmits<{
  switchTab: [tab: string]
}>()

function printBrief() {
  window.print()
}
</script>

<style scoped>
.design-brief { max-width: 800px; }

.brief-hero {
  margin-bottom: 20px;
}

.brief-hero h2 { font-size: 20px; margin-bottom: 4px; }
.brief-hero p { font-size: 13px; color: var(--text-secondary); }

.no-data {
  background: var(--bg-card);
  border-radius: 12px;
  padding: 64px 24px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(60, 36, 21, 0.06);
}

.no-data-icon { font-size: 56px; margin-bottom: 12px; }
.no-data h3 { font-size: 17px; margin-bottom: 6px; }
.no-data p { font-size: 13px; color: var(--text-secondary); margin-bottom: 20px; }

.brief-content {
  background: var(--bg-card);
  border-radius: 12px;
  padding: 28px;
  box-shadow: 0 2px 12px rgba(60, 36, 21, 0.06);
}

.brief-section {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border);
}
.brief-section:last-of-type { border-bottom: none; }

.brief-section h3 {
  font-size: 15px;
  margin-bottom: 10px;
  color: var(--brand);
}

.brief-section p {
  font-size: 13px;
  line-height: 1.7;
  color: var(--text-primary);
}

.brief-meta {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.meta-label {
  font-size: 12px;
  color: var(--text-secondary);
}

.meta-value {
  font-size: 14px;
  font-weight: 500;
}

.meta-value.highlight {
  color: var(--brand);
  font-size: 20px;
  font-weight: 700;
}

.brief-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid var(--border);
}

.brief-disclaimer {
  font-size: 11px;
  color: var(--text-secondary);
}

@media print {
  .app-header, .main-tabs, .brief-footer { display: none; }
  .brief-section { break-inside: avoid; }
}
</style>
