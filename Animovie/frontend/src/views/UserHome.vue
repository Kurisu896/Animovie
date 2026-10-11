<!-- 页面：用户主页 -->
<template>
  <!-- 页面主容器，使用grid布局实现两栏结构 -->
  <div class="page">
    <!-- 左侧用户信息面板 -->
    <div class="left">
      <!-- 用户信息卡片 -->
      <div class="panel">
        <!-- 头像容器 -->
        <div class="avatar">
          <!-- 显示用户头像：v-if判断user.avatarPath是否存在 -->
          <!-- 数据流向：user.avatarPath来自loadUser()方法加载的用户数据，显示到<img>的src属性 -->
          <!-- 图片URL：base+user.avatarPath，base来自../utils/api.js的BASE常量 -->
          <img v-if="user.avatarPath" :src="base+user.avatarPath" alt="avatar" />
          <!-- 头像占位符：当用户没有头像时显示 -->
          <div v-else class="avatarPh">No Avatar</div>
        </div>
        <!-- 昵称和用户ID -->
        <div class="nickname">
          <!-- 显示用户昵称或用户名：使用 || 运算符处理空值 -->
          <!-- 数据流向：user.nickname和user.username来自loadUser()方法加载的用户数据 -->
          {{ user.nickname || user.username }} 
          <!-- 显示用户ID：v-if判断user.id是否存在 -->
          <!-- 数据流向：user.id来自loadUser()方法加载的用户数据 -->
          <span class="uid" v-if="user.id">@{{ user.id }}</span>
        </div>
        <!-- 个性签名：v-if判断user.signature是否存在 -->
        <!-- 数据流向：user.signature来自loadUser()方法加载的用户数据 -->
        <div class="signature" v-if="user.signature">{{ user.signature }}</div>
        <!-- 管理员操作区域：v-if判断当前登录用户是否为管理员 -->
        <!-- 数据流向：role来自auth状态管理，通过useAuthStore()从../store/index.js获取 -->
        <div v-if="role==='ADMIN'" style="margin-top:8px">
          <!-- 封禁/解封确认对话框：Element Plus组件，用于二次确认操作 -->
          <!-- 数据流向：isBanned来自computed属性，根据user.status计算得出 -->
          <el-popconfirm 
            :title="isBanned ? '确认解除封禁该用户？' : '确认封禁该用户？'" 
            @confirm="toggleBan" <!-- 点击确认时调用toggleBan方法 -->
          >
            <!-- 操作按钮 -->
            <template #reference>
              <!-- 根据用户封禁状态显示不同的按钮类型和文本 -->
              <!-- 数据流向：isBanned来自computed属性 -->
              <el-button 
                :type="isBanned ? 'primary' : 'danger'" 
                size="small"
              >{{ isBanned ? '解除封禁' : '封禁用户' }}</el-button>
            </template>
          </el-popconfirm>
        </div>
      </div>
    </div>
    
    <!-- 右侧内容区 -->
    <div class="main">
      <el-tabs v-model="tab">
        <el-tab-pane :label="isSelf ? '我的收藏' : 'TA的收藏'" name="collections">
        <div class="collectionsTools">
          <el-select v-model="collectionStatus" size="small" style="width:160px">
            <el-option label="全部" value="ALL" />
            <el-option label="在看" value="WATCHING" />
            <el-option label="看过" value="WATCHED" />
            <el-option label="抛弃" value="DROPPED" />
          </el-select>
        </div>
        <div class="grid">
          <div v-for="c in collectionsFiltered" :key="c.id" class="collect-card-wrapper">
            <div class="subject-card" @click="goSubject(c.subjectId)">
              <div class="cover">
                <img v-if="subject(c.subjectId).coverPath" :src="base+subject(c.subjectId).coverPath" alt="cover" />
                <div v-else class="placeholder">No Cover</div>
                
                <!-- 悬浮操作层 -->
                <div class="cover-overlay" v-if="isSelf">
                  <el-tooltip content="修改收藏" placement="top">
                    <el-button 
                      circle 
                      type="primary"
                      icon="Edit" 
                      @click.stop="openEdit(c.subjectId)"
                      class="overlay-btn"
                    />
                  </el-tooltip>
                  <el-tooltip content="删除收藏" placement="top">
                    <el-popconfirm title="确认删除收藏？" @confirm="deleteCollection(c.subjectId)">
                      <template #reference>
                        <el-button 
                          circle 
                          type="danger"
                          icon="Delete" 
                          @click.stop
                          class="overlay-btn"
                        />
                      </template>
                    </el-popconfirm>
                  </el-tooltip>
                </div>
              </div>

              <div class="info">
                <div class="title-row">
                  <h3 class="title" :title="subjectName(c.subjectId)">{{ subjectName(c.subjectId) }}</h3>
                  <el-tag size="small" effect="dark" class="category-tag">{{ subject(c.subjectId).category }}</el-tag>
                </div>
                <div class="sub" :title="subject(c.subjectId).jpName">
                  {{ subject(c.subjectId).jpName }}
                </div>
                
                <div class="meta-tags">
                  <span class="meta-item"><el-icon><VideoPlay /></el-icon> {{ subject(c.subjectId).episodes }} 话</span>
                  <span class="meta-item" v-if="subject(c.subjectId).firstAirDate"><el-icon><Calendar /></el-icon> {{ subject(c.subjectId).firstAirDate }}</span>
                </div>

                <div class="extra-info">
                  <div class="info-line" v-if="subject(c.subjectId).studio">
                    <span class="label">制作:</span>
                    <span class="value">{{ subject(c.subjectId).studio }}</span>
                  </div>
                </div>

                <div class="user-collection-stats">
                  <div class="status-badge" :class="c.status.toLowerCase()">
                    {{ mapStatus(c.status) }}
                  </div>
                  <div class="progress-text" v-if="c.progressEpisode">
                    已看 {{ c.progressEpisode }} 话
                  </div>
                  <div class="user-rating" v-if="c.rating">
                    <el-icon><StarFilled /></el-icon>
                    <span>{{ c.rating }}</span>
                  </div>
                </div>
              </div>
            </div>
            <!-- 吐槽内容（如果有） -->
            <div class="collection-comment" v-if="c.shortComment">
              <el-icon><ChatDotRound /></el-icon>
              <span class="comment-text" :title="c.shortComment">{{ c.shortComment }}</span>
            </div>
          </div>
        </div>
        </el-tab-pane>
        <el-tab-pane :label="isSelf ? '我的动态' : 'TA的动态'" name="timeline">
          <div class="timeline-container">
            <div v-for="it in activities" :key="it.id" class="timeline-item">
              <!-- 左侧：时间线 -->
              <div class="timeline-left">
                <div class="timeline-dot"></div>
                <div class="timeline-line"></div>
              </div>

              <!-- 右侧：内容区 -->
              <div class="timeline-content">
                <div class="content-header">
                  <span class="action-text">
                    <template v-if="it.type==='STATUS'">
                      更新了收藏状态为 <span class="status-tag" :class="it.detail.toLowerCase()">{{ mapStatus(it.detail) }}</span>
                    </template>
                    <template v-else-if="it.type==='PROGRESS'">
                      更新了观看进度至 <span class="highlight-text">第 {{ it.detail }} 集</span>
                    </template>
                    <template v-else-if="it.type==='RATING'">
                      打分了 <span class="rating-text">{{ it.detail }} 分</span>
                    </template>
                    <template v-else-if="it.type==='COMMENT'">
                      发表了吐槽
                    </template>
                    <template v-else>
                      进行了操作：{{ it.type }}
                    </template>
                  </span>
                  <span class="time-stamp">{{ it.createdAt }}</span>
                  
                  <div class="header-actions" v-if="isSelf || role==='ADMIN'">
                    <el-popconfirm title="确认删除动态？" @confirm="deleteActivity(it.id)">
                      <template #reference>
                        <el-button link type="danger" icon="Delete" class="delete-btn" />
                      </template>
                    </el-popconfirm>
                  </div>
                </div>

                <div class="comment-text" v-if="it.type==='COMMENT' && it.detail">
                  “{{ it.detail }}”
                </div>

                <!-- 条目卡片 -->
                <div class="subject-mini-card" @click="goSubject(it.subjectId)" v-if="subject(it.subjectId).id">
                  <div class="mini-cover">
                    <img v-if="subject(it.subjectId).coverPath" :src="base+subject(it.subjectId).coverPath" alt="cover" />
                    <div v-else class="placeholder">No Cover</div>
                  </div>
                  <div class="mini-info">
                    <div class="mini-title">{{ subjectName(it.subjectId) }}</div>
                    <div class="mini-sub">{{ subject(it.subjectId).jpName }}</div>
                    <div class="mini-meta">
                      <el-tag size="small" effect="plain" class="mini-tag">{{ subject(it.subjectId).category }}</el-tag>
                      <span class="mini-episodes">{{ subject(it.subjectId).episodes }} 话</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane :label="isSelf ? '我的话题' : 'TA的话题'" name="topics">
          <div class="forumList">
            <div v-for="row in topics" :key="row.id" class="card" @click="goTopic(row.id)">
              <div class="thumb">
                <img v-if="subject(row.subjectId).coverPath" :src="base+subject(row.subjectId).coverPath" alt="cover" />
                <div v-else class="placeholder">No Cover</div>
              </div>
              <div class="entry">
                <div class="title">{{ row.title }}</div>
                <div class="subj">{{ subject(row.subjectId).cnName || ('条目 '+row.subjectId) }}</div>
                <div class="meta">
                  回复 {{ row.replyCount || 0 }} · 更新时间 {{ row.updatedAt }}
                  <span class="sep">·</span>
                  <span class="status" :class="{ locked: row.status==='LOCKED', rejected: row.status==='REJECTED', open: row.status==='OPEN' }">
                    {{ row.status==='LOCKED' ? '待审核' : (row.status==='REJECTED' ? '已打回' : '已通过') }}
                  </span>
                  <el-tooltip v-if="isSelf && row.status==='REJECTED'" content="重新编辑" placement="top">
                    <el-button link type="primary" icon="Edit" style="margin-left:8px" @click.stop="openTopicEdit(row)" />
                  </el-tooltip>
                  <span v-if="role==='ADMIN' || isSelf" style="margin-left:8px">
                    <el-popconfirm title="确认删除该话题？" @confirm="deleteTopic(row.id)">
                      <template #reference>
                        <el-button link type="danger" icon="Delete" @click.stop />
                      </template>
                    </el-popconfirm>
                  </span>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
      <el-dialog v-model="topicEditVisible" title="重新编辑话题" width="560px" class="modern-dialog" append-to-body>
        <div class="dialog-content">
          <el-form :model="topicEdit" label-position="top">
            <el-form-item label="话题标题">
              <el-input v-model="topicEdit.title" placeholder="请输入话题标题" />
            </el-form-item>
            <el-form-item label="话题内容">
              <el-input type="textarea" v-model="topicEdit.content" :rows="8" placeholder="请完善话题内容..." />
            </el-form-item>
          </el-form>
        </div>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="topicEditVisible=false" round>取消</el-button>
            <el-button type="primary" @click="saveTopicEdit" round class="submit-btn">重新提交</el-button>
          </div>
        </template>
      </el-dialog>
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
  </div>
