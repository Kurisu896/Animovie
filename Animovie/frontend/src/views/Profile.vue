<!-- 页面：个人资料 -->
<template>
  <div class="page-container">
    <div class="toolbar">
      <h3 class="page-title">个人资料</h3>
      <div class="tools">
        <el-button type="primary" plain @click="pwdVisible=true" icon="Lock">修改密码</el-button>
      </div>
    </div>

    <el-card class="modern-card profile-card">
      <el-form :model="form" label-position="top" class="profile-form">
        <!-- 头像设置区块 -->
        <div class="form-section">
          <div class="section-title">
            <el-icon><Picture /></el-icon>
            <span>头像设置</span>
          </div>
          <div class="avatar-upload-container">
            <div class="avatar-preview">
              <img v-if="form.avatarPath" :src="base+form.avatarPath" alt="avatar" />
              <div v-else class="avatar-placeholder">
                <el-icon><User /></el-icon>
              </div>
            </div>
            <div class="upload-actions">
              <el-upload 
                :action="base+'/auth/avatar'"
                :headers="uploadHeaders"
                :name="'file'"
                :show-file-list="false"
                :on-success="onAvatarUploaded"
                class="avatar-uploader"
              >
                <el-button type="primary" size="small">更换头像</el-button>
              </el-upload>
              <p class="upload-tip">支持 JPG、PNG 格式，大小不超过 2MB</p>
            </div>
          </div>
        </div>

        <el-divider />

        <!-- 基本信息区块 -->
        <div class="form-section">
          <div class="section-title">
            <el-icon><User /></el-icon>
            <span>基本信息</span>
          </div>
          
          <el-form-item label="昵称">
            <el-input 
              v-model="form.nickname" 
              placeholder="起一个好听的名字吧" 
              :maxlength="15" 
              show-word-limit 
              class="modern-input"
            >
              <template #prefix>
                <el-icon><EditPen /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item label="个人签名">
            <el-input 
              v-model="form.signature" 
              type="textarea"
              :rows="3"
              placeholder="介绍一下你自己..." 
              :maxlength="255" 
              show-word-limit 
              class="modern-input"
            />
          </el-form-item>
        </div>

        <div class="form-footer">
          <el-button 
            type="primary" 
            @click="saveAll" 
            :loading="saving"
            class="save-btn"
            size="large"
            round
          >保存所有修改</el-button>
        </div>
      </el-form>
    </el-card>

    <!-- 修改密码对话框 -->
    <el-dialog 
      v-model="pwdVisible" 
      title="修改登录密码" 
      width="440px"
      class="modern-dialog"
      append-to-body
    >
      <div class="dialog-content">
        <el-form label-position="top">
          <el-form-item label="当前密码">
            <el-input 
              v-model="pwd.current" 
              type="password" 
              show-password
              placeholder="请输入当前使用的密码" 
              class="modern-input"
            />
          </el-form-item>
          
          <el-form-item label="新密码">
            <el-input 
              v-model="pwd.new" 
              type="password" 
              show-password
              placeholder="8-20位字母或数字" 
              class="modern-input"
            />
            <div class="pwd-tip-row">
              <el-icon :class="pwdOk ? 'ok' : 'bad'">
                <CircleCheck v-if="pwdOk" /><CircleClose v-else />
              </el-icon> 
              <span>8-20 位，仅包含字母或数字</span>
            </div>
          </el-form-item>
          
          <el-form-item label="确认新密码">
            <el-input 
              v-model="pwd.confirm" 
              type="password" 
              show-password
              placeholder="请再次输入新密码" 
              class="modern-input"
            />
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="pwdVisible=false" round>取消</el-button>
          <el-button 
            type="primary" 
            @click="changePassword" 
            :loading="changing"
            round
            class="submit-btn"
          >更新密码</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { get, BASE, formAuth } from '../utils/api'
import { useAuthStore } from '../store'
import { User, Picture, EditPen, Lock, CircleCheck, CircleClose } from '@element-plus/icons-vue'

