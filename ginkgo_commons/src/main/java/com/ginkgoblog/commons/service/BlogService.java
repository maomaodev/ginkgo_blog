package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.Blog;
import com.ginkgoblog.commons.vo.BlogVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
     * 通过推荐等级获取博客列表
     *
     * @param level
     * @return
     */
    List<Blog> getBlogListByLevel(Integer level);

    /**
     * 通过推荐等级获取博客Page，是否排序
     *
     * @param level
     * @return
     */
    IPage<Blog> getBlogPageByLevel(Page<Blog> page, Integer level, Integer useSort);

    /**
     * 通过状态获取博客数量
     *
     * @author xzx19950624@qq.com
     * @date 2018年10月22日下午3:30:28
     */
    Integer getBlogCount(Integer status);

    /**
     * 通过标签获取博客数目
     *
     * @author Administrator
     * @date 2019年6月19日16:28:16
     */
    List<Map<String, Object>> getBlogCountByTag();

    /**
     * 通过标签获取博客数目
     *
     * @author Administrator
     * @date 2019年11月27日13:14:34
     */
    List<Map<String, Object>> getBlogCountByBlogSort();

    /**
     * 获取一年内的文章贡献数
     *
     * @return
     */
    Map<String, Object> getBlogContributeCount();

    /**
     * 通过uid获取博客内容
     *
     * @param uid
     * @return
     */
    Blog getBlogByUid(String uid);

    /**
     * 根据BlogUid获取相关的博客
     *
     * @param blogUid
     * @return
     */
    List<Blog> getSameBlogByBlogUid(String blogUid);

    /**
     * 获取点击量前top的博客列表
     *
     * @param top
     * @return
     */
    List<Blog> getBlogListByTop(Integer top);

    /**
     * 获取博客列表
     *
     * @param blogVO
     * @return
     */
    IPage<Blog> getPageList(BlogVO blogVO);

    /**
     * 新增博客
     *
     * @param blogVO
     */
    String addBlog(BlogVO blogVO);

    /**
     * 编辑博客
     *
     * @param blogVO
     */
    String editBlog(BlogVO blogVO);

    /**
     * 推荐博客排序调整
     *
     * @param blogVOList
     * @return
     */
    String editBatch(List<BlogVO> blogVOList);

    /**
     * 批量删除博客
     *
     * @param blogVO
     */
    String deleteBlog(BlogVO blogVO);

    /**
     * 批量删除博客
     *
     * @param blogVoList
     * @return
     */
    String deleteBatchBlog(List<BlogVO> blogVoList);

    /**
     * 本地博客上传
     *
     * @param filedatas
     * @return
     */
    String uploadLocalBlog(List<MultipartFile> filedatas) throws IOException;

    /**
     *  web端使用的接口
     */

    /**
     * 通过推荐等级获取博客Page
     *
     * @param level       推荐级别
     * @param currentPage 当前页
     * @param useSort     是否使用排序字段
     * @return
     */
     IPage<Blog> getBlogPageByLevel(Integer level, Long currentPage, Integer useSort);

    /**
     * 获取首页排行博客
     *
     * @return
     */
    IPage<Blog> getHotBlog();


    /**
     * 获取最新的博客
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    IPage<Blog> getNewBlog(Long currentPage, Long pageSize);

    /**
     * 按时间戳获取博客
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    IPage<Blog> getBlogByTime(Long currentPage, Long pageSize);

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

    /**
     * 通过关键字搜索博客列表
     *
     * @param keywords
     * @param currentPage
     * @param pageSize
     * @return
     */
    Map<String, Object> getBlogByKeyword(String keywords, Long currentPage, Long pageSize);

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
     * 通过博客分类搜索博客
     *
     * @param blogSortUid
     * @param currentPage
     * @param pageSize
     * @return
     */
    IPage<Blog> searchBlogByBlogSort(String blogSortUid, Long currentPage, Long pageSize);

    /**
     * 通过作者搜索博客
     *
     * @param author
     * @param currentPage
     * @param pageSize
     * @return
     */
    IPage<Blog> searchBlogByAuthor(String author, Long currentPage, Long pageSize);


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
