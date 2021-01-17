package com.ginkgoblog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 博客分类表
 *
 * @author maomao
 * @date 2021-01-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_blog_sort")
public class BlogSort extends SuperEntity {
    /**
     * 标签名
     */
    private String name;

    /**
     * 标签点击数
     */
    private Integer clickCount;

    /**
     * 排序字段，越大越靠前
     */
    private Integer order;
}
