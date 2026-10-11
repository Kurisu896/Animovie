package com.bgm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bgm.domain.Topic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TopicMapper extends BaseMapper<Topic> {
    IPage<Topic> listBySubject(IPage<Topic> page, @Param("subjectId") Long subjectId);
    IPage<Topic> listAll(IPage<Topic> page);
    IPage<Topic> listByKeyword(IPage<Topic> page, @Param("keyword") String keyword);
    int incReply(@Param("id") Long id);
    int refreshReplyCount(@Param("id") Long id);
    IPage<Topic> listPending(IPage<Topic> page);
    IPage<Topic> listRejected(IPage<Topic> page);
    IPage<Topic> listByUser(IPage<Topic> page, @Param("userId") Long userId);
    int approve(@Param("id") Long id);
    int lock(@Param("id") Long id);
    int reject(@Param("id") Long id);
    int updateEdited(@Param("id") Long id,@Param("title") String title,@Param("content") String content);
    Topic getById(@Param("id") Long id);
}
