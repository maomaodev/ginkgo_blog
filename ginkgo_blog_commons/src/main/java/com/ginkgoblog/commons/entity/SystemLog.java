package com.ginkgoblog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统日志表
 *
 * @author maomao
 * @date 2021-01-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_system_log")
public class SystemLog extends SuperEntity {
    /**
     * 用户名
     */
    private String username;

    /**
     * 请求ip
     */
    private String ip;

    /**
     * 请求url
     */
    private String url;

    /**
     * 请求方式
     */
    private String type;

    /**
     * 请求类路径
     */
    private String path;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 请求描述
     */
    private String description;

    /**
     * ip来源
     */
    private String ipSource;

    /**
     * 请求花费的时间
     */
    private Integer duration;
}
