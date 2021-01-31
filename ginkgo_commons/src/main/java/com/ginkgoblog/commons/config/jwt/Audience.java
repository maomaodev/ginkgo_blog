package com.ginkgoblog.commons.config.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT相关配置
 *
 * @author maomao
 * @date 2021-01-31
 */
@ConfigurationProperties(prefix = "audience")
@Component
@Data
public class Audience {
    private String clientId;
    private String base64Secret;
    private String name;
    private int expiresSecond;
}
