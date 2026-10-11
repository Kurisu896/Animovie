// 包声明：定义该类所属的包路径
// 包名：com.bgm.utils.security，用于存放安全相关的工具类
package com.bgm.utils.security;

// 导入用户实体类
// 用途：createToken方法需要User对象作为参数
import com.bgm.domain.User;
// 导入JWT的Claims类
// 用途：表示JWT的负载部分，包含用户信息等声明
import io.jsonwebtoken.Claims;
// 导入JWT的Jws类
// 用途：表示已解析和验证的JWT
import io.jsonwebtoken.Jws;
// 导入JWT的JwtBuilder类
// 用途：构建JWT令牌
import io.jsonwebtoken.JwtBuilder;
// 导入JWT的Jwts工具类
// 用途：提供JWT的构建、解析和验证功能
import io.jsonwebtoken.Jwts;
// 导入JWT的签名算法枚举
// 用途：指定JWT的签名算法
import io.jsonwebtoken.SignatureAlgorithm;
// 导入JCE的密钥规范类
// 用途：根据密钥字符串创建SecretKey对象
import javax.crypto.spec.SecretKeySpec;
// 导入Java安全的Key接口
// 用途：表示加密密钥
import java.security.Key;

// 导入Java日期时间类
// 用途：设置JWT的过期时间
import java.util.Date;
// 导入UUID类
// 用途：生成JWT的唯一ID
import java.util.UUID;

public class JwtUtil {
    //JWT令牌过期时间
    static long expireMs=1000*60*60*24;
    
    //JWT签名密钥字符串
    static String secretSignature="bgm.local.secret";

    static Key key=new SecretKeySpec(secretSignature.getBytes(), SignatureAlgorithm.HS256.getJcaName());

    public static String createToken(User user){
        // 创建JwtBuilder实例，用于构建JWT
        JwtBuilder jwtBuilder= Jwts.builder();

        return jwtBuilder.setHeaderParam("typ","JWT") // 设置令牌类型为JWT
                .setHeaderParam("alg","HS256") // 设置签名算法为HS256
                .claim("username",user.getUsername()) // 添加用户名声明
                .claim("role",user.getRole()) // 添加角色声明
                .setSubject("bgm-app") // 设置令牌主题
                .setExpiration(new Date(System.currentTimeMillis()+expireMs)) // 设置过期时间（24小时后）
                .setId(UUID.randomUUID().toString()) // 生成唯一ID
                .signWith(SignatureAlgorithm.HS256,key) // 使用HS256算法和密钥签名
                .compact(); // 生成JWT字符串
    }
    

    public static boolean isValidToken(String token) {
        try{
            // 解析并验证JWT令牌
            // setSigningKey(key)：设置用于验证签名的密钥
            // parseClaimsJws(token)：解析JWT，验证签名，返回Jws对象
            Jws<Claims> claimsJws= Jwts.parser().setSigningKey(key).parseClaimsJws(token); 
            
            // 检查令牌中是否包含username声明
            claimsJws.getBody().get("username");
        }catch (Exception e){
            // 出现异常（如格式错误、签名无效、过期等），返回false
            return false;
        }
        // 验证通过，返回true
        return true;
    }

    public static Claims parseClaims(String token){
        try{
            // 解析并验证JWT令牌
            Jws<Claims> claimsJws= Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            
            // 返回令牌的负载部分（Claims对象）
            return claimsJws.getBody(); 
        }catch (Exception e){
            // 解析失败，返回null
            return null;
        }
    }

    public static String verifyToken(String token) {
        try{
            // 解析并验证JWT令牌
            Jws<Claims> claimsJws= Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            
            // 获取令牌的负载部分
            Claims body = claimsJws.getBody();
            
            // 获取令牌的过期时间
            Date expiration = body.getExpiration();
            
            // 计算令牌剩余有效期（毫秒）
            long gap=expiration.getTime()-System.currentTimeMillis();
            
            // 如果令牌已过期（剩余时间<0），返回null
            if(gap<0){
                return null;
            }
            // 如果令牌即将过期（剩余时间<过期时间的10%），生成新令牌
            else if(gap<expireMs*0.1){
                // 创建JwtBuilder实例
                JwtBuilder jwtBuilder= Jwts.builder();
                
                // 构建新令牌
                // 复用原令牌的Claims，更新过期时间和ID
                return jwtBuilder.setHeaderParam("typ","JWT") // 设置令牌类型
                        .setHeaderParam("alg","HS256") // 设置签名算法
                        .setClaims(body) // 复用原令牌的Claims
                        .setSubject("bgm-app") // 设置主题
                        .setExpiration(new Date(System.currentTimeMillis()+expireMs)) // 设置新的过期时间（24小时后）
                        .setId(UUID.randomUUID().toString()) // 生成新的唯一ID
                        .signWith(SignatureAlgorithm.HS256,key) // 使用密钥签名
                        .compact(); // 生成新的JWT字符串
            }
            // 令牌有效且未即将过期，返回原令牌
            else {
                return token;
            }
        }catch (Exception e){
            // 解析或验证失败，返回null
            return null;
        }
    }
}
