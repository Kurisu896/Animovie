package com.bgm.controller;

import com.bgm.common.ApiResponse;
import com.bgm.domain.Subject;
import com.bgm.domain.Topic;
import com.bgm.mapper.SubjectMapper;
import com.bgm.mapper.TopicMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
/**
 * 控制器：全局搜索（subjects/topics/users）
 * 职责：按关键字检索；支持 @ID 精确查找用户
 */
@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired SubjectMapper subjectMapper;
    @Autowired TopicMapper topicMapper;
    @Autowired com.bgm.mapper.UserMapper userMapper;

    @GetMapping("/all")
    public ApiResponse<java.util.Map<String,Object>> all(@RequestParam String keyword){
        java.util.Map<String,Object> res=new java.util.HashMap<>();
        java.util.Map<String,Object> p=new java.util.HashMap<>();
        p.put("keyword", keyword);
        Page<Subject> page = new Page<>(1, 10);
        IPage<Subject> sPage = subjectMapper.listAdvanced(page, p);
        List<Subject> subjects = sPage.getRecords();
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Topic> tPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1,10);
        IPage<Topic> ti = topicMapper.listByKeyword(tPage, keyword);
        List<Topic> topics = ti.getRecords();
        java.util.List<com.bgm.domain.User> users;
        if(keyword!=null && keyword.startsWith("@")){
            String idStr = keyword.substring(1).trim();
            try{
                Long id = Long.parseLong(idStr);
                com.bgm.domain.User u = userMapper.selectById(id);
                users = new java.util.ArrayList<>();
                if(u!=null) users.add(u);
            }catch(Exception e){
                com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.bgm.domain.User> uPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1,10);
                users = userMapper.listByKeyword(uPage, keyword).getRecords();
            }
        }else{
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.bgm.domain.User> uPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1,10);
            users = userMapper.listByKeyword(uPage, keyword).getRecords();
        }
        res.put("subjects", subjects);
        res.put("topics", topics);
        res.put("users", users);
        return ApiResponse.ok(res);
    }
}
