package com.ginkgoblog.commons.vo;

import com.ginkgoblog.base.vo.SuperVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 友链表 表现层对象
 *
 * @author maomao
 * @date 2021-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LinkVO extends SuperVO {
    /**
     * 友链标题
     */
    private String title;
    /**
     * 友链介绍
     */
    private String summary;
    /**
     * 友链地址
     */
    private String url;

    /**
     * 友链状态： 0 申请中， 1：已上线，  2：已拒绝
     */
    private Integer linkStatus;

    /**
     * 排序字段
     */
    private Integer sort;
}
