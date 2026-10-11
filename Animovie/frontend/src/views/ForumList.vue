<!-- 页面：论坛列表 -->
<template>
  <div class="page-container">
    <div class="toolbar">
      <h3 class="page-title">论坛</h3>
      <div class="tools">
        <el-input v-model="keyword" placeholder="按标题/条目搜索" style="width:260px" />
        <el-button type="primary" @click="load">搜索</el-button>
        <el-button type="success" @click="openPublish">发布话题</el-button>
        <el-select v-if="role==='ADMIN'" v-model="viewStatus" placeholder="视图" style="width:160px" @change="()=>{ pageNum=1; load() }" :value-key="'value'">
          <el-option
            v-for="item in viewOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
            <template #default>
              {{ item.label }}
              <span v-if="item.value === 'LOCKED'" style="color:red">({{ pendingCount }})</span>
            </template>
          </el-option>
        </el-select>
      </div>
    </div>
    <el-card class="modern-card forum-card">
      <div class="forum-list">
        <div v-for="row in items" :key="row.id" class="topic-card" @click="goTopic(row.id)">
          <!-- 条目封面 -->
          <div class="topic-cover">
            <img v-if="subject(row.subjectId).coverPath" :src="base+subject(row.subjectId).coverPath" alt="cover" />
            <div v-else class="placeholder">No Cover</div>
            <div class="subject-tag">{{ subject(row.subjectId).category }}</div>
          </div>

          <!-- 话题内容 -->
          <div class="topic-main">
            <div class="topic-header">
              <h4 class="topic-title">{{ row.title }}</h4>
              <div class="topic-status" v-if="row.status!=='OPEN'">
                <el-tag size="small" :type="row.status==='LOCKED' ? 'warning' : 'danger'" effect="dark">
                  {{ row.status==='LOCKED' ? '待审核' : '已打回' }}
                </el-tag>
              </div>
            </div>

            <div class="topic-subject">
              <el-icon><Collection /></el-icon>
              <span>{{ subject(row.subjectId).cnName || ('条目 '+row.subjectId) }}</span>
            </div>

            <div class="topic-author">
              <div class="author-avatar">
                <img v-if="row.avatarPath" :src="base+row.avatarPath" alt="avatar" />
                <div v-else class="avatar-placeholder">{{ (row.nickname || row.username || '?').charAt(0) }}</div>
              </div>
              <span class="author-name">{{ row.nickname || row.username || '神秘用户' }}</span>
              <span class="post-time">
                <el-icon><Calendar /></el-icon>
                {{ row.updatedAt }}
              </span>
            </div>

            <div class="topic-footer">
              <div class="topic-meta">
                <span class="meta-item">
                  <el-icon><ChatLineRound /></el-icon>
                  {{ row.replyCount || 0 }} 回复
                </span>
              </div>

              <div class="admin-actions" v-if="role==='ADMIN' || row.userId===meId">
                <template v-if="role==='ADMIN' && viewStatus==='LOCKED'">
                  <el-button size="small" type="primary" plain @click.stop="approve(row.id)">通过</el-button>
                  <el-button size="small" plain @click.stop="reject(row.id)">退回</el-button>
                </template>
                <template v-if="role==='ADMIN' && viewStatus==='REJECTED'">
                  <el-button size="small" type="primary" plain @click.stop="approve(row.id)">通过</el-button>
                  <el-button size="small" plain @click.stop="lock(row.id)">锁定</el-button>
                </template>
                <el-popconfirm title="确认删除该话题？" @confirm="deleteTopic(row.id)">
                  <template #reference>
                    <el-button type="danger" link icon="Delete" @click.stop />
                  </template>
                </el-popconfirm>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="pager">
        <el-pagination 
          background 
          layout="total, prev, pager, next" 
          :total="total" 
          :page-size="pageSize" 
          :current-page="pageNum" 
          @current-change="(p)=>{ pageNum=p; load() }" 
        />
      </div>
    </el-card>
  </div>

  <el-dialog v-model="publishDialog" title="发布话题" width="560px" class="modern-dialog">
    <div class="dialog-content">
      <el-form :model="publish" label-position="top">
        <el-form-item label="关联条目">
          <el-select v-model="publish.subjectId" filterable remote reserve-keyword :remote-method="searchSubjects" :loading="subjectLoading" placeholder="输入关键字搜索条目" style="width:100%">
            <el-option v-for="s in subjectOptions" :key="s.id" :label="s.cnName || (s.jpName || ('条目 '+s.id))" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="话题标题">
          <el-input v-model="publish.title" :maxlength="200" show-word-limit placeholder="吸引人的标题..." />
        </el-form-item>
        <el-form-item label="正文内容">
          <el-input type="textarea" v-model="publish.content" :rows="8" placeholder="分享你的想法..." />
        </el-form-item>
        <el-alert v-if="publishError" :title="publishError" type="error" show-icon closable style="margin-bottom: 16px;" />
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="publishDialog=false" round>取消</el-button>
        <el-button type="primary" :loading="publishing" @click="submitPublish" round class="submit-btn">发布话题</el-button>
      </div>
    </template>
  </el-dialog>
