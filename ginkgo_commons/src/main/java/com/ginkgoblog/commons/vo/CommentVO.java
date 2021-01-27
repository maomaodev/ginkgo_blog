package com.ginkgoblog.commons.vo;

import com.ginkgoblog.base.vo.SuperVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论表 表现层对象
 *
 * @author maomao
 * @date 2021-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommentVO extends SuperVO {
    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 回复某条评论的uid
     */
    private String toUid;

    /**
     * 回复某个人的uid
     */
    private String toUserUid;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 评论类型： 0: 评论   1: 点赞
     */
    private Integer type;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 博客uid
     */
    private String blogUid;

    /**
     * 评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等
     */
    private String source;
}
