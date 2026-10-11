package com.bgm.domain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 用户实体类
 * 功能：映射数据库users表的结构，用于数据传输和持久化
 * 对应表：users
 * 数据流向：
 *   - 数据库查询 → User实体对象 → Service层 → Controller层 → 前端
 *   - 前端提交 → Controller层 → User实体对象 → Service层 → 数据库插入/更新
 * 主要调用位置：
 *   - UserService.register：创建新用户时实例化
 *   - UserService.verify：登录验证时返回
 *   - AuthController：用户相关API返回给前端
 *   - UserMapper：数据库操作时使用
 */
// Lombok注解：自动生成getter、setter、equals、hashCode、toString等方法
@Data
// MyBatis Plus注解：指定实体类对应的数据库表名为users
@TableName("users")
public class User {
    /**
     * 用户ID，主键
     * 注解：@TableId，标记为主键；type = IdType.AUTO，自增策略
     * 数据类型：Long
     * 用途：唯一标识用户记录
     * 数据流向：
     *   - 数据库：自增生成
     *   - 前端：作为用户唯一标识，用于URL参数、用户信息展示等
     *   - Service层：用于查询、更新、删除用户
     */
    @TableId(type = IdType.AUTO)
    Long id;
    
    /**
     * 用户名
     * 数据类型：String
     * 用途：用户登录标识，唯一不可重复
     * 数据来源：
     *   - 注册时由用户输入
     *   - 登录时用于身份验证
     * 数据流向：
     *   - 前端表单提交 → User实体 → 数据库存储
     *   - 数据库查询 → User实体 → 前端展示
     */
    String username;
    
    /**
     * 密码哈希值
     * 数据类型：String
     * 用途：存储用户密码的加密哈希值，用于登录验证
     * 加密方式：BCrypt加密
     * 数据来源：
     *   - 注册时由UserService.register方法使用BCryptPasswordEncoder加密
     *   - 登录时由UserService.verify方法验证
     * 数据流向：
     *   - 仅在UserService内部使用，不返回给前端
     *   - 数据库存储加密后的哈希值，不存储明文密码
     */
    String passwordHash;
    
    /**
     * 用户昵称
     * 数据类型：String
     * 用途：用户在系统中的显示名称，可修改
     * 数据来源：
     *   - 注册时由用户输入
     *   - 用户可在个人资料中修改
     * 数据流向：
     *   - 前端表单提交 → User实体 → 数据库存储
     *   - 数据库查询 → User实体 → 前端展示（如评论、动态等）
     */
    String nickname;
    
    /**
     * 用户头像路径
     * 数据类型：String
     * 用途：存储用户头像图片的相对路径
     * 默认值：/uploads/avatars/default.jpg
     * 数据来源：
     *   - 注册时使用默认头像
     *   - 用户可在个人资料中上传修改
     * 数据流向：
     *   - 前端表单提交（文件上传）→ 服务器保存文件 → 存储路径到User实体 → 数据库
     *   - 数据库查询 → User实体 → 前端拼接BASE_URL后显示图片
     * 图片URL格式：BASE_URL + avatarPath
     */
    String avatarPath;
    
    /**
     * 用户个性签名
     * 数据类型：String
     * 用途：用户的个人简介或个性签名
     * 数据来源：
     *   - 用户可在个人资料中设置
     * 数据流向：
     *   - 前端表单提交 → User实体 → 数据库存储
     *   - 数据库查询 → User实体 → 前端展示
     */
    String signature;
    
    /**
     * 用户角色
     * 数据类型：String
     * 用途：标识用户权限级别
     * 可能值：
     *   - ADMIN：管理员，拥有所有权限
     *   - USER：普通用户，拥有基础权限
     * 默认值：USER
     * 数据来源：
     *   - 注册时默认设置为USER
     *   - 管理员可通过后台修改
     * 数据流向：
     *   - 数据库存储 → User实体 → JWT token → 前端存储 → 后续请求携带
     *   - 用于权限控制，如管理后台访问、评论删除等
     */
    String role;
    
    /**
     * 用户状态
     * 数据类型：Integer
     * 用途：标识用户账号状态
     * 可能值：
     *   - 1：启用，可正常使用系统
     *   - 0：封禁，无法登录和使用系统
     * 默认值：1
     * 数据来源：
     *   - 注册时默认设置为1
     *   - 管理员可通过后台封禁/解封用户
     * 数据流向：
     *   - 数据库存储 → User实体 → 用于登录验证（UserService.verify）
     *   - 前端：可用于显示用户状态
     */
    Integer status;
    
    /**
     * 创建时间
     * 数据类型：LocalDateTime
     * 注解：@JsonFormat，指定JSON序列化格式为yyyy-MM-dd HH:mm:ss，时区为Asia/Shanghai
     * 用途：记录用户账号创建时间
     * 数据来源：
     *   - 注册时由UserService.register方法设置为当前时间
     * 数据流向：
     *   - 数据库存储 → User实体 → 前端展示（如用户注册时间）
     */
    @JsonFormat(pattern="yyyy-MM-dd  HH:mm:ss", timezone="Asia/Shanghai")
    LocalDateTime createdAt;
}
