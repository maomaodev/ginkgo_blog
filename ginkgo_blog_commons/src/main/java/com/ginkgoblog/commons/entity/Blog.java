package com.ginkgoblog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 博客表
 *
 * @author maomao
 * @date 2021-01-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_blog")
public class Blog extends SuperEntity {
    /**
     * 博客标题
     */
    private String title;

    /**
     * 博客内容
     */
    private String content;

    /**
     * 博客作者
     */
    private String author;

    /**
     * 博客简介
     */
    private String summary;

    /**
     * 博客点击数
     */
    private Integer clickCount;

    /**
     * 博客收藏数
     */
    private Integer collectCount;

    /**
     * 推荐等级，0:正常
     */
    private Integer level;

    /**
     * 排序字段，越大越靠前
     */
    private Integer order;

    /**
     * 博客标签id
     */
    private String tagId;

    /**
     * 标题图片id
     */
    private String pictureId;

    /**
     * 管理员id
     */
    private String adminId;

    /**
     * 博客分类id
     */
    private String blogSortId;

    /**
     * 是否发布，0:否，1:是
     */
    private Boolean isPublish;

    /**
     * 是否原创，0:否，1:是
     */
    private Boolean isOriginal;

    /**
     * 是否开启评论，0:否，1:是
     */
    private Boolean isComment;
}
