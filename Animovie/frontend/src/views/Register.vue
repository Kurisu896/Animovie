<!-- 页面：注册 -->
<template>
  <div class="auth-page-container">
    <el-card class="modern-card auth-card">
      <div class="auth-header">
        <h3 class="page-title">注册</h3>
        <p class="auth-subtitle">加入社区，开启您的影视之旅</p>
      </div>
      
      <el-form :model="form" label-position="top" @submit.prevent="submit" class="auth-form">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" :maxlength="64" show-word-limit prefix-icon="User" />
        </el-form-item>
        
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="请输入昵称" :maxlength="15" show-word-limit prefix-icon="Postcard" />
        </el-form-item>
        
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" :maxlength="20" prefix-icon="Lock" show-password />
          <div class="pwd-tip">
            <el-icon :class="pwdOk ? 'status-ok' : 'status-bad'">
              <component :is="pwdOk ? 'CircleCheckFilled' : 'CircleCloseFilled'" />
            </el-icon>
            <span>8-20位，仅包含字母或数字</span>
          </div>
        </el-form-item>
        
        <div class="auth-actions">
          <el-button type="primary" native-type="submit" :loading="loading" class="submit-btn">立即注册</el-button>
        </div>
        
        <div class="auth-footer">
          <span>已有账号？</span>
          <el-link type="primary" @click="$router.push('/login')">立即登录</el-link>
        </div>

        <el-alert v-if="info" :title="info" type="success" show-icon closable class="auth-alert" />
        <el-alert v-if="error" :title="error" type="error" show-icon closable class="auth-alert" />
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { form } from '../utils/api'
import { User, Lock, Postcard, CircleCheckFilled, CircleCloseFilled } from '@element-plus/icons-vue'

export default {
  components: { User, Lock, Postcard, CircleCheckFilled, CircleCloseFilled },
  data(){ 
    return { 
      form:{ username:'', password:'', nickname:'' }, 
      loading:false, 
      error:'', 
      info:'' 
    } 
  },
  methods:{
    async submit(){
      this.loading = true; this.error=''; this.info=''
      try{
        const r = await form('/auth/register', this.form)
        if(r.code===0){ 
          this.$message.success('注册成功');
          this.$router.push('/login') 
        }
        else{ 
          this.error = r.msg || '注册失败' 
        }
      }catch(e){ 
        this.error = e.message 
      }
      finally{ 
        this.loading=false 
      }
    }
  },
  computed:{ 
    pwdOk(){ return /^[A-Za-z0-9]{8,20}$/.test(this.form.password||'') } 
  }
}
</script>

<style scoped>
.auth-card {
  width: 100%;
  max-width: 480px;
  padding: 20px;
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.auth-subtitle {
  color: #64748b;
  font-size: 14px;
  margin-top: 8px;
}

.auth-form {
  margin-top: 20px;
}

.pwd-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #64748b;
  margin-top: 8px;
}

.status-ok { color: #10b981; }
.status-bad { color: #ef4444; }

.auth-actions {
  margin-top: 32px;
}

.submit-btn {
  width: 100%;
  height: 42px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
}

.auth-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 14px;
  color: #64748b;
}

.auth-alert {
  margin-top: 20px;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #334155;
  padding-bottom: 8px;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 12px;
}
</style>
