package com.ginkgoblog.admin.controller;

import com.ginkgoblog.admin.log.OperationLogger;
import com.ginkgoblog.commons.service.LinkService;
import com.ginkgoblog.commons.vo.LinkVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 友链表 Controller 层
 *
 * @author maomao
 * @date 2021-01-27
 */
@RestController
@Api(value = "友情链接相关接口", tags = {"友情链接相关接口"})
@RequestMapping("/link")
@Slf4j
public class LinkController {
    @Autowired
    private LinkService linkService;

}
