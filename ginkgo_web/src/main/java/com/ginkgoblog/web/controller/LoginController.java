package com.ginkgoblog.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ginkgoblog.base.constants.MessageConstants;
import com.ginkgoblog.base.constants.RedisConstants;
import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.base.constants.SystemConstants;
import com.ginkgoblog.base.enums.StatusEnum;
import com.ginkgoblog.base.holder.RequestHolder;
import com.ginkgoblog.commons.entity.User;
import com.ginkgoblog.commons.feign.PictureFeignClient;
import com.ginkgoblog.commons.service.UserService;
import com.ginkgoblog.commons.utils.WebUtils;
import com.ginkgoblog.commons.vo.UserVO;
import com.ginkgoblog.utils.IpUtils;
import com.ginkgoblog.utils.JsonUtils;
import com.ginkgoblog.utils.ResultUtils;
import com.ginkgoblog.utils.StringUtils;
import com.ginkgoblog.web.utils.RabbitMqUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
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
    private RabbitMqUtils rabbitMqUtils;
    @Autowired
    private WebUtils webUtils;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private PictureFeignClient pictureFeignClient;
    @Value(value = "${BLOG.USER_TOKEN_SURVIVAL_TIME}")
    private Long userTokenSurvivalTime;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public String login(@RequestBody UserVO userVO) {
        String userName = userVO.getUserName();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq(SqlConstants.USER_NAME, userName)
                .or().eq(SqlConstants.EMAIL, userName));
        queryWrapper.last("limit 1");
        User user = userService.getOne(queryWrapper);
        if (user == null || StatusEnum.DISABLED == user.getStatus()) {
            return ResultUtils.result(SystemConstants.ERROR, "用户不存在");
        }
        if (StatusEnum.FREEZE == user.getStatus()) {
            return ResultUtils.result(SystemConstants.ERROR, "用户账号未激活");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (StringUtils.isNotEmpty(user.getPassWord())
                && encoder.matches(userVO.getPassWord(), user.getPassWord())) {
            // 更新用户登录信息
            HttpServletRequest request = RequestHolder.getRequest();
            String ip = IpUtils.getIpAddr(request);
            Map<String, String> userMap = IpUtils.getOsAndBrowserInfo(request);
            user.setBrowser(userMap.get(SqlConstants.BROWSER));
            user.setOs(userMap.get(SqlConstants.OS));
            user.setLastLoginIp(ip);
            user.setLastLoginTime(new Date());
            user.updateById();

            // 获取用户头像
            if (!StringUtils.isEmpty(user.getAvatar())) {
                String avatarResult = pictureFeignClient.getPicture(user.getAvatar(), ",");
                List<String> picList = webUtils.getPicture(avatarResult);
                if (picList != null && picList.size() > 0) {
                    user.setPhotoUrl(webUtils.getPicture(avatarResult).get(0));
                }
            }
            // 生成token
            String token = StringUtils.getUUID();
            // 过滤密码
            user.setPassWord("");
            // 将从数据库查询的数据缓存到redis中
            redisTemplate.opsForValue().set(RedisConstants.USER_TOEKN + RedisConstants.SEGMENTATION + token,
                    JsonUtils.objectToJson(user), userTokenSurvivalTime, TimeUnit.HOURS);

            return ResultUtils.result(SystemConstants.SUCCESS, token);
        } else {
            return ResultUtils.result(SystemConstants.ERROR, "账号或密码错误");
        }
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public String register(@RequestBody UserVO userVO) {
        if (userVO.getUserName().length() < 5 || userVO.getUserName().length() >= 20
                || userVO.getPassWord().length() < 5 || userVO.getPassWord().length() >= 20) {
            return ResultUtils.result(SystemConstants.ERROR, MessageConstants.PARAM_INCORRECT);
        }

        // 根据邮箱和用户名查找是否有相同的用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq(SqlConstants.USER_NAME, userVO.getUserName())
                .or().eq(SqlConstants.EMAIL, userVO.getEmail()));
        queryWrapper.eq(SqlConstants.STATUS, StatusEnum.ENABLE);
        User user = userService.getOne(queryWrapper);
        if (user != null) {
            return ResultUtils.result(SystemConstants.ERROR, "用户已存在");
        }

        HttpServletRequest request = RequestHolder.getRequest();
        String ip = IpUtils.getIpAddr(request);
        Map<String, String> map = IpUtils.getOsAndBrowserInfo(request);
        // 填充用户信息并插入
        user = new User();
        user.setUserName(userVO.getUserName());
        user.setNickName(userVO.getNickName());
        user.setPassWord(new BCryptPasswordEncoder().encode(userVO.getPassWord()));
        user.setEmail(userVO.getEmail());
        // 设置账号来源，蘑菇博客
        user.setSource("MOGU");
        user.setLastLoginIp(ip);
        user.setBrowser(map.get(SqlConstants.BROWSER));
        user.setOs(map.get(SqlConstants.OS));
        user.setStatus(StatusEnum.FREEZE);
        user.insert();

        // 生成随机激活的token
        String token = StringUtils.getUUID();
        // 过滤密码
        user.setPassWord("");
        // 将从数据库查询的数据缓存到redis中，用于用户邮箱激活，1小时后过期
        redisTemplate.opsForValue().set(RedisConstants.ACTIVATE_USER + RedisConstants.SEGMENTATION + token,
                JsonUtils.objectToJson(user), 1, TimeUnit.HOURS);
        // 发送邮件，进行账号激活
        rabbitMqUtils.sendActivateEmail(user, token);
        return ResultUtils.result(SystemConstants.SUCCESS, "注册成功，请登录邮箱进行账号激活");
    }

    @ApiOperation("激活用户账号")
    @GetMapping("/activeUser/{token}")
    public String bindUserEmail(@PathVariable("token") String token) {
        // 从redis中获取用户信息
        String userInfo = redisTemplate.opsForValue()
                .get(RedisConstants.ACTIVATE_USER + RedisConstants.SEGMENTATION + token);
        if (StringUtils.isEmpty(userInfo)) {
            return ResultUtils.result(SystemConstants.ERROR, MessageConstants.INVALID_TOKEN);
        }

        User user = JsonUtils.jsonToPojo(userInfo, User.class);
        if (StatusEnum.FREEZE != user.getStatus()) {
            return ResultUtils.result(SystemConstants.ERROR, "用户账号已经被激活");
        }

        user.setStatus(StatusEnum.ENABLE);
        user.updateById();
        return ResultUtils.result(SystemConstants.SUCCESS, MessageConstants.OPERATION_SUCCESS);
    }

    @ApiOperation("退出登录")
    @PostMapping(value = "/logout")
    public String logout(@ApiParam(name = "token", value = "token令牌")
                         @RequestParam(name = "token", required = false) String token) {
        if (StringUtils.isEmpty(token)) {
            return ResultUtils.result(SystemConstants.ERROR, MessageConstants.OPERATION_FAIL);
        }
        redisTemplate.opsForValue().set(RedisConstants.USER_TOEKN + RedisConstants.SEGMENTATION + token, "");
        return ResultUtils.result(SystemConstants.SUCCESS, "退出成功");
    }
}
