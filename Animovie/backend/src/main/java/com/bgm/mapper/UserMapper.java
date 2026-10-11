// 包声明：定义该接口所属的包路径
// 包名：com.bgm.mapper，用于存放MyBatis Mapper接口，执行数据库操作
package com.bgm.mapper;

// 导入MyBatis Plus的BaseMapper接口
// 用途：提供基础的CRUD操作方法，如insert、update、delete、select等
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// 导入MyBatis Plus的分页接口
// 用途：用于分页查询，封装分页参数和结果
import com.baomidou.mybatisplus.core.metadata.IPage;
// 导入用户实体类
// 用途：指定Mapper操作的实体类型
import com.bgm.domain.User;
// 导入MyBatis的Mapper注解
// 用途：标记该接口为MyBatis Mapper接口，Spring会自动扫描并生成实现类
import org.apache.ibatis.annotations.Mapper;
// 导入MyBatis的Param注解
// 用途：为方法参数指定名称，便于在SQL中引用
import org.apache.ibatis.annotations.Param;

/*
 * 用户数据访问接口
 * 功能：定义用户相关的数据库操作方法
 * 继承关系：extends BaseMapper<User>，继承MyBatis Plus提供的基础CRUD方法
 * 调用位置：
 *   - UserService：使用BaseMapper的selectOne方法
 *   - UserController：可能使用listByKeyword方法进行分页查询
 *   - 其他需要用户数据的Service类
 */
// MyBatis注解：标记该接口为Mapper接口，Spring自动扫描生成实现
@Mapper
// 接口继承：继承BaseMapper<User>，获得基础CRUD操作能力
public interface UserMapper  extends BaseMapper<User> {
    IPage<User> listByKeyword(IPage<User> page, @Param("keyword") String keyword);
}
