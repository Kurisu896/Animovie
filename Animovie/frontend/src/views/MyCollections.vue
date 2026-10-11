<!-- 页面：我的收藏 -->
<template>
  <div class="page-container">
    <h3 class="page-title">我的收藏</h3>
    <div class="clean-search-area">
      <!-- 主搜索行：输入框 → 高级筛选开关 → 搜索按钮（无新增按钮） -->
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
        <el-button type="primary" size="large" @click="applyFilter" class="compact-btn">
          搜索
        </el-button>
      </div>

      <!-- 高级筛选区（展开后显示） -->
      <Transition name="slide">
        <div v-show="showFilters" class="advanced-filters">
          <!-- 分类 -->
          <el-select v-model="category" placeholder="分类" clearable style="width:110px">
            <el-option label="TV" value="TV" />
            <el-option label="OVA" value="OVA" />
            <el-option label="MOVIE" value="MOVIE" />
            <el-option label="MANGA" value="MANGA" />
          </el-select>

          <!-- 国家/地区 -->
          <el-select v-model="country" placeholder="国家" clearable style="width:110px">
            <el-option label="日本" value="日本" />
            <el-option label="中国" value="中国" />
            <el-option label="美国" value="美国" />
            <el-option label="其他" value="其他" />
          </el-select>

          <!-- 首播年月 -->
          <el-date-picker
            v-model="monthDate"
            type="month"
            placeholder="首播年月"
            value-format="YYYY-MM"
            style="width:150px"
          />

          <!-- 我的评分范围 -->
          <div class="rating-group">
            <span class="label">我的评分</span>
            <el-input-number v-model="ratingMin" :min="1" :max="10" :step="1" size="default" style="width:88px" />
            <span class="sep">~</span>
            <el-input-number v-model="ratingMax" :min="1" :max="10" :step="1" size="default" style="width:88px" />
          </div>

          <!-- 标签关键字 -->
          <el-input v-model="tagKeyword" placeholder="标签关键字" clearable style="width:180px" />

          <!-- 状态筛选 -->
          <el-select v-model="status" placeholder="状态" clearable style="width:130px">
            <el-option label="在看" value="WATCHING" />
            <el-option label="看过" value="WATCHED" />
            <el-option label="抛弃" value="DROPPED" />
          </el-select>
        </div>
      </Transition>
    </div>
    <div class="grid">
      <div v-for="it in displayItems" :key="it.id" class="section">
        <div class="cover" @click="goDetail(it.subjectId)" style="cursor:pointer">
          <img v-if="subject(it.subjectId).coverPath" :src="base+subject(it.subjectId).coverPath" alt="cover" />
          <div v-else class="placeholder">No Cover</div>
        </div>
        <div class="info fixed">
          <h3 class="title" :title="subject(it.subjectId).cnName || ('条目 '+it.subjectId)" @click="goDetail(it.subjectId)" style="cursor:pointer">{{ subject(it.subjectId).cnName || ('条目 '+it.subjectId) }}</h3>
          <div class="sub" :title="subject(it.subjectId).alias ? (subject(it.subjectId).jpName + ' / ' + subject(it.subjectId).alias) : subject(it.subjectId).jpName">{{ subject(it.subjectId).jpName }} <span v-if="subject(it.subjectId).alias">/ {{ subject(it.subjectId).alias }}</span></div>
          <div class="meta">
            <span class="tag">{{ subject(it.subjectId).category }}</span>
            <span class="sep">/</span>
            <span>{{ subject(it.subjectId).episodes }} 话</span>
            <span v-if="subject(it.subjectId).firstAirDate" class="sep">/</span>
            <span v-if="subject(it.subjectId).firstAirDate">首播 {{ subject(it.subjectId).firstAirDate }}</span>
          </div>
          <div class="status">状态：{{ mapStatus(it.status) }}</div>
          <div class="progress">进度：第 {{ it.progressEpisode }} 集</div>
          <div class="rating">评分：{{ it.rating || '-' }}</div>
          <div class="date" :title="it.updatedDate">更新时间：{{ it.updatedDate }}</div>
          <div class="comment" v-if="it.shortComment" :title="it.shortComment">吐槽：{{ it.shortComment }}</div>
          <div class="actions">
            <el-button size="small" @click.stop="openEdit(it)">编辑</el-button>
            <el-popconfirm title="确认删除该收藏？" @confirm="deleteCollection(it.id)">
              <template #reference>
                <el-button type="danger" size="small" @click.stop>删除</el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>
      </div>
    </div>
    <div style="margin-top:12px;display:flex;justify-content:flex-end">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page="pageNum" @current-change="(p)=>{ pageNum=p; pageChange() }" />
    </div>
    
    <!-- 编辑收藏对话框 -->
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
import { get, BASE, post, del } from '../utils/api'
export default {
  data(){ 
    return { 
      items:[], 
      total:0, 
      pageNum:1, 
      pageSize:12, 
      status:'', 
      keyword:'', 
      category:'', 
      country:'', 
      monthDate:'', 
      ratingMin:null, 
      ratingMax:null, 
      tagKeyword:'', 
      base: BASE, 
      subjectsById:{}, 
      displayItems:[],
      showFilters: false, // 控制高级筛选展开
      editVisible: false, // 编辑对话框可见性
      editForm: { // 编辑表单数据
        id: null,
        status: '',
        progressEpisode: 0,
        rating: null,
        shortComment: '',
        maxEpisodes: 0
      }
    } 
  },
  mounted(){ this.load() },
  methods:{
    async load(){
      const r = await get('/collection/my', { pageNum:this.pageNum, pageSize:this.pageSize })
      if(r.code===0){
        const arr = r.data.items || []
        this.items = arr
        this.total = r.data.total || arr.length
        await this.loadSubjects()
        this.applyFilter()
      }
    },
    async loadSubjects(){
      const ids = [...new Set(this.items.map(x=> x.subjectId))]
      for(const id of ids){
        if(!this.subjectsById[id]){
          const r = await get('/subject/'+id)
          if(r.code===0 && r.data){ this.subjectsById[id] = r.data }
        }
      }
    },
    applyFilter(){
      let src = this.items
      if(this.status) src = src.filter(x=> x.status===this.status)
      if(this.keyword) src = src.filter(x=>{ const s=this.subject(x.subjectId)||{}; const k=this.keyword; return (s.cnName||'').includes(k)||(s.jpName||'').includes(k)||(s.alias||'').includes(k) })
      if(this.category) src = src.filter(x=> (this.subject(x.subjectId).category||'')===this.category)
      if(this.country) src = src.filter(x=> (this.subject(x.subjectId).country||'')===this.country)
      if(this.tagKeyword) src = src.filter(x=> (this.subject(x.subjectId).tags||'').includes(this.tagKeyword))
      if(this.monthDate){ const parts=String(this.monthDate).split('-'); const y=Number(parts[0]); const m=Number(parts[1]); src = src.filter(x=>{ const d=(this.subject(x.subjectId).firstAirDate||''); if(!d) return false; const ps=String(d).split('-'); return Number(ps[0])===y && Number(ps[1])===m }) }
      if(this.ratingMin!=null) src = src.filter(x=> (x.rating||0) >= this.ratingMin)
      if(this.ratingMax!=null) src = src.filter(x=> (x.rating||0) <= this.ratingMax)
      this.displayItems = src
    },
    pageChange(){ this.load() },
    mapStatus(s){ return s==='WATCHING'?'在看': s==='WATCHED'?'看过':'抛弃' }
    ,subject(id){ return this.subjectsById[id] || {} }
    ,goDetail(id){ this.$router.push('/subject/'+id) }
    ,openEdit(it){
      // 打开编辑对话框，填充表单数据
      const s = this.subject(it.subjectId)
      this.editForm = {
        id: it.id,
        subjectId: it.subjectId,
        status: it.status,
        progressEpisode: it.progressEpisode || 0,
        rating: it.rating || null,
        shortComment: it.shortComment || '',
        maxEpisodes: s.episodes || 0
      }
      this.editVisible = true
    },
    async saveEdit(){
      // 保存编辑后的收藏数据
      if (this.editForm.progressEpisode > this.editForm.maxEpisodes && this.editForm.maxEpisodes > 0) {
        this.$message.warning(`当前进度不能超过总话数 (${this.editForm.maxEpisodes})`)
        return
      }
      const payload = {
        id: this.editForm.id,
        subjectId: this.editForm.subjectId,
        status: this.editForm.status,
        progressEpisode: this.editForm.progressEpisode,
        rating: this.editForm.rating,
        shortComment: this.editForm.shortComment
      }
      const r = await post('/collection/upsert', payload)
      if(r.code===0){
        this.$message.success('已保存')
        this.editVisible = false
        this.load() // 重新加载收藏列表
      } else {
        this.$message.error(r.msg||'失败')
      }
    },
    async deleteCollection(id){
      // 删除收藏
      const r = await del('/collection/one?id='+id)
      if(r.code===0){
        this.$message.success('已删除')
        this.load() // 重新加载收藏列表
      } else {
        this.$message.error(r.msg||'失败')
      }
    }
  }
}
</script>
<style scoped>
.page-title {
  margin-bottom: 32px;
}
.grid { display:grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap:12px }
.section { display:flex; background: var(--card-bg); border:1px solid var(--border-color); border-radius:16px; box-shadow: var(--card-shadow); padding:16px; transition: all 0.3s; }
.section:hover { transform: translateY(-4px); box-shadow: 0 12px 24px rgba(0,0,0,0.1); border-color: var(--primary-color); }
.cover { width:140px; height:198px; flex-shrink:0; overflow:hidden; border-radius:10px; background: var(--bg-color); border: 1px solid var(--border-color); }
.cover img { width:100%; height:100%; object-fit:cover; display:block }
.placeholder { width:100%; height:100%; display:flex; align-items:center; justify-content:center; background: var(--bg-color); color: var(--text-secondary); opacity: 0.5; font-size: 12px; }
.info { flex:1; padding-left:16px }
.info.fixed { display:grid; grid-template-rows: 26px 26px 26px 20px 20px 20px 20px 24px auto; row-gap:2px }
.info.fixed .title, .info.fixed .sub { white-space:nowrap; overflow:hidden; text-overflow:ellipsis }
.info.fixed .meta, .info.fixed .status, .info.fixed .progress, .info.fixed .rating, .info.fixed .date, .info.fixed .comment { white-space:nowrap; overflow:hidden; text-overflow:ellipsis }
.title { margin:0 0 4px; font-size:17px; font-weight: 700; color: var(--text-main); cursor: pointer; transition: color 0.2s; }
.title:hover { color: var(--primary-color); }
.sub { color: var(--text-secondary); font-size:13px; margin-bottom:6px; opacity: 0.8; }
.meta { color: var(--text-secondary); font-size:13px; margin-bottom:6px; display: flex; align-items: center; gap: 4px; }
.meta .tag { background: rgba(99, 102, 241, 0.1); color: var(--primary-color); padding:2px 8px; border-radius:6px; font-size:11px; font-weight: 600; }
.meta .sep { margin:0 2px; color: var(--border-color); }
.info { font-size:13px; color: var(--text-secondary); }
.comment { margin-bottom:6px; color: var(--text-secondary); font-style: italic; }
.date { color: var(--text-secondary); opacity: 0.6; }
.actions {
  margin-top:auto;
  display:flex;
  gap:8px;
  padding-top: 12px;
  border-top: 1px dashed var(--border-color);
}

