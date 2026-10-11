package com.bgm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("subject_comment_likes")
public class SubjectCommentLike {
    @TableId(type = IdType.AUTO)
    Long id;
    @TableField("comment_id") Long commentId;
    @TableField("user_id") Long userId;
    @TableField("created_at") LocalDateTime createdAt;
}

