package com.ginkgoblog.utils;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回统一接口
 *
 * @author maomao
 * @date 2021-01-20
 */
public class ResultUtils {
    /**
     * 统一返回的方法
     *
     * @param code success、error
     * @param data 返回的具体数据
     * @return json格式化的字符串
     */
    public static String result(Object code, Object data) {
        Map<Object, Object> map = new HashMap<>(2);
        map.put("code", code);
        map.put("data", data);
        return JSON.toJSONString(map);
    }
}