</template>

<script>
// 导入API方法和BASE常量
// 来源：../utils/api.js，用于发送HTTP请求
import { get, BASE, post, formAuth } from '../utils/api'
import { del } from '../utils/api'
import { VideoPlay, Calendar, Edit, Delete, StarFilled, ChatDotRound } from '@element-plus/icons-vue'
// 导入认证状态管理
// 来源：../store/index.js，使用Pinia进行状态管理
import { useAuthStore } from '../store'

// 导出Vue组件配置对象
// 组件名称：UserHome，用于显示用户主页
// 组件功能：展示用户信息、收藏列表和动态时间线
// 组件位置：src/views/UserHome.vue
// 组件类型：Vue 3 组件，使用Composition API + Options API 混合写法
export default {
  components: { VideoPlay, Calendar, Edit, Delete, StarFilled, ChatDotRound },
  // Vue 3 Composition API的setup函数
  // 作用：初始化组件的响应式状态和逻辑
  // 调用时机：组件实例创建时
  // 返回值：包含auth状态管理实例的对象
  setup(){
    // 获取认证状态管理实例
    // useAuthStore()来源：../store/index.js，Pinia状态管理
    // 作用：获取当前登录用户的认证状态，包括token、role等
    const auth = useAuthStore(); 
    // 返回auth对象，使其在组件中可用
    return { auth } 
  },
  
  // 组件数据：定义组件的响应式状态
  // 数据类型：对象，包含多个属性
  data(){
    return {
      id: null, // 当前查看的用户ID
                // 数据流向：从this.$route.params.id获取，在mounted()生命周期钩子中赋值
                // 数据用途：用于加载指定用户的信息、收藏和动态
      user: {}, // 用户信息对象
                // 数据流向：通过loadUser()方法从后端API加载
                // 数据用途：显示用户头像、昵称、签名等基本信息
      collections: [], // 用户收藏列表
                       // 数据流向：通过loadCollections()方法从后端API加载
                       // 数据用途：显示用户收藏的条目列表
      activities: [], // 用户动态列表
                      // 数据流向：通过loadActivities()方法从后端API加载
                      // 数据用途：显示用户的动态时间线
      subjectsById: {}, // 以ID为键的条目映射
                        // 数据流向：通过loadSubjects()方法从后端API加载
                        // 数据用途：缓存条目数据，避免重复请求
      base: BASE, // API基础URL
                 // 数据来源：从../utils/api.js导入
                 // 数据用途：构建完整的API请求URL
      meId: null, // 当前登录用户ID
      editVisible:false,
      editSubjectId:null,
      editForm:{ status:'', progressEpisode:0, rating:null, shortComment:'', maxEpisodes:0 },
      tab:'collections',
      collectionStatus:'ALL',
      topics:[],
      topicsTotal:0,
      topicEditVisible:false,
      topicEdit:{ id:null, title:'', content:'' }
                 // 数据流向：通过loadMe()方法从后端API加载
                 // 数据用途：判断是否查看自己的主页
    } 
  },
  
  // 计算属性：基于组件数据动态计算的值
  // 数据类型：对象，包含多个计算属性
  // 特点：响应式，数据变化时自动重新计算
  computed:{
    // 获取当前登录用户的角色
    // 数据流向：从this.auth.role获取，auth来自setup()函数
    // 数据用途：判断是否显示管理员操作按钮
    role(){ return this.auth.role || '' },
    
    // 获取当前登录用户的token
    // 数据流向：从this.auth.token获取，auth来自setup()函数
    // 数据用途：用于发送需要认证的API请求
    token(){ return this.auth.token || '' },
    
    // 判断用户是否被封禁
    // 数据流向：基于this.user.status计算
    // 计算逻辑：将user.status转换为数字，判断是否等于0
    // 数据用途：显示封禁/解封按钮和对话框标题
    isBanned(){ return Number(this.user.status||0)===0 },
    
    // 判断是否查看自己的主页
    // 数据流向：基于this.meId和this.id计算
    // 计算逻辑：比较meId和id是否相等
    // 数据用途：显示"我的收藏"/"TA的收藏"等不同标题
    isSelf(){ return this.meId && this.id && Number(this.meId)===Number(this.id) }
    ,collectionsFiltered(){ const s=this.collectionStatus; if(s==='ALL') return this.collections||[]; return (this.collections||[]).filter(x=> String(x.status||'')===s) }
  },
  
  // 生命周期钩子：组件挂载完成后执行
  // 调用时机：组件DOM挂载到页面后
  // 功能：初始化组件数据，加载用户信息、收藏和动态
  // 特点：使用async/await处理异步操作
  async mounted(){
    // 从路由参数中获取用户ID
    // this.$route来源：Vue Router，用于访问当前路由信息
    // params.id来源：路由配置中的动态参数，如/user/:id
    this.id = Number(this.$route.params.id);
    
    // 加载用户信息
    // 方法来源：组件内部的loadUser()方法
    // 作用：从后端API加载指定用户的基本信息
    await this.loadUser();
    
    // 加载用户收藏
    // 方法来源：组件内部的loadCollections()方法
    // 作用：从后端API加载指定用户的收藏列表
    await this.loadCollections();
    
    // 加载用户动态
    // 方法来源：组件内部的loadActivities()方法
    // 作用：从后端API加载指定用户的动态列表
    await this.loadActivities();
    
    // 加载当前登录用户信息
    // 方法来源：组件内部的loadMe()方法
    // 作用：从后端API加载当前登录用户的ID
    await this.loadMe();
    // 加载用户话题
    await this.loadTopics();
  },
  watch:{
    tab(val){
      if(val==='collections') this.loadCollections()
      else if(val==='timeline') this.loadActivities()
      else if(val==='topics') this.loadTopics()
    },
    '$route.params.id': {
      immediate: false,
      handler(val){
        const nid = Number(val)
        if(nid && nid!==this.id){
          this.id = nid
          this.user = {}
          this.collections = []
          this.activities = []
          this.subjectsById = {}
          this.topics = []
          this.loadUser()
          this.loadCollections()
          this.loadActivities()
          this.loadTopics()
        }
      }
    }
  },
  
  // 组件方法：定义组件的业务逻辑
  // 数据类型：对象，包含多个方法
  // 特点：支持async/await异步操作
  methods:{
    // 加载用户信息
    // 方法类型：异步方法，返回Promise
    // 方法参数：无
    // 方法功能：通过API获取指定用户的基本信息
    // API调用：get('/auth/user/'+this.id)
    // API来源：../utils/api.js
    // API路径：/auth/user/{id}，获取指定ID的用户信息
    async loadUser(){
      // 发送GET请求，获取用户信息
      const r = await get('/auth/user/'+this.id);
      // 如果请求成功（r.code===0）
      if(r.code===0){
        // 更新user对象
        this.user = r.data || {};
      }
    },
    
    // 加载当前登录用户信息
    // 方法类型：异步方法，返回Promise
    // 方法参数：无
    // 方法功能：通过API获取当前登录用户的ID
    // API调用：get('/auth/me')
    // API来源：../utils/api.js
    // API路径：/auth/me，获取当前登录用户的信息
    async loadMe(){
      // 如果没有token，直接返回
      if(!this.token) return;
      try{
        // 发送GET请求，获取当前登录用户信息
        const r = await get('/auth/me');
        // 如果请求成功（r.code===0）
        if(r.code===0){
          // 更新meId
          this.meId = r.data.id || null;
        }
      }catch(e){
        // 捕获异常，不做处理
      }
    },
    
    // 加载用户收藏
    // 方法类型：异步方法，返回Promise
    // 方法参数：无
    // 方法功能：通过API获取指定用户的收藏列表
    // API调用：get('/collection/by-user', { userId: this.id, pageNum: 1, pageSize: 15 })
    // API来源：../utils/api.js
    // API路径：/collection/by-user，获取指定用户的收藏列表
    async loadCollections(){
      // 发送GET请求，获取用户收藏列表
      const r = await get('/collection/by-user', { 
        userId: this.id, // 要获取收藏的用户ID
        pageNum: 1, // 页码，当前为第1页
        pageSize: 15 // 每页数量，当前为15条
      });
      // 如果请求成功（r.code===0）
      if(r.code===0){
        // 更新collections数组
        this.collections = r.data.items||[];
        // 加载收藏的条目详情
        // 方法调用：this.loadSubjects()，参数为收藏条目的subjectId数组
        // 方法来源：组件内部的loadSubjects()方法
        await this.loadSubjects(this.collections.map(x=> x.subjectId));
      }
    },
    
    // 加载用户动态
    // 方法类型：异步方法，返回Promise
    // 方法参数：无
    // 方法功能：通过API获取指定用户的动态列表
    // API调用：get('/activity/user', { userId: this.id, pageNum: 1, pageSize: 20 })
    // API来源：../utils/api.js
    // API路径：/activity/user，获取指定用户的动态列表
    async loadActivities(){
      // 发送GET请求，获取用户动态列表
      const r = await get('/activity/user', { 
        userId: this.id, // 要获取动态的用户ID
        pageNum: 1, // 页码，当前为第1页
        pageSize: 20 // 每页数量，当前为20条
      });
      // 如果请求成功（r.code===0）
      if(r.code===0){
        // 更新activities数组
        this.activities = r.data.items||[];
        // 提取动态涉及的条目ID，过滤掉空值
        const ids = (this.activities||[]).map(x=> x.subjectId).filter(Boolean);
        // 加载条目详情
        // 方法调用：this.loadSubjects()，参数为动态涉及的subjectId数组
        await this.loadSubjects(ids);
      }
    },
    // 加载用户话题
    async loadTopics(){
      if(this.isSelf){
        const r = await get('/discussion/my', { pageNum:1, pageSize:20 });
        if(r && r.code===0){ this.topics = r.data.items||[]; const ids = [...new Set(this.topics.map(x=> x.subjectId))]; await this.loadSubjects(ids) }
      } else {
        const r = await get('/discussion/user-topics', { userId:this.id, pageNum:1, pageSize:20 });
        if(r && r.code===0){ this.topics = r.data.items||[]; const ids = [...new Set(this.topics.map(x=> x.subjectId))]; await this.loadSubjects(ids) }
      }
    },
    
    // 加载条目详情
    // 方法类型：异步方法，返回Promise
    // 方法参数：ids - 条目ID数组
    // 方法功能：通过API获取多个条目的详情，并缓存到subjectsById中
    // API调用：get('/subject/'+sid)，对每个条目ID调用一次
    // API来源：../utils/api.js
    // API路径：/subject/{id}，获取指定ID的条目详情
    async loadSubjects(ids){
      // 使用Set去重，避免重复请求
      const set = new Set(ids||[]);
      // 遍历条目ID，加载未缓存的条目
      for(const sid of set){
        // 如果条目未缓存
        if(!this.subjectsById[sid]){
          // 发送GET请求，获取条目详情
          const s = await get('/subject/'+sid);
          // 如果请求成功（s.code===0）且有数据
          if(s.code===0 && s.data){
            // 将条目详情缓存到subjectsById中
            this.subjectsById[sid] = s.data;
          }
        }
      }
    },
    
    // 获取指定ID的条目对象
    // 方法类型：同步方法，返回对象
    // 方法参数：id - 条目ID
    // 方法功能：从subjectsById中获取指定ID的条目对象
    // 返回值：条目对象或空对象
    subject(id){
      // 从subjectsById中获取条目，如果不存在则返回空对象
      return this.subjectsById[id] || {} 
    },
    
    // 获取指定ID的条目名称
    // 方法类型：同步方法，返回字符串
    // 方法参数：id - 条目ID
    // 方法功能：获取指定ID的条目名称，处理空值情况
    // 返回值：条目中文名称或默认名称
    subjectName(id){
      // 如果id为空，返回"已删除条目"
      if(!id) return '已删除条目';
      // 获取条目对象
      const s = this.subject(id);
      // 返回条目中文名称或默认名称
      return s.cnName || ('条目 '+id);
    },
    
    // 将状态字符串映射为中文
    // 方法类型：同步方法，返回字符串
    // 方法参数：s - 状态字符串
    // 方法功能：将英文状态字符串转换为中文
    // 返回值：中文状态名称
    mapStatus(s){
      // 使用条件运算符，将英文状态映射为中文
      return s==='WATCHING'?'在看': s==='WATCHED'?'看过':'抛弃';
    },
    
    // 生成动态消息文本
    // 方法类型：同步方法，返回字符串
    // 方法参数：it - 动态对象
    // 方法功能：根据动态类型生成相应的消息文本
    // 返回值：动态消息文本
    msg(it){
      // 获取条目名称
      const name = this.subject(it.subjectId)?(this.subject(it.subjectId).cnName||('条目 '+it.subjectId)):'已删除条目';
      // 根据动态类型生成消息
      if(it.type==='STATUS') return `更新了「${name}」的收藏状态为「${this.mapStatus(it.detail)}」`;
      if(it.type==='PROGRESS') return `更新了「${name}」的观看进度至第 ${it.detail} 集`;
      if(it.type==='RATING') return `为「${name}」打分：${it.detail} 分`;
      if(it.type==='COMMENT') return `对「${name}」发表吐槽：${it.detail}`;
      return `在「${name}」进行了操作：${it.type} ${it.detail}`;
    },
    
    // 跳转到条目详情页
    // 方法类型：同步方法，无返回值
    // 方法参数：id - 条目ID
    // 方法功能：使用Vue Router跳转到条目详情页
    // 路由跳转：this.$router.push('/subject/'+id)
    // $router来源：Vue Router，用于页面跳转
    // 跳转路径：/subject/{id}，条目详情页
    goSubject(id){ 
      // 使用Vue Router跳转到条目详情页
      this.$router.push('/subject/'+id) 
    },
    // 跳转到话题详情
    goTopic(id){ if(!id) return; this.$router.push('/discussion/topic/'+id) },
    // 删除动态
    async deleteActivity(id){ const r = await del('/activity/one?id='+id); if(r.code===0){ this.$message.success('已删除动态'); this.activities = this.activities.filter(x=> x.id!==id) } else { this.$message.error(r.msg||'删除失败') } },
    // 删除话题
    async deleteTopic(id){ const r = await del('/discussion/topic/'+id); if(r.code===0){ this.$message.success('已删除'); this.topics = this.topics.filter(x=> x.id!==id) } else { this.$message.error(r.msg||'失败') } },
    // 重新编辑话题
    openTopicEdit(row){ if(!row) return; this.topicEditVisible=true; this.topicEdit = { id: row.id, title: row.title||'', content: row.content||'' } },
    async saveTopicEdit(){ const p = { topicId:this.topicEdit.id, title:this.topicEdit.title||'', content:this.topicEdit.content||'' }; const r = await formAuth('/discussion/topic/update', p); if(r.code===0){ this.$message.success('已提交，待审核'); this.topicEditVisible=false; this.topics = (this.topics||[]).map(x=> x.id===this.topicEdit.id ? { ...x, title:this.topicEdit.title, content:this.topicEdit.content, status:'LOCKED' } : x) } else { this.$message.error(r.msg||'失败') } },
    
    // 封禁/解封用户
    // 方法类型：异步方法，返回Promise
    // 方法参数：无
    // 方法功能：根据用户当前状态，执行封禁或解封操作
    // API调用：
    //  - 封禁：formAuth('/auth/admin/ban', { userId: this.user.id })
    //  - 解封：formAuth('/auth/admin/unban', { userId: this.user.id })
    // API来源：../utils/api.js
    // API路径：
    //  - /auth/admin/ban，封禁指定用户
    //  - /auth/admin/unban，解封指定用户
    async toggleBan(){
      // 检查当前登录用户是否为管理员
      if(this.role!=='ADMIN'){
        // 如果不是管理员，显示错误消息
        this.$message.error('无权限');
        // 返回，终止执行
        return;
      }
      
      // 如果用户已被封禁（isBanned为true）
      if(this.isBanned){
        // 解除封禁
        // 发送POST请求，调用管理员解封API
        const r = await formAuth('/auth/admin/unban', { userId: this.user.id });
        // 如果请求成功（r.code===0）
        if(r.code===0){
          // 显示成功消息
          this.$message.success('已解封');
          // 更新用户状态为正常（1）
          this.user.status=1;
        } else {
          // 如果请求失败，显示错误消息
          this.$message.error(r.msg||'失败');
        }
      } else {
        // 封禁用户
        // 发送POST请求，调用管理员封禁API
        const r = await formAuth('/auth/admin/ban', { userId: this.user.id });
        // 如果请求成功（r.code===0）
        if(r.code===0){
          // 显示成功消息
          this.$message.success('已封禁');
          // 更新用户状态为封禁（0）
          this.user.status=0;
        } else {
          // 如果请求失败，显示错误消息
          this.$message.error(r.msg||'失败');
        }
      }
    }
    ,async prefill(id){ try{ const r = await get('/collection/one', { subjectId:id }); if(r.code===0 && r.data){ this.editForm = { status:r.data.status||'', progressEpisode:r.data.progressEpisode||0, rating:r.data.rating||null, shortComment:r.data.shortComment||'' } } else { this.editForm = { status:'', progressEpisode:0, rating:null, shortComment:'' } } }catch(e){ this.editForm = { status:'', progressEpisode:0, rating:null, shortComment:'' } } }
    ,async openEdit(id){ 
      if(!this.token){ this.$message.warning('请先登录'); return } 
      this.editSubjectId = id; 
      await this.prefill(id); 
      const s = this.subject(id);
      if (s) this.editForm.maxEpisodes = s.episodes || 0;
      this.editVisible = true 
    }
    ,async saveEdit(){ 
      if (this.editForm.progressEpisode > this.editForm.maxEpisodes && this.editForm.maxEpisodes > 0) {
        this.$message.warning(`当前进度不能超过总话数 (${this.editForm.maxEpisodes})`)
        return
      }
      const id = this.editSubjectId; 
      const payload = Object.assign({ subjectId:id }, this.editForm); 
      delete payload.maxEpisodes;
      const r = await post('/collection/upsert', payload); 
      if(r.code===0){ this.$message.success('已保存'); this.editVisible=false; this.loadCollections() } else { this.$message.error(r.msg||'失败') } 
    }
    ,async deleteCollection(id){ const r = await del('/collection/one?subjectId='+id); if(r.code===0){ this.$message.success('已删除收藏'); this.collections = (this.collections||[]).filter(x=> x.subjectId!==id) } else { this.$message.error(r.msg||'失败') } }
  }
}
</script>

