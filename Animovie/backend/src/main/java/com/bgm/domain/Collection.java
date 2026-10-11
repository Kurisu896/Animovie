package com.bgm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 实体：收藏记录，对应表 collections
 * 字段：id 主键；userId 用户ID；subjectId 条目ID；status 收藏状态（WATCHING/WATCHED/DROPPED）；
 * progressEpisode 观看进度；rating 评分；shortComment 吐槽；updatedDate 更新时间
 */
@Data
@TableName("collections")
public class Collection {
    @TableId(type = IdType.AUTO)
    Long id;
    Long userId;
    Long subjectId;
    String status;
    Integer progressEpisode;
    Integer rating;
    String shortComment;
    @JsonFormat(pattern="yyyy-MM-dd  HH:mm:ss", timezone="Asia/Shanghai")
    LocalDateTime updatedDate;
}
