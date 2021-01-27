package com.ginkgoblog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ginkgoblog.base.entity.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 友情链接表
 *
 * @author maomao
 * @date 2021-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_link")
public class Link extends SuperEntity {
    /**
     * 友链标题
     */
    private String title;

    /**
     * 友链介绍
     */
    private String summary;

    /**
     * 友链地址
     */
    private String url;

    /**
     * 友链状态： 0 申请中， 1：上线  2: 已下架
     */
    private Integer linkStatus;

    /**
     * 点击数
     */
    private Integer clickCount;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 管理员UID
     */
    private String adminUid;

    /**
     * 申请用户Uid
     */
    private String userUid;
}
