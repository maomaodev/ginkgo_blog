package com.ginkgoblog.commons.feign;

import com.ginkgoblog.commons.config.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Web远程接口
 *
 * @author maomao
 * @date 2021-01-22
 */
@FeignClient(name = "ginkgo-web", configuration = FeignConfig.class)
public interface WebFeignClient {

    @GetMapping("/oauth/getSystemConfig")
    String getSystemConfig(@RequestParam("token") String token);

}
