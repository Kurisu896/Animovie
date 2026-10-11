<!-- 页面：我的动态 -->
<template>
  <div class="page-container">
    <div class="toolbar">
      <h3 class="page-title">我的动态</h3>
      <div class="tools"></div>
    </div>
    <el-card class="modern-card timeline-card">
      <div class="timeline-container">
        <div v-for="it in items" :key="it.id" class="timeline-item">
          <!-- 左侧：时间线 -->
          <div class="timeline-left">
            <div class="timeline-dot"></div>
            <div class="timeline-line"></div>
          </div>

          <!-- 右侧：内容区 -->
          <div class="timeline-content">
            <div class="content-header">
              <span class="action-text">
                <template v-if="it.type==='STATUS'">
                  更新了收藏状态为 <span class="status-tag" :class="it.detail.toLowerCase()">{{ mapStatus(it.detail) }}</span>
                </template>
                <template v-else-if="it.type==='PROGRESS'">
                  更新了观看进度至 <span class="highlight-text">第 {{ it.detail }} 集</span>
                </template>
                <template v-else-if="it.type==='RATING'">
                  打分了 <span class="rating-text">{{ it.detail }} 分</span>
                </template>
                <template v-else-if="it.type==='COMMENT'">
                  发表了吐槽
                </template>
                <template v-else>
                  进行了操作：{{ it.type }}
                </template>
              </span>
              <span class="time-stamp">{{ it.createdAt }}</span>
              
              <div class="header-actions">
                <el-popconfirm title="确认删除动态？" @confirm="deleteItem(it.id)">
                  <template #reference>
                    <el-button link type="danger" icon="Delete" class="delete-btn" />
                  </template>
                </el-popconfirm>
              </div>
            </div>

            <div class="comment-text" v-if="it.type==='COMMENT' && it.detail">
              “{{ it.detail }}”
            </div>

            <!-- 条目卡片 -->
            <div class="subject-mini-card" @click="goDetail(it.subjectId)" v-if="subjectsById[it.subjectId]">
              <div class="mini-cover">
                <img v-if="subjectsById[it.subjectId].coverPath" :src="base+subjectsById[it.subjectId].coverPath" alt="cover" />
                <div v-else class="placeholder">No Cover</div>
              </div>
              <div class="mini-info">
                <div class="mini-title">{{ subjectName(it.subjectId) }}</div>
                <div class="mini-sub">{{ (subjectsById[it.subjectId]||{}).jpName }}</div>
                <div class="mini-meta">
                  <el-tag size="small" effect="plain" class="mini-tag">{{ (subjectsById[it.subjectId]||{}).category }}</el-tag>
                  <span class="mini-episodes">{{ (subjectsById[it.subjectId]||{}).episodes }} 话</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
    <div class="pager">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page="pageNum" @current-change="(p)=>{ pageNum=p; load() }" />
    </div>
  </div>
</template>
<script>
import { get, del, BASE } from '../utils/api'
export default {
  data(){ return { items:[], total:0, pageNum:1, pageSize:20, subjectsById:{}, base: BASE } },
  mounted(){ this.load() },
  methods:{
    async load(){
      const r = await get('/activity/my', { pageNum:this.pageNum, pageSize:this.pageSize })
      if(r.code===0){
        this.items = r.data || []
        await this.loadSubjects()
        this.total = this.pageNum * this.pageSize + 1
      }
    },
    async loadSubjects(){
      const ids = [...new Set((this.items||[]).map(x=> x.subjectId).filter(Boolean))]
      for(const id of ids){
        if(!this.subjectsById[id]){
          const s = await get('/subject/'+id)
          if(s.code===0 && s.data){ this.subjectsById[id] = s.data }
        }
      }
    },
    subjectName(id){ if(!id){ return '已删除条目' } const s = this.subjectsById[id] || {}; return s.cnName || '已删除条目' },
    msg(it){
      const name = this.subjectName(it.subjectId)
      if(it.type==='STATUS') return `更新了「${name}」的收藏状态为「${this.mapStatus(it.detail)}」`
      if(it.type==='PROGRESS') return `更新了「${name}」的观看进度至第 ${it.detail} 集`
      if(it.type==='RATING') return `为「${name}」打分：${it.detail} 分`
      if(it.type==='COMMENT') return `对「${name}」发表吐槽：${it.detail}`
      return `在「${name}」进行了操作：${it.type} ${it.detail}`
    },
    mapStatus(s){ return s==='WATCHING'?'在看': s==='WATCHED'?'看过':'抛弃' }
    ,goDetail(id){ this.$router.push('/subject/'+id) }
    ,async deleteItem(id){
      const r = await del('/activity/one?id='+id)
      if(r.code===0){ this.items = this.items.filter(x=> x.id!==id); this.$message.success('已删除动态') } else { this.$message.error(r.msg||'删除失败') }
    }
  }
}
</script>
<style scoped>
.toolbar { display:flex; justify-content:space-between; align-items:center; margin: 0 0 24px 0; padding-top: 4px; }

.timeline-card {
  padding: 10px 20px;
}

.timeline-container {
  display: flex;
  flex-direction: column;
}

.timeline-item {
  display: flex;
  gap: 20px;
}

.timeline-left {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 24px;
}

.timeline-dot {
  width: 12px;
  height: 12px;
  background: var(--primary-color);
  border-radius: 50%;
  border: 3px solid var(--card-bg);
  box-shadow: 0 0 0 1px var(--border-color);
  margin-top: 6px;
  z-index: 1;
}

.timeline-line {
  width: 2px;
  flex: 1;
  background: var(--border-color);
  margin: 4px 0;
}

.timeline-item:last-child .timeline-line {
  display: none;
}

.timeline-content {
  flex: 1;
  padding-bottom: 30px;
}

.content-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.action-text {
  color: var(--text-main);
  font-size: 14px;
}

.status-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.status-tag.watching { background: #dcfce7; color: #15803d; }
.status-tag.watched { background: #dbeafe; color: #1d4ed8; }
.status-tag.dropped { background: #fee2e2; color: #b91c1c; }

.highlight-text {
  color: #6366f1;
  font-weight: 600;
}

.rating-text {
  color: #f59e0b;
  font-weight: 700;
}

.time-stamp {
  color: var(--text-secondary);
  font-size: 12px;
  margin-left: auto;
}

.header-actions {
  margin-left: 12px;
}

.comment-text {
  background: var(--bg-color);
  padding: 12px 16px;
  border-radius: 12px;
  color: var(--text-secondary);
  font-size: 14px;
  font-style: italic;
  margin-bottom: 12px;
  border-left: 4px solid var(--border-color);
}

/* 条目卡片美化 */
.subject-mini-card {
  display: flex;
  gap: 16px;
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.2s;
  max-width: 500px;
}

.subject-mini-card:hover {
  border-color: var(--primary-color);
  background: var(--bg-color);
  transform: translateX(4px);
}

.mini-cover {
  width: 60px;
  height: 84px;
  border-radius: 6px;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
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
  min-width: 0;
}

.mini-title {
  font-weight: 700;
  font-size: 14px;
  color: var(--text-main);
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.mini-sub {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.mini-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.mini-tag {
  border-radius: 4px;
}

.mini-episodes {
  font-size: 12px;
  color: var(--text-secondary);
  opacity: 0.7;
}

.pager { margin-top: 24px; display:flex; justify-content:flex-end }
</style>
