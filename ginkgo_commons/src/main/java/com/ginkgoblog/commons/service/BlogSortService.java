package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.BlogSort;

import java.util.List;

/**
 * 博客分类表 Service 层
 *
 * @author maomao
 * @date 2021-01-17
 */
public interface BlogSortService extends IService<BlogSort> {

    /**
     * 获取博客分类列表
     *
     * @return
     */
    List<BlogSort> getList();

}
