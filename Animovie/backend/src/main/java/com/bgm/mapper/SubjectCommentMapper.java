package com.bgm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bgm.domain.SubjectComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SubjectCommentMapper extends BaseMapper<SubjectComment> {
    IPage<SubjectComment> listBySubjectOrderByCreatedDesc(IPage<SubjectComment> page, @Param("subjectId") Long subjectId);
    IPage<SubjectComment> listBySubjectOrderByLikes(IPage<SubjectComment> page, @Param("subjectId") Long subjectId);
    int deleteByIdAndUser(@Param("id") Long id,@Param("userId") Long userId);
    int incLike(@Param("id") Long id);
    int decLike(@Param("id") Long id);
}
