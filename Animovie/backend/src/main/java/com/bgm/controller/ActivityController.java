package com.bgm.controller;

import com.bgm.common.ApiResponse;
import com.bgm.domain.User;
import com.bgm.domain.UserActivity;
import com.bgm.mapper.UserMapper;
import com.bgm.mapper.UserActivityMapper;
import com.bgm.utils.security.JwtUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
/**
 * 控制器：时间线/动态（user_activity）
 * 职责：全站动态列表、按用户分页动态、管理员删除动态
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Autowired
    UserActivityMapper userActivityMapper;
    @Autowired
    UserMapper userMapper;
    private Long userIdFromToken(String token){
        if(token==null) return null;
        if(token.startsWith("Bearer ")) token=token.substring(7);
        Claims c= JwtUtil.parseClaims(token);
        if(c==null) return null;
        String username=(String)c.get("username");
        User u=userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return u==null?null:u.getId();
    }
    private boolean isAdmin(String token){
        if(token==null) return false; if(token.startsWith("Bearer ")) token=token.substring(7);
        io.jsonwebtoken.Claims c= com.bgm.utils.security.JwtUtil.parseClaims(token); if(c==null) return false; Object role=c.get("role"); return role!=null && role.toString().equals("ADMIN");
    }
    @GetMapping("/my")
    public ApiResponse<List<UserActivity>> my(@RequestHeader("Authorization") String token,@RequestParam(defaultValue="1") int pageNum,@RequestParam(defaultValue="20") int pageSize){
        Long uid=userIdFromToken(token);
        if(uid==null) return ApiResponse.fail("unauthorized");
        Page<UserActivity> page = new Page<>(pageNum,pageSize);
        IPage<UserActivity> res = userActivityMapper.listByUser(page, uid);
        return ApiResponse.ok(res.getRecords());
    }
    /** 全站动态分页 */
    @GetMapping("/all")
    public ApiResponse<com.bgm.service.PageResult<UserActivity>> all(@RequestParam(defaultValue="1") int pageNum,@RequestParam(defaultValue="20") int pageSize){
        Page<UserActivity> page = new Page<>(pageNum,pageSize);
        IPage<UserActivity> res = userActivityMapper.listAll(page);
        com.bgm.service.PageResult<UserActivity> pr=new com.bgm.service.PageResult<>();
        pr.setItems(res.getRecords()); pr.setTotal(res.getTotal()); pr.setPageNum(pageNum); pr.setPageSize(pageSize);
        return ApiResponse.ok(pr);
    }

    /** 指定用户的动态分页 */
    @GetMapping("/user")
    public ApiResponse<com.bgm.service.PageResult<UserActivity>> byUser(@RequestParam Long userId,@RequestParam(defaultValue="1") int pageNum,@RequestParam(defaultValue="20") int pageSize){
        Page<UserActivity> page = new Page<>(pageNum,pageSize);
        IPage<UserActivity> res = userActivityMapper.listByUser(page, userId);
        com.bgm.service.PageResult<UserActivity> pr=new com.bgm.service.PageResult<>();
        pr.setItems(res.getRecords()); pr.setTotal(res.getTotal()); pr.setPageNum(pageNum); pr.setPageSize(pageSize);
        return ApiResponse.ok(pr);
    }

    @DeleteMapping("/admin/one")
    public ApiResponse<Integer> adminDelete(@RequestHeader("Authorization") String token,@RequestParam Long id){
        if(!isAdmin(token)) return ApiResponse.fail("forbidden");
        int r = userActivityMapper.deleteById(id);
        return ApiResponse.ok(r);
    }

    @DeleteMapping("/one")
    public ApiResponse<Integer> delete(@RequestHeader("Authorization") String token,@RequestParam Long id){
        Long uid=userIdFromToken(token);
        if(uid==null) return ApiResponse.fail("unauthorized");
        int r=userActivityMapper.deleteByIdAndUser(id,uid);
        return ApiResponse.ok(r);
    }
}
