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
     * 给博客设置标签
     *
     * @param blog
     * @return
     */
    Blog setTagByBlog(Blog blog);

    /**
     * 给博客设置分类
     *
     * @param blog
     * @return
     */
    Blog setSortByBlog(Blog blog);

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
     * 根据BlogUid获取相关的博客
     *
     * @param blogUid
     * @return
     */
    List<Blog> getSameBlogByBlogUid(String blogUid);

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

    /**
     * 通过博客Uid获取点赞数
     *
     * @param uid
     * @return
     */
    Integer getBlogPraiseCountByUid(String uid);

    /**
     * 通过UID给博客点赞
     *
     * @param uid
     * @return
     */
     String praiseBlogByUid(String uid);

    /**
     * 根据标签Uid获取相关的博客
     *
     * @param tagUid
     * @return
     */
    IPage<Blog> getSameBlogByTagUid(String tagUid);

    /**
     * 通过博客分类UID获取博客列表
     *
     * @param blogSortUid
     * @param currentPage
     * @param pageSize
     * @return
     */
    IPage<Blog> getListByBlogSortUid(String blogSortUid, Long currentPage, Long pageSize);

}
