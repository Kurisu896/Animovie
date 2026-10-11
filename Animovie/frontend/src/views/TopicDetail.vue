<!-- 页面：话题详情 -->
<template>
  <div class="page-container topic-page">
    <div class="columnA">
      <el-card class="modern-card topic-detail-card">
        <div class="topic-header">
          <div class="topic-title-section">
            <h2 class="topic-title">{{ topic.title }}</h2>
            <div class="topic-meta">
              <div class="meta-item author" @click="goUser(topic.userId)">
                <div class="author-avatar-mini">
                  <img v-if="topic.avatarPath" :src="base+topic.avatarPath" alt="avatar" />
                  <div v-else class="avatar-placeholder-mini">{{ (topic.nickname || topic.username || '?').charAt(0) }}</div>
                </div>
                <span>{{ topic.nickname || topic.username || '用户' }}</span>
              </div>
              <span class="meta-sep">·</span>
              <div class="meta-item">
                <el-icon><Calendar /></el-icon>
                <span>{{ topic.createdAt }}</span>
              </div>
              <span class="meta-sep">·</span>
              <div class="meta-item subject-link" @click="goSubject(topic.subjectId)">
                <el-icon><Collection /></el-icon>
                <span>{{ subjectName }}</span>
              </div>
            </div>
          </div>
          <div class="header-actions" v-if="role==='ADMIN' || (meId && topic.userId===meId)">
            <el-popconfirm title="确认删除该话题？" @confirm="deleteTopic">
              <template #reference>
                <el-button type="danger" plain size="small" icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>
        <div class="topic-content" v-if="topic.content">{{ topic.content }}</div>
      </el-card>

      <el-card class="modern-card replies-card">
        <div class="replies-header">
          <div class="replies-title">
            <el-icon><ChatLineRound /></el-icon>
            <span>全部回复 ({{ total }})</span>
          </div>
          <div class="replies-tools">
            <el-select v-model="commentSort" size="small" style="width:120px" @change="()=>{ pageNum=1; loadComments() }">
              <el-option label="最新" value="latest" />
              <el-option label="热门" value="likes" />
            </el-select>
            <el-button type="primary" size="small" @click="openReply(null)" icon="Plus">发表回复</el-button>
          </div>
        </div>

        <div class="comments-list">
          <div class="comment-item" v-for="row in nestedComments" :key="row.id">
            <div class="comment-main">
              <div class="comment-avatar" @click="goUser(row.userId)">
                <img v-if="row.avatarPath" :src="base+row.avatarPath" alt="avatar" />
                <div v-else class="avatar-placeholder">{{ (row.nickname || row.username || '?').charAt(0) }}</div>
              </div>
              <div class="comment-body">
                <div class="comment-header-row">
                  <span class="comment-author" @click="goUser(row.userId)">{{ row.nickname || row.username || '用户' }}</span>
                  <span class="comment-floor">#{{ row.floor }}</span>
                </div>
                <div class="comment-sign" v-if="row.signature">{{ row.signature }}</div>
                <div class="comment-text">{{ row.content }}</div>
                <div class="comment-footer">
                  <span class="comment-time">{{ row.createdAt }}</span>
                  <div class="comment-ops">
                    <span class="op-item like" :class="{ liked: isLiked(row.id) }" @click="toggleLike(row)">
                      <el-icon><CaretTop /></el-icon>
                      {{ row.likeCount }}
                    </span>
                    <span class="op-item reply-link" @click="openReply(row)">
                      回复
                    </span>
                    <el-button v-if="role==='ADMIN' || row.userId===meId" link size="small" type="danger" icon="Delete" class="delete-link" @click.stop="deleteComment(row)" />
                  </div>
                </div>
              </div>
            </div>

            <!-- 子回复 -->
            <div class="child-replies" v-if="row.children && row.children.length">
              <div class="child-item" v-for="child in row.children" :key="child.id">
                <div class="child-avatar" @click="goUser(child.userId)">
                  <img v-if="child.avatarPath" :src="base+child.avatarPath" alt="avatar" />
                  <div v-else class="avatar-placeholder-mini">{{ (child.nickname || child.username || '?').charAt(0) }}</div>
                </div>
                <div class="child-body">
                  <div class="child-header-row">
                    <span class="child-author" @click="goUser(child.userId)">{{ child.nickname || child.username || '用户' }}</span>
                    <span class="child-floor">#{{ child.floor }}</span>
                  </div>
                  <div class="child-text">{{ child.content }}</div>
                  <div class="child-footer">
                    <span class="child-time">{{ child.createdAt }}</span>
                    <div class="child-ops">
                      <span class="op-item like mini" :class="{ liked: isLiked(child.id) }" @click="toggleLike(child)">
                        <el-icon><CaretTop /></el-icon>
                        {{ child.likeCount }}
                      </span>
                      <span class="op-item reply-link mini" @click="openReply(row)">
                        回复
                      </span>
                      <el-button v-if="role==='ADMIN' || child.userId===meId" link size="small" type="danger" icon="Delete" class="delete-link mini" @click.stop="deleteComment(child)" />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="pager" v-if="total > pageSize">
          <el-pagination 
            background 
            layout="prev, pager, next" 
            :total="total" 
            :page-size="pageSize" 
            :current-page="pageNum" 
            @current-change="(p)=>{ pageNum=p; loadComments() }" 
          />
        </div>
      </el-card>

      <el-dialog v-model="replyVisible" :title="replyTarget ? ('回复：' + (replyTarget.nickname || replyTarget.username || ('#' + replyTarget.floor))) : '发表回复'" width="560px" class="modern-dialog">
        <div class="dialog-content">
          <el-form :model="replyForm" label-position="top">
            <el-form-item label="回复内容">
              <el-input v-model="replyForm.content" type="textarea" :rows="6" placeholder="写下你的回复..." />
            </el-form-item>
          </el-form>
        </div>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="replyVisible=false" round>取消</el-button>
            <el-button type="primary" @click="submitReply" round class="submit-btn">发布回复</el-button>
          </div>
        </template>
      </el-dialog>
    </div>
    <div class="columnC">
      <el-card class="modern-card subject-side-card" @click="goSubject(topic.subjectId)">
        <div class="mini-card-header">关联条目</div>
        <div class="mini-card-content">
          <div class="mini-cover">
            <img v-if="subject.coverPath" :src="base+subject.coverPath" alt="cover" />
            <div v-else class="placeholder">No Cover</div>
          </div>
          <div class="mini-info">
            <h4 class="mini-title">{{ subject.cnName || subjectName }}</h4>
            <div class="mini-sub">{{ subject.jpName }}</div>
            <div class="mini-meta">
              <el-tag size="small" effect="plain" class="mini-tag">{{ subject.category }}</el-tag>
              <span class="mini-episodes">{{ subject.episodes }} 话</span>
            </div>
            <div class="mini-actions" v-if="token">
              <el-button 
                size="small" 
                :type="collect ? 'danger' : 'primary'" 
                :icon="collect ? 'StarFilled' : 'Star'"
                plain
                @click.stop="openEdit(topic.subjectId)"
                class="mini-collect-btn"
              >
                {{ collectText(topic.subjectId) }}
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>
    <el-dialog v-model="editVisible" title="修改收藏状态" width="520px" class="modern-dialog" append-to-body>
      <div class="dialog-content">
        <el-form :model="editForm" label-position="top">
          <el-form-item label="观看状态">
            <el-radio-group v-model="editForm.status" class="status-radio-group">
              <el-radio-button label="WATCHING">在看</el-radio-button>
              <el-radio-button label="WATCHED">看过</el-radio-button>
              <el-radio-button label="DROPPED">抛弃</el-radio-button>
            </el-radio-group>
          </el-form-item>
          
          <el-row :gutter="20">
            <el-col :span="10">
              <el-form-item label="当前进度">
                  <el-input-number 
                    v-model="editForm.progressEpisode" 
                    :min="0" 
                    :max="editForm.maxEpisodes || 9999" 
                    controls-position="right"
                    style="width: 100%" 
                  />
                </el-form-item>
            </el-col>
            <el-col :span="14">
              <el-form-item label="我的评分">
                <div class="rate-wrapper">
                  <el-rate v-model="editForm.rating" :max="10" show-score text-color="#ff9900" />
                </div>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="吐槽一下">
            <el-input
              v-model="editForm.shortComment"
              type="textarea"
              :rows="4"
              maxlength="200"
              show-word-limit
              placeholder="写下你的简评..."
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editVisible=false" round>取消</el-button>
          <el-button type="primary" @click="saveEdit" round class="submit-btn">保存修改</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
  </template>
