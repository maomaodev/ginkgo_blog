package com.ginkgoblog.picture;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author maomao
 * @date 2021-01-20
 */
@SpringBootApplication
@EnableSwagger2
@MapperScan(basePackages = "com.ginkgoblog.picture.mapper")
@ComponentScan(basePackages = {
        "com.ginkgoblog.picture.config",
        "com.ginkgoblog.picture.service",
        "com.ginkgoblog.picture.controller",
        "com.ginkgoblog.commons.config"
})
public class PictureApplication {
    public static void main(String[] args) {
        SpringApplication.run(PictureApplication.class, args);
    }
}
