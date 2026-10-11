<template>
  <div>
    <el-drawer v-model="open" size="480px" direction="rtl" class="assistant-drawer" :with-header="false">
      <div class="assistant-container">
        <!-- 自定义头部 -->
        <div class="assistant-header">
          <div class="header-left">
            <div class="ai-badge">
              <el-icon :size="20"><Cpu /></el-icon>
            </div>
            <div class="header-text">
              <div class="header-title">AI 智能助手</div>
              <div class="header-status">
                <span class="status-dot"></span>
                在线
              </div>
            </div>
          </div>
          <div class="header-actions">
            <el-button link @click="clearMessages" icon="Refresh">清空</el-button>
            <el-button link @click="open = false" icon="Close" />
          </div>
        </div>

        <div class="chat-main">
          <!-- 消息列表 -->
          <div class="messages-scroll" ref="msgRef">
            <div class="welcome-section" v-if="messages.length === 0">
              <div class="welcome-tip">
                <div class="welcome-icon">
                  <el-icon :size="40"><ChatLineRound /></el-icon>
                </div>
                <h3>你好！我是 Bangumi 助手</h3>
                <p>我可以帮你查找条目、了解热门话题，或者回答关于二次元的问题。</p>
              </div>
              
              <div class="suggestions-grid">
                <div 
                  v-for="(s, idx) in suggestions" 
                  :key="idx" 
                  class="suggestion-item"
                  @click="useSuggestion(s)"
                >
                  <el-icon><MagicStick /></el-icon>
                  <span>{{ s }}</span>
                </div>
              </div>
            </div>

            <transition-group name="msg-fade">
              <div v-for="(m, i) in messages" :key="'msg-' + i" :class="['msg-item', m.role]">
                <!-- 头像 -->
                <div class="msg-avatar">
                  <div v-if="m.role === 'assistant'" class="ai-avatar">
                    <el-icon :size="16"><Cpu /></el-icon>
                  </div>
                  <el-avatar v-else :size="32" :src="userAvatar ? (base + userAvatar) : ''">
                    {{ nickname ? nickname.charAt(0).toUpperCase() : 'U' }}
                  </el-avatar>
                </div>

                <!-- 气泡 -->
                <div class="msg-bubble">
                  <div class="msg-text" v-html="formatContent(m.content)"></div>
                  
                  <!-- 关联内容 -->
                  <div v-if="m.role === 'assistant' && (m.subjects?.length || m.topics?.length)" class="msg-refs">
                    <!-- 关联条目 -->
                    <div v-if="m.subjects?.length" class="ref-section">
                      <div class="ref-section-title">
                        <el-icon><Collection /></el-icon>
                        相关条目
                      </div>
                      <div class="ref-grid">
                        <div class="mini-subject-card" v-for="s in m.subjects" :key="s.id" @click="goSubject(s.id)">
                          <div class="mini-cover">
                            <img v-if="s.coverPath" :src="base + s.coverPath" alt="cover" />
                            <div v-else class="placeholder">No Cover</div>
                          </div>
                          <div class="mini-info">
                            <div class="mini-name">{{ s.cnName || s.jpName }}</div>
                            <div class="mini-meta">
                              <span class="ref-tag">{{ s.category }}</span>
                              <span class="ref-score" v-if="s.score">
                                <el-icon><Star /></el-icon>
                                {{ s.score }}
                              </span>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>

                    <!-- 关联话题 -->
                    <div v-if="m.topics?.length" class="ref-section">
                      <div class="ref-section-title">
                        <el-icon><ChatDotRound /></el-icon>
                        相关讨论
                      </div>
                      <div class="ref-grid">
                        <div class="mini-topic-card" v-for="t in m.topics" :key="t.id" @click="goTopic(t.id)">
                          <div class="topic-icon">
                            <el-icon><ChatLineSquare /></el-icon>
                          </div>
                          <div class="topic-info">
                            <div class="topic-title">{{ t.title }}</div>
                            <div class="topic-meta">
                              <span>{{ t.replyCount || 0 }} 回复</span>
                              <span class="dot">·</span>
                              <span>{{ t.updatedAt }}</span>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </transition-group>

            <!-- 加载状态 -->
            <div class="msg-item assistant" v-if="loading">
              <div class="msg-avatar">
                <div class="ai-avatar">
                  <el-icon :size="16" class="is-loading"><Loading /></el-icon>
                </div>
              </div>
              <div class="msg-bubble thinking">
                <div class="typing-indicator">
                  <span></span><span></span><span></span>
                </div>
              </div>
            </div>
          </div>

          <!-- 输入框 -->
          <div class="input-area">
            <div class="input-container">
              <div class="input-wrapper">
                <el-input 
                  v-model="text" 
                  placeholder="在此输入你的问题..." 
                  @keyup.enter.exact="send"
                  :rows="1"
                  type="textarea"
                  autosize
                  class="modern-chat-input"
                />
                <div class="input-actions">
                  <el-tooltip content="按 Enter 发送，Shift+Enter 换行" placement="top">
                    <el-button 
                      type="primary" 
                      circle
                      icon="Promotion"
                      @click="send" 
                      :loading="loading" 
                      :disabled="!text.trim()"
                      class="send-btn"
                    />
                  </el-tooltip>
                </div>
              </div>
              <div class="input-footer">
                <span class="ai-model-tag">Bangumi AI 2.0</span>
                <span class="input-count">{{ text.length }}/500</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { post, BASE, get } from '../utils/api'
