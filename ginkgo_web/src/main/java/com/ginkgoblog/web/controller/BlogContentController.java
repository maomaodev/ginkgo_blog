package com.ginkgoblog.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ginkgoblog.base.constants.CodeConstants;
import com.ginkgoblog.base.constants.SystemConstants;
import com.ginkgoblog.base.enums.BehaviorEnum;
import com.ginkgoblog.base.enums.PublishEnum;
import com.ginkgoblog.base.enums.StatusEnum;
import com.ginkgoblog.base.holder.RequestHolder;
import com.ginkgoblog.commons.entity.Blog;
import com.ginkgoblog.commons.feign.PictureFeignClient;
import com.ginkgoblog.commons.service.BlogService;
import com.ginkgoblog.commons.utils.WebUtils;
import com.ginkgoblog.utils.IpUtils;
import com.ginkgoblog.utils.ResultUtils;
import com.ginkgoblog.utils.StringUtils;
import com.ginkgoblog.web.log.OperationLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 文章详情 Controller 层
 *
 * @author maomao
 * @date 2021-01-26
 */
@RestController
@RequestMapping("/content")
@Api("文章详情相关接口")
@Slf4j
public class BlogContentController {

    @Autowired
    private BlogService blogService;
    @Autowired
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private WebUtils webUtils;
    @Value(value = "${BLOG.ORIGINAL_TEMPLATE}")
    private String ORIGINAL_TEMPLATE;
    @Value(value = "${BLOG.REPRINTED_TEMPLATE}")
    private String REPRINTED_TEMPLATE;

    @OperationLog(value = "点击博客", behavior = BehaviorEnum.BLOG_CONTENT)
    @ApiOperation(value = "通过Uid获取博客内容", notes = "通过Uid获取博客内容")
    @GetMapping("/getBlogByUid")
    public String getBlogByUid(@ApiParam(name = "uid", value = "博客UID", required = false) @RequestParam(name = "uid", required = false) String uid) {
        if (StringUtils.isEmpty(uid)) {
            return ResultUtils.result(SystemConstants.ERROR, "UID不能为空");
        }

        HttpServletRequest request = RequestHolder.getRequest();
        String ip = IpUtils.getIpAddr(request);
        Blog blog = blogService.getById(uid);
        if (blog == null || blog.getStatus() == StatusEnum.DISABLED
                || blog.getIsPublish().equals(PublishEnum.NO_PUBLISH)) {
            return ResultUtils.result(CodeConstants.ERROR, "该文章已下架或被删除");
        }

        // 设置文章版权申明
        setBlogCopyright(blog);
        // 设置博客标签
        blogService.setTagByBlog(blog);
        // 获取分类
        blogService.setSortByBlog(blog);
        // 设置博客标题图
        setPhotoListByBlog(blog);

        // 从Redis取出数据，判断该用户是否点击过
        String jsonResult = stringRedisTemplate.opsForValue().get("BLOG_CLICK:" + ip + "#" + uid);
        if (StringUtils.isEmpty(jsonResult)) {
            // 给博客点击数增加
            Integer clickCount = blog.getClickCount() + 1;
            blog.setClickCount(clickCount);
            blog.updateById();
            // 将该用户点击记录存储到redis中, 24小时后过期，即在这个时间内同一用户点击同一博客不再增加点击数
            stringRedisTemplate.opsForValue().set("BLOG_CLICK:" + ip + "#" + uid,
                    blog.getClickCount().toString(), 24, TimeUnit.HOURS);
        }
        return ResultUtils.result(SystemConstants.SUCCESS, blog);
    }

    @ApiOperation("通过Uid获取博客点赞数")
    @GetMapping("/getBlogPraiseCountByUid")
    public String getBlogPraiseCountByUid(@ApiParam(name = "uid", value = "博客UID")
                                          @RequestParam(name = "uid", required = false) String uid) {

        return ResultUtils.result(SystemConstants.SUCCESS, blogService.getBlogPraiseCountByUid(uid));
    }

    @OperationLog(value = "通过Uid给博客点赞", behavior = BehaviorEnum.BLOG_PRAISE)
    @ApiOperation("通过Uid给博客点赞")
    @GetMapping("/praiseBlogByUid")
    public String praiseBlogByUid(@ApiParam(name = "uid", value = "博客UID") @RequestParam(name = "uid", required = false) String uid) {

        return blogService.praiseBlogByUid(uid);
    }

    @ApiOperation("根据标签Uid获取相关的博客")
    @GetMapping("/getSameBlogByTagUid")
    public String getSameBlogByTagUid(@ApiParam(name = "tagUid", value = "博客标签UID", required = true) @RequestParam(name = "tagUid", required = true) String tagUid,
                                      @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                      @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(tagUid)) {
            return ResultUtils.result(SystemConstants.ERROR, "标签UID不能为空");
        }
        return ResultUtils.result(SystemConstants.SUCCESS, blogService.getSameBlogByTagUid(tagUid));
    }

    @ApiOperation("根据BlogUid获取相关的博客")
    @GetMapping("/getSameBlogByBlogUid")
    public String getSameBlogByBlogUid(@ApiParam(name = "blogUid", value = "博客标签UID", required = true) @RequestParam(name = "blogUid", required = true) String blogUid) {
        if (StringUtils.isEmpty(blogUid)) {
            return ResultUtils.result(SystemConstants.ERROR, "博客UID不能为空");
        }
        // 获取同博客分类下的博客
        List<Blog> blogList = blogService.getSameBlogByBlogUid(blogUid);
        IPage<Blog> pageList = new Page<>();
        pageList.setRecords(blogList);
        return ResultUtils.result(SystemConstants.SUCCESS, pageList);
    }

    /**
     * 设置博客标题图
     *
     * @param blog
     */
    private void setPhotoListByBlog(Blog blog) {
        if (blog != null && !StringUtils.isEmpty(blog.getFileUid())) {
            String result = pictureFeignClient.getPicture(blog.getFileUid(), ",");
            List<String> picList = webUtils.getPicture(result);
            log.info("##### picList: #######" + picList);
            if (picList != null && picList.size() > 0) {
                blog.setPhotoList(picList);
            }
        }
    }

    /**
     * 设置博客版权
     *
     * @param blog
     */
    private void setBlogCopyright(Blog blog) {
        if ("1".equals(blog.getIsOriginal())) {
            // 如果是原创的话
            blog.setCopyright(ORIGINAL_TEMPLATE);
        } else {
            String reprintedTemplate = REPRINTED_TEMPLATE;
            String[] variable = {blog.getArticlesPart(), blog.getAuthor()};
            String str = String.format(reprintedTemplate, variable);
            blog.setCopyright(str);
        }
    }
}
