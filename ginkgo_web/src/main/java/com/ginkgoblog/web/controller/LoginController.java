package com.ginkgoblog.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ginkgoblog.base.enums.EStatus;
import com.ginkgoblog.base.holder.RequestHolder;
import com.ginkgoblog.commons.entity.User;
import com.ginkgoblog.commons.feign.PictureFeignClient;
import com.ginkgoblog.commons.service.UserService;
import com.ginkgoblog.commons.utils.WebUtil;
import com.ginkgoblog.commons.vo.UserVO;
import com.ginkgoblog.utils.*;
import com.ginkgoblog.web.constants.MessageConf;
import com.ginkgoblog.web.constants.RedisConf;
import com.ginkgoblog.web.constants.SQLConf;
import com.ginkgoblog.web.constants.SysConf;
import com.ginkgoblog.web.utils.RabbitMqUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户登录 Controller 层
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
    @Autowired
    private RabbitMqUtil rabbitMqUtil;
    @Autowired
    private WebUtil webUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private PictureFeignClient pictureFeignClient;
    @Value(value = "${BLOG.USER_TOKEN_SURVIVAL_TIME}")
    private Long userTokenSurvivalTime;

    @ApiOperation(value = "用户登录", notes = "用户登录")
    @PostMapping("/login")
    public String login(@RequestBody UserVO userVO) {
        String userName = userVO.getUserName();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq(SQLConf.USER_NAME, userName).or().eq(SQLConf.EMAIL, userName));
        queryWrapper.last("limit 1");
        User user = userService.getOne(queryWrapper);
        if (user == null || EStatus.DISABLED == user.getStatus()) {
            return ResultUtil.result(SysConf.ERROR, "用户不存在");
        }

        if (EStatus.FREEZE == user.getStatus()) {
            return ResultUtil.result(SysConf.ERROR, "用户账号未激活");
        }

        if (StringUtils.isNotEmpty(user.getPassWord()) && user.getPassWord().equals(MD5Utils.string2MD5(userVO.getPassWord()))) {
            // 更新登录信息
            HttpServletRequest request = RequestHolder.getRequest();
            String ip = IpUtils.getIpAddr(request);
            Map<String, String> userMap = IpUtils.getOsAndBrowserInfo(request);
            user.setBrowser(userMap.get(SysConf.BROWSER));
            user.setOs(userMap.get(SysConf.OS));
            user.setLastLoginIp(ip);
            user.setLastLoginTime(new Date());
            user.updateById();
            // 获取用户头像
            if (!StringUtils.isEmpty(user.getAvatar())) {
                String avatarResult = pictureFeignClient.getPicture(user.getAvatar(), ",");
                List<String> picList = webUtil.getPicture(avatarResult);
                if (picList != null && picList.size() > 0) {
                    user.setPhotoUrl(webUtil.getPicture(avatarResult).get(0));
                }
            }
            // 生成token
            String token = StringUtils.getUUID();

            // 过滤密码
            user.setPassWord("");
            //将从数据库查询的数据缓存到redis中
            redisUtil.setEx(SysConf.USER_TOEKN + SysConf.REDIS_SEGMENTATION + token, JsonUtils.objectToJson(user), userTokenSurvivalTime, TimeUnit.HOURS);

            return ResultUtil.result(SysConf.SUCCESS, token);
        } else {
            return ResultUtil.result(SysConf.ERROR, "账号或密码错误");
        }
    }

    @ApiOperation(value = "用户注册", notes = "用户注册")
    @PostMapping("/register")
    public String register(@RequestBody UserVO userVO) {
        if(userVO.getUserName().length() < 5 || userVO.getUserName().length() >= 20 || userVO.getPassWord().length() < 5 || userVO.getPassWord().length() >= 20) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.PARAM_INCORRECT);
        }
        HttpServletRequest request = RequestHolder.getRequest();
        String ip = IpUtils.getIpAddr(request);
        Map<String, String> map = IpUtils.getOsAndBrowserInfo(request);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq(SQLConf.USER_NAME, userVO.getUserName()).or().eq(SQLConf.EMAIL, userVO.getEmail()));
        queryWrapper.eq(SysConf.STATUS, EStatus.ENABLE);
        User user = userService.getOne(queryWrapper);
        if (user != null) {
            return ResultUtil.result(SysConf.ERROR, "用户已存在");
        }
        user = new User();
        user.setUserName(userVO.getUserName());
        user.setNickName(userVO.getNickName());
        user.setPassWord(MD5Utils.string2MD5(userVO.getPassWord()));
        user.setEmail(userVO.getEmail());
        // 设置账号来源，蘑菇博客
        user.setSource("MOGU");
        user.setLastLoginIp(ip);
        user.setBrowser(map.get(SysConf.BROWSER));
        user.setOs(map.get(SysConf.OS));
        user.setStatus(EStatus.FREEZE);
        user.insert();

        // 生成随机激活的token
        String token = StringUtils.getUUID();

        // 过滤密码
        user.setPassWord("");

        //将从数据库查询的数据缓存到redis中，用于用户邮箱激活，1小时后过期
        redisUtil.setEx(RedisConf.ACTIVATE_USER + RedisConf.SEGMENTATION + token, JsonUtils.objectToJson(user), 1, TimeUnit.HOURS);

        // 发送邮件，进行账号激活
        rabbitMqUtil.sendActivateEmail(user, token);

        return ResultUtil.result(SysConf.SUCCESS, "注册成功，请登录邮箱进行账号激活");
    }

    @ApiOperation(value = "激活用户账号", notes = "激活用户账号")
    @GetMapping("/activeUser/{token}")
    public String bindUserEmail(@PathVariable("token") String token) {
        // 从redis中获取用户信息
        String userInfo = redisUtil.get(RedisConf.ACTIVATE_USER + RedisConf.SEGMENTATION + token);
        if (StringUtils.isEmpty(userInfo)) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.INVALID_TOKEN);
        }
        User user = JsonUtils.jsonToPojo(userInfo, User.class);
        if (EStatus.FREEZE != user.getStatus()) {
            return ResultUtil.result(SysConf.ERROR, "用户账号已经被激活");
        }
        user.setStatus(EStatus.ENABLE);
        user.updateById();
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.OPERATION_SUCCESS);
    }

    @ApiOperation(value = "退出登录", notes = "退出登录", response = String.class)
    @PostMapping(value = "/logout")
    public String logout(@ApiParam(name = "token", value = "token令牌", required = false) @RequestParam(name = "token", required = false) String token) {
        if (StringUtils.isEmpty(token)) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.OPERATION_FAIL);
        }
        redisUtil.set(SysConf.USER_TOEKN + SysConf.REDIS_SEGMENTATION + token, "");
        return ResultUtil.result(SysConf.SUCCESS, "退出成功");
    }
}
