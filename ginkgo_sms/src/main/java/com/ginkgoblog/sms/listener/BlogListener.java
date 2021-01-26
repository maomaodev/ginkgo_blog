package com.ginkgoblog.sms.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 博客消费监听器(用于更新Redis和索引)
 *
 * @author maomao
 * @date 2021-01-26
 */
@Slf4j
@Component
public class BlogListener {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


}