.clean-search-area {
  background: var(--card-bg);
  padding: 24px 32px;
  margin-bottom: 32px;
  border-radius: 20px;
  border: 1px solid var(--border-color);
  box-shadow: var(--card-shadow);
}
.main-search-row { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.keyword-input { flex: 1; min-width: 280px; max-width: 500px; }
.filter-toggle {
  display: flex; align-items: center; gap: 6px; padding: 10px 14px;
  background: var(--bg-color); border: 1px solid var(--border-color); border-radius: 8px;
  cursor: pointer; font-size: 14px; color: var(--text-secondary); transition: all 0.2s; white-space: nowrap;
}
.filter-toggle:hover { background: var(--border-color); color: var(--text-main); }
.filter-toggle .rotated { transform: rotate(180deg); }
.compact-btn { min-width: 78px; padding: 0 16px !important; font-weight: 600; }
.advanced-filters {
  margin-top: 16px; padding-top: 12px; display: flex; flex-wrap: wrap;
  gap: 14px; align-items: center;
  border-top: 1px dashed var(--border-color);
}
.rating-group { display: flex; align-items: center; gap: 8px; }
.rating-group .label { font-size: 14px; color: var(--text-secondary); white-space: nowrap; }
.rating-group .sep { color: var(--text-secondary); opacity: 0.5; font-size: 14px; padding: 0 4px; }
.rating-group .el-input-number {width: 100px !important;}
.slide-enter-active, .slide-leave-active { transition: all 0.25s ease; }
.slide-enter-from, .slide-leave-to { opacity: 0; transform: translateY(-8px); }
@media (max-width: 768px) {
  .main-search-row { flex-direction: column; align-items: stretch; }
  .keyword-input, .filter-toggle, .compact-btn { width: 100%; }
  .filter-toggle { justify-content: center; }
}
</style>
