package com.ginkgoblog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 异常日志表
 *
 * @author maomao
 * @date 2021-01-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_exception_log")
public class ExceptionLog extends SuperEntity{
    /**
     * 异常对象json格式
     */
    private String json;

    /**
     * 异常信息
     */
    private String message;

    /**
     * 请求ip
     */
    private String ip;

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
}
