<!-- 页面：全局搜索 -->
<template>
  <div class="page-container">
    <div class="toolbar">
      <h3 class="page-title">全局搜索</h3>
      <div class="tools">
        <el-input v-model="keyword" placeholder="输入关键字搜索条目、话题或用户..." style="width:360px" @keyup.enter.native="doSearch" clearable>
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="doSearch">搜索</el-button>
      </div>
    </div>

    <div v-if="hasResults" class="results-container">
      <!-- 条目搜索结果 -->
      <div class="section" v-if="subjects.length">
        <h4 class="section-title">相关条目</h4>
        <div class="grid">
          <div v-for="it in subjects" :key="it.id" class="item-card" @click="goSubject(it.id)">
            <div class="thumb">
              <img v-if="it.coverPath" :src="base+it.coverPath" alt="cover" />
              <div v-else class="placeholder">No Cover</div>
            </div>
            <div class="entry">
              <div class="title" :title="it.cnName">{{ it.cnName }}</div>
              <div class="sub" :title="it.jpName">{{ it.jpName }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 论坛话题结果 -->
      <div class="section" v-if="topics.length">
        <h4 class="section-title">相关话题</h4>
        <el-card class="modern-card no-padding">
          <ul class="topic-list">
            <li v-for="t in topics" :key="t.id" @click="goTopic(t.id)">
              <div class="topic-item">
                <span class="topic-title">{{ t.title }}</span>
                <span v-if="subjectName(t.subjectId)" class="topic-subject">
                  <el-tag size="small" type="info" effect="plain">{{ subjectName(t.subjectId) }}</el-tag>
                </span>
              </div>
            </li>
          </ul>
        </el-card>
      </div>

      <!-- 用户搜索结果 -->
      <div class="section" v-if="users.length">
        <h4 class="section-title">相关用户</h4>
        <div class="grid user-grid">
          <div v-for="u in users" :key="u.id" class="user-card" @click="goUser(u.id)">
            <el-avatar :size="48" :src="u.avatarPath ? (base + u.avatarPath) : ''" class="user-avatar">
              {{ (u.nickname || u.username || 'U').charAt(0).toUpperCase() }}
            </el-avatar>
            <div class="user-info">
              <div class="user-name">{{ u.nickname || u.username }} <span class="uid">@{{ u.id }}</span></div>
              <div class="user-sig" v-if="u.signature">{{ u.signature }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="searched" class="empty-state">
      <el-empty description="没有找到相关结果" />
    </div>
  </div>
</template>
<script>
import { get, BASE } from '../utils/api'
export default {
  data(){ 
    return { 
      keyword:'', 
      subjects:[], 
      topics:[], 
      users:[], 
      base: BASE, 
      subjectsById:{},
      searched: false
    } 
  },
  computed: {
    hasResults() {
      return this.subjects.length > 0 || this.topics.length > 0 || this.users.length > 0
    }
  },
  methods:{
    async doSearch(){ 
      const r = await get('/search/all', { keyword:this.keyword }); 
      this.searched = true;
      if(r.code===0){ 
        this.subjects=r.data.subjects||[]; 
        this.topics=r.data.topics||[]; 
        this.users=r.data.users||[]; 
        await this.loadTopicSubjects() 
      } 
    },
    goSubject(id){ this.$router.push('/subject/'+id) },
    goTopic(id){ this.$router.push('/discussion/topic/'+id) },
    goUser(id){ this.$router.push('/user/'+id) },
    async loadTopicSubjects(){ const ids = [...new Set((this.topics||[]).map(x=> x.subjectId).filter(Boolean))]; for(const id of ids){ if(!this.subjectsById[id]){ const s = await get('/subject/'+id); if(s.code===0 && s.data){ this.subjectsById[id] = s.data } } } },
    subjectName(id){ const s = this.subjectsById[id] || {}; return s.cnName || '' },
  }
}
</script>
<style scoped>
.toolbar { display:flex; justify-content:space-between; align-items:center; margin: 0 0 24px 0; padding-top: 4px; }
.tools { display:flex; gap:12px; align-items:center }

.results-container {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 16px;
  color: var(--text-main);
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title::before {
  content: '';
  width: 3px;
  height: 14px;
  background: var(--primary-color);
  border-radius: 2px;
}

/* 条目栅格 */
.grid { display:grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap:20px }
.item-card { 
  display:flex; 
  gap:12px; 
  background: var(--card-bg); 
  border: 1px solid var(--border-color); 
  border-radius: 12px; 
  padding: 12px; 
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: var(--card-shadow);
}
.item-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.1);
  border-color: var(--primary-color);
}

.thumb { width:80px; height:113px; flex-shrink:0; overflow:hidden; border-radius:6px; background:var(--bg-color) }
.thumb img { width:100%; height:100%; object-fit:cover; display:block }
.placeholder { width:100%; height:100%; display:flex; align-items:center; justify-content:center; color:var(--text-secondary); font-size: 12px; opacity: 0.5; }

.entry { flex: 1; min-width: 0; }
.entry .title { font-size:15px; font-weight: 600; color: var(--text-main); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.entry .sub { font-size:13px; color: var(--text-secondary); margin-top: 4px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }

/* 话题列表 */
.no-padding { :deep(.el-card__body) { padding: 0; } }
.topic-list { list-style:none; padding:0; margin:0 }
.topic-list li { 
  padding: 14px 20px; 
  border-bottom: 1px solid var(--border-color); 
  cursor: pointer; 
  transition: background 0.2s;
}
.topic-list li:last-child { border-bottom: none; }
.topic-list li:hover { background: var(--bg-color); }
.topic-item { display: flex; justify-content: space-between; align-items: center; gap: 16px; }
.topic-title { font-size: 15px; color: var(--text-main); font-weight: 500; }

/* 用户栅格 */
.user-grid { grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); }
.user-card { 
  display: flex; 
  align-items: center; 
  gap: 12px; 
  background: var(--card-bg); 
  border: 1px solid var(--border-color); 
  border-radius: 12px; 
  padding: 16px; 
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: var(--card-shadow);
}
.user-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.1);
  border-color: var(--primary-color);
}
.user-avatar { border: 2px solid var(--card-bg); box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.user-info { flex: 1; min-width: 0; }
.user-name { font-size: 15px; font-weight: 600; color: var(--text-main); }
.user-sig { font-size: 13px; color: var(--text-secondary); margin-top: 2px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.uid { color: var(--text-secondary); opacity: 0.7; font-size: 12px; font-weight: 400; margin-left: 4px; }

.empty-state { padding: 60px 0; }

@media (max-width: 768px) {
  .toolbar { flex-direction: column; align-items: flex-start; gap: 16px; }
  .tools { width: 100%; }
  .tools .el-input { width: 100% !important; }
}
</style>
