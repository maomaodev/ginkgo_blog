package com.ginkgoblog.commons.vo;

import com.ginkgoblog.base.vo.SuperVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统日志表 表现层对象
 *
 * @author maomao
 * @date 2021-01-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysLogVO extends SuperVO {
    /**
     * 操作用户名
     */
    private String userName;

    /**
     * 操作人uid
     */
    private String adminUid;

    /**
     * 请求IP
     */
    private String ip;

    /**
     * ip来源
     */
    private String ipSource;

    /**
     * 请求地址
     */
    private String url;

    /**
     * 请求方式 GET POST
     */
    private String type;

    /**
     * 请求类路径
     */
    private String classPath;

    /**
     * 方法名
     */
    private String method;

    /**
     * 参数
     */
    private String params;

    /**
     * 描述
     */
    private String operation;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 方法请求花费的时间，单位毫秒
     */
    private Long spendTime;
}