import { 
  Cpu, Refresh, Close, ChatLineRound, Loading, Promotion, 
  ChatLineSquare, MagicStick, Collection, ChatDotRound, Star 
} from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

export default {
  components: { 
    Cpu, Refresh, Close, ChatLineRound, Loading, Promotion, 
    ChatLineSquare, MagicStick, Collection, ChatDotRound, Star 
  },
  props: { modelValue: { type: Boolean, default: false } },
  emits: ['update:modelValue'],
  data() { 
    return { 
      open: this.modelValue, 
      text: '', 
      messages: [], 
      base: BASE, 
      loading: false,
      userAvatar: '',
      nickname: '',
      suggestions: [
        '最近有什么热门动漫？',
        '推荐几部高评分的电影',
        '帮我找找热门的讨论',
        '“Animovie・漫影集”是做什么的？'
      ]
    } 
  },
  watch: { 
    modelValue(v) { 
      this.open = v 
      if(v && !this.nickname) this.loadUserInfo()
    }, 
    open(v) { 
      this.$emit('update:modelValue', v) 
    } 
  },
  methods: {
    async loadUserInfo() {
      const r = await get('/auth/me')
      if(r.code === 0) {
        this.nickname = r.data.nickname || r.data.username
        this.userAvatar = r.data.avatarPath
      }
    },
    async clearMessages() {
      try {
        await ElMessageBox.confirm('确定要清空所有聊天记录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          roundButton: true
        })
        this.messages = []
      } catch (e) {
        // 取消清空
      }
    },
    useSuggestion(s) {
      this.text = s
      this.send()
    },
    formatContent(content) {
      if (!content) return ''
      // 简单的格式化：换行符转为 <br>，加粗转为 <strong>
      return content
        .replace(/\n/g, '<br>')
        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    },
    async send() { 
      const t = (this.text || '').trim(); 
      if (!t || this.loading) return; 
      
      this.messages.push({ role: 'user', content: t }); 
      this.text = ''; 
      this.loading = true; 
      
      this.$nextTick(() => this.scrollToBottom());
      
      try { 
        const r = await post('/ai/chat', { message: t }); 
        if (r.code === 0) { 
          const d = r.data || {}; 
          this.messages.push({ 
            role: 'assistant', 
            content: d.text || '我正在思考中...', 
            subjects: d.subjects || [], 
            topics: d.topics || [] 
          }); 
        } else { 
          this.messages.push({ role: 'assistant', content: r.msg || '抱歉，我现在有点忙，请稍后再试。' }); 
        } 
      } catch (e) { 
        this.messages.push({ role: 'assistant', content: '连接服务器失败，请检查网络设置。' }); 
      } finally { 
        this.loading = false; 
        this.$nextTick(() => this.scrollToBottom());
      } 
    },
    scrollToBottom() {
      const el = this.$refs.msgRef; 
      if (el) el.scrollTop = el.scrollHeight; 
    },
    goSubject(id) { 
      if (!id) return; 
      this.open = false; 
      this.$router.push('/subject/' + id); 
    },
    goTopic(id) { 
      if (!id) return; 
      this.open = false; 
      this.$router.push('/discussion/topic/' + id); 
    }
  }
}
</script>

