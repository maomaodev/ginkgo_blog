package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.base.constants.RedisConstants;
import com.ginkgoblog.base.constants.SqlConstants;
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
import com.ginkgoblog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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
}
