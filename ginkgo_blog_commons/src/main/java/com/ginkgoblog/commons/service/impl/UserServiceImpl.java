package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.base.constants.MessageConstants;
import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.base.enums.StatusEnum;
import com.ginkgoblog.commons.entity.User;
import com.ginkgoblog.commons.mapper.UserMapper;
import com.ginkgoblog.commons.service.UserService;
import com.ginkgoblog.commons.vo.UserVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        return null;
    }

    @Override
    public IPage<User> getPageList(UserVO userVO) {
        return null;
    }

    @Override
    public String registerUser(UserVO userVO) {
        return null;
    }

    @Override
    public String loginUser(UserVO userVO) {
        return null;
    }

    @Override
    public String activeUser(UserVO userVO) {
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
        return MessageConstants.UPDATE_SUCCESS;
    }

    @Override
    public String resetUserPassword(UserVO userVO) {
        User user = this.getById(userVO.getUid());
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassWord(encoder.encode(defaultPassword));
        this.updateById(user);
        return MessageConstants.OPERATION_SUCCESS;
    }

    @Override
    public String deleteUser(UserVO userVO) {
        User user = this.getById(userVO.getUid());
        user.setStatus(StatusEnum.DISABLED);
        this.updateById(user);
        return MessageConstants.DELETE_SUCCESS;
    }
}
