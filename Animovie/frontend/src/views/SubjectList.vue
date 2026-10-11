<!-- 页面：动漫条目列表 -->
<template>
  <div class="page-container">
    <h3 class="page-title">影视条目</h3>
    <div class="clean-search-area">
      <!-- 主搜索行：输入框 + 高级筛选开关 + 搜索按钮 + 新增按钮 -->
      <div class="main-search-row">
        <el-input
          v-model="keyword"
          placeholder="输入中文名 / 日文名 / 别名 搜索..."
          clearable
          size="large"
          class="keyword-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <!-- 高级筛选开关 -->
        <div class="filter-toggle" @click="showFilters = !showFilters">
          <el-icon size="16"><Filter /></el-icon>
          <span>高级筛选</span>
          <el-icon :class="{ 'rotated': showFilters }" size="14"><ArrowDown /></el-icon>
        </div>

        <!-- 搜索按钮 -->
        <el-button type="primary" size="large" @click="load" class="compact-btn">
          搜索
        </el-button>

        <!-- 新增条目 -->
        <el-button
          v-if="role==='ADMIN'"
          type="success"
          size="large"
          @click="openCreate"
          class="compact-btn"
        >
          + 新增
        </el-button>
      </div>

      <!-- 高级筛选内容 -->
      <Transition name="slide">
        <div v-show="showFilters" class="advanced-filters">
          <el-select v-model="category" placeholder="分类" clearable style="width:110px">
            <el-option label="TV" value="TV" />
            <el-option label="OVA" value="OVA" />
            <el-option label="MOVIE" value="MOVIE" />
            <el-option label="MANGA" value="MANGA" />
          </el-select>

          <el-select v-model="country" placeholder="国家" clearable style="width:110px">
            <el-option label="日本" value="日本" />
            <el-option label="中国" value="中国" />
            <el-option label="美国" value="美国" />
            <el-option label="其他" value="其他" />
          </el-select>

          <el-date-picker
            v-model="monthDate"
            type="month"
            placeholder="首播年月"
            value-format="YYYY-MM"
            style="width:150px"
          />

          <div class="rating-group">
            <el-input-number v-model="ratingMin" :min="0" :max="10" :step="0.5" size="default" style="width:88px" />
            <span class="sep">~</span>
            <el-input-number v-model="ratingMax" :min="0" :max="10" :step="0.5" size="default" style="width:88px" />
          </div>

          <el-input v-model="tagKeyword" placeholder="标签关键字" clearable style="width:180px" />

          <el-select v-model="viewMode" style="width:130px">
            <el-option label="卡片视图" value="card" />
            <el-option label="列表视图" value="list" />
          </el-select>
        </div>
      </Transition>
    </div>
  

    <div v-if="viewMode==='card'" ref="gridRef" class="grid">
      <div v-for="it in items" :key="it.id" class="section" @click="goSubject(it.id)">
        <div class="cover">
          <img v-if="it.coverPath" :src="base+it.coverPath" alt="cover" />
          <div v-else class="placeholder">No Cover</div>
          
          <!-- 悬浮操作层 -->
          <div class="cover-overlay" v-if="token">
            <el-tooltip :content="collectText(it.id)" placement="top">
              <el-button 
                circle 
                :type="isCollected(it.id) ? 'danger' : 'primary'"
                :icon="isCollected(it.id) ? 'Collection' : 'Plus'" 
                @click.stop="openEdit(it)"
                class="overlay-btn"
              />
            </el-tooltip>
          </div>
        </div>

        <div class="info">
          <div class="title-row">
            <el-tooltip :content="it.cnName" placement="top" :disabled="it.cnName.length < 10">
              <h3 class="title">{{ it.cnName }}</h3>
            </el-tooltip>
            <el-tag size="small" effect="dark" class="category-tag">{{ it.category }}</el-tag>
          </div>
          <div class="sub" :title="it.alias ? (it.jpName + ' / ' + it.alias) : it.jpName">
            {{ it.jpName }} <span v-if="it.alias">/ {{ it.alias }}</span>
          </div>
          
          <div class="meta-tags">
            <span class="meta-item"><el-icon><VideoPlay /></el-icon> {{ it.episodes }} 话</span>
            <span class="meta-item" v-if="it.firstAirDate"><el-icon><Calendar /></el-icon> {{ it.firstAirDate }}</span>
          </div>

          <div class="extra-info">
            <div class="info-line" v-if="it.studio || it.country">
              <span class="label">制作:</span>
              <span class="value">{{ it.studio }} <span v-if="it.country">/ {{ it.country }}</span></span>
            </div>
            <div class="info-line" v-if="it.tags">
              <span class="label">标签:</span>
              <span class="value">{{ it.tags }}</span>
            </div>
          </div>

          <div class="rating-stats" v-if="ratingStatsById[it.id]">
            <span class="score">{{ ratingStatsById[it.id].avg }}</span>
            <span class="total">({{ ratingStatsById[it.id].total }} 人评分)</span>
          </div>

          <div class="admin-actions" v-if="role==='ADMIN'">
            <el-divider direction="vertical" />
            <el-tooltip content="编辑条目" placement="top">
              <el-button link type="primary" icon="Edit" @click.stop="openSubjectEdit(it)" />
            </el-tooltip>
            <el-tooltip content="删除条目" placement="top">
              <el-popconfirm title="确认删除条目？" @confirm="removeSubject(it.id)">
                <template #reference>
                  <el-button link type="danger" icon="Delete" @click.stop />
                </template>
              </el-popconfirm>
            </el-tooltip>
          </div>
        </div>
      </div>
    </div>

    <!-- 翻页（卡片视图） -->
    <div v-if="viewMode==='card'" class="pager-row">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="pageNum"
        @current-change="(p)=>{ pageNum=p; load() }"
      />
    </div>

    <div v-else class="modern-card table-card">
      <el-table :data="items" border>
        <el-table-column label="封面" width="160">
          <template #default="{ row }">
            <div class="cover cover-sm">
              <img v-if="row.coverPath" :src="base+row.coverPath" alt="cover" />
              <div v-else class="placeholder">No Cover</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="cnName" label="中文名" />
        <el-table-column prop="jpName" label="日文名" />
        <el-table-column prop="alias" label="别名" />
        <el-table-column prop="category" label="分类" />
        <el-table-column prop="episodes" label="话数" />
        <el-table-column prop="studio" label="制作公司" />
        <el-table-column prop="officialSite" label="官网" />
        <el-table-column prop="firstAirDate" label="首播日期" />
        <el-table-column prop="country" label="国家/地区" />
        <el-table-column prop="tags" label="标签" />
        <el-table-column label="平均分">
          <template #default="{ row }">
            <span>{{ avgText(row.id) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260">
          <template #default="{ row }">
            <div class="tActions" style="display:flex; gap:8px; align-items:center">
              <el-button size="small" :class="collectClass(row.id)" @click.stop="openEdit(row)">{{ collectText(row.id) }}</el-button>
              <template v-if="role==='ADMIN'">
                <el-button size="small" @click.stop="openSubjectEdit(row)">编辑</el-button>
                <el-popconfirm title="确认删除条目？" @confirm="removeSubject(row.id)">
                  <template #reference>
                    <el-button size="small" type="danger" @click.stop>删除</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager-row">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page="pageNum"
          @current-change="(p)=>{ pageNum=p; load() }"
        />
      </div>
    </div>
    <el-alert v-if="error" :title="error" type="error" show-icon closable style="margin-top:8px" />
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

    <!-- 新增条目弹窗 -->
    <el-dialog v-model="createVisible" title="新增条目" width="680px" class="modern-dialog" append-to-body>
      <div class="dialog-content">
        <el-form :model="create" label-position="top">
          <div class="dialog-section-title">基本信息</div>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="中文名">
                <el-input v-model="create.cnName" placeholder="必填" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="日文名">
                <el-input v-model="create.jpName" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="别名">
                <el-input v-model="create.alias" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="分类">
                <el-select v-model="create.category" style="width: 100%">
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
                <el-date-picker v-model="create.firstAirDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="总话数">
                <el-input-number v-model="create.episodes" :min="0" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="制作公司">
                <el-input v-model="create.studio" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="制片国家/地区">
                <el-input v-model="create.country" />
              </el-form-item>
            </el-col>
          </el-row>

          <div class="dialog-section-title">扩展信息</div>
          <el-form-item label="官网链接">
            <el-input v-model="create.officialSite" />
          </el-form-item>

          <el-form-item label="标签">
            <el-input v-model="create.tags" />
          </el-form-item>

          <div class="dialog-section-title">封面设置</div>
          <el-form-item label="">
            <el-upload 
              :action="base+'/subject/cover'" 
              :headers="uploadHeaders" 
              :name="'file'" 
              :data="{ cnName: create.cnName }" 
              list-type="picture-card" 
              :limit="1" 
              :file-list="createUploadList" 
              :on-success="onCreateCoverUploaded"
              class="modern-upload"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="createVisible=false" round>取消</el-button>
          <el-button type="primary" @click="createSubmit" round class="submit-btn">创建条目</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script>
import { get, post, BASE } from '../utils/api'
import { useAuthStore } from '../store'
import { Search, Filter, ArrowDown, VideoPlay, Calendar, Collection, Plus, Edit, Delete } from '@element-plus/icons-vue'

export default {
  components: { Search, Filter, ArrowDown, VideoPlay, Calendar, Collection, Plus, Edit, Delete },
  data(){ 
    return { 
      keyword:'', 
      category:'', 
      country:'', 
      monthDate:'', 
      ratingMin:null, 
      ratingMax:null, 
      tagKeyword:'', 
      items:[], 
      total:0, 
      pageNum:1, 
      pageSize:9, 
      error:'', 
      base: BASE, 
      viewMode:'card', 
      gridRef:null, 
      ro:null, 
      form:{}, 
      token:'', 
      role: localStorage.getItem('role')||'', 
      editVisible:false, 
      editSubjectId:null, 
      editForm:{ status:'', progressEpisode:0, rating:null, shortComment:'', maxEpisodes:0 }, 
      subjectEditVisible:false, 
      subjectEdit:{}, 
      uploadHeaders: { Authorization: 'Bearer '+(localStorage.getItem('token')||'') }, 
      subjectEditUploadList:[], 
      createVisible:false, 
      create:{ cnName:'', jpName:'', alias:'', firstAirDate:'', category:'TV', episodes:12, studio:'', country:'', officialSite:'', tags:'', coverPath:'' }, 
      createUploadList:[], 
      ratingStatsById:{},
      showFilters: false   // 新增：控制高级筛选展开
    } 
  },
  mounted(){
    window.addEventListener('resize', this.onResize);
    this.$nextTick(()=>{ this.attachObserver(); this.computePageSize(true) });
    this.token = localStorage.getItem('token') || ''
  },
  beforeUnmount(){ window.removeEventListener('resize', this.onResize) },
  methods:{
    onResize(){ this.computePageSize() },
    attachObserver(){
      try{
        if(this.ro){ this.ro.disconnect() }
        const el = this.$refs.gridRef
        if(el){
          this.ro = new ResizeObserver(()=> this.computePageSize())
          this.ro.observe(el)
        }
      }catch(e){}
    },
    computePageSize(initial=false){
      this.$nextTick(()=>{
        try{
          if(this.viewMode==='card'){
            const size = 9
            if(size !== this.pageSize){ this.pageSize = size; this.pageNum = 1 }
            if(initial){ this.load() }
            return
          }
          const el = this.$refs.gridRef;
          if(!el){ return }
          const rect = el.getBoundingClientRect();
          const width = rect.width;
          const height = window.innerHeight - rect.top - 160;
          const cardW = 320, gap = 12, cardH = 200;
          const cols = Math.max(1, Math.floor((width + gap) / (cardW + gap)));
          const rows = Math.max(1, Math.floor((height + gap) / (cardH + gap)));
          const size = Math.max(1, cols * rows);
          if(size && size !== this.pageSize){ this.pageSize = size; this.pageNum = 1 }
          if(initial){ this.load() }
        }catch(e){ if(initial){ this.load() } }
      })
    },
    async load(){
      this.error=''
      try{
        let y=null, m=null;
        if(this.monthDate){ const parts = String(this.monthDate).split('-'); if(parts.length===2){ y = Number(parts[0]); m = Number(parts[1]) } }
        const r = await get('/subject', { keyword:this.keyword, category:this.category, country:this.country, year:y, month:m, ratingMin:this.ratingMin, ratingMax:this.ratingMax, tagKeyword:this.tagKeyword, pageNum:this.pageNum, pageSize:this.pageSize })
        if(r.code===0){
          this.items = r.data.items; this.total = r.data.total
          this.items.forEach(it=>{
            if(!this.form[it.id]) this.form[it.id] = { status:'', progressEpisode:0, rating:null, shortComment:'' }
            this.prefill(it.id)
          })
          await this.loadAvgStats()
        }
        else{ this.error = r.msg || '加载失败' }
      }catch(e){ this.error = e.message }
    },
    async prefill(id){
      try{
        const r = await get('/collection/one', { subjectId:id })
        if(r.code===0 && r.data){ this.form[id] = { status:r.data.status||'', progressEpisode:r.data.progressEpisode||0, rating:r.data.rating||null, shortComment:r.data.shortComment||'' } }
        else{ this.form[id] = { status:'', progressEpisode:0, rating:null, shortComment:'' } }
      }catch(e){ this.form[id] = { status:'', progressEpisode:0, rating:null, shortComment:'' } }
    },
    openEdit(it){
      const id = it.id
      if(!this.form[id]) this.form[id] = { status:'', progressEpisode:0, rating:null, shortComment:'' }
      this.editSubjectId = id
      this.editForm = Object.assign({ maxEpisodes: it.episodes }, this.form[id])
      this.editVisible = true
    },
    async saveEdit(){
      const id = this.editSubjectId
      if (this.editForm.progressEpisode > this.editForm.maxEpisodes && this.editForm.maxEpisodes > 0) {
        this.$message.warning(`当前进度不能超过总话数 (${this.editForm.maxEpisodes})`)
        return
      }
      const payload = Object.assign({ subjectId:id }, this.editForm)
      delete payload.maxEpisodes
      const r = await post('/collection/upsert', payload)
      if(r.code===0){ this.$message.success('已保存'); this.form[id] = Object.assign({}, this.editForm); this.editVisible=false } else { this.$message.error(r.msg||'失败') }
    }
    ,collectText(id){ const f=this.form[id]; return f && f.status ? '已收藏' : '收藏' }
    ,isCollected(id){ const f=this.form[id]; return !!(f && f.status) }
    ,collectClass(id){ const f=this.form[id]; return f && f.status ? 'btn-collected' : 'btn-collect' }
    ,goSubject(id){ this.$router.push('/subject/'+id) }
    ,openSubjectEdit(row){ this.subjectEditVisible=true; this.subjectEdit = { ...row }; this.subjectEditUploadList=[] }
    ,async updateSubject(){ try{ const r = await fetch(this.base+'/subject', { method:'PUT', headers:{ 'Content-Type':'application/json', 'Authorization':'Bearer '+(localStorage.getItem('token')||'') }, body: JSON.stringify(this.subjectEdit) }); const j = await r.json(); if(j.code===0){ this.$message.success('保存成功'); this.subjectEditVisible=false; this.load() } else { this.$message.error(j.msg||'失败') } }catch(e){ this.$message.error(e.message||'失败') } }
    ,async removeSubject(id){ try{ const r = await fetch(this.base+'/subject/'+id, { method:'DELETE', headers:{ 'Authorization':'Bearer '+(localStorage.getItem('token')||'') } }); const j = await r.json(); if(j.code===0){ this.$message.success('删除成功'); this.load() } else { this.$message.error(j.msg||'失败') } }catch(e){ this.$message.error(e.message||'失败') } }
    ,onSubjectCoverUploaded(resp){ if(resp && resp.code===0){ this.subjectEdit.coverPath = resp.data } else if (typeof resp==='string'){ this.subjectEdit.coverPath = resp } }
    ,openCreate(){ this.createVisible=true; this.createUploadList=[] }
    ,onCreateCoverUploaded(resp){ if(resp && resp.code===0){ this.create.coverPath = resp.data } else if (typeof resp==='string'){ this.create.coverPath = resp } }
    ,async createSubmit(){ try{ const r = await fetch(this.base+'/subject', { method:'POST', headers:{ 'Content-Type':'application/json', 'Authorization':'Bearer '+(localStorage.getItem('token')||'') }, body: JSON.stringify(this.create) }); const j = await r.json(); if(j.code===0){ this.$message.success('新增成功'); this.createVisible=false; this.create={ cnName:'', jpName:'', alias:'', firstAirDate:'', category:'TV', episodes:12, studio:'', country:'', officialSite:'', tags:'', coverPath:'' }; this.pageNum=1; this.load() } else { this.$message.error(j.msg||'失败') } }catch(e){ this.$message.error(e.message||'失败') } }
    ,async loadAvgStats(){ try{ const ids = (this.items||[]).map(x=> x.id); for(const id of ids){ if(!this.ratingStatsById[id]){ const s = await get('/subject/rating-stats', { subjectId:id }); if(s.code===0){ const avg = Number(s.data.avg||0).toFixed(1); const total = s.data.total||0; this.ratingStatsById[id] = { avg, total } } } } this.ratingStatsById = { ...this.ratingStatsById } }catch(e){} }
    ,avgText(id){ const st = this.ratingStatsById[id]; if(!st) return '-'; return `${st.avg} 分 / ${st.total} 人` }
  }
}
</script>
<style scoped>
/* 条目列表页：卡片栅格容器 */
.grid { display:grid; grid-template-columns: repeat(3, 1fr); gap:24px }
.page-title { margin-bottom: 32px; }
/* 单个条目卡片容器 */
.section { 
  display:flex; 
  background: var(--card-bg); 
  border:1px solid var(--border-color); 
  border-radius:16px; 
  box-shadow: var(--card-shadow); 
  padding:16px; 
  cursor:pointer;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
  color: var(--text-main);
}
.section:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.15);
  border-color: var(--primary-color);
}

