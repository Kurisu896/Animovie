// 后端API的基础URL常量，所有API请求都基于此URL构建
export const BASE = 'http://localhost:8080'

/**
 * 生成请求头函数
 * @returns {Object} 包含Content-Type和Authorization的请求头对象
 */
function headers(){
  // 创建请求头对象，默认设置Content-Type为application/json
  const h = { 'Content-Type': 'application/json' }
  // 从localStorage中获取token
  const t = localStorage.getItem('token')
  // 如果token存在，添加Authorization头，格式为Bearer token
  if(t){ h['Authorization'] = 'Bearer ' + t }
  // 返回构建好的请求头
  return h
}

/**
 * 处理未授权（401）错误的函数
 * 当后端返回401状态码时，清除本地token并跳转到登录页
 */
function handleUnauthorized(){
  try{
    // 清除localStorage中的token
    localStorage.removeItem('token') 
  }catch(e){}
  try{
    // 跳转到登录页面
    window.location.href = '/login' 
  }catch(e){}
}

/**
 * 发送JSON格式的POST请求
 * @param {string} path - 请求路径，如'/collection/upsert'
 * @param {Object} body - 请求体，JSON对象
 * @returns {Promise<Object>} 后端返回的JSON响应
 */
export async function post(path, body){
  // 使用fetch API发送POST请求
  const res = await fetch(BASE+path, {
    method:'POST', // 请求方法为POST
    headers: headers(), // 使用headers()函数生成请求头，自动带上token
    body: JSON.stringify(body) // 将请求体转换为JSON字符串
  })
  
  // 如果返回401状态码，处理未授权错误
  if(res.status===401){
    handleUnauthorized(); // 清除token并跳转到登录页
    throw new Error('Unauthorized') // 抛出错误
  }
  
  // 将响应转换为JSON并返回
  return res.json()
}

/**
 * 发送表单格式的POST请求（不带Authorization token）
 * 适用于登录、注册等无需认证的表单提交
 * @param {string} path - 请求路径，如'/auth/register'
 * @param {Object} data - 表单数据对象
 * @returns {Promise<Object>} 后端返回的JSON响应
 */
export async function form(path, data){
  // 使用fetch API发送POST请求
  const res = await fetch(BASE+path, {
    method:'POST', // 请求方法为POST
    // 设置Content-Type为application/x-www-form-urlencoded（表单格式）
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    // 将表单数据转换为URL编码的字符串，如'username=用户名&password=密码'
    body: new URLSearchParams(data).toString()
  })
  
  // 将响应转换为JSON并返回
  return res.json()
}

/**
 * 发送表单格式的POST请求（带Authorization token）
 * 适用于需要认证的表单提交
 * @param {string} path - 请求路径
 * @param {Object} data - 表单数据对象
 * @returns {Promise<Object>} 后端返回的JSON响应
 */
export async function formAuth(path, data){
  // 创建请求头对象，设置Content-Type为application/x-www-form-urlencoded
  const h = { 'Content-Type': 'application/x-www-form-urlencoded' }
  // 从localStorage中获取token
  const t = localStorage.getItem('token')
  // 如果token存在，添加Authorization头
  if(t){ h['Authorization'] = 'Bearer ' + t }
  
  // 使用fetch API发送POST请求
  const res = await fetch(BASE+path, {
    method:'POST', // 请求方法为POST
    headers: h, // 使用自定义的请求头，包含token
    body: new URLSearchParams(data).toString() // 将表单数据转换为URL编码的字符串
  })
  
  // 如果返回401状态码，处理未授权错误
  if(res.status===401){
    handleUnauthorized(); // 清除token并跳转到登录页
    throw new Error('Unauthorized') // 抛出错误
  }
  
  // 将响应转换为JSON并返回
  return res.json()
}

/**
 * 发送GET请求，支持参数拼接
 * @param {string} path - 请求路径，如'/auth/signin'
 * @param {Object} [params] - 请求参数对象，可选
 * @returns {Promise<Object>} 后端返回的JSON响应
 */
export async function get(path, params){
  // 创建URL对象，基于BASE+path
  const url = new URL(BASE+path)
  
  // 如果有参数，将参数拼接到URL上
  if(params){
    // 遍历参数对象的所有键
    Object.keys(params).forEach(k=> {
      const v = params[k]
      // 只添加非undefined、非null、非空字符串的参数
      if(v!==undefined && v!==null && v!=='') {
        // 将参数添加到URL的查询字符串中
        url.searchParams.append(k, v)
      }
    })
  }
  
  // 使用fetch API发送GET请求
  const res = await fetch(url, {
    headers: headers() // 使用headers()函数生成请求头，自动带上token
  })
  
  // 如果返回401状态码，处理未授权错误
  if(res.status===401){
    handleUnauthorized(); // 清除token并跳转到登录页
    throw new Error('Unauthorized') // 抛出错误
  }
  
  // 将响应转换为JSON并返回
  return res.json()
}

/**
 * 发送DELETE请求
 * @param {string} path - 请求路径
 * @returns {Promise<Object>} 后端返回的JSON响应
 */
export async function del(path){
  // 使用fetch API发送DELETE请求
  const res = await fetch(BASE+path, {
    method:'DELETE', // 请求方法为DELETE
    headers: headers() // 使用headers()函数生成请求头，自动带上token
  })
  
  // 如果返回401状态码，处理未授权错误
  if(res.status===401){
    handleUnauthorized(); // 清除token并跳转到登录页
    throw new Error('Unauthorized') // 抛出错误
  }
  
  // 将响应转换为JSON并返回
  return res.json()
}
