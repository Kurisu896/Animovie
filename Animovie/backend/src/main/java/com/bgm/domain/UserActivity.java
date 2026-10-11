package com.bgm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 实体：用户动态/时间线事件，对应表 user_activity
 * 字段：id 主键；userId 用户ID；subjectId 条目ID；type 类型（STATUS/PROGRESS/RATING/COMMENT 等）；
 * detail 详情文本；createdAt 创建时间
 */
@Data
@TableName("user_activity")
public class UserActivity {
    @TableId(type = IdType.AUTO)
    Long id;
    Long userId;
    Long subjectId;
    String type;
    String detail;
    @JsonFormat(pattern="yyyy-MM-dd  HH:mm:ss", timezone="Asia/Shanghai")
    LocalDateTime createdAt;
}
