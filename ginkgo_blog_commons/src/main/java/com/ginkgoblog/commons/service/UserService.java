package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.User;
import com.ginkgoblog.commons.vo.UserVO;

import java.util.List;

/**
 * 用户表 Service 层
 *
 * @author maomao
 * @date 2021-01-17
 */
public interface UserService extends IService<User> {
    /**
     * 通过用户来源和id获取用户
     *
     * @param source 用户来源
     * @param id     用户id
     * @return 用户
     */
    User getUserBySourceAndId(String source, String id);

    /**
     * 根据id列表获取用户列表
     *
     * @param ids id列表
     * @return 用户列表
     */
    List<User> getUserListByIds(List<String> ids);

    /**
     * 获取用户数
     *
     * @param isDelete 是否删除
     * @return 用户数
     */
    Integer getUserCount(Boolean isDelete);

    /**
     * 设置请求相关，如浏览器，IP，IP来源等
     *
     * @param user 用户信息
     * @return 用户
     */
    User setRequestInfo(User user);

    /**
     * 获取用户分页列表
     *
     * @param userVO 用户表现层对象
     * @return 用户分页列表
     */
    IPage<User> getPageList(UserVO userVO);

    /**
     * 用户注册
     *
     * @param userVO 用户表现层对象
     * @return 处理信息
     */
    String register(UserVO userVO);

    /**
     * 用户登录
     *
     * @param userVO 用户表现层对象
     * @return 处理信息
     */
    String login(UserVO userVO);

    /**
     * 用户激活
     *
     * @param userVO 用户表现层对象
     * @return 处理信息
     */
    String active(UserVO userVO);

    /**
     * 编辑用户
     *
     * @param userVO 用户表现层对象
     * @return 处理信息
     */
    String editUser(UserVO userVO);

    /**
     * 重置用户密码
     *
     * @param userVO 用户表现层对象
     * @return 处理信息
     */
    String resetUserPassword(UserVO userVO);

    /**
     * 删除用户
     *
     * @param userVO 用户表现层对象
     * @return 处理信息
     */
    String deleteUser(UserVO userVO);
}
