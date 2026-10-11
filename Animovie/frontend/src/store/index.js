// 导入Pinia状态管理库的defineStore函数
// 来源：pinia库，用于创建Pinia状态管理store实例
// 用途：定义全局状态管理store，管理应用中的共享数据
import { defineStore } from 'pinia'

// 定义并导出认证状态管理store
// 定义方式：使用defineStore函数，第一个参数为store的唯一标识'id'
// 导出后使用方式：import { useAuthStore } from '../store/index.js'，然后const auth = useAuthStore()
// 功能：管理用户认证相关的状态，包括token和role
// 数据流向：store中的状态可被多个组件共享和修改
// 应用场景：用于用户登录、权限控制、页面访问限制等
// 组件调用：在Login.vue、Profile.vue、UserHome.vue等需要认证的组件中调用
// 作用范围：全局作用域，所有组件均可访问
// 存储方式：状态保存在内存中，同时持久化到localStorage
// 初始化方式：页面加载时从localStorage读取初始状态
export const useAuthStore = defineStore('auth', {
  // state选项：定义store的响应式状态
  // 类型：函数，返回初始状态对象
  // 调用时机：store实例创建时自动调用
  // 初始化数据来源：localStorage，实现页面刷新后状态保持
  // 状态属性：
  //   - token: 用户认证令牌
  //   - role: 用户角色
  state: () => ({
    // token属性：存储用户的JWT认证令牌
    // 初始值来源：从浏览器localStorage中读取'auth-token'键值
    // 初始化逻辑：若localStorage中不存在'auth-token'，则初始值为空字符串
    // 数据流向：
    //   - 写入：登录成功时从Login.vue组件调用setToken方法设置
    //   - 读取：在api.js中用于添加请求头认证
    //   - 持久化：每次修改后自动存储到localStorage
    // 作用：用于API请求的身份验证，判断用户是否已登录
    token: localStorage.getItem('token') || '',
    
    // role属性：存储用户的角色信息
    // 初始值来源：从浏览器localStorage中读取'auth-role'键值
    // 初始化逻辑：若localStorage中不存在'auth-role'，则初始值为空字符串
    // 数据流向：
    //   - 写入：登录成功时从setToken方法中解析token获取
    //   - 读取：在UserHome.vue等组件中用于权限控制
    //   - 持久化：每次修改后自动存储到localStorage
    // 可能值：'USER'（普通用户）、'ADMIN'（管理员）、''（未登录）
    // 作用：用于页面级别的权限控制，如显示/隐藏管理员操作按钮
    role: localStorage.getItem('role') || ''
  }),
  
  // actions选项：定义修改store状态的方法
  // 类型：对象，包含多个方法
  // 调用方式：通过store实例调用，如auth.setToken(token)
  // 特点：支持异步操作，可以包含任意业务逻辑
  // 应用场景：登录、登出、修改用户信息等
  actions: {
    // setToken方法：设置用户认证令牌和角色
    // 方法功能：更新store中的token和role状态，并持久化到localStorage
    // 调用场景：
    //   - 登录成功时：从Login.vue组件调用，传入后端返回的JWT token
    //   - 注册成功时：从Register.vue组件调用（如果有注册功能）
    //   - 刷新token时：从api.js中的请求拦截器调用
    // 参数说明：
    //   - t：字符串类型，后端返回的JWT token
    // 数据流向：
    //   1. 接收后端返回的token参数
    //   2. 更新store中的token状态
    //   3. 将token存储到localStorage
    //   4. 解析token获取role信息
    //   5. 更新store中的role状态
    //   6. 将role存储到localStorage
    setToken(t) {
      // 更新store实例中的token状态
      // 数据流向：从参数t -> store.token
      this.token = t 
      
      // 将token存储到localStorage中，实现页面刷新后状态保持
      // 数据流向：从store.token -> localStorage.setItem('auth-token', t)
      localStorage.setItem('token', t) 
      
      // 尝试从JWT token中解析出用户角色信息
      // JWT token结构：header.payload.signature，使用.分隔
      // 解析逻辑：
      //   1. 将token按.分割为数组
      //   2. 获取payload部分（索引为1）
      //   3. 使用atob进行base64解码
      //   4. 将解码后的字符串转换为JSON对象
      //   5. 从JSON对象中提取role字段
      try {
        // 分割JWT token，获取payload部分
        // 操作：将token字符串按.分割为数组，获取索引为1的元素
        // 数据来源：参数t（JWT token）
        // 预期结果：得到base64编码的payload字符串
        const tokenParts = (t || '').split('.')
        const payloadBase64 = tokenParts[1] || ''
        
        // 解码base64编码的payload字符串
        // 方法调用：使用浏览器内置的atob函数
        // 功能：将base64编码的字符串转换为原始字符串
        // 浏览器兼容性：所有现代浏览器支持
        const payloadJson = atob(payloadBase64)
        
        // 将JSON字符串解析为JavaScript对象
        // 方法调用：使用JavaScript内置的JSON.parse函数
        // 功能：将JSON格式字符串转换为JavaScript对象
        // 数据流向：从payloadJson字符串 -> 解析为p对象
        const p = JSON.parse(payloadJson) || {}
        
        // 从payload对象中提取role字段
        // 数据流向：从p对象 -> r变量
        // 默认值：若p.role不存在，则使用空字符串
        const r = p.role || '' 
        
        // 更新store实例中的role状态
        // 数据流向：从r变量 -> store.role
        this.role = r
        
        // 将role存储到localStorage中，实现页面刷新后状态保持
        // 数据流向：从store.role -> localStorage.setItem('auth-role', r)
        localStorage.setItem('role', r)
      }
      // 异常处理：若token解析失败（如token格式错误、无效token等）
      // 触发条件：
      //   - token格式不正确（缺少.分隔符）
      //   - payload部分不是有效的base64编码
      //   - 解码后的payload不是有效的JSON格式
      //   - payload中没有role字段
      catch(e) {
        // 重置store中的role状态为空字符串
        // 数据流向：空字符串 -> store.role
        this.role = ''
        
        // 从localStorage中移除role条目
        // 操作：调用localStorage.removeItem方法
        // 功能：清除localStorage中的'auth-role'键值对
        // 目的：确保状态一致性，避免无效role残留
        localStorage.removeItem('role')
      }
    },
    
    // clear方法：清除用户认证状态
    // 功能：
    //   1. 清空store中的token和role状态
    //   2. 移除localStorage中的对应条目
    //   3. 实现用户登出功能
    // 调用场景：
    //   - 用户主动登出时（如点击登出按钮）
    //   - token过期或无效时
    //   - 权限验证失败时
    // 组件调用：在Header.vue、Profile.vue等组件中调用
    // 数据流向：
    //   1. 将store.token设为空字符串
    //   2. 将store.role设为空字符串
    //   3. 从localStorage中移除'auth-token'
    //   4. 从localStorage中移除'auth-role'
    clear() {
      // 重置store中的token状态为空字符串
      // 数据流向：空字符串 -> store.token
      this.token = ''
      
      // 重置store中的role状态为空字符串
      // 数据流向：空字符串 -> store.role
      this.role = ''
      
      // 从localStorage中移除token条目
      // 操作：调用localStorage.removeItem方法
      // 功能：清除localStorage中的'auth-token'键值对
      localStorage.removeItem('token')
      
      // 从localStorage中移除role条目
      // 操作：调用localStorage.removeItem方法
      // 功能：清除localStorage中的'auth-role'键值对
      localStorage.removeItem('role')
    }
  }
})
