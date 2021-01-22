package com.ginkgoblog.web.controller;

import com.ginkgoblog.base.constants.MessageConstants;
import com.ginkgoblog.base.constants.SystemConstants;
import com.ginkgoblog.commons.service.UserService;
import com.ginkgoblog.commons.vo.UserVO;
import com.ginkgoblog.utils.ResultUtils;
import com.ginkgoblog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户登录管理相关接口
 *
 * @author maomao
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/login")
@Api("用户登录管理相关接口")
@Slf4j
public class LoginController {

    @Autowired
    private UserService userService;


    @ApiOperation("用户注册")
    @PostMapping("/register")
    public String register(@RequestBody UserVO userVO){
        if(StringUtils.isBlank(userVO.getUserName()) || StringUtils.isBlank(userVO.getPassWord())){
            return ResultUtils.result(SystemConstants.ERROR, MessageConstants.PARAM_INCORRECT);
        }

        return ResultUtils.result(SystemConstants.SUCCESS, "注册成功，请登录邮箱进行账号激活");
    }

}
