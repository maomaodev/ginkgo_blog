package com.ginkgoblog.web.controller;

import com.ginkgoblog.base.enums.EBehavior;
import com.ginkgoblog.commons.service.BlogService;
import com.ginkgoblog.commons.service.BlogSortService;
import com.ginkgoblog.utils.ResultUtil;
import com.ginkgoblog.utils.StringUtils;
import com.ginkgoblog.web.constants.SysConf;
import com.ginkgoblog.web.log.BussinessLog;
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
@Api(value = "分类相关接口", tags = {"分类相关接口"})
@Slf4j
public class ClassifyController {

    @Autowired
    private BlogService blogService;
    @Autowired
    private BlogSortService blogSortService;

    @ApiOperation(value = "获取分类的信息", notes = "获取分类的信息")
    @GetMapping("/getBlogSortList")
    public String getBlogSortList() {
        log.info("获取分类信息");
        return ResultUtil.result(SysConf.SUCCESS, blogSortService.getList());
    }

    @BussinessLog(value = "点击分类", behavior = EBehavior.VISIT_CLASSIFY)
    @ApiOperation(value = "通过blogSortUid获取文章", notes = "通过blogSortUid获取文章")
    @GetMapping("/getArticleByBlogSortUid")
    public String getArticleByBlogSortUid(HttpServletRequest request,
                                          @ApiParam(name = "blogSortUid", value = "分类UID", required = false) @RequestParam(name = "blogSortUid", required = false) String blogSortUid,
                                          @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                          @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        if (StringUtils.isEmpty(blogSortUid)) {
            log.info("点击分类,传入BlogSortUid不能为空");
            return ResultUtil.result(SysConf.ERROR, "传入BlogSortUid不能为空");
        }
        log.info("通过blogSortUid获取文章列表");
        return ResultUtil.result(SysConf.SUCCESS, blogService.getListByBlogSortUid(blogSortUid, currentPage, pageSize));
    }
}