export default {
  components: { User, Picture, EditPen, Lock, CircleCheck, CircleClose },
  // Vue 3 Composition API的setup函数
  setup(){
    // 获取认证状态管理实例
    const auth = useAuthStore();
    // 返回auth对象，使其在组件中可用
    return { auth } 
  },
  
  // 组件数据定义
  data(){
    // 返回组件的响应式数据
    return {
      form:{ avatarPath:'', nickname:'', signature:'' }, // 个人资料表单数据
      base: BASE, // 从../utils/api.js导入的BASE常量，用于构建请求URL
      // 上传请求头，包含Authorization token，从localStorage获取
      uploadHeaders:{ Authorization:'Bearer '+(localStorage.getItem('token')||'') }, 
      fileList:[], // 上传文件列表
      saving:false, // 保存按钮加载状态
      pwd:{ current:'', new:'', confirm:'' }, // 密码修改表单数据
      changing:false, // 密码修改按钮加载状态
      pwdVisible:false // 修改密码对话框显示状态
    } 
  },
  
  // 组件挂载完成后的生命周期钩子
  async mounted(){
    // 调用load方法加载用户资料
    await this.load() 
  },
  
  // 计算属性定义
  computed:{
    // 检查新密码是否符合规则
    pwdOk(){
      // 使用正则表达式验证密码：8-20位，仅包含字母或数字
      return /^[A-Za-z0-9]{8,20}$/.test(this.pwd.new||'') 
    }
  },
  
  // 组件方法定义
  methods:{
    // 加载用户资料方法
    async load(){
      // 调用get方法发送GET请求，路径为/auth/me
      // get方法来自../utils/api.js，用于发送带有Authorization header的GET请求
      const r = await get('/auth/me'); 
      
      // 如果请求成功（code===0）
      if(r.code===0){
        // 更新form数据
        this.form.avatarPath=r.data.avatarPath||''; // 头像路径
        this.form.signature=r.data.signature||''; // 签名
        this.form.nickname=r.data.nickname||''; // 昵称
        this.fileList=[]; // 清空文件列表
      } 
    },
    
    // 头像上传成功回调方法
    onAvatarUploaded(resp){
      // 如果响应成功（code===0）
      if(resp && resp.code===0){
        // 更新form.avatarPath为返回的头像路径
        this.form.avatarPath = resp.data;
        // 显示成功消息
        this.$message.success('头像已更新');
        // 清空文件列表
        this.fileList=[]; 
      } 
      // 兼容直接返回字符串的情况
      else if(typeof resp==='string'){
        // 更新form.avatarPath为返回的字符串
        this.form.avatarPath = resp;
        // 清空文件列表
        this.fileList=[]; 
      } 
    },
    
    // 保存所有个人资料方法
    async saveAll(){
      // 验证昵称长度不超过20字符
      if((this.form.nickname||'').length>20){
        // 显示错误消息
        this.$message.error('昵称最多 20 字符'); 
        return; // 终止执行
      }
      
      // 设置保存状态为true
      this.saving=true;
      
      try{
        // 调用formAuth方法更新昵称
        // formAuth方法来自../utils/api.js，用于发送带有Authorization header的表单请求
        const r1 = await formAuth('/auth/nickname', { nickname:this.form.nickname||'' });
        
        // 如果更新昵称失败
        if(r1.code!==0){
          // 显示错误消息
          this.$message.error(r1.msg||'昵称保存失败'); 
          return; // 终止执行
        }
        
        // 调用formAuth方法更新签名
        const r2 = await formAuth('/auth/signature', { signature:this.form.signature||'' });
        
        // 如果更新签名失败
        if(r2.code!==0){
          // 显示错误消息
          this.$message.error(r2.msg||'签名保存失败'); 
          return; // 终止执行
        }
        
        // 显示成功消息
        this.$message.success('个人资料已保存');
      } 
      finally {
        // 无论成功或失败，设置保存状态为false
        this.saving=false; 
      }
    },
    
    // 验证密码是否符合规则的方法
    pwdValid(){
      // 使用正则表达式验证密码：8-20位，仅包含字母或数字
      return /^[A-Za-z0-9]{8,20}$/.test(this.pwd.new||'') 
    },
    
    // 修改密码方法
    async changePassword(){
      // 验证新密码是否符合规则
      if(!this.pwdValid()){
        // 显示错误消息
        this.$message.error('新密码不符合要求'); 
        return; // 终止执行
      }
      
      // 验证两次输入的新密码是否一致
      if((this.pwd.new||'')!== (this.pwd.confirm||'')){
        // 显示错误消息
        this.$message.error('两次输入不一致'); 
        return; // 终止执行
      }
      
      // 设置修改密码状态为true
      this.changing=true;
      
      try{
        // 调用formAuth方法修改密码
        // formAuth方法来自../utils/api.js，用于发送带有Authorization header的表单请求
        const r = await formAuth('/auth/password', { 
          currentPassword:this.pwd.current||'', // 当前密码
          newPassword:this.pwd.new||'' // 新密码
        });
        
        // 如果修改密码成功
        if(r.code===0){
          // 显示成功消息
          this.$message.success('修改密码成功，请重新登录');
          // 清空密码表单
          this.pwd={ current:'', new:'', confirm:'' };
          // 关闭修改密码对话框
          this.pwdVisible=false;
          // 调用auth状态管理的clear方法，清除认证状态
          this.auth.clear();
          // 使用Vue Router跳转到/login路径（登录页面）
          this.$router.push('/login'); 
        } 
        // 如果修改密码失败
        else {
          // 显示错误消息
          this.$message.error(r.msg||'失败'); 
        }
      } 
      finally {
        // 无论成功或失败，设置修改密码状态为false
        this.changing=false; 
      } 
    }
  }
}
</script>

<style scoped>
.profile-card {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px;
}

.profile-form {
  max-width: 600px;
  margin: 0 auto;
}

.form-section {
  margin-bottom: 32px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 700;
  color: var(--text-main);
  margin-bottom: 24px;
}

.section-title .el-icon {
  color: var(--primary-color);
  font-size: 20px;
}

/* 头像上传样式 */
.avatar-upload-container {
  display: flex;
  align-items: center;
  gap: 32px;
  padding: 8px 0;
}

.avatar-preview {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  background: var(--bg-color);
  border: 4px solid var(--card-bg);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  color: var(--text-secondary);
  opacity: 0.5;
}

.upload-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.upload-tip {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
}

/* 输入框样式已在 App.vue 中统一定义，这里做微调 */
.modern-input :deep(.el-textarea__inner) {
  padding: 12px;
}

.form-footer {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.save-btn {
  padding-left: 60px;
  padding-right: 60px;
}

/* 密码修改提示 */
.pwd-tip-row {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  font-size: 13px;
  color: var(--text-secondary);
}

.pwd-tip-row .el-icon {
  font-size: 16px;
}

.pwd-tip-row .ok { color: #10b981; }
.pwd-tip-row .bad { color: #ef4444; }

@media (max-width: 768px) {
  .profile-card {
    padding: 20px;
  }
  
  .avatar-upload-container {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
}
</style>