</template>
<script>
import { get, BASE, formAuth, del } from '../utils/api'
import { ChatLineRound, Calendar, Collection, Delete } from '@element-plus/icons-vue'

export default {
  components: { ChatLineRound, Calendar, Collection, Delete },
  data(){ return { items:[], total:0, pageNum:1, pageSize:10, keyword:'', subjectsById:{}, base: BASE, role: localStorage.getItem('role')||'', token: localStorage.getItem('token')||'', meId:null, publishDialog:false, publish:{ subjectId:null, title:'', content:'' }, subjectOptions:[], subjectLoading:false, publishing:false, publishError:'', viewStatus:'OPEN', pendingCount:0, viewOptions: [{ value: 'OPEN', label: '公开' }, { value: 'LOCKED', label: '待审核' }, { value: 'REJECTED', label: '已打回' }] } },
  mounted(){ this.load(); this.loadMe() },
  methods:{
    async load(){
      const isPending = this.role==='ADMIN' && this.viewStatus==='LOCKED'
      const isRejected = this.role==='ADMIN' && this.viewStatus==='REJECTED'
      let r
      if(isPending){ r = await get('/discussion/admin/pending', { pageNum:this.pageNum, pageSize:this.pageSize }) }
      else if(isRejected){ r = await get('/discussion/admin/rejected', { pageNum:this.pageNum, pageSize:this.pageSize }) }
      else if(this.keyword && this.keyword.trim()){
        r = await get('/discussion/search', { keyword:this.keyword.trim(), pageNum:this.pageNum, pageSize:this.pageSize })
      }else{
        r = await get('/discussion/all', { pageNum:this.pageNum, pageSize:this.pageSize })
      }
      if(r.code===0){ 
        this.items = r.data.items || []; 
        this.total = r.data.total || 0; 
        // 如果当前视图是待审核，更新待审核数量
        if(isPending) this.pendingCount = r.data.total || 0;
        await this.loadSubjects();
        // 如果不是管理员或当前视图不是待审核，获取待审核总数
        if(this.role==='ADMIN' && !isPending) {
          const pendingRes = await get('/discussion/admin/pending', { pageNum:1, pageSize:1 });
          if(pendingRes.code===0) this.pendingCount = pendingRes.data.total || 0;
        }
      }
    },
    async loadSubjects(){
      const ids = [...new Set(this.items.map(x=> x.subjectId))]
      for(const id of ids){ if(!this.subjectsById[id]){ const s = await get('/subject/'+id); if(s.code===0 && s.data){ this.subjectsById[id] = s.data } } }
    },
    subject(id){ return this.subjectsById[id] || {} },
    goTopic(id){ this.$router.push('/discussion/topic/'+id) },
    openPublish(){ this.publishDialog=true; this.publish={ subjectId:null, title:'', content:'' }; this.publishError=''; this.subjectOptions=[] },
    async searchSubjects(q){
      this.subjectLoading=true
      try{
        const r = await get('/subject', { keyword:q||'', pageNum:1, pageSize:10 })
        if(r.code===0){ this.subjectOptions = (r.data.items||[]) }
      }finally{ this.subjectLoading=false }
    },
    async submitPublish(){
      this.publishError=''
      if(!this.publish.subjectId || !this.publish.title){ this.publishError='请选择条目并填写标题'; return }
      this.publishing=true
      try{
        const r = await formAuth('/discussion/topic', { subjectId:this.publish.subjectId, title:this.publish.title, content:this.publish.content||'' })
        if(r.code===0){ this.publishDialog=false; this.$message.success('已提交，待审核通过后展示'); this.load() }
        else{ this.publishError = r.msg || '发布失败' }
      }catch(e){ this.publishError = e.message }
      finally{ this.publishing=false }
    },
    async loadMe(){ if(!this.token) return; try{ const r = await get('/auth/me'); if(r.code===0){ this.meId = r.data.id || null } }catch(e){} },
    async deleteTopic(id){ 
      const r = await del('/discussion/topic/'+id); 
      if(r.code===0){ 
        this.$message.success('已删除'); 
        this.items = this.items.filter(x=> x.id!==id); 
        this.total = Math.max(0,(this.total||0)-1);
        window.location.reload();
      } else { 
        this.$message.error(r.msg||'失败') 
      } 
    },
    async approve(id){ const r = await formAuth('/discussion/admin/approve', { topicId:id }); if(r.code===0){ this.$message.success('已通过'); this.load() } else { this.$message.error(r.msg||'失败') } },
    async reject(id){ const r = await formAuth('/discussion/admin/reject', { topicId:id }); if(r.code===0){ this.$message.success('已打回'); this.load() } else { this.$message.error(r.msg||'失败') } },
    async lock(id){ const r = await formAuth('/discussion/admin/lock', { topicId:id }); if(r.code===0){ this.$message.success('已重新锁定'); this.load() } else { this.$message.error(r.msg||'失败') } },
  }
}
</script>
<style scoped>
.toolbar { display:flex; justify-content:space-between; align-items:center; margin: 0 0 24px 0; padding-top: 4px; }
.tools { display:flex; gap:12px; align-items:center }

