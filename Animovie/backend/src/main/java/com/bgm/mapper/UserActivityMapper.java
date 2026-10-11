package com.bgm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bgm.domain.UserActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserActivityMapper extends BaseMapper<UserActivity> {
    IPage<UserActivity> listByUser(IPage<UserActivity> page, @Param("userId") Long userId);
    IPage<UserActivity> listAll(IPage<UserActivity> page);
    int deleteByIdAndUser(@Param("id") Long id,@Param("userId") Long userId);
}
