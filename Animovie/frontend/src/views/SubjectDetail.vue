<!-- 页面：动漫详情 -->
<template>
  <div class="page-container subject-detail-page">
    <div class="columnA">
      <el-card class="modern-card detail-side-card">
        <div class="cover">
          <img v-if="subject.coverPath" :src="base+subject.coverPath" alt="cover" />
          <div v-else class="placeholder">No Cover</div>
        </div>
        <div class="info">
          <h2 class="title">{{ subject.cnName }}</h2>
          <div class="sub" v-if="subject.jpName || subject.alias">
            {{ subject.jpName }} 
            <span v-if="subject.alias" class="alias">/ {{ subject.alias }}</span>
          </div>
          
          <div class="detail-info-list">
            <div class="info-item">
              <el-icon><Collection /></el-icon>
              <span class="label">类型</span>
              <el-tag size="small" effect="plain" class="info-tag">{{ subject.category }}</el-tag>
            </div>
            
            <div class="info-item">
              <el-icon><VideoPlay /></el-icon>
              <span class="label">话数</span>
              <span class="value">{{ subject.episodes }} 话</span>
            </div>
            
            <div class="info-item" v-if="subject.firstAirDate">
              <el-icon><Calendar /></el-icon>
              <span class="label">开播</span>
              <span class="value">{{ subject.firstAirDate }}</span>
            </div>
            
            <div class="info-item" v-if="subject.studio">
              <el-icon><OfficeBuilding /></el-icon>
              <span class="label">制作</span>
              <span class="value">{{ subject.studio }}</span>
            </div>

            <div class="info-item" v-if="subject.country">
              <el-icon><Location /></el-icon>
              <span class="label">地区</span>
              <span class="value">{{ subject.country }}</span>
            </div>
            
            <div class="info-item site" v-if="subject.officialSite">
              <el-icon><Link /></el-icon>
              <span class="label">官网</span>
              <el-link :href="subject.officialSite" target="_blank" type="primary" :underline="false">
                点击访问
              </el-link>
            </div>
          </div>

          <div class="tags-section" v-if="subject.tags">
            <div class="section-label">标签</div>
            <div class="tag-cloud">
              <el-tag 
                v-for="tag in (subject.tags || '').split(/[ ,，]/).filter(t => t.trim())" 
                :key="tag"
                size="small"
                class="user-tag"
                round
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>

          <div class="admin-actions" v-if="role==='ADMIN'">
            <el-button type="primary" plain size="small" icon="Edit" @click="openSubjectEdit()">编辑条目</el-button>
            <el-popconfirm title="确认删除条目？" @confirm="removeSubject()">
              <template #reference>
                <el-button type="danger" plain size="small" icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>
      </el-card>
    </div>
    <div class="columnB">
      <el-card class="modern-card progress-card">
        <h3 class="panelTitle">观看进度</h3>
        <ul class="prg_list">
          <li v-for="i in (subject.episodes || 12)" :key="i">
            <el-popover placement="top" trigger="hover" :width="275">
              <template #reference>
                <div class="box" :class="boxClass(i)">{{ i }}</div>
              </template>
              <div class="popActions">
                <el-button size="small" type="primary" @click="saveProgress('WATCHED', i)">看过</el-button>
                <el-button size="small" @click="saveProgress('WATCHING', i)">在看</el-button>
                <el-button size="small" type="warning" @click="saveProgress('DROPPED', i)">抛弃</el-button>
                <el-button size="small" type="info" @click="resetEpisode(i)">重置</el-button>
              </div>
            </el-popover>
          </li>
        </ul>
      </el-card>
      <el-card class="modern-card comments-card" style="margin-top: 24px;">
        <SubjectComments v-if="id!==null && Number.isFinite(id)" :subjectId="id" />
      </el-card>
    </div>
    <div class="columnC">
      <el-card class="modern-card rating-card">
        <h3 class="panelTitle">评分概览</h3>
        <div class="avgRow">平均分：<span class="avgNum">{{ ratingAvg }}</span>（{{ ratingTotal }} 人评分）</div>
        <div class="bars">
          <div class="barRow" v-for="n in 10" :key="n">
            <span class="label">{{ 11 - n }}</span>
            <div class="bar"><div class="fill" :style="{ width: barWidth(11-n) }"></div></div>
            <span class="count">{{ countAt(11-n) }}</span>
          </div>
        </div>
      </el-card>
      <el-card class="modern-card collection-card" style="margin-top: 24px;">
        <h3 class="panelTitle">收藏状态</h3>
        <el-form :model="form" label-position="top">
          <el-form-item label="观看状态">
            <el-radio-group v-model="form.status" class="status-radio-group mini">
              <el-radio-button label="WATCHING">在看</el-radio-button>
              <el-radio-button label="WATCHED">看过</el-radio-button>
              <el-radio-button label="DROPPED">抛弃</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="当前进度">
            <div style="display:flex; align-items:center; gap:8px">
              <el-input-number 
                v-model="form.progressEpisode" 
                :min="0" 
                :max="subject.episodes || 9999" 
                size="small" 
                controls-position="right" 
              />
              <span style="font-size:13px; color:var(--text-secondary); opacity:0.7">/ {{ subject.episodes || '?' }} 话</span>
            </div>
          </el-form-item>
          <el-form-item label="我的评分">
            <div class="rate-wrapper">
              <el-rate v-model="form.rating" :max="10" show-score text-color="#ff9900" />
            </div>
          </el-form-item>
          <el-form-item label="吐槽一下">
            <el-input
              v-model="form.shortComment"
              type="textarea"
              :rows="4"
              maxlength="500"
              show-word-limit
              placeholder="写下你的简评..."
              class="commentInput"
            />
          </el-form-item>
          <el-form-item label-width="0">
            <div style="display: flex; gap: 8px; width: 100%;">
              <el-button type="primary" @click="save" :disabled="!token" style="flex: 1" round>保存修改</el-button>
              <el-popconfirm title="确认删除收藏？" @confirm="deleteCollection">
                <template #reference>
                  <el-button type="danger" :disabled="!token" round icon="Delete"></el-button>
                </template>
              </el-popconfirm>
            </div>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
  <!-- 编辑条目弹窗 -->
  <el-dialog v-model="subjectEditVisible" title="编辑条目信息" width="680px" class="modern-dialog" append-to-body>
    <div class="dialog-content">
      <el-form :model="subjectEdit" label-position="top">
        <div class="dialog-section-title">基本信息</div>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="中文名">
              <el-input v-model="subjectEdit.cnName" placeholder="条目中文名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="日文名">
              <el-input v-model="subjectEdit.jpName" placeholder="条目原名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="别名">
              <el-input v-model="subjectEdit.alias" placeholder="其他名称，逗号分隔" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类">
              <el-select v-model="subjectEdit.category" style="width: 100%">
                <el-option label="TV 动画" value="TV" />
                <el-option label="OVA / OAD" value="OVA" />
                <el-option label="剧场版 MOVIE" value="MOVIE" />
                <el-option label="漫画 MANGA" value="MANGA" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <div class="dialog-section-title">发布信息</div>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="首播日期">
              <el-date-picker v-model="subjectEdit.firstAirDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总话数">
              <el-input-number v-model="subjectEdit.episodes" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="制作公司">
              <el-input v-model="subjectEdit.studio" placeholder="制作公司名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="制片国家/地区">
              <el-input v-model="subjectEdit.country" placeholder="如：日本、中国" />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="dialog-section-title">扩展信息</div>
        <el-form-item label="官网链接">
          <el-input v-model="subjectEdit.officialSite" placeholder="https://" />
        </el-form-item>

        <el-form-item label="标签">
          <el-input v-model="subjectEdit.tags" placeholder="使用空格或逗号分隔标签" />
        </el-form-item>

        <div class="dialog-section-title">封面设置</div>
        <el-form-item label="">
          <el-upload 
            :action="base+'/subject/cover'" 
            :headers="uploadHeaders" 
            :name="'file'" 
            :data="{ cnName: subjectEdit.cnName, oldPath: subjectEdit.coverPath }" 
            list-type="picture-card" 
            :limit="1" 
            :file-list="subjectEditUploadList" 
            :on-success="onSubjectCoverUploaded"
            class="modern-upload"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="subjectEditVisible=false" round>取消</el-button>
        <el-button type="primary" @click="updateSubject" round class="submit-btn">保存修改</el-button>
      </div>
    </template>
  </el-dialog>
  </template>
