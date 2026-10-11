package com.bgm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bgm.domain.Collection;
import com.bgm.mapper.CollectionMapper;
import com.bgm.mapper.UserActivityMapper;
import com.bgm.domain.UserActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CollectionService {
    @Autowired
    CollectionMapper collectionMapper;
    @Autowired
    UserActivityMapper userActivityMapper;
    public int upsert(Collection c){
        c.setUpdatedDate(LocalDateTime.now());
        Collection exists = collectionMapper.selectOne(new LambdaQueryWrapper<Collection>() // 查之前有没有收藏过
                .eq(Collection::getUserId, c.getUserId())
                .eq(Collection::getSubjectId, c.getSubjectId()));
        if(exists==null){ return collectionMapper.insert(c); } // 第一次收藏 → 插入新记录
        c.setId(exists.getId());
        return collectionMapper.updateById(c); // 不是第一次 → 更新旧记录
    }
    public void logActivities(Collection before, Collection after){
        LocalDateTime now=LocalDateTime.now();
        // 1. 状态变更：新状态不为空且与旧状态不同
        if(after.getStatus()!=null && (before==null || !after.getStatus().equals(before.getStatus()))){
            UserActivity ua=new UserActivity(); ua.setUserId(after.getUserId()); ua.setSubjectId(after.getSubjectId());
                                                ua.setType("STATUS"); ua.setDetail(after.getStatus()); ua.setCreatedAt(now); userActivityMapper.insert(ua);
        }
        // 2. 进度变更：新进度不为空且与旧进度不同
        if(after.getProgressEpisode()!=null && (before==null || !after.getProgressEpisode().equals(before.getProgressEpisode()))){
            UserActivity ua=new UserActivity(); ua.setUserId(after.getUserId()); ua.setSubjectId(after.getSubjectId()); ua.setType("PROGRESS");
                                                ua.setDetail(String.valueOf(after.getProgressEpisode())); ua.setCreatedAt(now); userActivityMapper.insert(ua);
        }
        // 3. 评分变更：新评分不为空且与旧评分不同
        if(after.getRating()!=null && (before==null || !after.getRating().equals(before.getRating()))){
            UserActivity ua=new UserActivity(); ua.setUserId(after.getUserId()); ua.setSubjectId(after.getSubjectId()); ua.setType("RATING");
                                                ua.setDetail(String.valueOf(after.getRating())); ua.setCreatedAt(now); userActivityMapper.insert(ua);
        }
        // 4. 吐槽变更：新吐槽内容不为空，且与旧吐槽不同
        String newComment = after.getShortComment();
        String oldComment = before == null ? null : before.getShortComment();
        if(newComment != null && !newComment.trim().isEmpty() && (oldComment == null || !newComment.equals(oldComment))){
            UserActivity ua=new UserActivity(); ua.setUserId(after.getUserId()); ua.setSubjectId(after.getSubjectId()); ua.setType("COMMENT");
                                                ua.setDetail(newComment); ua.setCreatedAt(now); userActivityMapper.insert(ua);
        }
    }
    public Collection getByUserSubject(Long userId,Long subjectId){
        return collectionMapper.selectOne(new LambdaQueryWrapper<Collection>()
                .eq(Collection::getUserId, userId)
                .eq(Collection::getSubjectId, subjectId));
    }
    public PageResult<Collection> list(Long userId,int pageNum,int pageSize){
        Page<Collection> page = new Page<>(pageNum, pageSize);
        IPage<Collection> res = collectionMapper.selectPage(page, new LambdaQueryWrapper<Collection>()
                .eq(Collection::getUserId, userId)
                .orderByDesc(Collection::getUpdatedDate));
        PageResult<Collection> pr=new PageResult<>();
        pr.setItems(res.getRecords()); pr.setTotal(res.getTotal()); pr.setPageNum(pageNum); pr.setPageSize(pageSize);
        return pr;
    }

    public int delete(Long userId,Long subjectId){
        return collectionMapper.delete(new LambdaQueryWrapper<Collection>()
                .eq(Collection::getUserId, userId)
                .eq(Collection::getSubjectId, subjectId));
    }
}
