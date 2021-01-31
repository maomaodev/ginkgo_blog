package com.ginkgoblog.commons.config.feign;

import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign相关配置
 *
 * @author maomao
 * @date 2021-01-22
 */
@Configuration
public class FeignConfig {
    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("user", "123456");
    }

    /**
     * 创建Feign请求拦截器，在发送请求前设置认证的token
     *
     * @return
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new FeignInterceptor();
    }
}
