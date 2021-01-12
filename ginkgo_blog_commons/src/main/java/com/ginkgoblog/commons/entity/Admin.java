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
    private String username;

    private String password;

    private String nickname;

    private String gender;

    private Date birthday;

    private String avatar;

    private String phoneNumber;

    private String qq;

    private String wechat;

    private String github;

    private String gitee;

    private String email;

    private String occupation;

    private String summary;

    private String resume;

    private String roleId;

    private Integer loginCount;

    private Date lastLoginTime;

    private String lastLoginIp;
}
