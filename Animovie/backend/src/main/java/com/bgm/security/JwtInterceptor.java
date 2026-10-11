package com.bgm.security;
import com.bgm.utils.security.JwtUtil;
import com.bgm.mapper.UserMapper;
import com.bgm.domain.User;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT认证拦截器
 * 功能：拦截请求，验证JWT令牌的有效性
 * 实现：实现HandlerInterceptor接口，重写preHandle方法
 * 拦截逻辑：
 *   1. 允许OPTIONS请求
 *   2. 允许/auth路径下的请求（登录、注册等）
 *   3. 允许公开资源请求
 *   4. 验证JWT令牌
 *   5. 解析令牌获取用户信息
 *   6. 查询用户是否存在且状态正常
 * 调用位置：
 *   - Spring MVC请求处理前自动调用
 * 依赖：
 *   - userMapper：用户数据访问接口
 *   - JwtUtil：JWT工具类
 */
// Spring注解：将该类标记为Spring管理的组件，自动注入到Spring容器
@Component
// 实现HandlerInterceptor接口，成为Spring MVC拦截器
public class JwtInterceptor implements HandlerInterceptor {
    /**
     * 用户数据访问接口实例
     * 注入方式：Spring自动注入
     * 用途：查询用户信息，验证用户是否存在且状态正常
     */
    @org.springframework.beans.factory.annotation.Autowired
    private UserMapper userMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取请求URI
        String uri=request.getRequestURI();
        
        // 允许OPTIONS请求（跨域预检请求）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        // 允许/auth路径下的请求（登录、注册等公开接口）
        if(uri.startsWith("/auth")){
            return true;
        }
        
        // 检查是否为公开资源
        if(isPublic(request)){
            return true;
        }
        
        // 从请求头获取Authorization令牌
        String token=request.getHeader("Authorization"); 
        // 检查令牌是否为空
        if(token==null||token.isEmpty()){
            // 令牌为空，设置响应状态码为401（未授权），拦截请求
            response.setStatus(401);
            return false;
        }
        // 处理Bearer令牌格式
        if(token.startsWith("Bearer ")){
            // 移除Bearer前缀，提取实际令牌
            token=token.substring(7);
        }
        // 验证令牌是否有效
        if(!JwtUtil.isValidToken(token)){
            // 令牌无效，设置响应状态码为401，拦截请求
            response.setStatus(401);
            return false;
        }
        try{
            // 解析令牌获取Claims对象
            io.jsonwebtoken.Claims c = JwtUtil.parseClaims(token); 
            
            // 检查Claims是否为空
            if(c==null){ 
                response.setStatus(401); 
                return false; 
            }
            
            // 从Claims中提取用户名
            String username = (String)c.get("username");
            
            // 检查用户名是否为空
            if(username==null){ 
                response.setStatus(401); 
                return false; 
            }
            
            // 根据用户名查询用户
            // 使用LambdaQueryWrapper构建查询条件：用户名等于提取的username
            // 调用位置：userMapper.selectOne
            // 数据流向：username → LambdaQueryWrapper → userMapper.selectOne → User对象
            User u = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username)); 
            
            // 检查用户是否存在且状态正常（status=1）
            if(u==null || u.getStatus()!=1){ 
                response.setStatus(401); 
                return false; 
            }
        }catch(Exception e){ 
            // 发生异常，设置响应状态码为401，拦截请求
            response.setStatus(401); 
            return false; 
        }
        
        // 所有验证通过，放行请求
        return true; 
    }

    /**
     * 检查请求是否为公开资源
     * 功能：判断请求是否无需认证即可访问
     * 参数：
     *   - request：HttpServletRequest对象，包含请求信息
     * 返回值：
     *   - boolean：true表示公开资源，false表示需要认证
     * 公开资源规则：
     *   - /uploads/路径下的静态资源
     *   - GET请求且路径匹配以下规则：
     *     - /subject开头：主题相关接口
     *     - /discussion/topics开头：讨论主题列表
     *     - /discussion/all开头：所有讨论
     *     - /discussion/comments开头：评论列表
     *     - /subject-comments开头：主题评论
     *     - /activity/all开头：所有动态
     *     - /rank开头：排行榜
     *     - /search开头：搜索接口
     *     - /activity/user开头：用户动态
     *     - /collection/by-user开头：用户收藏
     */
    private boolean isPublic(HttpServletRequest request){
        // 获取请求方法和URI
        String m = request.getMethod(); 
        String u = request.getRequestURI();
        
        // 允许/uploads/路径下的静态资源（如头像、图片等）
        if(u.startsWith("/uploads/")) return true;
        
        // 只处理GET请求
        if("GET".equalsIgnoreCase(m)){
            // 主题相关接口：主题列表和详情
            if(u.startsWith("/subject")) return true; 
            // 讨论主题列表
            if(u.startsWith("/discussion/topics")) return true;
            // 所有讨论
            if(u.startsWith("/discussion/all")) return true;
            // 评论列表
            if(u.startsWith("/discussion/comments")) return true;
            // 主题评论
            if(u.startsWith("/subject-comments")) return true;
            // 所有动态
            if(u.startsWith("/activity/all")) return true;
            // 排行榜
            if(u.startsWith("/rank")) return true;
            // 搜索接口
            if(u.startsWith("/search")) return true;
            // 用户动态
            if(u.startsWith("/activity/user")) return true;
            // 用户收藏
            if(u.startsWith("/collection/by-user")) return true;
        }
        
        // 其他请求需要认证
        return false;
    }
}
