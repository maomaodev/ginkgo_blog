package com.ginkgoblog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论表
 *
 * @author maomao
 * @date 2021-01-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_comment")
public class Comment extends SuperEntity {
    /**
     * 用户id
     */
    private String userId;

    /**
     * 一级评论id
     */
    private String firstCommentId;

    /**
     * 回复某条评论的id
     */
    private String toCommentId;

    /**
     * 回复某个人的id
     */
    private String toUserId;

    /**
     * 博客id
     */
    private String blogId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论来源
     */
    private String source;

    /**
     * 论类型，0:评论，1:点赞
     */
    private Integer type;
}
