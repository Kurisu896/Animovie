<template>
  <div class="page">
    <div class="toolbar">
      <h3 class="heading">我的话题</h3>
      <div class="tools"></div>
    </div>
    <div class="browser">
      <div class="item" v-for="row in items" :key="row.id" @click="goTopic(row.id)">
        <div class="thumb">
          <img v-if="subject(row.subjectId).coverPath" :src="base+subject(row.subjectId).coverPath" alt="cover" />
          <div v-else class="placeholder">No Cover</div>
        </div>
        <div class="entry">
          <div class="title">{{ row.title }}</div>
          <div class="subj">{{ subject(row.subjectId).cnName || ('条目 '+row.subjectId) }}</div>
          <div class="meta">
            <span>回复 {{ row.replyCount || 0 }}</span>
            <span class="sep">·</span>
            <span>更新时间 {{ row.updatedAt }}</span>
            <span class="sep">·</span>
            <span class="status" :class="{ locked: row.status==='LOCKED', open: row.status==='OPEN', rejected: row.status==='REJECTED' }">
              {{ row.status==='LOCKED' ? '待审核' : (row.status==='REJECTED' ? '已打回' : '已通过') }}
            </span>
            <el-button v-if="row.status==='REJECTED'" size="small" style="margin-left:8px" @click.stop="openEdit(row)">重新编辑</el-button>
            <el-popconfirm title="确认删除该话题？" @confirm="deleteTopic(row.id)">
              <template #reference>
                <el-button size="small" type="danger" style="margin-left:8px" @click.stop>删除话题</el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>
      </div>
    </div>
    <div class="pager">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page="pageNum" @current-change="(p)=>{ pageNum=p; load() }" />
    </div>
    <el-dialog v-model="editVisible" title="重新编辑话题" width="520px">
      <el-form :model="editForm" label-width="90px">
        <el-form-item label="标题"><el-input v-model="editForm.title" /></el-form-item>
        <el-form-item label="内容"><el-input type="textarea" v-model="editForm.content" :rows="6" /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitEdit">提交</el-button>
          <el-button @click="editVisible=false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>
<script>
import { get, BASE, formAuth, del } from '../utils/api'
export default {
  data(){ return { items:[], total:0, pageNum:1, pageSize:10, base: BASE, subjectsById:{}, editVisible:false, editForm:{ id:null, title:'', content:'' } } },
  mounted(){ this.load() },
  methods:{
    async load(){ const r = await get('/discussion/my', { pageNum:this.pageNum, pageSize:this.pageSize }); if(r.code===0){ this.items = r.data.items||[]; this.total = r.data.total||0; await this.loadSubjects() } },
    async loadSubjects(){ const ids=[...new Set(this.items.map(x=> x.subjectId))]; for(const id of ids){ if(!this.subjectsById[id]){ const s=await get('/subject/'+id); if(s.code===0 && s.data){ this.subjectsById[id]=s.data } } } },
    subject(id){ return this.subjectsById[id] || {} },
    goTopic(id){ if(!id) return; this.$router.push('/discussion/topic/'+id) },
    openEdit(row){ this.editForm = { id: row.id, title: row.title||'', content: row.content||'' }; this.editVisible = true },
    async submitEdit(){ const r = await formAuth('/discussion/topic/update', { topicId:this.editForm.id, title:this.editForm.title, content:this.editForm.content }); if(r.code===0){ this.$message.success('已提交，等待审核'); this.editVisible=false; this.load() } else { this.$message.error(r.msg||'失败') } }
    ,async deleteTopic(id){ const r = await del('/discussion/topic/'+id); if(r.code===0){ this.$message.success('已删除'); this.items = this.items.filter(x=> x.id!==id); this.total = Math.max(0,(this.total||0)-1) } else { this.$message.error(r.msg||'失败') } }
  }
}
</script>
<style scoped>
.page { max-width:1040px; margin:0 auto }
.toolbar { display:flex; justify-content:space-between; align-items:center; margin-bottom:10px }
.heading { margin:0 }
.tools { display:flex; gap:8px; align-items:center }
.browser { display:block }
.item { display:grid; grid-template-columns: 120px 1fr; gap:12px; background:#fff; border:1px solid #e5e7eb; border-radius:8px; box-shadow:0 1px 2px rgba(0,0,0,0.06); padding:12px; margin-bottom:10px; cursor:pointer }
.thumb { width:120px; height:170px; overflow:hidden; border-radius:4px; background:#f3f4f6 }
.thumb img { width:100%; height:100%; object-fit:cover; display:block }
.placeholder { width:100%; height:100%; display:flex; align-items:center; justify-content:center; color:#9ca3af }
.entry { flex:1 }
.title { margin:0 0 4px; font-size:16px; color:#111827 }
.subj { color:#374151; font-size:13px; margin-bottom:4px }
.meta { color:#6b7280; font-size:13px }
.meta .sep { margin:0 6px; color:#9ca3af }
.status.locked { color:#f59e0b }
.status.open { color:#10b981 }
.status.rejected { color:#ef4444 }
.pager { margin-top:8px; display:flex; justify-content:flex-end }
</style>
