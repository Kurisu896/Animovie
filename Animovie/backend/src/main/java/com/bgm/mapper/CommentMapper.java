package com.bgm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bgm.domain.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    IPage<Comment> listByTopic(IPage<Comment> page, @Param("topicId") Long topicId);
    IPage<Comment> listByTopicOrderByCreatedDesc(IPage<Comment> page, @Param("topicId") Long topicId);
    IPage<Comment> listByTopicOrderByLikes(IPage<Comment> page, @Param("topicId") Long topicId);
    int incLike(@Param("id") Long id);
    int decLike(@Param("id") Long id);
    Integer maxFloor(@Param("topicId") Long topicId);
}
