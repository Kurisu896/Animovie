<template>
  <el-container style="height:100vh">
    <!-- 顶部栏 -->
    <el-header class="modern-header">
      <div class="header-inner">
        <!-- 左侧 Logo（点击回首页） -->
        <div class="logo-wrap" @click="$router.push('/')">
          <h2 class="logo">Animovie・漫影集</h2>
        </div>

        <!-- 中间导航（登录后显示） -->
        <nav class="nav-center" v-if="token">
          <ul class="nav-list">
            <li><router-link to="/subject">影视条目</router-link></li>
            <li><router-link to="/forum">论坛</router-link></li>
            <li><router-link to="/timeline">全站动态</router-link></li>
            <li><router-link to="/rank">排行榜</router-link></li>
            <li><router-link to="/search">搜索</router-link></li>
          </ul>
        </nav>

        <!-- 右侧用户区 -->
        <div class="user-area">
          <el-button 
            circle 
            class="theme-toggle-btn" 
            @click="toggleDarkMode"
          >
            <el-icon :size="18">
              <component :is="isDark ? 'Sunny' : 'Moon'" />
            </el-icon>
          </el-button>

          <template v-if="token">
            <el-dropdown trigger="hover">
              <div class="avatar-wrap">
                <img
                  v-if="me.avatarPath"
                  :src="base + me.avatarPath"
                  alt="avatar"
                  class="avatar"
                />
                <span class="nickname">{{ me.nickname || '用户' }}</span>
                <el-icon class="arrow"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="go('/user/' + me.id)">我的主页</el-dropdown-item>
                  <el-dropdown-item @click="go('/profile')">个人资料</el-dropdown-item>
                  <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>

          <template v-else>
            <el-button type="primary" round class="nav-auth-btn" @click="go('/login')">登录</el-button>
            <el-button round class="nav-auth-btn register-btn" @click="go('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>

    <el-main style="padding: 0">
      <router-view :key="$route.fullPath" />
      <div class="assistant-fab" @click="assistantVisible=true" aria-label="AI 助手">
        <div class="assistant-label">AI 助手</div>
        <div class="brand">
          <div class="brand-inner">
            <el-icon :size="24"><ChatLineRound /></el-icon>
          </div>
          <div class="brand-pulse"></div>
        </div>
      </div>
      <AIAssistant v-model="assistantVisible" />
    </el-main>
  </el-container>
</template>

<script>
import { useAuthStore } from './store'
import { get, BASE } from './utils/api'
import { ArrowDown, ChatLineRound, Moon, Sunny } from '@element-plus/icons-vue'
import AIAssistant from './components/AIAssistant.vue'

export default {
  components: { ArrowDown, ChatLineRound, Moon, Sunny, AIAssistant },
  setup() {
    const auth = useAuthStore()
    return { auth }
  },
  data() {
    return { 
      me: { id: null, avatarPath: '', signature: '', nickname: '' }, 
      base: BASE,
      assistantVisible: false,
      isDark: false
    }
  },
  computed: {
    token() { return this.auth.token },
  },
  methods: {
    go(i) { this.$router.push(i) },
    logout() {
      this.auth.clear()
      this.$router.push('/')
      this.me = { id: null, avatarPath: '', signature: '', nickname: '' }
    },
    toggleDarkMode() {
      this.isDark = !this.isDark
      this.applyDarkMode()
    },
    applyDarkMode() {
      if (this.isDark) {
        document.documentElement.classList.add('dark')
        localStorage.setItem('theme', 'dark')
      } else {
        document.documentElement.classList.remove('dark')
        localStorage.setItem('theme', 'light')
      }
    },
    async loadMe() {
      if (this.token) {
        const r = await get('/auth/me')
        if (r.code === 0) {
          this.me = { ...this.me, ...r.data }
        }
      }
    }
  },
  async mounted() {
    this.isDark = localStorage.getItem('theme') === 'dark'
    this.applyDarkMode()
    this.loadMe()
  },
  watch: {
    token(val) {
      if (val) this.loadMe()
      else this.me = { id: null, avatarPath: '', signature: '', nickname: '' }
    }
  }
}
</script>

