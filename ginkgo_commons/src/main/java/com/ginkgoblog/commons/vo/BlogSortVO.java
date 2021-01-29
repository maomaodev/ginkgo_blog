package com.ginkgoblog.commons.vo;

import com.ginkgoblog.base.vo.SuperVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 博客分类表 表现层对象
 *
 * @author maomao
 * @date 2021-01-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BlogSortVO extends SuperVO {

    /**
     * 分类名
     */
//    @NotBlank(groups = {Insert.class, Update.class})
    private String sortName;

    /**
     * 分类介绍
     */
    private String content;

    /**
     * 排序字段
     */
    private Integer sort;
}
