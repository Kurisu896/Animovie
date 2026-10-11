// 包声明：定义该类所属的包路径
// 包名：com.bgm.service，用于存放业务逻辑层服务类
package com.bgm.service;

// 导入用户实体类
// 来源：com.bgm.domain.User，定义了用户的数据库映射结构
import com.bgm.domain.User;
// 导入用户数据访问接口
// 来源：com.bgm.mapper.UserMapper，继承自MyBatis Plus的BaseMapper，提供数据库操作方法
import com.bgm.mapper.UserMapper;
// 导入MyBatis Plus的Lambda查询构造器
// 用途：用于构建类型安全的SQL查询条件
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
// 导入Spring的自动注入注解
// 用途：自动注入依赖的Bean
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring Security的BCrypt密码加密器
// 用途：用于密码的哈希加密和验证
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// 导入Spring的服务注解
// 用途：将该类标记为Spring管理的服务Bean
import org.springframework.stereotype.Service;

// 导入Java 8的本地日期时间类
// 用途：获取当前时间
import java.time.LocalDateTime;

/**
 * 用户服务类
 * 功能：处理用户注册、登录验证等业务逻辑
 * 调用位置：
 *   - register方法：被AuthController的register方法调用
 *   - verify方法：被AuthController的login方法调用
 * 依赖：
 *   - userMapper：用户数据访问接口
 *   - encoder：BCrypt密码加密器
 */
@Service
public class UserService {
    /**
     * 用户数据访问接口实例
     * 注入方式：Spring自动注入
     * 用途：执行用户相关的数据库操作
     */
    @Autowired
    UserMapper userMapper;
    
    /**
     * BCrypt密码加密器实例
     * 初始化方式：构造方法初始化
     * 用途：
     *   - register方法：对用户密码进行哈希加密
     *   - verify方法：验证用户输入的密码与数据库中存储的哈希值是否匹配
     */
    BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
    
    /**
     * 用户注册方法
     * 功能：创建新用户，将用户信息保存到数据库
     * 参数：
     *   - username：用户名，由前端表单提交
     *   - password：用户密码，由前端表单提交
     *   - nickname：用户昵称，由前端表单提交
     * 返回值：
     *   - 成功：返回新创建的User对象
     *   - 失败：返回null（如用户名已存在）
     * 数据流向：
     *   1. 前端表单提交注册数据
     *   2. AuthController.register调用此方法
     *   3. 验证用户名是否存在
     *   4. 创建新User对象并设置属性
     *   5. 调用userMapper.insert将数据插入数据库
     *   6. 返回新User对象给AuthController
     *   7. AuthController生成token并返回给前端
     * 调用位置：com.bgm.controller.AuthController.register
     */
    public User register(String username,String password,String nickname){
        // 查询用户名是否已存在
        // 使用LambdaQueryWrapper构建查询条件：用户名等于输入的username
        // 调用userMapper.selectOne执行单条查询
        // 数据流向：从数据库查询user表，返回User对象或null
        User exists=userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        
        // 如果用户名已存在，返回null表示注册失败
        if(exists!=null){
            return null;
        }
        
        // 创建新的User对象
        // 实例化来源：com.bgm.domain.User，通过无参构造函数创建
        User u=new User();
        
        // 设置用户名
        // 数据流向：从方法参数username → User对象的username属性
        u.setUsername(username);
        
        // 对密码进行BCrypt哈希加密
        // 方法调用：encoder.encode(password)，返回加密后的哈希字符串
        // 数据流向：从方法参数password → 加密后的passwordHash → User对象的passwordHash属性
        u.setPasswordHash(encoder.encode(password));
        
        // 设置用户昵称
        // 数据流向：从方法参数nickname → User对象的nickname属性
        u.setNickname(nickname);
        
        // 设置用户角色：默认普通用户
        // 数据流向：常量"USER" → User对象的role属性
        u.setRole("USER");
        
        // 设置用户状态：1表示启用
        // 数据流向：常量1 → User对象的status属性
        u.setStatus(1);
        
        // 设置默认头像路径
        // 数据流向：常量"/uploads/avatars/default.jpg" → User对象的avatarPath属性
        u.setAvatarPath("/uploads/avatars/default.jpg");
        
        // 设置创建时间：当前系统时间
        // 方法调用：LocalDateTime.now()，返回当前日期时间
        // 数据流向：当前时间 → User对象的createdAt属性
        u.setCreatedAt(LocalDateTime.now());
        
        // 将用户数据插入数据库
        // 方法调用：userMapper.insert(u)，执行INSERT SQL语句
        // 数据流向：User对象 → 数据库user表
        userMapper.insert(u);
        
        // 返回新创建的User对象
        // 数据流向：User对象 → 调用者AuthController.register
        return u;
    }
    
    /**
     * 用户登录验证方法
     * 功能：验证用户名和密码是否正确，返回合法的User对象
     * 参数：
     *   - username：用户名，由前端登录表单提交
     *   - password：用户密码，由前端登录表单提交
     * 返回值：
     *   - 成功：返回验证通过的User对象
     *   - 失败：返回null（如用户名不存在、密码错误、用户被禁用）
     * 数据流向：
     *   1. 前端表单提交登录数据
     *   2. AuthController.login调用此方法
     *   3. 根据用户名查询用户
     *   4. 验证密码是否匹配
     *   5. 验证用户状态是否启用
     *   6. 返回验证通过的User对象给AuthController
     *   7. AuthController生成token并返回给前端
     * 调用位置：com.bgm.controller.AuthController.login
     */
    public User verify(String username,String password){
        // 根据用户名查询用户
        // 使用LambdaQueryWrapper构建查询条件：用户名等于输入的username
        // 调用userMapper.selectOne执行单条查询
        // 数据流向：从数据库查询user表，返回User对象或null
        User u=userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        
        // 如果用户不存在，返回null表示验证失败
        if(u==null){
            return null;
        }
        
        // 验证密码是否匹配
        // 条件：
        //   - password不为null且长度大于0
        //   - 使用encoder.matches验证输入密码与数据库中的哈希值是否匹配
        // 返回值：不匹配则返回null
        if(password!=null && password.length()>0 && !encoder.matches(password,u.getPasswordHash())){
            return null;
        }
        
        // 验证用户状态是否启用
        // 条件：user.status == 1（启用）
        // 返回值：未启用则返回null
        if(u.getStatus()!=1){
            return null;
        }
        
        // 返回验证通过的User对象
        // 数据流向：User对象 → 调用者AuthController.login
        return u;
    }
}