<style scoped>
.modern-header {
  height: 70px;
  padding: 0 32px;
  background: var(--header-bg);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--border-color);
  position: sticky;
  top: 0;
  z-index: 1000;
}

/* 核心：flex 布局实现左-中-右 + 居中 */
.header-inner {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
}

/* 左侧 Logo */
.logo-wrap {
  cursor: pointer;
  transition: opacity 0.2s;
}
.logo-wrap:hover { opacity: 0.8; }
.logo {
  margin: 0;
  font-size: 24px;
  font-weight: 800;
  color: var(--text-main);
  letter-spacing: -0.5px;
}

/* 中间自定义导航（彻底去掉 el-menu 的底横线） */
.nav-center {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}
.nav-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  gap: 36px;
}
.nav-list li a {
  color: var(--text-secondary);
  text-decoration: none;
  font-weight: 600;
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 10px;
  position: relative;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
}

.nav-list li a:hover {
  color: var(--primary-color);
  background: rgba(99, 102, 241, 0.08);
}

.nav-list li a.router-link-active {
  color: #fff !important;
  background: var(--primary-color);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.35);
}

/* 移除旧的下划线样式 */
.nav-list li a::after {
  display: none;
}

html.dark .nav-list li a:hover {
  background: rgba(129, 140, 248, 0.15);
}

html.dark .nav-list li a.router-link-active {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.4);
}

