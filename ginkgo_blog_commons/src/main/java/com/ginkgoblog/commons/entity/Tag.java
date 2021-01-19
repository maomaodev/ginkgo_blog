package com.ginkgoblog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ginkgoblog.base.entity.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签表
 *
 * @author maomao
 * @date 2021-01-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_tag")
public class Tag extends SuperEntity {
    /**
     * 标签内容
     */
    private String content;

    /**
     * 标签简介
     */
    private Integer clickCount;

    /**
     * 排序字段，数值越大，越靠前
     */
    private Integer sort;
}
