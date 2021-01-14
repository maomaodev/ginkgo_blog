package com.ginkgoblog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author maomao
 * @date 2021-01-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_admin")
public class Admin extends SuperEntity {
    /**
     * 管理员名
     */
    private String name;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 性别
     */
    private String gender;

    /**
     * 出生日期
     */
    private Date birthday;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * QQ
     */
    private String qq;

    /**
     * 微信
     */
    private String wechat;

    /**
     * GitHub
     */
    private String github;

    /**
     * Gitee
     */
    private String gitee;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 职业
     */
    private String occupation;

    /**
     * 自我介绍
     */
    private String summary;

    /**
     * 履历
     */
    private String resume;

    /**
     * 角色 id
     */
    private String roleId;

    /**
     * 登录次数
     */
    private Integer loginCount;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 最后登录 IP
     */
    private String lastLoginIp;
}
