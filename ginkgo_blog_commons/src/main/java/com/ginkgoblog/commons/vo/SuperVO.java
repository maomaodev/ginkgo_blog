package com.ginkgoblog.commons.vo;

import lombok.Data;

/**
 * 表现层对象父类
 *
 * @author maomao
 * @date 2021-01-18
 */
@Data
public class SuperVO {
    /**
     * 唯一id
     */
    private String id;

    /**
     * 是否删除，0:删除，1:删除
     */
    private Boolean isDelete;
}
