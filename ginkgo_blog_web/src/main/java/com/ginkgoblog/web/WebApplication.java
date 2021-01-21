package com.ginkgoblog.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author maomao
 * @date 2021-01-11
 */
@SpringBootApplication
@EnableSwagger2
@MapperScan(basePackages = "com.ginkgoblog.commons.mapper")
@ComponentScan(basePackages = {
        "com.ginkgoblog.web.config",
        "com.ginkgoblog.web.controller",
        "com.ginkgoblog.commons.config",
        "com.ginkgoblog.commons.service"
})
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
