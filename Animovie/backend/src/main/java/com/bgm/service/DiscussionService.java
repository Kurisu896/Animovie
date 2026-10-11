package com.bgm.service;

import com.bgm.domain.Comment;
import com.bgm.domain.Topic;
import com.bgm.mapper.CommentLikeMapper;
import com.bgm.mapper.CommentMapper;
import com.bgm.mapper.TopicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiscussionService {
    @Autowired
    TopicMapper topicMapper;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    CommentLikeMapper commentLikeMapper;

    public PageResult<Topic> listTopics(Long subjectId,int pageNum,int pageSize){
        Page<Topic> page = new Page<>(pageNum, pageSize);
        IPage<Topic> res = topicMapper.listBySubject(page, subjectId);
        PageResult<Topic> pr=new PageResult<>(); pr.setItems(res.getRecords()); pr.setTotal(res.getTotal()); pr.setPageNum(pageNum); pr.setPageSize(pageSize); return pr;
    }
    public Topic createTopic(Long subjectId,Long userId,String title,String content){
        Topic t=new Topic(); t.setSubjectId(subjectId); t.setUserId(userId); t.setTitle(title); t.setContent(content); t.setStatus("LOCKED"); t.setReplyCount(0); t.setCreatedAt(LocalDateTime.now()); t.setUpdatedAt(LocalDateTime.now());
        topicMapper.insert(t); return t;
    }
    public PageResult<Topic> listAllTopics(int pageNum,int pageSize){
        Page<Topic> page = new Page<>(pageNum, pageSize);
        IPage<Topic> res=topicMapper.listAll(page);
        PageResult<Topic> pr=new PageResult<>(); pr.setItems(res.getRecords()); pr.setTotal(res.getTotal()); pr.setPageNum(pageNum); pr.setPageSize(pageSize); return pr;
    }
    public PageResult<Topic> searchTopics(String keyword,int pageNum,int pageSize){
        Page<Topic> page = new Page<>(pageNum, pageSize);
        IPage<Topic> res=topicMapper.listByKeyword(page, keyword);
        PageResult<Topic> pr=new PageResult<>(); pr.setItems(res.getRecords()); pr.setTotal(res.getTotal()); pr.setPageNum(pageNum); pr.setPageSize(pageSize); return pr;
    }
    public PageResult<Topic> listPending(int pageNum,int pageSize){
        Page<Topic> page = new Page<>(pageNum, pageSize);
        IPage<Topic> res=topicMapper.listPending(page);
        PageResult<Topic> pr=new PageResult<>(); pr.setItems(res.getRecords()); pr.setTotal(res.getTotal()); pr.setPageNum(pageNum); pr.setPageSize(pageSize); return pr;
    }
    public PageResult<Topic> listByUser(Long userId,int pageNum,int pageSize){
        Page<Topic> page = new Page<>(pageNum, pageSize);
        IPage<Topic> res=topicMapper.listByUser(page,userId);
        PageResult<Topic> pr=new PageResult<>(); pr.setItems(res.getRecords()); pr.setTotal(res.getTotal()); pr.setPageNum(pageNum); pr.setPageSize(pageSize); return pr;
    }
    public PageResult<Comment> listComments(Long topicId,int pageNum,int pageSize,String sort){
        Page<Comment> page = new Page<>(pageNum, pageSize);
        IPage<Comment> res;
        if("likes".equalsIgnoreCase(sort)){
            res=commentMapper.listByTopicOrderByLikes(page,topicId);
        }else if("latest".equalsIgnoreCase(sort)){
            res=commentMapper.listByTopicOrderByCreatedDesc(page,topicId);
        }else{
            res=commentMapper.listByTopic(page,topicId);
        }
        PageResult<Comment> pr=new PageResult<>(); pr.setItems(res.getRecords()); pr.setTotal(res.getTotal()); pr.setPageNum(pageNum); pr.setPageSize(pageSize); return pr;
    }

    public Topic getTopic(Long id){
        return topicMapper.getById(id);
    }

    public java.util.List<Long> likedByUser(Long userId,Long topicId){
        return commentLikeMapper.listByUserTopic(userId,topicId);
    }

    public int deleteTopic(Long id){ return topicMapper.deleteById(id); }
    public int approveTopic(Long id){ return topicMapper.approve(id); }
    public int lockTopic(Long id){ return topicMapper.lock(id); }
    public int rejectTopic(Long id){ return topicMapper.reject(id); }
    public PageResult<Topic> listRejected(int pageNum,int pageSize){
        Page<Topic> page = new Page<>(pageNum, pageSize);
        IPage<Topic> res=topicMapper.listRejected(page);
        PageResult<Topic> pr=new PageResult<>(); pr.setItems(res.getRecords()); pr.setTotal(res.getTotal()); pr.setPageNum(pageNum); pr.setPageSize(pageSize); return pr;
    }
    public int updateTopic(Long id,String title,String content){ return topicMapper.updateEdited(id,title,content); }
    public int deleteComment(Long id){
        Comment c = commentMapper.selectById(id);
        int r = commentMapper.deleteById(id);
        if(c!=null && c.getTopicId()!=null){ topicMapper.refreshReplyCount(c.getTopicId()); }
        return r;
    }
    @Transactional
    public Comment addComment(Long topicId,Long userId,Long parentId,String content){
        Integer max=commentMapper.maxFloor(topicId); int floor=(max==null?0:max)+1;
        Comment c=new Comment(); c.setTopicId(topicId); c.setUserId(userId); c.setParentId(parentId);
                                 c.setContent(content); c.setLikeCount(0); c.setFloor(floor); c.setCreatedAt(LocalDateTime.now());
        commentMapper.insert(c); topicMapper.refreshReplyCount(topicId); return c;
    }
    @Transactional
    public boolean likeComment(Long commentId,Long userId){
        Long ex=commentLikeMapper.exists(commentId,userId);
        if(ex!=null && ex>0){ return false; }
        commentLikeMapper.insert(commentId,userId); commentMapper.incLike(commentId); return true;
    }
    @Transactional
    public boolean unlikeComment(Long commentId,Long userId){
        int r=commentLikeMapper.delete(commentId,userId); if(r>0){ commentMapper.decLike(commentId); return true; } return false;
    }
}
