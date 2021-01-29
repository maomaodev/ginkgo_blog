package com.ginkgoblog.commons.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ginkgoblog.base.vo.SuperVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 网站配置表 表现层对象
 *
 * @author maomao
 * @date 2021-01-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WebConfigVO extends SuperVO {
    /**
     * 网站Logo
     */
    private String logo;

    /**
     * 网站名称
     */
    private String name;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String summary;

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 作者
     */
    private String author;

    /**
     * 备案号
     */
    private String recordNum;

    /**
     * 支付宝收款码FileId
     */
    private String aliPay;

    /**
     * 微信收款码FileId
     */
    private String weixinPay;

    /**
     * 是否开启评论(0:否， 1:是)
     */
    private String startComment;

    /**
     * github地址
     */
    private String github;

    /**
     * gitee地址
     */
    private String gitee;

    /**
     * QQ号
     */
    private String qqNumber;

    /**
     * QQ群
     */
    private String qqGroup;

    /**
     * 微信号
     */
    private String weChat;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 显示的列表（用于控制邮箱、QQ、QQ群、Github、Gitee、微信是否显示在前端）
     */
    private String showList;

    /**
     * 登录方式列表（用于控制前端登录方式，如账号密码,码云,Github,QQ,微信）
     */
    private String loginTypeList;


    // 以下字段不存入数据库，封装为了方便使用

    /**
     * 标题图
     */
    @TableField(exist = false)
    private List<String> photoList;

    /**
     * 支付宝付款码
     */
    @TableField(exist = false)
    private String aliPayPhoto;

    /**
     * 微信付款码
     */
    @TableField(exist = false)
    private String weixinPayPhoto;
}
