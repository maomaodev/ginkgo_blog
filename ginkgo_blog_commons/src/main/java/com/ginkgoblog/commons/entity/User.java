package com.ginkgoblog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户表
 *
 * @author maomao
 * @date 2021-01-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
public class User extends SuperEntity {
    /**
     * 用户名
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
     * 性别，0:女，1:男
     */
    private Integer gender;

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
     * 登录次数
     */
    private Integer loginCount;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 最后登录ip
     */
    private String lastLoginIp;

    /**
     * ip来源
     */
    private String ipSource;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 账号来源
     */
    private String source;

    /**
     * 平台id
     */
    private String platformId;

    /**
     * 是否开启评论，0:否，1:是
     */
    private Boolean isComment;

    /**
     * 是否开启邮件通知，0:否，1:是
     */
    private Boolean isEmailNotify;
}