/* 封面大小（卡片/表格） */
.cover { 
  width:120px; 
  height:170px; 
  flex-shrink:0; 
  overflow:hidden; 
  border-radius:10px; 
  background: var(--bg-color);
  position: relative;
}
.cover img { width:100%; height:100%; object-fit:cover; display:block; transition: transform 0.5s; }
.section:hover .cover img { transform: scale(1.05); }

/* 悬浮操作层 */
.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}
.cover:hover .cover-overlay { opacity: 1; }
.overlay-btn { box-shadow: 0 4px 8px rgba(0,0,0,0.2); }

.placeholder { width:100%; height:100%; display:flex; align-items:center; justify-content:center; background: var(--bg-color); color: var(--text-secondary); opacity: 0.6; }

/* 信息区 */
.info { flex:1; padding-left:16px; display: flex; flex-direction: column; min-width: 0; }

.title-row { display: flex; justify-content: space-between; align-items: center; gap: 8px; margin-bottom: 6px; overflow: hidden; }
.title { margin:0; font-size:16px; font-weight: 700; color: var(--text-main); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; flex: 1; line-height: 1.4; }
.category-tag { flex-shrink: 0; border-radius: 6px; }

.sub { color: var(--text-secondary); font-size:13px; margin-bottom:8px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.meta-tags { display: flex; flex-wrap: wrap; gap: 12px; margin-bottom: 8px; }
.meta-item { display: flex; align-items: center; gap: 4px; font-size: 12px; color: var(--text-secondary); }
.meta-item .el-icon { font-size: 14px; color: var(--text-secondary); opacity: 0.7; }

.extra-info { margin-bottom: 4px; display: flex; flex-direction: column; gap: 4px; }
.info-line { font-size: 12px; color: var(--text-secondary); display: flex; gap: 4px; overflow: hidden; }
.info-line .label { font-weight: 600; color: var(--text-secondary); opacity: 0.6; flex-shrink: 0; }
.info-line .value { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.rating-stats { display: flex; align-items: baseline; gap: 6px; }
.rating-stats .score { font-size: 20px; font-weight: 800; color: #f59e0b; }
.rating-stats .total { font-size: 12px; color: var(--text-secondary); opacity: 0.7; }

.admin-actions { margin-top: 12px; display: flex; align-items: center; gap: 4px; }
.admin-actions .el-divider { margin: 0 8px; }

/* 列表模式适配 */
.cover-sm { width:100px; height:140px }

/* ==================== 美化搜索栏样式 ==================== */
.clean-search-area {
  background: var(--card-bg);
  padding: 24px 32px;
  margin-bottom: 32px;
  border-radius: 20px;
  border: 1px solid var(--border-color);
  box-shadow: var(--card-shadow);
}

.main-search-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.keyword-input {
  flex: 1;
  min-width: 280px;
  max-width: 500px;
}

/* 筛选开关（放在中间） */
.filter-toggle {
  display: flex; 
  align-items: center; 
  gap: 6px; 
  padding: 10px 14px; 
  background: var(--bg-color); 
  border: 1px solid var(--border-color); 
  border-radius: 8px; 
  cursor: pointer; 
  font-size: 14px; 
  color: var(--text-secondary); 
  transition: all 0.2s; 
  white-space: nowrap; 
}

.filter-toggle:hover {
  background: var(--border-color);
  color: var(--text-main);
}

.filter-toggle .rotated {
  transform: rotate(180deg);
}

/* 搜索和新增按钮更窄 */
.compact-btn {
  min-width: 78px;
  padding: 0 16px !important;
  font-weight: 600;
}

/* 高级筛选展开区 */
.advanced-filters {
  margin-top: 16px;
  padding-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  align-items: center;
}

.rating-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rating-group .sep {
  color: #94a3b8;
  font-size: 14px;
  padding: 0 4px;
}

.rating-group .el-input-number {width: 120px !important;}

/* 展开动画 */
.slide-enter-active, .slide-leave-active {
  transition: all 0.25s ease;
}
.slide-enter-from, .slide-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

.table-card {
  padding: 24px;
  margin-bottom: 24px;
}

.pager-row {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

/* 手机端换行 */
@media (max-width: 768px) {
  .main-search-row {
    flex-direction: column;
    align-items: stretch;
  }
  .keyword-input,
  .filter-toggle,
  .compact-btn {
    width: 100%;
  }
  .filter-toggle {
    justify-content: center;
  }
}
</style>