<style scoped>
/* 用户主页：两栏栅格布局容器 */
.page {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 24px;
  padding: 24px;
}

/* 左侧信息面板 */
.left {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.left .panel {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--card-shadow);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  border: 1px solid var(--border-color);
}

/* 头像容器与样式 */
.avatar {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  overflow: hidden;
  background: var(--bg-color);
  border: 4px solid var(--card-bg);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 16px;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatarPh {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  font-weight: 600;
}

/* 昵称与用户ID */
.nickname {
  font-size: 20px;
  font-weight: 800;
  color: var(--text-main);
  margin-bottom: 8px;
}

.uid {
  display: block;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
  margin-top: 4px;
}

/* 个性签名 */
.signature {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 12px 0;
  padding: 12px;
  background: var(--bg-color);
  border-radius: 12px;
  width: 100%;
}

/* 右侧内容区 */
.main {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--card-shadow);
  min-height: 600px;
  border: 1px solid var(--border-color);
}

:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: var(--border-color);
}

:deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 600;
  padding: 0 24px;
}

/* 收藏卡片网格 */
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 24px;
  margin-top: 16px;
}

.collectionsTools {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
}

.collect-card-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.subject-card { 
  display: flex; 
  background: var(--card-bg); 
  border: 1px solid var(--border-color); 
  border-radius: 16px; 
  box-shadow: var(--card-shadow); 
  padding: 16px; 
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.subject-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.1);
  border-color: var(--primary-color);
}

