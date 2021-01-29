package com.ginkgoblog.commons.vo;

import com.ginkgoblog.base.vo.SuperVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统配置表 表现层对象
 *
 * @author maomao
 * @date 2021-01-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysConfigVO extends SuperVO {
    /**
     * 七牛云公钥
     */
    private String qiNiuAccessKey;

    /**
     * 七牛云私钥
     */
    private String qiNiuSecretKey;

    /**
     * 七牛云上传空间
     */
    private String qiNiuBucket;

    /**
     * 七牛云存储区域 华东（z0），华北(z1)，华南(z2)，北美(na0)，东南亚(as0)
     */
    private String qiNiuArea;

    /**
     * 图片是否上传七牛云 (0:否， 1：是)
     */
    private String uploadQiNiu;

    /**
     * 图片是否上传本地存储 (0:否， 1：是)
     */
    private String uploadLocal;

    /**
     * 图片显示优先级（ 1 展示 七牛云,  0 本地）
     */
    private String picturePriority;

    /**
     * 本地存储图片服务器，域名前缀：   http://localhost:8600
     */
    private String localPictureBaseUrl;

    /**
     * 七牛云存储图片服务器，域名前缀： http://images.moguit.cn
     */
    private String qiNiuPictureBaseUrl;

    /**
     * 邮箱账号
     */
    private String email;

    /**
     * 邮箱发件人用户名
     */
    private String emailUserName;

    /**
     * 邮箱密码
     */
    private String emailPassword;

    /**
     * SMTP地址
     */
    private String smtpAddress;

    /**
     * SMTP端口
     */
    private String smtpPort;

    /**
     * 是否开启邮件通知(0:否， 1:是)
     * 当有新的反馈，友链申请时进行通知，首先需要在系统管理处设置接收通知的邮箱
     */
    private String startEmailNotification;
}
