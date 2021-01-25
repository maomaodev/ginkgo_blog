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
import com.ginkgoblog.base.enums.PublishEnum;
import com.ginkgoblog.base.enums.StatusEnum;
import com.ginkgoblog.base.holder.RequestHolder;
import com.ginkgoblog.commons.entity.Blog;
import com.ginkgoblog.commons.entity.BlogSort;
import com.ginkgoblog.commons.entity.Tag;
import com.ginkgoblog.commons.feign.PictureFeignClient;
import com.ginkgoblog.commons.mapper.BlogMapper;
import com.ginkgoblog.commons.service.BlogService;
import com.ginkgoblog.commons.service.BlogSortService;
import com.ginkgoblog.commons.service.TagService;
import com.ginkgoblog.commons.utils.WebUtils;
import com.ginkgoblog.utils.IpUtils;
import com.ginkgoblog.utils.JsonUtils;
import com.ginkgoblog.utils.ResultUtils;
import com.ginkgoblog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    private TagService tagService;
    @Autowired
    private BlogSortService blogSortService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private PictureFeignClient pictureFeignClient;


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
        queryWrapper.eq(SqlConstants.STATUS, StatusEnum.ENABLE);
        queryWrapper.eq(SqlConstants.IS_PUBLISH, PublishEnum.PUBLISH);
        queryWrapper.orderByDesc(SqlConstants.CREATE_TIME);
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SqlConstants.CONTENT));

        IPage<Blog> pageList = this.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = this.setTagAndSortAndPictureByBlogList(list);
        pageList.setRecords(list);
        return pageList;
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
        queryWrapper.eq(SqlConstants.STATUS, StatusEnum.ENABLE);
        queryWrapper.orderByDesc(SqlConstants.CREATE_TIME);
        queryWrapper.eq(SqlConstants.IS_PUBLISH, PublishEnum.PUBLISH);
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
        queryWrapper.eq(SqlConstants.STATUS, StatusEnum.ENABLE);
        queryWrapper.orderByDesc(SqlConstants.CREATE_TIME);
        queryWrapper.eq(SqlConstants.IS_PUBLISH, PublishEnum.PUBLISH);
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
}
