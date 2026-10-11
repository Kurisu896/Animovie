<!-- 页面：首页 -->
<template>
  <div class="home-wrapper">
    <div class="home-container">
      <!-- 未登录视图 -->
      <div class="hero-section" v-if="!token">
      <div class="hero-content">
        <h1 class="hero-title">
          <span class="text-gradient">Animovie・漫影集</span>
          <br />记录你的影视日常
        </h1>
        <p class="hero-subtitle">
          探索海量条目、参与热门讨论、追踪观看进度、与同好分享感受。
        </p>
        <div class="hero-actions">
          <el-button type="primary" size="large" class="action-btn" round @click="$router.push('/login')">立即登录</el-button>
          <el-button size="large" class="action-btn" round @click="$router.push('/register')">注册账号</el-button>
        </div>
      </div>
      
      <div class="features-section">
        <el-row :gutter="24">
          <el-col :xs="24" :sm="8" v-for="(feature, index) in features" :key="index">
            <el-card class="feature-card" shadow="hover">
              <el-icon class="feature-icon" :size="40" :color="feature.color">
                <component :is="feature.icon" />
              </el-icon>
              <h3 class="feature-title">{{ feature.title }}</h3>
              <p class="feature-desc">{{ feature.desc }}</p>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 已登录视图 -->
    <div class="dashboard-section" v-else>
      <el-card class="welcome-card" shadow="never">
        <div class="welcome-header">
          <el-avatar :size="64" class="welcome-avatar" :src="avatarPath ? (base + avatarPath) : ''">
            <span v-if="!avatarPath">{{ (nickname || 'User').charAt(0).toUpperCase() }}</span>
          </el-avatar>
          <div class="welcome-text">
            <h2 class="welcome-title">欢迎回来，{{ nickname || '用户' }}！</h2>
            <p class="welcome-subtitle">今天想看点什么？或者去社区逛逛？</p>
          </div>
        </div>
      </el-card>

      <h3 class="section-title">快速导航</h3>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" v-for="(nav, index) in navs" :key="index">
          <div class="nav-card-wrapper" @click="handleNav(nav)">
            <div class="nav-card-inner">
              <div class="nav-icon-box" :style="{ backgroundColor: nav.bgColor }">
                <el-icon :size="28" :color="nav.color">
                  <component :is="nav.icon" />
                </el-icon>
              </div>
              <div class="nav-info">
                <div class="nav-name">{{ nav.name }}</div>
                <div class="nav-desc">{{ nav.desc }}</div>
              </div>
              <el-icon class="nav-arrow" :size="16"><ArrowRight /></el-icon>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    </div>
  </div>
</template>

<script>
import { get, BASE } from '../utils/api'
import { useAuthStore } from '../store'
import { ArrowRight, User, VideoPlay, ChatDotRound, Trophy, ChatLineRound, Search, DataLine } from '@element-plus/icons-vue'

export default {
  components: { ArrowRight, User, VideoPlay, ChatDotRound, Trophy, ChatLineRound, Search, DataLine },
  setup(){ 
    const auth = useAuthStore(); 
    return { auth } 
  },
  data(){ 
    return { 
      base: BASE,
      userId: null,
      nickname:'',
      avatarPath:'',
      features: [
        { icon: 'VideoPlay', title: '条目探索', desc: '发现最新、最热的动画、漫画与游戏', color: '#409EFF' },
        { icon: 'ChatDotRound', title: '社区讨论', desc: '在论坛和时间线与同好畅所欲言', color: '#67C23A' },
        { icon: 'DataLine', title: '进度追踪', desc: '管理你的收藏，记录每一次观看进度', color: '#E6A23C' }
      ],
      navs: [
        { icon: 'User', name: '我的主页', desc: '查看个人收藏与动态', path: '', isDynamic: true, color: '#409EFF', bgColor: '#ecf5ff' },
        { icon: 'VideoPlay', name: '条目浏览', desc: '发现海量动漫与游戏', path: '/subject', color: '#F56C6C', bgColor: '#fef0f0' },
        { icon: 'ChatDotRound', name: '全站动态', desc: '看看大家都在追什么', path: '/timeline', color: '#E6A23C', bgColor: '#fdf6ec' },
        { icon: 'Trophy', name: '排行榜', desc: '权威的高分条目榜单', path: '/rank', color: '#67C23A', bgColor: '#f0f9eb' },
        { icon: 'ChatLineRound', name: '社区论坛', desc: '参与热门话题讨论', path: '/forum', color: '#409EFF', bgColor: '#ecf5ff' },
        { icon: 'Search', name: '全站搜索', desc: '快速查找所需内容', path: '/search', color: '#909399', bgColor: '#f4f4f5' }
      ]
    } 
  },
  computed:{ 
    token(){ return this.auth.token } 
  },
  async mounted(){ 
    if(this.token){ 
      const r = await get('/auth/me'); 
      if(r.code===0){ 
        this.userId = r.data.id || null
        this.nickname = r.data.nickname || r.data.username || '' 
        this.avatarPath = r.data.avatarPath || ''
      } 
    } 
  },
  methods: {
    handleNav(nav) {
      if (nav.isDynamic) {
        this.$router.push('/user/' + (this.userId || ''))
      } else {
        this.$router.push(nav.path)
      }
    }
  }
}
</script>

