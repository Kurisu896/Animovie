package com.bgm.controller;

import com.bgm.common.ApiResponse;
import com.bgm.domain.Collection;
import com.bgm.domain.User;
import com.bgm.mapper.UserMapper;
import com.bgm.service.CollectionService;
import com.bgm.service.PageResult;
import com.bgm.utils.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/collection")
public class CollectionController {
    @Autowired
    CollectionService collectionService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    com.bgm.mapper.CollectionMapper collectionMapper;
    private Long userIdFromToken(String token){
        if(token==null) return null;
        if(token.startsWith("Bearer ")) token=token.substring(7);
        Claims c= JwtUtil.parseClaims(token);
        if(c==null) return null;
        String username=(String)c.get("username");
        User u=userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return u==null?null:u.getId();
    }
    @PostMapping("/upsert")
    public ApiResponse<Integer> upsert(@RequestHeader("Authorization") String token,@RequestBody Collection c){
        Long uid=userIdFromToken(token); // 从 token 解析出你是谁（userId）
        if(uid==null) return ApiResponse.fail("unauthorized");
        c.setUserId(uid); // 把 userId 塞进 c 对象
        // 关键：精准查找该条目之前的收藏状态，用于动态对比
        Collection before = collectionService.getByUserSubject(uid, c.getSubjectId());
        int r = collectionService.upsert(c); // 保存收藏（插入或更新）
        collectionService.logActivities(before,c); // 根据对比生成动态
        return ApiResponse.ok(r);
    }
    @GetMapping("/my")
    public ApiResponse<PageResult<Collection>> my(@RequestHeader("Authorization") String token,@RequestParam(defaultValue="1") int pageNum,@RequestParam(defaultValue="12") int pageSize){
        Long uid=userIdFromToken(token);
        if(uid==null) return ApiResponse.fail("unauthorized");
        return ApiResponse.ok(collectionService.list(uid,pageNum,pageSize));
    }
    @GetMapping("/one")
    public ApiResponse<Collection> one(@RequestHeader("Authorization") String token,@RequestParam Long subjectId){
        Long uid=userIdFromToken(token);
        if(uid==null) return ApiResponse.fail("unauthorized");
        Collection c = collectionService.getByUserSubject(uid,subjectId);
        return ApiResponse.ok(c);
    }

    @DeleteMapping("/one")
    public ApiResponse<Integer> delete(@RequestHeader("Authorization") String token,@RequestParam Long subjectId){
        Long uid=userIdFromToken(token);
        if(uid==null) return ApiResponse.fail("unauthorized");
        int r = collectionService.delete(uid,subjectId);
        return ApiResponse.ok(r);
    }
    @GetMapping("/by-user")
    public ApiResponse<PageResult<Collection>> byUser(@RequestParam Long userId,@RequestParam(defaultValue="1") int pageNum,@RequestParam(defaultValue="15") int pageSize){
        return ApiResponse.ok(collectionService.list(userId,pageNum,pageSize));
    }
}
    
