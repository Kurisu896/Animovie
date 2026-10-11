<!-- 页面：排行榜 -->
<template>
  <div class="page-container">
    <div class="toolbar">
      <h3 class="page-title">排行榜</h3>
      <div class="tools">
        <el-select v-model="by" placeholder="维度" style="width:160px" @change="load">
          <el-option label="收藏数" value="collections" />
          <el-option label="评分" value="rating" />
        </el-select>
      </div>
    </div>
    <el-card class="modern-card ranking-card">
      <div v-if="by==='rating'" class="tip">
        <el-icon><InfoFilled /></el-icon>
        提示：评分人数达到 5 人才计入排名
      </div>
      
      <div class="ranking-list">
        <div class="ranking-item" v-for="(it, i) in items" :key="it.id" @click="goSubject(it.id)">
          <!-- 排名勋章 -->
          <div class="rank-badge" :class="'rank-' + displayRank(i)">
            <span class="rank-num">{{ displayRank(i) }}</span>
          </div>

          <!-- 封面 -->
          <div class="thumb">
            <img v-if="it.coverPath" :src="base+it.coverPath" alt="cover" />
            <div v-else class="placeholder">No Cover</div>
            
            <!-- 快速收藏按钮 -->
            <div class="quick-action" v-if="token">
              <el-tooltip :content="collectText(it.id)" placement="top">
                <el-button 
                  circle 
                  :type="collectsById[it.id] ? 'danger' : 'primary'" 
                  :icon="collectsById[it.id] ? 'StarFilled' : 'Star'"
                  @click.stop="openEdit(it.id)"
                />
              </el-tooltip>
            </div>
          </div>

          <!-- 信息区 -->
          <div class="entry">
            <div class="title-row">
              <h4 class="title" :title="it.cnName">{{ it.cnName }}</h4>
              <div class="rating-badge" v-if="it.avgRating">
                <el-icon><StarFilled /></el-icon>
                <span>{{ Number(it.avgRating).toFixed(1) }}</span>
              </div>
            </div>

            <div class="sub" :title="it.alias ? (it.jpName + ' / ' + it.alias) : it.jpName">
              {{ it.jpName }}<span v-if="it.alias"> / {{ it.alias }}</span>
            </div>

            <div class="meta">
              <el-tag size="small" effect="plain" class="category-tag">{{ it.category }}</el-tag>
              <span class="sep">·</span>
              <span>{{ it.episodes }} 话</span>
              <template v-if="it.firstAirDate">
                <span class="sep">·</span>
                <span>{{ it.firstAirDate }}</span>
              </template>
            </div>

            <div class="extra-info">
              <div class="studio" v-if="it.studio">
                <el-icon><OfficeBuilding /></el-icon>
                <span>{{ it.studio }}</span>
              </div>
              <div class="tags" v-if="it.tags">
                <el-icon><CollectionTag /></el-icon>
                <span class="tag-text">{{ it.tags }}</span>
              </div>
            </div>

            <div class="stats-row">
              <div class="stat-item">
                <span class="stat-label">收藏</span>
                <span class="stat-value">{{ it.collectCount || 0 }}</span>
              </div>
              <div class="stat-item" v-if="by==='rating' && it.ratingCount">
                <span class="stat-label">评分人次</span>
                <span class="stat-value">{{ it.ratingCount }}</span>
              </div>
              <div class="site-link" v-if="it.officialSite">
                <el-link :href="it.officialSite" target="_blank" type="primary" :underline="false" @click.stop>
                  官网 <el-icon><Link /></el-icon>
                </el-link>
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
    </el-card>
  </div>
</template>
<script>
import { get, BASE, post } from '../utils/api'
import { del } from '../utils/api'
import { InfoFilled, StarFilled, Star, OfficeBuilding, CollectionTag, Link } from '@element-plus/icons-vue'

