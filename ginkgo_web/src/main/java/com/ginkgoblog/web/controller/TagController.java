package com.ginkgoblog.web.controller;

import com.ginkgoblog.base.constants.SystemConstants;
import com.ginkgoblog.commons.service.BlogService;
import com.ginkgoblog.commons.service.TagService;
import com.ginkgoblog.utils.ResultUtils;
import com.ginkgoblog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 标签 Controller 层
 *
 * @author maomao
 * @date 2021-01-23
 */
@RestController
@RequestMapping("/tag")
@Api("博客标签相关接口")
@Slf4j
public class TagController {
    @Autowired
    TagService tagService;
    @Autowired
    BlogService blogService;

    @ApiOperation("获取标签的信息")
    @GetMapping("/getTagList")
    public String getTagList() {
        log.info("获取标签信息");
        return ResultUtils.result(SystemConstants.SUCCESS, tagService.getList());
    }

    @ApiOperation("通过TagUid获取文章")
    @GetMapping("/getArticleByTagUid")
    public String getArticleByTagUid(
            @ApiParam(name = "tagUid", value = "标签UID") @RequestParam(name = "tagUid", required = false) String tagUid,
            @ApiParam(name = "currentPage", value = "当前页数") @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
            @ApiParam(name = "pageSize", value = "每页显示数目") @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        if (StringUtils.isEmpty(tagUid)) {
            return ResultUtils.result(SystemConstants.ERROR, "传入TagUid不能为空");
        }
        log.info("通过blogSortUid获取文章列表");
        return ResultUtils.result(SystemConstants.SUCCESS, blogService.searchBlogByTag(tagUid, currentPage, pageSize));
    }
}
