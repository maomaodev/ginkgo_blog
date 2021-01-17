package com.ginkgoblog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单表
 *
 * @author maomao
 * @date 2021-01-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_category_menu")
public class CategoryMenu extends SuperEntity {
    /**
     * 菜单名
     */
    private String name;

    /**
     * 菜单级别
     */
    private Integer level;

    /**
     * 菜单简介
     */
    private String summary;

    /**
     * 菜单父id
     */
    private String parentId;

    /**
     * url地址
     */
    private String url;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序字段，越大越靠
     */
    private Integer order;

    /**
     * 是否显示，0:否，1:是
     */
    private Boolean isShow;
}
