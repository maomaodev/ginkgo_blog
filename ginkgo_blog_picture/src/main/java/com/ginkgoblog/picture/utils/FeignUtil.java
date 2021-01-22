package com.ginkgoblog.picture.utils;

import com.alibaba.fastjson.JSON;
import com.ginkgoblog.base.constants.RedisConstants;
import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.base.constants.SystemConstants;
import com.ginkgoblog.commons.feign.AdminFeignClient;
import com.ginkgoblog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Feign操作工具类
 *
 * @author maomao
 * @date 2021-01-22
 */
@Component
public class FeignUtil {

    @Autowired
    AdminFeignClient adminFeignClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 通过Token获取七牛云配置
     *
     * @param token
     * @return
     */
    public Map<String, String> getQiNiuConfig(String token) {
        // 从Redis中获取的SystemConf或者通过feign获取的
        Map<String, String> resultMap = new HashMap<>();

        // 从Redis中获取内容
        String jsonResult = stringRedisTemplate.opsForValue().get(
                RedisConstants.SYSTEM_CONFIG + RedisConstants.SEGMENTATION + token);
        // 判断Redis中是否有数据
        if (StringUtils.isNotEmpty(jsonResult)) {
            resultMap = (Map<String, String>) JSON.parse(jsonResult);
            // resultMap = (Map<String, String>) JsonUtils.jsonToMap(jsonResult, String.class);
        } else {
            // 进行七牛云校验
            String resultStr = adminFeignClient.getSystemConfig();
            Map<String, Object> resultTempMap = (Map<String, Object>) JSON.parse(resultStr);

            if (resultTempMap.get(SystemConstants.CODE) != null
                    && SystemConstants.SUCCESS.equals(resultTempMap.get(SystemConstants.CODE).toString())) {
                // 从返回的数据data中取出七牛云配置
                resultMap = (Map<String, String>) resultTempMap.get(SystemConstants.DATA);
                // 将token存储到redis中，设置30分钟后过期
                stringRedisTemplate.opsForValue().set(RedisConstants.SYSTEM_CONFIG
                        + RedisConstants.SEGMENTATION + token, JSON.toJSONString(resultMap), 30, TimeUnit.MINUTES);
            }
        }

        return resultMap;
    }


}
