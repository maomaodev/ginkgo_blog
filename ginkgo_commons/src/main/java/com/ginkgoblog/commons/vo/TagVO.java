package com.ginkgoblog.commons.vo;

import com.ginkgoblog.base.vo.SuperVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签表 表现层对象
 *
 * @author maomao
 * @date 2021-01-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TagVO extends SuperVO {
    /**
     * 标签内容
     */
//    @NotBlank(groups = {Insert.class, Update.class})
    private String content;

    /**
     * 排序字段
     */
    private Integer sort;
}
