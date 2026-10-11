package com.bgm.controller;

import com.bgm.common.ApiResponse;
import com.bgm.domain.Subject;
import com.bgm.mapper.SubjectMapper;
import com.bgm.service.PageResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
/**
 * 控制器：排行榜（subjects）
 * 职责：按收藏数/评分维度分页返回条目排行
 */
@RestController
@RequestMapping("/rank")
public class RankingController {
    @Autowired
    SubjectMapper subjectMapper;

    @GetMapping("/top")
    public ApiResponse<PageResult<Subject>> top(@RequestParam(defaultValue="collections") String by,@RequestParam(defaultValue="1") int pageNum,@RequestParam(defaultValue="15") int pageSize){
        Page<Subject> page = new Page<>(pageNum, pageSize);
        IPage<Subject> res;
        if("rating".equalsIgnoreCase(by)){
            int minCount = 5;
            res = subjectMapper.listByAvgRating(page, minCount);
        }else{
            res = subjectMapper.listByCollectCount(page);
        }
        PageResult<Subject> pr=new PageResult<>(); pr.setItems(res.getRecords()); pr.setTotal(res.getTotal()); pr.setPageNum(pageNum); pr.setPageSize(pageSize);
        return ApiResponse.ok(pr);
    }
}
