package com.bgm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bgm.domain.CommentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommentLikeMapper extends BaseMapper<CommentLike> {
    int insert(@Param("commentId") Long commentId,@Param("userId") Long userId);
    int delete(@Param("commentId") Long commentId,@Param("userId") Long userId);
    Long exists(@Param("commentId") Long commentId,@Param("userId") Long userId);
    List<Long> listByUserTopic(@Param("userId") Long userId, @Param("topicId") Long topicId);
}