<script>
import { get, formAuth, post } from '../utils/api'
import { del, BASE } from '../utils/api'
import { ElMessageBox } from 'element-plus'
import { Calendar, Collection, Delete, ChatLineRound, Plus, CaretTop, StarFilled, Star } from '@element-plus/icons-vue'

export default {
  components: { Calendar, Collection, Delete, ChatLineRound, Plus, CaretTop, StarFilled, Star },
  data(){ return { id:null, topic:{}, subjectName:'', subject:{}, base: BASE, comments:[], pageNum:1, pageSize:20, total:0, replyVisible:false, replyForm:{ content:'' }, replyTarget:null, likedIds:[], role: localStorage.getItem('role')||'', token: localStorage.getItem('token')||'', collect:null, editVisible:false, editForm:{ status:'', progressEpisode:0, rating:null, shortComment:'', maxEpisodes:0 }, commentSort:'latest', meId:null } },
  computed:{
    nestedComments(){
      const list = Array.isArray(this.comments) ? this.comments : []
      const roots = list.filter(c => !c.parentId)
      const childrenByParent = {}
      for(const c of list){ if(c.parentId){ (childrenByParent[c.parentId] ||= []).push(c) } }
      for(const r of roots){ r.children = childrenByParent[r.id] || [] }
      return roots
    }
  },
  async mounted(){
    await this.setTopicIdAndLoad(this.$route.params.id)
    await this.loadMe()
  },
  methods:{
    async setTopicIdAndLoad(paramId){
      let nid = Number(paramId)
      if(!Number.isFinite(nid)) nid = parseInt(paramId, 10)
      if(Number.isNaN(nid) || !nid){ this.id = null; return }
      this.id = nid
      await this.loadTopic()
      await this.loadComments()
      await this.loadMe()
    },
    async loadTopic(){
      if(!this.id || !Number.isFinite(this.id)) return
      const r = await get('/discussion/topic/'+this.id)
      if(r.code===0 && r.data){
        this.topic = r.data
        if(this.topic && this.topic.subjectId){
          const s = await get('/subject/'+this.topic.subjectId)
          if(s.code===0 && s.data){ this.subject = s.data; this.subjectName = s.data.cnName || s.data.jpName || ('条目 '+this.topic.subjectId) }
          await this.loadCollect(); await this.prefill()
        }
        
      }
    },
    async loadComments(){
      if(!this.id || !Number.isFinite(this.id)) return
      const r = await get('/discussion/comments', { topicId:this.id, pageNum:this.pageNum, pageSize:this.pageSize, sort:this.commentSort })
      if(r.code===0){ this.comments = r.data.items || []; this.total = r.data.total || 0 }
      
      await this.loadLiked()
    },
    async submitReply(){
      const payload = { topicId:this.id, content:this.replyForm.content }
      if(this.replyTarget && this.replyTarget.id){ payload.parentId = this.replyTarget.id }
      const r = await formAuth('/discussion/comment', payload)
      if(r.code===0){ this.$message.success('已发布'); this.replyVisible=false; this.replyForm={ content:'' }; this.pageNum=1; this.loadComments() } else { this.$message.error(r.msg||'失败') }
    },
    openReply(row){ if(!this.token){ this.$message.warning('请先登录'); return } this.replyTarget = row || null; this.replyVisible = true },
    async like(row){ const r = await formAuth('/discussion/like', { commentId:row.id }); if(r.code===0){ row.likeCount++; } },
    async unlike(row){ const r = await del('/discussion/like?commentId='+row.id); if(r.code===0){ if(row.likeCount>0) row.likeCount--; } },
    isLiked(id){ return this.likedIds.includes(id) },
    async loadLiked(){ const r = await get('/discussion/liked', { topicId:this.id }); if(r.code===0){ this.likedIds = r.data || [] } },
    async loadMe(){ if(!this.token) return; try{ const r = await get('/auth/me'); if(r.code===0){ this.meId = r.data.id || null } }catch(e){} },
    async toggleLike(row){ if(this.isLiked(row.id)){ await this.unlike(row); this.likedIds = this.likedIds.filter(x=> x!==row.id) } else { const resp = await formAuth('/discussion/like', { commentId:row.id }); if(resp.code===0){ row.likeCount++; this.likedIds = [...this.likedIds, row.id] } else { if((resp.msg||'')==='exists'){ this.likedIds = [...this.likedIds, row.id] } } } },
    goSubject(id){ this.$router.push('/subject/'+id) }
    ,goUser(id){ if(!id) return; this.$router.push('/user/'+id) }
    ,async deleteComment(row){
      try{
        await ElMessageBox.confirm('确定删除该评论？删除后不可恢复', '确认删除', { confirmButtonText:'删除', cancelButtonText:'取消', type:'warning' })
      }catch(e){ return }
      const r = await del('/discussion/comment/'+row.id); const j = r;
      if(j.code===0){ this.$message.success('已删除'); this.comments = this.comments.filter(x=> x.id!==row.id); this.total = Math.max(0,(this.total||0)-1); if(this.replyTarget && this.replyTarget.id===row.id){ this.replyTarget=null } } else { this.$message.error(j.msg||'失败') }
    }
    ,async loadCollect(){ if(!this.token || !this.topic.subjectId) return; try{ const r = await get('/collection/one', { subjectId:this.topic.subjectId }); if(r.code===0){ this.collect = r.data || null } }catch(e){} }
    ,async prefill(){ if(!this.topic.subjectId) return; try{ const r = await get('/collection/one', { subjectId:this.topic.subjectId }); if(r.code===0 && r.data){ this.editForm = { status:r.data.status||'', progressEpisode:r.data.progressEpisode||0, rating:r.data.rating||null, shortComment:r.data.shortComment||'' } } else { this.editForm = { status:'', progressEpisode:0, rating:null, shortComment:'' } } }catch(e){ this.editForm = { status:'', progressEpisode:0, rating:null, shortComment:'' } } }
    ,async openEdit(id){ 
      if(!this.token){ this.$message.warning('请先登录'); return } 
      await this.prefill(); 
      this.editForm.maxEpisodes = this.subject.episodes || 0;
      this.editVisible = true 
    }
    ,async saveEdit(){ 
      if (this.editForm.progressEpisode > this.editForm.maxEpisodes && this.editForm.maxEpisodes > 0) {
        this.$message.warning(`当前进度不能超过总话数 (${this.editForm.maxEpisodes})`)
        return
      }
      const payload = Object.assign({ subjectId:this.topic.subjectId }, this.editForm); 
      delete payload.maxEpisodes;
      const r = await post('/collection/upsert', payload); 
      if(r.code===0){ this.$message.success('已保存'); this.editVisible=false; this.loadCollect() } else { this.$message.error(r.msg||'失败') } 
    }
    ,collectText(id){ return this.collect ? '已收藏' : '收藏' }
    ,collectClass(id){ return this.collect ? 'btn-collected' : 'btn-collect' }
    ,async toggleCollect(id){ if(!this.token){ this.$message.warning('请先登录'); return } if(this.collect){ const r = await del('/collection/one?subjectId='+id); if(r.code===0){ this.$message.success('已取消收藏'); this.collect = null } else { this.$message.error(r.msg||'失败') } } else { const r = await post('/collection/upsert', { subjectId:id, status:'WATCHING' }); if(r.code===0){ this.$message.success('已收藏'); this.collect = { subjectId:id, status:'WATCHING' } } else { this.$message.error(r.msg||'失败') } } }
    ,async deleteTopic(){ try{ const r = await del('/discussion/topic/'+this.id); if(r.code===0){ this.$message.success('已删除'); this.$router.push('/forum') } else { this.$message.error(r.msg||'失败') } }catch(e){ this.$message.error(e.message||'失败') } }
  }
  ,watch:{
    '$route.params.id': {
      immediate: false,
      handler(val){ this.setTopicIdAndLoad(val) }
    }
  }
}
</script>
<style scoped>
.topic-page { 
  display: grid; 
  grid-template-columns: 1fr 320px; 
  gap: 24px; 
}