/* 右侧用户区 */
.user-area {
  display: flex;
  align-items: center;
  gap: 16px;
}
.nav-auth-btn {
  padding: 8px 20px;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s;
  height: 36px;
}
.register-btn {
  border: 1.5px solid #e2e8f0;
  color: #475569;
}
.register-btn:hover {
  border-color: #6366f1;
  color: #6366f1;
  background: rgba(99, 102, 241, 0.05);
}
.avatar-wrap {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 12px;
  transition: background 0.25s;
}
.avatar-wrap:hover { background: var(--border-color); }
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--border-color);
  margin-right: 10px;
}
.nickname {
  color: var(--text-main);
  font-weight: 600;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.arrow { color: #94a3b8; margin-left: 4px; }

/* 响应式：小屏隐藏中间导航文字，只留Logo和头像 */
@media (max-width: 960px) {
  .nav-list { gap: 20px; }
  .nav-list li a { font-size: 14px; }
}
@media (max-width: 640px) {
  .modern-header { padding: 0 16px; }
  .nav-center { display: none; }   /* 超小屏隐藏中间导航 */
  .nickname { display: none; }
}

/* AI 助手悬浮球样式优化 */
.assistant-fab {
  position: fixed;
  right: 32px;
  bottom: 32px;
  z-index: 2000;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  pointer-events: auto;
}

.assistant-label {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  padding: 8px 16px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 700;
  color: #4f46e5;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  opacity: 0;
  transform: translateX(10px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  pointer-events: none;
  white-space: nowrap;
  border: 1px solid rgba(79, 70, 229, 0.1);
}

.assistant-fab:hover .assistant-label {
  opacity: 1;
  transform: translateX(0);
}

.brand {
  position: relative;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.brand-inner {
  width: 100%;
  height: 100%;
  border-radius: 20px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #d946ef 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.4);
  position: relative;
  z-index: 2;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.brand-pulse {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 20px;
  background: #6366f1;
  opacity: 0.3;
  z-index: 1;
  animation: brand-pulse 2s infinite;
}

@keyframes brand-pulse {
  0% { transform: scale(1); opacity: 0.4; }
  100% { transform: scale(1.4); opacity: 0; }
}

.assistant-fab:hover .brand-inner {
  transform: translateY(-5px) scale(1.05) rotate(5deg);
  border-radius: 16px;
  box-shadow: 0 12px 32px rgba(99, 102, 241, 0.5);
}

.assistant-fab:active .brand-inner {
  transform: scale(0.95);
}

@media (max-width: 640px) {
  .assistant-fab {
    right: 20px;
    bottom: 20px;
  }
  .brand {
    width: 52px;
    height: 52px;
  }
  .assistant-label {
    display: none;
  }
}
</style>
<style>
/* 全局样式统一 */
:root {
  --primary-color: #6366f1;
  --bg-color: #f8fafc;
  --header-bg: rgba(255, 255, 255, 0.88);
  --card-bg: #ffffff;
  --text-main: #1e293b;
  --text-secondary: #475569;
  --border-color: rgba(0, 0, 0, 0.06);
  --card-shadow: 0 8px 16px rgba(0, 0, 0, 0.05);
  --border-radius: 16px;
}

:root.dark {
  --primary-color: #818cf8;
  --bg-color: #0b0f1a;
  --header-bg: rgba(11, 15, 26, 0.8);
  --card-bg: #161b2a;
  --text-main: #f8fafc;
  --text-secondary: #94a3b8;
  --border-color: rgba(255, 255, 255, 0.08);
  --card-shadow: 0 12px 32px rgba(0, 0, 0, 0.5);
}

/* Element Plus 暗黑模式全局覆盖 */
html.dark {
  --el-bg-color: var(--bg-color);
  --el-bg-color-overlay: var(--card-bg);
  --el-text-color-primary: var(--text-main);
  --el-text-color-regular: var(--text-secondary);
  --el-border-color: var(--border-color);
  --el-fill-color-blank: var(--card-bg);
  --el-fill-color: rgba(255, 255, 255, 0.05);
  --el-mask-color: rgba(0, 0, 0, 0.8);
}

html.dark .el-select__wrapper,
html.dark .el-input__wrapper,
html.dark .el-textarea__inner {
  background-color: var(--card-bg) !important;
  box-shadow: 0 0 0 1px var(--border-color) inset !important;
  color: var(--text-main) !important;
}

html.dark .el-select__wrapper:hover,
html.dark .el-input__wrapper:hover {
  box-shadow: 0 0 0 1px var(--primary-color) inset !important;
}

html.dark .el-tooltip__trigger {
  background-color: transparent !important;
}

html.dark .el-tooltip__trigger.el-button:not(.el-button--primary) {
  background-color: var(--card-bg) !important;
  border-color: var(--border-color) !important;
  color: var(--text-main) !important;
}

/* 按钮在暗黑模式下的样式适配 */
html.dark .el-button:not(.el-button--primary):not(.el-button--danger):not(.el-button--success):not(.el-button--warning):not(.el-button--info) {
  background-color: var(--card-bg);
  border-color: var(--border-color);
  color: var(--text-main);
}

html.dark .el-button:not(.el-button--primary):not(.el-button--danger):hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
  background-color: rgba(129, 140, 248, 0.05);
}

html.dark .el-dropdown-menu {
  background-color: var(--card-bg);
  border: 1px solid var(--border-color);
  box-shadow: var(--card-shadow);
}

html.dark .el-dropdown-menu__item {
  color: var(--text-main);
}

html.dark .el-dropdown-menu__item:hover {
  background-color: rgba(255, 255, 255, 0.05);
  color: var(--primary-color);
}

body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: var(--bg-color);
  color: var(--text-main);
  transition: background-color 0.3s, color 0.3s;
}

/* 顶部导航栏背景 */
.modern-header {
  height: 70px;
  padding: 0 32px;
  background: var(--header-bg);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid var(--border-color);
  position: sticky;
  top: 0;
  z-index: 1000;
}

/* 顶部文字颜色适配 */
.logo {
  margin: 0;
  font-size: 24px;
  font-weight: 800;
  color: var(--text-main);
  letter-spacing: -0.5px;
}

.nav-list li a {
  color: var(--text-secondary);
  text-decoration: none;
  font-weight: 500;
  font-size: 15px;
  padding: 8px 0;
  position: relative;
  transition: all 0.3s;
}

.nickname {
  color: var(--text-main);
  font-weight: 500;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 暗黑模式切换按钮 */
.theme-toggle-btn {
  background: transparent !important;
  border: 1.5px solid var(--border-color) !important;
  color: var(--text-secondary) !important;
  transition: all 0.3s !important;
}

.theme-toggle-btn:hover {
  border-color: var(--primary-color) !important;
  color: var(--primary-color) !important;
  background: rgba(99, 102, 241, 0.05) !important;
}

/* 页面通用容器 */
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
  min-height: calc(100vh - 70px);
  box-sizing: border-box;
}

/* 专门为登录注册页准备的居中容器 */
.auth-page-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 70px);
  padding: 20px;
  box-sizing: border-box;
}

/* 统一卡片风格 */
.modern-card {
  border-radius: var(--border-radius) !important;
  border: none !important;
  background-color: var(--card-bg) !important;
  color: var(--text-main) !important;
  box-shadow: var(--card-shadow) !important;
  transition: transform 0.3s, box-shadow 0.3s, background-color 0.3s !important;
}

.modern-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08) !important;
}

