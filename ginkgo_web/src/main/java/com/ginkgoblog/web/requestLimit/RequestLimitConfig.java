package com.ginkgoblog.web.requestLimit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author maomao
 * @date 2021-01-31
 */
@ConfigurationProperties(prefix = "request-limit")
@Component
@Data
public class RequestLimitConfig {

    /**
     * 允许访问的数量
     */
    public int amount;
    /**
     * 时间段
     */
    public long time;
}