<script>
import { get, post, BASE, del } from '../utils/api'
import SubjectComments from '../components/SubjectComments.vue'
import { Delete, Collection, VideoPlay, Calendar, OfficeBuilding, Location, Link, Edit, Plus } from '@element-plus/icons-vue'

export default {
  components:{ SubjectComments, Delete, Collection, VideoPlay, Calendar, OfficeBuilding, Location, Link, Edit, Plus },
  data(){ return { id: null, subject:{}, base: BASE, token:'', role: localStorage.getItem('role')||'', form:{ status:'', progressEpisode:0, rating:null, shortComment:'' }, droppedEpisodes:[], watchingEpisode:null, resetEpisodes:[], ratingCounts:[], ratingAvg:0, ratingTotal:0, subjectEditVisible:false, subjectEdit:{}, uploadHeaders: { Authorization: 'Bearer '+(localStorage.getItem('token')||'') }, subjectEditUploadList:[] } },
  async mounted(){
    this.id = Number(this.$route.params.id)
    if(!Number.isFinite(this.id)){ this.id = parseInt(this.$route.params.id, 10) }
    await this.loadData()
  },
  watch: {
    // 监听路由参数变化，当id变化时重新加载数据
    '$route.params.id': {
      handler(newId) {
        this.id = Number(newId)
        if(!Number.isFinite(this.id)){ this.id = parseInt(newId, 10) }
        this.loadData()
      },
      immediate: true
    }
  },
  methods: {
    // 加载数据的通用方法
    async loadData() {
      this.token = localStorage.getItem('token') || ''
      const r = await get('/subject/'+this.id)
      if(r.code===0) this.subject = r.data || {}
      await this.loadRatingStats()
      const c = await get('/collection/one', { subjectId:this.id })
      if(c.code===0 && c.data){
        this.form = { status:c.data.status||'', progressEpisode:c.data.progressEpisode||0, rating:c.data.rating||null, shortComment:c.data.shortComment||'' }
        if(this.form.status==='DROPPED' && this.form.progressEpisode){
          this.droppedEpisodes = [Number(this.form.progressEpisode)]
          this.form.status=''
        }
      } else {
        // 如果没有收藏记录，重置表单
        this.form = { status:'', progressEpisode:0, rating:null, shortComment:'' }
        this.droppedEpisodes = []
        this.watchingEpisode = null
        this.resetEpisodes = []
      }
    },
    async loadRatingStats(){ try{ const s = await get('/subject/rating-stats', { subjectId:this.id }); if(s.code===0){ const arr = s.data.counts || []; this.ratingCounts = Array.isArray(arr)? arr : []; this.ratingAvg = Number(s.data.avg||0).toFixed(1); this.ratingTotal = s.data.total||0 } }catch(e){} },
    countAt(n){ const i = Number(n); if(!Array.isArray(this.ratingCounts)) return 0; return this.ratingCounts[i]||0 },
    barWidth(n){ const max = Math.max(...(this.ratingCounts||[])); const c = this.countAt(n); const pct = max>0 ? Math.round(c*100/max) : 0; return pct+"%" },
    async save(){
      if (this.form.progressEpisode > this.subject.episodes && this.subject.episodes > 0) {
        this.$message.warning(`当前进度不能超过总话数 (${this.subject.episodes})`)
        return
      }
      const payload = Object.assign({ subjectId:this.id }, this.form) // this.id=1 (动漫ID)，payload = {subjectId:1, status:'WATCHING', progressEpisode:5, rating:8, shortComment:'好看'}
      // payload大概长这样：
      // { subjectId: 123, status: "WATCHING", progressEpisode: 12, rating: 9, shortComment: "好看" }
      const r = await post('/collection/upsert', payload) // post 来自 api.js，发 JSON POST
      if(r.code===0){ this.$message.success('已保存') } else { this.$message.error(r.msg||'失败') } //成功或失败弹窗
    }
    ,async deleteCollection(){
      const r = await del('/collection/one?subjectId='+this.id)
      if(r.code===0){
        this.$message.success('已删除收藏')
        this.form = { status:'', progressEpisode:0, rating:null, shortComment:'' }
        this.droppedEpisodes = []
        this.watchingEpisode = null
      }else{
        this.$message.error(r.msg||'删除失败')
      }
    }
    ,boxClass(i){
      const p = Number(this.form.progressEpisode || 0)
      if(this.watchingEpisode===i) return 'watching'
      if(this.resetEpisodes.includes(i)) return 'light'
      if(this.droppedEpisodes.includes(i)) return 'gray'
      if(i<=p) return 'deep'
      return 'light'
    }
    ,async saveProgress(status, episode){ //比如 status='WATCHING' (在看状态), episode=5 (在看第几集)
      if(status==='DROPPED'){
        if(!this.droppedEpisodes.includes(episode)) this.droppedEpisodes.push(episode)
        if(this.watchingEpisode===episode) this.watchingEpisode = null
        this.$message.success('已标记为抛弃（单集）')
        return
      }
      if(status==='WATCHED'){
        this.form.status = 'WATCHED'
        this.form.progressEpisode = episode
        this.watchingEpisode = null
        this.droppedEpisodes = this.droppedEpisodes.filter(e=> e>episode)
        this.resetEpisodes = this.resetEpisodes.filter(e=> e>episode)
        const payload = { subjectId:this.id, status:'WATCHED', progressEpisode:episode, rating:this.form.rating, shortComment:this.form.shortComment }
        const r = await post('/collection/upsert', payload)
        if(r.code===0){ this.$message.success('进度已更新') } else { this.$message.error(r.msg||'失败') }
        return
      }
      if(status==='WATCHING'){
        this.form.status = 'WATCHING'
        this.watchingEpisode = episode
        // 自动推进已看进度：若当前集大于已看进度，则推进
        if(Number(episode) > Number(this.form.progressEpisode || 0)){
          this.form.progressEpisode = Number(episode)
          // 清除已推进范围内的抛弃标记
          this.droppedEpisodes = this.droppedEpisodes.filter(e=> e>Number(episode))
          this.resetEpisodes = this.resetEpisodes.filter(e=> e>Number(episode))
        } else {
          // 仅移除当前集的抛弃标记
          this.droppedEpisodes = this.droppedEpisodes.filter(e=> e!==episode)
        }
        this.resetEpisodes = this.resetEpisodes.filter(e=> e!==episode)
        const payload = { subjectId:this.id, status:'WATCHING', progressEpisode:this.form.progressEpisode, rating:this.form.rating, shortComment:this.form.shortComment }
        const r = await post('/collection/upsert', payload)
        if(r.code===0){ this.$message.success('状态已更新为在看') } else { this.$message.error(r.msg||'失败') }
        return
      }
    }
    ,resetEpisode(episode){
      this.droppedEpisodes = this.droppedEpisodes.filter(e=> e!==episode)
      if(this.watchingEpisode===episode) this.watchingEpisode = null
      if(!this.resetEpisodes.includes(episode)) this.resetEpisodes.push(episode)
      this.$message.success('已重置本集状态')
    }
    ,openSubjectEdit(){ this.subjectEditVisible=true; this.subjectEdit = { ...this.subject }; this.subjectEditUploadList=[] }
    ,async updateSubject(){ try{ const r = await fetch(this.base+'/subject', { method:'PUT', headers:{ 'Content-Type':'application/json', 'Authorization':'Bearer '+(localStorage.getItem('token')||'') }, body: JSON.stringify(this.subjectEdit) }); const j = await r.json(); if(j.code===0){ this.$message.success('保存成功'); this.subjectEditVisible=false; const rr = await get('/subject/'+this.id); if(rr.code===0){ this.subject = rr.data||{} } } else { this.$message.error(j.msg||'失败') } }catch(e){ this.$message.error(e.message||'失败') } }
    ,onSubjectCoverUploaded(resp){ if(resp && resp.code===0){ this.subjectEdit.coverPath = resp.data } else if (typeof resp==='string'){ this.subjectEdit.coverPath = resp } }
    ,async removeSubject(){ try{ const r = await fetch(this.base+'/subject/'+this.id, { method:'DELETE', headers:{ 'Authorization':'Bearer '+(localStorage.getItem('token')||'') } }); const j = await r.json(); if(j.code===0){ this.$message.success('删除成功'); setTimeout(()=> this.$router.push('/subject'), 500) } else { this.$message.error(j.msg||'失败') } }catch(e){ this.$message.error(e.message||'失败') } }
  }
}
</script>
<style scoped>
/* 条目详情页：三栏布局容器 */
.subject-detail-page { display:grid; grid-template-columns: 280px 1fr 320px; gap:24px }
/* 左侧信息列 */
.columnA { display:flex; flex-direction:column; gap:24px }
/* 条目封面与图片 */
.cover { 
  width:100%; 
  aspect-ratio: 260/368; 
  overflow:hidden; 
  border-radius:12px; 
  background: var(--bg-color); 
  border:1px solid var(--border-color); 
  box-shadow: 0 4px 12px rgba(0,0,0,0.05); 
}
.cover img { width:100%; height:100%; object-fit:cover; display:block }
/* 基本信息卡片 */
.detail-side-card { padding: 0; }
.info { padding:20px }
/* 标题与副标题 */
.title { margin:0 0 8px; font-size:22px; font-weight: 700; color: var(--text-main); line-height: 1.3; }
.sub { color: var(--text-secondary); font-size:14px; margin-bottom:20px; line-height: 1.4; }
.sub .alias { color: var(--text-secondary); opacity: 0.7; font-weight: 400; }

