package com.ginkgoblog.web.controller;

import com.ginkgoblog.base.constants.SystemConstants;
import com.ginkgoblog.base.enums.BehaviorEnum;
import com.ginkgoblog.commons.service.BlogService;
import com.ginkgoblog.commons.service.BlogSortService;
import com.ginkgoblog.utils.ResultUtils;
import com.ginkgoblog.utils.StringUtils;
import com.ginkgoblog.web.log.OperationLog;
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
 * 用户登录 Controller 层
 *
 * @author maomao
 * @date 2021-01-26
 */
@RestController
@RequestMapping("/classify")
@Api("分类相关接口")
@Slf4j
public class ClassifyController {

    @Autowired
    private BlogService blogService;
    @Autowired
    private BlogSortService blogSortService;

    @ApiOperation("获取分类的信息")
    @GetMapping("/getBlogSortList")
    public String getBlogSortList() {
        log.info("获取分类信息");
        return ResultUtils.result(SystemConstants.SUCCESS, blogSortService.getList());
    }

    @OperationLog(value = "点击分类", behavior = BehaviorEnum.VISIT_CLASSIFY)
    @ApiOperation("通过blogSortUid获取文章")
    @GetMapping("/getArticleByBlogSortUid")
    public String getArticleByBlogSortUid(
            @ApiParam(name = "blogSortUid", value = "分类UID", required = false) @RequestParam(name = "blogSortUid", required = false) String blogSortUid,
            @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
            @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        if (StringUtils.isEmpty(blogSortUid)) {
            log.info("点击分类,传入BlogSortUid不能为空");
            return ResultUtils.result(SystemConstants.ERROR, "传入BlogSortUid不能为空");
        }
        log.info("通过blogSortUid获取文章列表");
        return ResultUtils.result(SystemConstants.SUCCESS, blogService.getListByBlogSortUid(blogSortUid, currentPage, pageSize));
    }
}