.cover { 
  width: 100px; 
  height: 140px; 
  flex-shrink: 0; 
  overflow: hidden; 
  border-radius: 10px; 
  background: #f3f4f6;
  position: relative;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.cover img { width: 100%; height: 100%; object-fit: cover; display: block; transition: transform 0.5s; }
.subject-card:hover .cover img { transform: scale(1.05); }

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
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s;
}

.cover:hover .cover-overlay { opacity: 1; }
.overlay-btn { box-shadow: 0 4px 8px rgba(0,0,0,0.2); }

.placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; background: #f3f4f6; color: #9ca3af; font-size: 12px; }

.info { flex: 1; padding-left: 16px; display: flex; flex-direction: column; min-width: 0; }

.title-row { display: flex; justify-content: space-between; align-items: center; gap: 8px; margin-bottom: 6px; overflow: hidden; }
.title { margin: 0; font-size: 16px; font-weight: 700; color: var(--text-main); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; flex: 1; line-height: 1.4; }
.category-tag { flex-shrink: 0; border-radius: 6px; }

.sub { color: var(--text-secondary); font-size: 13px; margin-bottom: 8px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.meta-tags { display: flex; flex-wrap: wrap; gap: 12px; margin-bottom: 8px; }
.meta-item { display: flex; align-items: center; gap: 4px; font-size: 12px; color: var(--text-secondary); }
.meta-item .el-icon { font-size: 14px; color: var(--text-secondary); opacity: 0.7; }

.extra-info { margin-bottom: 8px; display: flex; flex-direction: column; gap: 4px; }
.info-line { font-size: 12px; color: var(--text-secondary); display: flex; gap: 4px; overflow: hidden; }
.info-line .label { font-weight: 600; color: var(--text-secondary); opacity: 0.6; flex-shrink: 0; }
.info-line .value { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.user-collection-stats {
  margin-top: auto;
  display: flex;
  align-items: center;
  gap: 12px;
  padding-top: 10px;
  border-top: 1px dashed var(--border-color);
}

.status-badge {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
}

.status-badge.watching { background: #dcfce7; color: #15803d; }
.status-badge.watched { background: #dbeafe; color: #1d4ed8; }
.status-badge.dropped { background: #fee2e2; color: #b91c1c; }

.progress-text { font-size: 12px; color: var(--text-secondary); font-weight: 600; }

.user-rating {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #f59e0b;
  font-weight: 800;
  font-size: 14px;
  margin-left: auto;
}

.collection-comment {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  background: var(--bg-color);
  padding: 10px 14px;
  border-radius: 12px;
  border-left: 4px solid var(--border-color);
}

.collection-comment .el-icon { color: var(--text-secondary); opacity: 0.5; margin-top: 2px; }
.collection-comment .comment-text {
  font-size: 13px;
  color: var(--text-secondary);
  font-style: italic;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 时间线样式 */
.timeline-container {
  display: flex;
  flex-direction: column;
  padding-top: 16px;
}

.timeline-item {
  display: flex;
  gap: 20px;
}

.timeline-left {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 20px;
}

.timeline-dot {
  width: 10px;
  height: 10px;
  background: #6366f1;
  border-radius: 50%;
  border: 2px solid #fff;
  box-shadow: 0 0 0 1px #e2e8f0;
  margin-top: 6px;
  z-index: 1;
}

.timeline-line {
  width: 2px;
  flex: 1;
  background: #f1f5f9;
  margin: 4px 0;
}

.timeline-item:last-child .timeline-line {
  display: none;
}

.timeline-content {
  flex: 1;
  padding-bottom: 30px;
}

.content-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.action-text {
  color: var(--text-main);
  font-size: 14px;
}

.status-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
}

.status-tag.watching { background: #dcfce7; color: #15803d; }
.status-tag.watched { background: #dbeafe; color: #1d4ed8; }
.status-tag.dropped { background: #fee2e2; color: #b91c1c; }

.highlight-text { color: #6366f1; font-weight: 600; }
.rating-text { color: #f59e0b; font-weight: 700; }
.time-stamp { color: var(--text-secondary); font-size: 12px; margin-left: auto; }

.comment-text {
  background: var(--bg-color);
  padding: 10px 14px;
  border-radius: 10px;
  color: var(--text-secondary);
  font-size: 13px;
  font-style: italic;
  margin-bottom: 12px;
  border-left: 3px solid var(--border-color);
}

.subject-mini-card {
  display: flex;
  gap: 12px;
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 10px;
  cursor: pointer;
  transition: all 0.2s;
  max-width: 440px;
}

.subject-mini-card:hover {
  border-color: var(--primary-color);
  background: var(--bg-color);
  transform: translateX(4px);
}

.mini-cover {
  width: 50px;
  height: 70px;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  flex-shrink: 0;
}

.mini-cover img { width: 100%; height: 100%; object-fit: cover; }
.mini-info { flex: 1; display: flex; flex-direction: column; justify-content: center; min-width: 0; }
.mini-title { font-weight: 700; font-size: 13px; color: var(--text-main); margin-bottom: 2px; }
.mini-sub { font-size: 11px; color: var(--text-secondary); margin-bottom: 4px; }
.mini-meta { display: flex; align-items: center; gap: 6px; }
.mini-tag { border-radius: 3px; font-size: 10px; }
.mini-episodes { font-size: 11px; color: var(--text-secondary); opacity: 0.7; }

/* 话题卡片美化 */
.forumList {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 16px;
}

.forumList .card {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  transition: all 0.3s;
  cursor: pointer;
  height: auto;
  position: relative;
}

.forumList .card:hover {
  transform: translateY(-2px);
  box-shadow: var(--card-shadow);
  border-color: var(--primary-color);
}

.forumList .thumb {
  width: 100px;
  height: 140px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.forumList .thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.forumList .entry {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.forumList .title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-main);
  margin-bottom: 6px;
}

.forumList .subj {
  font-size: 13px;
  color: var(--primary-color);
  font-weight: 600;
  margin-bottom: 8px;
}

.forumList .meta {
  font-size: 12px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.forumList .status {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}

.forumList .status.locked { background: #fef3c7; color: #d97706; }
.forumList .status.rejected { background: #fee2e2; color: #dc2626; }
.forumList .status.open { background: #dcfce7; color: #16a34a; }

.cardActions { 
  position: absolute; 
  top: 8px; 
  right: 12px; 
  display: flex; 
  gap: 6px; 
}
</style>
