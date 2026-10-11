<!-- 组件：条目评论 -->
<template>
  <div>
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px">
      <h3 style="margin:0">评论区</h3>
      <div style="display:flex; align-items:center; gap:8px">
        <el-select v-model="sort" size="small" style="width:140px" @change="()=>{ pageNum=1; load() }">
          <el-option label="按最新" value="latest" />
          <el-option label="按点赞" value="likes" />
        </el-select>
        <el-button type="primary" size="small" @click="replyVisible=true">发表评论</el-button>
      </div>
    </div>
    <div class="comments">
      <div class="commentItem" v-for="row in items" :key="row.id">
        <div class="avatarBox">
          <div class="avatar" @click="goUser(row.userId)">
            <img v-if="row.avatarPath" :src="base+row.avatarPath" alt="avatar" />
            <div v-else class="avatarPh"></div>
          </div>
        </div>
        <div class="body">
          <div class="authorLine">{{ row.nickname || row.username || '用户' }}</div>
          <div class="sign" v-if="row.signature">{{ row.signature }}</div>
          <div class="text">{{ row.content }}</div>
          <div class="submeta">{{ row.createdAt }}</div>
          <div class="ops">
            <span class="thumb" :class="{ liked: isLiked(row.id) }" @click="toggleLike(row)">❤ {{ row.likeCount }}</span>
            <el-popconfirm v-if="role==='ADMIN' || row.userId===meId" title="确认删除该评论？" @confirm="deleteComment(row)">
              <template #reference>
                <el-button link type="danger" icon="Delete" @click.stop />
              </template>
            </el-popconfirm>
          </div>
        </div>
      </div>
    </div>
    <div style="margin-top:8px; display:flex; justify-content:flex-end">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page="pageNum" @current-change="(p)=>{ pageNum=p; load() }" />
    </div>

    <el-dialog v-model="replyVisible" title="发表评论" width="560px" class="modern-dialog" append-to-body>
      <div class="dialog-content">
        <el-form :model="form" label-position="top">
          <el-form-item label="评论内容">
            <el-input v-model="form.content" type="textarea" :rows="6" placeholder="写下你的评论..." />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="replyVisible=false" round>取消</el-button>
          <el-button type="primary" @click="submit" round class="submit-btn">发布评论</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script>
import { get, formAuth, BASE } from '../utils/api'
import { del } from '../utils/api'
import { Delete } from '@element-plus/icons-vue'

export default {
  components: { Delete },
  props:{ subjectId: { type: Number, required: true } },
  data(){ return { items:[], total:0, pageNum:1, pageSize:20, replyVisible:false, form:{ content:'' }, base: BASE, likedIds:[], usersById:{}, role: localStorage.getItem('role')||'', sort:'latest', meId:null, token: localStorage.getItem('token')||'' } },
  mounted(){ this.load(); this.loadMe() },
  methods:{
    async load(){
      const r = await get('/subject-comments', { subjectId:this.subjectId, pageNum:this.pageNum, pageSize:this.pageSize, sort:this.sort })
      if(r.code===0){ this.items = r.data.items || []; this.total = r.data.total || 0 }
      const ids = [...new Set((this.items||[]).map(x=> x.userId))]
      await this.ensureUsers(ids)
      for(const row of this.items){ const u = this.usersById[row.userId]; if(u){ row.nickname = row.nickname || u.nickname; row.username = row.username || u.username; row.avatarPath = row.avatarPath || u.avatarPath } }
      await this.loadLiked()
    },
    async submit(){
      const r = await formAuth('/subject-comments', { subjectId:this.subjectId, content:this.form.content })
      if(r.code===0){ this.$message.success('已发布'); this.replyVisible=false; this.form={ content:'' }; this.pageNum=1; this.load() } else { this.$message.error(r.msg||'失败') }
    },
    isLiked(id){ return this.likedIds.includes(id) },
    async loadLiked(){
      const t = localStorage.getItem('token')||''
      if(!t){ this.likedIds=[]; return }
      try{
        const r = await get('/subject-comments/liked', { subjectId:this.subjectId })
        if(r.code===0){ this.likedIds = r.data || [] }
      }catch(e){ this.likedIds=[] }
    },
    async toggleLike(row){ if(this.isLiked(row.id)){ const r = await del('/subject-comments/like?id='+row.id); if(r.code===0){ if(row.likeCount>0) row.likeCount--; this.likedIds = this.likedIds.filter(x=> x!==row.id) } } else { const resp = await formAuth('/subject-comments/like', { id:row.id }); if(resp.code===0){ row.likeCount++; this.likedIds = [...this.likedIds, row.id] } else { if((resp.msg||'')==='exists'){ this.likedIds = [...this.likedIds, row.id] } } } }
    ,async fetchUser(id){ if(!id) return; if(this.usersById[id]) return; try{ const u = await get('/auth/user/'+id); if(u.code===0 && u.data){ this.usersById[id] = u.data } }catch(e){} }
    ,async ensureUsers(ids){ for(const id of (ids||[])){ await this.fetchUser(id) } }
    ,goUser(id){ this.$router.push('/user/'+id) }
    ,async loadMe(){ if(!this.token) return; try{ const r = await get('/auth/me'); if(r.code===0){ this.meId = r.data.id || null } }catch(e){} }
    ,async deleteComment(row){ const isAdmin = (this.role==='ADMIN'); const path = isAdmin ? ('/subject-comments/'+row.id) : ('/subject-comments/user/'+row.id); const r = await del(path); if(r.code===0){ this.$message.success('已删除'); this.items = this.items.filter(x=> x.id!==row.id); this.total = Math.max(0,(this.total||0)-1) } else { this.$message.error(r.msg||'失败') } }
  }
}
</script>
<style scoped>
/* 条目详情页评论组件：列表容器 */
.comments { display:block }
/* 评论条目布局 */
.commentItem { display:grid; grid-template-columns: 80px 1fr; gap:12px; padding:16px 0; border-top:1px solid var(--border-color) }
.commentItem:first-child { border-top:none }
/* 头像容器与样式 */
.avatarBox { display:flex; flex-direction:column; align-items:center; gap:8px }
.avatar { width:56px; height:56px; border-radius:50%; overflow:hidden; background: var(--bg-color); border:1px solid var(--border-color); cursor: pointer; transition: transform 0.2s; }
.avatar:hover { transform: scale(1.05); }
.avatar img { width:100%; height:100%; object-fit:cover; display:block }
.avatarPh { width:100%; height:100%; display:flex; align-items:center; justify-content:center; color: var(--text-secondary); opacity: 0.5; }
/* 正文区文本样式 */
.authorLine { color: var(--text-main); font-weight: 700; font-size:14px; margin-bottom:4px }
.sign { color: var(--text-secondary); font-size:12px; margin-bottom:4px; opacity: 0.7; }
.text { color: var(--text-main); font-size:14px; line-height: 1.6; }
.submeta { color: var(--text-secondary); font-size:12px; margin-top:6px; opacity: 0.6; }
/* 操作区与点赞 */
.ops { margin-top:8px; display: flex; align-items: center; gap: 12px; }
.thumb { cursor:pointer; color: var(--text-secondary); user-select:none; font-size: 13px; display: flex; align-items: center; gap: 4px; transition: all 0.2s; }
.thumb:hover { color: var(--primary-color); }
.thumb.liked { color:#ec4899; font-weight: 700; }
.ops .el-button { margin-left: 0; }
</style>
