package com.ginkgoblog.web.controller;

import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.base.constants.SystemConstants;
import com.ginkgoblog.base.enums.EBehavior;
import com.ginkgoblog.commons.service.AdminService;
import com.ginkgoblog.commons.service.WebConfigService;
import com.ginkgoblog.utils.ResultUtils;
import com.ginkgoblog.web.log.OperationLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 关于我 Controller 层
 *
 * @author maomao
 * @date 2021-01-25
 */
@RestController
@RequestMapping("/about")
@Api("关于我相关接口")
@Slf4j
public class AboutMeController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private WebConfigService webConfigService;

    @OperationLog(value = "关于我", behavior = EBehavior.VISIT_PAGE)
    @ApiOperation("关于我")
    @GetMapping("/getMe")
    public String getMe() {
        log.info("获取关于我的信息");
        return ResultUtils.result(SystemConstants.SUCCESS, adminService.getAdminByUser(SqlConstants.ADMIN));
    }

    @ApiOperation("获取联系方式")
    @GetMapping("/getContact")
    public String getContact() {
        log.info("获取联系方式");
        return ResultUtils.result(SystemConstants.SUCCESS, webConfigService.getWebConfigByShowList());
    }
}
