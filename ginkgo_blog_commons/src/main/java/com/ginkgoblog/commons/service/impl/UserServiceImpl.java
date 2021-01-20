package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.base.constants.MessageConstants;
import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.base.constants.SystemConstants;
import com.ginkgoblog.base.enums.StatusEnum;
import com.ginkgoblog.base.holder.RequestHolder;
import com.ginkgoblog.commons.entity.User;
import com.ginkgoblog.commons.mapper.UserMapper;
import com.ginkgoblog.commons.service.UserService;
import com.ginkgoblog.commons.vo.UserVO;
import com.ginkgoblog.utils.IpUtils;
import com.ginkgoblog.utils.ResultUtils;
import com.ginkgoblog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Value("${DEFAULT_PASSWORD}")
    private String defaultPassword;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public User getUserBySourceAndUuid(String source, String uuid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SqlConstants.UUID, uuid).eq(SqlConstants.SOURCE, source);
        return this.getOne(queryWrapper);
    }

    @Override
    public Integer getUserCount(int status) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SqlConstants.STATUS, status);
        return this.count(queryWrapper);
    }

    @Override
    public User setRequestInfo(User user) {
        HttpServletRequest request = RequestHolder.getRequest();
        Map<String, String> map = IpUtils.getOsAndBrowserInfo(request);
        String os = map.get("OS");
        String browser = map.get("BROWSER");
        String ip = IpUtils.getIpAddr(request);

        // 设置IP、操作系统、浏览器、最后登录时间
        user.setLastLoginIp(ip);
        user.setOs(os);
        user.setBrowser(browser);
        user.setLastLoginTime(new Date());

        // 从Redis中获取IP来源
        String str = stringRedisTemplate.opsForValue().get("IP_SOURCE:" + ip);
        if (StringUtils.isEmpty(str)) {
            String addresses = IpUtils.getAddresses("ip=" + ip, "utf-8");
            if (StringUtils.isNotEmpty(addresses)) {
                user.setIpSource(addresses);
                stringRedisTemplate.opsForValue().set("IP_SOURCE:" + ip, addresses, 24, TimeUnit.HOURS);
            }
        } else {
            user.setIpSource(str);
        }

        return user;
    }

    @Override
    public IPage<User> getPageList(UserVO userVO) {



        return null;
    }

    @Override
    public String editUser(UserVO userVO) {
        User user = this.getById(userVO.getUid());
        user.setEmail(userVO.getEmail());
        user.setStartEmailNotification(userVO.getStartEmailNotification());
        user.setOccupation(userVO.getOccupation());
        user.setGender(userVO.getGender());
        user.setQqNumber(userVO.getQqNumber());
        user.setSummary(userVO.getSummary());
        user.setBirthday(userVO.getBirthday());
        user.setAvatar(userVO.getAvatar());
        user.setNickName(userVO.getNickName());
        user.setUserTag(userVO.getUserTag());
        user.setCommentStatus(userVO.getCommentStatus());
        this.updateById(user);
        return ResultUtils.result(SystemConstants.SUCCESS,MessageConstants.UPDATE_SUCCESS);
    }

    @Override
    public String resetUserPassword(UserVO userVO) {
        User user = this.getById(userVO.getUid());
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassWord(encoder.encode(defaultPassword));
        this.updateById(user);
        return ResultUtils.result(SystemConstants.SUCCESS, MessageConstants.OPERATION_SUCCESS);
    }

    @Override
    public String deleteUser(UserVO userVO) {
        User user = this.getById(userVO.getUid());
        user.setStatus(StatusEnum.DISABLED);
        this.updateById(user);
        return ResultUtils.result(SystemConstants.SUCCESS, MessageConstants.DELETE_SUCCESS);
    }
}