/* 统一弹窗风格 */
.modern-dialog {
  border-radius: 20px !important;
  overflow: hidden;
  background: var(--card-bg) !important;
}

.modern-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 24px 24px 16px;
  border-bottom: 1px solid var(--border-color);
}

.modern-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-main);
}

.modern-dialog :deep(.el-dialog__body) {
  padding: 24px;
  color: var(--text-main);
}

.modern-dialog :deep(.el-dialog__footer) {
  padding: 16px 24px 24px;
  border-top: 1px solid var(--border-color);
}

.status-radio-group {
  display: flex;
  width: 100%;
}

.status-radio-group :deep(.el-radio-button) {
  flex: 1;
}

.status-radio-group :deep(.el-radio-button__inner) {
  width: 100%;
  border-radius: 10px !important;
  border: 1.5px solid var(--border-color) !important;
  background-color: var(--card-bg) !important;
  color: var(--text-secondary) !important;
  margin: 0 4px;
  transition: all 0.3s;
}

.status-radio-group :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background-color: var(--primary-color) !important;
  border-color: var(--primary-color) !important;
  color: #ffffff !important;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3) !important;
}

.rate-wrapper {
  padding: 8px 0;
}

.dialog-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.submit-btn {
  padding-left: 30px;
  padding-right: 30px;
}

/* 弹窗内的表单美化 */
.dialog-content :deep(.el-form-item__label) {
  font-weight: 600 !important;
  color: var(--text-secondary) !important;
  padding-bottom: 8px !important;
}

.dialog-content :deep(.el-input__wrapper),
.dialog-content :deep(.el-textarea__inner) {
  border-radius: 10px !important;
  background-color: var(--bg-color) !important;
  box-shadow: 0 0 0 1px var(--border-color) inset !important;
  color: var(--text-main) !important;
  transition: all 0.2s !important;
}

.dialog-content :deep(.el-input__inner),
.dialog-content :deep(.el-textarea__inner) {
  color: var(--text-main) !important;
}

.dialog-content :deep(.el-input__wrapper:hover),
.dialog-content :deep(.el-textarea__inner:hover) {
  box-shadow: 0 0 0 1px var(--text-secondary) inset !important;
}

.dialog-content :deep(.el-input__wrapper.is-focus),
.dialog-content :deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1.5px var(--primary-color) inset !important;
}

/* 弹窗内的小标题 */
.dialog-section-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-main);
  margin: 24px 0 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.dialog-section-title::before {
  content: '';
  width: 3px;
  height: 14px;
  background: var(--primary-color);
  border-radius: 2px;
}

.dialog-section-title:first-child {
  margin-top: 0;
}

.modern-upload :deep(.el-upload--picture-card) {
  border-radius: 12px;
  border: 2px dashed var(--border-color);
  background-color: var(--bg-color);
  transition: all 0.3s;
}

.modern-upload :deep(.el-upload--picture-card:hover) {
  border-color: var(--primary-color);
  background-color: rgba(99, 102, 241, 0.05);
}

/* 标题样式 */
.page-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0;
  color: var(--text-main);
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title::before {
  content: '';
  width: 4px;
  height: 20px;
  background: var(--primary-color);
  border-radius: 2px;
}

/* AI 助手小球在暗黑模式下的阴影 */
:root.dark .brand-inner {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.5);
}

:root.dark .assistant-label {
  background: rgba(30, 41, 59, 0.95);
  color: #818cf8;
  border-color: rgba(129, 140, 248, 0.1);
}

</style>
