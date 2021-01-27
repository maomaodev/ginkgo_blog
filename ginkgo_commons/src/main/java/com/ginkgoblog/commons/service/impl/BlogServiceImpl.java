package com.ginkgoblog.commons.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.base.constants.MessageConstants;
import com.ginkgoblog.base.constants.RedisConstants;
import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.base.constants.SystemConstants;
import com.ginkgoblog.base.enums.*;
import com.ginkgoblog.base.holder.RequestHolder;
import com.ginkgoblog.commons.entity.Blog;
import com.ginkgoblog.commons.entity.BlogSort;
import com.ginkgoblog.commons.entity.Comment;
import com.ginkgoblog.commons.entity.Tag;
import com.ginkgoblog.commons.feign.PictureFeignClient;
import com.ginkgoblog.commons.mapper.BlogMapper;
import com.ginkgoblog.commons.service.BlogService;
import com.ginkgoblog.commons.service.BlogSortService;
import com.ginkgoblog.commons.service.CommentService;
import com.ginkgoblog.commons.service.TagService;
import com.ginkgoblog.commons.utils.WebUtils;
import com.ginkgoblog.utils.IpUtils;
import com.ginkgoblog.utils.JsonUtils;
import com.ginkgoblog.utils.ResultUtils;
import com.ginkgoblog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 博客表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-23
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Autowired
    private WebUtils webUtils;
    @Autowired
    private CommentService commentService;
    @Autowired
    private TagService tagService;
    @Autowired
    private BlogSortService blogSortService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private PictureFeignClient pictureFeignClient;

    @Value(value = "${BLOG.FIRST_COUNT}")
    private Integer BLOG_FIRST_COUNT;
    @Value(value = "${BLOG.SECOND_COUNT}")
    private Integer BLOG_SECOND_COUNT;
    @Value(value = "${BLOG.THIRD_COUNT}")
    private Integer BLOG_THIRD_COUNT;
    @Value(value = "${BLOG.FOURTH_COUNT}")
    private Integer BLOG_FOURTH_COUNT;
    @Value(value = "${BLOG.HOT_COUNT}")
    private Integer BLOG_HOT_COUNT;
    @Value(value = "${BLOG.NEW_COUNT}")
    private Integer BLOG_NEW_COUNT;

    @Override
    public List<Blog> setTagAndSortByBlogList(List<Blog> list) {
        List<String> sortUids = new ArrayList<>();
        List<String> tagUids = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                sortUids.add(item.getBlogSortUid());
            }
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                List<String> tagUidsTemp = StringUtils.changeStringToString(item.getTagUid(),
                        SqlConstants.FILE_SEGMENTATION);
                tagUids.addAll(tagUidsTemp);
            }
        });

        Collection<BlogSort> sortList = new ArrayList<>();
        Collection<Tag> tagList = new ArrayList<>();

        if (sortUids.size() > 0) {
            sortList = blogSortService.listByIds(sortUids);
        }
        if (tagUids.size() > 0) {
            tagList = tagService.listByIds(tagUids);
        }

        Map<String, BlogSort> sortMap = new HashMap<>();
        Map<String, Tag> tagMap = new HashMap<>();
        sortList.forEach(item -> sortMap.put(item.getUid(), item));
        tagList.forEach(item -> tagMap.put(item.getUid(), item));

        for (Blog item : list) {
            //设置分类
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                item.setBlogSort(sortMap.get(item.getBlogSortUid()));
            }

            //获取标签
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                List<String> tagUidsTemp = StringUtils.changeStringToString(
                        item.getTagUid(), SqlConstants.FILE_SEGMENTATION);
                List<Tag> tagListTemp = new ArrayList<>();

                tagUidsTemp.forEach(tag -> {
                    tagListTemp.add(tagMap.get(tag));
                });
                item.setTagList(tagListTemp);
            }
        }

        return list;
    }

    @Override
    public List<Blog> setTagAndSortAndPictureByBlogList(List<Blog> list) {
        StringBuffer fileUids = new StringBuffer();
        List<String> sortUids = new ArrayList<>();
        List<String> tagUids = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUids.append(item.getFileUid()).append(",");
            }
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                sortUids.add(item.getBlogSortUid());
            }
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                tagUids.add(item.getTagUid());
            }
        });

        // 远程服务调用获取图片信息
        String pictureList = pictureFeignClient.getPicture(fileUids.toString(), ",");
        // 解析图片的信息
        List<Map<String, Object>> picList = webUtils.getPictureMap(pictureList);
        // 分别获取博客分类和标签的信息
        Collection<BlogSort> sortList = new ArrayList<>();
        Collection<Tag> tagList = new ArrayList<>();
        if (sortUids.size() > 0) {
            sortList = blogSortService.listByIds(sortUids);
        }
        if (tagUids.size() > 0) {
            tagList = tagService.listByIds(tagUids);
        }

        // key为uid
        Map<String, BlogSort> sortMap = new HashMap<>();
        Map<String, Tag> tagMap = new HashMap<>();
        Map<String, String> pictureMap = new HashMap<>();

        sortList.forEach(item -> sortMap.put(item.getUid(), item));
        tagList.forEach(item -> tagMap.put(item.getUid(), item));
        picList.forEach(item -> pictureMap.put(item.get("uid").toString(),
                item.get("url").toString()));

        for (Blog item : list) {
            // 设置分类
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                item.setBlogSort(sortMap.get(item.getBlogSortUid()));
                if (sortMap.get(item.getBlogSortUid()) != null) {
                    item.setBlogSortName(sortMap.get(item.getBlogSortUid()).getSortName());
                }
            }

            // 获取标签
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                List<String> tagUidsTemp = StringUtils.changeStringToString(item.getTagUid(), ",");
                List<Tag> tagListTemp = new ArrayList<>();
                tagUidsTemp.forEach(tag -> tagListTemp.add(tagMap.get(tag)));
                item.setTagList(tagListTemp);
            }

            // 获取图片
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidsTemp = StringUtils.changeStringToString(item.getFileUid(), ",");
                List<String> pictureListTemp = new ArrayList<>();

                pictureUidsTemp.forEach(picture -> pictureListTemp.add(pictureMap.get(picture)));
                item.setPhotoList(pictureListTemp);
                // 只设置一张标题图
                if (pictureListTemp.size() > 0) {
                    item.setPhotoUrl(pictureListTemp.get(0));
                } else {
                    item.setPhotoUrl("");
                }
            }
        }
        return list;
    }

    @Override
    public Blog setTagByBlog(Blog blog) {
        String tagUid = blog.getTagUid();
        if (!StringUtils.isEmpty(tagUid)) {
            String[] uids = tagUid.split(SqlConstants.FILE_SEGMENTATION);
            List<Tag> tagList = new ArrayList<>();
            for (String uid : uids) {
                Tag tag = tagService.getById(uid);
                if (tag != null && tag.getStatus() != EStatus.DISABLED) {
                    tagList.add(tag);
                }
            }
            blog.setTagList(tagList);
        }
        return blog;
    }

    @Override
    public Blog setSortByBlog(Blog blog) {
        if (blog != null && !StringUtils.isEmpty(blog.getBlogSortUid())) {
            BlogSort blogSort = blogSortService.getById(blog.getBlogSortUid());
            blog.setBlogSort(blogSort);
        }
        return blog;
    }

    @Override
    public IPage<Blog> searchBlogByTag(String tagUid, Long currentPage, Long pageSize) {
        Tag tag = tagService.getById(tagUid);
        if (tag != null) {
            HttpServletRequest request = RequestHolder.getRequest();
            String ip = IpUtils.getIpAddr(request);
            // 从Redis取出数据，判断该用户24小时内，是否点击过该标签
            String jsonResult = redisTemplate.opsForValue().get(RedisConstants.TAG_CLICK
                    + RedisConstants.SEGMENTATION + ip + "#" + tagUid);
            if (StringUtils.isEmpty(jsonResult)) {
                // 给标签点击数增加
                int clickCount = tag.getClickCount() + 1;
                tag.setClickCount(clickCount);
                tag.updateById();
                // 将该用户点击记录存储到redis中, 24小时后过期
                redisTemplate.opsForValue().set(RedisConstants.TAG_CLICK + RedisConstants.SEGMENTATION
                        + ip + RedisConstants.WELL_NUMBER + tagUid, clickCount + "", 24, TimeUnit.HOURS);
            }
        }

        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);

        queryWrapper.like(SqlConstants.TagUid, tagUid);
        queryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SqlConstants.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.orderByDesc(SqlConstants.CREATE_TIME);
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SqlConstants.CONTENT));

        IPage<Blog> pageList = this.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = this.setTagAndSortAndPictureByBlogList(list);
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public IPage<Blog> getBlogPageByLevel(Page<Blog> page, Integer level, Integer useSort) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SqlConstants.LEVEL, level);
        queryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SqlConstants.IS_PUBLISH, EPublish.PUBLISH);

        if (useSort == 0) {
            queryWrapper.orderByDesc(SqlConstants.CREATE_TIME);
        } else {
            queryWrapper.orderByDesc(SqlConstants.SORT);
        }

        // 因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SqlConstants.CONTENT));
        return this.page(page, queryWrapper);
    }

    @Override
    public IPage<Blog> getBlogPageByLevel(Integer level, Long currentPage, Integer useSort) {
        // 从Redis中获取内容
        String jsonResult = redisTemplate.opsForValue().get(
                RedisConstants.BLOG_LEVEL + RedisConstants.SEGMENTATION + level);

        // 判断redis中是否有文章
        if (StringUtils.isNotEmpty(jsonResult)) {
            List list = JsonUtils.jsonArrayToArrayList(jsonResult);
            IPage pageList = new Page();
            pageList.setRecords(list);
            return pageList;
        }

        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        switch (level) {
            case ELevel.NORMAL:
                page.setSize(BLOG_NEW_COUNT);
                break;
            case ELevel.FIRST:
                page.setSize(BLOG_FIRST_COUNT);
                break;
            case ELevel.SECOND:
                page.setSize(BLOG_SECOND_COUNT);
                break;
            case ELevel.THIRD:
                page.setSize(BLOG_THIRD_COUNT);
                break;
            case ELevel.FOURTH:
                page.setSize(BLOG_FOURTH_COUNT);
                break;
            default:
                break;
        }

        IPage<Blog> pageList = this.getBlogPageByLevel(page, level, useSort);
        List<Blog> list = pageList.getRecords();

        // 一级推荐或者二级推荐没有内容时，自动把top5填充至一级推荐和二级推荐中
        if ((level == SqlConstants.ONE || level == SqlConstants.TWO) && list.size() == 0) {
            QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
            Page<Blog> hotPage = new Page<>();
            hotPage.setCurrent(1);
            hotPage.setSize(BLOG_HOT_COUNT);
            queryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
            queryWrapper.eq(SqlConstants.IS_PUBLISH, EPublish.PUBLISH);
            queryWrapper.orderByDesc(SqlConstants.CLICK_COUNT);
            queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SqlConstants.CONTENT));

            IPage<Blog> hotPageList = this.page(hotPage, queryWrapper);
            List<Blog> hotBlogList = hotPageList.getRecords();
            List<Blog> secondBlogList = new ArrayList<>();
            List<Blog> firstBlogList = new ArrayList<>();
            for (int a = 0; a < hotBlogList.size(); a++) {
                // 当推荐大于两个的时候
                if ((hotBlogList.size() - firstBlogList.size()) > BLOG_SECOND_COUNT) {
                    firstBlogList.add(hotBlogList.get(a));
                } else {
                    secondBlogList.add(hotBlogList.get(a));
                }
            }

            firstBlogList = setBlog(firstBlogList);
            secondBlogList = setBlog(secondBlogList);

            // 将从数据库查询的数据缓存到redis中，设置1小时后过期
            redisTemplate.opsForValue().set(RedisConstants.BLOG_LEVEL + RedisConstants.SEGMENTATION + SqlConstants.ONE,
                    JsonUtils.objectToJson(firstBlogList), 1, TimeUnit.HOURS);
            redisTemplate.opsForValue().set(RedisConstants.BLOG_LEVEL + RedisConstants.SEGMENTATION + SqlConstants.TWO,
                    JsonUtils.objectToJson(secondBlogList), 1, TimeUnit.HOURS);

            switch (level) {
                case SqlConstants.ONE:
                    pageList.setRecords(firstBlogList);
                    break;
                case SqlConstants.TWO:
                    pageList.setRecords(secondBlogList);
                    break;
                default:
                    break;
            }
            return pageList;
        }

        list = setBlog(list);
        pageList.setRecords(list);

        //将从数据库查询的数据缓存到redis中
        redisTemplate.opsForValue().set(RedisConstants.BLOG_LEVEL + RedisConstants.SEGMENTATION + level,
                JsonUtils.objectToJson(list), 1, TimeUnit.HOURS);
        return pageList;
    }

    @Override
    public IPage<Blog> getHotBlog() {
        //从Redis中获取内容
        String jsonResult = redisTemplate.opsForValue().get(RedisConstants.HOT_BLOG);
        //判断redis中是否有文章
        if (StringUtils.isNotEmpty(jsonResult)) {
            List list = JsonUtils.jsonArrayToArrayList(jsonResult);
            IPage pageList = new Page();
            pageList.setRecords(list);
            return pageList;
        }

        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        Page<Blog> page = new Page<>();
        page.setCurrent(0);
        page.setSize(BLOG_HOT_COUNT);
        queryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SqlConstants.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.orderByDesc(SqlConstants.CLICK_COUNT);

        //因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SqlConstants.CONTENT));
        IPage<Blog> pageList = this.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = setBlog(list);

        // 将从热门博客缓存到redis中
        if (list.size() > 0) {
            redisTemplate.opsForValue().set(RedisConstants.HOT_BLOG, JsonUtils.objectToJson(list), 1, TimeUnit.HOURS);
        }
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public IPage<Blog> getNewBlog(Long currentPage, Long pageSize) {
        // 只缓存第一页的内容
        if (currentPage == 1L) {
            //从Redis中获取内容
            String jsonResult = redisTemplate.opsForValue().get(RedisConstants.NEW_BLOG);

            //判断redis中是否有文章
            if (StringUtils.isNotEmpty(jsonResult)) {
                List list = JsonUtils.jsonArrayToArrayList(jsonResult);
                IPage pageList = new Page();
                pageList.setRecords(list);
                return pageList;
            }
        }

        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(BLOG_NEW_COUNT);
        queryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SqlConstants.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.orderByDesc(SqlConstants.CREATE_TIME);
        //因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SqlConstants.CONTENT));

        IPage<Blog> pageList = this.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        if (list.size() <= 0) {
            return pageList;
        }

        list = setBlog(list);
        // 将从最新博客缓存到redis中
        if (currentPage == 1L) {
            redisTemplate.opsForValue().set(RedisConstants.NEW_BLOG, JsonUtils.objectToJson(list), 1, TimeUnit.HOURS);
        }
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public IPage<Blog> getBlogByTime(Long currentPage, Long pageSize) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        queryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SqlConstants.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.orderByDesc(SqlConstants.CREATE_TIME);
        // 因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SqlConstants.CONTENT));

        IPage<Blog> pageList = this.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = setBlog(list);
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public List<Blog> getSameBlogByBlogUid(String blogUid) {
        Blog blog = this.getById(blogUid);
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
        Page<Blog> page = new Page<>();
        page.setCurrent(1);
        page.setSize(10);

        // 通过分类来获取相关博客
        String blogSortUid = blog.getBlogSortUid();
        queryWrapper.eq(SqlConstants.BLOG_SORT_UID, blogSortUid);
        queryWrapper.eq(SqlConstants.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.orderByDesc(SqlConstants.CREATE_TIME);

        IPage<Blog> pageList = this.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = this.setTagAndSortByBlogList(list);

        // 过滤掉当前的博客
        List<Blog> newList = new ArrayList<>();
        for (Blog item : list) {
            if (item.getUid().equals(blogUid)) {
                continue;
            }
            newList.add(item);
        }
        return newList;
    }

    @Override
    public String getBlogTimeSortList() {
        // 从Redis中获取内容
        String monthResult = redisTemplate.opsForValue().get(RedisConstants.MONTH_SET);
        // 判断redis中时候包含归档的内容
        if (StringUtils.isNotEmpty(monthResult)) {
            List list = JSON.parseArray(monthResult);
            return ResultUtils.result(SystemConstants.SUCCESS, list);
        }

        // 第一次启动的时候归档
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SqlConstants.CREATE_TIME);
        queryWrapper.eq(SqlConstants.IS_PUBLISH, EPublish.PUBLISH);
        // 因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SqlConstants.CONTENT));
        List<Blog> list = this.list(queryWrapper);

        // 给博客增加标签和分类
        list = this.setTagAndSortByBlogList(list);

        // key为归档日期，value为对应日期的博客列表
        Map<String, List<Blog>> map = new HashMap<>();
        Set<String> monthSet = new TreeSet<>();
        for (Blog blog : list) {
            Date createTime = blog.getCreateTime();
            String month = new SimpleDateFormat("yyyy年MM月").format(createTime);
            monthSet.add(month);

            if (!map.containsKey(month)) {
                map.put(month, new ArrayList<>());
            }
            map.get(month).add(blog);
        }

        // 缓存该月份下的所有文章  key: 月份   value：月份下的所有文章
        map.forEach((key, value) -> redisTemplate.opsForValue().set(
                RedisConstants.BLOG_SORT_BY_MONTH + RedisConstants.SEGMENTATION + key,
                JsonUtils.objectToJson(value)));

        // 将从数据库查询的数据缓存到redis中
        redisTemplate.opsForValue().set(RedisConstants.MONTH_SET,
                JsonUtils.objectToJson(monthSet));
        return ResultUtils.result(SystemConstants.SUCCESS, monthSet);
    }

    @Override
    public String getArticleByMonth(String monthDate) {
        if (StringUtils.isEmpty(monthDate)) {
            return ResultUtils.result(SystemConstants.ERROR, MessageConstants.PARAM_INCORRECT);
        }

        // 从Redis中获取内容
        String contentResult = redisTemplate.opsForValue().get(
                RedisConstants.BLOG_SORT_BY_MONTH + RedisConstants.SEGMENTATION + monthDate);
        // 判断redis中时候包含该日期下的文章
        if (StringUtils.isNotEmpty(contentResult)) {
            List list = JsonUtils.jsonArrayToArrayList(contentResult);
            return ResultUtils.result(SystemConstants.SUCCESS, list);
        }

        // 第一次启动的时候归档
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SqlConstants.CREATE_TIME);
        queryWrapper.eq(SqlConstants.IS_PUBLISH, EPublish.PUBLISH);
        // 因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SqlConstants.CONTENT));
        List<Blog> list = this.list(queryWrapper);

        // 给博客增加标签和分类
        list = this.setTagAndSortByBlogList(list);

        // key为归档日期，value为对应日期的博客列表
        Map<String, List<Blog>> map = new HashMap<>();
        Set<String> monthSet = new TreeSet<>();
        for (Blog blog : list) {
            Date createTime = blog.getCreateTime();
            String month = new SimpleDateFormat("yyyy年MM月").format(createTime);
            monthSet.add(month);

            if (!map.containsKey(month)) {
                map.put(month, new ArrayList<>());
            }
            map.get(month).add(blog);
        }

        // 缓存该月份下的所有文章  key: 月份   value：月份下的所有文章
        map.forEach((key, value) -> redisTemplate.opsForValue().set(
                RedisConstants.BLOG_SORT_BY_MONTH + RedisConstants.SEGMENTATION + key,
                JsonUtils.objectToJson(value)));
        // 将从数据库查询的数据缓存到redis中
        redisTemplate.opsForValue().set(RedisConstants.MONTH_SET, JsonUtils.objectToJson(monthSet));
        return ResultUtils.result(SystemConstants.SUCCESS, map.get(monthDate));
    }

    @Override
    public Integer getBlogPraiseCountByUid(String uid) {
        int pariseCount = 0;
        if (StringUtils.isEmpty(uid)) {
            log.error("传入的UID为空");
            return pariseCount;
        }
        //从Redis取出用户点赞数据
        String pariseJsonResult = redisTemplate.opsForValue().get(
                RedisConstants.BLOG_PRAISE + RedisConstants.SEGMENTATION + uid);
        if (!StringUtils.isEmpty(pariseJsonResult)) {
            pariseCount = Integer.parseInt(pariseJsonResult);
        }
        return pariseCount;
    }

    @Override
    public String praiseBlogByUid(String uid) {
        if (StringUtils.isEmpty(uid)) {
            return ResultUtils.result(SystemConstants.ERROR, MessageConstants.PARAM_INCORRECT);
        }

        HttpServletRequest request = RequestHolder.getRequest();
        // 如果用户登录了
        if (request.getAttribute(SqlConstants.USER_UID) != null) {
            String userUid = request.getAttribute(SqlConstants.USER_UID).toString();
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(SqlConstants.USER_UID, userUid);
            queryWrapper.eq(SqlConstants.BLOG_UID, uid);
            queryWrapper.eq(SqlConstants.TYPE, ECommentType.PRAISE);
            queryWrapper.last("LIMIT 1");
            Comment praise = commentService.getOne(queryWrapper);
            if (praise != null) {
                return ResultUtils.result(SystemConstants.ERROR, MessageConstants.YOU_HAVE_BEEN_PRISE);
            }
        } else {
            return ResultUtils.result(SystemConstants.ERROR, MessageConstants.PLEASE_LOGIN_TO_PRISE);
        }

        Blog blog = this.getById(uid);
        String pariseJsonResult = redisTemplate.opsForValue().get(RedisConstants.BLOG_PRAISE + RedisConstants.SEGMENTATION + uid);

        if (StringUtils.isEmpty(pariseJsonResult)) {
            //给该博客点赞数
            redisTemplate.opsForValue().set(RedisConstants.BLOG_PRAISE + RedisConstants.SEGMENTATION + uid, "1");
            blog.setCollectCount(1);
            blog.updateById();

        } else {
            Integer count = blog.getCollectCount() + 1;
            //给该博客点赞 +1
            redisTemplate.opsForValue().set(RedisConstants.BLOG_PRAISE + RedisConstants.SEGMENTATION + uid,
                    count.toString());
            blog.setCollectCount(count);
            blog.updateById();
        }

        // 已登录用户，向评论表添加点赞数据
        if (request.getAttribute(SqlConstants.USER_UID) != null) {
            String userUid = request.getAttribute(SqlConstants.USER_UID).toString();
            Comment comment = new Comment();
            comment.setUserUid(userUid);
            comment.setBlogUid(uid);
            comment.setSource(ECommentSource.BLOG_INFO.getCode());
            comment.setType(ECommentType.PRAISE);
            comment.insert();
        }
        return ResultUtils.result(SystemConstants.SUCCESS, blog.getCollectCount());
    }

    @Override
    public IPage<Blog> getSameBlogByTagUid(String tagUid) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        Page<Blog> page = new Page<>();
        page.setCurrent(1);
        page.setSize(10);

        queryWrapper.eq(SqlConstants.TAG_UID, tagUid);
        queryWrapper.orderByDesc(SqlConstants.CREATE_TIME);
        queryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SqlConstants.IS_PUBLISH, EPublish.PUBLISH);

        IPage<Blog> pageList = this.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = this.setTagAndSortByBlogList(list);
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public IPage<Blog> getListByBlogSortUid(String blogSortUid, Long currentPage, Long pageSize) {
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);

        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SqlConstants.CREATE_TIME);
        queryWrapper.eq(SqlConstants.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(SqlConstants.BLOG_SORT_UID, blogSortUid);

        // 因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SqlConstants.CONTENT));
        IPage<Blog> pageList = this.page(page, queryWrapper);

        // 给博客增加标签和分类
        List<Blog> list = this.setTagAndSortByBlogList(pageList.getRecords());
        pageList.setRecords(list);
        return pageList;
    }

    /**
     * 设置博客的分类标签和内容
     *
     * @param list
     * @return
     */
    private List<Blog> setBlog(List<Blog> list) {
        final StringBuffer fileUids = new StringBuffer();
        List<String> sortUids = new ArrayList<>();
        List<String> tagUids = new ArrayList<>();

        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUids.append(item.getFileUid() + SqlConstants.FILE_SEGMENTATION);
            }
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                sortUids.add(item.getBlogSortUid());
            }
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                tagUids.add(item.getTagUid());
            }
        });
        String pictureList = null;

        pictureList = this.pictureFeignClient.getPicture(fileUids.toString(), SqlConstants.FILE_SEGMENTATION);
        List<Map<String, Object>> picList = webUtils.getPictureMap(pictureList);
        Collection<BlogSort> sortList = new ArrayList<>();
        Collection<Tag> tagList = new ArrayList<>();
        if (sortUids.size() > 0) {
            sortList = blogSortService.listByIds(sortUids);
        }
        if (tagUids.size() > 0) {
            tagList = tagService.listByIds(tagUids);
        }


        Map<String, BlogSort> sortMap = new HashMap<>();
        Map<String, Tag> tagMap = new HashMap<>();
        Map<String, String> pictureMap = new HashMap<>();

        sortList.forEach(item -> sortMap.put(item.getUid(), item));
        tagList.forEach(item -> tagMap.put(item.getUid(), item));
        picList.forEach(item -> pictureMap.put(item.get(SqlConstants.UID).toString(), item.get(SqlConstants.URL).toString()));


        for (Blog item : list) {
            //设置分类
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                item.setBlogSort(sortMap.get(item.getBlogSortUid()));
            }
            //获取标签
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                List<String> tagUidsTemp = StringUtils.changeStringToString(item.getTagUid(), SqlConstants.FILE_SEGMENTATION);
                List<Tag> tagListTemp = new ArrayList<>();

                tagUidsTemp.forEach(tag -> {
                    tagListTemp.add(tagMap.get(tag));
                });
                item.setTagList(tagListTemp);
            }
            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidsTemp = StringUtils.changeStringToString(item.getFileUid(), SqlConstants.FILE_SEGMENTATION);
                List<String> pictureListTemp = new ArrayList<>();

                pictureUidsTemp.forEach(picture -> pictureListTemp.add(pictureMap.get(picture)));
                item.setPhotoList(pictureListTemp);
            }
        }
        return list;
    }
}
