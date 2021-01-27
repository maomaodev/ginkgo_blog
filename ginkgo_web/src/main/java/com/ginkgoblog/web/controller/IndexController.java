package com.ginkgoblog.web.controller;

import com.ginkgoblog.base.constants.SystemConstants;
import com.ginkgoblog.base.enums.EBehavior;
import com.ginkgoblog.commons.service.BlogService;
import com.ginkgoblog.commons.service.LinkService;
import com.ginkgoblog.commons.service.TagService;
import com.ginkgoblog.commons.service.WebConfigService;
import com.ginkgoblog.utils.ResultUtils;
import com.ginkgoblog.web.log.OperationLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页 Controller 层
 *
 * @author maomao
 * @date 2021-01-26
 */
@RestController
@RequestMapping("/index")
@Api("首页相关接口")
@Slf4j
public class IndexController {

    @Autowired
    private TagService tagService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private WebConfigService webConfigService;

    @Value(value = "${BLOG.HOT_TAG_COUNT}")
    private Integer BLOG_HOT_TAG_COUNT;
    @Value(value = "${BLOG.FRIENDLY_LINK_COUNT}")
    private Integer FRIENDLY_LINK_COUNT;
    @Value(value = "${BLOG.NEW_COUNT}")
    private Long BLOG_NEW_COUNT;

    @ApiOperation("通过推荐等级获取博客列表")
    @GetMapping("/getBlogByLevel")
    public String getBlogByLevel(
            @ApiParam(name = "level", value = "推荐等级", required = false) @RequestParam(name = "level", required = false, defaultValue = "0") Integer level,
            @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
            @ApiParam(name = "useSort", value = "使用排序", required = false) @RequestParam(name = "useSort", required = false, defaultValue = "0") Integer useSort) {

        return ResultUtils.result(SystemConstants.SUCCESS, blogService.getBlogPageByLevel(level, currentPage, useSort));
    }

    @ApiOperation("获取首页排行博客")
    @GetMapping("/getHotBlog")
    public String getHotBlog() {

        log.info("获取首页排行博客");
        return ResultUtils.result(SystemConstants.SUCCESS, blogService.getHotBlog());
    }

    @ApiOperation("获取首页最新的博客")
    @GetMapping("/getNewBlog")
    public String getNewBlog(
            @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
            @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        log.info("获取首页最新的博客");
        return ResultUtils.result(SystemConstants.SUCCESS, blogService.getNewBlog(currentPage, BLOG_NEW_COUNT));
    }


    @ApiOperation("按时间戳获取博客")
    @GetMapping("/getBlogByTime")
    public String getBlogByTime(
            @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
            @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        log.info("按时间戳获取博客");
        return ResultUtils.result(SystemConstants.SUCCESS, blogService.getBlogByTime(currentPage, BLOG_NEW_COUNT));
    }

    @ApiOperation("获取最热标签")
    @GetMapping("/getHotTag")
    public String getHotTag() {

        log.info("获取最热标签");
        return ResultUtils.result(SystemConstants.SUCCESS, tagService.getHotTag(BLOG_HOT_TAG_COUNT));
    }

    @ApiOperation(value = "获取友情链接", notes = "获取友情链接")
    @GetMapping("/getLink")
    public String getLink() {

        log.info("获取友情链接");
        return ResultUtils.result(SystemConstants.SUCCESS, linkService.getListByPageSize(FRIENDLY_LINK_COUNT));
    }

    @OperationLog(value = "点击友情链接", behavior = EBehavior.FRIENDSHIP_LINK)
    @ApiOperation("增加友情链接点击数")
    @GetMapping("/addLinkCount")
    public String addLinkCount(@ApiParam(name = "uid", value = "友情链接UID", required = false) @RequestParam(name = "uid", required = false) String uid) {

        log.info("点击友链");
        return linkService.addLinkCount(uid);
    }

    @ApiOperation("获取网站配置")
    @GetMapping("/getWebConfig")
    public String getWebConfig() {

        log.info("获取网站配置");
        return ResultUtils.result(SystemConstants.SUCCESS, webConfigService.getWebConfigByShowList());
    }
}
