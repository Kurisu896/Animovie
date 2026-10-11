package com.bgm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 实体：条目，对应表 subjects
 * 字段：id 主键；cnName 中文名；jpName 日文名；alias 别名；firstAirDate 首播日期；
 * episodes 话数；studio 制作公司；country 国家/地区；officialSite 官网；category 分类；
 * tags 标签；coverPath 封面路径；collectCount 收藏数；avgRating 平均评分；ratingCount 评分人数；
 * updatedDate 更新时间
 */
@Data
@TableName("subjects")
public class Subject {
    @TableId(type = IdType.AUTO)
    Long id;
    @TableField("cn_name") String cnName;
    @TableField("jp_name") String jpName;
    String alias;
    @TableField("first_air_date") LocalDate firstAirDate;
    Integer episodes;
    String studio;
    String country;
    @TableField("official_site") String officialSite;
    String category;
    String tags;
    @TableField("cover_path") String coverPath;
    @TableField(exist=false) Integer collectCount;
    @TableField(exist=false) Double avgRating;
    @TableField(exist=false) Integer ratingCount;
    @TableField("updated_date") LocalDateTime updatedDate;
}
