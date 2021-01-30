package com.ginkgoblog.sms;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author maomao
 * @date 2021-01-26
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableRabbit
@EnableEurekaClient
@EnableFeignClients("com.ginkgoblog.sms.feign")
public class SmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmsApplication.class, args);
    }
}
