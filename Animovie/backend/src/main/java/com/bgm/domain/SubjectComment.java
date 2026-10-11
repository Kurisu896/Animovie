package com.bgm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 实体：条目评论，对应表 subject_comments
 * 字段：id 主键；subjectId 条目ID；userId 用户ID；content 内容；likeCount 点赞数；
 * createdAt 创建时间；username/nickname/avatarPath/signature 冗余展示字段
 */
@Data
@TableName("subject_comments")
public class SubjectComment {
    @TableId(type = IdType.AUTO)
    Long id;
    @TableField("subject_id") Long subjectId;
    @TableField("user_id") Long userId;
    String content;
    @TableField("like_count") Integer likeCount;
    @JsonFormat(pattern="yyyy-MM-dd  HH:mm:ss", timezone="Asia/Shanghai")
    @TableField("created_at") LocalDateTime createdAt;
    @TableField(exist=false) String username;
    @TableField(exist=false) String nickname;
    @TableField(exist=false) String avatarPath;
    @TableField(exist=false) String signature;
}
