package com.bgm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bgm.domain.Subject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface SubjectMapper extends BaseMapper<Subject> {
    IPage<Subject> listAdvanced(IPage<Subject> page, @Param("p") Map<String,Object> params);
    IPage<Subject> listByCollectCount(IPage<Subject> page);
    IPage<Subject> listByAvgRating(IPage<Subject> page, @Param("minCount") int minCount);
    Double avgRatingBySubject(@Param("subjectId") Long subjectId);
    java.util.List<java.util.Map<String,Object>> ratingCountsBySubject(@Param("subjectId") Long subjectId);
}
