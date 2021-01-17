package com.ginkgoblog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
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