/* 详细信息列表 */
.detail-info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: var(--text-secondary);
}

.info-item .el-icon {
  font-size: 16px;
  color: var(--text-secondary);
  opacity: 0.6;
}

.info-item .label {
  color: var(--text-secondary);
  opacity: 0.6;
  font-weight: 600;
  width: 32px;
  flex-shrink: 0;
}

.info-item .value {
  color: var(--text-main);
  font-weight: 500;
}

.info-tag {
  border-radius: 4px;
}

/* 标签区块 */
.tags-section {
  margin-bottom: 24px;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
}

.section-label {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-secondary);
  opacity: 0.6;
  margin-bottom: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.user-tag {
  background: var(--bg-color);
  border-color: var(--border-color);
  color: var(--text-secondary);
  transition: all 0.2s;
}

.user-tag:hover {
  background: var(--border-color);
  color: var(--primary-color);
  border-color: var(--primary-color);
  transform: translateY(-1px);
}

/* 管理员操作 */
.admin-actions {
  display: flex;
  gap: 10px;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
}

/* 观看进度面板与集数按钮 */
.panelTitle { margin:0 0 16px; font-size:18px; font-weight: 600; color: var(--text-main); display: flex; align-items: center; gap: 8px; }
.panelTitle::before { content: ''; width: 4px; height: 16px; background: var(--primary-color); border-radius: 2px; }
.prg_list { list-style:none; padding:0; margin:0; display:flex; flex-wrap:wrap; gap:8px }
.box { width:32px; height:32px; border-radius:6px; display:flex; align-items:center; justify-content:center; font-size:13px; font-weight: 600; color:#fff; cursor:pointer; transition: all 0.2s; }
.box:hover { transform: scale(1.1); filter: brightness(1.1); }
.box.light { background: var(--border-color); color: var(--text-secondary); }
.box.deep { background: var(--primary-color); }
.box.watching { background:#F56C6C; }
.box.gray { background: var(--text-secondary); opacity: 0.6; }
.avgRow { font-size:15px; color: var(--text-secondary); margin-bottom:16px; background: var(--bg-color); padding: 12px 16px; border-radius: 12px; border: 1px solid var(--border-color); }
.avgNum { font-weight:800; color: #f59e0b; font-size: 24px; margin-left:4px; margin-right:4px }
/* 评分分布柱状图 */
.bars { display:flex; flex-direction:column; gap:8px }
.barRow { display:grid; grid-template-columns: 24px 1fr 40px; gap:12px; align-items:center }
.label { color: var(--text-secondary); font-size:12px; font-weight: 600; }
.bar { height:8px; background: var(--bg-color); border-radius:4px; overflow:hidden; border: 1px solid var(--border-color); }
.fill { height:100%; background: linear-gradient(90deg, var(--primary-color), #67C23A); border-radius: 4px; }
.count { color: var(--text-secondary); opacity: 0.7; font-size:12px; text-align: right; }
</style>
