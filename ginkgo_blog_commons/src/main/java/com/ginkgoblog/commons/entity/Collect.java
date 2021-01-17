package com.ginkgoblog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收藏表
 *
 * @author maomao
 * @date 2021-01-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_collect")
public class Collect extends SuperEntity {
    /**
     * 用户id
     */
    private String userId;

    /**
     * 博客id
     */
    private String blogId;
}
