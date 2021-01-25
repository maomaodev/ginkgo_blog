package com.ginkgoblog.web.controller;

import com.ginkgoblog.base.enums.BehaviorEnum;
import com.ginkgoblog.commons.service.BlogService;
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

/**
 * 博客归档 Controller 层
 *
 * @author maomao
 * @date 2021-01-25
 */
@RestController
@RequestMapping("/sort")
@Api("博客归档相关接口")
@Slf4j
public class SortController {
    @Autowired
    BlogService blogService;

    @ApiOperation("归档")
    @GetMapping("/getSortList")
    public String getSortList() {
        log.info("获取归档日期");
        return blogService.getBlogTimeSortList();
    }

    @OperationLog(value = "点击归档", behavior = BehaviorEnum.VISIT_SORT)
    @ApiOperation("通过月份获取文章")
    @GetMapping("/getArticleByMonth")
    public String getArticleByMonth(@ApiParam(name = "monthDate", value = "归档的日期")
                                    @RequestParam(name = "monthDate", required = false) String monthDate) {
        log.info("通过月份获取文章列表");
        return blogService.getArticleByMonth(monthDate);
    }
}