.forum-card {
  padding: 20px;
}

.forum-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(460px, 1fr));
  gap: 24px;
}

.topic-card { 
  display: flex; 
  gap: 20px; 
  padding: 20px; 
  background: var(--card-bg); 
  border: 1px solid var(--border-color); 
  border-radius: 20px; 
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); 
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.topic-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--card-shadow);
  border-color: var(--primary-color);
}

.topic-cover { 
  width: 100px; 
  height: 140px; 
  overflow: hidden; 
  border-radius: 10px; 
  background: var(--bg-color); 
  flex-shrink: 0;
  position: relative;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  border: 1px solid var(--border-color);
}

.topic-cover img { 
  width: 100%; 
  height: 100%; 
  object-fit: cover; 
  display: block;
  transition: transform 0.3s;
}

.topic-card:hover .topic-cover img {
  transform: scale(1.05);
}

.subject-tag {
  position: absolute;
  top: 6px;
  left: 6px;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
  backdrop-filter: blur(4px);
}

.placeholder { 
  width: 100%; 
  height: 100%; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  color: #94a3b8; 
  font-size: 12px;
}

.topic-main { 
  flex: 1; 
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.topic-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 8px;
}

.topic-title { 
  font-size: 18px; 
  font-weight: 700; 
  color: var(--text-main); 
  margin-bottom: 8px;
  line-height: 1.4;
}

.topic-subject {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #6366f1;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 12px;
}

.topic-author {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.author-avatar { 
  width: 24px; 
  height: 24px; 
  border-radius: 50%; 
  overflow: hidden; 
  background: #f1f5f9; 
  border: 1px solid #e2e8f0;
  flex-shrink: 0;
}

.author-avatar img { width: 100%; height: 100%; object-fit: cover; }

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #6366f1;
  color: #fff;
  font-size: 12px;
  font-weight: bold;
}

.author-name { font-size: 13px; font-weight: 600; color: var(--text-main); }
.post-time { font-size: 12px; color: var(--text-secondary); }

.topic-footer { 
  margin-top: 16px; 
  padding-top: 16px; 
  border-top: 1px dashed var(--border-color); 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
}

.topic-meta { display: flex; gap: 16px; }
.meta-item { display: flex; align-items: center; gap: 4px; font-size: 13px; color: var(--text-secondary); }

.admin-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pager { margin-top: 24px; display:flex; justify-content:flex-end }
</style>
