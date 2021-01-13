package com.ginkgoblog.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author maomao
 * @date 2021-01-13
 */
@SpringBootApplication
@EnableSwagger2
@MapperScan(basePackages = "com.ginkgoblog.commons.mapper")
@ComponentScan(basePackages = {
        "com.ginkgoblog.admin.config",
        "com.ginkgoblog.admin.controller",
        "com.ginkgoblog.commons.service"
})
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
