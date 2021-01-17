package com.ginkgoblog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色表
 *
 * @author maomao
 * @date 2021-01-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_role")
public class Role extends SuperEntity {
    /**
     * 角色名
     */
    private String name;

    /**
     * 角色介绍
     */
    private String summary;

    /**
     * 角色管辖的菜单id
     */
    private String categoryMenuIds;

}
