<template>
  <div class="app-shell">
    <header class="app-header">
      <div class="header-content">
        <div class="brand">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none">
            <rect width="24" height="24" rx="6" fill="#8B5E3C"/>
            <path d="M5 20L12 4L19 20H5Z" fill="#F5DEB3" opacity="0.9"/>
          </svg>
          <span class="brand-name">风格智选</span>
          <span class="brand-tag">AI Design Advisor</span>
        </div>
        <div class="header-desc">AI 驱动的室内设计风格分析助手</div>
      </div>
    </header>
    <main class="app-main">
      <HomeView />
    </main>
    <footer class="app-footer">
      <span>Powered by DeepSeek AI · 设计建议仅供参考</span>
    </footer>
  </div>
</template>

<script setup lang="ts">
import HomeView from '@/views/HomeView.vue'
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }

:root {
  --bg-warm: #FBF7F4;
  --bg-card: #FFFFFF;
  --text-primary: #3C2415;
  --text-secondary: #8B7355;
  --brand: #8B5E3C;
  --brand-light: #C4A882;
  --accent: #D4A76A;
  --border: #E8DDD4;
  --success: #6B8E7B;
  --error: #C4685A;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background: var(--bg-warm);
  color: var(--text-primary);
  -webkit-font-smoothing: antialiased;
}

.app-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background: linear-gradient(135deg, #3C2415 0%, #5C3D2E 50%, #8B5E3C 100%);
  color: #fff;
  padding: 24px 0;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-name {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.brand-tag {
  font-size: 12px;
  opacity: 0.7;
  background: rgba(255,255,255,0.15);
  padding: 2px 8px;
  border-radius: 10px;
}

.header-desc {
  font-size: 14px;
  opacity: 0.85;
}

.app-main {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 24px;
}

.app-footer {
  text-align: center;
  padding: 16px;
  color: var(--text-secondary);
  font-size: 12px;
  border-top: 1px solid var(--border);
}
</style>
