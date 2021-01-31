package com.ginkgoblog.picture;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author maomao
 * @date 2021-01-20
 */
@SpringBootApplication
@EnableSwagger2
@EnableEurekaClient
@EnableFeignClients("com.ginkgoblog.commons.feign")
@MapperScan(basePackages = "com.ginkgoblog.picture.mapper")
@ComponentScan(basePackages = {
        "com.ginkgoblog.picture.config",
        "com.ginkgoblog.picture.service",
        "com.ginkgoblog.picture.controller",
        "com.ginkgoblog.picture.utils",
        "com.ginkgoblog.commons.config.feign",
        "com.ginkgoblog.commons.config.redis",
        "com.ginkgoblog.utils"
})
public class PictureApplication {
    public static void main(String[] args) {
        SpringApplication.run(PictureApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                //配置允许跨域访问的路径
                registry.addMapping("/**/**")
                        .allowedOrigins("*")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .exposedHeaders("")
                        .maxAge(3600);
            }
        };
    }
}
