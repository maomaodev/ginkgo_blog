package com.ginkgoblog.base.vo;

import lombok.Data;

/**
 * 用于分页
 *
 * @author maomao
 * @date 2021-01-27
 */
@Data
public class PageInfo {
    /**
     * 关键字
     */
    private String keyword;

    /**
     * 当前页
     */
    private Long currentPage;

    /**
     * 页大小
     */
    private Long pageSize;
}
