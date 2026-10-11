package com.bgm.controller;

import com.bgm.common.ApiResponse;
import com.bgm.domain.Comment;
import com.bgm.domain.Topic;
import com.bgm.domain.User;
import com.bgm.mapper.CommentMapper;
import com.bgm.mapper.UserMapper;
import com.bgm.service.DiscussionService;
import com.bgm.service.PageResult;
import com.bgm.utils.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
/**
 * 控制器：论坛话题与评论（topics/comments）
 * 职责：话题详情、评论列表、点赞、管理员删除话题/评论、发表评论
 */
@RestController
@RequestMapping("/discussion")
public class DiscussionController {
    @Autowired
    DiscussionService discussionService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    CommentMapper commentMapper;
    private Long userIdFromToken(String token){
        if(token==null) return null; if(token.startsWith("Bearer ")) token=token.substring(7);
        Claims c= JwtUtil.parseClaims(token); if(c==null) return null; String username=(String)c.get("username"); User u=userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>().eq(User::getUsername, username)); return u==null?null:u.getId();
    }

    @GetMapping("/topics")
    public ApiResponse<PageResult<Topic>> topics(@RequestParam Long subjectId,@RequestParam(defaultValue="1") int pageNum,@RequestParam(defaultValue="10") int pageSize){
        return ApiResponse.ok(discussionService.listTopics(subjectId,pageNum,pageSize));
    }
    @GetMapping("/all")
    public ApiResponse<PageResult<Topic>> all(@RequestParam(defaultValue="1") int pageNum,@RequestParam(defaultValue="10") int pageSize){
        return ApiResponse.ok(discussionService.listAllTopics(pageNum,pageSize));
    }
    @GetMapping("/search")
    public ApiResponse<PageResult<Topic>> search(@RequestParam String keyword,@RequestParam(defaultValue="1") int pageNum,@RequestParam(defaultValue="10") int pageSize){
        return ApiResponse.ok(discussionService.searchTopics(keyword,pageNum,pageSize));
    }
    @PostMapping("/topic")
    public ApiResponse<Topic> createTopic(@RequestHeader("Authorization") String token,@RequestParam Long subjectId,@RequestParam String title,@RequestParam(required=false) String content){
        Long uid=userIdFromToken(token); if(uid==null) return ApiResponse.fail("unauthorized");
        User u = userMapper.selectById(uid);
        if(u!=null && u.getStatus()!=null && u.getStatus()==0){
            return ApiResponse.fail("用户已被封禁，禁止进行该操作");
        }
        return ApiResponse.ok(discussionService.createTopic(subjectId,uid,title,content));
    }

    @PostMapping("/topic/update")
    public ApiResponse<String> updateTopic(@RequestHeader("Authorization") String token,@RequestParam Long topicId,@RequestParam String title,@RequestParam(required=false) String content){
        Long uid=userIdFromToken(token);
        Topic t=discussionService.getTopic(topicId);
        if(!isAdmin(token) && (uid==null || t==null || !t.getUserId().equals(uid))) return ApiResponse.fail("forbidden");
        int r = discussionService.updateTopic(topicId,title,content);
        return r>0?ApiResponse.ok("ok"):ApiResponse.fail("failed");
    }

    /** 管理员：待审核列表（LOCKED） */
    @GetMapping("/admin/pending")
    public ApiResponse<PageResult<Topic>> pending(@RequestHeader("Authorization") String token,@RequestParam(defaultValue="1") int pageNum,@RequestParam(defaultValue="10") int pageSize){
        if(!isAdmin(token)) return ApiResponse.fail("forbidden");
        return ApiResponse.ok(discussionService.listPending(pageNum,pageSize));
    }
    /** 管理员：已打回列表（REJECTED） */
    @GetMapping("/admin/rejected")
    public ApiResponse<PageResult<Topic>> rejected(@RequestHeader("Authorization") String token,@RequestParam(defaultValue="1") int pageNum,@RequestParam(defaultValue="10") int pageSize){
        if(!isAdmin(token)) return ApiResponse.fail("forbidden");
        return ApiResponse.ok(discussionService.listRejected(pageNum,pageSize));
    }
    /** 管理员：审核通过 */
    @PostMapping("/admin/approve")
    public ApiResponse<String> approve(@RequestHeader("Authorization") String token,@RequestParam Long topicId){
        if(!isAdmin(token)) return ApiResponse.fail("forbidden");
        int r = discussionService.approveTopic(topicId);
        return r>0?ApiResponse.ok("ok"):ApiResponse.fail("failed");
    }
    /** 管理员：退回（打回） */
    @PostMapping("/admin/reject")
    public ApiResponse<String> reject(@RequestHeader("Authorization") String token,@RequestParam Long topicId){
        if(!isAdmin(token)) return ApiResponse.fail("forbidden");
        int r = discussionService.rejectTopic(topicId);
        return r>0?ApiResponse.ok("ok"):ApiResponse.fail("failed");
    }
    /** 管理员：重新锁定（待审核） */
    @PostMapping("/admin/lock")
    public ApiResponse<String> lock(@RequestHeader("Authorization") String token,@RequestParam Long topicId){
        if(!isAdmin(token)) return ApiResponse.fail("forbidden");
        int r = discussionService.lockTopic(topicId);
        return r>0?ApiResponse.ok("ok"):ApiResponse.fail("failed");
    }
    /** 作者：我的话题（包含 OPEN/LOCKED） */
    @GetMapping("/my")
    public ApiResponse<PageResult<Topic>> my(@RequestHeader("Authorization") String token,@RequestParam(defaultValue="1") int pageNum,@RequestParam(defaultValue="10") int pageSize){
        Long uid=userIdFromToken(token); if(uid==null) return ApiResponse.fail("unauthorized");
        return ApiResponse.ok(discussionService.listByUser(uid,pageNum,pageSize));
    }

    /** 指定用户的话题列表（用于用户主页选项卡） */
    @GetMapping("/user-topics")
    public ApiResponse<PageResult<Topic>> topicsByUser(@RequestParam Long userId,@RequestParam(defaultValue="1") int pageNum,@RequestParam(defaultValue="10") int pageSize){
        return ApiResponse.ok(discussionService.listByUser(userId,pageNum,pageSize));
    }
    @GetMapping("/topic/{id}")
    public ApiResponse<Topic> getTopic(@RequestHeader(value="Authorization",required=false) String token,@PathVariable Long id){
        Topic t = discussionService.getTopic(id);
        if(t==null) return ApiResponse.fail("not_found");
        if("OPEN".equalsIgnoreCase(String.valueOf(t.getStatus()))) return ApiResponse.ok(t);
        Long uid=userIdFromToken(token);
        if(isAdmin(token) || (uid!=null && t.getUserId()!=null && t.getUserId().equals(uid))) return ApiResponse.ok(t);
        return ApiResponse.fail("not_found");
    }
    /** 列出话题评论：支持 sort=latest/likes 排序 */
    @GetMapping("/comments")
    public ApiResponse<PageResult<Comment>> comments(@RequestParam Long topicId,@RequestParam(defaultValue="1") int pageNum,@RequestParam(defaultValue="20") int pageSize,@RequestParam(defaultValue="latest") String sort){
        return ApiResponse.ok(discussionService.listComments(topicId,pageNum,pageSize,sort));
    }
    @PostMapping("/comment")
    public ApiResponse<Comment> addComment(@RequestHeader("Authorization") String token,@RequestParam Long topicId,@RequestParam(required=false) Long parentId,@RequestParam String content){
        Long uid=userIdFromToken(token); if(uid==null) return ApiResponse.fail("unauthorized");
        User u = userMapper.selectById(uid);
        if(u!=null && u.getStatus()!=null && u.getStatus()==0){
            return ApiResponse.fail("用户已被封禁，禁止进行该操作");
        }
        return ApiResponse.ok(discussionService.addComment(topicId,uid,parentId,content));
    }
    @PostMapping("/like")
    public ApiResponse<String> like(@RequestHeader("Authorization") String token,@RequestParam Long commentId){
        Long uid=userIdFromToken(token); if(uid==null) return ApiResponse.fail("unauthorized");
        boolean ok=discussionService.likeComment(commentId,uid); return ok?ApiResponse.ok("liked"):ApiResponse.fail("exists");
    }
    @DeleteMapping("/like")
    public ApiResponse<String> unlike(@RequestHeader("Authorization") String token,@RequestParam Long commentId){
        Long uid=userIdFromToken(token); if(uid==null) return ApiResponse.fail("unauthorized");
        boolean ok=discussionService.unlikeComment(commentId,uid); return ok?ApiResponse.ok("unliked"):ApiResponse.fail("not_found");
    }

    @GetMapping("/liked")
    public ApiResponse<java.util.List<Long>> liked(@RequestHeader("Authorization") String token,@RequestParam Long topicId){
        Long uid=userIdFromToken(token); if(uid==null) return ApiResponse.fail("unauthorized");
        return ApiResponse.ok(discussionService.likedByUser(uid,topicId));
    }

    private boolean isAdmin(String token){ if(token==null) return false; if(token.startsWith("Bearer ")) token=token.substring(7); io.jsonwebtoken.Claims c= com.bgm.utils.security.JwtUtil.parseClaims(token); if(c==null) return false; Object role=c.get("role"); return role!=null && role.toString().equals("ADMIN"); }
    @DeleteMapping("/topic/{id}")
    public ApiResponse<Integer> deleteTopic(@RequestHeader("Authorization") String token,@PathVariable Long id){ Long uid=userIdFromToken(token); Topic t=discussionService.getTopic(id); if(!isAdmin(token) && (uid==null || t==null || !t.getUserId().equals(uid))) return ApiResponse.fail("forbidden"); return ApiResponse.ok(discussionService.deleteTopic(id)); }
    @DeleteMapping("/comment/{id}")
    public ApiResponse<Integer> deleteComment(@RequestHeader("Authorization") String token,@PathVariable Long id){
        Long uid = userIdFromToken(token);
        if(isAdmin(token)){
            return ApiResponse.ok(discussionService.deleteComment(id));
        }
        if(uid==null) return ApiResponse.fail("unauthorized");
        com.bgm.domain.Comment c = commentMapper.selectById(id);
        if(c==null || c.getUserId()==null || !c.getUserId().equals(uid)) return ApiResponse.fail("forbidden");
        return ApiResponse.ok(discussionService.deleteComment(id));
    }
}
