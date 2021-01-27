package com.ginkgoblog.base.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表现层对象父类
 *
 * @author maomao
 * @date 2021-01-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SuperVO extends PageInfo {
    /**
     * 唯一uid
     */
    private String uid;

    /**
     * 状态（0：失效、1：生效）
     */
    private Integer status;
}
