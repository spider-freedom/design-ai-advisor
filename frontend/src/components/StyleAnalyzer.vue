<template>
  <div class="style-analyzer">
    <!-- Input Panel -->
    <div class="analyzer-input">
      <h2>描述你的空间，AI 为你推荐设计风格</h2>
      <p class="subtitle">输入房间类型、面积、偏好，获取专业风格分析与配色方案</p>

      <div class="form-grid">
        <div class="form-item full-width">
          <label>房间描述 <span class="required">*</span></label>
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="例如：我需要设计一间 25 平米的客厅，希望有温馨舒适的感觉，平时喜欢看书和招待朋友..."
          />
        </div>

        <div class="form-item">
          <label>空间类型</label>
          <el-select v-model="form.roomType" style="width: 100%">
            <el-option label="客厅" value="living_room" />
            <el-option label="卧室" value="bedroom" />
            <el-option label="厨房" value="kitchen" />
            <el-option label="浴室" value="bathroom" />
            <el-option label="书房" value="study" />
            <el-option label="阳台" value="balcony" />
            <el-option label="全屋" value="full_house" />
          </el-select>
        </div>

        <div class="form-item">
          <label>偏好风格（可选）</label>
          <el-select v-model="form.preferredStyle" style="width: 100%" clearable placeholder="让 AI 自动推荐">
            <el-option label="现代简约" value="现代简约" />
            <el-option label="北欧风" value="北欧风" />
            <el-option label="新中式" value="新中式" />
            <el-option label="工业风" value="工业风" />
            <el-option label="日式侘寂" value="日式侘寂" />
            <el-option label="轻奢风" value="轻奢风" />
            <el-option label="地中海风" value="地中海风" />
            <el-option label="美式乡村" value="美式乡村" />
            <el-option label="极简主义" value="极简主义" />
            <el-option label="奶油风" value="奶油风" />
            <el-option label="原木风" value="原木风" />
          </el-select>
        </div>

        <div class="form-item">
          <label>预算水平</label>
          <el-select v-model="form.budget" style="width: 100%">
            <el-option label="经济型（简约实用）" value="economy" />
            <el-option label="标准型（品质舒适）" value="standard" />
            <el-option label="高端型（精致设计）" value="premium" />
            <el-option label="奢华型（顶级配置）" value="luxury" />
          </el-select>
        </div>

        <div class="form-item">
          <label>面积（平方米）</label>
          <el-input-number v-model="form.area" :min="10" :max="500" style="width: 100%" />
        </div>
      </div>

      <el-button
        type="primary"
        size="large"
        class="analyze-btn"
        :loading="store.analyzeStatus === 'loading'"
        :disabled="!form.description.trim()"
        @click="handleAnalyze"
      >
        {{ store.analyzeStatus === 'loading' ? 'AI 正在分析...' : '🔍 开始分析' }}
      </el-button>
    </div>

    <!-- Loading -->
    <div v-if="store.analyzeStatus === 'loading'" class="loading-card">
      <div class="loading-pulse">
        <span></span><span></span><span></span>
      </div>
      <p>AI 正在分析你的空间需求，匹配合适的设计风格...</p>
    </div>

    <!-- Error -->
    <div v-if="store.analyzeStatus === 'error'" class="error-card">
      <p>❌ {{ store.analyzeError }}</p>
      <el-button size="small" @click="handleAnalyze">重新分析</el-button>
    </div>

    <!-- Results -->
    <div v-if="store.analyzeStatus === 'done' && store.analyzeResult" class="result-card">
      <div class="result-header">
        <div class="result-style">
          <span class="result-badge">{{ store.analyzeResult.recommendedStyle }}</span>
          <span class="result-label">推荐风格</span>
        </div>
      </div>

      <p class="result-desc">{{ store.analyzeResult.styleDescription }}</p>

      <div class="result-grid">
        <div class="result-block">
          <h4>🎨 配色方案</h4>
          <p>{{ store.analyzeResult.colorScheme }}</p>
        </div>
        <div class="result-block">
          <h4>🪑 家具指南</h4>
          <p>{{ store.analyzeResult.furnitureGuide }}</p>
        </div>
        <div class="result-block full-width">
          <h4>✨ 装饰搭配</h4>
          <p>{{ store.analyzeResult.decorTips }}</p>
        </div>
      </div>

      <!-- Alternative Styles -->
      <div v-if="store.analyzeResult.alternativeStyles?.length" class="alternatives">
        <h4>备选风格方案</h4>
        <div class="alt-grid">
          <div
            v-for="alt in store.analyzeResult.alternativeStyles"
            :key="alt.styleName"
            class="alt-card"
          >
            <div class="alt-header">
              <span class="alt-name">{{ alt.styleName }}</span>
              <el-progress :percentage="alt.matchScore" :stroke-width="6" :show-text="false" />
              <span class="alt-score">{{ alt.matchScore }}%</span>
            </div>
            <p class="alt-reason">{{ alt.reason }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useDesignStore } from '@/stores/design'
