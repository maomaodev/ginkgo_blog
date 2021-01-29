package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.BlogSort;
import com.ginkgoblog.commons.vo.BlogSortVO;

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
     * @param blogSortVO
     * @return
     */
    IPage<BlogSort> getPageList(BlogSortVO blogSortVO);

    /**
     * 获取博客分类列表
     *
     * @return
     */
    List<BlogSort> getList();

    /**
     * 新增博客分类
     *
     * @param blogSortVO
     */
    String addBlogSort(BlogSortVO blogSortVO);

    /**
     * 编辑博客分类
     *
     * @param blogSortVO
     */
    String editBlogSort(BlogSortVO blogSortVO);

    /**
     * 批量删除博客分类
     *
     * @param blogSortVoList
     */
    String deleteBatchBlogSort(List<BlogSortVO> blogSortVoList);

    /**
     * 置顶博客分类
     *
     * @param blogSortVO
     */
    String stickBlogSort(BlogSortVO blogSortVO);

    /**
     * 通过点击量排序博客
     *
     * @return
     */
    String blogSortByClickCount();

    /**
     * 通过引用量排序博客
     *
     * @return
     */
    String blogSortByCite();

    /**
     * 获取排序最高的一个博客分类
     *
     * @return
     */
    BlogSort getTopOne();
}
