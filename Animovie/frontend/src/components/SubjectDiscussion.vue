<template>
  <div>
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px">
      <h3 style="margin:0">讨论区</h3>
      <el-button type="primary" size="small" @click="createVisible=true">新建话题</el-button>
    </div>
    <el-table :data="items" border>
      <el-table-column prop="title" label="标题">
        <template #default="{ row }">
          <a @click="goTopic(row.id)" style="cursor:pointer; color:#2563eb">{{ row.title }}</a>
        </template>
      </el-table-column>
      <el-table-column prop="replyCount" label="回复" width="100" />
      <el-table-column prop="updatedAt" label="更新时间" width="200" />
    </el-table>
    <div style="margin-top:8px; display:flex; justify-content:flex-end">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page="pageNum" @current-change="(p)=>{ pageNum=p; load() }" />
    </div>

    <el-dialog v-model="createVisible" title="新建话题" width="600">
      <el-form :model="form" label-width="90">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="6" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible=false">取消</el-button>
        <el-button type="primary" @click="create">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script>
import { get, formAuth } from '../utils/api'
export default {
  props:{ subjectId: { type: Number, required: true } },
  data(){ return { items:[], total:0, pageNum:1, pageSize:10, createVisible:false, form:{ title:'', content:'' } } },
  mounted(){ this.load() },
  methods:{
    async load(){
      const r = await get('/discussion/topics', { subjectId:this.subjectId, pageNum:this.pageNum, pageSize:this.pageSize })
      if(r.code===0){ this.items = r.data.items || []; this.total = r.data.total || 0 }
    },
    async create(){
      const r = await formAuth('/discussion/topic', { subjectId:this.subjectId, title:this.form.title, content:this.form.content })
      if(r.code===0){ this.$message.success('已发布'); this.createVisible=false; this.form={ title:'', content:'' }; this.pageNum=1; this.load() } else { this.$message.error(r.msg||'失败') }
    },
    goTopic(id){ this.$router.push('/discussion/topic/'+id) }
  }
}
</script>
