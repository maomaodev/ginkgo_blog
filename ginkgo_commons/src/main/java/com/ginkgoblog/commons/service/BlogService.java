package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.Blog;

import java.util.List;

/**
 * 博客表 Service 层
 *
 * @author maomao
 * @date 2021-01-17
 */
public interface BlogService extends IService<Blog> {

    /**
     * 给博客列表设置分类和标签
     *
     * @param list
     * @return
     */
    List<Blog> setTagAndSortByBlogList(List<Blog> list);

    /**
     * 给博客列表设置分类，标签，图片
     *
     * @param list
     * @return
     */
    List<Blog> setTagAndSortAndPictureByBlogList(List<Blog> list);


    /**
     * 通过标签搜索博客
     *
     * @param tagUid
     * @param currentPage
     * @param pageSize
     * @return
     */
    IPage<Blog> searchBlogByTag(String tagUid, Long currentPage, Long pageSize);

    /**
     * 获取博客的归档日期
     *
     * @return
     */
    String getBlogTimeSortList();

    /**
     * 通过月份获取日期
     *
     * @param monthDate
     * @return
     */
    String getArticleByMonth(String monthDate);
}
