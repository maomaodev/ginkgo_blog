package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.commons.entity.User;
import com.ginkgoblog.commons.mapper.UserMapper;
import com.ginkgoblog.commons.service.UserService;
import com.ginkgoblog.commons.vo.UserVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author maomao
 * @date 2021-01-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public User getUserBySourceAndId(String source, String id) {
        return null;
    }

    @Override
    public List<User> getUserListByIds(List<String> ids) {
        return this.listByIds(ids);
    }

    @Override
    public Integer getUserCount(Boolean isDelete) {
        return null;
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
    public String register(UserVO userVO) {
        return null;
    }

    @Override
    public String login(UserVO userVO) {
        return null;
    }

    @Override
    public String active(UserVO userVO) {
        return null;
    }

    @Override
    public String editUser(UserVO userVO) {
        return null;
    }

    @Override
    public String resetUserPassword(UserVO userVO) {
        return null;
    }

    @Override
    public String deleteUser(UserVO userVO) {
        return null;
    }
}