.topic-detail-card {
  padding: 24px;
  margin-bottom: 24px;
}

.topic-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border-color);
}

.topic-title {
  font-size: 24px;
  font-weight: 800;
  color: var(--text-main);
  margin: 0 0 12px 0;
  line-height: 1.3;
}

.topic-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--text-secondary);
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.meta-item.author {
  cursor: pointer;
  color: var(--text-main);
  font-weight: 600;
}

.meta-item.author:hover {
  color: #6366f1;
}

.author-avatar-mini {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.author-avatar-mini img { width: 100%; height: 100%; object-fit: cover; }

.meta-item.subject-link {
  cursor: pointer;
  color: #6366f1;
  font-weight: 600;
}

.meta-sep { color: #cbd5e1; }

.topic-content {
  font-size: 16px;
  line-height: 1.8;
  color: var(--text-main);
  white-space: pre-wrap;
}

/* 回复列表 */
.replies-card {
  padding: 24px;
}

.replies-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-color);
}

.replies-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 700;
  color: var(--text-main);
}

.replies-tools {
  display: flex;
  align-items: center;
  gap: 12px;
}

.comments-list {
  display: flex;
  flex-direction: column;
}

.comment-item {
  padding: 24px 0;
  border-bottom: 1px solid var(--border-color);
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-main {
  display: flex;
  gap: 16px;
}

.comment-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  flex-shrink: 0;
  cursor: pointer;
}

