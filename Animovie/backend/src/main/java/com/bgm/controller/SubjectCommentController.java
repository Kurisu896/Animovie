package com.bgm.controller;

import com.bgm.common.ApiResponse;
import com.bgm.domain.SubjectComment;
import com.bgm.domain.User;
import com.bgm.mapper.UserMapper;
import com.bgm.service.PageResult;
import com.bgm.service.SubjectCommentService;
import com.bgm.utils.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
/**
 * 控制器：条目评论（subject_comments）
 * 职责：列表、发表评论、点赞、删除（管理员/本人）
 */
@RestController
@RequestMapping("/subject-comments")
public class SubjectCommentController {
    @Autowired
    SubjectCommentService subjectCommentService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    com.bgm.mapper.SubjectCommentMapper subjectCommentMapper;
    private Long userIdFromToken(String token){
        if(token==null) return null; if(token.startsWith("Bearer ")) token=token.substring(7);
        Claims c= JwtUtil.parseClaims(token); if(c==null) return null; String username=(String)c.get("username"); User u=userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>().eq(User::getUsername, username)); return u==null?null:u.getId();
    }
    /** 列出条目评论：支持 sort=latest/likes 排序 */
    @GetMapping
    public ApiResponse<PageResult<SubjectComment>> list(@RequestParam Long subjectId,@RequestParam(defaultValue="1") int pageNum,@RequestParam(defaultValue="20") int pageSize,@RequestParam(defaultValue="latest") String sort){
        return ApiResponse.ok(subjectCommentService.list(subjectId,pageNum,pageSize,sort));
    }
    @PostMapping
    public ApiResponse<SubjectComment> add(@RequestHeader("Authorization") String token,@RequestParam Long subjectId,@RequestParam String content){
        Long uid=userIdFromToken(token); if(uid==null) return ApiResponse.fail("unauthorized");
        User u = userMapper.selectById(uid);
        if(u!=null && u.getStatus()!=null && u.getStatus()==0){
            return ApiResponse.fail("用户已被封禁，禁止进行该操作");
        }
        return ApiResponse.ok(subjectCommentService.add(subjectId,uid,content));
    }
    @PostMapping("/like")
    public ApiResponse<String> like(@RequestHeader("Authorization") String token,@RequestParam Long id){
        Long uid=userIdFromToken(token); if(uid==null) return ApiResponse.fail("unauthorized");
        boolean ok=subjectCommentService.like(id,uid); return ok?ApiResponse.ok("liked"):ApiResponse.fail("exists");
    }
    @DeleteMapping("/like")
    public ApiResponse<String> unlike(@RequestHeader("Authorization") String token,@RequestParam Long id){
        Long uid=userIdFromToken(token); if(uid==null) return ApiResponse.fail("unauthorized");
        boolean ok=subjectCommentService.unlike(id,uid); return ok?ApiResponse.ok("unliked"):ApiResponse.fail("not_found");
    }

    @GetMapping("/liked")
    public ApiResponse<java.util.List<Long>> liked(@RequestHeader("Authorization") String token,@RequestParam Long subjectId){
        Long uid=userIdFromToken(token); if(uid==null) return ApiResponse.fail("unauthorized");
        return ApiResponse.ok(subjectCommentService.likedByUser(uid,subjectId));
    }

    private boolean isAdmin(String token){ if(token==null) return false; if(token.startsWith("Bearer ")) token=token.substring(7); io.jsonwebtoken.Claims c= com.bgm.utils.security.JwtUtil.parseClaims(token); if(c==null) return false; Object role=c.get("role"); return role!=null && role.toString().equals("ADMIN"); }
    /** 管理员删除评论（任意） */
    @DeleteMapping("/{id}")
    public ApiResponse<Integer> delete(@RequestHeader("Authorization") String token,@PathVariable Long id){ if(!isAdmin(token)) return ApiResponse.fail("forbidden"); int r=subjectCommentMapper.deleteById(id); return ApiResponse.ok(r); }

    /** 用户删除自己的评论 */
    @DeleteMapping("/user/{id}")
    public ApiResponse<Integer> deleteMy(@RequestHeader("Authorization") String token,@PathVariable Long id){ Long uid=userIdFromToken(token); if(uid==null) return ApiResponse.fail("unauthorized"); int r=subjectCommentMapper.deleteByIdAndUser(id,uid); return r>0?ApiResponse.ok(r):ApiResponse.fail("forbidden"); }
}
