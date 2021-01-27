package com.ginkgoblog.web.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ginkgoblog.base.constants.MessageConstants;
import com.ginkgoblog.base.constants.RedisConstants;
import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.base.constants.SystemConstants;
import com.ginkgoblog.base.enums.EStatus;
import com.ginkgoblog.commons.entity.SysConfig;
import com.ginkgoblog.commons.entity.User;
import com.ginkgoblog.commons.service.SysConfigService;
import com.ginkgoblog.utils.ResultUtils;
import com.ginkgoblog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 第三方登录认证 Controller 层
 *
 * @author maomao
 * @date 2021-01-23
 */
@RestController
@RequestMapping("/oauth")
@Api("第三方登录相关接口")
@Slf4j
public class AuthController {






    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("删除accessToken")
    @RequestMapping("/delete/{accessToken}")
    public String deleteUserAccessToken(@PathVariable("accessToken") String accessToken) {
        stringRedisTemplate.delete(RedisConstants.USER_TOEKN + RedisConstants.SEGMENTATION + accessToken);
        return ResultUtils.result(SystemConstants.SUCCESS, MessageConstants.DELETE_SUCCESS);
    }

    /**
     * 通过token获取七牛云配置
     *
     * @param token
     * @return
     */
    @GetMapping("/getSystemConfig")
    public String getSystemConfig(@RequestParam("token") String token) {
        // 首先验证用户token，若未通过则直接返回错误
        String userInfo = stringRedisTemplate.opsForValue().get(
                RedisConstants.USER_TOEKN + RedisConstants.SEGMENTATION + token);
        if (StringUtils.isEmpty(userInfo)) {
            return ResultUtils.result(SystemConstants.ERROR, MessageConstants.INVALID_TOKEN);
        }
        // 查询创建时间最新的一条系统配置
        QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(SqlConstants.CREATE_TIME);
        queryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
        queryWrapper.last("LIMIT 1");
        SysConfig SystemConfig = sysConfigService.getOne(queryWrapper);
        return ResultUtils.result(SystemConstants.SUCCESS, SystemConfig);
    }










    @ApiOperation(value = "绑定用户邮箱", notes = "绑定用户邮箱")
    @GetMapping("/bindUserEmail/{token}/{code}")
    public String bindUserEmail(@PathVariable("token") String token, @PathVariable("code") String code) {
        // 验证token
        String userInfo = stringRedisTemplate.opsForValue().get(
                RedisConstants.USER_TOEKN + RedisConstants.SEGMENTATION + token);
        if (StringUtils.isEmpty(userInfo)) {
            return ResultUtils.result(SystemConstants.ERROR, MessageConstants.INVALID_TOKEN);
        }
        User user = JSON.parseObject(userInfo, User.class);
        user.updateById();
        return ResultUtils.result(SystemConstants.SUCCESS, MessageConstants.OPERATION_SUCCESS);
    }
}
