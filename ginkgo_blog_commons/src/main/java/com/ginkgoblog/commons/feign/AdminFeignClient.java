package com.ginkgoblog.commons.feign;

/**
 * Admin远程接口
 *
 * @author maomao
 * @date 2021-01-22
 */
public interface AdminFeignClient {
    /**
     * 获取系统配置
     *
     * @return 系统配置
     */
    String getSystemConfig();
}
