package com.bgm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 实体：话题评论，对应表 comments
 * 字段：id 主键；topicId 话题ID；userId 用户ID；parentId 父评论ID；content 内容；
 * likeCount 点赞数；floor 楼层；createdAt 创建时间；username/nickname/avatarPath/signature 冗余展示字段
 */
@Data
@TableName("comments")
public class Comment {
    @TableId(type = IdType.AUTO)
    Long id;
    @TableField("topic_id") Long topicId;
    @TableField("user_id") Long userId;
    @TableField("parent_id") Long parentId;
    String content;
    @TableField("like_count") Integer likeCount;
    Integer floor;
    @JsonFormat(pattern="yyyy-MM-dd  HH:mm:ss", timezone="Asia/Shanghai")
    @TableField("created_at") LocalDateTime createdAt;
    @TableField(exist=false) String username;
    @TableField(exist=false) String nickname;
    @TableField(exist=false) String avatarPath;
    @TableField(exist=false) String signature;
}
