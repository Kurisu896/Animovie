package com.bgm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 实体：话题，对应表 topics
 * 字段：id 主键；subjectId 条目ID；userId 用户ID；title 标题；content 内容；status 状态；
 * replyCount 回复数；createdAt/updatedAt 时间；username/nickname/avatarPath/signature 冗余展示字段
 */
@Data
@TableName("topics")
public class Topic {
    @TableId(type = IdType.AUTO)
    Long id;
    @TableField("subject_id") Long subjectId;
    @TableField("user_id") Long userId;
    String title;
    String content;
    String status;
    @TableField("reply_count") Integer replyCount;
    @JsonFormat(pattern="yyyy-MM-dd  HH:mm:ss", timezone="Asia/Shanghai")
    @TableField("created_at") LocalDateTime createdAt;
    @JsonFormat(pattern="yyyy-MM-dd  HH:mm:ss", timezone="Asia/Shanghai")
    @TableField("updated_at") LocalDateTime updatedAt;
    @TableField(exist=false) String username;
    @TableField(exist=false) String nickname;
    @TableField(exist=false) String avatarPath;
    @TableField(exist=false) String signature;
}
