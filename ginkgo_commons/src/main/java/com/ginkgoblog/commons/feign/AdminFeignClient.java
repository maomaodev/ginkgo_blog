package com.ginkgoblog.commons.feign;

import com.ginkgoblog.commons.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Admin远程接口
 *
 * @author maomao
 * @date 2021-01-22
 */
@FeignClient(name = "ginkgo-admin", configuration = FeignConfig.class)
public interface AdminFeignClient {
    /**
     * 获取系统配置
     *
     * @return 系统配置
     */
    @GetMapping("/systemConfig/getSystemConfig")
    String getSystemConfig();
}
