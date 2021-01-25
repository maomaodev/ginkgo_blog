package com.ginkgoblog.admin.controller;

import com.ginkgoblog.commons.entity.Admin;
import com.ginkgoblog.commons.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author maomao
 * @date 2021-01-13
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@Api("管理员相关接口")
public class AdminController {

    @Autowired
    private AdminService adminService;

}
