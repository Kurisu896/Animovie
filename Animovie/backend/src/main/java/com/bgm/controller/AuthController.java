package com.bgm.controller;

import com.bgm.common.ApiResponse;
import com.bgm.domain.User;
import com.bgm.service.UserService;
import com.bgm.utils.security.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 允许跨域请求
 */
@CrossOrigin

/**
 * 控制器：认证与用户资料（/auth）
 * 包含：注册、登录、当前用户信息、资料更新（头像/昵称/签名）、修改密码、管理员封禁/解封
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    /**
     * 用户服务，用于处理用户相关业务逻辑
     */
    @Autowired
    UserService userService;
    
    /**
     * 用户Mapper，用于直接操作数据库
     */
    @Autowired
    com.bgm.mapper.UserMapper userMapper;
    
    /**
     * 健康检查接口
     * @return ApiResponse<String> - 返回 "pong" 表示服务正常
     */
    @GetMapping("/ping")
    public ApiResponse<String> ping(){
        // 返回成功响应，内容为 "pong"
        return ApiResponse.ok("pong");
    }
    
    /**
     * 通过GET请求获取token（兼容旧版客户端）
     * @param username 用户名
     * @param password 密码
     * @return ApiResponse<String> - 返回JWT token
     */
    @GetMapping("/token")
    public ApiResponse<String> token(@RequestParam String username, @RequestParam String password){
        // 调用login方法处理登录逻辑
        return login(username, password);
    }
    /**
     * 注册用户：校验用户名/昵称/密码长度与格式，返回创建的用户
     * @param username 用户名
     * @param password 密码
     * @param nickname 昵称（可选）
     * @return ApiResponse<User> - 返回创建的用户对象
     */
    @PostMapping("/register")
    public ApiResponse<User> register(
            @RequestParam String username,      // 从form表单中获取用户名
            @RequestParam String password,      // 从form表单中获取密码
            @RequestParam(required=false) String nickname) { // 从form表单中获取昵称（可选）
        
        // 校验用户名：非空且长度不超过30字符
        if(username == null || username.trim().isEmpty() || username.length() > 30) {
            return ApiResponse.fail("用户名长度需在 1-30 之间");
        }
        
        // 校验昵称：如果提供，长度不超过20字符
        if(nickname != null && nickname.length() > 20) {
            return ApiResponse.fail("昵称最多 20 个字符");
        }
        
        // 校验密码长度：8-20字符
        if(password == null || password.length() < 8 || password.length() > 20) {
            return ApiResponse.fail("密码需 8-20 位");
        }
        
        // 校验密码格式：仅包含字母和数字
        if(!password.matches("[A-Za-z0-9]{8,20}")) {
            return ApiResponse.fail("密码仅可包含字母和数字");
        }
        
        // 调用服务层注册用户
        User u = userService.register(username, password, nickname);
        
        // 如果用户名已存在，返回失败响应
        if(u == null) {
            return ApiResponse.fail("用户名已存在");
        }
        
        // 返回成功响应，包含创建的用户对象
        return ApiResponse.ok(u);
    }
    
    /**
     * 生成模拟token（用于测试）
     * @param username 用户名
     * @return ApiResponse<String> - 返回JWT token
     */
    @PostMapping("/mockToken")
    public ApiResponse<String> mockToken(@RequestParam String username){
        // 验证用户是否存在
        User u = userService.verify(username, "");
        
        // 如果用户不存在，返回失败响应
        if(u == null) {
            return ApiResponse.fail("no such user");
        }
        
        // 生成JWT token
        String token = com.bgm.utils.security.JwtUtil.createToken(u);
        
        // 返回成功响应，包含生成的token
        return ApiResponse.ok(token);
    }
    
    /**
     * 登录：校验用户存在与未封禁、密码正确，返回 JWT token
     * @param username 用户名
     * @param password 密码
     * @return ApiResponse<String> - 返回JWT token
     */
    @PostMapping("/signin")
    public ApiResponse<String> login(@RequestParam String username, @RequestParam String password){
        try {
            // 根据用户名查询用户
            User u = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
            
            // 如果用户不存在，返回失败响应
            if(u == null) {
                return ApiResponse.fail("用户不存在");
            }
            
            // 如果用户已被封禁（status=0），返回失败响应
            if(u.getStatus() != 1) {
                return ApiResponse.fail("用户已被封禁");
            }
            
            // 创建BCrypt密码编码器
            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder enc = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
            
            // 校验密码是否匹配
            if(!enc.matches(password, u.getPasswordHash())) {
                return ApiResponse.fail("密码错误");
            }
            
            // 生成JWT token
            String token = JwtUtil.createToken(u);
            
            // 返回成功响应，包含生成的token
            return ApiResponse.ok(token);
        } catch (Exception e) {
            // 捕获异常，返回失败响应
            return ApiResponse.fail(e.getMessage());
        }
    }

    /**
     * 修改密码：校验原密码与新密码规则，更新为新哈希
     * @param token 请求头中的Authorization token
     * @param currentPassword 当前密码
     * @param newPassword 新密码
     * @return ApiResponse<String> - 返回操作结果
     */
    @PostMapping("/password")
    public ApiResponse<String> changePassword(
            @RequestHeader("Authorization") String token,  // 请求头中的Authorization token
            @RequestParam String currentPassword,          // 当前密码
            @RequestParam String newPassword) {             // 新密码
        
        // 从token中解析出用户ID
        Long id = userIdFromToken(token);
        
        // 如果解析失败，返回未授权响应
        if(id == null) {
            return ApiResponse.fail("unauthorized");
        }
        
        // 根据ID查询用户
        User u = userMapper.selectById(id);
        
        // 如果用户不存在，返回未授权响应
        if(u == null) {
            return ApiResponse.fail("unauthorized");
        }
        
        // 创建BCrypt密码编码器
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder enc = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        
        // 校验原密码是否正确
        if(!enc.matches(currentPassword, u.getPasswordHash())) {
            return ApiResponse.fail("原密码不正确");
        }
        
        // 校验新密码长度：8-20字符
        if(newPassword == null || newPassword.length() < 8 || newPassword.length() > 20) {
            return ApiResponse.fail("新密码需 8-20 位");
        }
        
        // 校验新密码格式：仅包含字母和数字
        if(!newPassword.matches("[A-Za-z0-9]{8,20}")) {
            return ApiResponse.fail("新密码仅可包含字母和数字");
        }
        
        // 对新密码进行BCrypt加密
        String hash = enc.encode(newPassword);
        
        // 创建更新对象，设置ID和新密码哈希
        User upPass = new User();
        upPass.setId(id);
        upPass.setPasswordHash(hash);
        
        // 更新数据库中的密码
        int r = userMapper.updateById(upPass);
        
        // 返回更新结果：成功或失败
        return r > 0 ? ApiResponse.ok("ok") : ApiResponse.fail("failed");
    }

    /**
     * 从JWT token中解析出用户ID
     * @param token JWT token
     * @return Long - 用户ID，解析失败返回null
     */
    private Long userIdFromToken(String token){
        // 如果token为null，返回null
        if(token == null) return null;
        
        // 如果token以"Bearer "开头，去除前缀
        if(token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 解析JWT token，获取Claims
        io.jsonwebtoken.Claims c = JwtUtil.parseClaims(token);
        
        // 如果解析失败，返回null
        if(c == null) return null;
        
        // 从Claims中获取用户名
        String username = (String) c.get("username");
        
        // 根据用户名查询用户
        User u = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        
        // 如果用户不存在或已被封禁，返回null
        if(u == null || u.getStatus() != 1) {
            return null;
        }
        
        // 返回用户ID
        return u.getId();
    }
    
    /**
     * 检查用户是否为管理员
     * @param token JWT token
     * @return boolean - true表示是管理员，false表示不是
     */
    private boolean isAdmin(String token){
        // 如果token为null，返回false
        if(token == null) return false;
        
        // 如果token以"Bearer "开头，去除前缀
        if(token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 解析JWT token，获取Claims
        io.jsonwebtoken.Claims c = JwtUtil.parseClaims(token);
        
        // 如果解析失败，返回false
        if(c == null) return false;
        
        // 从Claims中获取角色
        Object role = c.get("role");
        
        // 如果角色是"ADMIN"，返回true，否则返回false
        return role != null && role.toString().equals("ADMIN");
    }

    /**
     * 获取当前登录用户的基本信息（id/username/nickname/avatar/signature）
     * @param token 请求头中的Authorization token
     * @return ApiResponse<Map<String,Object>> - 返回用户信息
     */
    @GetMapping("/me")
    public ApiResponse<java.util.Map<String,Object>> me(@RequestHeader("Authorization") String token){
        // 从token中解析出用户ID
        Long id = userIdFromToken(token);
        
        // 如果解析失败，返回未授权响应
        if(id == null) {
            return ApiResponse.fail("unauthorized");
        }
        
        // 根据ID查询用户
        User u = userMapper.selectById(id);
        
        // 创建返回结果Map
        java.util.Map<String,Object> m = new java.util.HashMap<>();
        
        // 处理头像路径：如果为空，使用默认头像
        String ap = u.getAvatarPath(); 
        if(ap == null || ap.trim().isEmpty()) {
            ap = "/uploads/avatars/default.jpg"; 
        }
        
        // 将用户信息放入Map
        m.put("id", u.getId());                 // 用户ID
        m.put("username", u.getUsername());     // 用户名
        m.put("nickname", u.getNickname());     // 昵称
        m.put("avatarPath", ap);                // 头像路径
        m.put("signature", u.getSignature());   // 签名
        
        // 返回成功响应，包含用户信息
        return ApiResponse.ok(m);
    }

    /**
     * 管理员封禁用户：status 置为 0
     * @param token 请求头中的Authorization token
     * @param userId 要封禁的用户ID
     * @return ApiResponse<String> - 返回操作结果
     */
    @PostMapping("/admin/ban")
    public ApiResponse<String> banUser(
            @RequestHeader("Authorization") String token,  // 请求头中的Authorization token
            @RequestParam Long userId) {                    // 要封禁的用户ID
        
        // 检查当前用户是否为管理员
        if(!isAdmin(token)) {
            return ApiResponse.fail("forbidden");
        }
        
        // 创建更新对象，设置ID和状态为0（封禁）
        User up0 = new User(); 
        up0.setId(userId); 
        up0.setStatus(0);
        
        // 更新数据库中的用户状态
        int r = userMapper.updateById(up0);
        
        // 返回更新结果：成功或失败
        return r > 0 ? ApiResponse.ok("ok") : ApiResponse.fail("failed");
    }

    /**
     * 上传头像：文件存储到 uploads/avatars，文件名=用户id+扩展名，更新 avatarPath
     * @param token 请求头中的Authorization token
     * @param file 要上传的头像文件
     * @return ApiResponse<String> - 返回头像URL
     */
    @PostMapping(value="/avatar", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadAvatar(
            @RequestHeader("Authorization") String token,  // 请求头中的Authorization token
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {  // 要上传的头像文件
        
        // 从token中解析出用户ID
        Long id = userIdFromToken(token); 
        
        // 如果解析失败，返回未授权响应
        if(id == null) {
            return ApiResponse.fail("unauthorized");
        }
        
        try {
            // 定义头像存储目录
            java.nio.file.Path dir = java.nio.file.Paths.get("uploads", "avatars");
            
            // 创建目录（如果不存在）
            java.nio.file.Files.createDirectories(dir);
            
            // 获取文件扩展名
            String ext = ""; 
            String orig = file.getOriginalFilename(); 
            if(orig != null) {
                int i = orig.lastIndexOf('.'); 
                if(i >= 0) {
                    ext = orig.substring(i);
                }
            }
            
            // 生成文件名：用户ID + 扩展名
            String name = String.valueOf(id) + ext;
            
            // 查询用户，检查是否有旧头像需要删除
            User u = userMapper.selectById(id); 
            if(u != null) {
                String old = u.getAvatarPath(); 
                // 如果旧头像路径存在且不是默认头像，删除旧头像文件
                if(old != null && old.startsWith("/uploads/avatars/")) {
                    String fn = old.substring(old.lastIndexOf('/') + 1); 
                    if(fn != null && !fn.equalsIgnoreCase("default.jpg")) {
                        java.nio.file.Files.deleteIfExists(dir.resolve(fn)); 
                    }
                }
            }
            
            // 定义目标文件路径
            java.nio.file.Path target = dir.resolve(name);
            
            // 复制文件到目标路径（覆盖已存在的文件）
            try(java.io.InputStream in = file.getInputStream()) {
                java.nio.file.Files.copy(in, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING); 
            }
            
            // 生成头像URL
            String url = "/uploads/avatars/" + name;
            
            // 更新用户头像路径
            User up = new User(); 
            up.setId(id); 
            up.setAvatarPath(url);
            userMapper.updateById(up);
            
            // 返回成功响应，包含头像URL
            return ApiResponse.ok(url);
        } catch(Exception e) {
            // 捕获异常，返回失败响应
            return ApiResponse.fail("upload failed"); 
        }
    }

    /**
     * 更新签名
     * @param token 请求头中的Authorization token
     * @param signature 新签名
     * @return ApiResponse<String> - 返回操作结果
     */
    @PostMapping("/signature")
    public ApiResponse<String> updateSignature(
            @RequestHeader("Authorization") String token,  // 请求头中的Authorization token
            @RequestParam String signature) {              // 新签名
        
        // 从token中解析出用户ID
        Long id = userIdFromToken(token); 
        
        // 如果解析失败，返回未授权响应
        if(id == null) {
            return ApiResponse.fail("unauthorized");
        }
        
        // 创建更新对象，设置ID和新签名
        User upSig = new User(); 
        upSig.setId(id); 
        upSig.setSignature(signature);
        
        // 更新数据库中的用户签名
        int rSig = userMapper.updateById(upSig);
        
        // 返回更新结果：成功或失败
        return rSig > 0 ? ApiResponse.ok("ok") : ApiResponse.fail("failed");
    }

    /**
     * 更新昵称
     * @param token 请求头中的Authorization token
     * @param nickname 新昵称
     * @return ApiResponse<String> - 返回操作结果
     */
    @PostMapping("/nickname")
    public ApiResponse<String> updateNickname(
            @RequestHeader("Authorization") String token,  // 请求头中的Authorization token
            @RequestParam String nickname) {                // 新昵称
        
        // 从token中解析出用户ID
        Long id = userIdFromToken(token); 
        
        // 如果解析失败，返回未授权响应
        if(id == null) {
            return ApiResponse.fail("unauthorized");
        }
        
        // 创建更新对象，设置ID和新昵称
        User upNick = new User(); 
        upNick.setId(id); 
        upNick.setNickname(nickname);
        
        // 更新数据库中的用户昵称
        int rNick = userMapper.updateById(upNick);
        
        // 返回更新结果：成功或失败
        return rNick > 0 ? ApiResponse.ok("ok") : ApiResponse.fail("failed");
    }

    /**
     * 获取指定用户的公开信息（含 status，用于主页显示）
     * @param id 要查询的用户ID
     * @return ApiResponse<Map<String,Object>> - 返回用户公开信息
     */
    @GetMapping("/user/{id}")
    public ApiResponse<java.util.Map<String,Object>> user(@PathVariable Long id){
        // 根据ID查询用户
        User u = userMapper.selectById(id);
        
        // 如果用户不存在，返回未找到响应
        if(u == null) {
            return ApiResponse.fail("not_found");
        }
        
        // 创建返回结果Map
        java.util.Map<String,Object> m = new java.util.HashMap<>();
        
        // 处理头像路径：如果为空，使用默认头像
        String ap1 = u.getAvatarPath(); 
        if(ap1 == null || ap1.trim().isEmpty()) {
            ap1 = "/uploads/avatars/default.jpg"; 
        }
        
        // 将用户公开信息放入Map
        m.put("id", u.getId());                 // 用户ID
        m.put("username", u.getUsername());     // 用户名
        m.put("nickname", u.getNickname());     // 昵称
        m.put("avatarPath", ap1);               // 头像路径
        m.put("signature", u.getSignature());   // 签名
        m.put("status", u.getStatus());         // 用户状态（0:封禁, 1:正常）
        
        // 返回成功响应，包含用户公开信息
        return ApiResponse.ok(m);
    }

    /**
     * 管理员解除封禁：status 置为 1
     * @param token 请求头中的Authorization token
     * @param userId 要解除封禁的用户ID
     * @return ApiResponse<String> - 返回操作结果
     */
    @PostMapping("/admin/unban")
    public ApiResponse<String> unbanUser(
            @RequestHeader("Authorization") String token,  // 请求头中的Authorization token
            @RequestParam Long userId) {                    // 要解除封禁的用户ID
        
        // 检查当前用户是否为管理员
        if(!isAdmin(token)) {
            return ApiResponse.fail("forbidden");
        }
        
        // 创建更新对象，设置ID和状态为1（正常）
        User up1 = new User(); 
        up1.setId(userId); 
        up1.setStatus(1);
        
        // 更新数据库中的用户状态
        int r1 = userMapper.updateById(up1);
        
        // 返回更新结果：成功或失败
        return r1 > 0 ? ApiResponse.ok("ok") : ApiResponse.fail("failed");
    }
}