export default {
  components: { InfoFilled, StarFilled, Star, OfficeBuilding, CollectionTag, Link },
  data(){ return { by:'collections', items:[], total:0, pageNum:1, pageSize:15, base: BASE, token: localStorage.getItem('token')||'', collectsById:{}, editVisible:false, editSubjectId:null, editForm:{ status:'', progressEpisode:0, rating:null, shortComment:'' , maxEpisodes:0} } },
  mounted(){ this.load() },
  methods:{
    async load(){ const r = await get('/rank/top', { by:this.by, pageNum:this.pageNum, pageSize:this.pageSize }); if(r.code===0){ this.items=r.data.items||[]; this.total=r.data.total||0; await this.loadCollects() } },
    goSubject(id){ this.$router.push('/subject/'+id) },
    displayRank(i){ return (this.pageNum-1)*this.pageSize + i + 1 },
    async loadCollects(){ if(!this.token) return; const ids = this.items.map(x=> x.id); for(const id of ids){ try{ const r = await get('/collection/one', { subjectId:id }); if(r.code===0){ this.collectsById[id] = r.data || null } }catch(e){} } this.collectsById = { ...this.collectsById } },
    collectText(id){ return this.collectsById[id] ? '已收藏' : '收藏' },
    collectClass(id){ return this.collectsById[id] ? 'btn-collected' : 'btn-collect' },
    async prefill(id){ try{ const r = await get('/collection/one', { subjectId:id }); if(r.code===0 && r.data){ this.editForm = { status:r.data.status||'', progressEpisode:r.data.progressEpisode||0, rating:r.data.rating||null, shortComment:r.data.shortComment||'' } } else { this.editForm = { status:'', progressEpisode:0, rating:null, shortComment:'' } } }catch(e){ this.editForm = { status:'', progressEpisode:0, rating:null, shortComment:'' } } },
    async openEdit(id){
      if(!this.token){ this.$message.warning('请先登录'); return }
      this.editSubjectId = id
      await this.prefill(id)
      const it = this.items.find(x => x.id === id)
      if (it) this.editForm.maxEpisodes = it.episodes || 0
      this.editVisible = true
    },
    async saveEdit(){
      if (this.editForm.progressEpisode > this.editForm.maxEpisodes && this.editForm.maxEpisodes > 0) {
        this.$message.warning(`当前进度不能超过总话数 (${this.editForm.maxEpisodes})`)
        return
      }
      const id = this.editSubjectId
      const payload = Object.assign({ subjectId:id }, this.editForm)
      delete payload.maxEpisodes
      const r = await post('/collection/upsert', payload)
      if(r.code===0){
        this.$message.success('已保存')
        this.editVisible=false
        const existed = !!this.collectsById[id]
        this.collectsById[id] = { subjectId:id, status:this.editForm.status }
        this.collectsById = { ...this.collectsById }
        const it = this.items.find(x=> x.id===id)
        if(it && !existed){ it.collectCount = (it.collectCount||0) + 1 }
      } else {
        this.$message.error(r.msg||'失败')
      }
    }
  }
}
</script>
<style scoped>
.toolbar { display:flex; justify-content:space-between; align-items:center; margin: 0 0 24px 0; padding-top: 4px; }
.tools { display:flex; gap:8px; align-items:center }

.ranking-card {
  padding: 20px;
}

.tip { 
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-secondary); 
  font-size: 13px; 
  margin-bottom: 24px; 
  background: rgba(99, 102, 241, 0.05); 
  padding: 12px 16px; 
  border-radius: 12px; 
  border-left: 4px solid var(--primary-color); 
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.ranking-item { 
  display: flex; 
  gap: 24px; 
  background: var(--card-bg); 
  border: 1px solid var(--border-color); 
  border-radius: 16px; 
  padding: 20px; 
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  align-items: center;
}

.ranking-item:hover {
  transform: translateY(-4px);
  box-shadow: var(--card-shadow);
  border-color: var(--primary-color);
}

/* 排名勋章样式 */
.rank-badge {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-family: 'Georgia', serif;
  font-size: 24px;
  font-weight: 900;
  color: #94a3b8;
  position: relative;
}

.rank-1 { color: #f59e0b; font-size: 32px; }
.rank-2 { color: #94a3b8; font-size: 28px; }
.rank-3 { color: #b45309; font-size: 26px; }

.rank-num {
  z-index: 1;
}

/* 封面样式 */
.thumb { 
  width: 100px; 
  height: 140px; 
  overflow: hidden; 
  border-radius: 10px; 
  background: #f3f4f6;
  flex-shrink: 0;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  position: relative;
}

.thumb img { width: 100%; height: 100%; object-fit: cover; display: block; transition: transform 0.3s; }

.ranking-item:hover .thumb img {
  transform: scale(1.05);
}

.quick-action {
  position: absolute;
  top: 6px;
  right: 6px;
  opacity: 0;
  transform: translateY(-10px);
  transition: all 0.3s;
}

.ranking-item:hover .quick-action {
  opacity: 1;
  transform: translateY(0);
}

/* 信息区样式 */
.entry { flex: 1; min-width: 0; }

.title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 6px;
  gap: 12px;
}

.title { 
  margin: 0; 
  font-size: 18px; 
  font-weight: 700;
  color: var(--text-main);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.rating-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
  padding: 2px 8px;
  border-radius: 8px;
  font-weight: 700;
  font-size: 14px;
  border: 1px solid rgba(245, 158, 11, 0.2);
}

.sub { 
  color: var(--text-secondary); 
  font-size: 13px; 
  margin-bottom: 12px; 
  white-space: nowrap; 
  overflow: hidden; 
  text-overflow: ellipsis; 
}

.meta { 
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-secondary); 
  font-size: 13px; 
  margin-bottom: 12px; 
}

.category-tag {
  border-radius: 6px;
}

.sep { color: var(--border-color); }

.extra-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
}

.studio, .tags {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--text-secondary);
}

.tag-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stats-row {
  display: flex;
  align-items: center;
  gap: 20px;
  padding-top: 12px;
  border-top: 1px dashed var(--border-color);
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-label {
  font-size: 11px;
  color: var(--text-secondary);
  opacity: 0.7;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-value {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-main);
}

.site-link {
  margin-left: auto;
}

/* 页脚分页 */
.pager { margin-top: 24px; display: flex; justify-content: flex-end; }
</style>
