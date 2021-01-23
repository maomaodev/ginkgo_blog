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
import com.ginkgoblog.commons.entity.Tag;
import com.ginkgoblog.commons.mapper.BlogMapper;
import com.ginkgoblog.commons.service.BlogService;
import com.ginkgoblog.commons.service.TagService;
import com.ginkgoblog.utils.IpUtils;
import com.ginkgoblog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
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
    TagService tagService;
    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    public List<Blog> setTagAndSortAndPictureByBlogList(List<Blog> list) {
        return null;
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
