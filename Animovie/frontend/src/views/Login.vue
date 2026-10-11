<!-- 页面：登录 -->
<template>
  <div class="auth-page-container">
    <el-card class="modern-card auth-card">
      <div class="auth-header">
        <h3 class="page-title">登录</h3>
        <p class="auth-subtitle">欢迎回来，请登录您的账号</p>
      </div>
      
      <el-form :model="form" label-position="top" @submit.prevent="submit" class="auth-form">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" :maxlength="30" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" :maxlength="20" prefix-icon="Lock" show-password />
        </el-form-item>
        
        <div class="auth-actions">
          <el-button type="primary" native-type="submit" :loading="loading" class="submit-btn">立即登录</el-button>
        </div>
        
        <div class="auth-footer">
          <span>还没有账号？</span>
          <el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
        </div>

        <el-alert v-if="error" :title="error" type="error" show-icon closable class="auth-alert" />
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { form } from '../utils/api'
import { useAuthStore } from '../store'
import { User, Lock } from '@element-plus/icons-vue'

export default {
  components: { User, Lock },
  data(){
    return {
      form:{ username:'', password:'' },
      loading:false,
      error:''
    }
  },
  setup(){
    const auth = useAuthStore();
    return { auth }
  },
  methods:{
    async submit(){
      this.loading = true; this.error=''
      try{
        let r = await form('/auth/signin', this.form) 
        if(r.code===0){
          this.auth.setToken(r.data);
          this.$router.push('/') 
        }
        else{
          this.error = r.msg || '登录失败' 
        }
      }
      catch(e){
        this.error = e.message 
      }
      finally{
        this.loading=false 
      }
    }
  }
}
</script>

<style scoped>
.auth-card {
  width: 100%;
  max-width: 420px;
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