<style scoped>
/* 预留整个页面的背景 URL */
.home-wrapper {
  /* background-image: url('../img/bg.jpg'); */
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
  min-height: calc(100vh - 70px);
}

.home-container {
  max-width: 1080px;
  margin: 0 auto;
  padding: 40px 20px;
}

/* === 未登录视图样式 === */
.hero-section {
  text-align: center;
  padding: 60px 0;
  animation: fadeIn 0.8s ease-out;
}

.hero-content {
  margin-bottom: 60px;
}

.hero-title {
  font-size: 48px;
  font-weight: 800;
  line-height: 1.2;
  margin: 0 0 20px;
  color: var(--text-main);
}

.text-gradient {
  background: linear-gradient(120deg, #409EFF, #67C23A);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 18px;
  color: var(--text-secondary);
  max-width: 600px;
  margin: 0 auto 40px;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.action-btn {
  width: 140px;
  font-weight: 600;
}

.features-section {
  margin-top: 40px;
}

.feature-card {
  text-align: center;
  padding: 30px 20px;
  border-radius: 16px;
  border: none;
  transition: transform 0.3s, box-shadow 0.3s;
  background: var(--card-bg);
  height: 100%;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--card-shadow) !important;
}

.feature-icon {
  margin-bottom: 20px;
}

.feature-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 12px;
  color: var(--text-main);
}

.feature-desc {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.5;
}

/* === 已登录视图样式 === */
.dashboard-section {
  animation: fadeIn 0.6s ease-out;
}

.welcome-card {
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(139, 92, 246, 0.1) 100%);
  border: 1px solid var(--border-color);
  margin-bottom: 40px;
}

.welcome-header {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 20px;
}

.welcome-avatar {
  background: var(--primary-color);
  color: white;
  font-size: 28px;
  font-weight: bold;
}

.welcome-title {
  font-size: 28px;
  font-weight: bold;
  margin: 0 0 8px;
  color: var(--text-main);
}

.welcome-subtitle {
  font-size: 16px;
  color: var(--text-secondary);
  margin: 0;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 20px;
  color: var(--text-main);
  padding-left: 12px;
  border-left: 4px solid var(--primary-color);
}

.nav-card-wrapper {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.nav-card-inner {
  display: flex;
  align-items: center;
  padding: 20px;
  background: var(--card-bg);
  border-radius: 16px;
  border: 1px solid var(--border-color);
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.nav-card-wrapper:hover .nav-card-inner {
  transform: translateY(-4px);
  box-shadow: var(--card-shadow);
  border-color: var(--primary-color);
}

.nav-icon-box {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  transition: transform 0.3s;
}

.nav-card-wrapper:hover .nav-icon-box {
  transform: scale(1.1) rotate(5deg);
}

.nav-info {
  flex: 1;
  min-width: 0;
}

.nav-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-main);
  margin-bottom: 4px;
}

.nav-desc {
  font-size: 13px;
  color: var(--text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.nav-arrow {
  color: #cbd5e1;
  opacity: 0;
  transform: translateX(-10px);
  transition: all 0.3s;
}

.nav-card-wrapper:hover .nav-arrow {
  opacity: 1;
  transform: translateX(0);
  color: var(--primary-color);
}

/* 动画 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 768px) {
  .hero-title { font-size: 36px; }
  .welcome-header { flex-direction: column; text-align: center; }
}
</style>