import { analyzeDesign } from '@/api/design'

const store = useDesignStore()

const form = reactive({
  description: '',
  roomType: 'living_room',
  preferredStyle: '',
  budget: 'standard',
  area: undefined as number | undefined,
})

async function handleAnalyze() {
  if (!form.description.trim()) return
  store.setAnalyzeLoading()
  try {
    const result = await analyzeDesign({
      description: form.description,
      roomType: form.roomType,
      preferredStyle: form.preferredStyle || undefined,
      budget: form.budget,
      area: form.area,
    })
    store.setAnalyzeResult(result)
  } catch (e: any) {
    store.setAnalyzeError(e.response?.data?.message || e.message || '分析失败，请重试')
  }
}
</script>

<style scoped>
.style-analyzer { max-width: 900px; }

.analyzer-input {
  background: var(--bg-card);
  border-radius: 12px;
  padding: 28px;
  box-shadow: 0 2px 12px rgba(60, 36, 21, 0.06);
  margin-bottom: 20px;
}

.analyzer-input h2 {
  font-size: 20px;
  margin-bottom: 6px;
  color: var(--text-primary);
}

.subtitle {
  color: var(--text-secondary);
  font-size: 13px;
  margin-bottom: 20px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 20px;
}

.form-item { display: flex; flex-direction: column; gap: 6px; }
.form-item.full-width { grid-column: 1 / -1; }
.form-item label { font-size: 13px; font-weight: 500; color: var(--text-primary); }
.required { color: var(--error); }

.analyze-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
  background: var(--brand) !important;
  border-color: var(--brand) !important;
}
.analyze-btn:hover { background: #6B4530 !important; }

/* Loading */
.loading-card {
  background: var(--bg-card);
  border-radius: 12px;
  padding: 48px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(60, 36, 21, 0.06);
}

.loading-pulse {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 16px;
}
.loading-pulse span {
  width: 10px; height: 10px;
  background: var(--brand);
  border-radius: 50%;
  animation: pulse 1.4s infinite;
}
.loading-pulse span:nth-child(2) { animation-delay: 0.2s; }
.loading-pulse span:nth-child(3) { animation-delay: 0.4s; }
@keyframes pulse {
  0%, 60%, 100% { opacity: 0.3; transform: scale(0.8); }
  30% { opacity: 1; transform: scale(1.2); }
}

/* Error */
.error-card {
  background: #FEF2F2;
  border: 1px solid #FECACA;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  color: var(--error);
}

/* Results */
.result-card {
  background: var(--bg-card);
  border-radius: 12px;
  padding: 28px;
  box-shadow: 0 2px 12px rgba(60, 36, 21, 0.06);
}

.result-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.result-style {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.result-badge {
  display: inline-block;
  background: linear-gradient(135deg, var(--brand), var(--accent));
  color: #fff;
  padding: 8px 24px;
  border-radius: 20px;
  font-size: 18px;
  font-weight: 700;
}

.result-label { font-size: 12px; color: var(--text-secondary); margin-top: 4px; }

.result-desc {
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: 20px;
}

.result-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.result-block {
  background: #FBF7F4;
  border-radius: 8px;
  padding: 16px;
}
.result-block.full-width { grid-column: 1 / -1; }
.result-block h4 { font-size: 14px; margin-bottom: 6px; color: var(--brand); }
.result-block p { font-size: 13px; line-height: 1.6; color: var(--text-primary); }

/* Alternatives */
.alternatives {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--border);
}

.alternatives h4 {
  font-size: 14px;
  margin-bottom: 12px;
  color: var(--text-primary);
}

.alt-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.alt-card {
  background: #FBF7F4;
  border-radius: 8px;
  padding: 14px;
}

.alt-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.alt-name { font-weight: 600; font-size: 14px; white-space: nowrap; }
.alt-score { font-size: 12px; color: var(--text-secondary); font-weight: 500; }

.alt-reason {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.5;
}

@media (max-width: 768px) {
  .form-grid, .result-grid, .alt-grid { grid-template-columns: 1fr; }
}
</style>