<style scoped>
/* 针对 el-drawer 的穿透样式，解决暗黑模式下的背景和边框问题 */
:deep(.el-drawer) {
  background-color: var(--card-bg) !important;
  box-shadow: -10px 0 30px rgba(0, 0, 0, 0.2);
}

.assistant-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: var(--bg-color);
}

/* 头部样式 */
.assistant-header {
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--border-color);
  background: var(--header-bg);
  backdrop-filter: blur(10px);
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-badge {
  width: 38px;
  height: 38px;
  border-radius: 12px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.header-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-main);
  letter-spacing: -0.01em;
}

.header-status {
  font-size: 11px;
  color: #10b981;
  display: flex;
  align-items: center;
  gap: 5px;
  margin-top: 1px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
  box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.2);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(0.9); }
  100% { opacity: 1; transform: scale(1); }
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 聊天区域 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  position: relative;
}

.messages-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 24px 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  scroll-behavior: smooth;
}

/* 欢迎界面 */
.welcome-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 10px;
}

.welcome-tip {
  text-align: center;
  margin-bottom: 32px;
  animation: fadeInDown 0.6s ease-out;
}

.welcome-icon {
  width: 72px;
  height: 72px;
  background: var(--bg-color);
  color: var(--primary-color);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  border: 1px solid var(--border-color);
}

.welcome-tip h3 {
  font-size: 20px;
  color: var(--text-main);
  margin-bottom: 12px;
}

.welcome-tip p {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  max-width: 300px;
  margin: 0 auto;
}

.suggestions-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
  width: 100%;
  max-width: 340px;
  animation: fadeInUp 0.8s ease-out;
}

.suggestion-item {
  padding: 12px 16px;
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  font-size: 13px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.suggestion-item:hover {
  border-color: #6366f1;
  background: #f5f7ff;
  color: #6366f1;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.08);
}

.suggestion-item .el-icon {
  color: #8b5cf6;
}

/* 消息动画 */
.msg-fade-enter-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}
.msg-fade-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

/* 消息项样式 */
.msg-item {
  display: flex;
  gap: 12px;
  max-width: 92%;
}

.msg-item.user {
  flex-direction: row-reverse;
  align-self: flex-end;
}

.msg-avatar {
  flex-shrink: 0;
  margin-top: 4px;
}

.ai-avatar {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.msg-bubble {
  padding: 14px 18px;
  border-radius: 20px;
  font-size: 14px;
  line-height: 1.65;
  position: relative;
  transition: all 0.2s;
}

.assistant .msg-bubble {
  background: var(--card-bg);
  color: var(--text-main);
  border-top-left-radius: 4px;
  border: 1px solid var(--border-color);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}

.user .msg-bubble {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  color: #fff;
  border-top-right-radius: 4px;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.2);
}

.msg-text {
  white-space: pre-wrap;
  word-break: break-word;
}

.msg-text :deep(strong) {
  font-weight: 700;
  color: inherit;
  opacity: 0.95;
}

