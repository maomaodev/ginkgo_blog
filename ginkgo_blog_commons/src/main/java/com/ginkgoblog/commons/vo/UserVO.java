package com.ginkgoblog.commons.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author maomao
 * @date 2021-01-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserVO extends SuperVO {
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