.comment-avatar img { width: 100%; height: 100%; object-fit: cover; }

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #6366f1;
  color: #fff;
  font-weight: bold;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.comment-author {
  font-weight: 700;
  color: var(--text-main);
  font-size: 15px;
  cursor: pointer;
}

.comment-author:hover { color: #6366f1; }

.comment-floor {
  font-size: 12px;
  color: var(--text-secondary);
  opacity: 0.7;
}

.comment-sign {
  font-size: 12px;
  color: var(--text-secondary);
  opacity: 0.7;
  margin-bottom: 8px;
}

.comment-text {
  font-size: 15px;
  line-height: 1.6;
  color: var(--text-main);
  margin-bottom: 12px;
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-time {
  font-size: 12px;
  color: var(--text-secondary);
  opacity: 0.7;
}

.comment-ops {
  display: flex;
  align-items: center;
  gap: 16px;
}

.op-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.op-item:hover { color: #6366f1; }

.op-item.like.liked {
  color: #ef4444;
}

.op-item.reply-link {
  font-weight: 600;
}

/* 子回复 */
.child-replies {
  margin-top: 16px;
  margin-left: 64px;
  padding: 16px;
  background: var(--bg-color);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.child-item {
  display: flex;
  gap: 12px;
}

.child-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  flex-shrink: 0;
  cursor: pointer;
}

.child-avatar img { width: 100%; height: 100%; object-fit: cover; }

.avatar-placeholder-mini {
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

.child-body {
  flex: 1;
  min-width: 0;
}

.child-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2px;
}

.child-author {
  font-weight: 600;
  color: var(--text-main);
  font-size: 14px;
  cursor: pointer;
}

.child-floor {
  font-size: 11px;
  color: var(--text-secondary);
  opacity: 0.5;
}

.child-text {
  font-size: 14px;
  color: var(--text-main);
  margin-bottom: 8px;
}

.child-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.child-time {
  font-size: 11px;
  color: var(--text-secondary);
  opacity: 0.7;
}

.child-ops {
  display: flex;
  align-items: center;
  gap: 12px;
}

.op-item.mini {
  font-size: 12px;
}

/* 侧边栏卡片 */
.subject-side-card {
  cursor: pointer;
  transition: all 0.3s;
}

.subject-side-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.08);
}

.mini-card-header {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-secondary);
  margin-bottom: 16px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.mini-card-content {
  display: flex;
  gap: 16px;
}

.mini-cover {
  width: 80px;
  height: 112px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  flex-shrink: 0;
}

.mini-cover img { width: 100%; height: 100%; object-fit: cover; }

.mini-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.mini-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-main);
  margin: 0 0 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.mini-sub {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.mini-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 12px;
}

.mini-tag { border-radius: 4px; }
.mini-episodes { font-size: 12px; color: #94a3b8; }

.mini-actions {
  margin-top: auto;
}

.mini-collect-btn {
  width: 100%;
}

.pager { 
  margin-top: 24px; 
  display: flex; 
  justify-content: flex-end; 
}
</style>
