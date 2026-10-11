package com.bgm.service;

import com.bgm.domain.SubjectComment;
import com.bgm.mapper.SubjectCommentLikeMapper;
import com.bgm.mapper.SubjectCommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubjectCommentService {
    @Autowired
    SubjectCommentMapper subjectCommentMapper;
    @Autowired
    SubjectCommentLikeMapper subjectCommentLikeMapper;

    public PageResult<SubjectComment> list(Long subjectId,int pageNum,int pageSize,String sort){
        Page<SubjectComment> page = new Page<>(pageNum, pageSize);
        IPage<SubjectComment> res;
        if("likes".equalsIgnoreCase(sort)){
            res=subjectCommentMapper.listBySubjectOrderByLikes(page,subjectId);
        }else{
            res=subjectCommentMapper.listBySubjectOrderByCreatedDesc(page,subjectId);
        }
        PageResult<SubjectComment> pr=new PageResult<>(); pr.setItems(res.getRecords()); pr.setTotal(res.getTotal()); pr.setPageNum(pageNum); pr.setPageSize(pageSize); return pr;
    }
    public SubjectComment add(Long subjectId,Long userId,String content){
        SubjectComment c=new SubjectComment(); c.setSubjectId(subjectId); c.setUserId(userId); c.setContent(content); c.setLikeCount(0); c.setCreatedAt(LocalDateTime.now());
        subjectCommentMapper.insert(c); return c;
    }
    @Transactional
    public boolean like(Long id,Long userId){
        Long ex=subjectCommentLikeMapper.exists(id,userId); if(ex!=null && ex>0) return false;
        subjectCommentLikeMapper.insert(id,userId); subjectCommentMapper.incLike(id); return true;
    }
    @Transactional
    public boolean unlike(Long id,Long userId){
        int r=subjectCommentLikeMapper.delete(id,userId); if(r>0){ subjectCommentMapper.decLike(id); return true; } return false;
    }

    public java.util.List<Long> likedByUser(Long userId,Long subjectId){
        return subjectCommentLikeMapper.listByUserSubject(userId,subjectId);
    }
}
