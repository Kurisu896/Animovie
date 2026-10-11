package com.bgm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bgm.domain.SubjectCommentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SubjectCommentLikeMapper extends BaseMapper<SubjectCommentLike> {
    int insert(@Param("commentId") Long commentId,@Param("userId") Long userId);
    int delete(@Param("commentId") Long commentId,@Param("userId") Long userId);
    Long exists(@Param("commentId") Long commentId,@Param("userId") Long userId);
    java.util.List<Long> listByUserSubject(@Param("userId") Long userId, @Param("subjectId") Long subjectId);
}
