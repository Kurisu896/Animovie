package com.bgm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bgm.domain.Subject;
import com.bgm.mapper.SubjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SubjectService {
    @Autowired
    SubjectMapper subjectMapper;
    public int add(Subject s){
        if(s.getCoverPath()==null || s.getCoverPath().trim().isEmpty()){
            s.setCoverPath("/uploads/covers/default.jpg");
        }
        s.setUpdatedDate(LocalDateTime.now());
        return subjectMapper.insert(s);
    }
    public int edit(Subject s){
        if(s.getCoverPath()==null || s.getCoverPath().trim().isEmpty()){
            s.setCoverPath("/uploads/covers/default.jpg");
        }
        s.setUpdatedDate(LocalDateTime.now());
        return subjectMapper.updateById(s);
    }
    public int remove(Long id){
        return subjectMapper.deleteById(id);
    }
    public PageResult<Subject> listAdv(String keyword,String category,String country,Integer year,Integer month,Double ratingMin,Double ratingMax,String tagKeyword,int pageNum,int pageSize){
        java.util.Map<String,Object> params=new java.util.HashMap<>();
        params.put("keyword", keyword);
        params.put("category", category);
        params.put("country", country);
        params.put("year", year);
        params.put("month", month);
        params.put("ratingMin", ratingMin);
        params.put("ratingMax", ratingMax);
        params.put("tagKeyword", tagKeyword);
        Page<Subject> page = new Page<>(pageNum, pageSize);
        IPage<Subject> res = subjectMapper.listAdvanced(page, params);
        PageResult<Subject> pr=new PageResult<>(); pr.setItems(res.getRecords()); pr.setTotal(res.getTotal()); pr.setPageNum(pageNum); pr.setPageSize(pageSize); return pr;
    }
    public Subject get(Long id){
        return subjectMapper.selectById(id);
    }
}