/* 引用卡片样式 */
.msg-refs {
  margin-top: 18px;
  padding-top: 14px;
  border-top: 1px solid #f1f5f9;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.ref-section-title {
  font-size: 11px;
  font-weight: 700;
  color: #94a3b8;
  margin-bottom: 10px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  display: flex;
  align-items: center;
  gap: 6px;
}

.ref-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.mini-subject-card {
  display: flex;
  gap: 12px;
  padding: 10px;
  background: var(--bg-color);
  border: 1px solid var(--border-color);
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.mini-subject-card:hover {
  background: var(--card-bg);
  border-color: var(--primary-color);
  transform: translateX(6px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.mini-cover {
  width: 44px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.mini-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.mini-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.mini-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.mini-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ref-tag {
  font-size: 11px;
  background: var(--card-bg);
  color: var(--text-secondary);
  padding: 1px 6px;
  border-radius: 6px;
  border: 1px solid var(--border-color);
}

.ref-score {
  font-size: 11px;
  font-weight: 700;
  color: #f59e0b;
  display: flex;
  align-items: center;
  gap: 2px;
}

.mini-topic-card {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: var(--bg-color);
  border: 1px solid var(--border-color);
  border-radius: 14px;
  cursor: pointer;
  align-items: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.mini-topic-card:hover {
  background: var(--card-bg);
  border-color: var(--primary-color);
  transform: translateX(6px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.topic-icon {
  width: 32px;
  height: 32px;
  background: var(--card-bg);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-color);
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.03);
}

.topic-title {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 2px;
}

.topic-meta {
  font-size: 11px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
}

.dot {
  opacity: 0.5;
}

/* 输入区域 */
.input-area {
  padding: 20px 20px 24px;
  background: var(--card-bg);
  border-top: 1px solid var(--border-color);
}

.input-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.input-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  background: var(--bg-color);
  padding: 10px 14px;
  border-radius: 18px;
  border: 1px solid var(--border-color);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.input-wrapper:focus-within {
  border-color: var(--primary-color);
  background: var(--card-bg);
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.12);
}

.modern-chat-input {
  flex: 1;
}

.modern-chat-input :deep(.el-textarea__inner) {
  border: none !important;
  background: transparent !important;
  box-shadow: none !important;
  padding: 6px 0;
  max-height: 120px;
  font-size: 14.5px;
  color: var(--text-main);
}

.input-actions {
  display: flex;
  align-items: center;
  padding-bottom: 4px;
}

.send-btn {
  width: 36px !important;
  height: 36px !important;
  transition: all 0.3s;
}

.send-btn:not(:disabled):hover {
  transform: scale(1.1) rotate(-10deg);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 4px;
}

.ai-model-tag {
  font-size: 10px;
  font-weight: 700;
  color: var(--text-secondary);
  background: var(--bg-color);
  padding: 2px 8px;
  border-radius: 6px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border: 1px solid var(--border-color);
}

.input-count {
  font-size: 11px;
  color: var(--text-secondary);
  opacity: 0.5;
}

/* 正在输入动画 */
.typing-indicator {
  display: flex;
  gap: 5px;
  padding: 6px 4px;
}

.typing-indicator span {
  width: 6px;
  height: 6px;
  background: #6366f1;
  opacity: 0.3;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: translateY(0); opacity: 0.3; }
  40% { transform: translateY(-6px); opacity: 1; }
}

@keyframes fadeInDown {
  from { opacity: 0; transform: translateY(-20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 滚动条美化 */
.messages-scroll::-webkit-scrollbar {
  width: 6px;
}

.messages-scroll::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: 10px;
}

.messages-scroll::-webkit-scrollbar-thumb:hover {
  background: var(--text-secondary);
  opacity: 0.3;
}

.messages-scroll::-webkit-scrollbar-track {
  background: transparent;
}
</style>
